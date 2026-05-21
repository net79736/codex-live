import importlib.util
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch


MODULE_PATH = Path(__file__).with_name("tdd_guard.py")


def load_module():
    spec = importlib.util.spec_from_file_location("tdd_guard_hook", MODULE_PATH)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class TddGuardHookTest(unittest.TestCase):
    def test_prepare_tdd_guard_data_copies_codex_managed_instructions(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp).resolve()
            source = root / ".codex" / "tdd-guard" / "data" / "instructions.md"
            source.parent.mkdir(parents=True)
            source.write_text("Codex managed instructions", encoding="utf-8")

            module._prepare_tdd_guard_data(root)

            compatibility_file = root / ".codex" / "tdd-guard" / "compat" / ".claude" / "tdd-guard" / "data" / "instructions.md"
            self.assertEqual(compatibility_file.read_text(encoding="utf-8"), "Codex managed instructions")

    def test_main_sets_codex_tdd_guard_root(self):
        module = load_module()
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp).resolve()
            (root / ".codex" / "tdd-guard" / "data").mkdir(parents=True)
            captured = {}

            def fake_run(cmd, **kwargs):
                if cmd[:3] == ["git", "rev-parse", "--show-toplevel"]:
                    return type("Result", (), {"returncode": 0, "stdout": str(root), "stderr": ""})()
                captured.update(kwargs)
                return type("Result", (), {"returncode": 0})()

            with patch.object(module.subprocess, "run", side_effect=fake_run), patch.object(module.sys.stdin, "read", return_value="{}"):
                self.assertEqual(module.main(), 0)

            env = captured["env"]
            self.assertEqual(env["TDD_GUARD_PROJECT_ROOT"], str(root))
            self.assertEqual(env["TDD_GUARD_DATA_DIR"], str(root / ".codex" / "tdd-guard" / "data"))
            self.assertEqual(env["CLAUDE_PROJECT_DIR"], str(root / ".codex" / "tdd-guard" / "compat"))


if __name__ == "__main__":
    unittest.main()
