# TS18 Validation Runbook (human-executed)

## 0) Preconditions
- TS18 target hardware connected via ADB.
- Private storage for raw evidence; only redacted summaries go in repo.
- Test packages noted:
  - stock: `com.tw.music`
  - Auxio-TS: `org.oxycblt.auxio` (or current app id)

## 1) Baseline capture (device + package ecosystem)
1. `adb devices`
2. `adb shell getprop ro.build.fingerprint`
3. `adb shell getprop ro.build.version.release`
4. `adb shell pm list packages | grep -Ei 'auxio|tw|music|zlink|tlink|launcher|dudu|ilauncher|dofun'`
5. Save outputs with timestamp and commit SHA.

## 2) Stock `com.tw.music` baseline
1. Start stock playback.
2. Run: `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-music-playing`
3. Capture:
   - `adb shell dumpsys media_session`
   - `adb shell dumpsys notification`
   - `adb shell dumpsys audio`
4. Manual observations:
   - notification transport control behavior
   - steering-wheel/hardware media keys
   - launcher/home widget metadata & controls
   - TWTHEME visual coupling
   - ZLink/TLink idle vs active behavior
   - navigation-mixing and sleep/resume

## 3) Auxio-TS baseline
1. Install/update Auxio-TS and play local media.
2. Run: `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-ts-playing`
3. Collect the same dumpsys captures and manual observations.

## 4) Stock-vs-Auxio active session comparison
1. Use equivalent playback states for both apps.
2. Run comparator: `./scripts/ts18_compare_media_sessions.sh <stock_dump> <auxio_dump>`
3. Compare owner, state/actions, metadata, command path behavior.

## 5) Notification controls
For each app: test play/pause/next/prev repeatedly and record latency/failures.

## 6) Media keys / steering wheel
For each app:
- trigger hardware key events,
- capture session/audio traces,
- classify each event route: standard Android / vendor-routed / undelivered.

## 7) Launcher widget and launcher variants
1. Compare stock vs Auxio on default launcher.
2. If present, repeat on iLauncher/DUDU/DoFun variants.
3. Record metadata freshness and control support gaps.

## 8) ZLink/TLink coexistence
1. Repeat baseline with ZLink/TLink idle.
2. Repeat with active projection.
3. Compare focus/session ownership and metadata/control visibility.

## 9) FLAC/local-library matrix
Test at minimum:
- 16/44.1
- 16/48
- 24/48 (if available)

Record scan/index/play/seek/resume behavior and glitches.

## 10) Equalizer/audio-effects + sound-priority/navigation-mixing
- Evaluate with OEM EQ/effects states.
- Trigger navigation prompts during playback and capture duck/mute/recover behavior.

## 11) Sleep/resume
- screen off/on, background/foreground, ACC-like sleep/resume (if safe), then compare restoration/focus/session continuity.

## 12) Coexistence and rollback
- Verify stock+Auxio coexistence, default-player switching, uninstall/reinstall rollback.

## 13) Evidence packaging format
Each scenario report must include:
- timestamp + tested commit SHA,
- app build/version,
- commands run,
- observed results,
- confidence + porting-decision labels,
- unresolved gaps and next test.
