import importlib.util
import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
import unittest
import zipfile


ROOT = Path(__file__).resolve().parents[1]
SCANNER = Path(os.environ.get("REMAINING_SCAN_SCRIPT", ROOT / "remaining_scan.py"))
FIXTURES = Path(__file__).parent / "fixtures" / "remaining_scan"


class RemainingScanTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        javac = shutil.which("javac")
        if javac is None:
            raise unittest.SkipTest("javac is required to compile the regression fixture")
        cls.build = tempfile.TemporaryDirectory()
        cls.addClassCleanup(cls.build.cleanup)
        cls.classes = Path(cls.build.name) / "classes"
        cls.classes.mkdir()
        subprocess.run(
            [javac, "--release", "8", "-d", str(cls.classes)]
            + [str(path) for path in sorted(FIXTURES.rglob("*.java"))],
            check=True, capture_output=True, text=True,
        )

    def prepare(self, names, mappings):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        root = Path(temporary.name)
        shutil.copy2(SCANNER, root / "remaining_scan.py")
        shutil.copy2(ROOT / "enigma_violation_audit.py", root)
        jar = root / "game-lib.jar"
        with zipfile.ZipFile(jar, "w") as archive:
            for name in names:
                entry = name + ".class"
                archive.write(self.classes / entry, entry)
        mapping_dir = root / "mappings"
        mapping_dir.mkdir()
        (mapping_dir / "fixture.mapping").write_text(mappings, encoding="utf-8")
        return root, jar, mapping_dir

    def run_cli(self, root, *args):
        if "REMAINING_SCAN_SCRIPT" in os.environ:
            with zipfile.ZipFile(root / "game-lib.jar") as archive:
                names = [name[:-6].replace("/", ".") for name in archive.namelist()
                         if name.endswith(".class")]
            for command in (["unzip", "-Z1", str(root / "game-lib.jar")],
                            ["javap", "-p", "-cp", str(root / "game-lib.jar"), *names]):
                probe = subprocess.run(command, capture_output=True, text=True)
                self.assertEqual(probe.returncode, 0, probe.stderr)
                self.assertTrue(probe.stdout.strip(), command)
                print(f"Baseline tool probe: {command[0]} exit=0\n{probe.stdout}", file=sys.stderr)
        result = subprocess.run(
            [sys.executable, str(root / "remaining_scan.py"), *map(str, args)],
            cwd=root, capture_output=True, text=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)
        return result.stdout

    def load_scanner(self, root):
        # Load the copied scanner with the real helper, without triggering a scan.
        sys.path.insert(0, str(root))
        self.addCleanup(sys.path.remove, str(root))
        spec = importlib.util.spec_from_file_location("fixture_remaining_scan", root / "remaining_scan.py")
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        return module

    def test_partial_overload_mapping_cli(self):
        root, _, _ = self.prepare(
            ["fixture/Overloads"],
            "CLASS fixture/Overloads readable/Overloads\n\tMETHOD a ready ()V\n",
        )
        output = self.run_cli(root)
        self.assertIn("classes with remaining scrambled members: 1; total members: 3", output)
        self.assertIn("   3  fixture.Overloads", output)

    def test_field_and_method_same_name_cli(self):
        root, _, _ = self.prepare(
            ["fixture/Kinds"],
            "CLASS fixture/Kinds readable/Kinds\n\tFIELD b count I\n\tMETHOD c clear ()V\n",
        )
        output = self.run_cli(root)
        self.assertIn("classes with remaining scrambled members: 1; total members: 2", output)
        self.assertNotIn("fixture.Kinds", output)

    def test_identity_rows_cover_only_exact_member_cli(self):
        root, _, _ = self.prepare(
            ["fixture/Identity"],
            "CLASS fixture/Identity\n\tFIELD d d I\n\tMETHOD e e ()V\n",
        )
        output = self.run_cli(root)
        self.assertIn("classes with remaining scrambled members: 1; total members: 2", output)

    def test_exact_descriptors_and_missing_descriptor_are_not_wildcards(self):
        root, jar, mappings = self.prepare(
            ["fixture/Overloads"],
            "CLASS fixture/Overloads\n\tMETHOD a ready ()V\n\tMETHOD a legacy\n",
        )
        remaining = self.load_scanner(root).find_remaining(jar, mappings)
        self.assertEqual(remaining, {"fixture/Overloads": [
            ("METHOD", "a", "(I)V"),
            ("METHOD", "a", "(J)V"),
            ("METHOD", "a", "(Ljava/lang/String;)V"),
        ]})

    def test_parameter_only_row_does_not_map_member_names(self):
        root, jar, mappings = self.prepare(
            ["fixture/Overloads"],
            "CLASS fixture/Overloads\n\tMETHOD a (I)V\n\t\tARG 1 value\n",
        )
        remaining = self.load_scanner(root).find_remaining(jar, mappings)
        self.assertEqual(remaining, {"fixture/Overloads": [
            ("METHOD", "a", "()V"),
            ("METHOD", "a", "(I)V"),
            ("METHOD", "a", "(J)V"),
            ("METHOD", "a", "(Ljava/lang/String;)V"),
        ]})

    def test_scope_and_explicit_owner_only(self):
        root, jar, mappings = self.prepare(
            ["fixture/Scope", "fixture/Scope$Nested", "fixture/Parent", "fixture/Child", "android/External"],
            "CLASS fixture/Scope\n\tCLASS Nested\n\t\tFIELD ab nestedValue I\n"
            "CLASS fixture/Parent\n\tMETHOD a activate ()V\n",
        )
        remaining = self.load_scanner(root).find_remaining(jar, mappings)
        self.assertEqual(remaining, {
            "fixture/Child": [("METHOD", "a", "()V")],
            "fixture/Scope": [("FIELD", "ab", "I")],
        })

    def test_explicit_cli_paths(self):
        root, jar, mappings = self.prepare(["fixture/Scope"], "")
        custom_jar = root / "custom.jar"
        custom_mappings = root / "custom.mapping"
        jar.rename(custom_jar)
        (mappings / "fixture.mapping").rename(custom_mappings)
        mappings.rmdir()
        output = self.run_cli(root, "--jar", custom_jar, "--mappings", custom_mappings)
        self.assertIn("classes with remaining scrambled members: 1; total members: 1", output)

    def test_import_does_not_scan_or_print(self):
        root, jar, mappings = self.prepare([], "")
        jar.unlink()
        shutil.rmtree(mappings)
        result = subprocess.run(
            [sys.executable, "-c", "import remaining_scan"],
            cwd=root, capture_output=True, text=True,
        )
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(result.stdout, "")
        self.assertEqual(result.stderr, "")

    def test_bad_class_fails_instead_of_reporting_clean(self):
        root, jar, _ = self.prepare([], "")
        with zipfile.ZipFile(jar, "a") as archive:
            archive.writestr("fixture/Broken.class", b"not a class file")
        result = subprocess.run(
            [sys.executable, str(root / "remaining_scan.py")],
            cwd=root, capture_output=True, text=True,
        )
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("fixture/Broken.class", result.stderr)
        self.assertNotIn("total members", result.stdout)


if __name__ == "__main__":
    unittest.main()
