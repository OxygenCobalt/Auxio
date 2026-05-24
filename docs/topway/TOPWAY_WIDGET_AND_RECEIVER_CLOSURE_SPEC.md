# Topway Native Widget and Receiver Lifecycle Specification

This document is a second-pass compatibility specification derived from the current apktool/JADX archives for the official Topway `com.tw.music` APK.

It is intended to guide Auxio-TS PR #34 completion work.

## Source archives reviewed

- `topway-apktool.zip`
- `topway-apktool0.zip`
- `topway-music-smali-main.zip` where available in the current environment

The two apktool archives confirm manifest/resources/smali. The JADX archive provides readable alias/deobfuscation references, but apktool paths/names are treated as authoritative for runtime identity.

## Runtime identity

Observed runtime package:

```text
com.tw.music
```

JADX aliases such as:

```text
com.p060tw.music
com.p073tw.music
```

are decompiler artefacts and must not be used as runtime package names.

## Manifest facts

Observed stock manifest properties:

```text
package="com.tw.music"
android:sharedUserId="android.uid.system"
service android:name="com.tw.music.MusicService"
receiver android:name="com.tw.music.view.MusicWidgetProvider"
meta-data android:name="android.appwidget.provider" android:resource="@xml/appwidget_info"
```

Auxio-TS requirements:

- Do **not** copy `sharedUserId`.
- Do **not** impersonate `com.tw.music`.
- Do **not** copy privileged/system assumptions.
- Do implement only the safe broadcast/widget contract in an isolated bridge.

## Widget provider resource

Observed stock provider XML:

```text
res/xml/appwidget_info.xml
res/xml-sw768dp/appwidget_info.xml
```

Key observed properties:

```text
updatePeriodMillis="0"
initialLayout="@layout/music_widget"
minWidth approximately 430-424dp
minHeight approximately 200-194dp
```

Compatibility interpretation:

- Widget updates are event-driven, not periodic polling.
- Auxio-TS should not add unconditional background polling.
- AppWidgetProvider callbacks should render current safe state and delegate long work elsewhere.
- The TS18 widget parity target includes landscape-friendly dimensions and progress/time fields.

## Widget layout

Observed stock layout:

```text
res/layout/music_widget.xml
```

Core fields:

```text
@id/music_widget
@id/albumart
@id/title
@id/artist
@id/control_prev
@id/control_play
@id/control_next
@id/tv_current_time
@id/tv_duration
@id/seek_bar_progress
```

Visual structure:

- horizontal root layout;
- album art on the left;
- vertical metadata/control panel on the right;
- title and artist as single-line centered text;
- previous / play-pause / next image controls;
- current time and duration row;
- horizontal progress bar below time/duration.

Observed scale:

```text
overall widget root: approximately 490w x 225h Topway dimension units
album art: approximately 150w x 150h
title text: 18dp-ish Topway height
artist text: 16dp-ish Topway height
progress bar max/min height: approximately 3h
```

Auxio-TS requirements:

- Existing widget layouts must expose equivalent display concepts where architecture permits:
  - title
  - artist/subtitle
  - current time
  - duration
  - progress
  - album art/fallback
  - prev/play-pause/next controls
  - root/art tap to Now Playing
- A layout that only shows title/artist/play-pause is not enough for Topway parity.
- Use existing Auxio widget architecture; do not add a parallel provider unless absolutely necessary.

## Widget update logic

Readable JADX shows `MusicWidgetProvider.onUpdate()`:

1. renders a base RemoteViews state;
2. starts `MusicService`;
3. sends broadcast:
   - action `com.tw.music.action.cmd`
   - extra `cmd=update`
   - extra `appWidgetIds=<ids>`
4. adds flag `0x40000000`;
5. sends sticky broadcast.

Compatibility interpretation:

- Auxio-TS should mirror the behaviour safely, not literally.
- Do **not** use sticky broadcasts unless there is a clear need and permissions allow it.
- `cmd=update` should refresh widgets if widget instances exist.
- If widget instances do not exist, the update should be a safe no-op.
- AppWidget update must not rely on long-running provider work.

## Widget timing/progress units

Observed stock provider converts position/duration from milliseconds to seconds for the RemoteViews progress bar:

```text
currentPositionSeconds = currentPosition / 1000
durationSeconds = duration / 1000
setProgressBar(seek_bar_progress, durationSeconds, currentPositionSeconds, false)
```

Observed text formatting:

```text
m:ss when under one hour
h:mm:ss when one hour or longer
```

Auxio-TS requirements:

- Widget progress bar max/progress must use consistent units.
- Prefer seconds for RemoteViews parity and lower churn.
- Current time and duration text should match stock formatting (`m:ss` or `h:mm:ss`).
- Bad or unknown duration should render as zero/placeholder safely.

## Widget album art

Observed stock behaviour:

```text
if bitmap != null and bitmap.getByteCount() <= 3680000:
    setImageViewBitmap(albumart, bitmap)
else:
    setImageViewResource(albumart, R.drawable.album)
```

Compatibility interpretation:

- Auxio-TS should keep album art RemoteViews-safe.
- Large bitmap/binder-size failures must be avoided.
- Missing/invalid artwork must reset to fallback instead of stale prior art.

## Widget control PendingIntents

Observed stock provider:

- album art click: `PendingIntent.getActivity(...)` to `MusicActivity`;
- previous: explicit service intent with `com.tw.music.action.prev`;
- play-pause: explicit service intent with `com.tw.music.action.pp`;
- next: explicit service intent with `com.tw.music.action.next`.

Auxio-TS requirements:

- Root/art tap should open Now Playing.
- Transport buttons should map to existing Auxio playback actions.
- Request codes must be stable and distinct.
- No stale extras or stale PendingIntent payloads.
- No forced autoplay from inert/no-song state.

## Command receiver/service path

Observed stock service dynamically registers actions:

```text
com.tw.music.action.cmd
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
```

Observed `onStartCommand()` handles direct service intents:

```text
cmd=prev or action prev -> previous
cmd=next or action next -> next
cmd=pp or action pp -> play/pause
```

Observed internal receiver handles:

```text
cmd=update -> widget refresh for appWidgetIds
```

Auxio-TS requirements:

- Dynamic receiver path alone only works when service/process is alive.
- If external Topway/iLauncher actions are expected to work cold, a narrow exported receiver is needed.
- If a manifest receiver is added, it must only declare the allowed actions and delegate quickly.
- Receiver logic must be no-op safe when playback/session/current song is unavailable.
- `cmd=update` must be safe with no widgets.

## Launcher seek input

Observed stock activity dynamically registers:

```text
com.android.launcher.widget_music_progress
```

Receiver extracts:

```text
music_progress
```

and calls presenter seek.

Compatibility interpretation:

- Stock receive path appears activity-lifetime scoped, not manifest-static.
- Auxio-TS may choose static receiver for better TS18/iLauncher cold compatibility, but must do so explicitly and safely.
- Seek must be guarded:
  - current song/session exists;
  - duration is valid;
  - extra is numeric;
  - target is clamped or rejected safely;
  - no crash on malformed/missing extras.

## Metadata broadcast

Observed vendor/music integration sends:

```text
action: com.tw.music.info
extras:
  musicTitle
  musicaArtist
  musicAlbum
  musicPath
```

The spelling `musicaArtist` is observed and must be preserved.

Auxio-TS requirements:

- Use the same metadata policy as MediaSession/widget/notification.
- Avoid duplicate/stale broadcasts.
- Do not fake unavailable values.
- Use safest available URI/path for `musicPath`.
- Do not leak private paths unnecessarily.

## Progress/duration broadcast

Observed vendor/music integration sends:

```text
action: com.tw.launcher.music_progress_duration
extras:
  msg_music_progress = currentPosition
  msg_music_duration = duration
```

The observed values are milliseconds.

The vendor loop sends roughly every 1000 ms while active.

Auxio-TS requirements:

- Broadcast progress/duration in milliseconds for Topway compatibility.
- Throttle around 1000 ms while active.
- Stop or clear on no-session/reset.
- Do not create an always-on poller.
- Prefer existing playback progression/service lifecycle hooks.

## XT/AIDL evidence

Observed evidence includes:

```text
com.tw.service.xt
CommandService
ITWCommandAidl
IMusicCallBack
```

Classification:

- Confidence: Observed
- Porting decision: Useful as evidence only / Unsafe to port directly
- Production status: blocked by default

Do not implement XT/AIDL binding in Auxio-TS without a separate human-approved design PR.

## Critical PR #34 closure requirements

PR #34 should not be treated as complete until all of the following are either implemented or explicitly rejected with a technical rationale:

1. Static/manifest Topway receiver strategy.
2. Widget current-time/duration/progress visual parity.
3. Widget no-session/stale-state clearing for timeline fields and artwork.
4. `cmd=update` widget refresh behaviour.
5. Topway metadata/progress broadcast lifecycle.
6. Tests for runtime edge cases, not just constants.
7. Guardrail path allowlist including manifest receiver action strings if needed.
8. Docs updated to match actual implementation state.
