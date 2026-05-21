#!/usr/bin/env bash
# Requires GNU sed (Linux/CI). On macOS install gnu-sed and use gsed, or run in a Linux container.
set -euo pipefail
IN="${1:?usage: $0 <evidence-pack> [output-dir]}"
OUT="${2:-ts18-redacted-pack}"
[ -d "$IN" ] || { echo "Missing input dir: $IN" >&2; exit 1; }
rm -rf "$OUT"
mkdir -p "$OUT"
# Use "$IN/." to include hidden files and handle directories that may be empty.
cp -R "$IN/." "$OUT"

redact_file() {
  local f="$1"
  [ -f "$f" ] || return 0
  # Use a temp file for portability across GNU sed versions; avoids -i '' vs -i '' differences.
  local tmp
  tmp="$(mktemp)"
  sed -E \
    -e 's/(serial(no)?|fingerprint|android_id|device_id)[^\n]*/\1=[REDACTED]/Ig' \
    -e 's/(([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})/[MAC_REDACTED]/g' \
    -e 's/(ssid|bssid|bluetooth_name)[^\n]*/\1=[REDACTED]/Ig' \
    -e 's#(/storage/emulated/0/)[^ \t\n]*#\1[REDACTED_PATH]#g' \
    "$f" > "$tmp" && mv "$tmp" "$f" || { rm -f "$tmp"; return 1; }
}

find "$OUT" -type f -name '*.txt' -print0 | while IFS= read -r -d '' file; do redact_file "$file"; done
find "$OUT" -type f -name '*.md' -print0 | while IFS= read -r -d '' file; do redact_file "$file"; done

echo "Redacted pack created: $OUT"
