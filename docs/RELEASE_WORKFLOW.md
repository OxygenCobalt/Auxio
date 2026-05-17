# Manual release workflow

Auxio-TS provides a manual-only GitHub Actions release workflow at:

- `.github/workflows/manual-release.yml`

It can only be triggered from **Actions → Manual Release → Run workflow** (`workflow_dispatch`).

## Required repository secrets

Set these repository secrets before running the workflow:

- `KEYSTORE_BASE64` (base64-encoded keystore file)
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The workflow decodes the keystore into `$RUNNER_TEMP/release.keystore` and never commits it.

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

## Release notes format

The workflow generates:

```markdown
## What's changed

- [PR title](https://github.com/OWNER/REPO/pull/123) by @author
- [Commit summary](https://github.com/OWNER/REPO/commit/SHA) by @author
```

- It uses the previous semver tag as the base when available.
- It includes non-PR commits as commit links.

## Troubleshooting signing failures

- Missing secret values fail fast with a clear error.
- Invalid `KEYSTORE_BASE64` fails during decode.
- If Gradle signing properties are not supplied, release signing config is not applied.
- Ensure keystore alias/password values match the keystore contents.

## Verify the APK is signed

After downloading the APK, verify with Android build tools:

```bash
apksigner verify --verbose Auxio-TS-vX.Y.Z-release.apk
```
