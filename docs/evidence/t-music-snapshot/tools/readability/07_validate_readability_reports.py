#!/usr/bin/env python3
"""Validate readability closure artifacts for mapping/report hygiene."""
from __future__ import annotations

import csv
import re
import sys
from pathlib import Path

ROOT = Path('.')
MAPPING = ROOT / 'mappings/manual-enigma/music-core.mapping'
LEDGER = ROOT / 'docs/reports/readability-method-boundary-review.tsv'
HIGH_IMPACT = ROOT / 'docs/reports/readability-high-impact-methods.md'
CANDIDATE = ROOT / 'docs/reports/readability-candidate-map.tsv'
PLAN = ROOT / 'docs/readability-closure-plan-2026-05.md'

ACTIVE_STATUSES = {'unresolved_actionable', 'deferred_static', 'runtime_needed'}
ALLOWED_STATUSES = {
    'unresolved_actionable', 'deferred_static', 'runtime_needed',
    'confirmed', 'closed', 'reviewed_but_grouped', 'low_value',
    'generated_or_bridge', 'simple_delegate', 'trivial_accessor',
    'constructor_or_clinit', 'no_readability_value',
}
DUP_TARGET_ALLOWLIST: set[str] = set()
APP_PREFIXES = ('Lcom/tw/music/', 'Lcom/eckom/xtlibrary/')
SUPPORT_PREFIXES = (
    'Landroid/', 'Landroidx/', 'Landroid/arch/', 'Landroid/support/',
    'Lantlr/', 'Lcpdetector/', 'Lcom/bumptech/', 'Lcom/google/', 'Lorg/',
    'Lokhttp3/', 'Lokio/', 'Ljava/', 'Ljavax/', 'Ldalvik/',
)


def read(path: Path) -> str:
    return path.read_text(encoding='utf-8', errors='replace')


def load_tsv(path: Path) -> list[dict[str, str]]:
    with path.open('r', encoding='utf-8', newline='') as f:
        return list(csv.DictReader(f, delimiter='\t'))


def parse_true_remaining(md: str) -> int | None:
    m = re.search(r'true_remaining_actionable:\s*(\d+)', md)
    return int(m.group(1)) if m else None


def main() -> int:
    errs: list[str] = []

    mapping_text = read(MAPPING)
    if '->' in mapping_text:
        errs.append('mapping file contains arrow-format entries')

    targets: dict[str, int] = {}
    for lineno, raw in enumerate(mapping_text.splitlines(), 1):
        line = raw.strip()
        if not line or line.startswith('#'):
            continue
        if not line.startswith('CLASS '):
            continue
        parts = line.split()
        if len(parts) != 3:
            errs.append(f'invalid mapping line format at line {lineno}')
            continue
        _, src, dst = parts
        if src == dst:
            errs.append(f'mapping no-op line {lineno}')
        if dst in targets and dst not in DUP_TARGET_ALLOWLIST:
            errs.append(f'duplicate target mapping: {dst}')
        targets[dst] = lineno

    ledger_rows = load_tsv(LEDGER)
    for i, row in enumerate(ledger_rows, 2):
        status = (row.get('status') or '').strip()
        if status not in ALLOWED_STATUSES:
            errs.append(f'invalid ledger status at row {i}: {status!r}')
        for field in ('family', 'status', 'evidence'):
            if not (row.get(field) or '').strip():
                errs.append(f'missing {field} field at ledger row {i}')

    high_text = read(HIGH_IMPACT)
    declared = parse_true_remaining(high_text)
    triage_rows = load_tsv(ROOT / 'docs/reports/readability-method-triage-generated.tsv')
    triage_active_rows = [row for row in triage_rows if (row.get('status') or '').strip() in ACTIVE_STATUSES]
    ledger_active_rows = [row for row in ledger_rows if (row.get('status') or '').strip() in ACTIVE_STATUSES]
    triage_keys = {(r.get('owner_descriptor','').strip(), r.get('method_signature','').strip()) for r in triage_active_rows}
    ledger_keys = {(r.get('owner_descriptor','').strip(), r.get('method_signature','').strip()) for r in ledger_active_rows}
    calculated = len(triage_keys | ledger_keys)
    ledger_active = len(ledger_active_rows)
    if declared is not None and declared != calculated:
        errs.append(
            f'true_remaining_actionable differs from calculated active rows: '
            f'declared={declared} calculated={calculated}'
        )

    plan_text = read(PLAN)
    if 'reference/firstparty-jadx/' in plan_text and not (ROOT / 'reference/firstparty-jadx').exists():
        errs.append('docs reference missing reference/firstparty-jadx/ as available')
    if 'reference/vendor-jadx/' in plan_text and not (ROOT / 'reference/vendor-jadx').exists():
        errs.append('docs reference missing reference/vendor-jadx/ as available')

    for family in ('Launcher', 'Video', 'Radio', 'BT', 'Music'):
        if re.search(rf'{family} family[^\n]*closed', plan_text, re.IGNORECASE):
            if any((row.get('family') == family and row.get('status') in ACTIVE_STATUSES) for row in ledger_rows):
                errs.append(f'docs claim a closed family but active rows remain for {family}')

    candidate_rows = load_tsv(CANDIDATE)
    for row in candidate_rows:
        bucket = row.get('bucket', '')
        desc = row.get('descriptor', '')
        if bucket.startswith('AUTO_CONFIRM'):
            if not desc.startswith(APP_PREFIXES):
                errs.append('support/library surface entered app-owned AUTO_CONFIRM queues')
                break
            if desc.startswith(SUPPORT_PREFIXES):
                errs.append('support/library AUTO_CONFIRM prefix detected')
                break

    if errs:
        print('VALIDATION FAILED:')
        for err in errs:
            print(f'- {err}')
        return 1

    print('VALIDATION OK')
    print(f'active_rows={ledger_active}')
    print(f'true_remaining_actionable={declared}')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
