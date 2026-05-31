<p align="center"><img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="150"></p>
<h1 align="center"><b>Auxio-TS</b></h1>
<h4 align="center">TS18/Topway/DoFun Theme variant of Auxio — a replacement for stock <code>twmusic</code> / <code>com.tw.music</code></h4>

<p align="center">
    <a href="https://github.com/cbkii/Auxio-TS/releases/">
        <img alt="Release" src="https://img.shields.io/github/v/release/cbkii/Auxio-TS">
    </a>
    <a href="https://www.gnu.org/licenses/gpl-3.0">
        <img alt="License: GPL-3.0-or-later" src="https://img.shields.io/badge/license-GPL--3.0--or--later-2B6DBE.svg?style=flat">
    </a>
    <img alt="Minimum SDK Version" src="https://img.shields.io/badge/API-24%2B-1450A8?style=flat">
</p>

## About

**Auxio-TS** is a TS18/Topway head-unit variant of [Auxio](https://github.com/OxygenCobalt/Auxio).

It adapts Auxio for in-vehicle TS18/Topway Android head units and integrates with **DoFun Variety Theme** (`com.dofun.variety`) launcher/music widgets by providing a compatibility APK for the stock `twmusic` / `com.tw.music` contract.

Auxio-TS is not a replacement for the upstream Auxio project. It is a specialised downstream variant for a particular head-unit environment.

## Upstream acknowledgement

This project is based on **Auxio**, created and maintained upstream by [OxygenCobalt](https://github.com/OxygenCobalt).

The music player, core design, playback architecture, library model, UI foundation, and most application behaviour come from upstream Auxio. Auxio-TS exists because TS18/Topway/DoFun launchers expect specific stock music-app package names, activity names, broadcast strings, and widget-control behaviour that the normal Auxio app should not need to impersonate.

Please support, credit, and follow the upstream project:

- Upstream project: [OxygenCobalt/Auxio](https://github.com/OxygenCobalt/Auxio)
- Upstream issues/discussions: use upstream only for issues that also affect normal Auxio
- Auxio-TS issues: use this repo for TS18, Topway, DoFun Variety, `com.tw.music`, or head-unit-specific behaviour

### Donate to upstream Auxio

If you find Auxio-TS useful, please consider supporting the original Auxio project and author. You can support Auxio development through OxygenCobalt’s GitHub Sponsors page:

[Donate to upstream Auxio via GitHub Sponsors](https://github.com/sponsors/OxygenCobalt)

## What Auxio-TS changes

Auxio-TS keeps Auxio as the runtime music player base and adds TS18/Topway/DoFun compatibility work around it.

### Key facts

| Area | Value |
| --- | --- |
| Upstream base | [OxygenCobalt/Auxio](https://github.com/OxygenCobalt/Auxio) |
| Primary compatibility target | DoFun Variety Theme / `com.dofun.variety` |
| Stock app replacement contract | `twmusic` / `com.tw.music` |
| Topway release package | `com.tw.music` |
| Topway launcher/activity component | `com.tw.music.MusicActivity` |
| Topway release variant | `topwayTwMusicRelease` |
| Standard development variant | `org.oxycblt.auxio` |

### Variant model

Auxio-TS has two separate identities:

| Variant | Package identity | Purpose |
| --- | --- | --- |
| `standard` | `org.oxycblt.auxio` | Normal Auxio-derived development/upstream baseline |
| `topwayTwMusic` | `com.tw.music` | TS18/Topway/DoFun-compatible APK intended to stand in for stock `twmusic` |

Only the dedicated Topway/DoFun compatibility variant uses the `com.tw.music` package identity.

## Compatibility scope

Auxio-TS targets compatibility surfaces needed by DoFun Variety and TS18/Topway launchers, such as:

- `com.tw.music` package identity for the compatibility APK
- `com.tw.music.MusicActivity` launcher/activity alias
- Android `MediaSession` / `MediaBrowserService`
- Topway-style metadata/progress broadcasts
- Topway-style widget control broadcasts
- DoFun launcher/music-widget recognition

Observed private/system/vendor hooks from stock TS18 apps have been treated as reference evidence only so far. Auxio-TS won't copy private system privileges or vendor-only APIs into production code unless a protocol is fully understood, justified, reviewed, and implemented safely.

## Building

Auxio-TS inherits upstream Auxio’s Android build requirements.

Auxio relies on a patched Media3 setup and native metadata dependencies, so a working Android build environment needs:

- JDK 21
- Android SDK
- CMake / native build tooling where required
- `ninja-build`
- Git submodules initialised recursively

Initial setup:

```sh
git submodule update --init --recursive
bash scripts/prepare-ci-environment.sh
````

Build the standard development APK:

```sh
./gradlew :app:assembleStandardDebug
```

Build the TS18/Topway/DoFun compatibility APK:

```sh
./gradlew :app:assembleTopwayTwMusicRelease
```

For CI-equivalent local checks:

```sh
bash scripts/check-ts18-apk-reference-contracts.sh
bash scripts/check-dofun-topway-compat.sh
bash scripts/check-headunit-compat-safety.sh
```

## Validation

Before installing on a TS18/head-unit device, check existing packages:

```sh
adb shell 'cmd package list packages | grep -E "com\.tw\.music|com\.tw\.media|org\.oxycblt\.auxio|com\.dofun\.variety"'
```

After installing the Topway build, validate package/activity/media visibility:

```sh
adb shell cmd package resolve-activity --brief com.tw.music
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.APP_MUSIC
adb shell cmd package query-intent-services -a android.media.browse.MediaBrowserService
adb shell dumpsys media_session | grep -i -A60 'com.tw.music\|auxio'
```

Exercise Topway-style controls:

```sh
adb shell am broadcast -a com.tw.music.action.pp
adb shell am broadcast -a com.tw.music.action.next
adb shell am broadcast -a com.tw.music.action.prev
```

See [`docs/TS18_RUNTIME_VALIDATION.md`](docs/TS18_RUNTIME_VALIDATION.md) for the full on-device checklist.

## Documentation

| Doc                                                                          | Purpose                                     |
| ---------------------------------------------------------------------------- | ------------------------------------------- |
| [`docs/README.md`](docs/README.md)                                           | Documentation index                         |
| [`docs/DOFUN_VARIETY_COMPATIBILITY.md`](docs/DOFUN_VARIETY_COMPATIBILITY.md) | DoFun/Topway compatibility contract         |
| [`docs/TS18_APK_REFERENCE.md`](docs/TS18_APK_REFERENCE.md)                   | Compact APK-derived reference evidence      |
| [`docs/TS18_RUNTIME_VALIDATION.md`](docs/TS18_RUNTIME_VALIDATION.md)         | Head-unit validation checklist              |
| [`docs/RELEASE_WORKFLOW.md`](docs/RELEASE_WORKFLOW.md)                       | Release process and expected APK assets     |
| [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)                                 | Local setup, CI, Roborazzi, and repo layout |

## Contributing

For general Auxio issues, features, or behaviour that also affects the normal upstream app, prefer the upstream project first:

[OxygenCobalt/Auxio](https://github.com/OxygenCobalt/Auxio)

Use this repo for Auxio-TS-specific work, especially:

* TS18/Topway head-unit behaviour
* DoFun Variety Theme widget/panel integration
* `com.tw.music` compatibility APK behaviour
* Topway broadcast/control bridge behaviour
* release and validation workflows for the TS18 variant

Keep changes aligned with upstream Auxio where practical. Avoid unnecessary divergence from upstream unless required for the TS18/Topway/DoFun compatibility target.

## License

Auxio-TS is a downstream derivative of Auxio and remains aligned with upstream Auxio’s free-software licence terms.

Auxio-TS is distributed under the same GPL-3.0-or-later terms. See [`LICENSE`](LICENSE) and [`NOTICE`](NOTICE) where present for licence and attribution details.

You may use, study, share, and improve this software under those GPL terms. Any redistribution or modification must preserve the applicable GPL licence obligations and upstream attribution.
