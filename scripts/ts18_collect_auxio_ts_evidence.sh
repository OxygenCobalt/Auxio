#!/system/bin/sh
# TS18/Auxio-TS evidence collector.
# Runs with ordinary adb shell or local terminal shell. Root is not required.
# Usage:
#   AUXIO_TS_PACKAGE=org.oxycblt.auxio sh scripts/ts18_collect_auxio_ts_evidence.sh auxio-ts-playing
#   AUXIO_TS_PACKAGE=com.tw.music sh scripts/ts18_collect_auxio_ts_evidence.sh stock-music-playing

set -u

SCENARIO="${1:-unspecified}"
PKG="${AUXIO_TS_PACKAGE:-org.oxycblt.auxio}"
STAMP="$(date +%Y%m%d_%H%M%S 2>/dev/null || echo unknown_time)"
OUT_BASE="${TS18_EVIDENCE_DIR:-/sdcard/Download}"
OUT="$OUT_BASE/auxio-ts-evidence_${STAMP}_${SCENARIO}"
mkdir -p "$OUT" 2>/dev/null || OUT="/sdcard/auxio-ts-evidence_${STAMP}_${SCENARIO}"
mkdir -p "$OUT" 2>/dev/null || exit 1

if command -v timeout >/dev/null 2>&1; then
  RUN_TIMEOUT_CMD="timeout 45"
else
  RUN_TIMEOUT_CMD=""
fi

run() {
  name="$1"; shift
  {
    echo "###############################################################################"
    echo "# $name"
    echo "###############################################################################"
    echo "Date: $(date 2>/dev/null)"
    echo "Scenario: $SCENARIO"
    echo "Package: $PKG"
    echo "Command: $*"
    echo
    if [ -n "$RUN_TIMEOUT_CMD" ]; then
      $RUN_TIMEOUT_CMD "$@" 2>&1
    else
      "$@" 2>&1
    fi
    echo
    echo "Exit code: $?"
  } > "$OUT/$name.txt" 2>&1
}

run shell_identity id
run uname uname -a

run selected_props sh -c "getprop | grep -Ei 'ro.build|ro.product|ro.hardware|ro.board|ro.boot|persist.tw|persist.sys.tw|persist.sys.theme|persist.phone_connect_app|zlink|tlink|media|audio|bluetooth|usb|adb|camera|reverse|can|spdif|dsp|ivi' | sort"
run package_path sh -c "pm path '$PKG'; pm path com.tw.music; pm path com.tw.service; pm path com.tw.service.xt; pm path com.tw.radio; pm path com.zjinnova.zlink; pm path com.google.android.projection.gearhead"
run package_list_relevant sh -c "pm list packages -f -U 2>/dev/null | grep -Ei 'auxio|oxycblt|tw|dofun|zlink|tlink|gearhead|spotify|audioplayer|music|radio|bt|bluetooth|theme|launcher|car' | sort"
run media_session sh -c "dumpsys media_session 2>&1; echo; cmd media_session list-sessions 2>&1"
run audio sh -c "dumpsys audio 2>&1"
run notification_filtered sh -c "dumpsys notification 2>&1 | grep -Ei '$PKG|auxio|music|media|session|notification|playback|transport|zlink|tw' | head -300"
run activity_filtered sh -c "dumpsys activity services 2>&1 | grep -Ei '$PKG|auxio|music|media|session|tw|zlink|tlink|radio|eq|launcher' | head -300"
run input_devices sh -c "cat /proc/bus/input/devices 2>&1; echo; find /system/usr/keylayout /vendor/usr/keylayout -type f 2>/dev/null | while read f; do echo '###' \$f; grep -Ei 'MEDIA|MUSIC|VOLUME|PLAY|PAUSE|NEXT|PREVIOUS|RADIO|BT|CALL|VOICE|HEADSET|STEER|WHEEL|TW' \$f 2>/dev/null; done"
run logcat_filtered sh -c "logcat -d -v threadtime 2>&1 | grep -Ei '$PKG|auxio|com.tw.music|com.tw.service|com.tw.service.xt|com.tw.radio|TWService|MediaSession|AudioFocus|AUDIO_BECOMING_NOISY|MEDIA_BUTTON|KeyEvent|ZLink|TLink|gearhead|launcher|theme|MusicTheme|Bluetooth|AVRCP|radio|navigation|navi' | tail -1000"
run ps_relevant sh -c "ps -A 2>/dev/null | grep -Ei '$PKG|auxio|tw|zlink|tlink|gearhead|music|radio|bt|bluetooth|launcher|theme' | sort"
run storage_music_paths sh -c "find /sdcard /storage -maxdepth 4 -type f \( -iname '*.mp3' -o -iname '*.flac' -o -iname '*.m4a' -o -iname '*.ogg' -o -iname '*.opus' -o -iname '*.wav' \) 2>/dev/null | head -300"

cat > "$OUT/README.txt" <<EOF
Auxio-TS evidence bundle
Scenario: $SCENARIO
Package: $PKG
Created: $STAMP

Manual fields to fill before sharing:
- Was playback active? yes/no
- File type: mp3/flac/other
- Did notification controls appear? yes/no
- Did TS18 launcher/home widget update? yes/no
- Did steering wheel/media keys work? yes/no
- Did ZLink/TLink/Android Auto show metadata? yes/no/not tested
- Did sleep/resume work? yes/no/not tested
- Any visible audio focus/nav mixing issue?

Do not commit raw evidence bundles to a public repo without redaction.
EOF

if command -v zip >/dev/null 2>&1; then
  if (cd "$OUT_BASE" 2>/dev/null && zip -qr "$(basename "$OUT").zip" "$(basename "$OUT")"); then
    echo "Created zip: $OUT_BASE/$(basename "$OUT").zip"
  fi
fi

echo "Created evidence folder: $OUT"
