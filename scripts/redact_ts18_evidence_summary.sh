#!/usr/bin/env sh
set -eu
IN="${1:?usage: $0 <evidence-pack> [output-dir]}"
OUT="${2:-ts18-redacted-pack}"
[ -d "$IN" ] || { echo "Missing input dir: $IN" >&2; exit 1; }
rm -rf "$OUT"
mkdir -p "$OUT"
cp -R "$IN/." "$OUT"

python3 - "$OUT" <<'PY'
import re
import sys
from pathlib import Path

out = Path(sys.argv[1])
text_file_suffixes = {".txt", ".md"}

def redact(content: str) -> str:
    content = re.sub(r"(?i)(serial(?:no)?|fingerprint|android_id|device_id)[^\n]*", r"\1=[REDACTED]", content)
    content = re.sub(
        r"(?i)\b([a-z0-9_.-]*(?:token|id|hash|uuid|session)[a-z0-9_.-]*)\s*[:=]\s*[0-9A-Fa-f]{16,}\b",
        r"\1=[HEX_OR_ID_REDACTED]",
        content,
    )
    content = re.sub(r"(?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}", "[MAC_REDACTED]", content)
    content = re.sub(r"(?i)(ssid|bssid|bluetooth_name)[^\n]*", r"\1=[REDACTED]", content)
    content = re.sub(r"/storage/emulated/0/[^\n]*", "/storage/emulated/0/[REDACTED_PATH]", content)
    return content

for file in out.rglob("*"):
    if not file.is_file() or file.suffix.lower() not in text_file_suffixes:
        continue
    original = file.read_text(encoding="utf-8", errors="ignore")
    updated = redact(original)
    if updated != original:
        file.write_text(updated, encoding="utf-8")
PY
echo "Redacted pack created: $OUT"
