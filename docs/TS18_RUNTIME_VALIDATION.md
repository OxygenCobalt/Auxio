# TS18 runtime validation checklist

Practical on-device checklist for validating Auxio-TS on a TS18/Topway head unit running DoFun Variety (`com.dofun.variety`). Static scripts, Gradle builds, emulator tests, and Roborazzi screenshots are useful pre-flight evidence, but do **not** prove full DoFun launcher/widget parity without real TS18 hardware validation.

## Test build under validation

| Field | Value |
|-------|-------|
| Auxio-TS commit SHA |  |
| APK file | `app/build/outputs/apk/topwayTwMusic/release/app-topwayTwMusic-release.apk` |
| Expected package | `com.tw.music` |
| Expected launcher component | `com.tw.music/.MusicActivity` |
| Head unit model/firmware |  |
| DoFun/Variety package/version |  |
| Tester/date |  |

Use `topwayTwMusicRelease` or another signed exact-package build for final DoFun identity validation. `topwayTwMusicDebug` installs as `com.tw.music.debug`, which is useful for development but is not DoFun's fixed stock package target.

## Pre-install checks

```sh
# Check for existing stock music package
adb shell cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media"

# Check DoFun is present
adb shell cmd package list packages | grep com.dofun.variety
```

If stock `com.tw.music` is present, it may win the music slot or cause a signature conflict. Do not disable or remove stock packages without explicit operator approval. With approval, a temporary disable command is:

```sh
adb shell pm disable-user --user 0 com.tw.music
```

## Install

```sh
adb install -r app/build/outputs/apk/topwayTwMusic/release/app-topwayTwMusic-release.apk
```

## Package resolution checks

```sh
# Confirm com.tw.music resolves to Auxio-TS
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -p com.tw.music
# Expected: com.tw.music/com.tw.music.MusicActivity

# APP_MUSIC category
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC -p com.tw.music
# Expected: com.tw.music/com.tw.music.MusicActivity

# Manifest/service/provider detail
adb shell dumpsys package com.tw.music | grep -iE 'MusicActivity|MAIN|MUSIC_PLAYER|APP_MUSIC|LAUNCHER|MediaBrowserService|CoverProvider|com.tw.music.action|widget_music_progress'
# Expected: MediaBrowserService, com.tw.music.image.CoverProvider, and Topway receiver actions are present
```

## Media browser check

```sh
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
# Expected: com.tw.music listed
```

## Media session check

```sh
adb shell dumpsys media_session | grep -i -A60 'com.tw.music\|auxio'
# Expected: active session with metadata when playing

adb shell dumpsys audio | grep -iE 'com.tw.music|auxio|focus|USAGE_MEDIA|STREAM_MUSIC'
# Expected: normal media audio focus while playing
```

## DoFun widget recognition

| Step | Expected |
|------|----------|
| Open DoFun launcher music panel/widget | Auxio-TS icon appears in music hotseat |
| Tap music hotseat icon | Auxio-TS opens (com.tw.music.MusicActivity) |
| Play a track in Auxio-TS | Widget shows track title/artist |

## Widget playback controls

| Action | Expected |
|--------|----------|
| Tap play/pause on widget | Playback toggles |
| Tap next on widget | Next track plays |
| Tap prev on widget | Previous track plays |
| Seek via widget progress bar | Playback position changes |

## Broadcast verification

```sh
# Watch Topway broadcasts
adb shell logcat -v time | grep -iE 'tw.music|music_progress|dofun|variety'

# Manual control tests
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev

# Generic command-action form
adb shell am broadcast -a com.tw.music.action.cmd --es cmd pp
adb shell am broadcast -a com.tw.music.action.cmd --es cmd next
adb shell am broadcast -a com.tw.music.action.cmd --es cmd prev

# Optional seek/progress action if the launcher emits it
adb shell am broadcast -a com.android.launcher.widget_music_progress --ei music_progress 30000
```

## Process restart check

```sh
adb shell am force-stop com.tw.music
# Reopen from DoFun widget — app should restart and resume
```

## Launcher restart check

```sh
adb shell am force-stop com.dofun.variety
# Wait for launcher to restart — music widget should still show Auxio-TS state
```

## Reboot check

Reboot the head unit. After boot:
- DoFun music widget should still recognise Auxio-TS
- Tapping the music hotseat should open Auxio-TS
- Previous playback state should be recoverable

## Evidence to retain outside git

Keep runtime evidence outside the repository unless a maintainer explicitly asks for a small, redacted fixture. Useful artifacts are:

- `adb shell dumpsys package com.tw.music`
- `adb shell dumpsys media_session`
- filtered logcat covering DoFun widget recognition, controls, and playback state
- screenshots/photos of DoFun widget recognition, metadata, progress, and controls
- exact APK filename, commit SHA, head-unit firmware, and DoFun version

## Pass/fail summary

| Check | Pass | Fail | Notes |
|-------|------|------|-------|
| Package resolves as com.tw.music | ☐ | ☐ | |
| APP_MUSIC intent resolves | ☐ | ☐ | |
| MediaBrowserService listed | ☐ | ☐ | |
| MediaSession active during playback | ☐ | ☐ | |
| DoFun widget shows Auxio-TS | ☐ | ☐ | |
| Widget controls work | ☐ | ☐ | |
| Broadcast commands work | ☐ | ☐ | |
| Process restart recovers | ☐ | ☐ | |
| Launcher restart stable | ☐ | ☐ | |
| Reboot stable | ☐ | ☐ | |
