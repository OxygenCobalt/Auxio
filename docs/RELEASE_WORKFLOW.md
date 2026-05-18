# Manual release workflow

Auxio-TS provides a manual-only GitHub Actions release workflow at:

- `.github/workflows/manual-release.yml`

It can only be triggered from **Actions → Manual Release → Run workflow** (`workflow_dispatch`).

## Required repository settings

- **Actions workflow token permissions** must allow write access sufficient for:
  - committing `app/build.gradle` version metadata to the branch;
  - creating/pushing git tags;
  - creating GitHub releases.
- Workflow-level permissions in `.github/workflows/manual-release.yml` are:
  - `contents: write`
  - `pull-requests: read`

## Required repository secrets

Set these repository secrets before running the workflow:

- `KEYSTORE_BASE64` (base64-encoded keystore file)
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The workflow decodes the keystore into `$RUNNER_TEMP/release.keystore` and never commits it. GitHub-hosted runner temp files are ephemeral and are discarded when the job ends.

## Required submodule setup

Release and CI builds require initialized submodules (including nested ones):

- `media/core_settings.gradle`
- `media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg`
- `musikr/src/main/cpp/taglib`

The workflows enforce this using:

- `scripts/check-submodules.sh`

If submodules are missing, workflows fail with:

```text
Submodules are not initialized. Do not create missing files manually. Run git submodule update --init --recursive or ensure actions/checkout uses submodules: recursive.
```

## Workflow inputs

- `version_tag` (optional): `vMAJOR.MINOR.PATCH` (example: `v4.0.11`)
- `draft` (required boolean): create release as draft
- `prerelease` (required boolean): mark release as prerelease

## Version model used in this repository

Primary app version metadata is stored in `app/build.gradle`:

- `defaultConfig.versionName`
- `defaultConfig.versionCode`

Release workflow behavior:

1. If `version_tag` is provided, it must match `vMAJOR.MINOR.PATCH`.
2. If `version_tag` is blank:
   - latest `v*.*.*` tag is discovered and patch is incremented;
   - if no tag exists, it falls back to `versionName`;
   - if `versionName` is not semver-like, it uses `v0.1.0`.
3. `versionName` is synced to the selected tag without `v`.
4. `versionCode` increments monotonically by `+1`.

To keep releases reproducible, version metadata is committed before tag/release creation so the tag points to the exact release version commit.

## Draft releases

Set `draft: true` to create an unpublished draft release.
Set `draft: false` to publish immediately.

## Failed-release recovery

When a run fails after creating a version commit and/or tag, recover carefully:

1. Inspect what already exists:

   ```bash
   git fetch --tags --force
   git tag --list 'v*.*.*' --sort=-v:refname | head
   gh release list --limit 20
   ```

2. Check whether a tag exists without a release:
   - if a release already exists for the tag, do not reuse it;
   - if only the tag exists and it points to the intended release commit, rerun with explicit `version_tag` set to that tag.

3. Do **not** rerun with blank `version_tag` if a previous failed run already created a tag and version commit.
   - Blank rerun auto-increments and may create an unintended next version.

4. Delete an orphaned tag only when you are certain it is incorrect and unused:

   ```bash
   git push origin :refs/tags/vX.Y.Z
   ```

5. Prefer rerunning as draft first (`draft: true`), verify artifacts/notes, then publish.

## Release notes format

The workflow generates:

```markdown
## What's changed

- [PR title](https://github.com/cbkii/Auxio-TS/pull/<PR_NUMBER>) by @author
- [Commit summary](https://github.com/cbkii/Auxio-TS/commit/<COMMIT_SHA>) by @author
```

- It uses the previous semver tag as the base when available.
- It includes non-PR commits as commit links.
- Missing PR metadata/API lookup failures degrade safely to commit-note entries with concise workflow warnings.

## Troubleshooting signing failures

- Missing secret values fail fast with a clear error.
- Invalid `KEYSTORE_BASE64` fails during decode.
- If Gradle signing properties are not supplied, release signing config is not applied.
- Ensure keystore alias/password values match the keystore contents.

## Local/manual validation commands

Run these before release troubleshooting and after CI/workflow changes:

```sh
git submodule sync --recursive
git submodule update --init --recursive
test -f media/core_settings.gradle
test -d media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg
test -d musikr/src/main/cpp/taglib
./gradlew tasks
./gradlew test
./gradlew assembleDebug
./gradlew lint
find scripts -type f -name '*.sh' -print -exec sh -n {} \;
```

## Successful release checklist

A successful run should produce:

- signed release APK (workflow artifact upload succeeds);
- GitHub Release for the intended tag;
- release notes containing `## What's changed` and bullet items;
- `$GITHUB_STEP_SUMMARY` entry with hyperlink to the created release.

## Verify the APK is signed

After downloading the APK, verify with Android build tools:

```bash
apksigner verify --verbose Auxio-TS-vX.Y.Z-release.apk
```
