# TS18 APK reference summary

This file summarises the useful facts extracted from the attached DoFun Variety and stock Topway music APKs. Detailed snippets are in `docs/reference/ts18-apk/`.

## Source APKs

| APK | SHA-256 | Role |
| --- | --- | --- |
| `com.dofun.variety_V9.7.2.367.260312.apk` | `75e7ea9b46d68754253aa385e6ac750aae957a5b72196fec5449ccf2782c60b1` | DoFun launcher/theme widget target |
| `com.tw.music_TW_THEME.20240715.apk` | `4f5495e270a7c86bab232e2b7ee2ecd2d71f3450f6f20ed5f36feaa4229c1518` | Stock music app replacement contract |

## Development meaning

DoFun Variety is not just looking for any Android media app. Its extracted config contains fixed stock music identities:

```text
com.tw.media / com.tw.music.MusicActivity
com.tw.music / com.tw.music.MusicActivity
```

For Auxio-TS this means the important compatibility APK is a dedicated Topway build that installs as `com.tw.music` and exposes `com.tw.music.MusicActivity` as a launcher/music alias.

## Safe surfaces to mirror

Mirror only the observed public/safe surfaces:

- Android `MediaSession`
- Android `MediaBrowserService`
- Android notification/media controls
- isolated Topway broadcast/control bridge:
  - `com.tw.music.info`
  - `com.tw.launcher.music_progress_duration`
  - `com.tw.music.action.cmd`
  - `com.tw.music.action.prev`
  - `com.tw.music.action.next`
  - `com.tw.music.action.pp`
  - `com.android.launcher.widget_music_progress`

## Surfaces to avoid in product code

Do not port or depend on:

- `android.uid.system`
- `sharedUserId`
- `android.tw.john.TWUtil`
- `com.tw.service.xt.aidl.ITWCommandAidl`
- `com.tw.service.xt.aidl.ITWCommandCallbackAidl`
- fake `cn.cardoor.libs.media.RemoteMediaService`

These are observed in APKs but are not safe production dependencies for Auxio-TS.

## Files

```text
docs/reference/ts18-apk/reference-contracts.json
docs/reference/ts18-apk/dofun-variety/apps_match_config.music-excerpts.json
docs/reference/ts18-apk/dofun-variety/apps_config.music-excerpts.json
docs/reference/ts18-apk/dofun-variety/manifest.string-hits.txt
docs/reference/ts18-apk/twmusic/manifest.string-hits.txt
docs/reference/ts18-apk/twmusic/classes.string-hits.txt
docs/reference/ts18-apk/SHA256SUMS.txt
```
