# DoFun Variety compatibility

Auxio-TS is a TS18/Topway head-unit variant of Auxio that integrates with the DoFun Variety Theme launcher (`com.dofun.variety`) and serves as a drop-in replacement for the stock `twmusic` / `com.tw.music` music app.

## Why DoFun expects stock music identities

DoFun Variety's `assets/apps_match_config.json` contains a **fixed** `hotseat_app_music` entry:

```json
{
  "package_name": "com.tw.music",
  "class_name": "com.tw.music.MusicActivity"
}
```

A normal Auxio package (`org.oxycblt.auxio`) cannot win this fixed music hotseat slot. The `topwayTwMusic` product flavour provides the required identity.

## Variant matrix

| Variant | Package ID | Launcher activity | Purpose |
|---------|-----------|-------------------|---------|
| `standardRelease` | `org.oxycblt.auxio` | `org.oxycblt.auxio.MainActivity` | Normal Auxio-TS build |
| `standardDebug` | `org.oxycblt.auxio.debug` | `org.oxycblt.auxio.MainActivity` | Dev build |
| `topwayTwMusicRelease` | `com.tw.music` | `com.tw.music.MusicActivity` (alias) | DoFun-compatible release |
| `topwayTwMusicDebug` | `com.tw.music.debug` | `com.tw.music.MusicActivity` (alias) | DoFun-compatible debug |

## Activity alias

`app/src/topwayTwMusic/AndroidManifest.xml` declares `com.tw.music.MusicActivity` as an `activity-alias` targeting `org.oxycblt.auxio.MainActivity` with intent filters:

- `android.intent.action.MAIN`
- `android.intent.action.MUSIC_PLAYER`
- `android.intent.category.LAUNCHER`
- `android.intent.category.DEFAULT`
- `android.intent.category.APP_MUSIC`

## MediaBrowserService / MediaSession

The base manifest exports `android.media.browse.MediaBrowserService`. Standard Android MediaSession metadata is published for controllers including DoFun's notification listener path.

## Topway broadcast/control contract

**Outgoing (metadata + progress):**

| Action | Extras |
|--------|--------|
| `com.tw.music.info` | `musicTitle`, `musicaArtist`, `musicAlbum`, `musicPath` |
| `com.tw.launcher.music_progress_duration` | `msg_music_progress`, `msg_music_duration` |

**Incoming (controls):**

| Action | Extras |
|--------|--------|
| `com.tw.music.action.cmd` | `cmd` = `prev` / `next` / `pp` / `update` |
| `com.tw.music.action.prev` | — |
| `com.tw.music.action.next` | — |
| `com.tw.music.action.pp` | — |
| `com.android.launcher.widget_music_progress` | `music_progress` (seek) |

The bridge receiver (`TopwayMusicBridgeReceiver`) and contract (`TopwayMusicContract.kt`) live in `app/src/main/java/org/oxycblt/auxio/headunit/topway/`.

## Provider authority

Authorities follow the variant application ID:

| Variant | CoverProvider authority |
|---------|------------------------|
| `standardRelease` | `org.oxycblt.auxio.image.CoverProvider` |
| `topwayTwMusicRelease` | `com.tw.music.image.CoverProvider` |
| `topwayTwMusicDebug` | `com.tw.music.debug.image.CoverProvider` |

The manifest uses `${applicationId}.image.CoverProvider` placeholder.

## Known limitation: no fake Cardoor/RemoteMediaService

DoFun queries `cn.cardoor.libs.media.RemoteMediaService` but its binder protocol is unproven. Do not implement a fake service — it may cause DoFun to bind and fail harder than the MediaSession/notification fallback path.

## Validation commands

```sh
# Static check
bash scripts/check-dofun-topway-compat.sh

# Package resolution on device
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -p com.tw.music
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC -p com.tw.music
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
```
