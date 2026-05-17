# GitHub Actions Guide — `com.tw.music`

This guide explains how to use the GitHub Actions workflows in this repository.

It is written for:
- new contributors
- maintainers who are not CI experts
- coding agents (Copilot/Codex) that need quick operational context

---

## 1) What this automation is for

This repo is an Apktool/JADX reverse-engineering workspace for `com.tw.music` (TS18 Android head-unit environment).

The Actions suite helps you do 3 kinds of work:

1. **Guardrails** (CI safety checks)
   - Make sure critical vendor/TW integration strings still exist.
2. **Build + analysis**
   - Build unsigned APKs.
   - Export JADX reference trees.
   - Import MT-normalized APK references.
   - Run parity diffs between APKs.
3. **Maintenance + release**
   - Refresh maps/reports and optional auto-commit.
   - Sign and release APKs (secret-dependent).

---

## 2) How to run workflows

### Automatic runs
- `CI` runs automatically on pushes to `main`/`work` and on pull requests.

### Manual runs (`workflow_dispatch`)
1. Open **GitHub → Actions**.
2. Select a workflow.
3. Click **Run workflow**.
4. Set inputs (or keep defaults).

### Where to find outputs
- **Step Summary**: quick human-readable run results.
- **Artifacts**: downloadable files (APKs, JADX output, logs, reports).
- **Repo commits**: only `Maintenance` can push generated docs, and only if explicitly enabled.

### Agent/operator dispatch rule (important)
- For any workflow that can write to the branch, keep the write-toggle input **false** unless a run is explicitly intended to commit generated outputs.
- In this repo today, that toggle is `allow_push` on `maintenance.yml`:
  - `allow_push=false` (default): dry-run style regeneration + artifact output, **no branch write**.
  - `allow_push=true`: commit/push generated docs when diff exists.
- Copilot/Codex/GitHub Agents should explicitly state in their run instructions which value they expect for write-toggle inputs.

---

## 3) Workflow index (quick reference)

| Workflow | Trigger | Main purpose | Safe by default? |
|---|---|---|---|
| `ci.yml` | auto + manual | Vendor/token guard checks | Yes |
| `build.yml` | manual | Build unsigned APK | Yes |
| `jadx-export.yml` | manual | Export raw/aliased JADX references | Yes |
| `mt-import.yml` | manual | Import MT APK reference decode/export | Yes |
| `parity-diff.yml` | manual | Compare baseline/candidate APK manifest + resources | Yes |
| `maintenance.yml` | manual | Refresh docs/maps/reports or init mapping notes | Yes (`allow_push=false`) |
| `sign-release.yml` | manual | Build, sign, verify, tag, release | **Sensitive** (secrets + publication) |

> Note: `_reusable-build-unsigned.yml` is an internal reusable workflow called by `build.yml`.

---

## 4) Detailed workflow guide

## CI (`.github/workflows/ci.yml`)

### What it does
- `vendor-guard`: checks protected vendor tokens from `tools/vendor-audit/protected_tokens.txt`.
- `parity-smoke`: checks key TW/music integration strings and a manifest structural check.

### Why it exists
- Prevent accidental breakage of vendor/TW integration boundaries.

### When to run
- Always (it auto-runs on push/PR).
- Manual rerun after large smali/resource edits.

### When not to run
- N/A (cheap safety workflow).

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `run_vendor_guard` | boolean | `true` | Whether the `vendor-guard` job runs on manual dispatch. | `true`: scan protected vendor tokens. `false`: skip vendor token scan job. |
| `run_parity_smoke` | boolean | `true` | Whether the `parity-smoke` job runs on manual dispatch. | `true`: run structural/token smoke checks. `false`: skip parity-smoke job. |

### Outputs
- No artifacts.
- Detailed markdown summary in each job.

---

## Build (`.github/workflows/build.yml`)

### What it does
- Builds `dist/com.tw.music-unsigned.apk` from `app/apktool`.
- Uses shared reusable build logic.

### Why it exists
- Fast way to verify that the editable Apktool tree still compiles.

### When to run
- Before JADX export, parity checks, or release preparation.

### When not to run
- If you only need token checks (`CI`) or report refresh (`Maintenance`).

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `apktool_ver` | string | `""` (auto-latest) | Apktool version downloaded by setup. | Any valid Apktool release version string. |
| `jadx_ver` | string | `""` (auto-latest) | JADX version pre-fetched by setup (for downstream consistency/caching). | Any valid JADX release version string. |
| `upload_artifact` | boolean | `true` | Whether built unsigned APK is uploaded as an artifact. | `true`: upload artifact. `false`: keep output only in runner workspace. |
| `artifact_name` | string | `""` (auto) | Optional artifact name override. | Empty: auto name (`unsigned-apk-<run_id>`). Non-empty: use provided name. |

### Outputs
- Unsigned APK artifact (if enabled).
- Step summary with APK path/size/artifact name.

---

## JADX Export (`.github/workflows/jadx-export.yml`)

### What it does
- Exports decompiled reference output to:
  - `reference/jadx-raw/`
  - `reference/jadx-aliased/`
- Supports export modes: `raw`, `aliased`, `both`.
- Optional inline build before export.

### Why it exists
- Keeps readable reference output fresh for reverse-engineering and rename work.

### When to run
- After generating a fresh unsigned APK.
- After updating `mappings/manual-enigma/` (for aliased view).

### When not to run
- If you only need to verify compile success (`Build` is faster).

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `source_apk` | string | `dist/com.tw.music-unsigned.apk` | APK file used as JADX input. | Repo-relative path to an APK. Must exist at runtime. |
| `mode` | choice | `both` | Which export trees to generate. | `raw`: only `reference/jadx-raw`; `aliased`: only `reference/jadx-aliased`; `both`: generate both. |
| `build_first` | boolean | `false` | Whether to build unsigned APK from `app/apktool` before export. | `true`: run inline build first. `false`: use existing `source_apk`. |
| `apktool_ver` | string | `""` (auto-latest) | Apktool version for optional build step. | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `jadx_ver` | string | `""` (auto-latest) | JADX version used for export. | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `clean_output_dirs` | boolean | `true` | Whether existing files in export directories are cleared first. | `true`: remove old files before export. `false`: keep existing files (risk mixed/stale output). |
| `upload_artifacts` | boolean | `true` | Whether export/log artifacts are uploaded. | `true`: upload selected artifacts. `false`: no artifact upload. |
| `raw_retention_days` | string (integer) | `30` | Artifact retention for raw/aliased output artifacts. | Numeric string (e.g., `7`, `30`, `90`). |
| `log_retention_days` | string (integer) | `14` | Artifact retention for JADX log artifact. | Numeric string (e.g., `7`, `14`, `30`). |

### Outputs
- Raw/aliased JADX artifacts (if enabled).
- JADX logs artifact (if enabled).
- Step summary with counts and status.

### Caveat
- `reference/*` outputs are reference-only; do not hand-edit.

---

## MT Import (`.github/workflows/mt-import.yml`)

### What it does
- Decodes MT-normalized APK into `.input/apktool-mt/`.
- Exports JADX MT reference to `reference/jadx-mt/`.

### Why it exists
- Supports comparison/de-obfuscation workflows against MT-normalized builds.

### When to run
- When `dist/com.tw.music-MT.apk` is updated.
- When you need fresh MT reference output.

### When not to run
- If not doing MT/de-obfuscation comparison work.

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `mt_apk` | string | `dist/com.tw.music-MT.apk` | MT-normalized APK used for decode/export. | Repo-relative APK path; must exist. |
| `apktool_ver` | string | `""` (auto-latest) | Apktool version for decode step. | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `jadx_ver` | string | `""` (auto-latest) | JADX version for MT export. | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `clean_output_dirs` | boolean | `true` | Whether to clear existing `reference/jadx-mt` files before export. | `true`: clear old files. `false`: keep old files (risk stale/mixed output). |
| `upload_artifacts` | boolean | `true` | Whether MT artifacts are uploaded. | `true`: upload output/log artifacts. `false`: skip artifact uploads. |
| `output_retention_days` | string (integer) | `30` | Retention for MT output artifact. | Numeric string (e.g., `30`). |
| `log_retention_days` | string (integer) | `14` | Retention for MT log artifact. | Numeric string (e.g., `14`). |

### Outputs
- MT JADX output artifact (if enabled).
- MT log artifact (if enabled).
- Step summary with decode/export counts.

---

## Parity Diff (`.github/workflows/parity-diff.yml`)

### What it does
- Compares baseline vs candidate APK by:
  - decoded `AndroidManifest.xml` diff
  - decoded `res/` inventory diff

### Why it exists
- Detects unintended manifest/resource drift.

### When to run
- Before release.
- After significant modifications to `app/apktool`.

### When not to run
- For quick token-only safety checks (`CI` is faster).

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `build_candidate` | boolean | `true` | Whether workflow builds candidate APK before diff. | `true`: rebuild `dist/com.tw.music-unsigned.apk`. `false`: use existing `candidate_apk`. |
| `baseline_apk` | string | `dist/com.tw.music.apk` | Baseline APK for comparison. | Repo-relative APK path; must exist. |
| `candidate_apk` | string | `dist/com.tw.music-unsigned.apk` | Candidate APK for comparison. | Repo-relative APK path; must exist. |
| `fail_on_diff` | boolean | `false` | Whether detected diffs fail the workflow. | `true`: any diff causes failure. `false`: diffs reported but run can succeed. |
| `apktool_ver` | string | `""` (auto-latest) | Apktool version used by optional candidate build and decode. | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `jadx_ver` | string | `""` (auto-latest) | JADX version pre-fetched by setup (consistency/caching). | Leave blank to resolve latest release automatically, or set a specific version string to pin. |
| `upload_artifacts` | boolean | `true` | Whether parity diff files are uploaded. | `true`: upload diff reports artifact. `false`: no upload. |
| `artifact_retention_days` | string (integer) | `30` | Retention for parity diff artifact. | Numeric string (e.g., `30`). |
| `clean_existing_reports` | boolean | `true` | Whether prior parity report files are deleted first. | `true`: clean reports before run. `false`: keep existing files (risk stale report mix). |

### Outputs
- Diff artifacts (if differences exist and upload is enabled).
- Step summary with diff/no-diff status.

---

## Maintenance (`.github/workflows/maintenance.yml`)

### What it does
Two tasks:
- `refresh-reports`
  - regenerates docs/maps and docs/reports files
  - optionally commits/pushes updates
- `init-mapping-notes`
  - creates `docs/deobf/enigma-notes.md` scaffold if missing

### Why it exists
- Keeps generated reports and mapping notes consistent and discoverable.

### When to run
- `refresh-reports`: after meaningful smali/resource/reference changes.
- `init-mapping-notes`: when starting rename/de-obfuscation note work.

### When not to run
- If you only need build/analysis outputs and not docs maintenance.

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `task` | choice | `refresh-reports` | Which maintenance operation to run. | `refresh-reports`: regenerate docs maps/reports. `init-mapping-notes`: create notes scaffold if absent. |
| `allow_push` | boolean | `false` | Whether `refresh-reports` is allowed to commit/push generated changes. | `false` (safe): no push. `true`: perform commit/push when staged changes exist. |
| `upload_artifacts` | boolean | `true` | Whether generated outputs are uploaded as backup artifacts. | `true`: upload maintenance artifact bundle. `false`: skip upload. |
| `artifact_retention_days` | string (integer) | `14` | Retention for maintenance artifact bundle. | Numeric string (e.g., `14`). |

### Outputs
- Maintenance artifact bundle (if enabled).
- Optional commit/push only when `allow_push=true`.
- Step summary with job status, trigger/input details, plain-English progress, generated file counts, push status, artifact/log links, and next steps.

---

## Sign and Release (`.github/workflows/sign-release.yml`)

### What it does
Builds the APK from `app/apktool/` and produces up to **five APK variants** in one run.
Failure of any single variant does **not** abort the others.

| Variant | sharedUserId | Signing | Notes |
|---|---|---|---|
| **V1 `ts18-system`** | `android.uid.system` (intact) | KEYSTORE_BASE64 — v1+v2+v3 schemes | ⭐ **Priority TS18 release target** |
| **V2 `signed-uid`** | `android.uid.system` (intact) | KEYSTORE_BASE64 — standard path | Secondary system install |
| **V3 `signed-no-uid`** | Removed from manifest | KEYSTORE_BASE64 | Portable; no platform key required |
| **V4 `pixel9a-compat-no-uid`** | Removed in temp tree | KEYSTORE_BASE64 | Pixel/dev runtime-smoke only; includes `android.tw.john.*` shims |
| **V5 `unsigned-debug`** | `android.uid.system` (intact) | None (zipalign only) | Debug / inspection reference |

All variants are optional. The run succeeds as long as at least one APK is produced.
If `KEYSTORE_BASE64` is missing, V1–V4 are skipped and only V5 is produced (with a warning).

### Why it exists
- Controlled release path for signed APK distribution targeting TS18 head units.

### When to run
- Only for intentional release operations.

### When not to run
- Not for normal development builds.

### Manual inputs

| Input | Type | Default | What it controls | Values / behavior |
|---|---|---|---|---|
| `tag` | string | `""` (auto) | Release tag used for naming/tagging/release operations. | Strict semver `vX.Y.Z`. **Leave blank** to auto-increment patch from latest existing tag (or start at `v1.0.0`). |
| `draft` | boolean | `false` | Whether created GitHub Release is draft. | `true`: release is draft. `false`: published immediately. |
| `apktool_ver` | string | `""` (auto-latest) | Apktool version used for unsigned build step. | Leave blank to resolve latest release automatically. |
| `jadx_ver` | string | `""` (auto-latest) | JADX version pre-fetched by setup. | Leave blank to resolve latest release automatically. |
| `create_tag` | boolean | `true` | Whether workflow creates/pushes git tag if missing. | `true`: create/push missing tag. `false`: skip creation. |
| `create_release` | boolean | `true` | Whether workflow calls GitHub Release creation step. | `true`: create/reuse release entry. `false`: skip release creation. |
| `upload_artifacts` | boolean | `true` | Whether all produced APKs are uploaded as a workflow artifact bundle. | `true`: upload bundle. `false`: skip upload. |
| `artifact_retention_days` | string (integer) | `90` | Retention for APK artifact bundle. | Numeric string (e.g., `30`, `90`). |

### Required secrets
- `KEYSTORE_BASE64` — base64-encoded keystore `.jks`
- `KEYSTORE_PASSWORD` — keystore password
- `KEY_ALIAS` — key alias within the keystore
- `KEY_PASSWORD` — key password (falls back to `KEYSTORE_PASSWORD` if unset)

### Outputs
- Up to four APK variant files in `dist/` (V1 always first in the release asset list).
- Workflow artifact bundle with all produced APKs (if enabled).
- Git tag / GitHub release (if enabled).
- Job summary with per-variant outcome table.

### Safety notes
- Keystore material is decoded into runner temp storage — never committed to the workspace.
- Missing keystore is a soft-degradation (warning + skip), not a fatal error.
- `fail_on_unmatched_files: false` on the release action means absent variants are silently skipped.
- **Exception**: the **base unsigned APK build** (`build_base` step) is the one step that is NOT soft-fail. All four variants depend on a successfully assembled APK; if apktool cannot assemble the smali+resources tree, the run exits early with a clear error. Fix the underlying smali/resource error and re-run.

---

## 5) Recommended usage patterns

### A) “I changed smali and want a safe quick check”
1. Push branch.
2. Let `CI` run automatically.
3. If needed, run manual `CI` with one job toggled on/off.

### B) “I need a fresh unsigned APK”
1. Run `Build` with defaults.
2. Download artifact if needed.

### C) “I need fresh decompiled references”
1. Run `JADX Export`.
2. Use `mode=both` for full refresh.
3. Use `build_first=true` if APK is stale.

### D) “I want manifest/resource drift visibility”
1. Run `Parity Diff`.
2. Set `fail_on_diff=true` only when you want strict gate behavior.

### E) “I need to refresh docs/reports in-repo”
1. Run `Maintenance` with `task=refresh-reports`.
2. Keep `allow_push=false` for preview-only.
3. Set `allow_push=true` only when you intentionally want repo updates.

### F) “I need a signed release APK”
1. Confirm signing secrets are configured (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`).
2. Run `Sign and Release`.
3. Leave the `tag` field blank to auto-increment from the latest tag.
4. Use `draft=true` if you want manual review before publishing.
5. Check the job summary for per-variant outcomes — V1 (TS18 system) is the priority asset.

---

## 6) Secrets and sensitive operations

### Sensitive workflow
- `sign-release.yml` is the only workflow that requires signing secrets and can publish release assets.

### Caution points
- `create_tag=true` and `create_release=true` publish external release state.
- `maintenance.yml` can push commits only if `allow_push=true`.

---

## 7) Troubleshooting and practical limits

- **APK not found**: verify input paths are repo-relative and the file exists.
- **No artifact uploaded**: check whether `upload_*` input was set to `false`.
- **Retention parsing errors**: retention inputs are numeric strings (example: `30`).
- **Signing failures**: usually missing/invalid secrets or keystore alias/password mismatch.
- **Parity diff failed with code 2**: indicates diff command operational error, not just content differences.

---

## 8) Historical note (short)

Older monolithic/script-dispatch workflow paths were retired. The current suite is organized into dedicated GitHub-native workflows documented above.
