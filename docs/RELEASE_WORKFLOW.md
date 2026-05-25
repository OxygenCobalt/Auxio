# Manual signed APK release workflow

Auxio-TS uses one manual release workflow: `.github/workflows/manual-release.yml` (`Manual Release`).

## Run a release

1. Open **Actions → Manual Release → Run workflow**.
2. Select a branch (the workflow requires a branch so it can commit version metadata).
3. Provide inputs:
   - `version_tag` (optional): `MAJOR.MINOR.PATCH` or `vMAJOR.MINOR.PATCH`.
   - `draft`: create a draft release.
   - `prerelease`: mark as prerelease.
4. Run the job and open the release link in the job summary.

## Required secrets

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The keystore is decoded only to `$RUNNER_TEMP/release.keystore`. Secrets are masked and not printed.

## Required workflow permissions

- `contents: write`
- `pull-requests: read`

Repository Actions settings must allow workflow write access for commit/tag/release creation.

## Submodule requirement

The workflow checks out with `submodules: recursive` and then runs:

```sh
git submodule status --recursive
bash scripts/check-submodules.sh
```

Do not create missing submodule files manually.

## JDK/Android/Gradle requirements

The workflow uses:

- Temurin JDK 21 (`actions/setup-java@v4`)
- Gradle setup (`gradle/actions/setup-gradle@v4`)
- Android SDK setup (`android-actions/setup-android@v3`)
- `sdkmanager` install of required SDK/build-tools/NDK
- `apksigner` verification step before release upload

`local.properties` is not stored in the repository, but the GitHub Actions Android build/quality workflows now generate an ephemeral `local.properties` with `sdk.dir=$ANDROID_HOME` for runner reliability.

## PR release-readiness workflows

- `Android Build` now runs on `pull_request` to `dev`, `push` to `dev`, and `workflow_dispatch`.
- Successful PR and push runs upload `app/build/outputs/apk/debug/app-debug.apk` as the `auxio-ts-debug-apk` artifact with 7-day retention.
- `Android Build` also uploads build reports on failure so missing/debug-APK issues are distinguishable from Gradle failures.
- `Android Quality` now runs four independent jobs so one failure does not hide the others:
  - `Formatting` → `./gradlew --no-daemon --stacktrace spotlessCheck`
  - `Unit tests` → `./gradlew --no-daemon --stacktrace :app:testDebugUnitTest :musikr:testDebugUnitTest`
  - `Android lint` → `./gradlew --no-daemon --stacktrace :app:lintDebug`
  - `Head-unit safety` → `bash scripts/check-headunit-compat-safety.sh`

## Version and tag behavior

- If `version_tag` is provided, the workflow normalizes to `vMAJOR.MINOR.PATCH`.
- If omitted, it auto-increments the latest `vMAJOR.MINOR.PATCH` tag patch.
- `versionName` is updated to the normalized version without `v`.
- `versionCode` increments by `+1`.
- Existing tags/releases are never overwritten.
- The release tag is created on the commit that updates version metadata.

## Release sequence

1. Checkout + submodule verification.
2. JDK/Android/Gradle setup.
3. Version computation and metadata update.
4. Local version commit.
5. Signing secret validation.
6. Signed release APK build.
7. APK signature verification.
8. Push version commit.
9. Push release tag.
10. Generate release notes.
11. Create GitHub release and upload APK.
12. Write summary with clickable release link.

## Release notes behavior

Release body always starts with `## What's changed` and contains bullet points.

- PR metadata is preferred (linked PR title + author).
- Commit links are used when PR metadata is unavailable.
- PR lookup failures emit warnings but do not fail release creation.

## Expected release asset

One signed APK asset:

`Auxio-TS-vX.Y.Z-release.apk`

The workflow fails if no signed APK is found, more than one candidate is found, or signature verification fails.

## First safe validation run

Use a manual run with:

- `draft: true`
- explicit `version_tag` (for predictable validation)

Then verify draft status, release notes, uploaded signed APK, and summary link.

## Failed-release recovery basics

If a run fails after pushing commit/tag:

1. Check current tags/releases.
2. Confirm tag points to the intended version commit.
3. Retry with explicit `version_tag` only after intentional cleanup of the prior tag/release state; otherwise choose a new unique version.
4. Prefer retry as draft first.
