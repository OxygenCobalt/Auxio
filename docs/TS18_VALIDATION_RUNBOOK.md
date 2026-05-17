# TS18 Validation Runbook

## 0. Preparation
- Device: TS18 target (Android 10/API 29 class).
- Host tools: `adb`, shell, repo scripts.
- Confirm app package under test (default example: `org.oxycblt.auxio`).
- Ensure private evidence storage location (do not commit raw captures).

## 1. Install/build prep
1. Build debug APK from repo.
2. Install with:
   - `adb install -r app/build/outputs/apk/debug/*.apk`
3. Confirm packages:
   - `adb shell pm list packages | grep -Ei 'auxio|tw|music|zlink|tlink'`

## 2. Baseline stock `com.tw.music` capture
1. Play a local track in stock music app.
2. Run:
   - `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-music-playing`
3. Record manual observations:
   - launcher/home widget update,
   - notification controls,
   - steering-wheel/media-key behaviour,
   - audio mixing/navigation prompts,
   - sleep/resume stability.

## 3. Auxio-TS capture (local MP3)
1. Play local MP3 in Auxio-TS.
2. Run:
   - `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-ts-mp3-playing`
3. Validate:
   - active MediaSession visible,
   - notification transport controls responsive,
   - audio focus transitions expected,
   - launcher/widget behaviour logged.

## 4. FLAC validation
Test at least:
- 16/44.1 FLAC,
- 16/48 FLAC,
- 24/48 FLAC (if available).

For each, capture evidence and record:
- startup latency,
- pause/resume/seek behaviour,
- metadata/cover display,
- audible glitches or repeated buffering.

## 5. Media-session and notification checks
- Use `scripts/ts18_compare_media_sessions.sh` where applicable.
- Manually verify lockscreen/notification control path.
- Capture `dumpsys media_session`, `dumpsys notification`, `dumpsys audio` during playback.

## 6. Steering wheel / media key checks
- Trigger play/pause/next/prev from steering controls if available.
- Capture input + session traces during actions.
- Record whether keys reach standard media dispatch or appear vendor-routed.

## 7. Launcher/widget and TWTHEME checks
- Compare stock vs Auxio-TS metadata display on launcher/home widget.
- Note any package-specific behaviour.
- Do not assume contract; classify as observed/inferred.

## 8. ZLink/TLink checks
- With ZLink/TLink active (safe state), play Auxio-TS and compare against stock.
- Record metadata/control path outcomes and conflicts.

## 9. Sleep/resume and audio-priority checks
- Test while playing and paused:
  - screen off/on,
  - background/foreground transitions,
  - ACC-like sleep/resume if safely accessible.
- Validate continuity, focus reacquisition, and navigation-mixing behaviour.

## 10. Expected output artifacts
Expected per capture folder:
- package/device snapshots,
- `dumpsys media_session`,
- `dumpsys audio`,
- notification/appops snippets where available,
- redacted summary.

Bundle naming example:
- `auxio-ts-evidence_YYYYMMDD_HHMMSS_<scenario>.zip`

## 11. Evidence packaging for agent review
- Keep raw bundles private.
- Commit only redacted summaries/scripts.
- Include scenario label, timestamp, app version/commit SHA, and quick result table.
