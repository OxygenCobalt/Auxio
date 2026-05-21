# TS18 Hardware Validation Checklist

1. Install Auxio-TS build on TS18 hardware.
2. Place Auxio widget and publish shortcuts.
3. Start playback and verify visible notification.
4. Run capture: `bash scripts/ts18-create-evidence-pack.sh --device-label <label> --auxio-build <sha>`.
5. Exercise TS18-STD-001..017 per runbook.
6. Add screenshots/video references in `raw/screenshots/README.md`.
7. Zip/share large artifacts outside git.
8. Summarise: `python3 scripts/ts18-summarise-evidence-pack.py <pack_dir>`.
9. Propose matrix update: `python3 scripts/ts18-propose-gap-matrix-update.py <pack_dir>`.
10. Manually review `derived/parity-gap-matrix-update.md` then update canonical matrix.
