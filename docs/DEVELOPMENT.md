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

`prepare-ci-environment.sh` handles submodule init/update and the `common_ktx` proguard stub. Run it once after clone or submodule changes. A ZIP/snapshot checkout is insufficient because Gradle needs the `media` submodule, nested ffmpeg sources, and `musikr` taglib sources.

For Codex or a fresh Linux environment, `bash scripts/setup-codex-android-env.sh` can bootstrap/verify Android command-line tools, SDK platform/build tools, CMake, NDK, submodules, and a Gradle smoke test.

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


## Startup library loading and scans

Auxio-TS startup must expose the existing library, playback queue, MediaSession, and MediaBrowser surfaces before any slow storage scan. Normal launches rebuild the in-memory library from the persisted Musikr cache/database first and skip automatic rescans when a usable or known-empty library state is already recorded.

Filesystem scans are reserved for first install/no-library startup, explicit user refresh actions (**Refresh music** / **Rescan music** in settings or the home retry action), or recovery cases where persisted library data cannot be used. Manual refreshes run in the background and keep the currently visible library/playback surfaces alive until replacement data is ready.

For Topway/DoFun validation, confirm the `AuxioService` creates its MediaSession and MediaBrowser root promptly and that `TopwayMusicBridgeReceiver` commands still route while any scan notification/progress is active. Static/build validation is not a substitute for real TS18 head-unit widget testing.

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

## UI development and Roborazzi screenshots

Roborazzi is wired into the Gradle build (`build.gradle` plugin + `app/build.gradle` dependencies).
Test file: `app/src/test/java/org/oxycblt/auxio/ui/RoborazziSmokeScreenshotTest.kt`.

| Gradle task | Description |
|-------------|-------------|
| `:app:recordRoborazziStandardDebug / :app:recordRoborazziTopwayTwMusicDebug` | Capture new PNG baselines |
| `:app:verifyRoborazziStandardDebug / :app:verifyRoborazziTopwayTwMusicDebug` | Verify against committed baselines |
| `:app:compareRoborazziStandardDebug / :app:compareRoborazziTopwayTwMusicDebug` | Produce diff report without failing |
| `:app:verifyAndRecordRoborazziStandardDebug / :app:verifyAndRecordRoborazziTopwayTwMusicDebug` | Verify then record changed baselines |

Roborazzi uses Robolectric — no emulator or device required. Regular `test*UnitTest` tasks intentionally exclude `RoborazziSmokeScreenshotTest` so the PR unit-test job is not coupled to Roborazzi runtime artifact downloads; use the Roborazzi tasks/workflow for visual coverage.

Use the **UI Screenshots** (`ui-screenshots.yml`) workflow to trigger these tasks manually on any branch/PR and retrieve PNG + HTML report artifacts. Select `variant=standard` for normal Auxio-TS UI or `variant=topway_twmusic` for the DoFun/Topway package-identity variant. Trigger with `record` first to establish baselines, then use `verify` to detect regressions.

Baseline PNGs live adjacent to the test source (committed to the repo). They are generated at 1280×720 to match TS18/head-unit landscape resolution.

## CI and workflow coverage

| Workflow | Trigger | Responsibility |
|----------|---------|----------------|
| `android.yml` | push/PR to dev, app/build paths | Standard + Topway debug builds; DoFun compat checks; APK artifacts |
| `lint.yml` | push/PR to dev, app/build/scripts/docs/workflow paths | Workflow YAML syntax; shell script syntax; formatting (spotless); unit tests; Android lint; head-unit safety + DoFun compat scripts |
| `manual-release.yml` | manual dispatch | Signed standard + Topway release APKs; package identity verification |
| `ui-screenshots.yml` | manual dispatch | Roborazzi UI regression screenshots; PNG + HTML report artifacts |


### Branch protection / required checks

If GitHub branch protection is enabled, required status checks should match the current job names:

- `Android Build / build`
- `Android Quality / Workflow/script syntax`
- `Android Quality / Formatting`
- `Android Quality / Unit tests`
- `Android Quality / Android lint`
- `Android Quality / Head-unit safety`

Remove stale required checks for deleted workflows such as `Manual Roborazzi`, `Manual UI Screenshots`, `ts18-guardrails`, or `ts18-validation-tools`; their active coverage is replaced by `ui-screenshots.yml` and the Android Quality guardrail jobs above.

### Why deleted workflows are not retained

| Removed workflow | Reason |
|------------------|--------|
| `ts18-guardrails.yml` | Validated deleted research tooling (evidence scripts, scenario maps, fixture packs). The only still-relevant check (`check-headunit-compat-safety.sh`) is covered by the `headunit-safety` job in `lint.yml`. |
| `ts18-validation-tools.yml` | Validated deleted TS18 Python scripts and scenario map JSON. All referenced files are removed. Relevant headunit-safety check covered in `lint.yml`. |
| `manual-roborazzi.yml` | **Replaced** by `ui-screenshots.yml`. Functionality preserved and focused on current app UI needs. |
| `manual-ui-screenshots.yml` | Depended on deleted `scripts/capture-ui-screenshots.sh` and a brittle Android emulator setup. Superseded by the emulator-free Roborazzi approach in `ui-screenshots.yml`. |

