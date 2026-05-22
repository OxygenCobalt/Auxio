# TS18 Evidence Pack Index

Use naming: `YYYY-MM-DD-<device-or-build-label>/`.

## Registry row format
- Pack ID
- Date
- Device/firmware label
- Auxio build
- Scenarios covered
- Redaction status
- Location (repo path or external zip URI)
- Summary status
- Matrix update status

## Workflow
1. Capture pack: `bash scripts/ts18-create-evidence-pack.sh ...`
2. Summarise: `python3 scripts/ts18-summarise-evidence-pack.py <pack>`
3. Propose matrix updates: `python3 scripts/ts18-propose-gap-matrix-update.py <pack>`
4. Generate native candidates (if eligible): `python3 scripts/ts18-generate-native-candidates.py <pack>`
5. Redact before sharing: `bash scripts/redact_ts18_evidence_summary.sh <pack>`

Keep large raw artifacts external; store only redacted references in git.
