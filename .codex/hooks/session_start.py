#!/usr/bin/env python3
"""Add lightweight project context when a Codex session starts."""

import json


def main() -> None:
    print(json.dumps({
        "hookSpecificOutput": {
            "hookEventName": "SessionStart",
            "additionalContext": (
                "This repository uses AGENTS.md for project instructions and "
                ".codex/skills/ for Harness workflow skills. TDD Guard is enabled "
                "for write/edit hooks. For Java Spring projects, configure the "
                "JUnit5 reporter in Gradle or Maven so test results are written."
            ),
        }
    }))


if __name__ == "__main__":
    main()
