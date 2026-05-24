# Topway native compatibility requirements for Auxio-TS

## Purpose

This document converts the Topway APK decompile findings into requirements for the next Auxio-TS runtime implementation pass.

The objective is **full practical compatibility with the stock Topway music integration surfaces**, not merely generic Android media/widget correctness.

## Required isolated bridge package

Create/keep all Topway-specific runtime code under an isolated package, for example:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/
app/src/test/java/org/oxycblt/auxio/headunit/topway/
```

Topway strings must not be scattered through playback, widget, dashboard, or settings code.

## Required contract constants

Implement a central contract file, for example:

```text
TopwayMusicContract.kt
```

Required constants:

```text
ACTION_MUSIC_INFO = "com.tw.music.info"
EXTRA_MUSIC_TITLE = "musicTitle"
EXTRA_MUSIC_ARTIST = "musicaArtist"
EXTRA_MUSIC_ALBUM = "musicAlbum"
EXTRA_MUSIC_PATH = "musicPath"

ACTION_PROGRESS_DURATION = "com.tw.launcher.music_progress_duration"
EXTRA_PROGRESS = "msg_music_progress"
EXTRA_DURATION = "msg_music_duration"

ACTION_LAUNCHER_WIDGET_SEEK = "com.android.launcher.widget_music_progress"
EXTRA_WIDGET_PROGRESS = "music_progress"

ACTION_CMD = "com.tw.music.action.cmd"
ACTION_PREV = "com.tw.music.action.prev"
ACTION_NEXT = "com.tw.music.action.next"
ACTION_PLAY_PAUSE = "com.tw.music.action.pp"

EXTRA_CMD = "cmd"
EXTRA_APP_WIDGET_IDS = "appWidgetIds"
CMD_PREV = "prev"
CMD_NEXT = "next"
CMD_PLAY_PAUSE = "pp"
CMD_UPDATE = "update"
```

## Metadata broadcast requirement

Implement outgoing metadata broadcast parity:

```text
Intent action: com.tw.music.info
Extras: musicTitle, musicaArtist, musicAlbum, musicPath
```

Rules:

- Use existing Auxio metadata policy.
- Preserve the typo `musicaArtist`.
- Send on track metadata change.
- De-duplicate unchanged broadcasts where practical.
- Do not use sticky broadcast for metadata.
- Do not fake unavailable path values.
- Do not leak unsafe private filesystem paths.

## Progress/duration broadcast requirement

Implement outgoing progress/duration broadcast parity:

```text
Intent action: com.tw.launcher.music_progress_duration
Extras: msg_music_progress, msg_music_duration
```

Rules:

- Send while session/playback is active.
- Reuse existing playback progress lifecycle where possible.
- Throttle around 1 second.
- Stop when playback/session stops.
- Do not add a wakeful independent polling loop.

## Incoming transport command requirement

Accept these incoming Topway commands:

```text
com.tw.music.action.cmd with cmd=prev/next/pp/update
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
```

Map to Auxio actions:

```text
prev -> previous
next -> next
pp -> play/pause toggle
update -> refresh widget state
```

Rules:

- Gate by current session/current song/focus policy.
- Do not auto-start from inert/no-song state.
- Avoid unsafe autoplay.
- Receiver/exported status must be intentional and narrow.
- Keep `onReceive()` lightweight.

## Launcher seek requirement

Accept:

```text
com.android.launcher.widget_music_progress
extra music_progress
```

Rules:

- Seek only with active session and valid duration.
- Clamp out-of-range values.
- Ignore missing/bad extras.
- Never crash on malformed launcher broadcast.

## Widget RemoteViews parity requirement

Auxio-TS widget must support equivalents of stock fields:

```text
title
artist/subtitle
current time
duration
progress bar
album art or fallback
prev/play/next controls
album/root click to Now Playing
```

Rules:

- Use existing Auxio widget architecture.
- Do not create duplicate provider unless justified.
- No stale metadata after queue empty/no session.
- Update all widget instances.
- Update on metadata/state/progress and `cmd=update`.

## MediaSession/notification/widget unification requirement

Topway bridge broadcasts, Android MediaSession, notification, and widget should derive from one effective metadata state:

```text
title
subtitle/artist
album artist
album
duration
progress
artwork
media id/uri/path equivalent
```

## Guardrail requirement

Guardrails should allow Topway strings only in:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/
app/src/test/java/org/oxycblt/auxio/headunit/topway/
docs/
```

Guardrails should still block:

```text
sharedUserId
android.uid.system
package impersonation as com.tw.music
copied smali
com.tw.service.xt binder execution
ITWCommandAidl runtime binding
TWUtil/TWClient reflection
vendor package scanners
runtime probes/evidence collectors
```

## Acceptance criteria for implementation pass

The next implementation pass is not complete unless it implements and tests:

1. `TopwayMusicContract`;
2. outgoing `com.tw.music.info` broadcast;
3. outgoing `com.tw.launcher.music_progress_duration` broadcast;
4. incoming Topway transport commands;
5. incoming launcher seek;
6. widget RemoteViews title/artist/time/duration/progress/art/control parity;
7. widget update lifecycle parity;
8. metadata unification with MediaSession/notification/widget;
9. guardrail exception limited to isolated bridge;
10. concise docs updates.
