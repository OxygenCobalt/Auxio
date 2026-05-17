# GitHub Copilot Instructions — com.tw.music

## Target

This is `com.tw.music`, a system-level music app for **TS18-class Android head units** running **Android 13**, compiled against SDK 29 (APK origin), `sharedUserId=android.uid.system`. It is **not** a generic phone media player.

Working deployment target: **TS18 4GB / 64GB, 1280×720 landscape, Android 13**. See `docs/target-device-ts18-android13.md` for the full device profile and vendor environment assumptions.

## Package name

**The package name `com.tw.music` must never change.** The app ships as a system package; any rename breaks update compatibility, launcher integration, and vendor bindings permanently.

## Canonical edit surface

- **Edit only `app/apktool/`** — this is the decoded Apktool tree (smali + resources).
- All code changes are made in smali. All resource changes are made in `app/apktool/res/`.
- Build with `scripts/02_build_unsigned.sh`.

## Reference surfaces — read-only

| Path | Purpose |
|---|---|
| `reference/jadx-raw/` | Raw JADX Java output — reference only |
| `reference/jadx-aliased/` | JADX output with auto-generated readable aliases — reference only |
| `reference/firstparty-jadx/` | Focused first-party JADX output — read-only planning aid |
| `reference/vendor-jadx/` | Focused vendor/TW JADX output — read-only planning aid |
| `reference/jadx-mt/` | Optional MT Manager-normalised reference — may be absent unless generated |

**Never edit anything under `reference/`.** These are regenerated from the APK and any hand-edits will be lost.

Alias caveat: packages such as `com.p060tw.music` / `com.p073tw.music` in JADX output are alias artefacts; runtime package identity remains `com.tw.music`.

## TW / TWTHEME / vendor integrations — do not break

The app runs inside a **TW vendor firmware environment** (TWTHEME, com.tw.* ecosystem). All of the following are compatibility boundaries, not cleanup targets:

- Package `com.tw.*` namespace and all component names declared in `AndroidManifest.xml`
- Intent actions: `com.tw.music.action.cmd`, `.prev`, `.next`, `.pp`
- AIDL interfaces: `com.tw.service.xt.aidl.*` (ITWCommandAidl, IMusicCallBack, IRadioCallBack, IVideoCallBack, IBTCallBack, ITWCommandCallbackAidl)
- System properties: `persist.tw.ijk`, `persist.tw.ijk.noerror`, `persist.tw.ijk.opensles`
- EQ launch: `ComponentName("com.tw.eq", "com.tw.eq.EQActivity")`
- Radio hand-off surface: `com.tw.radio.*`
- Widget provider: `com.tw.music.view.MusicWidgetProvider`
- Theme: `@style/AppTheme` / `twtheme` / TWTHEME — theme resources consumed by the system launcher, theme switcher, or widget must remain compatible
- `android.uid.system` shared user ID — do not add permissions that would require a different signing identity
- TLink coexistence — `MediaSession` / `PlaybackState` / `MediaMetadata` are read by the TLink / CarPlay / Android Auto bridge; publish state correctly and conservatively

## Readability-first sequencing

- Prioritise readability closure for playback/control path before implementation changes.
- Do not assume MediaSession/notification integration is already correct; treat it as a future implementation phase after readability and architecture docs.

## Prefer compatibility-preserving changes

- Preserve existing behaviour before improving it.
- Do not perform speculative cleanups or bulk renames.
- Validate every meaningful change with a rebuild (`scripts/02_build_unsigned.sh`).
- Run `scripts/01_refresh_reports.sh` after meaningful changes.

## GitHub Actions run-input discipline (important for agents)

- Use `README_ACTIONS.md` for workflow input semantics before advising or triggering runs.
- If a workflow includes a branch-write toggle input, default it to **false** unless commit/push is explicitly intended.
- In this repo, `maintenance.yml` uses `allow_push`:
  - `allow_push=false` keeps runs read-only for branch history (artifacts/summaries only).
  - `allow_push=true` enables auto `git add/commit/push` for generated docs when changes exist.
- Always communicate the exact input values in recommendations (for example: `task`, `allow_push`, `upload_artifacts`).

## De-obfuscation is an ongoing task

Many class, method, and field names are obfuscated (e.g. `C0781k`, `p060tw`). Improving readability is part of the development scope. See `docs/deobfuscation-guidance.md` for the approved workflow.

## More detail

- Smali rules: `.github/instructions/smali.instructions.md`
- Resource / UI rules: `.github/instructions/resources.instructions.md`
- Review / compatibility rules: `.github/instructions/review.instructions.md`
- Full agent handbook: `AGENTS.md`
- Development milestones: `docs/agent-milestones.md`
- Target device profile and vendor environment: `docs/target-device-ts18-android13.md`

## Required static validation for smali/runtime tasks

Run from repo root after smali/runtime changes:

```bash
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/build_source_shim.sh
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
git diff --stat
git diff --numstat
git status --short
```

If `apktool` exists, optionally run:

```bash
apktool b app/apktool -o .local/<task-name>-check.apk
```

Do not commit `.local` APK outputs.
