# Manual signed APK release workflow

Auxio-TS has one authoritative manual release workflow: `.github/workflows/manual-release.yml` (`Manual Release`). GitHub Actions is the target release environment (not local agent containers). The workflow is intentionally narrow: it checks out submodules, sets up Android/JDK prerequisites, updates Android version metadata, builds one signed release APK, creates one GitHub release, uploads that APK as a release asset, and writes a release link to the job summary.

## How to run a release

1. Open **GitHub → Actions → Manual Release → Run workflow**.
2. Choose the branch to release from. The workflow fails intentionally if it is run from a tag, because it must commit version metadata before tagging.
3. Set inputs:
   - `version_tag`: optional `vMAJOR.MINOR.PATCH` tag, for example `v4.0.12`. Leave blank to auto-increment the patch version.
   - `draft`: `true` creates a draft release for review; `false` publishes immediately.
   - `prerelease`: `true` marks the release as a prerelease.
4. Start the workflow and wait for the `# Auxio-TS release` job summary. The summary includes a clickable link to the created release.

## Required repository secrets

Configure these repository secrets before running the workflow:

- `KEYSTORE_BASE64`: base64-encoded release keystore.
- `KEYSTORE_PASSWORD`: keystore password.
- `KEY_ALIAS`: signing key alias.
- `KEY_PASSWORD`: signing key password.

The workflow decodes the keystore only to `$RUNNER_TEMP/release.keystore`, masks signing values, passes signing values to Gradle through `ORG_GRADLE_PROJECT_*` environment variables, and never commits keystore material.

## Required repository settings and permissions

The workflow uses minimal explicit permissions:

- `contents: write` to push the version metadata commit/tag and create the GitHub release with an APK asset.
- `pull-requests: read` to look up PR metadata for release notes.

Repository Actions settings must allow the workflow token to write repository contents.

## Submodule requirement

Release builds require recursive submodules. The workflow uses `actions/checkout` with `fetch-depth: 0`, `fetch-tags: true`, and `submodules: recursive`, then runs:

```sh
git submodule status --recursive
bash scripts/check-submodules.sh
```

The preflight checks these stable build inputs:

- `media/core_settings.gradle`
- `media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg`
- `musikr/src/main/cpp/taglib/CMakeLists.txt`

If `media/core_settings.gradle` is missing, the media submodule was not initialised. Do **not** create this file manually; fix the checkout by running `git submodule update --init --recursive` locally or ensuring Actions uses recursive submodules.

## JDK, Android, and Gradle

The release workflow explicitly sets up Temurin JDK 21 with `actions/setup-java@v4`, then installs/configures Android SDK tooling with `android-actions/setup-android@v3` and `sdkmanager --install` for the required components:

- `platform-tools`
- `build-tools;36.0.0`
- `platforms;android-36`
- `ndk;28.2.13676358`

The workflow also prints Android SDK diagnostics before Gradle (`ANDROID_HOME`, `ANDROID_SDK_ROOT`, `which sdkmanager`, `sdkmanager --list_installed`, `which apksigner`) and fails early if `apksigner` is unavailable after setup. It uses the repository Gradle wrapper with `gradle/actions/setup-gradle@v4`. Native builds require `ninja`; the workflow installs `ninja-build` only when missing.

## Version and tag behaviour

Android version metadata lives in `app/build.gradle`:

- `defaultConfig.versionName`
- `defaultConfig.versionCode`

When `version_tag` is provided:

1. It must match `vMAJOR.MINOR.PATCH`.
2. The workflow fails if that tag or release already exists.
3. `versionName` is set to the tag without the leading `v`.
4. `versionCode` is incremented by `+1`.

When `version_tag` is blank:

1. The workflow finds the latest tag matching `vMAJOR.MINOR.PATCH`.
2. It increments the patch version.
3. If no semver tag exists, it derives from current `versionName` when possible.
4. If neither source is semver-like, it uses `v0.1.1` from the documented `0.1.0` fallback base.
5. It fails if the computed tag or release already exists.
6. It updates `versionName` and increments `versionCode` by `+1`.

The workflow commits the version metadata first, pushes that commit, then creates and pushes the release tag on that exact commit. It does not overwrite existing tags or releases.

## APK asset

The release task is:

```sh
./gradlew --no-daemon --stacktrace :app:assembleRelease
```

After the build, the workflow finds exactly one non-unsigned APK under `app/build/outputs/apk/release/`, verifies it with `apksigner verify --verbose`, renames it to:

```text
Auxio-TS-vX.Y.Z-release.apk
```

and uploads it as the GitHub release asset. It fails if no APK is found, if multiple plausible signed APKs are found, or if signature verification fails.

## Release notes

Release notes are generated into a markdown file passed to `gh release create`. The body always starts with:

```markdown
## What's changed
```

The workflow uses the previous semver tag as the comparison base when available, prefers PR titles and authors from GitHub API metadata, and falls back to direct commit links when PR lookup fails. Missing PR metadata emits workflow warnings but does not fail the release.

## Failed release recovery

If a run fails after the version commit or tag is pushed:

1. Inspect the state:

   ```sh
   git fetch --tags --force
   git tag --list 'v*.*.*' --sort=-v:refname | head
   gh release list --limit 20
   ```

2. If the tag exists but no release exists, confirm the tag points to the intended version commit.
3. Rerun with explicit `version_tag` only if you intentionally want to retry that exact version. Do not rerun with a blank tag after a partial release, because blank input auto-increments again.
4. Delete an incorrect orphan tag only when you are certain it is unused:

   ```sh
   git push origin :refs/tags/vX.Y.Z
   ```

5. Prefer rerunning with `draft: true`, verify the APK and notes, then publish the draft manually.

## Troubleshooting

### Missing submodules

Run locally:

```sh
git submodule sync --recursive
git submodule update --init --recursive
bash scripts/check-submodules.sh
```

Do not create fake submodule files. Missing `media/core_settings.gradle` means the media submodule was not initialised.

### Missing signing secrets

The workflow fails before Gradle if any signing secret is missing. Add all four required secrets and rerun. Do not print secret values in logs or commit keystores while debugging.

### Local validation commands

Useful checks before changing the workflow:

```sh
git diff --check
find .github/workflows -type f \( -name '*.yml' -o -name '*.yaml' \) -print
bash scripts/check-submodules.sh
./gradlew tasks
./gradlew :app:assembleDebug
```


### Workflow token cannot push commit/tag

If the version-metadata commit or tag push fails, verify repository settings allow the workflow token write access and that branch protection permits `github-actions[bot]` to push release-prep commits to the chosen branch.

### Android SDK tooling missing on runner

The workflow installs required SDK components explicitly via `sdkmanager`. If setup fails, rerun and inspect the SDK diagnostics step output (environment variables, installed package list, tool paths). Do not commit `local.properties`; fix the workflow runner environment or repository/network policy instead.
