# Development Readiness — Next Steps

Last updated: 2026-05-08 (UTC)

> Live gate/source-of-truth status is maintained only in `docs/migration-blueprint.md`.

## Required validation baseline (smali/runtime-related work)

```bash
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/build_source_shim.sh
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git diff --stat
git diff --numstat
git status --short
```

Optional when available:

```bash
apktool b app/apktool -o .local/<task-name>-check.apk
```

Do not commit `.local` APK outputs.

## Sequenced next actions

1. Finish/merge any remaining static smali repair sweep tasks.
2. Run local static validation including `tools/smali/validate_smali_static.py`.
3. Run optional Apktool build check if `apktool` is installed.
4. Execute Gate V TS18 runtime validation using `docs/manual-validation-runbook.md`.
5. If Gate V fails, open targeted bug-fix PR(s) based on captured evidence.
6. If Gate V passes, ingest evidence and update gate state in `docs/migration-blueprint.md`.
7. Prepare Gate RC checklist.
8. Start RC hardening only after Gate V and Gate RC evidence gates are satisfied.
