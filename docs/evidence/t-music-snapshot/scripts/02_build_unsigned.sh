#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck source=lib/common.sh
source "$ROOT/scripts/lib/common.sh"

APKTOOL_JAR="${APKTOOL_JAR:-$ROOT/.tools/apktool.jar}"
AAPT2_BIN="${AAPT2:-}"
[ -n "$AAPT2_BIN" ] || AAPT2_BIN=$(find_aapt2_for_repo "$ROOT") || true

[ -f "$APKTOOL_JAR" ] || {
  echo "[ERR] Missing apktool jar: $APKTOOL_JAR"
  echo "      Run scripts/00_bootstrap_tools.sh to re-download."
  exit 1
}
[ -n "$AAPT2_BIN" ] && [ -x "$AAPT2_BIN" ] || {
  echo "[ERR] No executable external aapt2 found."
  echo "      See docs/manual-steps/01-android-sdk-build-tools.md"
  exit 2
}

mkdir -p "$ROOT/dist" "$ROOT/.tmp"
export TMPDIR="$ROOT/.tmp"
rm -f "$ROOT/dist/com.tw.music-unsigned.apk"

# Copy aapt2 to a writable location so apktool can chmod it (the SDK dir on
# CI runners is often owned by root and apktool calls File.setExecutable()).
AAPT2_TMP="$ROOT/.tmp/aapt2"
cp "$AAPT2_BIN" "$AAPT2_TMP"
chmod +x "$AAPT2_TMP"
AAPT2_BIN="$AAPT2_TMP"

java -Xms256m -Xmx2048m \
  -jar "$APKTOOL_JAR" build \
  --aapt "$AAPT2_BIN" \
  "$ROOT/app/apktool" \
  -o "$ROOT/dist/com.tw.music-unsigned.apk"

[ -f "$ROOT/dist/com.tw.music-unsigned.apk" ] || {
  echo "[ERR] apktool exited 0 but APK not found: $ROOT/dist/com.tw.music-unsigned.apk"
  exit 1
}

echo "[OK] Unsigned APK: $ROOT/dist/com.tw.music-unsigned.apk"
