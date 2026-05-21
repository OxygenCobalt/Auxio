#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<USAGE
Usage: $0 [--base-dir <dir>] [--device-label <label>] [--firmware-label <label>] [--auxio-build <sha>] [--include-ecosystem-context]
USAGE
}

BASE_DIR="docs/evidence/ts18"
DEVICE_LABEL="ts18-device"
FIRMWARE_LABEL="unknown"
AUXIO_BUILD="unknown"
INCLUDE_ECOSYSTEM_CONTEXT="false"

while [ $# -gt 0 ]; do
  case "$1" in
    --base-dir) BASE_DIR="$2"; shift 2 ;;
    --device-label) DEVICE_LABEL="$2"; shift 2 ;;
    --firmware-label) FIRMWARE_LABEL="$2"; shift 2 ;;
    --auxio-build) AUXIO_BUILD="$2"; shift 2 ;;
    --include-ecosystem-context) INCLUDE_ECOSYSTEM_CONTEXT="true"; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; usage; exit 1 ;;
  esac
done

STAMP_DATE="$(date -u +%F)"
STAMP_FULL="$(date -u +%Y%m%dT%H%M%SZ)"
PACK_DIR="${BASE_DIR}/${STAMP_DATE}-${DEVICE_LABEL}"
RAW_DIR="${PACK_DIR}/raw"
DERIVED_DIR="${PACK_DIR}/derived"
CMD_LOG="${RAW_DIR}/commands.log"
mkdir -p "$RAW_DIR/screenshots" "$DERIVED_DIR"

cat > "${PACK_DIR}/README.md" <<README
# TS18 evidence pack

- Device label: ${DEVICE_LABEL}
- Firmware/theme label: ${FIRMWARE_LABEL}
- Auxio build: ${AUXIO_BUILD}
- Captured at (UTC): ${STAMP_FULL}
- Capture script: scripts/ts18-create-evidence-pack.sh

This pack is Tier 2 validation evidence only.
README

json_array='["TS18-STD-001","TS18-STD-002","TS18-STD-003","TS18-STD-004","TS18-STD-005","TS18-STD-006","TS18-STD-007","TS18-STD-008","TS18-STD-009","TS18-STD-010","TS18-STD-011","TS18-STD-012","TS18-STD-013","TS18-STD-014","TS18-STD-015","TS18-STD-016","TS18-STD-017"]'

cat > "${PACK_DIR}/evidence-manifest.json" <<MANIFEST
{
  "manifestVersion": "1.0.0",
  "deviceLabel": "${DEVICE_LABEL}",
  "deviceFingerprint": "redacted-or-omitted",
  "firmwareThemeLabel": "${FIRMWARE_LABEL}",
  "auxioBuild": "${AUXIO_BUILD}",
  "captureTimestampUtc": "${STAMP_FULL}",
  "captureScript": "scripts/ts18-create-evidence-pack.sh",
  "includeEcosystemContext": ${INCLUDE_ECOSYSTEM_CONTEXT},
  "scenarioIdsAttempted": ${json_array},
  "redactionStatus": "raw-unredacted",
  "knownLimitations": [],
  "evidenceFiles": []
}
MANIFEST

cat > "${RAW_DIR}/screenshots/README.md" <<SS
Place manual screenshots/videos outside git when large; keep only redacted references here.
SS

run_capture() {
  local name="$1"; shift
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] $*" >> "$CMD_LOG"
  if command -v timeout >/dev/null 2>&1; then
    timeout 25 "$@" > "${RAW_DIR}/${name}.txt" 2>&1 || true
  else
    "$@" > "${RAW_DIR}/${name}.txt" 2>&1 || true
  fi
}

if ! command -v adb >/dev/null 2>&1; then
  echo "adb not found; created scaffold only" > "${RAW_DIR}/capture-status.txt"
  exit 0
fi

run_capture media_session adb shell dumpsys media_session
run_capture notification adb shell dumpsys notification --noredact
run_capture audio adb shell dumpsys audio
run_capture appwidget adb shell dumpsys appwidget
run_capture shortcut adb shell dumpsys shortcut
run_capture activity adb shell dumpsys activity activities
run_capture device_profile adb shell getprop

run_capture action_open_now_playing adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.OPEN_NOW_PLAYING
run_capture action_open_queue adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.OPEN_QUEUE
run_capture key_play_pause adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE
run_capture key_next adb shell input keyevent KEYCODE_MEDIA_NEXT
run_capture key_previous adb shell input keyevent KEYCODE_MEDIA_PREVIOUS

if [ "$INCLUDE_ECOSYSTEM_CONTEXT" = "true" ]; then
  run_capture packages_redacted adb shell pm list packages
fi

echo "Evidence pack created at: $PACK_DIR"
