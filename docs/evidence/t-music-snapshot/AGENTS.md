# Agent Handbook — com.tw.music

## What this repo is

`com.tw.music` is a system-level music application for **TS18-class Android head units** — in-car Android 13 devices with a 1280×720 landscape touchscreen. The app runs under `android.uid.system` (shared system UID), integrates with TW/vendor AIDL services, and is distributed as a signed system package.

**Working deployment target: TS18 4GB / 64GB, 1280×720 landscape, Android 13.** See `docs/target-device-ts18-android13.md` for the full device profile and TW vendor environment assumptions.

This repo contains a bootstrapped reverse-engineering workspace built from the original APK using Apktool and JADX. The canonical editable surface is the decoded Apktool tree. Development work continues from this point toward a maintainability-improved, feature-enhanced release.

---

## Editable vs reference-only

| Surface | Path | Status |
|---|---|---|
| Decoded smali + resources | `app/apktool/` | **Editable — canonical source of truth** |
| Raw JADX Java output | `reference/jadx-raw/` | Read-only reference |
| Aliased JADX Java output | `reference/jadx-aliased/` | Read-only reference |
| Focused first-party JADX reference | `reference/firstparty-jadx/` | Read-only planning/reference aid |
| Focused vendor/TW JADX reference | `reference/vendor-jadx/` | Read-only planning/reference aid |
| MT-normalised reference (optional) | `reference/jadx-mt/` | Read-only readability aid (may be absent unless generated) |
| Manual rename mappings | `mappings/manual-enigma/` | Editable — Enigma format |
| JADX alias caches | `mappings/jadx/*.jobf` | Do not hand-edit |

**Never edit anything under `reference/`.** These directories are regenerated from the APK.

Alias caveat: JADX packages such as `com.p060tw.music` / `com.p073tw.music` are reference artefacts; runtime package identity remains `com.tw.music`.

---

## Hard rules

1. **Package name `com.tw.music` must never change.**
2. Do not edit anything under `reference/`.
3. All code changes go into `app/apktool/smali*/`. All resource changes go into `app/apktool/res/`.
4. If JADX output contradicts smali, trust the smali.
5. Preserve all component names, intent action strings, AIDL interface tokens, system property keys, and vendor service references unless a task explicitly requires changing them.
6. Put human-reviewed renames in `mappings/manual-enigma/` using Enigma `.mapping` format.
7. Do not hand-edit `mappings/jadx/*.jobf`.
8. Run `scripts/01_refresh_reports.sh` after meaningful changes.
9. Run `scripts/03_jadx_export_raw.sh` and `scripts/04_jadx_export_aliased.sh` after code changes to keep reference outputs fresh.
10. Use `scripts/02_build_unsigned.sh` for rebuilds.
11. Never commit or reference keystores, `.env` files, signing credentials, or API keys.
12. `android.uid.system` shared user ID — do not add permissions requiring a different UID.

---

## Standard workflow

```
# 1. Read smali and/or JADX reference to understand the area you are changing
# 2. Make smali / resource changes in app/apktool/
bash scripts/02_build_unsigned.sh
# 3. Inspect dist/com.tw.music-unsigned.apk — confirm build success
bash scripts/01_refresh_reports.sh
# 4. Required static validators for smali/runtime tasks
python3 tools/readability/07_validate_readability_reports.py
python3 tools/smali/validate_smali_static.py
bash scripts/08_verify_vendor_tokens.sh
bash tools/readability/06_diff_size_guard.sh
# 5. Optionally refresh JADX reference
bash scripts/03_jadx_export_raw.sh dist/com.tw.music-unsigned.apk
bash scripts/04_jadx_export_aliased.sh dist/com.tw.music-unsigned.apk
```

## GitHub Actions dispatch guidance (human + AI contributors)

- Use `README_ACTIONS.md` as the canonical workflow input guide.
- For any workflow that can write commits, keep write toggles **off by default** unless you explicitly intend to push generated outputs.
- Current write toggle:
  - `maintenance.yml` → `allow_push`
    - `false` (default): run reports/notes generation without writing to branch.
    - `true`: allows `git add/commit/push` for generated docs when diffs exist.
- When proposing or triggering a run, always state the exact input values being used, especially `allow_push`.

---

## Vendor and system integration boundaries

These are **compatibility boundaries, not cleanup targets**. The app exists within a **TW vendor firmware environment** (TWTHEME / `com.tw.*` ecosystem):

- Intent actions: `com.tw.music.action.cmd`, `.prev`, `.next`, `.pp`
- AIDL interfaces: `com.tw.service.xt.aidl.ITWCommandAidl`, `IMusicCallBack`, `IRadioCallBack`, `IVideoCallBack`, `IBTCallBack`, `ITWCommandCallbackAidl`
- System properties: `persist.tw.ijk`, `persist.tw.ijk.noerror`, `persist.tw.ijk.opensles` (and any other `persist.tw.*` / `persist.media.*` properties found in the codebase)
- EQ launch: `ComponentName("com.tw.eq", "com.tw.eq.EQActivity")`
- Radio hand-off: `com.tw.radio.*` intent surfaces
- Widget: `com.tw.music.view.MusicWidgetProvider` and its resource references
- Theme / TWTHEME: `@style/AppTheme`, `twtheme`, any `tw_`-prefixed resource name — the TW theme switcher controls the visual palette
- Playback engine: `TWMediaPlayer` / IjkMediaPlayer wrapping the `persist.tw.ijk*` property checks
- TLink coexistence: `MediaSession` / `PlaybackState` / `MediaMetadata` are read by the TLink / CarPlay / Android Auto bridge; publish state correctly and conservatively

Full enumeration: `docs/reports/vendor-hooks.txt`

---

## Development scope ahead

### De-obfuscation and readability (ongoing)

Much of the codebase uses obfuscated names (`C0781k`, `p060tw`, `p011c/p015b/...`). Improving readability is part of the development scope, not a separate phase. Proceed conservatively:

- Analyse smali behaviour before renaming anything.
- One logical rename at a time; rebuild and verify.
- Record every rename in `mappings/manual-enigma/`.
- Add inline smali comments (`# purpose: ...`) for complex logic.
- See `docs/deobfuscation-guidance.md` for the full workflow.

### Media controls and notification (future implementation phase)

The media session, playback state publication, metadata publication, and notification media controls are a later implementation phase after readability closure and architecture documentation. When working in this area:

- Preserve all existing `com.tw.music.action.*` broadcast dispatch — this is the TW control surface for hardware keys and steering-wheel controls.
- Improve `MediaSession`/`MediaSessionCompat` lifecycle management and state publication.
- Improve `PlaybackState` (correct state enum, position, speed, actions mask).
- Improve `MediaMetadata` publication (title, artist, album, duration, artwork URI).
- Improve notification `MediaStyle` actions (correct icons, correct action semantics).
- On **Android 13**, the system media notification is driven directly from `MediaSession` state — ensure metadata is consistently populated.
- On **TS18**, the vendor launcher and TLink / CarPlay / Android Auto bridge may read `MediaSession` state and notification content — treat all published state as externally visible.
- Do not replace `TWMediaPlayer`/IjkMediaPlayer with a different engine without first proving vendor-property compatibility on TS18.
- Do not migrate wholesale to Media3/ExoPlayer without explicit scope confirmation and vendor-compat proof.

### UI and asset improvement

- Head-unit-first: **1280×720 landscape** baseline layout, large touch targets (≥48 dp primary controls, ≥56–64 dp for play/pause/prev/next), low distraction.
- 4 GB RAM class device: avoid heavy UI, large in-memory bitmaps, or excessive layout nesting.
- Player screen, list screen, split-screen, settings layout are all in scope.
- Preserve all resource names referenced by `MusicWidgetProvider` or vendor components.
- Preserve TWTHEME / `@style/AppTheme` compatibility — the TW theme switcher controls the visual palette.
- See `.github/instructions/resources.instructions.md` for full rules.

### Maintainability

- Add smali comments for non-obvious logic.
- Document vendor boundary classes with header comments.
- Keep `docs/deobf/` notes updated as understanding improves.
- Update `mappings/manual-enigma/` as renames are confirmed.

---

## What "done" looks like for a change

A change is acceptable when:

1. `scripts/02_build_unsigned.sh` exits 0 and produces `dist/com.tw.music-unsigned.apk`.
2. No vendor-boundary string, token, component name, or property key has been altered (or the alteration is explicitly justified and documented).
3. Any rename has a `mappings/manual-enigma/` entry.
4. The commit message describes the behavioural change or improvement clearly.
5. No secrets or credentials are committed.

---

## GitHub authentication for Codex agents

- Codex environment authentication is provided via `GH_TOKEN`. `GITHUB_TOKEN` may also exist, but use `GH_TOKEN` as the preferred token for `gh`.
- Use `GH_REPO` for repo context; default to `${GH_REPO:-cbkii/t-music}` when unset.
- Never print, echo, persist, commit, or log token values.
- Use `gh`/GitHub API only when a task requires PR comments, review threads, issue comments, workflow runs/logs/reruns/dispatches, releases, or authenticated git operations.
- Basic capability checks must be limited to:
  - `test -n "$GH_TOKEN"`
  - `gh auth status --hostname github.com`
  - `gh repo view "${GH_REPO:-cbkii/t-music}" --json nameWithOwner,viewerPermission,isPrivate,url`
  - `git ls-remote origin HEAD`
- Do not use `statusCheckRollup` as an auth/environment preflight. Do not treat `statusCheckRollup` (or check-rollup GraphQL failures) as proof that GitHub auth is broken.
- For PR metadata, query only basic PR fields first.
- For CI data, prefer REST/Actions queries and `gh run view --log-failed` over GraphQL check-rollup fields.
- Stop for auth/environment reasons only when one of these is true:
  - `GH_TOKEN` is missing.
  - `gh auth status` fails.
  - Repo access fails.
  - Insufficient permissions (e.g., missing write access) for a task that requires it.
  - Basic PR access fails when PR access is required.
  - REST Actions/log access fails when CI logs are required.
- Do not perform a local-only substitute when a task explicitly requires authenticated GitHub metadata or write access.

---

## Important paths

| Purpose | Path |
|---|---|
| App entry points | `docs/maps/activities.txt`, `docs/maps/services.txt` |
| Widget / receiver | `docs/maps/receivers.txt` |
| Vendor hook enumeration | `docs/reports/vendor-hooks.txt` |
| JADX decompile failures | `docs/reports/jadx-problems.txt` |
| Manual rename notes | `docs/deobf/enigma-notes.md` (create if absent) |
| Rename policy | `docs/deobf/rename-policy.md` |
| De-obfuscation guide | `docs/deobfuscation-guidance.md` |
| Development milestones | `docs/agent-milestones.md` |
| Target device profile and vendor environment | `docs/target-device-ts18-android13.md` |
| Signing guidance | `docs/manual-steps/02-release-signing.md` |
| Resource/UI rules | `.github/instructions/resources.instructions.md` |
| Review rules | `.github/instructions/review.instructions.md` |
