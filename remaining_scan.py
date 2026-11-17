"""Find short declared members without an explicit member name mapping.

Coverage requires an exact (owner, FIELD|METHOD, sourceName, descriptor) row.
Identity name mappings count as covered. This does not resolve inherited name
propagation, so a reported declaration may already be renamed by the remapper.
"""
import argparse
from pathlib import Path
import re
import sys
import zipfile

from enigma_violation_audit import load_jar, parse_mappings


ROOT = Path(__file__).resolve().parent
SKIP_PREFIXES = ("com/codedisaster/", "android/", "java/", "javax/", "org/xml", "junit")
SHORT = re.compile(r"^[a-z]{1,2}$")


def find_remaining(jar, mappings):
    """Return owner -> [(kind, source name, descriptor)] for uncovered members."""
    classes, _refs, errors = load_jar(jar)
    if errors:
        raise ValueError("could not parse class files: " + "; ".join(
            f"{name}: {error}" for name, error in errors
        ))
    _class_map, covered, _file_count = parse_mappings(mappings)
    remaining = {}
    for owner, data in sorted(classes.items()):
        if "$" in owner.rsplit("/", 1)[-1] or owner.startswith(SKIP_PREFIXES):
            continue
        members = []
        for kind, declarations in (("FIELD", data["fields"]), ("METHOD", data["methods"])):
            for name, descriptor, _access in declarations:
                if SHORT.fullmatch(name) and (owner, kind, name, descriptor) not in covered:
                    members.append((kind, name, descriptor))
        if members:
            remaining[owner] = members
    return remaining


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", type=Path, default=ROOT / "game-lib.jar",
                        help="input JAR (default: game-lib.jar beside this script)")
    parser.add_argument("--mappings", type=Path, default=ROOT / "mappings",
                        help="Enigma mapping directory or file (default: mappings/ beside this script)")
    args = parser.parse_args(argv)
    try:
        remaining = find_remaining(args.jar, args.mappings)
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print(f"remaining_scan: {error}", file=sys.stderr)
        return 1

    total = sum(len(members) for members in remaining.values())
    print(f"classes with remaining scrambled members: {len(remaining)}; total members: {total}")
    print("Counts short declarations without explicit member name mappings; inherited name propagation is not resolved.")
    for owner, members in sorted(remaining.items(), key=lambda row: -len(row[1])):
        if len(members) >= 3:
            names = sorted({name for _kind, name, _descriptor in members})[:8]
            print(f"{len(members):4d}  {owner.replace('/', '.')}  e.g. {names}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
