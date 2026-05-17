#!/usr/bin/env bash
# Create a public-safe Auxio-TS evidence summary from a raw evidence folder.
# This strips obvious serial-like property lines and keeps only selected files.
set -euo pipefail
IN="${1:?usage: $0 <raw-evidence-folder>}"
OUT="${2:-auxio-ts-redacted-evidence-summary}"
rm -rf "$OUT"
mkdir -p "$OUT"
for f in README.txt media_session.txt audio.txt selected_props.txt package_list_relevant.txt package_path.txt input_devices.txt logcat_filtered.txt; do
  [ -f "$IN/$f" ] || continue
  sed -E \
    -e 's/(ro\.boot\.serialno|ro\.serialno|serial|Serial)[^\n]*/\1=[REDACTED]/Ig' \
    -e 's/[A-Fa-f0-9]{16,}/[HEX_OR_ID_REDACTED]/g' \
    "$IN/$f" > "$OUT/$f"
done
zip -qr "$OUT.zip" "$OUT"
echo "Created $OUT.zip"
