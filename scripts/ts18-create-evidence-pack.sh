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
SCRIPT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
SCENARIO_MAP="${SCRIPT_DIR}/../docs/templates/TS18_VALIDATION_SCENARIO_MAP.json"

require_value() {
  local opt="$1"
  local value="${2:-}"
  if [ -z "$value" ] || [[ "$value" == -* ]]; then
    echo "Missing or invalid value for ${opt}" >&2
    usage
    exit 1
  fi
}

reject_malformed_value() {
  local opt="$1"
  local value="$2"
  if [[ "$value" == *$'\n'* ]] || [[ "$value" == *$'\r'* ]]; then
    echo "Malformed value for ${opt}: newlines are not allowed" >&2
    usage
    exit 1
  fi
  if [ "$opt" = "--device-label" ]; then
    if [[ ! "$value" =~ ^[A-Za-z0-9_-]+$ ]]; then
      echo "Malformed value for ${opt}: only letters, numbers, '_' and '-' are allowed" >&2
      usage
      exit 1
    fi
  fi
}

while [ $# -gt 0 ]; do
  case "$1" in
    --base-dir)
      require_value "$1" "${2:-}"
      reject_malformed_value "$1" "$2"
      BASE_DIR="$2"
      shift 2
      ;;
    --device-label)
      require_value "$1" "${2:-}"
      reject_malformed_value "$1" "$2"
      DEVICE_LABEL="$2"
      shift 2
      ;;
    --firmware-label)
      require_value "$1" "${2:-}"
      reject_malformed_value "$1" "$2"
      FIRMWARE_LABEL="$2"
      shift 2
      ;;
    --auxio-build)
      require_value "$1" "${2:-}"
      reject_malformed_value "$1" "$2"
      AUXIO_BUILD="$2"
      shift 2
      ;;
    --include-ecosystem-context) INCLUDE_ECOSYSTEM_CONTEXT="true"; shift ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown argument: $1" >&2; usage; exit 1 ;;
  esac
done

STAMP_DATE="$(date -u +%F)"
STAMP_FULL="$(date -u +%Y%m%dT%H%M%SZ)"
PACK_DIR="${BASE_DIR}/${STAMP_DATE}-${DEVICE_LABEL}"
python3 - "$BASE_DIR" "$PACK_DIR" <<'PY'
import sys
from pathlib import Path

base = Path(sys.argv[1]).resolve()
pack = Path(sys.argv[2]).resolve()
if base != pack and base not in pack.parents:
    print(f"Pack directory escapes base dir: {pack} (base: {base})", file=sys.stderr)
    raise SystemExit(1)
PY
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

DEVICE_LABEL="$DEVICE_LABEL" \
FIRMWARE_LABEL="$FIRMWARE_LABEL" \
AUXIO_BUILD="$AUXIO_BUILD" \
STAMP_FULL="$STAMP_FULL" \
INCLUDE_ECOSYSTEM_CONTEXT="$INCLUDE_ECOSYSTEM_CONTEXT" \
python3 - "$SCENARIO_MAP" "$PACK_DIR/evidence-manifest.json" <<'PY'
import json
import os
import sys
from pathlib import Path

map_file = Path(sys.argv[1]).resolve()
output = Path(sys.argv[2])
expected = [f"TS18-STD-{i:03d}" for i in range(1, 18)]
scenarios = [item.get("id") for item in json.loads(map_file.read_text()).get("scenarios", [])]
if len(set(scenarios)) != len(scenarios) or sorted(scenarios) != expected:
    print("Scenario map must contain unique IDs TS18-STD-001..017", file=sys.stderr)
    raise SystemExit(1)

manifest = {
    "manifestVersion": "1.0.0",
    "deviceLabel": os.environ["DEVICE_LABEL"],
    "deviceFingerprint": "redacted-or-omitted",
    "firmwareThemeLabel": os.environ["FIRMWARE_LABEL"],
    "auxioBuild": os.environ["AUXIO_BUILD"],
    "captureTimestampUtc": os.environ["STAMP_FULL"],
    "captureScript": "scripts/ts18-create-evidence-pack.sh",
    "includeEcosystemContext": os.environ["INCLUDE_ECOSYSTEM_CONTEXT"] == "true",
    "scenarioIdsAttempted": scenarios,
    "redactionStatus": "raw-unredacted",
    "knownLimitations": [],
    "evidenceFiles": [],
}
output.write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
PY

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
