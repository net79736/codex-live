#!/usr/bin/env python3
"""Run lightweight validation at turn stop when a supported build file exists."""

import json
import os
import subprocess
import sys
from pathlib import Path
from typing import List, Optional


NPM_CHECKS = ("lint", "build", "test")


def _payload() -> dict:
    try:
        return json.load(sys.stdin)
    except json.JSONDecodeError:
        return {}


def _load_package_json(root: Path) -> dict:
    package_json = root / "package.json"
    if not package_json.exists():
        return {}
    try:
        return json.loads(package_json.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return {}


def _command_for(root: Path) -> Optional[List[str]]:
    if (root / "gradlew").exists():
        return ["./gradlew", "test"]
    if (root / "build.gradle").exists() or (root / "build.gradle.kts").exists():
        return ["gradle", "test"]
    if (root / "mvnw").exists():
        return ["./mvnw", "test"]
    if (root / "pom.xml").exists():
        return ["mvn", "test"]

    package = _load_package_json(root)
    scripts = package.get("scripts", {}) if isinstance(package, dict) else {}
    npm_runnable = [name for name in NPM_CHECKS if name in scripts]
    if npm_runnable:
        return ["npm", "run", npm_runnable[-1]]

    return None


def main() -> None:
    payload = _payload()
    if payload.get("stop_hook_active"):
        print(json.dumps({"continue": True}))
        return

    root = Path(os.environ.get("PWD", ".")).resolve()
    command = _command_for(root)

    if not command:
        print(json.dumps({"continue": True}))
        return

    result = subprocess.run(
        command,
        cwd=root,
        capture_output=True,
        text=True,
        timeout=180,
    )

    if result.returncode != 0:
        output = (result.stdout + "\n" + result.stderr).strip()
        print(json.dumps({
            "decision": "block",
            "reason": (
                "Stop validation failed. Fix this before finishing:\n\n"
                + " ".join(command)
                + " failed:\n"
                + output[-2000:]
            ),
        }))
        return

    print(json.dumps({"continue": True}))


if __name__ == "__main__":
    main()
