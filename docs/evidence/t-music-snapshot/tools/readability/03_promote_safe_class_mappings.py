#!/usr/bin/env python3

from __future__ import annotations

import argparse
import csv
import datetime as dt
from pathlib import Path

ROOT = Path(".")
CANDIDATE_TSV = ROOT / "docs/reports/readability-candidate-map.tsv"
MAPPING_FILE = ROOT / "mappings/manual-enigma/music-core.mapping"
EVIDENCE_FILE = ROOT / "mappings/manual-enigma/mapping-evidence.md"
NOTES_FILE = ROOT / "docs/deobf/enigma-notes.md"

SAFE_BUCKETS = {"AUTO_CONFIRM_CLASS_SOURCE", "AUTO_CONFIRM_NAMED_INNER"}
APP_PREFIXES = ("com/tw/music/", "com/eckom/xtlibrary/")
SKIP_BUCKETS = {"CLASSIFY_SUPPORT_THIRD_PARTY", "CLASSIFY_SYNTHETIC_LOW_VALUE"}
DUP_TARGET_ALLOWLIST = set()


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except FileNotFoundError:
        return ""


def descriptor_to_slash(descriptor: str) -> str:
    return descriptor.removeprefix("L").removesuffix(";")


def mapped_path(descriptor: str, proposed_name: str) -> str:
    old = descriptor_to_slash(descriptor)
    package = "/".join(old.split("/")[:-1])
    return f"{package}/{proposed_name}" if package else proposed_name


def load_existing_targets() -> set[str]:
    targets: set[str] = set()
    for line in read_text(MAPPING_FILE).splitlines():
        line = line.strip()
        if not line or line.startswith("#") or not line.startswith("CLASS "):
            continue
        parts = line.split()
        if len(parts) == 3:
            targets.add(parts[2])
    return targets


def load_candidates(limit: int) -> list[tuple[dict[str, str], str, str]]:
    if not CANDIDATE_TSV.exists():
        raise FileNotFoundError(
            f"missing candidate map: {CANDIDATE_TSV}. "
            "Run tools/readability/02_generate_mapping_candidates.py first."
        )

    existing = read_text(MAPPING_FILE) + read_text(EVIDENCE_FILE) + read_text(NOTES_FILE)
    existing_targets = load_existing_targets()
    selected: list[tuple[dict[str, str], str, str]] = []

    with CANDIDATE_TSV.open("r", encoding="utf-8", newline="") as file:
        for row in csv.DictReader(file, delimiter="\t"):
            if row["bucket"] in SKIP_BUCKETS or row["bucket"] not in SAFE_BUCKETS:
                continue

            old = descriptor_to_slash(row["descriptor"])
            if not old.startswith(APP_PREFIXES):
                continue

            new = mapped_path(row["descriptor"], row["proposed_name"])
            if old == new:
                continue

            if row["descriptor"] in existing or f"CLASS {old} {new}" in existing:
                continue

            if new in existing_targets and new not in DUP_TARGET_ALLOWLIST:
                continue

            selected.append((row, old, new))
            existing_targets.add(new)
            if len(selected) >= limit:
                break

    return selected


def append_mapping(rows: list[tuple[dict[str, str], str, str]]) -> None:
    if not rows:
        return

    with MAPPING_FILE.open("a", encoding="utf-8") as file:
        for _, old, new in rows:
            file.write(f"CLASS {old} {new}\n")


def append_evidence(rows: list[tuple[dict[str, str], str, str]]) -> None:
    if not rows:
        return

    now = dt.datetime.now(dt.timezone.utc).strftime("%Y-%m-%d UTC")
    with EVIDENCE_FILE.open("a", encoding="utf-8") as file:
        file.write(f"\n## Accelerated safe class mapping evidence ({now})\n")


def append_notes(rows: list[tuple[dict[str, str], str, str]]) -> None:
    if not rows:
        return

    now = dt.datetime.now(dt.timezone.utc).strftime("%Y-%m-%d UTC")
    with NOTES_FILE.open("a", encoding="utf-8") as file:
        file.write(f"\n## Accelerated readability mapping batch ({now})\n")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply-safe-class-mappings", action="store_true")
    parser.add_argument("--limit", type=int, default=80)
    args = parser.parse_args()

    rows = load_candidates(args.limit)
    print(f"selected {len(rows)} safe candidates")
    for _, old, new in rows:
        print(f"CLASS {old} {new}")

    if not args.apply_safe_class_mappings:
        print("dry-run only; pass --apply-safe-class-mappings to update files")
        return 0

    append_mapping(rows)
    append_evidence(rows)
    append_notes(rows)
    print("applied safe class mappings")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
