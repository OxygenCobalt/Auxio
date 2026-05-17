#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=lib/common.sh
source "$ROOT/scripts/lib/common.sh"

: "${KEYSTORE:?Set KEYSTORE=/path/to/keystore.jks}"
: "${KEY_ALIAS:?Set KEY_ALIAS=alias}"
: "${KEYSTORE_PASSWORD:?Set KEYSTORE_PASSWORD=password}"
KEY_PASSWORD="${KEY_PASSWORD:-$KEYSTORE_PASSWORD}"

BUILD_TOOLS_DIR="${BUILD_TOOLS_DIR:-}"
[ -n "$BUILD_TOOLS_DIR" ] || BUILD_TOOLS_DIR=$(find_build_tools_dir_for_repo "$ROOT") || true
[ -n "$BUILD_TOOLS_DIR" ] || { echo "[ERR] Could not find Android build-tools dir"; exit 1; }

UNSIGNED="$ROOT/dist/com.tw.music-unsigned.apk"
ALIGNED="$ROOT/dist/com.tw.music-aligned.apk"

[ -f "$UNSIGNED" ] || { echo "[ERR] Unsigned APK missing: $UNSIGNED"; exit 1; }

"$BUILD_TOOLS_DIR/zipalign" -P 16 -f -v 4 "$UNSIGNED" "$ALIGNED"
"$BUILD_TOOLS_DIR/apksigner" sign \
  --ks "$KEYSTORE" \
  --ks-key-alias "$KEY_ALIAS" \
  --ks-pass "pass:$KEYSTORE_PASSWORD" \
  --key-pass "pass:$KEY_PASSWORD" \
  "$ALIGNED"
"$BUILD_TOOLS_DIR/apksigner" verify -v "$ALIGNED"
echo "[OK] Signed APK: $ALIGNED"
