"""Detect rename hazards described in SKILL.md rules 1 and 3.

A member renamed to NAME must not share NAME + descriptor with a member that a
descendant class declares itself: member resolution walks from the reference's
owner class up the superclass chain and stops at the first matching
name+descriptor, so the descendant declaration shadows the renamed one (and a
static/instance mismatch raises IncompatibleClassChangeError).

Usage: python rename_conflict_check.py owner.java.name memberName desc newName [--jar path]
The owner may be given in obfuscated or deobfuscated form; this script only
knows the jar's (obfuscated) names, so pass the obf name.
"""
import json
import struct
import sys
import zipfile
from collections import defaultdict


def parse_class(data):
    pos = 8
    (cp_count,) = struct.unpack_from(">H", data, pos)
    pos += 2
    cp = [None] * cp_count
    i = 1
    while i < cp_count:
        tag = data[pos]
        pos += 1
        if tag == 1:
            (ln,) = struct.unpack_from(">H", data, pos)
            pos += 2
            cp[i] = data[pos:pos + ln].decode("utf-8", "replace")
            pos += ln
        elif tag in (7, 8, 16, 19, 20):
            (idx,) = struct.unpack_from(">H", data, pos)
            pos += 2
            cp[i] = ("ref", idx)
        elif tag in (15,):
            pos += 3
        elif tag in (3, 4, 9, 10, 11, 12, 17, 18):
            pos += 4
        elif tag in (5, 6):
            pos += 8
            i += 1
        else:
            raise ValueError("bad tag %d at %d" % (tag, pos))
        i += 1

    def utf8(idx):
        val = cp[idx]
        while isinstance(val, tuple):
            val = cp[val[1]]
        return val

    pos += 2  # access flags
    (this_class,) = struct.unpack_from(">H", data, pos)
    pos += 2
    (super_class,) = struct.unpack_from(">H", data, pos)
    pos += 2
    (iface_count,) = struct.unpack_from(">H", data, pos)
    pos += 2
    ifaces = []
    for _ in range(iface_count):
        (idx,) = struct.unpack_from(">H", data, pos)
        pos += 2
        ifaces.append(utf8(idx))

    def skip_attributes(p):
        (n,) = struct.unpack_from(">H", data, p)
        p += 2
        for _ in range(n):
            p += 2
            (ln,) = struct.unpack_from(">I", data, p)
            p += 4 + ln
        return p

    members = []
    for _ in range(2):  # fields then methods
        (n,) = struct.unpack_from(">H", data, pos)
        pos += 2
        rows = []
        for _ in range(n):
            access, name_idx, desc_idx = struct.unpack_from(">HHH", data, pos)
            pos += 6
            pos = skip_attributes(pos)
            rows.append((access, utf8(name_idx), utf8(desc_idx)))
        members.append(rows)

    return {
        "name": utf8(this_class),
        "super": utf8(super_class) if super_class else None,
        "interfaces": ifaces,
        "fields": members[0],
        "methods": members[1],
    }


def load(jar):
    classes = {}
    with zipfile.ZipFile(jar) as z:
        for entry in z.namelist():
            if entry.endswith(".class"):
                info = parse_class(z.read(entry))
                classes[info["name"]] = info
    return classes


def main():
    jar = "game-lib.jar"
    args = [a for a in sys.argv[1:]]
    if "--jar" in args:
        i = args.index("--jar")
        jar = args[i + 1]
        del args[i:i + 2]
    if len(args) < 4:
        print(__doc__)
        return 2
    owner, member, desc, new_name = args[:4]
    kind = "methods" if desc.startswith("(") else "fields"
    owner = owner.replace(".", "/")

    classes = load(jar)
    if owner not in classes:
        print("owner not found in jar: %s" % owner)
        return 2

    children = defaultdict(list)
    for name, info in classes.items():
        if info["super"]:
            children[info["super"]].append(name)
        for iface in info["interfaces"]:
            children[iface].append(name)

    seen, stack, descendants = set(), list(children.get(owner, [])), []
    while stack:
        cur = stack.pop()
        if cur in seen:
            continue
        seen.add(cur)
        descendants.append(cur)
        stack.extend(children.get(cur, []))

    clashes = []
    for d in descendants:
        info = classes.get(d)
        if not info:
            continue
        for access, fname, fdesc in info[kind]:
            if fname == new_name and fdesc == desc:
                own_static = bool(access & 0x0008)
                clashes.append({"descendant": d, "static": own_static})

    result = {
        "owner": owner,
        "member": member,
        "desc": desc,
        "new_name": new_name,
        "descendants_checked": len(descendants),
        "clashes": clashes,
    }
    print(json.dumps(result, indent=1))
    return 0 if not clashes else 1


if __name__ == "__main__":
    sys.exit(main())
