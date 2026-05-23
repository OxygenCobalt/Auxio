#!/usr/bin/env bash
set -u
OUT="validation-output"; LABEL="ts18"; PKG="org.oxycblt.auxio"; SCEN="TS18-STD-012"; PHASE="single"; ACT=""; NOLOG="false"; LOGSEC=20; ZIP="false"; DRY="false"; VERBOSE="false"
usage(){ echo "usage: $0 [--out dir] [--label lbl] [--package pkg] [--scenario id] [--single|--before|--after] [--action safe] [--no-logcat] [--logcat-seconds n] [--zip] [--dry-run] [--verbose]"; }
while [ $# -gt 0 ]; do case "$1" in --out) OUT="$2";shift 2;;--label) LABEL="$2";shift 2;;--package) PKG="$2";shift 2;;--scenario) SCEN="$2";shift 2;;--single) PHASE="single";shift;;--before) PHASE="before";shift;;--after) PHASE="after";shift;;--action) ACT="$2";shift 2;;--no-logcat) NOLOG="true";shift;;--logcat-seconds) LOGSEC="$2";shift 2;;--zip) ZIP="true";shift;;--dry-run) DRY="true";shift;;--verbose) VERBOSE="true";shift;;-h|--help) usage;exit 0;;*) echo "unknown $1";exit 1;; esac; done
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"; PACK_ID="${STAMP}-${LABEL}-${SCEN}-${PHASE}"; DIR="${OUT}/${PACK_ID}"; RAW="${DIR}/raw"; mkdir -p "$RAW"
run(){ local n="$1";shift; echo "$*" >>"$DIR/command-log.txt"; [ "$VERBOSE" = "true" ] && echo "run: $*"; if [ "$DRY" = "true" ]; then echo dry_run >"$RAW/$n.txt"; return 0; fi; "$@" >"$RAW/$n.txt" 2>&1 || echo "capture_failed=true" >>"$RAW/$n.txt"; }
act(){ case "$1" in launch-main) run action adb shell am start -n "$PKG/.MainActivity";;launch-now-playing) run action adb shell am start -a "$PKG.action.OPEN_NOW_PLAYING" -n "$PKG/.MainActivity";;launch-queue) run action adb shell am start -a "$PKG.action.OPEN_QUEUE" -n "$PKG/.MainActivity";;launch-playlists) run action adb shell am start -a "$PKG.action.OPEN_PLAYLISTS" -n "$PKG/.MainActivity";;media-play-pause) run action adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE;;media-next) run action adb shell input keyevent KEYCODE_MEDIA_NEXT;;media-previous) run action adb shell input keyevent KEYCODE_MEDIA_PREVIOUS;;headset-hook) run action adb shell input keyevent KEYCODE_HEADSETHOOK;;"") :;;*) echo "invalid action" >"$RAW/action.txt";; esac; }
if command -v adb >/dev/null 2>&1; then
 run device_props adb shell getprop; run package_state adb shell dumpsys package "$PKG"; run packages adb shell pm list packages
 run packages_filtered adb shell sh -c "pm list packages | grep -Ei 'auxio|tw|theme|ilauncher|zlink|tlink|launcher|media'"
 run media_session adb shell dumpsys media_session; run audio adb shell dumpsys audio; run notification adb shell dumpsys notification; run appwidget adb shell dumpsys appwidget; run shortcut adb shell dumpsys shortcut; run activity_top adb shell dumpsys activity top
 act "$ACT"
 if [ "$NOLOG" = "false" ]; then run logcat_filtered adb shell sh -c "logcat -d -t $LOGSEC | grep -Ei 'auxio|media|audio|widget|shortcut|focus|zlink|tlink|theme|launcher'"; fi
else
 echo "adb_not_found=true" >"$RAW/capture-status.txt"
fi
AUX_VER="unknown"; [ -f "$RAW/package_state.txt" ] && AUX_VER="$(rg -o 'versionName=[^ ]+' "$RAW/package_state.txt" -m1 | cut -d= -f2)" || true
FPR="unknown"; [ -f "$RAW/device_props.txt" ] && FPR="$(rg -o '\[ro.build.fingerprint\]: \[[^]]+\]' "$RAW/device_props.txt" -m1 | sed -E 's/.*\[([^]]+)\].*/\1/')" || true
printf '{\n "pack_id":"%s",\n "pack_kind":"real",\n "device_label":"%s",\n "scenario_id":"%s",\n "capture_phase":"%s",\n "created_at":"%s",\n "auxio_package":"%s",\n "auxio_version":"%s",\n "android_build_fingerprint":"%s",\n "ts18_profile_hint":"unknown",\n "files":[%s],\n "redaction_status":"raw",\n "capture_tool_version":"2.0.0"\n}\n' "$PACK_ID" "$LABEL" "$SCEN" "$PHASE" "$STAMP" "$PKG" "${AUX_VER:-unknown}" "${FPR:-unknown}" "$(find "$RAW" -maxdepth 1 -type f -printf '"raw/%f",' | sed 's/,$//')" > "$DIR/manifest.json"
if [ "$ZIP" = "true" ]; then (cd "$OUT" && zip -rq "${PACK_ID}.zip" "$PACK_ID"); fi
echo "$DIR"
