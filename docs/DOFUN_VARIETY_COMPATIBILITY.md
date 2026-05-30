# Auxio-TS compatibility with DoFun/Variety launcher music widgets

## Implementation status

Implemented in source as a real Android product flavour named `topwayTwMusic`. The normal/standard flavour keeps the Auxio package identity, while the Topway/DoFun flavour builds a separate APK with `applicationId = com.tw.music` and exposes `com.tw.music.MusicActivity` as a launcher/music `activity-alias` targeting `org.oxycblt.auxio.MainActivity`.

Variant identities:

| Variant | Package/application ID | Launcher activity | Label | Purpose |
| --- | --- | --- | --- | --- |
| `standardDebug` / `standardRelease` | `org.oxycblt.auxio` (`.debug` for debug) | `org.oxycblt.auxio.MainActivity` | `Auxio` / `Auxio Debug` | Normal Auxio-TS build. |
| `topwayTwMusicRelease` | `com.tw.music` | `com.tw.music.MusicActivity` alias → `org.oxycblt.auxio.MainActivity` | `Music` | DoFun/Variety fixed stock-music identity compatibility. |
| `topwayTwMusicDebug` | `com.tw.music.debug` | `com.tw.music.debug/com.tw.music.MusicActivity` alias → `org.oxycblt.auxio.MainActivity` | `Music` | Debug validation build; release is the exact `com.tw.music` identity DoFun matching expects. |

The implementation deliberately does **not** add a fake `cn.cardoor.libs.media.RemoteMediaService`; the extracted APK evidence shows the action exists but does not establish the binder/service protocol.

Verification status from the current implementation pass:

| Area | Status | Evidence |
| --- | --- | --- |
| Source implementation | Implemented | `app/build.gradle`, the `topwayTwMusic` source set, and the isolated Topway bridge are wired in product source. |
| Build verification | Build-verified | `:app:assembleStandardDebug`, `:app:assembleTopwayTwMusicDebug`, and `:app:assembleTopwayTwMusicRelease` completed successfully in a prepared Android SDK/JDK 21 environment. |
| Merged-manifest verification | Manifest-verified | Generated merged manifests confirm standard debug remains `org.oxycblt.auxio.debug`, Topway debug is `com.tw.music.debug`, and Topway release is `com.tw.music`. |
| APK manifest verification | APK-verified | `apkanalyzer` confirmed the release APK application ID is `com.tw.music`, the alias is `com.tw.music.MusicActivity`, and the CoverProvider authority is `com.tw.music.image.CoverProvider`. |
| Device install | Pending TS18 validation | No Android/TS18 device was attached in the verification environment. Use `docs/DOFUN_VARIETY_RUNTIME_VALIDATION.md`. |
| DoFun widget recognition/control | Pending TS18 validation | Requires a head unit running `com.dofun.variety`; do not claim full runtime compatibility until the checklist passes. |

## Purpose

This document is a developer/agent guide for implementing Auxio-TS compatibility with the DoFun/Variety launcher/theme package `com.dofun.variety` on Topway/TS18-style Android head units.

The goal is for the launcher/theme music hotseat, music widget, or music panel to recognise Auxio-TS as the local music app and to control/display playback state as far as the observed public/legacy contracts allow.

This guide is based on:

- Auxio-TS source snapshot: `Auxio-TS-dev.zip`
- DoFun/Variety APK snapshot: `com.dofun.variety_V9.7.2.367.260312.apk`
- Decoded/extracted DoFun assets and manifests in `docs/topway-dofun-variety/reference/`

## High-confidence observations

### DoFun package identity

The target launcher/theme APK package is:

```text
com.dofun.variety
```

Do not refer to it as `com.run.variety` in code comments, docs, or implementation tasks unless a separate APK proves that package exists.

### DoFun music hotseat matching is stock-identity based

`assets/apps_match_config.json` contains two `hotseat_app_music` entries. The important one is fixed to stock Topway identities:

```json
{
  "soft_name": "hotseat_app_music",
  "icon_name": "link_icon_music",
  "compare_soft_name": "音乐,音樂,Music",
  "more_packages": [
    {
      "package_name": "com.tw.media",
      "class_name": "com.tw.music.MusicActivity"
    },
    {
      "package_name": "com.tw.music",
      "class_name": "com.tw.music.MusicActivity"
    }
  ],
  "function": "music_set",
  "behavior": [
    "fixed"
  ]
}
```

Supporting extracted files:

- `docs/topway-dofun-variety/reference/dofun-variety/apps_match_config.full.json`
- `docs/topway-dofun-variety/reference/dofun-variety/apps_match_config.music-excerpts.json`

Implication: a normal Auxio-TS package identity such as `org.oxycblt.auxio` may be a valid Android media app, but it is unlikely to win the DoFun/Variety fixed music hotseat slot. The compatibility APK should present the stock-compatible package/class identity.

### DoFun app icon mapping also recognises stock music identities

`assets/apps_config.json` includes music-related mappings for:

- `cn.cardoor.dofunmusic`
- `com.tw.music`
- `com.tw.media` + `com.tw.music.MusicActivity`
- `music_set`
- various third-party music apps such as Spotify, YouTube Music, QQ Music, NetEase Cloud Music

Supporting extracted files:

- `docs/topway-dofun-variety/reference/dofun-variety/apps_config.full.json`
- `docs/topway-dofun-variety/reference/dofun-variety/apps_config.music-excerpts.json`

Implication: `com.tw.music` is the cleanest compatibility target for a dedicated Auxio-TS Topway variant. `com.tw.media` is broader and may collide with stock video/media responsibilities.

### DoFun has a private Cardoor media service surface, but its binder contract is not established

The decoded DoFun manifest includes:

```xml
<queries>
  <intent>
    <action android:name="cn.cardoor.libs.media.RemoteMediaService" />
  </intent>
</queries>

<service android:name="cn.cardoor.libs.media.impl.MediaSourceService" android:exported="true">
  <intent-filter>
    <action android:name="cn.cardoor.libs.media.RemoteMediaService" />
  </intent-filter>
</service>
```

It also includes a notification listener:

```xml
<service android:name="cn.cardoor.basic.media.NotifyService"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
  <intent-filter>
    <action android:name="android.service.notification.NotificationListenerService" />
  </intent-filter>
</service>
```

Supporting extracted files:

- `docs/topway-dofun-variety/reference/dofun-variety/AndroidManifest.decoded.xml`
- `docs/topway-dofun-variety/reference/dofun-variety/AndroidManifest.filtered-relevant.xml`
- `docs/topway-dofun-variety/reference/dofun-variety/classes.string-hits.filtered.txt`

Implication: DoFun may obtain media state through its own service and notification-listener path, but the private `cn.cardoor.libs.media.RemoteMediaService` binder protocol is not proven. Do not implement a fake service with this action unless a concrete AIDL/binder contract is recovered and validated.

## Current Auxio-TS relevant state

Auxio-TS already exposes useful Android-standard and Topway-compatible surfaces:

- Main activity: `org.oxycblt.auxio.MainActivity`
- Standard music launcher intent filters:
  - `android.intent.action.MAIN`
  - `android.intent.action.MUSIC_PLAYER`
  - `android.intent.category.LAUNCHER`
  - `android.intent.category.DEFAULT`
  - `android.intent.category.APP_MUSIC`
- Exported `android.media.browse.MediaBrowserService`
- Exported media button receiver
- Exported Topway bridge receiver for:
  - `com.tw.music.action.cmd`
  - `com.tw.music.action.prev`
  - `com.tw.music.action.next`
  - `com.tw.music.action.pp`
  - `com.android.launcher.widget_music_progress`
- Outgoing Topway metadata/progress broadcasts:
  - `com.tw.music.info`
  - `com.tw.launcher.music_progress_duration`

Supporting Auxio references:

- `docs/topway-dofun-variety/reference/auxio/current-source/app__src__main__AndroidManifest.xml`
- `docs/topway-dofun-variety/reference/auxio/current-source/app__build.gradle`
- `docs/topway-dofun-variety/reference/auxio/topway-bridge/TopwayMusicContract.kt`

## Required implementation direction

Implement this in the Android app source as a real product flavour/source set, not as a CI-only mutation.

Recommended variant:

```text
flavour name: topwayTwMusic
applicationId: com.tw.music
launcher-compatible activity alias: com.tw.music.MusicActivity
label: Music
```

Do not replace the normal Auxio-TS APK. The standard APK should remain `org.oxycblt.auxio` or whatever the repo currently defines. The Topway-compatible package should be a separate build output and release asset.

## Implementation requirements

### 1. Add a dedicated product flavour

Add a flavour dimension and a `topwayTwMusic` product flavour in `app/build.gradle`.

Use the template in:

```text
docs/topway-dofun-variety/templates/topwayTwMusic/gradle/product-flavour-snippet.gradle
```

Expected Gradle behaviour:

```sh
./gradlew :app:assembleStandardDebug
./gradlew :app:assembleStandardRelease
./gradlew :app:assembleTopwayTwMusicDebug
./gradlew :app:assembleTopwayTwMusicRelease
```

Because the app now has a `distribution` flavour dimension, bare `:app:assembleDebug` assembles all debug variants. Use the explicit standard/topway task when validating one APK identity.

### 2. Add `com.tw.music.MusicActivity` as an activity alias

The compatibility variant should expose:

```text
com.tw.music/.MusicActivity
```

Use `activity-alias` targeting the real Auxio main activity rather than duplicating activity code.

Template:

```text
docs/topway-dofun-variety/templates/topwayTwMusic/app-src-topwayTwMusic/AndroidManifest.xml
```

The alias should include:

```xml
<action android:name="android.intent.action.MAIN" />
<action android:name="android.intent.action.MUSIC_PLAYER" />
<category android:name="android.intent.category.DEFAULT" />
<category android:name="android.intent.category.LAUNCHER" />
<category android:name="android.intent.category.APP_MUSIC" />
```

If duplicate launchers appear, inspect the merged manifest and decide whether the base `MainActivity` launcher filter needs a manifest-merger override in the flavour. Do not remove the standard APK launcher entry globally.

### 3. Keep app identity and provider authorities coherent

Current Auxio source uses `@string/pkg_authority_cover` for `CoverProvider` authority.

Current standard values:

```xml
<string name="pkg_authority_cover">org.oxycblt.auxio.image.CoverProvider</string>
```

Current debug values:

```xml
<string name="pkg_authority_cover">org.oxycblt.auxio.debug.image.CoverProvider</string>
```

For `topwayTwMusic`, provide a source-set string override equivalent to:

```xml
<string name="pkg_authority_cover">com.tw.music.image.CoverProvider</string>
```

If the variant also has a debug application ID suffix, ensure the debug authority matches the actual debug application ID, for example:

```xml
<string name="pkg_authority_cover">com.tw.music.debug.image.CoverProvider</string>
```

The production manifest uses the `${applicationId}.image.CoverProvider` placeholder, so the provider authority follows the actual variant application ID:

| Variant | CoverProvider authority |
| --- | --- |
| `standardRelease` | `org.oxycblt.auxio.image.CoverProvider` |
| `standardDebug` | `org.oxycblt.auxio.debug.image.CoverProvider` |
| `topwayTwMusicRelease` | `com.tw.music.image.CoverProvider` |
| `topwayTwMusicDebug` | `com.tw.music.debug.image.CoverProvider` |

The source-set string overrides remain as documented compatibility breadcrumbs, but runtime URI generation uses `BuildConfig.APPLICATION_ID` in `CoverProvider`, matching the manifest placeholder. Avoid stale hard-coded authorities.

### 4. Audit `BuildConfig.APPLICATION_ID` assumptions

Current `Auxio.kt` checks only official package IDs for logging tree behaviour:

```kotlin
if (
    BuildConfig.APPLICATION_ID != "org.oxycblt.auxio" &&
        BuildConfig.APPLICATION_ID != "org.oxycblt.auxio.debug"
) {
    Timber.plant(CopyleftNoticeTree())
} else if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}
```

This does not appear to block `com.tw.music`, but agents must audit all `BuildConfig.APPLICATION_ID` use before changing package identity. Confirm service actions, pending intents, shortcuts, authorities, and saved keys remain coherent.

### 5. Preserve and validate Topway broadcasts/actions

Do not remove the existing bridge unless replacing it with a verified better implementation.

Current outgoing contract:

```text
ACTION com.tw.music.info
  musicTitle
  musicaArtist
  musicAlbum
  musicPath

ACTION com.tw.launcher.music_progress_duration
  msg_music_progress
  msg_music_duration
```

Current incoming contract:

```text
ACTION com.tw.music.action.cmd
  cmd = prev | next | pp | update

ACTION com.tw.music.action.prev
ACTION com.tw.music.action.next
ACTION com.tw.music.action.pp
ACTION com.android.launcher.widget_music_progress
  music_progress
```

Relevant source snapshot:

```text
docs/topway-dofun-variety/reference/auxio/topway-bridge/
```

### 6. Do not fake the Cardoor private service yet

Do not add this to Auxio-TS without a verified binder/AIDL contract:

```xml
<service android:exported="true">
  <intent-filter>
    <action android:name="cn.cardoor.libs.media.RemoteMediaService" />
  </intent-filter>
</service>
```

Reason: DoFun’s APK exposes and queries this action, but the actual protocol is not established from the extracted evidence. A fake service may cause DoFun to bind to Auxio and then fail harder than the Android media/session fallback path.

## Release/CI requirements

CI builds and publishes the Topway-compatible APK as a separate artefact. Manual releases upload two signed APKs:

```text
Auxio-TS-vX.Y.Z-standard-release.apk
Auxio-TS-vX.Y.Z-topway-twmusic-release.apk
```

Do not mutate package names only inside GitHub Actions. GitHub Actions proves and packages the variant defined in source.

## Local validation commands

Build/install commands:

```sh
./gradlew :app:assembleStandardDebug
./gradlew :app:assembleTopwayTwMusicDebug
./gradlew :app:assembleTopwayTwMusicRelease
adb install -r app/build/outputs/apk/standard/debug/app-standard-debug.apk
adb install -r app/build/outputs/apk/topwayTwMusic/debug/app-topwayTwMusic-debug.apk
```

For exact DoFun fixed-identity validation, install the signed/release `topwayTwMusicRelease` APK so the package name is exactly `com.tw.music`; the debug APK package is `com.tw.music.debug` because of the global debug suffix. Use the operator checklist in `docs/DOFUN_VARIETY_RUNTIME_VALIDATION.md` for device/head-unit validation.

Before installing the compatibility build:

```sh
adb shell 'cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media|org\.oxycblt\.auxio|com\.dofun\.variety"'
adb shell 'cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC'
adb shell 'cmd package query-intent-services -a android.media.browse.MediaBrowserService'
```

After installing the compatibility build:

```sh
adb shell 'cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media|org\.oxycblt\.auxio|com\.dofun\.variety"'
adb shell cmd package resolve-activity --brief com.tw.music
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
adb shell dumpsys media_session | grep -i -A40 'com.tw.music\|auxio'
```

Runtime traffic watch:

```sh
adb shell logcat -c
adb shell logcat -v time | grep -iE 'Auxio|Topway|tw.music|music_progress|MediaSource|NotifyService|cardoor|dofun|variety'
```

Manual broadcast tests:

```sh
adb shell am broadcast -a com.tw.music.info \
  --es musicTitle "Auxio Test" \
  --es musicaArtist "Test Artist" \
  --es musicAlbum "Test Album" \
  --es musicPath "/sdcard/Music/test.mp3"

adb shell am broadcast -a com.tw.launcher.music_progress_duration \
  --el msg_music_progress 30000 \
  --el msg_music_duration 180000

adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
```

If stock packages win the launcher slot first, temporarily disable them only for validation:

```sh
adb shell pm disable-user --user 0 com.tw.music
adb shell pm disable-user --user 0 com.tw.media
```

Re-enable them after testing if they are still needed:

```sh
adb shell pm enable com.tw.music
adb shell pm enable com.tw.media
```

## Static verification helper

After implementation, use:

```sh
bash scripts/check-dofun-topway-compat.sh
```

The helper checks source-level expectations, runtime bridge wiring, variant authority configuration, release asset naming, and generated merged manifest/APK evidence when build outputs exist. It falls back to source checks with warnings when outputs are absent. It is also run by the head-unit safety job alongside `scripts/check-headunit-compat-safety.sh`.

## Agent finish criteria

An implementation PR should finish only after confirming:

1. Standard Auxio-TS package identity remains unchanged.
2. A dedicated Topway APK builds with `applicationId = com.tw.music`.
3. The variant exposes `com.tw.music.MusicActivity` as a launcher/music alias.
4. `MediaBrowserService` remains exported and queryable.
5. `CoverProvider` authorities match the variant application ID.
6. Existing Topway metadata/progress broadcasts and incoming widget controls still work.
7. DoFun/Cardoor private media service is not faked unless its protocol is proven.
8. GitHub Actions or release docs clearly publish the Topway APK separately from the normal APK.
9. The validation commands above are documented in the implementation PR summary.

## Known limitations

- The DoFun APK is protected/obfuscated with a stub application, so public assets/manifests/strings are stronger evidence than decompiled control flow here.
- DoFun’s private `cn.cardoor.libs.media.RemoteMediaService` protocol is not recovered in this bundle.
- Actual launcher-widget behaviour still requires TS18/head-unit runtime validation because DoFun may combine package matching, notification listening, media-session inspection, and private services. Track the manual results in `docs/DOFUN_VARIETY_RUNTIME_VALIDATION.md`.
