#!/usr/bin/env python3
"""Structural-invariant audit for Enigma mappings.

Checks the two invariants that rename-only deobfuscation must preserve
(the remapped artifact must be "the same program, only renamed"):

  members  (invariant 1)  An ancestor class member must not be mapped to a
          final name+descriptor that a subclass also declares. Field/method
          refs resolve from the written owner class up the superclass chain
          (JVMS 5.4.3.2), so after remap such refs silently rebind to the
          subclass member. The audit also simulates every Fieldref/Methodref
          resolution before vs. after remap (superclass chain, then
          interfaces) and reports references whose binding changes.

  nested   (invariant 2)  When a class is renamed/moved, its `$`-nested
          classes must be mapped into the outer class's target package
          (`Outer$suffix` or `Outer_suffix`). A stranded nested class stays
          in the old package; Java <=8 nested classes are package-private,
          so cross-package access throws IllegalAccessError.

Pure read-only audit: parses the jar's class files (constant pools, field /
method tables) and the mapping files, prints a report, optionally writes
JSON. Exit status: 0 = clean, 1 = behavior-changing violations found.

Usage:
  python enigma_violation_audit.py --jar game.jar --mappings mappings/
  python enigma_violation_audit.py --jar game.jar --mappings mappings.map \\
      --invariant members --output report.json
  python enigma_violation_audit.py --jar game.jar --mappings mappings/ \\
      --focus-class com/example/GameObject --focus-names x,y,height --focus-desc F

Mapping input: either a directory of per-class .mapping files (Enigma
directory format, nested classes as relative `CLASS <suffix>` entries) or a
single Enigma v1 text mapping file. Descriptors may use either `.` or `/`
class-name separators; both are normalised.
"""
import argparse
import json
import os
import re
import struct
import sys
from collections import defaultdict, deque
import zipfile

ACC_STATIC, ACC_PRIVATE, ACC_INTERFACE = 0x0008, 0x0002, 0x0200
INIT = ("<init>", "<clinit>")
DESC_RE = re.compile(r"L([^;]+);")


def sl(s):
    return s.replace(".", "/")


def slashes_desc(d):
    return DESC_RE.sub(lambda m: "L" + m.group(1).replace(".", "/") + ";", d)


def package_of(p):
    return p.rsplit("/", 1)[0] if "/" in p else ""


def simple_of(p):
    return p.rsplit("/", 1)[-1] if "/" in p else p


# ---------------------------------------------------------------- jar parsing
def parse_class(data):
    """Return (name, acc, superName, interfaces, fields, methods, refs)."""
    if data[:4] != b"\xca\xfe\xba\xbe":
        raise ValueError("not a classfile")
    (cpn,) = struct.unpack_from(">H", data, 8)
    cp = [None] * cpn
    i, idx = 10, 1
    while idx < cpn:
        tag = data[i]; i += 1
        if tag == 1:
            (ln,) = struct.unpack_from(">H", data, i); i += 2
            cp[idx] = (1, data[i:i + ln].decode("utf-8", "replace")); i += ln
        elif tag in (3, 4):
            i += 4; cp[idx] = (tag,)
        elif tag in (5, 6):
            i += 8; cp[idx] = (tag,); idx += 1
        elif tag in (7, 8, 16, 19, 20):
            (j,) = struct.unpack_from(">H", data, i); i += 2
            cp[idx] = (tag, j)
        elif tag in (9, 10, 11, 12, 17, 18):
            a, b = struct.unpack_from(">HH", data, i); i += 4
            cp[idx] = (tag, a, b)
        elif tag == 15:
            cp[idx] = (15, data[i]); i += 3
        else:
            raise ValueError("bad cp tag %d at %d" % (tag, i))
        idx += 1

    def U(j):
        return cp[j][1]

    def C(j):
        e = cp[j]
        return U(e[1]) if e and e[0] == 7 else None

    acc, this, sup = struct.unpack_from(">HHH", data, i); i += 6
    name = C(this)
    supn = C(sup) if sup else None
    (nic,) = struct.unpack_from(">H", data, i); i += 2
    itfs = []
    for _ in range(nic):
        (j,) = struct.unpack_from(">H", data, i); i += 2
        itfs.append(C(j))

    def mems(i):
        (cnt,) = struct.unpack_from(">H", data, i); i += 2
        out = []
        for _ in range(cnt):
            a, n, d = struct.unpack_from(">HHH", data, i); i += 6
            (na,) = struct.unpack_from(">H", data, i); i += 2
            for _ in range(na):
                (ln,) = struct.unpack_from(">I", data, i + 2); i += 6 + ln
            out.append((U(n), U(d), a))
        return out, i

    fields, i = mems(i)
    methods, i = mems(i)
    refs = []
    for e in cp[1:]:
        if e and e[0] in (9, 10, 11):  # Fieldref/Methodref/InterfaceMethodref
            owner = U(cp[e[1]][1])
            if owner.startswith("["):
                continue
            nte = cp[e[2]]
            refs.append((owner, U(nte[1]), U(nte[2]),
                         "FIELD" if e[0] == 9 else "METHOD"))
    return name, acc, supn, itfs, fields, methods, refs


def load_jar(path):
    classes, refcount, errors = {}, defaultdict(int), []
    with zipfile.ZipFile(path) as zf:
        for zi in zf.infolist():
            if not zi.filename.endswith(".class"):
                continue
            try:
                (name, acc, sup, itfs, fields, methods, refs) = \
                    parse_class(zf.read(zi.filename))
            except Exception as ex:
                errors.append((zi.filename, str(ex)))
                continue
            classes[name] = {"acc": acc, "super": sup,
                             "interfaces": [x for x in itfs if x],
                             "fields": fields, "methods": methods}
            for (o, n, d, k) in refs:
                refcount[(k, o, n, d)] += 1
    return classes, refcount, errors


# ----------------------------------------------------------- mappings parsing
def parse_mappings(path):
    """Parse a mapping directory (recursive *.mapping) or a single file.

    Returns (class_map, member_map, file_count).
    class_map:  obf class (slash form) -> deobf class; nested relative
                entries are resolved to absolute names.
    member_map: (obfClass, "FIELD"|"METHOD", obfName, desc|None) -> deobfName
    """
    class_map, member_map = {}, {}
    files = 0

    def handle_lines(lines):
        nonlocal files
        files += 1
        stack = []  # [(indent, obfClass)]
        for raw in lines:
            if not raw.strip() or raw.strip().startswith("#"):
                continue
            indent = len(raw) - len(raw.lstrip())
            parts = raw.split()
            t = parts[0]
            while stack and stack[-1][0] >= indent:
                stack.pop()
            cur = stack[-1][1] if stack else None
            if t == "CLASS":
                a = parts[1]
                b = parts[2] if len(parts) > 2 else a
                if "/" in a or cur is None:
                    obf, deb = sl(a), sl(b)
                else:  # nested entry, relative to enclosing class
                    obf = cur + "$" + a.lstrip("$")
                    deb = class_map.get(cur, cur) + "$" + b.lstrip("$")
                class_map[obf] = deb
                stack.append((indent, obf))
            elif t in ("FIELD", "METHOD") and cur is not None and len(parts) >= 3:
                obf, deobf = parts[1], parts[2]
                desc = None
                if len(parts) > 3:
                    cand = parts[3]
                    if cand.startswith("(") or re.match(r"^[\[\(LBCDFIJSZV]", cand):
                        desc = slashes_desc(cand)
                member_map[(cur, t, obf, desc)] = deobf

    if os.path.isdir(path):
        for root, _dirs, files_list in os.walk(path):
            for fn in sorted(files_list):
                if not fn.endswith(".mapping"):
                    continue
                with open(os.path.join(root, fn), encoding="utf-8",
                          errors="replace") as fh:
                    handle_lines(fh)
    else:
        with open(path, encoding="utf-8", errors="replace") as fh:
            handle_lines(fh)
    return class_map, member_map, files


# ------------------------------------------------------------------- checks
def run(args):
    classes, refcount, parse_errors = load_jar(args.jar)
    class_map, member_map, map_files = parse_mappings(args.mappings)
    deobf_to_obf = {}
    for k, v in class_map.items():
        deobf_to_obf.setdefault(v, k)

    _nd = {}

    def norm_desc(d):
        """Mapping descriptors may use deobf class names; map back to obf."""
        r = _nd.get(d)
        if r is not None:
            return r

        def rep(m):
            n2 = deobf_to_obf.get(m.group(1))
            return "L" + (n2 if n2 is not None else m.group(1)) + ";"
        r = DESC_RE.sub(rep, d)
        _nd[d] = r
        return r

    def mem_final(c, kind, n, d):
        f = member_map.get((c, kind, n, d))
        if f is None and d is not None:
            f = member_map.get((c, kind, n, norm_desc(d)))
        if f is None:
            f = member_map.get((c, kind, n, None))
        return f if f is not None else n

    def cdsp(c):
        return class_map.get(c, c)

    # per-class indexes: obf declarations + remapped view
    decl_set, fin_map, self_collisions = {}, {}, []
    for c, cd in classes.items():
        ds, fm = {}, {}
        for kind, arr in (("FIELD", cd["fields"]), ("METHOD", cd["methods"])):
            ds[kind] = {(n, d) for (n, d, _a) in arr}
            fmk = {}
            for (n, d, _a) in arr:
                if n in INIT:
                    continue
                fn = mem_final(c, kind, n, d)
                key = (fn, d)
                if key in fmk:
                    if fmk[key] != n:
                        self_collisions.append({
                            "class": c, "deobf": cdsp(c), "kind": kind,
                            "obfA": fmk[key], "obfB": n, "final": fn,
                            "desc": d})
                else:
                    fmk[key] = n
            fm[kind] = fmk
        decl_set[c] = ds
        fin_map[c] = fm

    children_ext = defaultdict(list)
    gen_up = {}
    for c, cd in classes.items():
        s = cd["super"]
        if s in classes:
            children_ext[s].append(c)
        ps = ([s] if s in classes else []) + \
             [x for x in cd["interfaces"] if x in classes]
        gen_up[c] = ps

    ext_chain = {}
    for c in classes:
        lst, k = [], c
        while k in classes:
            lst.append(k)
            k = classes[k]["super"]
        ext_chain[c] = lst

    _stc = {}

    def ext_subtree(c):
        r = _stc.get(c)
        if r is not None:
            return r
        seen = {c}
        q = deque([c])
        while q:
            k = q.popleft()
            for ch in children_ext.get(k, ()):
                if ch not in seen:
                    seen.add(ch)
                    q.append(ch)
        _stc[c] = seen
        return seen

    def resolve(owner, kind, n, d, table):
        """First declaring class walking the superclass chain, then interfaces."""
        chain = ext_chain.get(owner)
        if chain is None:
            return None
        for k in chain:
            t = table.get(k, {}).get(kind)
            if t is not None and (n, d) in t:
                return k
        seen = set(chain)
        q = deque()
        for k in chain:
            for itf in classes[k]["interfaces"]:
                if itf in classes and itf not in seen:
                    seen.add(itf)
                    q.append(itf)
        while q:
            k = q.popleft()
            t = table.get(k, {}).get(kind)
            if t is not None and (n, d) in t:
                return k
            for itf in classes[k]["interfaces"]:
                if itf in classes and itf not in seen:
                    seen.add(itf)
                    q.append(itf)
        return None

    report = {
        "jar": os.path.abspath(args.jar),
        "mappings": os.path.abspath(args.mappings),
        "jar_classes": len(classes),
        "parse_errors": parse_errors,
        "mapping_files": map_files,
    }
    exit_bad = False

    # ---------------- invariant 1: member shadowing ----------------
    violations, events, unmatched = [], {}, []
    if args.invariant in ("members", "all"):
        groups = defaultdict(dict)  # (kind, final, desc) -> {cls: (obf, acc)}
        for c, cd in classes.items():
            for kind, arr in (("FIELD", cd["fields"]), ("METHOD", cd["methods"])):
                for (n, d, a) in arr:
                    if n in INIT:
                        continue
                    fn = mem_final(c, kind, n, d)
                    g = groups[(kind, fn, d)]
                    if c not in g:  # self-collisions recorded separately
                        g[c] = (n, a)

        for (kind, fn, d), g in groups.items():
            if len(g) < 2:
                continue
            for c, (n, a) in list(g.items()):
                seen = {c}
                q = deque([c])
                while q:
                    k = q.popleft()
                    for p in gen_up.get(k, ()):
                        if p in seen:
                            continue
                        seen.add(p)
                        q.append(p)
                        if p in g:
                            pn, pa = g[p]
                            if pn == n:            # same obf name = one
                                continue           # override family
                            if pa & ACC_PRIVATE:   # never inherited
                                continue
                            violations.append({
                                "P": p, "Pdeobf": cdsp(p),
                                "Piface": bool(classes[p]["acc"] & ACC_INTERFACE),
                                "m": pn, "m_acc": pa, "m_mapped": fn != pn,
                                "S": c, "Sdeobf": cdsp(c),
                                "s": n, "s_acc": a, "s_mapped": fn != n,
                                "kind": kind, "fn": fn, "desc": d})

        # impact: simulate every cp ref's resolution before vs. after remap
        events = {}
        for (kind, o, n, d), cnt in refcount.items():
            if o not in classes:
                continue
            D1 = resolve(o, kind, n, d, decl_set)
            if D1 is None:
                continue
            n2 = mem_final(D1, kind, n, d)
            D2 = resolve(o, kind, n2, d, fin_map)
            if D2 is None or D1 == D2:
                continue
            d2n = fin_map.get(D2, {}).get(kind, {}).get((n2, d), n2)
            ev = events.setdefault((kind, D1, n, d, D2, d2n), [0, []])
            ev[0] += cnt
            if o not in ev[1] and len(ev[1]) < 6:
                ev[1].append(o)

        refs_by_nd = defaultdict(list)
        for (kind, o, n, d), cnt in refcount.items():
            if o in classes:
                refs_by_nd[(kind, n, d)].append((o, cnt))

        for v in violations:
            key = (v["kind"], v["P"], v["m"], v["desc"], v["S"], v["s"])
            ev = events.get(key)
            v["impacted"] = ev[0] if ev else 0
            v["impacted_owners"] = ev[1] if ev else []
            tot = 0
            for (o, cnt) in refs_by_nd.get((v["kind"], v["m"], v["desc"]), ()):
                if o in ext_subtree(v["S"]):
                    tot += cnt
            v["spec_refs"] = tot

        vkeys = {(v["kind"], v["P"], v["m"], v["desc"], v["S"], v["s"])
                 for v in violations}
        unmatched = [{"kind": k[0], "D1": cdsp(k[1]), "m1": k[2],
                      "desc": k[3], "D2": cdsp(k[4]), "m2": k[5],
                      "cnt": e[0], "owners": e[1]}
                     for k, e in events.items() if k not in vkeys]

        must_fix = sorted([v for v in violations if v["impacted"] > 0],
                          key=lambda v: -v["impacted"])
        spec_only = [v for v in violations
                     if v["impacted"] == 0 and v["spec_refs"] > 0]
        zero_refs = [v for v in violations
                     if v["impacted"] == 0 and v["spec_refs"] == 0]

        def vline(v):
            return ("%s %s.%s -> %s %s | S=%s s_obf=%s (%s) | spec_refs=%d "
                    "impacted=%d owners=%s" % (
                        v["kind"], v["Pdeobf"], v["m"], v["fn"], v["desc"],
                        v["Sdeobf"], v["s"],
                        ("static " if v["s_acc"] & ACC_STATIC else "") +
                        ("priv" if v["s_acc"] & ACC_PRIVATE else "pub"),
                        v["spec_refs"], v["impacted"], v["impacted_owners"]))

        print("=== INVARIANT 1 (member shadowing) ===")
        print("violations: %d total | %d MUST FIX (impacted>0) | %d spec-only | "
              "%d refs==0 | self-collisions: %d" % (
                  len(violations), len(must_fix), len(spec_only),
                  len(zero_refs), len(self_collisions)))
        print("changed-binding ref events: %d triples, %d refs" % (
            len(events), sum(e[0] for e in events.values())))
        for s_ in self_collisions:
            print("  SELF %s %s.%s / %s -> both '%s' %s" % (
                s_["kind"], s_["deobf"], s_["obfA"], s_["obfB"],
                s_["final"], s_["desc"]))
        for v in must_fix:
            print("  MUST FIX " + vline(v))
        if args.max_print > 0:
            for v in spec_only[:args.max_print]:
                print("  safe(spec) " + vline(v))
            for v in zero_refs[:args.max_print]:
                print("  refs==0    " + vline(v))
            if len(unmatched) > 0:
                print("  unmatched changed-binding events (no violation pair "
                      "matched):")
                for u in unmatched[:args.max_print]:
                    print("    %s %s.%s -> %s.%s %s cnt=%d owners=%s" % (
                        u["kind"], u["D1"], u["m1"], u["D2"], u["m2"],
                        u["desc"], u["cnt"], u["owners"]))

        if must_fix or self_collisions:
            exit_bad = True
        report["members"] = {
            "violations": violations,
            "self_collisions": self_collisions,
            "events": [{"kind": k[0], "D1": k[1], "m1": k[2], "desc": k[3],
                        "D2": k[4], "m2": k[5], "cnt": e[0], "owners": e[1]}
                       for k, e in events.items()],
            "unmatched": unmatched,
        }

    # ---------------- invariant 2: nested classes follow outer ----------------
    nested_violations = []
    if args.invariant in ("nested", "all"):
        by_outer = defaultdict(list)
        for c in classes:
            if "$" in c:
                by_outer[c.rsplit("$", 1)[0]].append(c)
        for outer, lst in sorted(by_outer.items()):
            o_deobf = class_map.get(outer, outer)
            outer_moved = o_deobf != outer
            for n in lst:
                n_deobf = class_map.get(n, n)
                mapped = n in class_map
                if outer_moved:
                    if not mapped:
                        nested_violations.append({
                            "type": "stranded", "outer": outer,
                            "outer_deobf": o_deobf, "nested": n})
                    elif package_of(n_deobf) != package_of(o_deobf) or not (
                            simple_of(n_deobf).startswith(
                                simple_of(o_deobf) + "$") or
                            simple_of(n_deobf).startswith(
                                simple_of(o_deobf) + "_")):
                        nested_violations.append({
                            "type": "misplaced", "outer": outer,
                            "outer_deobf": o_deobf, "nested": n,
                            "nested_deobf": n_deobf})
                elif mapped and n_deobf != n and package_of(n_deobf) != \
                        package_of(outer):
                    nested_violations.append({
                        "type": "outer_not_moved", "outer": outer,
                        "nested": n, "nested_deobf": n_deobf})

        print("=== INVARIANT 2 (nested classes follow outer) ===")
        print("nested-class violations: %d" % len(nested_violations))
        for v in nested_violations[:args.max_print]:
            print("  %s %s | outer %s (%s) | nested %s%s" % (
                v["type"], v["outer_deobf"] if "outer_deobf" in v else "",
                v["outer"], v.get("nested_deobf", "-"), v["nested"], ""))
        if nested_violations:
            exit_bad = True
        report["nested"] = nested_violations

    # ---------------- focus check: subtree members by final name ----------------
    if args.focus_class:
        target = args.focus_class
        obf_target = None
        if target in classes:
            obf_target = target
        else:
            for o, d in class_map.items():
                if d == target:
                    obf_target = o
                    break
        names = set(args.focus_names.split(",")) if args.focus_names else None
        print("=== FOCUS: subtree of %s, final names %s (desc %s) ===" % (
            target, sorted(names) if names else "(any)", args.focus_desc))
        if obf_target is None:
            print("  class not found (checked obf and deobf namespaces)")
        else:
            sub = sorted(ext_subtree(obf_target))
            print("  subtree size: %d" % len(sub))
            hits = 0
            for c in sub:
                for kind, arr in (("FIELD", classes[c]["fields"]),
                                  ("METHOD", classes[c]["methods"])):
                    for (n, d, _a) in arr:
                        if n in INIT:
                            continue
                        if args.focus_desc and d != args.focus_desc:
                            continue
                        fn = mem_final(c, kind, n, d)
                        if names is not None and fn not in names:
                            continue
                        hits += 1
                        print("  %s %s %s (obf %s) -> final %s %s" % (
                            cdsp(c), kind, n, n, fn, d))
            if hits == 0:
                print("  (no declared members match -- clean)")
            report["focus"] = {"class": target, "subtree_size": len(sub),
                               "hits": hits}

    # ---------------- summary / exit ----------------
    report["exit_bad"] = exit_bad
    print("=== RESULT: %s ===" % ("VIOLATIONS FOUND" if exit_bad else "CLEAN"))
    if args.output:
        with open(args.output, "w", encoding="utf-8") as fh:
            json.dump(report, fh, indent=1)
        print("JSON written: %s" % args.output)
    return 1 if exit_bad else 0


def main(argv=None):
    ap = argparse.ArgumentParser(
        description="Structural-invariant audit for Enigma mappings "
                    "(member shadowing + nested-class follow).")
    ap.add_argument("--jar", required=True, help="input jar (obfuscated)")
    ap.add_argument("--mappings", required=True,
                    help="mapping directory (per-class .mapping files) or a "
                         "single Enigma v1 mapping file")
    ap.add_argument("--invariant", choices=("members", "nested", "all"),
                    default="all",
                    help="which invariant to check (default: all)")
    ap.add_argument("--output", help="write full JSON report to this path")
    ap.add_argument("--focus-class",
                    help="extra check: list declared members of this class's "
                         "whole subtree (deobf or obf name, slash form)")
    ap.add_argument("--focus-names",
                    help="comma-separated final member names for --focus-class"
                         " (e.g. x,y,height)")
    ap.add_argument("--focus-desc",
                    help="restrict --focus-class hits to this descriptor "
                         "(e.g. F)")
    ap.add_argument("--max-print", type=int, default=40,
                    help="max rows printed per category (0 = only counts; "
                         "default 40)")
    args = ap.parse_args(argv)
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    return run(args)


if __name__ == "__main__":
    sys.exit(main())
