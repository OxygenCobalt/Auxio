#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage:
  OUT_DIR=<path> PACKAGE=<app.package> SCENARIO=<name> [WAIT_SECONDS=<n>] \
    bash scripts/capture-ui-screenshots.sh

Environment variables:
  OUT_DIR         Output directory for PNG and diagnostics. Default: artifacts/ui-screenshots
  PACKAGE         Android package name to launch. Default: org.oxycblt.auxio.debug
  SCENARIO        all | playback_landscape | queue_landscape | home_landscape (default: all)
  WAIT_SECONDS    Delay between launch/actions and screencap. Default: 3
  MAIN_ACTIVITY   Optional main activity class name. Default: org.oxycblt.auxio.MainActivity
USAGE
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

OUT_DIR="${OUT_DIR:-artifacts/ui-screenshots}"
PACKAGE="${PACKAGE:-org.oxycblt.auxio.debug}"
SCENARIO="${SCENARIO:-all}"
WAIT_SECONDS="${WAIT_SECONDS:-3}"
MAIN_ACTIVITY="${MAIN_ACTIVITY:-org.oxycblt.auxio.MainActivity}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "ERROR: Missing required command: $1" >&2
    exit 2
  fi
}

capture_png() {
  local name="$1"
  local device_file="/sdcard/${name}.png"
  adb shell screencap -p "$device_file"
  adb pull "$device_file" "${OUT_DIR}/${name}.png" >/dev/null
  adb shell rm -f "$device_file" >/dev/null
}

prepare_device() {
  adb wait-for-device
  if ! adb get-state >/dev/null 2>&1; then
    echo "ERROR: No adb device available" >&2
    exit 3
  fi

  adb shell settings put system accelerometer_rotation 0 || true
  adb shell settings put system user_rotation 1 || true
  adb shell wm size 1280x720 || true
  adb shell wm density 200 || true
  adb shell input keyevent KEYCODE_WAKEUP || true
  adb shell input keyevent KEYCODE_MENU || true
}

write_metadata() {
  mkdir -p "$OUT_DIR"
  {
    echo "timestamp_utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    echo "scenario=${SCENARIO}"
    echo "package=${PACKAGE}"
    echo "wait_seconds=${WAIT_SECONDS}"
    echo "device_state=$(adb get-state 2>/dev/null || true)"
    echo "wm_size=$(adb shell wm size 2>/dev/null | tr -d '\r' || true)"
    echo "wm_density=$(adb shell wm density 2>/dev/null | tr -d '\r' || true)"
    echo "display_rotation=$(adb shell dumpsys input 2>/dev/null | awk -F': ' '/SurfaceOrientation/ {print $2; exit}' || true)"
  } > "${OUT_DIR}/capture-metadata.txt"
}

launch_home() {
  adb shell am force-stop "$PACKAGE" || true
  adb shell monkey -p "$PACKAGE" -c android.intent.category.LAUNCHER 1 >/dev/null
  sleep "$WAIT_SECONDS"
}

capture_logs() {
  adb logcat -d > "${OUT_DIR}/logcat.txt"
  adb shell dumpsys window > "${OUT_DIR}/dumpsys-window.txt"
  adb shell dumpsys activity activities > "${OUT_DIR}/dumpsys-activity.txt"
  adb shell dumpsys activity top > "${OUT_DIR}/dumpsys-activity-top.txt"
  adb shell dumpsys display > "${OUT_DIR}/dumpsys-display.txt"
  adb shell dumpsys package "$PACKAGE" > "${OUT_DIR}/dumpsys-package.txt" || true
}

run_scenario() {
  case "$SCENARIO" in
    all)
      launch_home
      capture_png "01-home-landscape"
      adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE || true
      sleep "$WAIT_SECONDS"
      capture_png "02-playback-landscape"
      adb shell am start -n "${PACKAGE}/${MAIN_ACTIVITY}" >/dev/null 2>&1 || true
      sleep "$WAIT_SECONDS"
      capture_png "03-queue-landscape"
      ;;
    home_landscape)
      launch_home
      capture_png "home-landscape"
      ;;
    playback_landscape)
      launch_home
      adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE || true
      sleep "$WAIT_SECONDS"
      capture_png "playback-landscape"
      ;;
    queue_landscape)
      launch_home
      adb shell am start -n "${PACKAGE}/${MAIN_ACTIVITY}" >/dev/null 2>&1 || true
      sleep "$WAIT_SECONDS"
      capture_png "queue-landscape"
      ;;
    *)
      echo "ERROR: Unsupported scenario '$SCENARIO'. Supported: all, playback_landscape, queue_landscape, home_landscape" >&2
      usage
      exit 4
      ;;
  esac
}

cleanup() {
  echo "Restoring device display settings..."
  adb shell wm size reset || true
  adb shell wm density reset || true
  adb shell settings put system accelerometer_rotation 1 || true
}

main() {
  require_cmd adb
  prepare_device
  trap cleanup EXIT
  write_metadata
  adb logcat -c || true
  run_scenario
  capture_logs

  local count
  count=$(find "$OUT_DIR" -maxdepth 1 -name '*.png' | wc -l | tr -d ' ')
  if [[ "$count" -eq 0 ]]; then
    echo "ERROR: No screenshots were produced in $OUT_DIR" >&2
    exit 5
  fi

  echo "Captured ${count} screenshot(s) and diagnostics under ${OUT_DIR}"
}

main "$@"
