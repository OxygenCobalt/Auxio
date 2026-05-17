# Navigation and Media Compatibility Plan (Google Maps / Waze)

## Scope and intent

This document defines how `com.tw.music` should coexist with navigation apps on the TS18 Android 13 target without introducing navigation-app-specific SDK dependencies.

- Integration target is **standard Android media behaviour** (MediaSession + PlaybackState + MediaMetadata + media notification), not private or custom APIs for Google Maps/Waze.
- Existing command ingress must remain the TW command path:
  - `com.tw.music.action.prev`
  - `com.tw.music.action.pp`
  - `com.tw.music.action.next`
  - `com.tw.music.action.cmd`
- Session/notification state must remain consistent with `MusicService`, `MusicInfo`, and `MusicWidgetProvider` projection.

## Compatibility model

### Primary mechanism: correct MediaSession publication

On Android 13, navigation overlays and external media surfaces read app playback state primarily from MediaSession state. For `com.tw.music`, compatibility depends on:

1. Exactly one active, authoritative playback session per active playback context.
2. Correct `PlaybackState` transitions (playing, paused, buffering, stopped, error).
3. Fresh `MediaMetadata` (title, artist, album, duration, artwork URI/bitmap strategy).
4. Notification/media controls mirroring the same state machine as service/widget state.

### Audio focus and ducking expectations

`com.tw.music` must cooperate with transient navigation prompts:

- Respect transient focus loss / duck requests during turn prompts.
- Do not publish stale paused/stopped state during short duck-only intervals.
- Recover normal gain and state after prompt completion.
- Preserve focus behaviour compatibility with existing TW/IJK playback stack and vendor properties.

## Google Maps coexistence

### Expected behaviour

- Google Maps foreground navigation should not hide or invalidate active media control capability.
- Media controls must remain discoverable via Android media surfaces while Maps is foreground.
- Session state should continue updating while prompts are spoken.

### Required technical conditions

- Active session is set/cleared with service lifecycle correctness.
- `PlaybackState` position and actions are updated continuously enough to avoid stale transport state.
- Metadata updates happen on every track change and relevant timeline change.
- Command callbacks for play/pause/next/prev continue routing through existing `com.tw.music.action.*` handling path.

### Stale-state prevention

- Coalesce updates but never suppress terminal transitions (pause/stop/error).
- Ensure widget/service/session all consume the same authoritative playback snapshot.
- Avoid delayed metadata publication after track changes.

### Validation approach (Google Maps)

- Start music playback, launch Google Maps, begin active route guidance.
- Trigger multiple turn prompts and verify playback/session state remains coherent.
- Execute controls (head-unit keys, widget, system media controls) and verify command-path continuity.
- Inspect logcat for session/service exceptions or binder failures.

## Waze coexistence

### Near-term behaviour target

- Treat Waze as a standard Android audio-focus/navigation actor.
- Validate coexistence using MediaSession correctness and focus handling only.

### Waze Audio Kit note

- Waze Audio Kit is a **future investigation item only**.
- This project should not assume Waze Audio Kit integration in current implementation scope.

### Audio focus expectations

- Duck/transient-loss handling should mirror Google Maps expectations.
- Playback state publication should remain accurate during transient navigation prompts.

### Validation approach (Waze)

- Repeat the same playback + navigation prompt matrix used for Maps.
- Verify no divergence in session controls, metadata freshness, or crash behaviour.

## Acceptance checks

A navigation compatibility implementation pass is acceptable when all checks pass on TS18 Android 13:

1. Playback remains visible/control-capable while Maps or Waze is foreground.
2. Navigation prompts do not break playback state continuity.
3. Metadata remains fresh across track changes and prompt interruptions.
4. Play/pause/next/previous actions continue routing through existing TW command path (`com.tw.music.action.*`).
5. Logcat shows no `MusicService` / MediaSession crashes or repeated session errors.
