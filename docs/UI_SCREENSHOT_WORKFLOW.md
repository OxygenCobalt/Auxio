# Manual UI Screenshot + Roborazzi Workflows

## Why this exists

Auxio-TS UI work needs visual evidence beyond source inspection. These workflows provide development-only visual artifacts for review while keeping screenshot/probe logic out of the production runtime APK.

- **Manual UI Screenshots**: emulator + adb captures (runtime-like)
- **Manual Roborazzi**: deterministic JVM screenshot/layout checks (test-only)

Neither replaces Tier 2 TS18 hardware validation.

## Workflows

- `.github/workflows/manual-ui-screenshots.yml`
- `.github/workflows/manual-roborazzi.yml`

## Manual UI Screenshots inputs

- `target_ref` (required): branch/tag/SHA
- `scenario` (required): `all`, `playback_landscape`, `queue_landscape`, `home_landscape`
- `upload_apk` (required): upload `auxio-ts-debug-apk`
- `pr_number` (optional): PR number for sticky comment
- `comment_on_pr` (required boolean): post/update sticky PR comment

## Manual UI Screenshots artifacts (20-day retention)

- `auxio-ts-ui-screenshots`: PNG screenshots
- `auxio-ts-ui-screenshot-logs`: logcat + dumpsys + metadata
- `auxio-ts-debug-apk` (optional)

The workflow writes artifact URLs to `$GITHUB_STEP_SUMMARY` and can upsert a sticky PR comment marker `<!-- auxio-ts-ui-screenshots -->`.

## Manual Roborazzi inputs

- `target_ref` (required): branch/tag/SHA
- `roborazzi_task` (required): `record`, `verify`, `compare`, `verify_and_record`
- `pr_number` (optional): PR number for sticky comment
- `comment_on_pr` (required boolean): post/update sticky PR comment

## Manual Roborazzi artifacts (20-day retention)

- `auxio-ts-roborazzi-reports`: `app/build/reports/roborazzi`
- `auxio-ts-roborazzi-outputs`: `app/build/outputs/roborazzi`

The workflow writes artifact URLs to `$GITHUB_STEP_SUMMARY` and can upsert a sticky PR comment marker `<!-- auxio-ts-roborazzi -->`.

## Run from GitHub UI

1. Actions → choose workflow.
2. Keep run branch selector on `dev`.
3. Set `target_ref` to PR branch or commit SHA.
4. For PR comments, set `comment_on_pr=true` and preferably supply `pr_number`.

## Run via gh CLI

UI screenshots:

```bash
gh workflow run manual-ui-screenshots.yml \
  --ref dev \
  -f target_ref=your-branch-or-sha \
  -f scenario=all \
  -f upload_apk=false \
  -f comment_on_pr=true \
  -f pr_number=123
```

Roborazzi:

```bash
gh workflow run manual-roborazzi.yml \
  --ref dev \
  -f target_ref=your-branch-or-sha \
  -f roborazzi_task=verify \
  -f comment_on_pr=true \
  -f pr_number=123
```

## Roborazzi tasks

- `:app:recordRoborazziDebug`
- `:app:verifyRoborazziDebug`
- `:app:compareRoborazziDebug`
- `:app:verifyAndRecordRoborazziDebug`

## What these prove / do not prove

- They prove UI rendering evidence useful for development review.
- They do **not** prove TS18 hardware parity or private/native TW/TWTHEME behavior.
- TS18 parity still requires Tier 2 hardware validation evidence workflows.

## Agent usage guidance

For UI/UX tasks, agents should:
1. capture before/after artifacts,
2. reference artifact links in review notes,
3. use Roborazzi for deterministic smoke checks on stable surfaces,
4. use emulator screenshots for runtime-like behavior validation.

For TS18/head-unit UI work, screenshots should approximate 1280x720 landscape and include playback + queue impacts where relevant.

## Runtime APK cleanliness boundary

All screenshot tooling is development/test-only. Do not add ADB/screenshot/probe/Roborazzi logic to `app/src/main` runtime code.
