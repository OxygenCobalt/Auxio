#!/usr/bin/env python3
"""Fail-fast scan to ensure protected TW vendor tokens remain present in source tree."""

from __future__ import annotations

import argparse
import pathlib
import sys


def load_tokens(path: pathlib.Path) -> list[str]:
    tokens: list[str] = []
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        tokens.append(line)
    return tokens


def collect_files(repo_root: pathlib.Path) -> list[pathlib.Path]:
    scan_roots = [repo_root / "app/apktool", repo_root / "docs", repo_root / "scripts"]
    files: list[pathlib.Path] = []
    for root in scan_roots:
        if not root.exists():
            continue
        for p in root.rglob("*"):
            if p.is_file():
                files.append(p)
    return files


def token_found(token: str, files: list[pathlib.Path]) -> bool:
    needle = token.encode("utf-8")
    for fp in files:
        try:
            if needle in fp.read_bytes():
                return True
        except OSError:
            continue
    return False


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--tokens", default="tools/vendor-audit/protected_tokens.txt")
    parser.add_argument("--repo-root", default=".")
    args = parser.parse_args()

    repo_root = pathlib.Path(args.repo_root).resolve()
    token_file = repo_root / args.tokens
    if not token_file.exists():
        print(f"ERROR: token file not found: {token_file}")
        return 2

    tokens = load_tokens(token_file)
    files = collect_files(repo_root)

    missing = [tok for tok in tokens if not token_found(tok, files)]

    print(f"Scanned files: {len(files)}")
    print(f"Protected tokens: {len(tokens)}")

    if missing:
        print("\nMissing protected tokens:")
        for tok in missing:
            print(f"- {tok}")
        return 1

    print("All protected tokens found.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
