# TS18 Validation Runbook (human-executed)

This runbook is for reproducible stock-vs-Auxio TS18 validation.

## 0) Preconditions
- Target hardware: TS18-like device (API 29 class preferred).
- Host tools: `adb`, shell, zip/unzip utilities.
- Private storage for raw captures (do not commit raw private data).
- App under test package noted (default `org.oxycblt.auxio`).

## 1) Baseline environment capture
1. Connect device and verify:
   - `adb devices`
2. Capture package baseline:
   - `adb shell pm list packages | grep -Ei 'auxio|tw|music|zlink|tlink|launcher|dudu|ilauncher|dofun'`
3. Save build/device metadata snapshot.

## 2) Stock `com.tw.music` baseline collection
1. Start playback in stock app.
2. Run capture script:
   - `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-music-playing`
3. Manually record:
   - notification controls responsiveness,
   - steering-wheel/media-key behavior,
   - launcher/home widget metadata,
   - ZLink/TLink interaction if active,
   - sound-priority/navigation-mixing behavior,
   - sleep/resume behavior.

## 3) Auxio-TS install and baseline capture
1. Build/install Auxio-TS debug APK.
2. Start local playback (MP3 first).
3. Run capture:
   - `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-ts-mp3-playing`
4. Confirm active media session and notification control path.

## 4) Active media session comparison
1. Collect both stock and Auxio captures with equivalent playback states.
2. Use:
   - `./scripts/ts18_compare_media_sessions.sh <stock_dump> <auxio_dump>`
3. Compare:
   - active session owner,
   - playback state,
   - metadata fields,
   - queue/transport command availability.

## 5) Notification transport controls
For both stock and Auxio:
1. Press play/pause/next/prev from notification.
2. Record latency/failures.
3. Capture dumps during interaction (`media_session`, `notification`, `audio`).

## 6) Media keys and steering wheel
1. Trigger hardware keys (play/pause/next/prev) repeatedly.
2. Capture input/session traces while triggering keys.
3. Classify each action as:
   - standard media dispatch,
   - vendor-routed,
   - not delivered.

## 7) Launcher widget / home-card behavior
1. Observe stock app widget behavior while playing and paused.
2. Repeat with Auxio-TS under same launcher.
3. If available, repeat on DoFun/iLauncher/DUDU variants.
4. Record differences in metadata refresh and control availability.

## 8) ZLink/TLink coexistence
1. With ZLink/TLink idle, run baseline playback checks.
2. With ZLink/TLink active, repeat checks.
3. Compare metadata/control ownership and focus transitions.

## 9) FLAC + local-library validation
1. Test FLAC set minimum:
   - 16/44.1,
   - 16/48,
   - 24/48 (if available).
2. Validate local-library indexing and playback consistency.
3. Record seek/resume/metadata behavior and glitches.

## 10) Equalizer/audio-effects coexistence
1. Run playback while OEM EQ/audio effects are enabled/disabled.
2. Observe focus/path conflicts and audible artifacts.
3. Record whether Auxio controls remain stable.

## 11) Sound-priority and navigation-mixing
1. While music plays, trigger navigation prompts/other source cues.
2. Record ducking/muting/mix behavior for stock vs Auxio.
3. Capture `dumpsys audio` during transitions.

## 12) Sleep/resume lifecycle
1. Test screen off/on and app background/foreground transitions.
2. Test ACC-like sleep/resume if safely possible.
3. Record session restoration, focus reacquisition, and control continuity.

## 13) Coexistence and rollback
1. Confirm stock and Auxio can coexist without breaking launcher entry points.
2. Validate uninstall/reinstall and default-player switching behavior.
3. Record rollback steps if an experiment introduces instability.

## 14) Evidence packaging
- Keep raw captures private.
- Create redacted summaries only for repo commits.
- Include:
  - scenario label,
  - timestamp,
  - app version + commit SHA,
  - observed vs inferred classification,
  - gaps requiring further validation.

Expected naming example:
- `auxio-ts-evidence_YYYYMMDD_HHMMSS_<scenario>.zip`
