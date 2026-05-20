#!/usr/bin/env python3
"""Run TDD Guard from Codex hooks with a stable project root."""

import os
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


def main() -> int:
    payload = sys.stdin.read()
    root = _project_root()
    env = os.environ.copy()

    # TDD Guard and its Java/JUnit5 reporter use these roots to find/write
    # .claude/tdd-guard/data/test.json. Keep both for compatibility.
    env.setdefault("TDD_GUARD_PROJECT_ROOT", str(root))
    env.setdefault("CLAUDE_PROJECT_DIR", str(root))

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
