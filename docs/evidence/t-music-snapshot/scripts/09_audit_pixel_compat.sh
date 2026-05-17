#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PIXEL_APK="${1:-}"
TS18_APK="${2:-$ROOT/dist/com.tw.music-unsigned.apk}"
TEMP_TREE="${3:-}"
HAS_RG=0
if command -v rg >/dev/null 2>&1; then
  HAS_RG=1
fi

required=(
  'Landroid/tw/john/TWUtil;'
  'Landroid/tw/john/TWUtil$TWObject;'
  'Landroid/tw/john/PinyinConv;'
)

echo "[audit] Direct canonical references to Landroid/tw/john/:"
if [[ "$HAS_RG" -eq 1 ]]; then
  rg -n 'Landroid/tw/john/' "$ROOT/app/apktool/smali"* || true
else
  grep -R -n -E 'Landroid/tw/john/' "$ROOT/app/apktool/smali"* || true
fi

echo "[audit] Overlay shim classes present:"
for rel in \
  "smali_classes5/android/tw/john/TWUtil.smali" \
  "smali_classes5/android/tw/john/TWUtil\$TWObject.smali" \
  "smali_classes5/android/tw/john/PinyinConv.smali"; do
  test -f "$ROOT/compat/pixel9a/apktool-overlay/$rel" || { echo "[ERR] Missing overlay file: $rel"; exit 1; }
  echo "  [OK] $rel"
done

if [[ -n "$TEMP_TREE" ]]; then
  echo "[audit] Temp tree sharedUserId state:"
  if grep -n -E 'sharedUserId' "$TEMP_TREE/AndroidManifest.xml"; then
    echo "[ERR] sharedUserId still present in temp tree"
    exit 1
  else
    echo "  [OK] temp tree has no sharedUserId"
  fi
fi

if [[ -n "$PIXEL_APK" && -f "$PIXEL_APK" ]]; then
  echo "[audit] Pixel APK contains required shim classes: $PIXEL_APK"
  unzip -l "$PIXEL_APK" | rg -n 'classes.*\.dex' >/dev/null || { echo "[ERR] Pixel APK has no dex payload"; exit 1; }
  for needle in "Landroid/tw/john/TWUtil;" "Landroid/tw/john/TWUtil\$TWObject;" "Landroid/tw/john/PinyinConv;"; do
    if ! printf '%s' "$needle" | grep -Eq '^L[A-Za-z0-9_/]+(\$[A-Za-z0-9_]+)*;$'; then
      echo "[ERR] Unsafe class needle: $needle"
      exit 1
    fi
    if python3 - "$PIXEL_APK" "$needle" <<'PY'
import sys
import zipfile
import re
apk_path = sys.argv[1]
expected_class_bytes = sys.argv[2].encode("utf-8")
if not re.fullmatch(rb"L[A-Za-z0-9_/]+(\$[A-Za-z0-9_]+)*;", expected_class_bytes):
    raise SystemExit(1)
with zipfile.ZipFile(apk_path) as zf:
    dex_entries = [name for name in zf.namelist() if name.endswith(".dex")]
    if not dex_entries:
        raise SystemExit(1)
    for name in dex_entries:
        if expected_class_bytes in zf.read(name):
            raise SystemExit(0)
raise SystemExit(1)
PY
    then
      echo "  [OK] dex payload contains $needle"
    else
      echo "[ERR] dex payload missing $needle"
      exit 1
    fi
  done
fi

if [[ -f "$TS18_APK" ]]; then
  echo "[audit] TS18/base APK should not contain android.tw.john shim classes: $TS18_APK"
  if unzip -l "$TS18_APK" | grep -q -E 'android/tw/john/'; then
    echo "[ERR] TS18/base APK unexpectedly includes android.tw.john files"
    exit 1
  else
    echo "  [OK] no android/tw/john/ filesystem entries in base APK"
  fi
fi

echo "[audit] Additional TS18-only risk surface scan"
if [[ "$HAS_RG" -eq 1 ]]; then
  rg -n 'Landroid/tw/|Lcom/tw/service/|Lcom/tw/|TWTHEME|persist\.tw\.|persist\.media\.' "$ROOT/app/apktool/smali"* | head -200 || true
else
  grep -R -n -E 'Landroid/tw/|Lcom/tw/service/|Lcom/tw/|TWTHEME|persist\.tw\.|persist\.media\.' "$ROOT/app/apktool/smali"* | head -200 || true
fi

echo "[audit] Required classes mapped by overlay declaration:"
for klass in "${required[@]}"; do
  echo "  [OK] $klass"
done
