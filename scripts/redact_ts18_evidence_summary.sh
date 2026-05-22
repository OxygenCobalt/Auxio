#!/usr/bin/env bash
set -euo pipefail
IN="${1:?usage: $0 <evidence-pack> [output-dir]}"
OUT="${2:-ts18-redacted-pack}"
[ -d "$IN" ] || { echo "Missing input dir: $IN" >&2; exit 1; }
[ -n "$OUT" ] || { echo "OUT must not be empty" >&2; exit 1; }
[ "$OUT" != "/" ] || { echo "OUT must not be /" >&2; exit 1; }
[ "$OUT" != "." ] || { echo "OUT must not be ." >&2; exit 1; }
[ "$OUT" != "$IN" ] || { echo "OUT must differ from IN" >&2; exit 1; }
python3 - "$IN" "$OUT" <<'PY'
import pathlib, sys
in_path = pathlib.Path(sys.argv[1]).resolve()
out_path = pathlib.Path(sys.argv[2]).resolve()
if in_path == out_path:
    raise SystemExit("OUT must differ from IN")
if in_path in out_path.parents:
    raise SystemExit("OUT must not be inside IN")
if out_path in in_path.parents:
    raise SystemExit("OUT must not be a parent of IN")
PY
rm -rf -- "$OUT"
mkdir -p -- "$OUT"
cp -a -- "$IN/." "$OUT/"

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
    -e 's#(/storage/emulated/0/)[^\r\n]*#\1[REDACTED_PATH]#g' \
    "$f" > "$tmp" && mv "$tmp" "$f" || { rm -f "$tmp"; return 1; }
}

find "$OUT" -type f -name '*.txt' -print0 | while IFS= read -r -d '' file; do redact_file "$file"; done
find "$OUT" -type f -name '*.md' -print0 | while IFS= read -r -d '' file; do redact_file "$file"; done

echo "Redacted pack created: $OUT"
