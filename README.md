<p align="center"><img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="150"></p>
<h1 align="center"><b>Auxio-TS</b></h1>
<h4 align="center">TS18/Topway/DoFun Theme variant of Auxio — a replacement for stock <code>twmusic</code> / <code>com.tw.music</code></h4>
<p align="center">
    <a href="https://github.com/cbkii/Auxio-TS/releases/">
        <img alt="Releases" src="https://img.shields.io/github/v/release/cbkii/Auxio-TS?style=flat&color=4B95DE">
    </a>
    <a href="https://www.gnu.org/licenses/gpl-3.0">
        <img src="https://img.shields.io/badge/license-GPL%20v3-2B6DBE.svg?style=flat">
    </a>
    <img alt="Minimum SDK Version" src="https://img.shields.io/badge/API-24%2B-1450A8?style=flat">
</p>

## About

Auxio-TS is a TS18/Topway head-unit variant of [Auxio](https://github.com/OxygenCobalt/Auxio) that integrates with the **DoFun Variety Theme** (`com.dofun.variety`) launcher/music widgets and serves as a drop-in replacement for the stock `twmusic` / `com.tw.music` music app.

### Key facts

- **Primary compatibility target:** DoFun Variety / `com.dofun.variety`
- **Replacement contract:** `com.tw.music` package with `com.tw.music.MusicActivity` component
- **Release APK:** `topwayTwMusicRelease` installs as exact package `com.tw.music`
- **Standard variant:** `org.oxycblt.auxio` remains available as a development/upstream baseline

## Building

```sh
bash scripts/prepare-ci-environment.sh

# Standard Auxio-TS (upstream baseline)
./gradlew :app:assembleStandardDebug

# DoFun-compatible Topway variant
./gradlew :app:assembleTopwayTwMusicRelease
```

## Validation

```sh
bash scripts/check-dofun-topway-compat.sh       # DoFun/Topway compatibility checks
bash scripts/check-headunit-compat-safety.sh     # Safety guardrails
```

## Documentation

| Doc | Purpose |
|-----|---------|
| [docs/DOFUN_VARIETY_COMPATIBILITY.md](docs/DOFUN_VARIETY_COMPATIBILITY.md) | DoFun integration reference |
| [docs/TS18_RUNTIME_VALIDATION.md](docs/TS18_RUNTIME_VALIDATION.md) | Head-unit validation checklist |
| [docs/RELEASE_WORKFLOW.md](docs/RELEASE_WORKFLOW.md) | Release process |
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | Local setup and repo layout |

## License

Auxio-TS is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0).
