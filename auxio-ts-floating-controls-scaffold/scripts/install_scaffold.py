#!/usr/bin/env python3
"""Install the Auxio-TS floating controls scaffold into a repo working tree.

This copies files from this scaffold into the target repository without overwriting
existing files unless --force is supplied.
"""
from __future__ import annotations

import argparse
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

COPY_MAP = {
    "src/topwayTwMusic": "app/src/topwayTwMusic",
    "patches": "docs/floating-controls-scaffold/patches",
    "docs": "docs/floating-controls-scaffold",
    "LICENSES": "docs/floating-controls-scaffold/LICENSES",
}


def copy_tree(src: Path, dst: Path, force: bool) -> list[Path]:
    copied: list[Path] = []
    for path in src.rglob("*"):
        if path.is_dir():
            continue
        rel = path.relative_to(src)
        target = dst / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.exists() and not force:
            print(f"skip existing: {target}")
            continue
        shutil.copy2(path, target)
        copied.append(target)
        print(f"copied: {target}")
    return copied


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--repo", default=".", help="Auxio-TS repository root")
    parser.add_argument("--force", action="store_true", help="overwrite existing files")
    args = parser.parse_args()

    repo = Path(args.repo).resolve()
    if not repo.exists():
        raise SystemExit(f"Repo path does not exist: {repo}")

    total: list[Path] = []
    for src_rel, dst_rel in COPY_MAP.items():
        src = ROOT / src_rel
        dst = repo / dst_rel
        if not src.exists():
            print(f"missing scaffold source: {src}")
            continue
        total.extend(copy_tree(src, dst, args.force))

    print(f"\nDone. Copied {len(total)} file(s).")
    print("Next: merge the manifest snippet, wire the playback bridge to Auxio's real player, and add settings UI.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
