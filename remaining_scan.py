"""Find classes that still contain genuinely scrambled (short) member names.

Compares member names found in game-lib.jar (via javap) against the obf names
recorded in the mappings directory. Members whose name is a short obfuscated
identifier (1-2 lowercase letters) and that have no mapping row are remaining
work. Everything else (long names, <init>/<clinit>, identity rows) is noise.
"""
import os
import re
import subprocess
import sys
from collections import defaultdict

ROOT = os.path.dirname(os.path.abspath(__file__))
JAR = os.path.join(ROOT, "game-lib.jar")
MAPPINGS = os.path.join(ROOT, "mappings")
SKIP_PREFIXES = ("com/codedisaster/", "android/", "java/", "javax/", "org/xml", "junit")
SHORT = re.compile(r"^[a-z]{1,2}$")

# 1. mappings: class (obf dotted) -> set of obf member names covered
covered = defaultdict(set)
classes_seen = set()
for dirpath, _dirnames, filenames in os.walk(MAPPINGS):
    for fn in filenames:
        if not fn.endswith(".mapping"):
            continue
        path = os.path.join(dirpath, fn)
        with open(path, encoding="utf-8") as f:
            for line in f:
                line = line.rstrip("\n")
                if not line.strip():
                    continue
                if not line.startswith("\t"):
                    obf_class = line.split()[1].replace("/", ".")
                    classes_seen.add(obf_class)
                else:
                    parts = line.split()
                    if parts[0] in ("FIELD", "METHOD"):
                        covered[obf_class].add(parts[1])

# 2. jar class list
out = subprocess.run(["unzip", "-Z1", JAR], capture_output=True, text=True).stdout
classes = [
    l[:-6].replace("/", ".")
    for l in out.splitlines()
    if l.endswith(".class") and "$" not in l.split("/")[-1]
]
classes = sorted(
    c for c in classes
    if not c.startswith(("com.codedisaster.", "android.", "java.", "javax.", "org.xml", "junit"))
)

# 3. javap in batches
remaining = defaultdict(lambda: [0, set()])  # class -> [count, {members}]
BATCH = 400
for i in range(0, len(classes), BATCH):
    chunk = classes[i : i + BATCH]
    r = subprocess.run(
        ["javap", "-p", "-cp", JAR] + chunk, capture_output=True, text=True, errors="replace"
    )
    cur = None
    for line in r.stdout.splitlines():
        s = line.strip()
        if not s:
            continue
        if s.endswith("{"):
            toks = s.rstrip("{").split()
            cur = None
            for j, t in enumerate(toks):
                if t in ("class", "interface", "enum") and j + 1 < len(toks):
                    cur = toks[j + 1]
                    break
            continue
        if cur is None or s in ("}",):
            continue
        m = re.match(r"^.*?\s([A-Za-z_$][A-Za-z0-9_$]*)\s*\(", s)
        kind = "method"
        if not m:
            m = re.match(r"^.*?\s([A-Za-z_$][A-Za-z0-9_$]*);", s)
            kind = "field"
        if not m:
            continue
        name = m.group(1)
        if name.startswith("<") or not SHORT.match(name):
            continue
        dotted = cur
        if name not in covered.get(dotted, set()):
            remaining[dotted][0] += 1
            remaining[dotted][1].add(name)

rows = sorted(remaining.items(), key=lambda kv: -kv[1][0])
total = sum(v[0] for v in remaining.values())
print(f"classes with remaining scrambled members: {len(rows)}; total members: {total}")
for cls, (n, names) in rows:
    if n >= 3:
        print(f"{n:4d}  {cls}  e.g. {sorted(names)[:8]}")
