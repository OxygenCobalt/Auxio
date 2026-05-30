# DoFun/Variety Topway runtime validation checklist

Use this checklist on a TS18/Topway head unit running the DoFun/Variety launcher/theme package `com.dofun.variety`. The build- and APK-manifest checks prove the compatibility APK shape, but launcher/widget recognition and control must be validated on real hardware before claiming full DoFun runtime parity.

## Test build under validation

| Field | Value |
| --- | --- |
| Auxio-TS commit SHA |  |
| APK file | `app/build/outputs/apk/topwayTwMusic/release/*.apk` |
| Expected package | `com.tw.music` |
| Expected launcher component | `com.tw.music/.MusicActivity` |
| Head unit model/firmware |  |
| DoFun/Variety package/version |  |
| Tester/date |  |

> Use `topwayTwMusicRelease` or another signed exact-package build for final DoFun identity validation. `topwayTwMusicDebug` installs as `com.tw.music.debug`, which is useful for development but is not the fixed stock package name DoFun's extracted config prefers.

## Pre-install package inventory

```sh
adb shell 'cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media|org\.oxycblt\.auxio|com\.dofun\.variety"'
```

Expected notes:

- `com.dofun.variety` should be installed.
- Existing stock `com.tw.music` or `com.tw.media` packages may win the music slot or cause signature conflicts.
- Do not disable or remove stock packages without explicit operator approval.

Result: PASS / FAIL / BLOCKED

Notes:

## Install exact-package compatibility APK

```sh
adb install -r app/build/outputs/apk/topwayTwMusic/release/*.apk
```

If installation fails with a signature conflict against stock `com.tw.music`, record the exact error and stop unless the operator explicitly approves a safe temporary package-disable/removal procedure.

Result: PASS / FAIL / BLOCKED

Notes:

## Package and component resolution

```sh
adb shell cmd package resolve-activity --brief com.tw.music
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
adb shell dumpsys package com.tw.music | grep -iE 'MusicActivity|MAIN|MUSIC_PLAYER|APP_MUSIC|LAUNCHER|MediaBrowserService|CoverProvider|com.tw.music.action|widget_music_progress'
```

Expected:

- `com.tw.music/.MusicActivity` resolves for the compatibility APK.
- The APP_MUSIC query includes `com.tw.music/.MusicActivity`.
- The media-browser-service query includes Auxio's `org.oxycblt.auxio.AuxioService` under package `com.tw.music`.
- `CoverProvider` authority is `com.tw.music.image.CoverProvider`.
- Topway command actions are registered on `TopwayMusicBridgeReceiver`.

Result: PASS / FAIL / BLOCKED

Notes:

## Media session and metadata exposure

Start Auxio-TS, grant required storage/media permissions, select a playable track, and start playback.

```sh
adb shell dumpsys media_session | grep -i -A60 'com.tw.music\|auxio'
adb shell logcat -c
adb shell logcat -v time | grep -iE 'Auxio|Topway|tw.music|music_progress|MediaSource|NotifyService|cardoor|dofun|variety|MediaSession|MediaBrowser'
```

Expected:

- A media session for the compatibility APK is active while playback is active.
- Track metadata and playback state update when the track changes or play/pause changes.
- Topway metadata/progress broadcasts are visible if the launcher or logs expose them.

Result: PASS / FAIL / BLOCKED

Notes:

## Widget-like control broadcasts

With playback loaded, exercise the public Topway actions:

```sh
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
```

Optional generic command-action form:

```sh
adb shell am broadcast -a com.tw.music.action.cmd --es cmd pp
adb shell am broadcast -a com.tw.music.action.cmd --es cmd next
adb shell am broadcast -a com.tw.music.action.cmd --es cmd prev
```

Optional seek/progress action if the launcher emits it:

```sh
adb shell am broadcast -a com.android.launcher.widget_music_progress --ei music_progress 30000
```

Expected:

- Play/pause toggles playback.
- Next/previous changes track when the queue supports it.
- Seek moves playback near the requested millisecond position when a valid queue item is loaded.
- No crash or tight broadcast loop appears in logcat.

Result: PASS / FAIL / BLOCKED

Notes:

## DoFun/Variety launcher widget validation

Use the launcher/theme UI after installing the exact-package compatibility APK.

| Check | Expected | Result | Notes |
| --- | --- | --- | --- |
| Music slot recognition | DoFun recognises `com.tw.music/.MusicActivity` or opens Auxio-TS from the music hotseat/panel. |  |  |
| Metadata display | Title, artist, album, and/or artwork update to the current Auxio track. |  |  |
| Progress display | Progress advances during playback and stops/clears on pause/stop as the widget expects. |  |  |
| Play/pause control | Widget play/pause controls Auxio playback. |  |  |
| Next/previous control | Widget next/previous controls Auxio playback. |  |  |
| Launcher restart | State recovers after force-stopping/restarting DoFun/Variety. |  |  |
| Auxio process restart | State recovers after Auxio process death/restart. |  |  |
| Reboot | Recognition and controls recover after reboot. |  |  |

## Evidence capture

Attach or archive:

- `adb shell dumpsys package com.tw.music`
- `adb shell dumpsys media_session`
- relevant logcat window
- screenshots/photos of DoFun widget recognition, metadata, progress, and controls
- exact APK filename and commit SHA
