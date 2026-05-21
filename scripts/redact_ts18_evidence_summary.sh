#!/usr/bin/env bash
set -euo pipefail
IN="${1:?usage: $0 <evidence-pack> [output-dir]}"
OUT="${2:-ts18-redacted-pack}"
[ -d "$IN" ] || { echo "Missing input dir: $IN" >&2; exit 1; }
rm -rf "$OUT"
mkdir -p "$OUT"
cp -R "$IN"/* "$OUT" 2>/dev/null || true

redact_file() {
  local f="$1"
  [ -f "$f" ] || return 0
  sed -E -i \
    -e 's/(serial(no)?|fingerprint|android_id|device_id)[^\n]*/\1=[REDACTED]/Ig' \
    -e 's/(([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})/[MAC_REDACTED]/g' \
    -e 's/(ssid|bssid|bluetooth_name)[^\n]*/\1=[REDACTED]/Ig' \
    -e 's#/storage/emulated/0/[^ ]+#/storage/emulated/0/[REDACTED_PATH]#g' \
    "$f"
}

while IFS= read -r -d '' file; do redact_file "$file"; done < <(find "$OUT" -type f -name '*.txt' -print0)
while IFS= read -r -d '' file; do redact_file "$file"; done < <(find "$OUT" -type f -name '*.md' -print0)

echo "Redacted pack created: $OUT"
