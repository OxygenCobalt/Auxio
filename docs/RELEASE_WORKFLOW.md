# Release workflow

## Build commands

```sh
# Standard Auxio-TS
./gradlew :app:assembleStandardRelease

# Topway/DoFun com.tw.music variant
./gradlew :app:assembleTopwayTwMusicRelease
```

## Release signing

The manual release workflow (`.github/workflows/manual-release.yml`) uses repository secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The keystore is decoded to `$RUNNER_TEMP/release.keystore` and never committed.

## Expected release APK assets

Each release publishes two signed APKs:

| Asset name | Package ID | Notes |
|-----------|-----------|-------|
| `Auxio-TS-vX.Y.Z-standard-release.apk` | `org.oxycblt.auxio` | Normal Auxio-TS |
| `Auxio-TS-vX.Y.Z-topway-twmusic-release.apk` | `com.tw.music` | DoFun-compatible |

## Warnings

- **The Topway APK installs as `com.tw.music`.** It will conflict with stock `com.tw.music` if not uninstalled/disabled first.
- **Stock package conflicts:** Before installing the Topway variant on a TS18 head unit, check for existing `com.tw.music` with `adb shell cmd package list packages | grep com.tw.music`.

## Running a release

1. Open **Actions → Manual Release → Run workflow** from the `dev` branch. The workflow refuses tag refs and non-`dev` branches so version metadata, tag, and release assets are produced from the protected/default line.
2. Provide `version_tag` (e.g. `1.2.3`) or leave blank for auto-increment.
3. Optionally set `draft: true` for validation.
4. The workflow validates signing secrets before building, builds both variants, runs the DoFun/Topway compatibility contract check against produced outputs, verifies APK signatures, creates a GitHub release, and uploads both APKs.
