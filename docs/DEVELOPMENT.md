# Development

## Prerequisites

- JDK 21 (Temurin recommended)
- Android SDK with build-tools, platform 34+, NDK
- Gradle (wrapper included)

## Setup

```sh
bash scripts/prepare-ci-environment.sh   # init submodules, validate, create stubs
./gradlew :app:assembleStandardDebug     # verify build
```

`prepare-ci-environment.sh` handles submodule init/update and the `common_ktx` proguard stub. Run it once after clone or submodule changes.

## Key Gradle tasks

| Task | Description |
|------|-------------|
| `:app:assembleStandardDebug` | Standard Auxio-TS debug APK |
| `:app:assembleTopwayTwMusicDebug` | DoFun-compatible debug APK (`com.tw.music.debug`) |
| `:app:assembleTopwayTwMusicRelease` | DoFun-compatible release APK (`com.tw.music`) |
| `:app:testStandardDebugUnitTest` | Unit tests |
| `:app:lintStandardDebug` | Android lint |
| `spotlessCheck` | Code formatting check |

## Repository layout

```
app/                          Android app module
  src/main/                   Shared source (all variants)
  src/topwayTwMusic/          Topway variant manifest + resources
  src/topwayTwMusicDebug/     Topway debug resources
musikr/                       Music indexing library
media/                        Media playback submodule
gradle/                       Gradle wrapper
scripts/                      CI/validation scripts
docs/                         Minimal focused documentation
```

## Compatibility check scripts

```sh
bash scripts/check-dofun-topway-compat.sh       # DoFun/Topway source+manifest validation
bash scripts/check-headunit-compat-safety.sh     # Safety guardrails (forbidden hooks, isolation)
```

Both are run by CI on every PR (`lint.yml` → Head-unit safety job).

## Product flavours

The app has a `distribution` flavour dimension:

- **`standard`** — normal Auxio identity (`org.oxycblt.auxio`)
- **`topwayTwMusic`** — DoFun/Topway identity (`com.tw.music`)

The Topway bridge code lives in `app/src/main/java/org/oxycblt/auxio/headunit/topway/` and is shared by all variants. The `topwayTwMusic` flavour adds only the manifest alias and resource overrides needed for package identity matching.
