#!/usr/bin/env bash
set -euo pipefail
LOG_FILE="${1:-pixel9a-validation.log}"
shift || true
APKS=("$@")
if [[ ${#APKS[@]} -eq 0 ]]; then
  mapfile -t APKS < <(ls -1 dist/com.tw.music-*.apk 2>/dev/null || true)
fi

{
  echo "=== Pixel validation harness $(date -u +%FT%TZ) ==="
  for apk in "${APKS[@]}"; do
    echo "\n=== APK: $apk ==="
    if [[ ! -f "$apk" ]]; then echo "missing file"; continue; fi
    adb logcat -c || true
    echo "--- install ---"
    adb install -r "$apk" || true
    echo "--- launcher resolve ---"
    adb shell cmd package resolve-activity --brief com.tw.music/.MainActivity || true
    echo "--- am start ---"
    adb shell am start -W -n com.tw.music/.MainActivity || true
    sleep 12
    echo "--- logcat snapshot ---"
    adb logcat -d -v threadtime | tail -500 || true
    echo "--- dumpsys package ---"
    adb shell dumpsys package com.tw.music || true
    echo "--- dropbox (system_app_crash) ---"
    adb shell dumpsys dropbox --print system_app_crash || true
    echo "--- uninstall ---"
    adb uninstall com.tw.music || true
  done
} | tee -a "$LOG_FILE"

echo "[OK] Harness log: $LOG_FILE"
