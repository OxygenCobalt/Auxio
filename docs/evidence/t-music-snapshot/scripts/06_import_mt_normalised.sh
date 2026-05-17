#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MT_APK="${1:-}"
[ -n "$MT_APK" ] && [ -f "$MT_APK" ] || {
  echo "Usage: $0 /path/to/mt-normalised.apk"
  exit 1
}
APKTOOL_JAR="${APKTOOL_JAR:-$ROOT/.tools/apktool.jar}"
JADX_BIN="${JADX_BIN:-$ROOT/.tools/jadx/bin/jadx}"
LOG="$ROOT/docs/reports/jadx-mt.log"
mkdir -p "$ROOT/reference/jadx-mt" "$ROOT/mappings/mt" \
         "$ROOT/.input/apktool-mt" "$ROOT/docs/reports"

# Decode MT APK for smali comparison (failures are non-fatal)
export TMPDIR="${TMPDIR:-$ROOT/.tmp}"
java -jar "$APKTOOL_JAR" d -f -o "$ROOT/.input/apktool-mt" "$MT_APK" \
  >/dev/null 2>&1 || echo "[WARN] Apktool decode of MT APK returned non-zero; continuing" >&2

JADX_HELP="$("$JADX_BIN" --help 2>&1 || true)"
CMD=("$JADX_BIN" --deobf --show-bad-code -d "$ROOT/reference/jadx-mt")
if printf '%s' "$JADX_HELP" | grep -q -- '--deobf-cfg-file'; then
  CMD+=(--deobf-cfg-file "$ROOT/mappings/mt/$(basename "${MT_APK%.apk}").jobf")
  printf '%s' "$JADX_HELP" | grep -q -- '--deobf-cfg-file-mode' \
    && CMD+=(--deobf-cfg-file-mode read-or-save)
fi
CMD+=("$MT_APK")

rm -rf "$ROOT/reference/jadx-mt"/*
rc=0
"${CMD[@]}" >"$LOG" 2>&1 || rc=$?

count=$(find "$ROOT/reference/jadx-mt" -type f \
  \( -name '*.java' -o -name '*.json' -o -name '*.xml' \) 2>/dev/null | wc -l | tr -d ' ')

if [ "$rc" -ne 0 ]; then
  if [ "${count:-0}" -gt 0 ]; then
    echo "[WARN] JADX MT export returned $rc but produced $count files; treating as success." >&2
    exit 0
  fi
  echo "[ERR ] JADX MT export failed and produced no output files." >&2
  sed -n '1,60p' "$LOG" >&2 || true
  exit "$rc"
fi
echo "[OK] JADX MT export: $count files"
