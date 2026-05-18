# TS18 Validation Runbook (human-executed)


> Canonical note: `docs/TS18_EXECUTION_PACK_PHASE1_4.md` is the authoritative Phase 1–4 checklist for scenario IDs, evidence folder convention (`reports/ts18/<YYYY-MM-DD>/<scenario-id>/`), gate/stop criteria, and the claim labelling template. Use this runbook for expanded operational steps.

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
2. Run: `AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-playing-local` (canonical scenario: `TS18-STOCK-002`)
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
2. Run: `AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh auxio-mp3-playing` (canonical scenario: `TS18-AUXIO-002`)
3. Collect the same dumpsys captures and manual observations.

## 4) Stock-vs-Auxio active session comparison
1. Use equivalent playback states for both apps.
2. Run comparator per package and diff saved outputs:
   - `./scripts/ts18_compare_media_sessions.sh com.tw.music > stock_media_session.txt`
   - `./scripts/ts18_compare_media_sessions.sh org.oxycblt.auxio > auxio_media_session.txt`
   - `diff -u stock_media_session.txt auxio_media_session.txt`
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


## 14) Head-unit UI/UX validation (TS18-facing, UI-only)
These checks validate Auxio-TS head-unit UX options. They **do not** prove TS18 native/TW/TWTHEME compatibility.

1. In Auxio settings, open **Look and feel → Head unit** and verify these options persist after app restart:
   - Head unit landscape mode
   - Driver side (Right-hand drive / Left-hand drive)
   - Large touch controls
   - Show album art in head-unit playback view
2. Confirm landscape-first behavior is stable across home, playback panel, queue, and settings.
3. Toggle driver side and confirm control placement changes on playback panel/bar without changing text direction.
4. Confirm persistent now-playing access remains available from browse/library flows.
5. Confirm Quick Picks shortcuts (if visible in this build) are actionable and do not remove deep browse paths.
6. Confirm metadata shortcut chips (if visible in this build) are actionable, metadata-based, and **exclude** file type, file size, bitrate, codec, sample rate, and storage size.
7. Confirm queue capabilities remain intact (open queue, reorder/drag if available, remove actions if available).
8. Confirm favourites shortcut/chip: appears when a playlist named "Favourites" exists; hidden (not shown as disabled) when no such playlist exists; tapping opens the Favourites playlist detail view. Songs can be added to Favourites via the standard "Add to Playlist" context-menu flow.
9. Confirm album-art control remains minimal (show/hide behavior only) and controls remain readable.
10. Confirm playback feedback is visible for user actions and does not spam passively.
11. Confirm no driving/parked restrictions are present.
12. Record claim labels in report:
   - Evidence confidence label
   - Porting decision label
