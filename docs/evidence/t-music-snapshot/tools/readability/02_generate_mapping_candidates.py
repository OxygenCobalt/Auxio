#!/usr/bin/env python3
"""
Generate compact readability mapping candidates for com.tw.music.

This tool accelerates readability closure by scanning canonical apktool/smali
sources and producing a compact, reviewable candidate map.

It is intentionally conservative:
- no smali edits;
- no descriptor renames;
- no automatic mapping promotion;
- no large raw dumps;
- no decompiler output committed.

Candidate buckets:
- AUTO_CONFIRM_CLASS_SOURCE
- AUTO_CONFIRM_NAMED_INNER
- REVIEW_HIGH_IMPACT
- CLASSIFY_VENDOR_EXTERNAL
- CLASSIFY_SYNTHETIC_LOW_VALUE
- CLASSIFY_SUPPORT_THIRD_PARTY
- UNSAFE_REFLECTION_SERIALIZATION
- UNKNOWN_NEEDS_EVIDENCE
"""

from __future__ import annotations

import csv
import datetime as dt
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

ROOT = Path(".")
SMALI_ROOTS = sorted(p for p in (ROOT / "app" / "apktool").glob("smali*") if p.is_dir())

MAPPING_FILE = ROOT / "mappings" / "manual-enigma" / "music-core.mapping"
OUT_MD = ROOT / "docs" / "reports" / "readability-candidate-map.md"
OUT_TSV = ROOT / "docs" / "reports" / "readability-candidate-map.tsv"

MAX_MD_ROWS_PER_BUCKET = 80

SUPPORT_PREFIXES = (
    "Landroid/",
    "Landroidx/",
    "Lantlr/",
    "Lcpdetector/",
    "Lcom/bumptech/",
    "Lcom/google/",
    "Lorg/",
    "Lokhttp3/",
    "Lokio/",
    "Ljava/",
    "Ljavax/",
    "Ldalvik/",
)

APP_OWNED_PREFIXES = (
    "com/tw/music/",
    "com/eckom/xtlibrary/",
)

VENDOR_TOKEN_PATTERNS = (
    "com.tw.music.action",
    "persist.tw.",
    "persist.media.",
    "com.tw.eq",
    "com.tw.radio",
    "com/tw/service/xt/aidl",
)

UNSAFE_PATTERNS = (
    "Ljava/lang/Class;->forName",
    "getDeclaredMethod",
    "getDeclaredField",
    "Ljava/lang/reflect/",
    "Ljava/io/Serializable;",
    "Parcelable",
    "Lorg/json/JSONObject;",
    "Lorg/json/JSONArray;",
    ".method public native",
    ".method private native",
    ".method protected native",
)

MINIFIED_NAME_RE = re.compile(r"^(?:[a-z]|[a-z]{1,2}|[A-Z]|[A-Z]{1,2}|C\d+[a-zA-Z]?|[a-z]\$[a-z0-9]+)$")


@dataclass(frozen=True)
class SmaliClass:
    descriptor: str
    path: Path
    source: str | None
    super_descriptor: str | None
    implements: tuple[str, ...]
    inner_name: str | None
    outer_descriptor: str | None
    body_sample: str

    @property
    def package_path(self) -> str:
        desc = self.descriptor.removeprefix("L").removesuffix(";")
        return "/".join(desc.split("/")[:-1])

    @property
    def simple_name(self) -> str:
        desc = self.descriptor.removeprefix("L").removesuffix(";")
        return desc.split("/")[-1]

    @property
    def source_stem(self) -> str | None:
        if not self.source:
            return None
        return self.source.removesuffix(".java").removesuffix(".kt")


@dataclass(frozen=True)
class Candidate:
    bucket: str
    descriptor: str
    proposed_name: str
    source: str
    evidence: str
    risk: str
    path: str


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except FileNotFoundError:
        return ""


def existing_mapped_descriptors() -> set[str]:
    text = read_text(MAPPING_FILE)
    mapped: set[str] = set()
    for line in text.splitlines():
        if line.strip().startswith("#"):
            continue
        for match in re.findall(r"L[\w/$]+;", line):
            mapped.add(match)
        if "->" in line:
            left = line.split("->", 1)[0].strip()
            if "/" in left:
                mapped.add("L" + left.strip("L;") + ";")
    return mapped


def parse_smali_file(path: Path) -> SmaliClass | None:
    text = read_text(path)
    class_match = re.search(r"^\.class\b.*?(L[\w/$]+;)", text, re.M)
    if not class_match:
        return None

    source_match = re.search(r'^\.source\s+"([^"]+)"', text, re.M)
    super_match = re.search(r"^\.super\s+(L[\w/$]+;)", text, re.M)
    implements = tuple(re.findall(r"^\.implements\s+(L[\w/$]+;)", text, re.M))

    inner_name = None
    inner_match = re.search(r'name\s*=\s*"([^"]+)"', text)
    if inner_match and inner_match.group(1) != "null":
        inner_name = inner_match.group(1)

    outer_descriptor = None
    outer_match = re.search(r"EnclosingClass;.*?value\s*=\s*(L[\w/$]+;)", text, re.S)
    if outer_match:
        outer_descriptor = outer_match.group(1)

    return SmaliClass(
        descriptor=class_match.group(1),
        path=path,
        source=source_match.group(1) if source_match else None,
        super_descriptor=super_match.group(1) if super_match else None,
        implements=implements,
        inner_name=inner_name,
        outer_descriptor=outer_descriptor,
        body_sample=text,
    )


def iter_smali_classes() -> Iterable[SmaliClass]:
    for root in SMALI_ROOTS:
        for path in sorted(root.rglob("*.smali")):
            item = parse_smali_file(path)
            if item:
                yield item


def is_support_or_third_party(cls: SmaliClass) -> bool:
    return cls.descriptor.startswith(SUPPORT_PREFIXES)


def is_generated_resource_holder(cls: SmaliClass) -> bool:
    return cls.simple_name == "R" or cls.simple_name.startswith("R$") or cls.simple_name == "BuildConfig"


def is_vendor_sensitive(cls: SmaliClass) -> bool:
    text = cls.descriptor + "\n" + cls.body_sample
    return any(pattern in text for pattern in VENDOR_TOKEN_PATTERNS)


def has_unsafe_coupling(cls: SmaliClass) -> bool:
    return any(pattern in cls.body_sample for pattern in UNSAFE_PATTERNS)


def meaningful_source_name(cls: SmaliClass) -> bool:
    stem = cls.source_stem
    if not stem:
        return False
    if stem in {"R", "BuildConfig"} or stem.startswith("R$"):
        return False
    if MINIFIED_NAME_RE.match(stem):
        return False
    return True


def proposed_name_from_source(cls: SmaliClass) -> str:
    stem = cls.source_stem or cls.simple_name
    if "$" in cls.simple_name and cls.outer_descriptor:
        outer = cls.outer_descriptor.removeprefix("L").removesuffix(";").split("/")[-1]
        inner = cls.inner_name or stem
        return f"{outer}${inner}"
    return stem


def classify(cls: SmaliClass, mapped: set[str]) -> Candidate | None:
    if cls.descriptor in mapped:
        return None

    if is_generated_resource_holder(cls):
        return Candidate("CLASSIFY_SYNTHETIC_LOW_VALUE", cls.descriptor, cls.simple_name, cls.source or "", "generated Android resource holder", "ignore", str(cls.path))

    if is_support_or_third_party(cls):
        return Candidate("CLASSIFY_SUPPORT_THIRD_PARTY", cls.descriptor, cls.simple_name, cls.source or "", "support/third-party prefix", "low app-maintainer value", str(cls.path))

    if is_vendor_sensitive(cls):
        return Candidate("CLASSIFY_VENDOR_EXTERNAL", cls.descriptor, cls.simple_name, cls.source or "", "vendor/runtime token pattern present", "do not rename casually", str(cls.path))

    if has_unsafe_coupling(cls):
        return Candidate("UNSAFE_REFLECTION_SERIALIZATION", cls.descriptor, cls.simple_name, cls.source or "", "reflection/serialization/native coupling pattern present", "manual review or mapping-only", str(cls.path))

    if cls.inner_name:
        if not any(cls.package_path.startswith(prefix) for prefix in APP_OWNED_PREFIXES):
            return Candidate("CLASSIFY_SUPPORT_THIRD_PARTY", cls.descriptor, cls.simple_name, cls.source or "", "non app-owned inner surface", "low app-maintainer value", str(cls.path))
        return Candidate("AUTO_CONFIRM_NAMED_INNER", cls.descriptor, proposed_name_from_source(cls), cls.source or "", f"non-null InnerClass name={cls.inner_name}; owner={cls.outer_descriptor or 'unknown'}", "class-level mapping-only appears safe; verify owner context", str(cls.path))

    if meaningful_source_name(cls):
        if not any(cls.package_path.startswith(prefix) for prefix in APP_OWNED_PREFIXES):
            return Candidate("CLASSIFY_SUPPORT_THIRD_PARTY", cls.descriptor, cls.simple_name, cls.source or "", "non app-owned source surface", "low app-maintainer value", str(cls.path))
        return Candidate("AUTO_CONFIRM_CLASS_SOURCE", cls.descriptor, proposed_name_from_source(cls), cls.source or "", ".source provides meaningful class-level name", "class-level mapping-only appears safe", str(cls.path))

    if "$" in cls.simple_name:
        return Candidate("CLASSIFY_SYNTHETIC_LOW_VALUE", cls.descriptor, cls.simple_name, cls.source or "", "anonymous/synthetic-looking inner without stable name", "defer unless high-impact", str(cls.path))

    if any(cls.package_path.startswith(prefix) for prefix in APP_OWNED_PREFIXES):
        return Candidate("REVIEW_HIGH_IMPACT", cls.descriptor, cls.simple_name, cls.source or "", "app-owned unreadable class without automatic evidence", "manual review required", str(cls.path))

    return Candidate("UNKNOWN_NEEDS_EVIDENCE", cls.descriptor, cls.simple_name, cls.source or "", "unclassified unreadable symbol", "needs evidence", str(cls.path))


def write_reports(candidates: list[Candidate]) -> None:
    OUT_MD.parent.mkdir(parents=True, exist_ok=True)

    with OUT_TSV.open("w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, delimiter="\t")
        writer.writerow(["bucket", "descriptor", "proposed_name", "source", "evidence", "risk", "path"])
        for c in candidates:
            writer.writerow([c.bucket, c.descriptor, c.proposed_name, c.source, c.evidence, c.risk, c.path])

    buckets: dict[str, list[Candidate]] = {}
    for c in candidates:
        buckets.setdefault(c.bucket, []).append(c)

    now = dt.datetime.now(dt.timezone.utc).strftime("%Y-%m-%d %H:%M:%S UTC")
    lines = [
        "# Readability Candidate Map",
        "",
        f"Generated: {now}",
        "",
        "Generated by `tools/readability/02_generate_mapping_candidates.py`.",
        "This is a compact review queue, not ground truth.",
        "",
        "## Bucket counts",
        "",
        "| Bucket | Count |",
        "|---|---:|",
    ]
    for bucket in sorted(buckets):
        lines.append(f"| `{bucket}` | {len(buckets[bucket])} |")

    for bucket in sorted(buckets):
        rows = buckets[bucket]
        lines.extend(["", f"## {bucket}", "", "| Descriptor | Proposed | Source | Evidence | Risk | Path |", "|---|---|---|---|---|---|"])
        for c in rows[:MAX_MD_ROWS_PER_BUCKET]:
            lines.append(f"| `{c.descriptor}` | `{c.proposed_name}` | `{c.source}` | {c.evidence} | {c.risk} | `{c.path}` |")
        if len(rows) > MAX_MD_ROWS_PER_BUCKET:
            lines.append(f"| … | … | … | {len(rows) - MAX_MD_ROWS_PER_BUCKET} more rows in TSV | … | … |")

    OUT_MD.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    mapped = existing_mapped_descriptors()
    candidates = [c for cls in iter_smali_classes() if (c := classify(cls, mapped))]
    candidates.sort(key=lambda c: (c.bucket, c.path, c.descriptor))
    write_reports(candidates)

    print(f"wrote {OUT_MD}")
    print(f"wrote {OUT_TSV}")
    counts: dict[str, int] = {}
    for c in candidates:
        counts[c.bucket] = counts.get(c.bucket, 0) + 1
    for bucket in sorted(counts):
        print(f"{bucket}: {counts[bucket]}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
