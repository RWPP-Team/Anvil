# Anvil

Anvil is a mapping for **Rusted Warfare**. It is free to use for everyone. No exceptions.

## Overview

Anvil provides a readable and consistent mapping for Rusted Warfare. It is intended for modders, tool developers, and anyone who works with the game's internal data.

The goal is to make mapping data easier to understand, reference, and maintain across tools and workflows.

## Contributions

We recommend using [Enigma-MCP](https://github.com/Margele/Enigma-MCP).

## Skill

See [SKILL.md](SKILL.md).

## Remaining short member names

Run `python remaining_scan.py` from any directory with Python 3. Defaults are
`game-lib.jar` and `mappings/` beside the script. Override them with:

```sh
python remaining_scan.py --jar path/to/game.jar --mappings path/to/mappings
```

The scanner uses the existing audit parser and Python's standard library; it
does not require Java, `javap`, or `unzip`. It counts declarations named with
1–2 lowercase letters outside `$` nested classes and the existing excluded
external packages. Coverage requires an exact source owner, member kind, source
name, and JVM descriptor. An explicit identity name mapping counts as covered.
A descriptor-less entry does not establish exact coverage. Parameter-only
`METHOD a (I)V` / `ARG` entries do not assign member names or cover other overloads.

Results mean **no explicit member name mapping** for that declaration. They do
not prove that the remapped member remains unnamed: inherited or interface name
propagation is not resolved. Totals include all matches; class detail rows still
require at least three matches. Unreadable inputs or class parse failures exit
nonzero rather than reporting a clean scan. Importing the module does not scan.

Run the regression tests with Python 3 and JDK 9+ `javac` on `PATH`. Tests compile
only the small Java sources in `tests/fixtures/remaining_scan/` with `--release 8`
into temporary JARs and use the real audit parser:

```sh
python -m unittest discover -s tests -v
```
