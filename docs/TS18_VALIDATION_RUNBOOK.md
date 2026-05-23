# TS18 Validation Runbook (acceptance testing)

## Scope
This runbook validates product behaviour on TS18 hardware. It does not depend on in-app probe modules.

Validation starts from expected behaviours derived from TS18/TW/TWTHEME sources and local evidence. Do not design new in-app probes merely because a behaviour lacks fresh runtime proof. Only add external diagnostics when a source-led expectation cannot be validated otherwise.

## Source-led framing for all scenarios

Each scenario in this runbook is derived from at least one source in the canonical corpus at `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`. When adding new scenarios, identify the source first:

1. Check whether a Priority 1 or 2 source predicts or implies the expected behaviour.
2. Use local evidence (Priority 3) to refine the scenario.
3. Design the scenario as an on-device acceptance check, not a product-code probe.
4. Only add external diagnostic commands when needed to observe the expected behaviour.

Validation starts from expected behaviours derived from TS18/TW/TWTHEME sources and local evidence. Do not design new in-app probes merely because a behaviour lacks fresh runtime proof. Only add external diagnostics when a source-led expectation cannot be validated otherwise.

## Parity gap identification (what triggers Tier 3/4 investigation)

A Tier 2 validation result is classified as a **parity gap** when:
1. A runbook scenario above fails consistently across multiple test cycles.
2. A comparable Tier 1 implementation is confirmed complete (i.e. the failure is not a Tier 1 bug).
3. Comparable third-party apps (Spotify, Poweramp) succeed where Auxio-TS fails, suggesting a TS18/TWTHEME-specific issue.
4. A plausible private/native contract exists in the Tier 0 evidence corpus that could explain the gap.

When a parity gap is identified:
- Document the gap in [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md) with observed evidence.
- Update the "Remaining TS18/TWTHEME validation" and "Native/private investigation needed?" columns.
- Do **not** add native/private contract code to production without an explicit human-approved design PR (Tier 4).

See [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy) for the full tier model and Tier 4 eligibility criteria.

> **No hardware validation has been completed to date.** All scenario statuses are `Requires TS18 validation` until real-device evidence is captured and linked here.

## Related canonical docs
- `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- `docs/TS18_REQUIREMENTS.md`
- `docs/TS18_INTEGRATION_ARCHITECTURE.md`
- `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`
- `docs/TS18_NATIVE_CONTRACTS.md`
- `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`

## Optional evidence helper
- Use `scripts/ts18-capture-media-state.sh [output_dir]` to capture timestamped `adb` snapshots for MediaSession/audio/notification before and after media key events.
- This helper is external-only validation and does not add in-app probes.

## Allowed evidence labels for TS18/TW/TWTHEME claims
Use only these values in evidence rows:

### Confidence
- **Observed**
- **Inferred**
- **Hypothesis**
- **Requires TS18 validation**
- **Unsupported**

### Porting decision
- **Directly reusable requirement**
- **Reusable validation idea**
- **Useful as evidence only**
- **Obsolete due to Auxio architecture**
- **Requires TS18 runtime validation**
- **Unsafe to port**
- **Should be explicitly avoided**

## Acceptance scenarios

### TS18-STD-001: MediaSession metadata visible
- **Setup:** Open Auxio, start local playback, keep app foreground once to warm service.
- **Steps:** Run `adb shell dumpsys media_session | sed -n '/org.oxycblt.auxio/,/state=/p'`.
- **Expected:** Title/artist/album/duration/actions/queue index reflect current track and update after next/prev. `mediaId`, `mediaUri`, and `artUri/albumArtUri` are present for the current item whenever artwork exists.
- **Evidence:** command output + lockscreen/launcher capture.
- **Pass/Fail:** Pass only if metadata updates within one track transition.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-002: Notification controls work
- **Setup:** Playback active, notification permission granted.
- **Steps:** Trigger notification play/pause/next/prev 3 cycles.
- **Expected:** Transport buttons remain visible; compact controls map to prev-play/next; state mirrors MediaSession.
- **Evidence:** screen recording + `adb shell dumpsys notification --noredact`.
- **Pass/Fail:** Fail on missing controls, incorrect compact action order (must remain prev/play-pause/next), or stale state after any cycle.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-003: Steering-wheel/media keys route to Auxio
- **Setup:** Auxio installed as user app; playback paused with prior queue.
- **Steps:** Send/trigger `KEYCODE_MEDIA_PLAY_PAUSE`, `NEXT`, `PREVIOUS`, `STOP`.
- **Expected:** Standard keycodes control Auxio session without vendor broadcast dependencies.
- **Evidence:** steering-wheel capture or `adb shell input keyevent` log + playback state snapshots.
- **Pass/Fail:** Fail if keys are dropped while service is backgrounded.
- **Confidence / Porting decision:** Requires TS18 validation / Requires TS18 runtime validation.

### TS18-STD-004: Audio focus duck/restore with navigation
- **Setup:** Auxio playback + nav app with voice prompts.
- **Steps:** Start route guidance; capture `adb shell dumpsys audio`.
- **Expected:** Auxio ducks on transient duck requests, pauses on transient/full loss, and only resumes on gain when playback was active before transient loss. Playback must not unexpectedly auto-start from a fully stopped state after focus returns.
- **Evidence:** audio dump before/during/after + subjective audio observation.
- **Pass/Fail:** Fail on non-restoring volume/play state.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-005: ZLink/TLink coexistence smoke test
- **Setup:** Connect projection stack (ZLink/TLink) if installed.
- **Steps:** Toggle projection idle/active while Auxio session exists.
- **Expected:** Auxio session remains discoverable and recovers transport control when projection releases focus.
- **Evidence:** media_session + audio dumps, brief video.
- **Pass/Fail:** Fail if Auxio becomes unrecoverable without force-stop.
- **Confidence / Porting decision:** Inferred / Reusable validation idea.

### TS18-STD-006: Sleep/resume playback state restoration
- **Setup:** Start playback, then put head unit display/device into sleep/standby cycle.
- **Steps:** Wake device, then run `adb shell dumpsys media_session` and verify Auxio session state.
- **Expected:** Session and transport state are coherent after wake; playback resumes or remains paused consistently with pre-sleep state. Metadata/notification content should refresh to the restored queue item without stale artwork/text; when playback fully stops, stale metadata must clear.
- **Evidence:** pre/post `dumpsys media_session` + lockscreen/launcher screenshots.
- **Pass/Fail:** Fail if state is lost, stale, or mismatched post-resume.
- **Confidence / Porting decision:** Requires TS18 validation / Requires TS18 runtime validation.

### TS18-STD-007: Android Auto / external browser browse tree smoke test
- **Setup:** Connect external controller (Android Auto or compatible browser controller).
- **Steps:** Browse root and one child level, then trigger one playable item.
- **Expected:** Root is browsable, playable items are marked playable, and playback starts from selected item.
- **Evidence:** controller screenshots + `adb shell dumpsys media_session` output.
- **Pass/Fail:** Fail if root/children are empty unexpectedly or flags are inconsistent.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-008: Landscape/head-unit UI readability
- **Setup:** Enable **Settings → UI → Head unit** options as desired; use a typical TS18 landscape display mode (e.g., 1024x600 class layout).
- **Steps:** Inspect now playing, queue, transport controls, and metadata readability from driver distance. Repeat once with **Large touch controls** on.
- **Expected:** Critical controls/text remain visible, readable, and touchable without phone-only layout breakage. Large-controls mode increases practical hit targets for core transport actions.
- **Evidence:** screenshots/video from launcher + app playback surfaces.
- **Pass/Fail:** Fail if key controls are clipped, inaccessible, or illegible.
- **Confidence / Porting decision:** Requires TS18 validation / Reusable validation idea.

### TS18-STD-009: FLAC/local-library playback smoke matrix
- **Setup:** Index local library containing at minimum FLAC 16/44.1, 16/48, and 24/48 samples.
- **Steps:** Play/seek/pause/resume each sample; verify metadata and transport updates.
- **Expected:** Each sample plays and seeks correctly; metadata remains stable through transitions.
- **Evidence:** playback captures + `adb shell dumpsys media_session` snapshots per sample class.
- **Pass/Fail:** Fail on decode, seek, or metadata regression for any matrix row.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

## Evidence format
Per scenario include:
- device fingerprint + firmware/theme variant labels
- Auxio build SHA/version
- exact commands run
- observed result
- confidence (must be one of the allowed confidence labels above)
- porting decision (must be one of the allowed porting decision labels above)
- unresolved risk and next action

### TS18-STD-010: Launcher shortcuts open correct Auxio surfaces
- **Setup:** Auxio installed, launcher supports app shortcuts.
- **Steps / commands:** `adb shell dumpsys shortcut`; launch shortcut intents for Shuffle All and Queue.
- **Expected result:** Shuffle All starts shuffled playback; Queue opens queue surface.
- **Evidence to capture:** `dumpsys shortcut`, screen capture, `dumpsys media_session`.
- **Pass/fail criteria:** Fail if shortcut launches wrong surface or no-op.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-011: Now-playing widget shows metadata and controls playback
- **Setup:** Place Auxio widget on launcher desktop.
- **Steps / commands:** Start playback, observe title/artist and press prev/play-pause/next.
- **Expected result:** Metadata updates and controls affect active MediaSession.
- **Evidence to capture:** `adb shell dumpsys appwidget`, `adb shell dumpsys media_session`, screen recording.
- **Pass/fail criteria:** Fail if controls do not change playback state.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-012: TWTHEME/iLauncher desktop widget placement smoke test
- **Setup:** TS18 launcher with widget host (iLauncher/TWTHEME style desktop).
- **Steps / commands:** Add widget, resize where supported, rotate/restart launcher.
- **Expected result:** Widget remains visible, readable, and responsive.
- **Evidence to capture:** before/after screenshots and `adb shell dumpsys appwidget`.
- **Pass/fail criteria:** Fail on placement crash or unusable render.
- **Confidence / Porting decision:** Requires TS18 validation / Requires TS18 runtime validation.

### TS18-STD-013: Shortcut/widget behaviour after sleep/resume
- **Setup:** Widget and shortcuts configured.
- **Steps / commands:** Put device to sleep/wake; trigger shortcut and widget controls.
- **Expected result:** Actions still route correctly and session state refreshes.
- **Evidence to capture:** pre/post `dumpsys media_session`, `dumpsys notification`, launcher video.
- **Pass/fail criteria:** Fail on stale or dead controls post-resume.
- **Confidence / Porting decision:** Requires TS18 validation / Requires TS18 runtime validation.

### TS18-STD-014: Widget transport actions update MediaSession/notification state
- **Setup:** Playback active and widget visible.
- **Steps / commands:** Run `adb shell input keyevent KEYCODE_MEDIA_PLAY_PAUSE`, `KEYCODE_MEDIA_NEXT`, `KEYCODE_MEDIA_PREVIOUS` and use widget controls.
- **Expected result:** MediaSession state and notification controls mirror widget-triggered actions.
- **Evidence to capture:** `adb shell dumpsys media_session`, `adb shell dumpsys notification --noredact` before/after.
- **Pass/fail criteria:** Fail if session/notification lags or diverges.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-015: TWTHEME/iLauncher readability at 1024x600-class layouts
- **Setup:** 1024x600-class display mode.
- **Steps / commands:** Inspect widget title/artist text and transport hit targets.
- **Expected result:** Text is legible and controls are touchable from driver distance.
- **Evidence to capture:** launcher photos/screenshots.
- **Pass/fail criteria:** Fail if text is clipped/illegible or buttons are too small.
- **Confidence / Porting decision:** Requires TS18 validation / Reusable validation idea.

### TS18-STD-016: Warm-start launcher/deep-link routing works through onNewIntent
- **Setup:** Auxio already running in background.
- **Steps / commands:** `adb shell am start -n org.oxycblt.auxio/.MainActivity -a org.oxycblt.auxio.action.OPEN_NOW_PLAYING`; repeat with `.action.OPEN_QUEUE`.
- **Expected result:** Now playing/queue surfaces open immediately without requiring fragment resume cycle.
- **Evidence to capture:** screen recording + `adb shell dumpsys activity activities` excerpt.
- **Pass/fail criteria:** Fail if intent accepted but destination not opened.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

### TS18-STD-017: Queue shortcut opens queue, not generic playback surface
- **Setup:** Queue shortcut published.
- **Steps / commands:** Trigger queue shortcut from launcher.
- **Expected result:** Queue view opens (`openQueue` path), not generic playback panel.
- **Evidence to capture:** UI capture + optional debug logs.
- **Pass/fail criteria:** Fail if routed to playback panel instead of queue.
- **Confidence / Porting decision:** Requires TS18 validation / Directly reusable requirement.

## Added validation focus for Head-Unit Experience Mode (Tier 1 batch)
- TS18-STD-006 / TS18-STD-010 / TS18-STD-011 should now explicitly verify:
  - Dashboard policy-driven chips route to intended visible destinations.
  - Queue empty state offers actionable recovery path (Shuffle) and does not dead-end.
  - Large controls mode visibly increases playback primary button ergonomics.
  - Dashboard settings toggle hides/shows quick-access area consistently after warm start.

These checks still require real TS18 hardware evidence; no pass is implied by code changes.

- Dashboard route semantics check: FOLDERS/DECADES are non-route metadata chips; HEAD_UNIT_SETTINGS is explicit and should open settings surface.

- Phase 5G/6A: this runbook now depends on external evidence import scripts and does not auto-promote Tier 3/4.

Phase 5G/6A workflow command: scripts/ts18-validation-workflow.sh (capture/validate/summarise/classify/propose-matrix).
