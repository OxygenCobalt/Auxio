#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
source "$ROOT/scripts/lib/common.sh"
APKTOOL_JAR="${APKTOOL_JAR:-$ROOT/.tools/apktool.jar}"
AAPT2_BIN="${AAPT2:-}"; [ -n "$AAPT2_BIN" ] || AAPT2_BIN=$(find_aapt2_for_repo "$ROOT") || true

[ -f "$APKTOOL_JAR" ] || { echo "[ERR] Missing apktool jar"; exit 1; }
[ -n "$AAPT2_BIN" ] && [ -x "$AAPT2_BIN" ] || { echo "[ERR] Missing executable aapt2"; exit 2; }

mkdir -p "$ROOT/dist" "$ROOT/.tmp"
AAPT2_TMP="$ROOT/.tmp/aapt2-pixel"
cp "$AAPT2_BIN" "$AAPT2_TMP" && chmod +x "$AAPT2_TMP"

PIXEL_TREE="$ROOT/.tmp/apktool-pixel9a-compat"
rm -rf "$PIXEL_TREE"
cp -r "$ROOT/app/apktool" "$PIXEL_TREE"

sed -i 's/[[:space:]]*android:sharedUserId="[^"]*"//g' "$PIXEL_TREE/AndroidManifest.xml"
if grep -q 'sharedUserId' "$PIXEL_TREE/AndroidManifest.xml"; then
  echo "[ERR] sharedUserId still present in temp manifest"; exit 1
fi

cp -r "$ROOT/compat/pixel9a/apktool-overlay/." "$PIXEL_TREE/"

UNSIGNED_OUT="$ROOT/dist/com.tw.music-pixel9a-compat-no-uid-unsigned.apk"
rm -f "$UNSIGNED_OUT"
java -Xms256m -Xmx2048m -jar "$APKTOOL_JAR" build --aapt "$AAPT2_TMP" "$PIXEL_TREE" -o "$UNSIGNED_OUT"
[ -f "$UNSIGNED_OUT" ] || { echo "[ERR] Pixel compat unsigned APK missing"; exit 1; }

bash "$ROOT/scripts/09_audit_pixel_compat.sh" "$UNSIGNED_OUT" "$ROOT/dist/com.tw.music-unsigned.apk" "$PIXEL_TREE"

if command -v aapt >/dev/null 2>&1; then
  aapt dump badging "$UNSIGNED_OUT" > "$ROOT/dist/com.tw.music-pixel9a-compat.badging.txt"
fi
sha256sum "$UNSIGNED_OUT" > "$UNSIGNED_OUT.sha256"

SIGNED_OUT=""
if [[ -n "${KEYSTORE:-}" && -n "${KS_ALIAS:-}" && -n "${KS_PASS:-}" ]]; then
  BT_DIR=$(dirname "$(command -v apksigner || true)")
  ZIPALIGN="${BT_DIR}/zipalign"
  APKSIGNER="${BT_DIR}/apksigner"
  if [[ -x "$ZIPALIGN" && -x "$APKSIGNER" ]]; then
    ALIGNED="$ROOT/.tmp/pixel9a-aligned.apk"
    SIGNED_OUT="$ROOT/dist/com.tw.music-pixel9a-compat-no-uid-signed.apk"
    "$ZIPALIGN" -P 16 -f -v 4 "$UNSIGNED_OUT" "$ALIGNED"
    "$APKSIGNER" sign --ks "$KEYSTORE" --ks-key-alias "$KS_ALIAS" --ks-pass "pass:${KS_PASS}" --key-pass "pass:${KS_PASS}" --out "$SIGNED_OUT" "$ALIGNED"
    "$APKSIGNER" verify -v "$SIGNED_OUT"
    sha256sum "$SIGNED_OUT" > "$SIGNED_OUT.sha256"
  else
    echo "[WARN] apksigner/zipalign not available; signed Pixel artifact skipped"
  fi
else
  echo "[INFO] Signing env not set (KEYSTORE/KS_ALIAS/KS_PASS); unsigned Pixel artifact produced"
fi

echo "[OK] Pixel compat temp tree: $PIXEL_TREE"
echo "[OK] Pixel unsigned APK: $UNSIGNED_OUT"
[[ -n "$SIGNED_OUT" ]] && echo "[OK] Pixel signed APK: $SIGNED_OUT"
