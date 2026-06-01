# Car Floating Controls Architecture

## Problem

On the TS18/Topway/DoFun head unit, the launcher/radio/navigation surfaces often sit outside Auxio-TS. The user needs a persistent, always-visible playback control surface above those screens, while avoiding display above Auxio-TS itself.

## Recommended design

Implement a first-party overlay service in Auxio-TS:

```text
Auxio playback/session layer
        ↑
CarOverlayPlaybackBridge
        ↑
CarFloatingControlsService
        ↑
WindowManager overlay view
```

### Why first-party

External overlay apps need notification listener/media-session discovery because they control unknown media apps. Auxio-TS owns the player, so the overlay can use internal player/session APIs directly. This is more reliable on TS18 firmware than relying on generic media keys or notification scraping.

## Runtime pieces

### CarFloatingControlsService

Responsibilities:

- check overlay permission;
- create/destroy the overlay view;
- keep one overlay instance only;
- handle start/stop/show/hide/toggle intents;
- expose media-control actions;
- keep an optional foreground notification for recoverability;
- persist position after drag;
- hide while Auxio-TS is foreground.

### CarFloatingControlsView

Programmatic view to avoid introducing Compose/XML dependencies before repo integration.

MVP layout:

```text
⠿  ⏮  ⏯  ⏭  ♪
```

- drag handle;
- previous;
- play/pause;
- next;
- open Auxio;
- optional title/artist/artwork in later phase.

### CarOverlayPlaybackBridge

Temporary integration seam. Replace fallback key-event control with real Auxio playback calls.

Preferred final implementation:

```text
playbackController.previous()
playbackController.togglePlayPause()
playbackController.next()
```

or connect to the app's existing MediaSession/Player controller if that is the canonical state owner.

### Visibility hooks

Use Auxio-owned lifecycle hooks, not UsageStats or Accessibility:

```text
Auxio activity/process foreground -> hide overlay
Auxio activity/process background -> show overlay if enabled
```

Implementation choices:

1. Hook all Auxio activities through a shared base activity.
2. Register `Application.ActivityLifecycleCallbacks`.
3. Use `ProcessLifecycleOwner` only if that dependency already exists or is acceptable.

## Variant strategy

Start under `topwayTwMusic` only. Once stable, Codex can decide whether to expose it to other variants behind a setting.

## Permission model

- `SYSTEM_ALERT_WINDOW` for draw-over-other-apps.
- Foreground service permission if a dedicated service is used.
- Android 13+ notification permission if target SDK requires notification posting.

## Settings

Suggested setting group: `Settings > Car / Head unit` or equivalent:

- Enable floating controls.
- Start automatically when playback starts.
- Show always / show only while playback active.
- Hide while Auxio is foreground.
- Position: bottom-right, bottom-left, top-right, top-left, custom.
- Button size: normal, large, huge.
- Opacity.
- Reset overlay position.

## Failure modes

| Failure | Likely cause | Fix |
|---|---|---|
| Overlay never appears | permission missing or service stopped | permission activity + readiness notice |
| Duplicate overlays | service recreates without removing old view | guard state and remove view before add |
| Overlay controls wrong app | fallback media keys used | integrate direct Auxio playback bridge |
| Overlay blocks Auxio UI | foreground hook missing | hide on Auxio foreground |
| Overlay disappears after sleep | service/process killed | boot/foreground notification/prefs recovery |
| Reverse camera overlay issue | MCU/system layer over Android | accept; do not try unsafe system overlay |
