#!/usr/bin/env python3
"""Run TDD Guard from Codex hooks with a stable project root."""

import os
import shutil
import subprocess
import sys
from pathlib import Path


def _project_root() -> Path:
    result = subprocess.run(
        ["git", "rev-parse", "--show-toplevel"],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode == 0:
        return Path(result.stdout.strip()).resolve()
    return Path.cwd().resolve()


def _prepare_tdd_guard_data(root: Path) -> Path:
    """Keep TDD Guard data managed under .codex, with tool compatibility."""
    codex_data = root / ".codex" / "tdd-guard" / "data"
    compatibility_root = root / ".codex" / "tdd-guard" / "compat"
    compatibility_data = compatibility_root / ".claude" / "tdd-guard" / "data"
    instructions = codex_data / "instructions.md"

    if not instructions.exists():
        return compatibility_root

    compatibility_data.mkdir(parents=True, exist_ok=True)
    shutil.copy2(instructions, compatibility_data / "instructions.md")
    return compatibility_root


def main() -> int:
    payload = sys.stdin.read()
    root = _project_root()
    compatibility_root = _prepare_tdd_guard_data(root)
    env = os.environ.copy()

    # .codex/tdd-guard/data is the repo-owned Codex path. CLAUDE_PROJECT_DIR is
    # still set because upstream tdd-guard may use it for compatibility.
    env.setdefault("TDD_GUARD_PROJECT_ROOT", str(root))
    env.setdefault("TDD_GUARD_DATA_DIR", str(root / ".codex" / "tdd-guard" / "data"))
    env.setdefault("CLAUDE_PROJECT_DIR", str(compatibility_root))

    result = subprocess.run(
        ["npx", "-y", "tdd-guard@latest"],
        input=payload,
        cwd=root,
        env=env,
        text=True,
    )
    return result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
