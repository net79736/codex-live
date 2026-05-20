#!/usr/bin/env python3
"""Block high-risk Bash commands before they run."""

import json
import re
import sys


BLOCK_PATTERNS = [
    (re.compile(r"\brm\s+-[^\n]*\brf\b|\brm\s+-[^\n]*\bfr\b"), "Recursive force delete is blocked."),
    (re.compile(r"\bgit\s+reset\s+--hard\b"), "git reset --hard is blocked."),
    (re.compile(r"\bgit\s+push\b[^\n]*\s--force(?:-with-lease)?\b"), "Force push is blocked."),
    (re.compile(r"\bDROP\s+TABLE\b", re.IGNORECASE), "DROP TABLE is blocked."),
]


def _load_payload() -> dict:
    try:
        return json.load(sys.stdin)
    except json.JSONDecodeError:
        return {}


def _command_from(payload: dict) -> str:
    tool_input = payload.get("tool_input")
    if isinstance(tool_input, dict):
        command = tool_input.get("command")
        if isinstance(command, str):
            return command
    return ""


def main() -> None:
    command = _command_from(_load_payload())
    for pattern, reason in BLOCK_PATTERNS:
        if pattern.search(command):
            print(json.dumps({
                "hookSpecificOutput": {
                    "hookEventName": "PreToolUse",
                    "permissionDecision": "deny",
                    "permissionDecisionReason": reason,
                }
            }))
            return


if __name__ == "__main__":
    main()
