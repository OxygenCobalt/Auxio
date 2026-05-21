# TS18 Native Parity Gap Matrix

This matrix is updated from validated evidence packs, not speculation.

| Scenario | Area | Android-standard parity status | Native/private investigation needed | Confidence | Porting decision | Next action |
|---|---|---|---|---|---|---|
| TS18-STD-001..017 | Multiple | Requires TS18 validation | No default | Requires TS18 validation | Requires TS18 runtime validation | Use `scripts/ts18-summarise-evidence-pack.py` then review `derived/parity-gap-matrix-update.md`. |

## Update workflow
1. Capture pack via `scripts/ts18-create-evidence-pack.sh`.
2. Summarise using `scripts/ts18-summarise-evidence-pack.py`.
3. Generate proposal using `scripts/ts18-propose-gap-matrix-update.py`.
4. Manually review and edit this matrix; do not auto-apply.

Tier 3 candidate drafts may be generated from failed/partial validated scenarios only; missing evidence alone must not trigger candidates.
