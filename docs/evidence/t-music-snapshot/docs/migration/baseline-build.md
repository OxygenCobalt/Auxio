# Baseline Build Reproducibility (Legacy Apktool Track)

This document captures the deterministic baseline flow for the current canonical source surface (`app/apktool`).

## Preconditions

- Android SDK Build-Tools and Java are installed (see `docs/manual-steps/01-android-sdk-build-tools.md`).
- Working directory is repo root.

## Baseline commands

```bash
bash scripts/02_build_unsigned.sh
bash scripts/01_refresh_reports.sh
bash tools/parity/hash_artifacts.sh
```

## Expected artifacts

After running the commands above, these artifacts should exist:

- `dist/com.tw.music-unsigned.apk`
- `docs/reports/baseline-apk.sha256`
- `docs/reports/baseline-manifest.sha256`
- `docs/reports/baseline-activities.sha256`
- `docs/reports/baseline-services.sha256`
- `docs/reports/baseline-receivers.sha256`

## Notes

- `tools/parity/hash_artifacts.sh` fails fast when the unsigned APK does not exist.
- Hashes are intended for reproducibility comparison between runs/environments in the migration's legacy verification track.
- To compare manifest/resource inventory drift between two artifacts, run:

```bash
bash tools/parity/diff_manifest_resources.sh /path/to/baseline.apk /path/to/candidate.apk
```

  Diffs are emitted to `docs/reports/parity-manifest.diff` and `docs/reports/parity-resources.diff` when differences are detected.
- In GitHub Actions, use manual workflow `parity-diff-manual` with:
  - `baseline_apk=dist/com.tw.music.apk`
  - `build_candidate=true`
  - `candidate_apk=dist/com.tw.music-unsigned.apk`
  - `fail_on_diff=false` for review-first mode (or `true` for blocking mode).
