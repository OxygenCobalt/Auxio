#!/bin/sh
set -u

BASE_DIR="${1:-ts18-captures}"
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"
OUT_DIR="${BASE_DIR}/capture-${STAMP}"
mkdir -p "${OUT_DIR}"

SUMMARY="${OUT_DIR}/SUMMARY.txt"
echo "TS18 media-state capture" > "${SUMMARY}"
echo "Timestamp (UTC): ${STAMP}" >> "${SUMMARY}"
echo "Output dir: ${OUT_DIR}" >> "${SUMMARY}"
echo >> "${SUMMARY}"

run_cmd() {
  name="$1"
  shift
  out="${OUT_DIR}/${name}.txt"
  echo "== ${name} ==" >> "${SUMMARY}"
  if command -v timeout >/dev/null 2>&1; then
    timeout 20 "$@" >"${out}" 2>&1 || true
  else
    "$@" >"${out}" 2>&1 || true
  fi
  echo "Saved: ${out}" >> "${SUMMARY}"
}

if ! command -v adb >/dev/null 2>&1; then
  echo "adb is not available in PATH" >> "${SUMMARY}"
  exit 0
fi

run_cmd adb-devices adb devices
run_cmd getprop-fingerprint adb shell getprop ro.build.fingerprint
run_cmd getprop-manufacturer adb shell getprop ro.product.manufacturer
run_cmd getprop-model adb shell getprop ro.product.model
run_cmd getprop-device adb shell getprop ro.product.device
run_cmd getprop-release adb shell getprop ro.build.version.release
run_cmd getprop-sdk adb shell getprop ro.build.version.sdk

run_cmd media-session-list adb shell cmd media_session list-sessions

run_cmd dumpsys-appwidget-before adb shell dumpsys appwidget
run_cmd dumpsys-shortcut-before adb shell dumpsys shortcut
run_cmd cmd-shortcut-before adb shell cmd shortcut dump
run_cmd am-start-shuffle adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.SHUFFLE_ALL
run_cmd am-start-now-playing adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.OPEN_NOW_PLAYING
run_cmd am-start-queue adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.OPEN_QUEUE
run_cmd dumpsys-media-session-before adb shell dumpsys media_session
run_cmd dumpsys-audio-before adb shell dumpsys audio
run_cmd dumpsys-notification-before adb shell dumpsys notification --noredact
run_cmd dumpsys-services adb shell dumpsys activity services
if [ -f "${OUT_DIR}/dumpsys-services.txt" ]; then
  grep -i auxio "${OUT_DIR}/dumpsys-services.txt" > "${OUT_DIR}/dumpsys-services-auxio.txt" 2>/dev/null || true
  if [ ! -s "${OUT_DIR}/dumpsys-services-auxio.txt" ]; then
    : > "${OUT_DIR}/dumpsys-services-auxio.txt"
  fi
  echo "Saved: ${OUT_DIR}/dumpsys-services-auxio.txt" >> "${SUMMARY}"
fi

run_cmd keyevent-play-pause adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE
run_cmd keyevent-next adb shell input keyevent KEYCODE_MEDIA_NEXT
run_cmd keyevent-previous adb shell input keyevent KEYCODE_MEDIA_PREVIOUS

run_cmd dumpsys-media-session-after adb shell dumpsys media_session
run_cmd dumpsys-audio-after adb shell dumpsys audio
run_cmd dumpsys-notification-after adb shell dumpsys notification --noredact

run_cmd dumpsys-appwidget-after adb shell dumpsys appwidget
run_cmd dumpsys-shortcut-after adb shell dumpsys shortcut
run_cmd cmd-shortcut-after adb shell cmd shortcut dump

echo >> "${SUMMARY}"
echo "Done." >> "${SUMMARY}"
echo "Capture written to ${OUT_DIR}"
