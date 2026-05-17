#!/usr/bin/env python3
"""
Classify remaining readability tail.

This is the mechanism that makes practical readability closure finite:
remaining symbols are intentionally classified instead of chased forever.
"""

from __future__ import annotations

import csv
import datetime as dt
from pathlib import Path

ROOT = Path(".")
CANDIDATE_TSV = ROOT / "docs" / "reports" / "readability-candidate-map.tsv"
OUT_CLASS = ROOT / "docs" / "reports" / "readability-tail-classification.md"
OUT_UNRESOLVED = ROOT / "docs" / "reports" / "readability-unresolved-queue.md"

TAIL_MAP = {
    "CLASSIFY_VENDOR_EXTERNAL": "vendor/external contract",
    "CLASSIFY_SUPPORT_THIRD_PARTY": "support/third-party",
    "CLASSIFY_SYNTHETIC_LOW_VALUE": "generated/synthetic or anonymous low-value",
    "UNSAFE_REFLECTION_SERIALIZATION": "unsafe reflection/JNI/serialization/resource coupling",
    "UNKNOWN_NEEDS_EVIDENCE": "unknown needs evidence",
    "REVIEW_HIGH_IMPACT": "high-impact unresolved",
}


def load_rows() -> list[dict[str, str]]:
    if not CANDIDATE_TSV.exists():
        raise SystemExit(f"missing {CANDIDATE_TSV}; run 02_generate_mapping_candidates.py first")
    with CANDIDATE_TSV.open("r", encoding="utf-8", newline="") as f:
        return list(csv.DictReader(f, delimiter="\t"))


def main() -> int:
    rows = load_rows()
    now = dt.datetime.now(dt.timezone.utc).strftime("%Y-%m-%d %H:%M:%S UTC")

    buckets: dict[str, list[dict[str, str]]] = {}
    for row in rows:
        category = TAIL_MAP.get(row["bucket"])
        if category:
            buckets.setdefault(category, []).append(row)

    OUT_CLASS.parent.mkdir(parents=True, exist_ok=True)

    lines = [
        "# Readability Tail Classification",
        "",
        f"Generated: {now}",
        "",
        "This report classifies remaining unreadable symbols for practical readability closure.",
        "",
        "| Category | Count |",
        "|---|---:|",
    ]
    for category in sorted(buckets):
        lines.append(f"| {category} | {len(buckets[category])} |")

    for category in sorted(buckets):
        lines.extend(["", f"## {category}", "", "| Descriptor | Source | Evidence | Risk | Path |", "|---|---|---|---|---|"])
        for row in buckets[category][:100]:
            lines.append(f"| `{row['descriptor']}` | `{row['source']}` | {row['evidence']} | {row['risk']} | `{row['path']}` |")
        if len(buckets[category]) > 100:
            lines.append(f"| … | … | {len(buckets[category]) - 100} more not shown | … | … |")

    OUT_CLASS.write_text("\n".join(lines) + "\n", encoding="utf-8")

    unresolved = buckets.get("high-impact unresolved", []) + buckets.get("unknown needs evidence", [])
    u_lines = [
        "# Readability Unresolved Queue",
        "",
        f"Generated: {now}",
        "",
        "These are symbols requiring evidence, deferral, or manual review.",
        "",
        "| Descriptor | Bucket | Source | Evidence | Risk | Path |",
        "|---|---|---|---|---|---|",
    ]
    for row in unresolved[:150]:
        u_lines.append(f"| `{row['descriptor']}` | `{row['bucket']}` | `{row['source']}` | {row['evidence']} | {row['risk']} | `{row['path']}` |")
    if len(unresolved) > 150:
        u_lines.append(f"| … | … | … | {len(unresolved) - 150} more not shown | … | … |")

    OUT_UNRESOLVED.write_text("\n".join(u_lines) + "\n", encoding="utf-8")

    print(f"wrote {OUT_CLASS}")
    print(f"wrote {OUT_UNRESOLVED}")
    for category in sorted(buckets):
        print(f"{category}: {len(buckets[category])}")
    print(f"unresolved queue: {len(unresolved)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
