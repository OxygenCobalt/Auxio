#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
APK_PATH="${1:-}"
[ -n "$APK_PATH" ] && [ -f "$APK_PATH" ] || {
  echo "Usage: $0 /path/to/source.apk"
  exit 1
}
JADX_BIN="${JADX_BIN:-$ROOT/.tools/jadx/bin/jadx}"
JOBF="$ROOT/mappings/jadx/$(basename "${APK_PATH%.apk}").jobf"
LOG="$ROOT/docs/reports/jadx-aliased.log"
mkdir -p "$ROOT/reference/jadx-aliased" "$ROOT/mappings/jadx" \
         "$ROOT/mappings/manual-enigma" "$ROOT/docs/reports"

JADX_HELP="$("$JADX_BIN" --help 2>&1 || true)"
CMD=("$JADX_BIN" --deobf --show-bad-code -d "$ROOT/reference/jadx-aliased")
if printf '%s' "$JADX_HELP" | grep -q -- '--mappings-path'; then
  CMD+=(--mappings-path "$ROOT/mappings/manual-enigma")
  printf '%s' "$JADX_HELP" | grep -q -- '--mappings-mode' \
    && CMD+=(--mappings-mode read)
fi
if printf '%s' "$JADX_HELP" | grep -q -- '--deobf-cfg-file'; then
  CMD+=(--deobf-cfg-file "$JOBF")
  printf '%s' "$JADX_HELP" | grep -q -- '--deobf-cfg-file-mode' \
    && CMD+=(--deobf-cfg-file-mode read-or-save)
fi
CMD+=("$APK_PATH")

rm -rf "$ROOT/reference/jadx-aliased"/*
rc=0
"${CMD[@]}" >"$LOG" 2>&1 || rc=$?

count=$(find "$ROOT/reference/jadx-aliased" -type f \
  \( -name '*.java' -o -name '*.json' -o -name '*.xml' \) 2>/dev/null | wc -l | tr -d ' ')

if [ "$rc" -ne 0 ]; then
  if [ "${count:-0}" -gt 0 ]; then
    echo "[WARN] JADX aliased returned $rc but produced $count files; treating as success." >&2
    exit 0
  fi
  echo "[ERR ] JADX aliased export failed and produced no output files." >&2
  sed -n '1,60p' "$LOG" >&2 || true
  exit "$rc"
fi
echo "[OK] JADX aliased export: $count files"
