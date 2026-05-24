# Topway music apktool/JADX compatibility analysis

## Executive summary

The attached Topway APK evidence shows that stock `com.tw.music` compatibility is not merely generic Android media/widget support. It uses a concrete Topway contract made of:

- an AppWidgetProvider + RemoteViews music widget;
- Topway-specific transport command action strings;
- metadata broadcasts consumed by launcher/XT surfaces;
- progress/duration broadcasts consumed by launcher/widget surfaces;
- a launcher seek broadcast;
- an XT service/AIDL bridge that is real but unsafe as a production dependency for Auxio-TS.

Auxio-TS should implement the safe broadcast/widget/action compatibility layer in an isolated bridge while preserving clean APK boundaries.

## Primary apktool manifest findings

The apktool manifest shows:

```text
package="com.tw.music"
android:sharedUserId="android.uid.system"
uses-permission android.permission.BROADCAST_STICKY
activity com.tw.music.MusicActivity launchMode="singleTask"
service com.tw.music.MusicService
receiver com.tw.music.view.MusicWidgetProvider
appwidget provider metadata @xml/appwidget_info
```

Interpretation:

- `com.tw.music` is the runtime identity of the stock app.
- `sharedUserId=android.uid.system` is a privileged/system-app property and must not be copied into Auxio-TS.
- Auxio-TS must not impersonate `com.tw.music`.
- The widget receiver/provider pattern is a directly reusable architecture idea, not a class/smali port.
- `BROADCAST_STICKY` explains why the stock app can use sticky widget update broadcasts; Auxio-TS should avoid sticky broadcasts unless a safe non-privileged reason exists.

## AppWidget provider resource findings

`res/xml/appwidget_info.xml`:

```text
minWidth @dimen/tw_dp_w430
minHeight @dimen/tw_dp_h200
updatePeriodMillis 0
initialLayout @layout/music_widget
```

`res/xml-sw768dp/appwidget_info.xml`:

```text
minWidth 424dp
minHeight 194dp
updatePeriodMillis 0
initialLayout @layout/music_widget
```

Interpretation:

- The stock widget is event-driven (`updatePeriodMillis=0`), not periodic-polling driven.
- Auxio-TS should update widget state from playback/session progress and metadata events, not an independent polling loop.
- TS18/iLauncher likely expects a medium-sized music-control widget footprint, not only a minimal Android notification-like card.

## Widget layout findings

`res/layout/music_widget.xml` contains a horizontal widget roughly organised as:

```text
album art image
vertical metadata/control column:
  title single-line text
  artist single-line text
  prev/play-pause/next controls
  current time + duration row
  horizontal progress bar
```

Important IDs / behaviours:

```text
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

Widget dimension evidence:

```text
base widget container: tw_dp_w490 x tw_dp_h225
album art: tw_dp_w150 x tw_dp_w150
provider min: tw_dp_w430 x tw_dp_h200
sw768dp provider min: 424dp x 194dp
```

Interpretation for Auxio-TS:

- Widget parity requires visible title, artist/subtitle, album art, current time, duration, progress, and prev/play/next controls.
- A widget that only displays title/play state is insufficient for native parity.
- Progress/time/duration must be rendered in RemoteViews if Auxio-TS wants TWTHEME/iLauncher widget compatibility to be credible.
- Do not copy the exact layout/drawables, but implement equivalent fields with Auxio resources.

## MusicWidgetProvider findings

Readable JADX path:

```text
reference/firstparty-jadx/sources/com/p060tw/music/view/MusicWidgetProvider.java
```

Runtime smali path:

```text
app/apktool/smali_classes3/com/tw/music/view/MusicWidgetProvider.smali
```

Observed behaviours:

- `onUpdate()` renders widget immediately.
- `onUpdate()` starts `MusicService`.
- `onUpdate()` sends `com.tw.music.action.cmd` with `cmd=update` and `appWidgetIds`.
- Widget rendering writes title, artist, current time, duration, progress, album art or fallback.
- `RemoteViews.setOnClickPendingIntent()` wires:
  - album art -> `MusicActivity`
  - prev -> `MusicService` with `com.tw.music.action.prev`
  - play/pause -> `MusicService` with `com.tw.music.action.pp`
  - next -> `MusicService` with `com.tw.music.action.next`
- `AppWidgetManager.updateAppWidget()` is called for explicit appWidgetIds or provider component.

Implementation implications:

- Auxio-TS widget root/album-art tap should route to Now Playing.
- Auxio-TS widget prev/play/next should use deterministic distinct PendingIntent request codes.
- Auxio-TS must not show stale previous-track widget state after no-session/null-song.
- Auxio-TS should support a `cmd=update` style compatibility command if implementing the Topway bridge.
- Auxio-TS should not start long-running work inside AppWidgetProvider callbacks.

## MusicService command findings

Readable JADX path:

```text
reference/firstparty-jadx/sources/com/p060tw/music/MusicService.java
reference/firstparty-jadx/sources/com/p060tw/music/C0781k.java
```

Observed actions registered in service receiver:

```text
com.tw.music.action.cmd
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
```

Observed command extra values:

```text
cmd=prev
cmd=next
cmd=pp
cmd=update
appWidgetIds=int[]
```

Observed mapping:

```text
prev or action.prev -> previous
next or action.next -> next
pp or action.pp -> toggle play/pause
update -> widget refresh for supplied appWidgetIds
```

Implementation implications:

- Auxio-TS should support these incoming Topway command actions in an isolated bridge receiver/service path.
- Commands must be gated by Auxio session/focus/current-song safety.
- `pp` must not cause unsafe autoplay from an inert/no-current-song state.
- `cmd=update` should refresh Auxio widget state, not start external evidence/probe behaviour.

## Launcher seek findings

Readable JADX path:

```text
reference/firstparty-jadx/sources/com/p060tw/music/MusicActivity.java
reference/firstparty-jadx/sources/com/p060tw/music/C0780j.java
```

Observed behaviour:

- `MusicActivity.onCreate()` registers a receiver for `com.android.launcher.widget_music_progress`.
- Receiver reads `music_progress` extra.
- Receiver calls player `seekTo()`.

Implementation implications:

- Auxio-TS should implement a safe listener/receiver for `com.android.launcher.widget_music_progress`.
- Seek must be gated: only seek with active session/current song and valid duration.
- Clamp out-of-range input.
- Ignore missing/bad extras.
- Do not crash if launcher sends malformed data.

## Metadata broadcast findings

Readable JADX/vendor paths:

```text
reference/jadx-aliased/sources/com/eckom/xtlibrary/p066b/p069f/p041d/C0601U.java
reference/vendor-jadx/sources/com/eckom/xtlibrary/p020b/p037f/p041d/C0601U.java
similar variants: C0628t, C0610ba
```

Observed broadcast:

```text
Intent("com.tw.music.info")
putExtra("musicTitle", title)
putExtra("musicaArtist", artist)   # typo is real and must be preserved
putExtra("musicAlbum", album)
putExtra("musicPath", path)
sendBroadcast(intent)
```

Implementation implications:

- Auxio-TS should publish `com.tw.music.info` from a Topway bridge when current metadata changes.
- Preserve the `musicaArtist` spelling.
- Use the same metadata policy as MediaSession/widget/notification.
- Do not fake unavailable path values; use safest available URI/path equivalent.
- De-duplicate unchanged broadcasts where practical.

## Launcher progress/duration broadcast findings

Readable vendor/JADX paths:

```text
reference/vendor-jadx/sources/com/eckom/xtlibrary/p020b/p037f/p041d/C0602V.java
reference/jadx-aliased/sources/com/eckom/xtlibrary/p066b/p069f/p041d/C0622n.java
reference/jadx-aliased/sources/com/eckom/xtlibrary/p066b/p069f/p041d/C0587F.java
```

Observed broadcast:

```text
Intent("com.tw.launcher.music_progress_duration")
putExtra("msg_music_progress", currentPosition)
putExtra("msg_music_duration", duration)
sendBroadcast(intent)
```

Observed scheduling behaviour:

```text
handler sends delayed progress refresh roughly every 1000 ms while active
```

Implementation implications:

- Auxio-TS should publish progress/duration while playback/session is active.
- Throttle to around 1 second or reuse existing playback progress updates.
- Do not create a wakeful/background polling loop independent of playback service lifecycle.
- Stop/clear when playback stops or no session exists.

## Notification / MediaSession bridge findings

apktool smali shows `com/tw/music/media/MediaControlBridge.smali` and `MediaNotificationController.smali`:

- creates a `MediaSessionCompat` tagged `com.tw.music.MediaControlBridge`;
- sets media session flags;
- `MediaSessionCompat.Callback` dispatches play/pause/next/prev into Topway action broadcasts;
- notification actions also use Topway transport action strings and `cmd` extras;
- notification style uses `MediaStyle` and media session token.

Implementation implications:

- Auxio-TS already has its own MediaSession and notification stack; do not add a parallel stock-style session unless necessary.
- Use Topway evidence to ensure Auxio-TS MediaSession callback/actions, notification compact actions, and Topway bridge commands agree.
- Do not dispatch actions to package `com.tw.music` from Auxio-TS.
- Do not impersonate the stock package.

## XT / AIDL findings

Readable path:

```text
reference/jadx-aliased/sources/com/eckom/xtlibrary/p066b/C0556b.java
reference/jadx-aliased/sources/p060c/p063b/p064a/p065a/p018a/InterfaceC0516d.java
reference/jadx-aliased/sources/p060c/p063b/p064a/p065a/p018a/InterfaceC0514b.java
```

Observed behaviour:

```text
bindService to com.tw.service.xt/com.tw.service.xt.CommandService
action com.tw.service.xt.CommandService.Bind
AIDL descriptors:
  com.tw.service.xt.aidl.ITWCommandAidl
  com.tw.service.xt.aidl.IMusicCallBack
```

Implementation implications:

- This confirms a deeper native integration path exists.
- It is not safe as a production dependency in Auxio-TS without a separate design PR.
- Do not implement runtime binding in the current release-readiness pass.
- Document as future Tier 3/Tier 4 candidate only.

## Safe Auxio-TS compatibility target

Implement now:

```text
TopwayMusicContract
TopwayMusicBroadcastBridge
TopwayCommandReceiver or explicit service/receiver path
Topway launcher seek receiver/path
Topway progress/duration publisher
Widget RemoteViews parity improvements
Metadata policy unification across MediaSession/notification/widget/Topway broadcasts
Guardrail exception limited to isolated bridge package
```

Still do not implement:

```text
package=com.tw.music
sharedUserId=android.uid.system
platform signing
copied smali
XT AIDL runtime binding
vendor service dependency
private TWTHEME resource loader
runtime package scanner/probe
```
