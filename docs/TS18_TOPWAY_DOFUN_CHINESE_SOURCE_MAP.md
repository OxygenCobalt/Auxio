# TS18 / Topway / DoFun Chinese Source Map for Auxio-TS Developers and Agents

**Intended repo path:** `docs/research/TS18_TOPWAY_DOFUN_CHINESE_SOURCE_MAP.md`  
**Prepared for:** Auxio-TS TS18 / Topway / DoFun integration work  
**Research date:** 2026-06-02  
**Primary language focus:** Chinese-language and Chinese-developer sources, with official Android docs used as implementation authority  
**Target device context:** TS18 / Topway `s9863a1h10_Natv` / `s9863a1h10`, Android 10 / SDK 29, DoFun Variety / 百变主题 `com.dofun.variety`

---

## 1. Purpose

This document is a source map and implementation guide for developers and AI agents working on Auxio-TS compatibility with TS18 / Topway / DoFun / 百变主题 head units.

It is designed to help agents search Chinese and international sources efficiently, without repeatedly rediscovering the same public facts or drifting into unsafe/private implementation work.

Auxio-TS should remain an Auxio-owned music app with carefully isolated Topway/DoFun compatibility wrappers. The correct engineering direction is:

1. Use official Android media/car/storage/overlay contracts as the safe base layer.
2. Use Topway/DoFun package/component compatibility only as a thin wrapper/facade.
3. Use public Topway-style broadcasts only where evidenced and isolated.
4. Treat private/native classes, vendor AIDL, Cardoor services, and `TWUtil` hardware APIs as evidence-gated only unless a real recoverable contract is available.
5. Validate anything DoFun-specific on the physical TS18 before claiming success.

---

## 2. Fixed Auxio-TS target facts to preserve

Do not contradict these facts in docs, CI checks, release notes, prompts, or code comments:

| Area | Fact |
|---|---|
| Device family | TS18 / Topway `s9863a1h10_Natv` / `s9863a1h10` |
| Runtime | Android 10 / SDK 29 |
| Display | 1280 x 720 landscape |
| Insets | top status bar about 55 px; right navigation bar about 55 px |
| Theme / launcher | DoFun Variety / 百变主题, package `com.dofun.variety` |
| Stock music package | `com.tw.music` |
| Stock music app state | system priv-app; normal user-signed APK cannot be assumed to replace it |
| App-only shell context | TermOne / Termux-style normal app UID, not ADB shell/root |
| ADB limitation | no observed local/TCP ADB listener; USB/OTG ADB physically unavailable in the user setup |
| USB music storage | `/storage/usbdisk0` exists and must be validated |
| DoFun fixed entries | `com.tw.music/com.tw.music.MusicActivity`; `com.tw.media/com.tw.music.MusicActivity` |

---

## 3. Implementation policy for agents

### 3.1 Production-safe surfaces

These are acceptable production directions:

- `MediaBrowserService` / `MediaLibraryService` and `MediaSession` as the real playback integration layer.
- Android media metadata, playback state, transport controls, media buttons, notification controls, and audio focus.
- Thin `com.tw.music` / `com.tw.media` package and component wrappers that delegate into Auxio-owned code.
- Public/intended Topway-style broadcast actions when isolated, sanitised, tested, and documented.
- Android `TYPE_APPLICATION_OVERLAY` for optional floating controls, with Android 10-safe foreground-service handling.
- MediaStore, removable media volume queries, and SAF/folder-selection fallback for USB/UDisk access.
- CI/static manifest checks for package IDs, aliases, exported components, provider authorities, and forbidden vendor/private imports.

### 3.2 Evidence-gated only

Do not add product code for these without recovered, device-specific contracts and a deliberate review:

- `cn.cardoor.libs.media.RemoteMediaService`
- `cn.cardoor.basic.media.NotifyService`
- `cn.cardoor.libs.media.impl.MediaSourceService`
- `android.tw.john.TWUtil`
- `android.tw.john.TWClient`
- `com.tw.service.xt.aidl.*`
- copied smali from stock apps
- platform signing assumptions
- `android.uid.system`
- `sharedUserId`
- fake vendor binders
- fake Cardoor services
- broad private/native integration

### 3.3 Licence rule

Many Chinese Android app repositories use GPL, AGPL, or unclear licences. Use them for behavioural/source-map research unless the licence is verified and compatible. Prefer reimplementation of patterns in Auxio-TS style over copying code.

---

## 4. Primary Chinese search terms / 中文检索词

Use these exact terms in GitHub, Gitee, GitCode, CSDN, Juejin, Bilibili, 52pojie, XDA, 4PDA, Telegram, and search engines.

### 4.1 TS18 / Topway / DoFun

```text
Topway TS18 com.tw.music
TS18 com.tw.music MusicActivity
TS18 com.tw.media MusicActivity
TS18 百变主题 com.dofun.variety
百变主题 车机 com.dofun.variety
车机 百变主题 音乐控件
车机 百变主题 音乐小部件
车机 DoFun Variety 音乐
Topway UIS8581A s9863a1h10
UIS8581A s9863a1h10 车机
TS18 s9863a1h10_Natv
TS18 s9863a1h10 Android 10
Topway TS18 固件
Topway TS18 dump
Topway TS18 firmware
```

### 4.2 Launcher / widget / media integration

```text
车机 Launcher Android 源码
车载 Launcher Android 源码
安卓车机 Launcher 音乐控件
安卓车机 音乐小部件
车机 MediaSession
车机 MediaBrowserService
车机 音乐控件 broadcast
车机 launcher 音乐广播
车机 桌面 音乐 widget
车载桌面 音乐控件 Android
Android Automotive CarLauncher 源码解析
Android 车载应用开发与分析 CarLauncher
APP_MUSIC MUSIC_PLAYER Android
```

### 4.3 Topway broadcast/action strings

```text
com.tw.music.action.cmd
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
com.tw.music.info
com.tw.launcher.music_progress_duration
com.android.launcher.widget_music_progress
musicTitle musicaArtist musicAlbum musicPath
msg_music_progress msg_music_duration
```

### 4.4 Private/native surfaces — evidence only

```text
cn.cardoor.libs.media.RemoteMediaService
cn.cardoor.basic.media.NotifyService
cn.cardoor.libs.media.impl.MediaSourceService
android.tw.john.TWUtil
android.tw.john.TWClient
com.tw.service.xt.aidl.ITWCommandAidl
com.tw.service.xt.aidl.ITWCommandCallbackAidl
com.tw.service.xt.aidl
```

### 4.5 Storage / USB / UDisk

```text
安卓车机 U盘 音乐 扫描
安卓车机 USB 音乐 扫描
Android 车机 U盘 MediaStore
Android 10 车机 U盘 权限
安卓11车机 本地音乐 扫描不到
/storage/usbdisk0 Android
Storage Access Framework U盘 音乐 Android
ACTION_OPEN_DOCUMENT_TREE U盘 Android
```

---

## 5. Official Android implementation authority

These sources should override forum posts, APK mirrors, and speculative reverse-engineering notes.

| Area | Source URL | Why it matters for Auxio-TS |
|---|---|---|
| Android media apps for cars | https://developer.android.com/training/cars/media | Establishes `MediaBrowserService` + `MediaSession` as the primary car media contract. Auxio-TS should integrate through this first, not through private Topway binders. |
| Android for Cars media design | https://developers.google.com/cars/design/create-apps/app-types/media | Useful for UX expectations on in-car media browsing/playback. |
| Testing Android apps for cars | https://developer.android.com/training/cars/testing | Useful for MediaBrowserService startup scenarios and car-specific validation thinking. |
| Legacy MediaBrowserService guide | https://developer.android.com/media/legacy/audio/mediabrowserservice | Shows service declaration, `onGetRoot`, `onLoadChildren`, session initialisation, and browser client behaviour. Useful if Auxio-TS is still on compat/legacy APIs. |
| Shared media / MediaStore | https://developer.android.com/training/data-storage/shared/media | Primary storage contract for local audio and external storage volumes. Check `MediaStore.getExternalVolumeNames()` and audio collections. |
| Storage Access Framework | https://developer.android.com/training/data-storage/shared/documents-files | User-controlled fallback for USB/UDisk folders if MediaStore misses `/storage/usbdisk0`. |
| All-files access | https://developer.android.com/training/data-storage/manage-all-files | Broad access exists but should not be default; use only as deliberate privileged/head-unit lane if ever justified. |
| Android 10 storage/privacy | https://developer.android.com/about/versions/10/privacy/changes | Confirms Android 10 scoped-storage constraints. Useful for `requestLegacyExternalStorage` discussion. |
| Android 11 storage changes | https://developer.android.com/about/versions/11/privacy/storage | Useful because modern target SDKs may face Android 11+ storage assumptions even on Android 10-era code paths. |
| `TYPE_APPLICATION_OVERLAY` | https://developer.android.com/reference/android/view/WindowManager.LayoutParams#TYPE_APPLICATION_OVERLAY | Correct non-system overlay window type for floating car controls on API 26+. |
| Foreground service types | https://developer.android.com/develop/background-work/services/fgs/service-types | `specialUse` and service-type requirements are Android 14+ concerns; code must be API-gated for TS18 Android 10. |
| Android AIDL | https://developer.android.com/develop/background-work/services/aidl | Use only for understanding IPC shape; do not invent vendor AIDL contracts. |
| Source Android dynamic AIDL | https://source.android.com/docs/core/architecture/aidl/dynamic-aidl | Useful for platform/native service context. Not directly applicable to normal APK product code on TS18. |
| Shizuku setup | https://shizuku.rikka.app/guide/setup/ | Privileged package-management lane only. Android 10 unrooted startup needs computer ADB; wireless debugging is Android 11+. |

Chinese-localised official docs can often be reached by adding `?hl=zh-cn` or `?hl=zh-tw` to Android Developers / Source Android URLs. Prefer the English canonical page when exact technical wording matters, then include the Chinese URL for agent searchability.

Examples:

```text
https://developer.android.com/training/cars/media?hl=zh-cn
https://developer.android.com/training/data-storage/shared/media?hl=zh-cn
https://developer.android.com/training/data-storage/shared/documents-files?hl=zh-cn
https://developer.android.com/develop/background-work/services/fgs/service-types?hl=zh-cn
https://developer.android.com/develop/background-work/services/aidl?hl=zh-cn
https://source.android.com/docs/core/architecture/aidl/dynamic-aidl?hl=zh-cn
```

---

## 6. AOSP / Android Automotive launcher sources

### 6.1 AOSP Car Launcher

- URL: https://android.googlesource.com/platform/packages/apps/Car/Launcher/
- Chinese search term: `Android Automotive CarLauncher 源码`
- Usefulness: High for understanding Android Automotive launcher/app-grid structure, but not proof of DoFun behaviour.
- Auxio-TS decision:
  - Use as architectural reference for launcher categories, app-grid assumptions, media app discovery, and car launcher lifecycle.
  - Do not assume DoFun implements AAOS behaviour exactly.
  - Still validate `com.tw.music/com.tw.music.MusicActivity` and `com.tw.media/com.tw.music.MusicActivity` on the real launcher.

### 6.2 linxu-link / CarLauncher

- URL: https://github.com/linxu-link/CarLauncher
- Chinese label: `Android 车载应用开发与分析（CarLauncher）`
- Notes:
  - The README states it reorganises AOSP Android 11 `CarLauncher` source for Android Studio reading.
  - It is explicitly described as read/reference-oriented because the original source has system dependencies.
- Usefulness: Medium/high for Chinese-language developer orientation.
- Auxio-TS decision:
  - Good for agents unfamiliar with Android Automotive launcher source layout.
  - Use for search terms and static understanding, not direct code copying.

### 6.3 CSDN / linkwj CarLauncher analysis

- URL: https://blog.csdn.net/linkwj/article/details/122381035
- Chinese title: `Android 车载应用开发与分析（5） - CarLauncher（一）`
- Notes:
  - Discusses Android 11 `CarLauncher`, references `android-11.0.0_r43`, and points to `packages/apps/Car/Launcher`.
- Usefulness: Medium as a Chinese explainer.
- Auxio-TS decision:
  - Good for agents to understand Chinese terminology around 车载桌面 / 车机桌面 / CarLauncher.
  - Treat as secondary to AOSP source.

### 6.4 lianghuiyong / Android-CarLauncher

- URL: https://github.com/lianghuiyong/Android-CarLauncher
- Chinese label: `车载 Launcher`
- Usefulness: Medium.
- What to inspect:
  - How it structures launcher UI for car-sized screens.
  - How app icons/shortcuts are exposed.
  - Any hard-coded app/component conventions.
- Auxio-TS decision:
  - Useful for practical launcher layout/component assumptions.
  - Not DoFun proof.

---

## 7. DoFun Variety / 百变主题 source map

DoFun/百变主题 appears mainly through APK mirrors and device/forum references rather than official open source. Treat mirror pages as weak evidence for package naming/versioning only.

### 7.1 7723 mirror

- URL: https://3g.7723.cn/ups/1557838
- Chinese name: `百变主题`
- Package shown: `com.dofun.variety`
- Version shown on that page: `V7.5.2.127.221208`
- Source quality: Low/medium. APK mirror, not official docs.
- Auxio-TS decision:
  - Use only to confirm public package-name traces and Chinese app name.
  - Do not rely on APK mirror descriptions for behaviour.
  - Do not download or redistribute APKs from mirror sources in the repo.

### 7.2 Ali213 mirror

- URL: https://m.ali213.net/aznew/680837.html
- Chinese name: `百变主题车机版`
- Package shown: `com.dofun.variety`
- Developer shown on page: `深圳驾控科技有限公司`
- Source quality: Low/medium. APK mirror, not authoritative firmware docs.
- Auxio-TS decision:
  - Useful as search anchor: `百变主题车机版`, `深圳驾控科技有限公司`, `com.dofun.variety`.
  - Do not infer launcher internals from marketing text.

### 7.3 XDA / 4PDA references

- XDA TS10/TS18 threads often mention `com.dofun.variety` and Topway packages in firmware/app dumps.
- Useful search URLs:
  - https://xdaforums.com/t/new-topway-ts10-uis7862-6gb-ram-128gb-head-unit-q-as.4227947/
  - https://xdaforums.com/t/ts10-18-unit-sprd-qp1a-uis8581a-non-fyt-eq-problem.4666568/
  - https://4pda.to/forum/index.php?showtopic=1015856
- Source quality: Medium for field reports, weak for exact API contracts.
- Auxio-TS decision:
  - Use to compare firmware families, package lists, and failure reports.
  - Never treat forum snippets as sufficient evidence for production private/native code.

---

## 8. Topway / TS18 / UIS8581 / s9863a1h10 firmware and device-family references

### 8.1 mkotyk / topwaytool

- URL: https://github.com/mkotyk/topwaytool
- Description: Tool for managing Android OS images on TopWay car head units; creates/extracts signed image files for some TopWay head units.
- Licence: Apache-2.0.
- Source quality: Medium/high for firmware-image workflow context; not an APK runtime API source.
- Auxio-TS decision:
  - Use to support documentation that stock/system app replacement belongs to root/system-image/firmware lanes, not normal TermOne app context.
  - Do not add topwaytool workflows to app CI unless explicitly building firmware artifacts, which Auxio-TS should not do by default.

### 8.2 TS18 firmware dump listing / Bazadampov

- URL: https://bazadampov.ru/dampy-avtomagnitol/topway-ts18-a125mad-v6-4-64-ts18-cr125lp4-v5
- Useful facts visible on page:
  - `Vendor = sprd`
  - `Model = s9863a1h10_Natv`
  - `Android Version = 10`
  - `SDK version = 29`
  - `Product Hardware = uis8581a2h10`
  - resolution listed as 1280 x 720 in page details
- Source quality: Medium for device-family corroboration; commercial firmware dump site.
- Auxio-TS decision:
  - Can corroborate the TS18 / SPRD / Android 10 / SDK 29 / UIS8581A family.
  - Do not use firmware dumps or proprietary files in the repo.

### 8.3 XDA TS10/TS18 UIS8581A thread

- URL: https://xdaforums.com/t/ts10-18-unit-sprd-qp1a-uis8581a-non-fyt-eq-problem.4666568/
- Useful facts:
  - Public field reports mention TS18 / SPRD / QP1A / UIS8581A / Android API 29 patterns.
- Source quality: Medium for troubleshooting context.
- Auxio-TS decision:
  - Use for search expansion and runtime validation expectations.
  - Not authoritative enough for product code assumptions.

### 8.4 TWRP device-tree generator / s9863a1h10

- Search/source URL: https://t.me/s/twrpdtgen?before=890
- Useful fact: public device-tree generator references include `Codename: s9863a1h10` and generated device tree links.
- Source quality: Medium/low; useful for platform sanity only.
- Auxio-TS decision:
  - Use as confirmation that `s9863a1h10` is a real Android device-tree family term.
  - Do not use generated device trees as app compatibility proof.

### 8.5 Linux kernel SP9863A reference

- URL: https://github.com/torvalds/linux/blob/master/arch/arm64/boot/dts/sprd/sp9863a-1h10.dts
- Source quality: High for SoC/platform lineage, low for app behaviour.
- Auxio-TS decision:
  - Useful only to understand Spreadtrum/Unisoc naming.
  - No direct app integration implications.

---

## 9. Chinese and Chinese-adjacent Android music-player references

These are useful for source-mining UX, local library scanning, lyrics, widgets, notifications, Android Auto, and release workflows. They are not Topway/DoFun proof.

### 9.1 Moriafly / SaltPlayerSource — 椒盐音乐

- URL: https://github.com/Moriafly/SaltPlayerSource
- Chinese name: `椒盐音乐`
- Description: Local music playback app; repository used for releases, feedback, and announcements.
- Licence shown: MIT.
- Useful issue: Android head-unit local music scanning problem.
  - URL: https://github.com/Moriafly/SaltPlayerSource/issues/816
  - Chinese issue: `在安卓车机安装后，无法正常扫描本地音乐文件和识别歌词`
- Auxio-TS decision:
  - Good for Chinese user reports about car-head-unit storage scanning and lyrics limitations.
  - The repository is not necessarily full app source, so use mainly for issue evidence and UX expectations.

### 9.2 pure-music / PureMusic — 棉花音乐

- URL: https://github.com/pure-music/PureMusic
- Chinese name: `棉花音乐`
- Description: Player for local files, Drive, Navidrome, Subsonic, Emby, Jellyfin, Plex; supports Android, iOS, and PC.
- Usefulness: Medium for local-file and multi-backend music-player behaviour.
- Auxio-TS decision:
  - Inspect for Android local-file UX patterns, release packaging, and cross-platform storage abstractions.
  - Check licence before copying any implementation.

### 9.3 maotoumao / MusicFree — 猫头猫 / MusicFree

- URL: https://github.com/maotoumao/MusicFree
- Chinese description: `一个插件化、定制化、无广告的免费音乐播放器，目前只支持 Android 和 Harmony OS。`
- Licence: AGPL-3.0.
- Notes:
  - Strong warning in README against fake/paid/cracked copies.
  - Has plugin and Android/Harmony OS structure.
- Auxio-TS decision:
  - Good for Chinese Android release/docs/plugin ecosystem thinking.
  - AGPL means do not copy code into Auxio-TS unless licence implications are deliberately accepted.

### 9.4 lovegaoshi / azusa-player-mobile — 电梓播放器手机版

- URL: https://github.com/lovegaoshi/azusa-player-mobile
- Chinese description: Bilibili third-party audio player.
- Claimed features in README include local songs on Android, Android Auto / CarPlay support, FFmpeg processing, cross-platform code, GitHub Actions builds.
- Licence: AGPL-3.0.
- Auxio-TS decision:
  - Useful for Android Auto/CarPlay and head-unit-ish feature search terms.
  - AGPL: pattern-mining only unless licence compatibility is consciously handled.

### 9.5 wangchenyan / ponymusic — 波尼音乐

- URL: https://github.com/wangchenyan/ponymusic
- Description: Android online music player like NetEase Cloud Music, based on Media3 and ExoPlayer.
- Chinese articles linked in README cover music player basics, widget, lyric scrolling, and online list loading.
- Auxio-TS decision:
  - Useful for Media3/ExoPlayer and Chinese tutorial references.
  - Less directly useful for local USB library or Topway compatibility.

### 9.6 Chinese FOSS Android app list

- URL: https://github.com/ivon852/awesome-foss-android-apps
- Chinese/traditional list of open-source Android apps, including music players such as VLC, mpv-android, Metro, Fossify Music Player.
- Auxio-TS decision:
  - Useful for Chinese-language discovery of equivalent open-source Android media apps.
  - Use it as an index, not implementation authority.

### 9.7 Upstream Auxio

- URL: https://github.com/OxygenCobalt/Auxio
- Description: Local music player built around modern media playback libraries.
- Auxio-TS decision:
  - Upstream reference for architecture and regressions.
  - TS18 compatibility must remain isolated and should not damage upstream-like standard behaviour.

---

## 10. Mature non-Chinese Android media references worth keeping in the source map

Even though this document prioritises Chinese sources, the following repos are stronger engineering references for Android media architecture and local/removable storage:

| Repo | URL | Use for |
|---|---|---|
| VLC Android | https://github.com/videolan/vlc-android | Mature local/removable media handling, playback service, notifications, formats. Licence-sensitive. |
| AntennaPod | https://github.com/AntennaPod/AntennaPod | Mature MediaSession, notifications, lifecycle, queue/recovery, Android Auto style behaviour. |
| UAMP | https://github.com/android/uamp | Google sample; archived but still useful for historical Android media app patterns. |
| Retro Music Player | https://github.com/RetroMusicPlayer/RetroMusicPlayer | Local music app, widgets, Android Auto, notification controls; licence-sensitive. |
| Shuttle | https://github.com/timusus/Shuttle | Older local music app; useful only as historical pattern reference. |
| Fossify Music Player | https://github.com/FossifyOrg/Music-Player | Simple local music player and issue trail around Android Auto support. |

---

## 11. Private/native / TWUtil findings and caution list

### 11.1 `android.tw.john.TWUtil` public traces

There are public repositories and forum posts that reference `android.tw.john.TWUtil`, but they do not provide a safe universal Topway/TS18 contract.

#### asb72 / dvd-bt

- URL: https://github.com/asb72/dvd-bt/blob/master/app/src/main/java/com/tw/bt/twUtil.java
- Observed pattern:
  - Imports `android.tw.john.TWUtil`.
  - Extends `TWUtil`.
  - Calls `open(...)`, `start()`, `write(...)`, `stop()`, and `close()`.
- Auxio-TS decision:
  - Evidence that some Topway-ish firmware exposes `android.tw.john.TWUtil` to privileged/system-like apps.
  - Not enough to implement in Auxio-TS product code.
  - If ever explored, keep in a diagnostics-only branch/probe, not release APK.

#### ivvlev / CarRadio

- URL: https://github.com/ivvlev/CarRadio
- Notes from README:
  - Firmware must contain `android.tw.john.TWUtil` and `android.tw.john.TWClient`.
  - If absent, the app loads emulated classes, but hardware audio/radio does not actually work.
- Auxio-TS decision:
  - Strong cautionary example: fake/stub vendor classes can make UI buttons move but not produce real hardware behaviour.
  - Supports the policy of not faking Topway/Cardoor services in production.

#### Stack Overflow / Android 10 car radio volume thread

- URL: https://stackoverflow.com/questions/78731714/how-to-programmatically-set-the-volume-on-a-car-radio-android-app-in-java
- Notes:
  - Android-standard `AudioManager` volume calls may behave differently on Android 10 head units.
  - Mentions FCC Car Launcher / KaierUtils using `android.tw.john.TWUtil`.
- Auxio-TS decision:
  - Useful as a warning that OEM audio stacks may not obey normal phone assumptions.
  - Not a reason to import `TWUtil` for Auxio-TS music playback.

### 11.2 `cn.cardoor` and `com.tw.service.xt.aidl` search status

Current searches on public GitHub/Gitee/GitCode/web did not find a reliable, complete, public, TS18-specific contract for:

```text
cn.cardoor.libs.media.RemoteMediaService
cn.cardoor.basic.media.NotifyService
cn.cardoor.libs.media.impl.MediaSourceService
com.tw.service.xt.aidl.ITWCommandAidl
com.tw.service.xt.aidl.ITWCommandCallbackAidl
```

Auxio-TS decision:

- Do not implement fake Cardoor services.
- Do not add `com.tw.service.xt.aidl.*` stubs to product code.
- Do not reflectively scan for `TWUtil` or vendor binder classes in release builds.
- Keep references only in docs, tests, diagnostics notes, or evidence-gated future-work files.

---

## 12. Recommended Auxio-TS fallback stacks

### 12.1 Launcher recognition

Preferred order:

1. `topwayTwMusicRelease`: `com.tw.music/com.tw.music.MusicActivity`.
2. `topwayTwMediaRelease`: `com.tw.media/com.tw.music.MusicActivity`.
3. Android `APP_MUSIC` / `MUSIC_PLAYER` category discovery.
4. Normal `org.oxycblt.auxio` launcher activity.
5. Real DoFun hotseat validation.

Implementation notes:

- `topwayTwMusicRelease` may conflict with stock system `com.tw.music`.
- `topwayTwMediaRelease` is an alternate fixed-entry candidate, not a universal no-root workaround.
- Debug variants must not be documented as final DoFun proof if they use suffixes or debug identities.

### 12.2 Media service/session

Preferred order:

1. One canonical playback/session owner.
2. One canonical exported Topway browse service for Topway variants, if needed.
3. Thin wrapper delegates into Auxio-owned playback code.
4. No duplicate playback sessions, duplicate notifications, or split service state.

Validation commands:

```sh
adb shell dumpsys media_session
adb shell cmd media_session list-sessions 2>/dev/null || true
adb shell dumpsys activity services | grep -E 'AuxioService|MusicService|MediaBrowserService'
adb shell dumpsys notification | grep -E 'Auxio|com.tw.music|com.tw.media'
```

### 12.3 Topway broadcast bridge

Incoming commands to handle:

```text
com.tw.music.action.cmd + cmd=prev
com.tw.music.action.cmd + cmd=next
com.tw.music.action.cmd + cmd=pp
com.tw.music.action.cmd + cmd=update
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
com.android.launcher.widget_music_progress
```

Outgoing state to publish, if validated/implemented:

```text
com.tw.music.info
musicTitle
musicaArtist     # preserve typo
musicAlbum
musicPath
com.tw.launcher.music_progress_duration
msg_music_progress
msg_music_duration
```

Safety notes:

- Sanitise all incoming extras.
- Rate-limit progress broadcasts.
- Do not over-tighten package-scoping if DoFun requires global broadcasts; document the exposure and sanitisation boundary.
- Standard Auxio builds should not emit Topway compatibility broadcasts unless intentionally enabled.

### 12.4 Widget recognition/update routing

Do not rely only on `AppWidgetManager.getAppWidgetIds()` for DoFun. DoFun may use a fixed launcher/hotseat path rather than a normal Android AppWidget host.

Topway-compatible variants should serve explicit `cmd=update` even when no normal widget instances are bound. Standard Auxio should keep normal widget gating.

### 12.5 Storage and USB/UDisk music

Preferred order:

1. MediaStore audio queries, including external volume names where available.
2. Removable-volume query/diagnostics.
3. SAF `ACTION_OPEN_DOCUMENT_TREE` user-selected folder fallback.
4. TS18 empty-library diagnostics mentioning `/storage/usbdisk0`.
5. Optional privileged/all-files access lane only if deliberately separated and documented.

Do not silently treat an empty MediaStore result as success on the TS18.

Validation commands:

```sh
adb shell ls -lah /storage
adb shell ls -lah /storage/usbdisk0
adb shell content query --uri content://media/external/audio/media --projection _id,_display_name,data 2>/dev/null | head -50
adb shell dumpsys package com.tw.music | grep -E 'READ_EXTERNAL_STORAGE|READ_MEDIA_AUDIO|MANAGE_EXTERNAL_STORAGE|requestLegacyExternalStorage'
```

### 12.6 Overlay controls

Use:

- `TYPE_APPLICATION_OVERLAY`.
- Android 10-safe foreground-service start behaviour.
- API-gated service-type constants.
- Bounds clamp for 1280 x 720 with top/right 55 px insets.
- Clean stop on permission revocation.

Do not use API 34-only `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` unconditionally on SDK 29.

### 12.7 Package-management lanes

| Lane | What it can do | TS18 caveat |
|---|---|---|
| Normal install | Install non-conflicting package IDs | Cannot replace stock system `com.tw.music` if signature/package conflict remains |
| `topwayTwMedia` | Try alternate `com.tw.media` DoFun entry | May conflict if firmware already has `com.tw.media`; not universal no-root bypass |
| ADB shell | `pm disable-user`, `pm uninstall --user 0`, `cmd package install-existing` | Requires real ADB shell access, not TermOne app UID |
| Shizuku | Privileged shell-like calls for authorised apps | Android 10 unrooted startup needs computer ADB; poor fit if USB/OTG ADB unavailable |
| Root/Sui | Privileged package control | Requires rooted firmware |
| Firmware/system image | Replace system app in image | High risk, device-specific, outside normal Auxio-TS APK release |

Recovery commands to document where ADB/root lane exists:

```sh
adb shell pm enable com.tw.music
adb shell cmd package install-existing --user 0 com.tw.music
adb shell pm install-existing --user 0 com.tw.music
```

---

## 13. Suggested repo checks / CI guardrails

Agents should ensure scripts/checks cover at least:

```text
standard package == org.oxycblt.auxio
topwayTwMusicRelease package == com.tw.music
topwayTwMediaRelease package == com.tw.media
com.tw.music/com.tw.music.MusicActivity resolves in topwayTwMusicRelease
com.tw.media/com.tw.music.MusicActivity resolves in topwayTwMediaRelease
provider authorities follow the active applicationId
Topway wrappers are thin and delegate into Auxio-owned code
no platform signing / sharedUserId / android.uid.system assumptions
no product-code cn.cardoor imports
no product-code android.tw.john imports
no product-code com.tw.service.xt.aidl imports
Topway broadcast strings centralised/isolated
metadata/progress payload tests preserve musicaArtist typo
overlay foreground-service type logic is API-gated
release workflow publishes standard, topwayTwMusic, topwayTwMedia APKs
release docs do not claim no-root universal installability
runtime validation docs include /storage/usbdisk0
```

---

## 14. Suggested validation command pack for real TS18

### 14.1 Package and launcher resolution

```sh
adb shell pm list packages | grep -E 'com.tw.music|com.tw.media|org.oxycblt.auxio|com.dofun.variety'
adb shell cmd package resolve-activity --brief com.tw.music/com.tw.music.MusicActivity
adb shell cmd package resolve-activity --brief com.tw.media/com.tw.music.MusicActivity
adb shell dumpsys package com.tw.music | sed -n '/Activities:/,/Receivers:/p'
adb shell dumpsys package com.tw.media | sed -n '/Activities:/,/Receivers:/p'
```

### 14.2 DoFun broadcast controls

```sh
adb shell am broadcast -a com.tw.music.action.cmd --es cmd update
adb shell am broadcast -a com.tw.music.action.cmd --es cmd pp
adb shell am broadcast -a com.tw.music.action.cmd --es cmd next
adb shell am broadcast -a com.tw.music.action.cmd --es cmd prev
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
adb shell am broadcast -a com.android.launcher.widget_music_progress --ei music_progress 30000
```

### 14.3 Media sessions/services/notifications

```sh
adb shell dumpsys media_session | grep -E 'com.tw.music|com.tw.media|Auxio|MediaSession' -C 3
adb shell dumpsys activity services | grep -E 'AuxioService|MusicService|MediaBrowserService' -C 3
adb shell dumpsys notification | grep -E 'com.tw.music|com.tw.media|Auxio' -C 3
```

### 14.4 Storage/USB/UDisk

```sh
adb shell ls -lah /storage
adb shell ls -lah /storage/usbdisk0
adb shell find /storage/usbdisk0 -maxdepth 3 -type f | head -50
adb shell content query --uri content://media/external/audio/media --projection _id,_display_name,data | head -50
```

### 14.5 Overlay

```sh
adb shell appops get com.tw.music SYSTEM_ALERT_WINDOW
adb shell dumpsys window | grep -E 'Auxio|overlay|TYPE_APPLICATION_OVERLAY|com.tw.music' -C 3
adb shell dumpsys activity services | grep -E 'CarOverlay|Foreground' -C 3
```

---

## 15. How agents should classify search results

When an agent finds a new source, classify it before acting:

| Classification | Meaning | Action |
|---|---|---|
| Official Android contract | Android Developers, AOSP, Source Android | Can drive implementation decisions. |
| Mature OSS app | VLC, AntennaPod, Auxio, Retro, etc. | Use for patterns after licence check. |
| Chinese OSS app/repo | Chinese developer repo or source-linked tutorial | Use for search terms, UX patterns, implementation patterns after licence check. |
| APK mirror | 7723, Ali213, random download site | Package/version/name evidence only; do not download into repo. |
| Forum field report | XDA, 4PDA, CSDN comments, Telegram | Useful for device behaviour hypotheses; must validate locally. |
| Decompiled/private code | stock APK classes, smali, vendor AIDL | Evidence-gated only; never copy into production without formal approval. |
| Exact private/native contract | Complete AIDL/API with device proof | Requires design review, isolation, graceful fallback, and tests. |

---

## 16. Immediate research conclusions for Auxio-TS

1. Chinese sources are useful for launcher terminology, DoFun package-name discovery, Android head-unit storage issues, and equivalent media-player behaviour.
2. Exact public Topway/DoFun `com.tw.music` implementation code remains scarce.
3. Public traces of `android.tw.john.TWUtil` exist, but they reinforce caution rather than justify production integration.
4. No reliable public `cn.cardoor` / `com.tw.service.xt.aidl` media-control contract was found in this pass.
5. The safest path remains Android-standard MediaSession/MediaBrowserService plus thin package/component wrappers and public broadcast compatibility.
6. `/storage/usbdisk0` must stay a first-class real-device validation requirement; do not assume MediaStore indexes it.
7. `topwayTwMedia` should remain documented as an alternate DoFun fixed-entry candidate, not a guaranteed no-root workaround.
8. `topwayTwMusic` remains the exact stock replacement identity but will normally conflict with stock `com.tw.music` unless package/signing state is managed.

---

## 17. Copy-paste prompt snippet for future agents

Use this snippet when asking Copilot/Codex/other agents to refresh Chinese research without going off-track:

```text
Refresh the Chinese and head-unit source map before editing. Search GitHub, Gitee, GitCode, CSDN, Juejin, XDA, 4PDA, and general web for:

- Topway TS18 com.tw.music
- TS18 百变主题 com.dofun.variety
- 车机 Launcher Android 源码
- 安卓车机 音乐控件
- com.tw.music.action.cmd
- com.tw.music.info musicTitle musicaArtist
- com.tw.launcher.music_progress_duration
- android.tw.john.TWUtil
- cn.cardoor.libs.media.RemoteMediaService
- com.tw.service.xt.aidl
- 安卓车机 U盘 音乐 扫描
- /storage/usbdisk0 Android

Classify every new source as official, mature OSS, Chinese OSS, APK mirror, forum field report, or private/native evidence. Do not implement fake Cardoor services, TWUtil reflection, copied smali, platform signing, android.uid.system, sharedUserId, or vendor AIDL unless a complete device-specific contract is recovered and explicitly approved. Prefer Android-standard MediaBrowserService/MediaSession, MediaStore/SAF, and thin Topway/DoFun wrappers.
```

