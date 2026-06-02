# DoFun Variety / Topway music compatibility

Auxio-TS targets TS18/Topway head units using DoFun Variety Theme (`com.dofun.variety`). The practical goal is for Auxio-TS to replace the stock `twmusic` / `com.tw.music` app contract so the DoFun launcher/theme music widget can recognise the app, open it, display playback state, and route controls.

This document is the concise compatibility guide. Raw reference snippets live in `docs/reference/ts18-apk/`.

## Primary compatibility contract

DoFun Variety's extracted `assets/apps_match_config.json` contains fixed music hotseat entries for:

```text
com.tw.media / com.tw.music.MusicActivity
com.tw.music / com.tw.music.MusicActivity
```

The clean Auxio-TS compatibility target is therefore a dedicated Topway/DoFun variant that exposes:

```text
application/package ID: com.tw.music
launcher/activity component: com.tw.music.MusicActivity
label: Music
```

A normal Auxio package identity such as `org.oxycblt.auxio` may be a valid Android media app, but it is unlikely to satisfy the fixed DoFun music hotseat contract.

## Variant expectations

| Build | Package/application ID | Launcher component | Purpose |
| --- | --- | --- | --- |
| Standard Auxio-TS | `org.oxycblt.auxio` | `org.oxycblt.auxio.MainActivity` | Development/upstream baseline |
| Standard debug | `org.oxycblt.auxio.debug` | `org.oxycblt.auxio.MainActivity` | Development/debug |
| Topway/DoFun release | `com.tw.music` | `com.tw.music.MusicActivity` alias to `org.oxycblt.auxio.MainActivity` | Exact DoFun fixed-identity compatibility APK |
| Topway/DoFun debug | normally `com.tw.music.debug` if debug suffix is kept | same alias | Development only; not a final DoFun identity proof |

Use a real Android product flavour/source set. Do not mutate package names only in CI.

## Required Android media surfaces

Keep Android-standard media integration intact:

- `MediaSession`
- `android.media.browse.MediaBrowserService`
- media notification
- media button handling
- metadata and playback state updates

DoFun may combine fixed package matching with notification listening, media session inspection, and private Cardoor services. The safe implementation starts with exact package/component identity plus Android-standard media surfaces plus the observed Topway broadcast bridge.

## Observed Topway public broadcast/control surface

Keep these constants centralised in an isolated bridge package such as:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/
```

Outgoing metadata:

```text
action: com.tw.music.info
extras: musicTitle, musicaArtist, musicAlbum, musicPath
```

Outgoing progress/duration:

```text
action: com.tw.launcher.music_progress_duration
extras: msg_music_progress, msg_music_duration
```

Incoming commands:

```text
com.tw.music.action.cmd
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
com.android.launcher.widget_music_progress
```

Known command/progress extras:

```text
cmd
appWidgetIds
music_progress
```

Preserve the misspelt `musicaArtist` extra because that is the observed stock-compatible spelling.

## Observed private surfaces that are not implementation approval

DoFun Variety references these private/Cardoor services:

```text
cn.cardoor.libs.media.RemoteMediaService
cn.cardoor.basic.media.NotifyService
cn.cardoor.libs.media.impl.MediaSourceService
```

Treat these as evidence only. Do not add a fake `RemoteMediaService` or bind to private Cardoor protocols unless a concrete binder/AIDL contract is recovered and approved.

The stock Topway music APK also contains private/system/vendor hooks such as:

```text
android.uid.system
android.tw.john.TWUtil
com.tw.service.xt.aidl.ITWCommandAidl
com.tw.service.xt.aidl.ITWCommandCallbackAidl
```

These must not be copied into Auxio-TS product code. Use Android-standard APIs and the isolated Topway bridge instead.


## Runtime fallback posture

Auxio-TS implements the highest-confidence DoFun/Topway fallbacks without copying private
vendor APIs:

- the release APK installs as `com.tw.music`;
- `com.tw.music.MusicActivity` aliases to Auxio's real activity;
- `com.tw.music.MusicService` is provided in the Topway flavour as a stock-name wrapper over
  Auxio's real media/browser/playback service;
- `com.tw.music.view.MusicWidgetProvider` is provided in the Topway flavour as a stock-name
  wrapper that forwards safe observed Topway widget/control broadcasts into Auxio's bridge;
- Topway metadata/progress broadcasts are always enabled in the real `com.tw.music` release
  variant, regardless of the head-unit UI layout preference;
- cold-start Topway play/pause commands restore saved playback state instead of being dropped
  when no current song is yet loaded in memory.

Auxio-TS still deliberately avoids fake `cn.cardoor.libs.media.RemoteMediaService`,
`android.tw.john.TWUtil`, and `com.tw.service.xt.aidl.*` implementations because the available
evidence identifies names but not a safe public protocol.

## Validation commands

Package/component checks:

```sh
adb shell 'cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media|org\.oxycblt\.auxio|com\.dofun\.variety"'
adb shell cmd package resolve-activity --brief com.tw.music
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
```

Media/session and widget traffic:

```sh
adb shell dumpsys media_session | grep -i -A60 'com.tw.music\|auxio'
adb shell logcat -v time | grep -iE 'Auxio|Topway|tw.music|music_progress|MediaSource|NotifyService|cardoor|dofun|variety|MediaSession|MediaBrowser'
```

Manual widget-command simulation:

```sh
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
```

Manual outgoing-state simulation:

```sh
adb shell am broadcast -a com.tw.music.info   --es musicTitle "Auxio Test"   --es musicaArtist "Test Artist"   --es musicAlbum "Test Album"   --es musicPath "/sdcard/Music/test.mp3"

adb shell am broadcast -a com.tw.launcher.music_progress_duration   --el msg_music_progress 30000   --el msg_music_duration 180000
```

## CI and safety checks

CI should protect:

- standard variant identity remains intact;
- Topway release variants install as exact `com.tw.music` and alternate `com.tw.media` identities;
- `com.tw.music.MusicActivity` alias exists;
- `MediaBrowserService` remains declared/exported as intended;
- provider authorities follow the variant application ID;
- Topway broadcast/action strings remain isolated;
- private/system/vendor hooks remain forbidden.

Use/update:

```sh
bash scripts/check-headunit-compat-safety.sh
```

Add or keep a more specific DoFun/Topway manifest/APK check when the flavour is implemented.

## Post-PR#53 exact-device hardening notes

**Evidence confidence:** Observed. **Porting decision:** Directly reusable requirement. The redacted `s9863a1h10` Android 10 profile confirms DoFun fixed entries for both `com.tw.music/com.tw.music.MusicActivity` and `com.tw.media/com.tw.music.MusicActivity`.

Auxio-TS now provides two Topway-compatible release identities:

| Variant | Package | DoFun component | Install constraint |
| --- | --- | --- | --- |
| `topwayTwMusicRelease` | `com.tw.music` | `com.tw.music/com.tw.music.MusicActivity` | Exact stock replacement; conflicts with stock system priv-app unless package state/signing is managed |
| `topwayTwMediaRelease` | `com.tw.media` | `com.tw.media/com.tw.music.MusicActivity` | Alternate DoFun fixed entry; not a universal no-root bypass and may conflict on some firmware |

Both variants reuse the same thin wrapper source set (`com.tw.music.MusicActivity`, `com.tw.music.MusicService`, and `com.tw.music.view.MusicWidgetProvider`) and delegate into Auxio-owned code. The wrapper exposes stock-compatible package/class/component names only; it does not add private Cardoor services, TWUtil reflection, vendor binders, system UID, `sharedUserId`, copied smali, or platform-signature requirements.

Topway-compatible variants use `com.tw.music.MusicService` as the canonical exported external MediaBrowserService. The base Auxio `org.oxycblt.auxio.AuxioService` remains available for explicit in-app starts, but its inherited browse/search intent filters are removed in the Topway wrapper manifest so external TS18/DoFun clients do not split across two service component names. **Evidence confidence:** Inferred from manifest design. **Porting decision:** Requires TS18 runtime validation. Runtime validation must still check for duplicate active sessions, duplicate foreground services, and duplicate lifecycle starts before claiming final TS18 parity.
