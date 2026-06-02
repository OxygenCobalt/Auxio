# AGENTS.md — Auxio-TS coding authority

## Project stance
- Start documentation navigation from `docs/README.md`.
- For Codex/environment setup and validation flow, use `docs/DEVELOPMENT.md`.
- Prefer consolidation/removal of stale docs over keeping historical wrappers.
- **Auxio-TS is a TS18/TW/TWTHEME variant app.** TS18/TW/TWTHEME parity is the product target.
- Android-standard APIs are the preferred **first implementation layer** (Tier 1), not the final authority.
- Keep TS18/TW integration in adapter/facade boundaries.
- Use `docs/evidence/t-music-snapshot/` as evidence only, not implementation source.
- **Native/private contracts are NOT permanently out of scope.** They require the formal gap-and-promotion process (Tier 2 validated gap → Tier 3 investigation → Tier 4 design PR). Do NOT say "private/native is out of scope" — say "not for production by default; requires formal gap-and-promotion process."



## DoFun Variety / stock twmusic compatibility authority

The primary TS18 music compatibility target is now explicitly grounded in the user-provided DoFun Variety and stock Topway music APKs.

Primary references:
- `docs/DOFUN_VARIETY_COMPATIBILITY.md`
- `docs/TS18_APK_REFERENCE.md`
- `docs/reference/ts18-apk/reference-contracts.json`
- `docs/reference/ts18-apk/dofun-variety/apps_match_config.music-excerpts.json`
- `docs/reference/ts18-apk/twmusic/classes.string-hits.txt`

Priority order for music-widget/package-identity work:
1. DoFun Variety Theme (`com.dofun.variety`) recognition and widget/control behaviour.
2. Stock `twmusic` / `com.tw.music` replacement contract.
3. Android-standard MediaSession/MediaBrowser/notification correctness.
4. Isolated Topway broadcast/action bridge.
5. Private/native investigation only through explicit evidence-gated approval.

Observed directly reusable requirements:
- DoFun music hotseat matching expects `com.tw.media` / `com.tw.music.MusicActivity` or `com.tw.music` / `com.tw.music.MusicActivity`.
- A dedicated Topway/DoFun release variant may intentionally install as exact package `com.tw.music` and expose `com.tw.music.MusicActivity`.
- The standard Auxio/Auxio-TS variant must keep its normal `org.oxycblt.auxio` identity.
- Topway bridge strings are allowed only inside the isolated bridge package/tests/docs.

Observed but not approved for product implementation:
- `cn.cardoor.libs.media.RemoteMediaService`
- `cn.cardoor.basic.media.NotifyService`
- `cn.cardoor.libs.media.impl.MediaSourceService`
- `android.uid.system`
- `sharedUserId`
- `android.tw.john.TWUtil`
- `com.tw.service.xt.aidl.ITWCommandAidl`
- `com.tw.service.xt.aidl.ITWCommandCallbackAidl`

Do not fake private Cardoor services or copy stock `twmusic` vendor/private implementation code. Mirror only the safe public compatibility contract unless a later human-approved design PR proves a private protocol and rollback path.

## TS18 Native Parity Tier Model

See canonical definition: [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](docs/TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)

| Tier | Name | Scope |
|------|------|-------|
| 0 | Evidence only | t-music snapshot, TWTHEME resources, diagnostics, public repos, firmware notes |
| 1 | Android-standard implementation | MediaSession, MediaBrowser, notification, audio focus, media buttons, AppWidget, shortcuts |
| 2 | TS18-aware validation | On-device evidence proving which TS18/TWTHEME surfaces see or ignore Tier 1 behaviour |
| 3 | Isolated native experiments | External scripts or non-production branches testing specific TW/TWTHEME contracts |
| 4 | Production native integration | Only via explicit human-approved design PR meeting all 8 production eligibility criteria |

A production native integration (Tier 4) is only eligible after an explicit human-approved design PR proves: product need; evidence-backed contract; no package impersonation; no copied smali; no platform-signature/system-UID dependency; safe fallback; isolated implementation; validation and rollback path.

## TS18/TW/TWTHEME source-led policy

TS18/TW/TWTHEME work must begin with the curated TS18/Topway/DoFun/TW source corpus and battle-tested public head-unit projects.

Android/Media3 standards are the Tier 1 implementation baseline. They are the preferred first implementation path but are not the final authority for TS18/TWTHEME-specific questions. For TS18-specific behaviour, agents must first search and classify TS18/TW/TWTHEME ecosystem sources before proposing implementation or validation changes.

Probe/diagnostics-driven work is secondary. It is allowed only when:
- the user provides fresh diagnostics;
- the repo already contains relevant captured evidence;
- no reliable public TS18/TW/TWTHEME or equivalent head-unit source exists;
- the output remains an external validation/runbook step, not speculative product-code scaffolding.

Do not add in-app TS18 probe frameworks, default-off vendor adapter skeletons, TWUtil/TWClient reflection scanners, vendor-service binders, package impersonation, `android.uid.system`/`sharedUserId` strategies, copied smali, or hidden diagnostics modules.

### Required source priority order for TS18/TW/TWTHEME work

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
  - Topway / TS10 / TS18 firmware references
  - DoFun / iLauncher / TWTHEME material
  - TWUtil / TWClient public references
  - ZLink/TLink/carchoose ecosystem clues
  - TWTHEME theme/window/PiP/launcher behaviour sources

Priority 2: Battle-tested public head-unit projects
  - Projects showing working Android head-unit integration patterns
  - Media metadata exposure, hardware-key routing, launcher/widget behaviour,
    and platform isolation precedents

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/
  - diagnostics/redacted/ts18_device_profile.json
  - Existing Auxio-TS TS18 docs

Priority 4: User-provided diagnostics
  - Fresh TS18 logs, dumpsys, bugreport extracts, package lists,
    theme APK listings, launcher behaviour captures

Priority 5: New probes/diagnostics
  - Allowed only when no reliable source, public equivalent, or
    user-provided evidence exists
  - Prefer external scripts/manual runbook steps
  - Do not add speculative probe frameworks to product app code
```

Canonical source corpus: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`

### Required agent workflow for TS18/TW/TWTHEME tasks

1. Search the TS18/TW/TWTHEME source corpus first (Priority 1 before Priority 5).
2. Add any new useful sources to the canonical source map (`docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`).
3. Classify source confidence and porting decision before proposing implementation.
4. Prefer public equivalent projects over speculative probes.
5. Use diagnostics only when provided by the user or when no source-led path exists.
6. Keep diagnostics as external scripts/runbook steps unless a later approved feature explicitly requires code.
7. Avoid TWUtil/TWClient reflection in product code.
8. Avoid vendor binders and package impersonation.


## Topway decompile-driven compatibility rule
- The official Topway `com.tw.music` apktool/JADX decompile is a primary local compatibility source.
- Generic Android MediaSession/AppWidget/shortcut compatibility is necessary but not sufficient.
- Agents must consult `docs/topway/` before proposing Topway/TS18 music compatibility work.
- JADX alias package names such as `com.p060tw.music` are not runtime package names.
- Safe Topway broadcast/action/seek/widget compatibility may be implemented only through an isolated bridge package.
- Existing broad prohibitions against `com.tw.music.action.*` are refined: these strings are forbidden generally, but allowed as constants inside the isolated Topway bridge, tests, and docs.
- Runtime APK must stay clean and must not include evidence/probe/capture tooling.

## TS18 claim labeling (required)
For TS18/TW/TWTHEME claims, include both labels:

- **Evidence confidence**: Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported
- **Porting decision**: Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided

Always distinguish between: product requirement / Android-standard implementation / TS18 runtime validation / native-private investigation / production eligibility.

## Hard constraints
- Do not change the standard Auxio/Auxio-TS package identity to `com.tw.music` or `com.tw.media`; only dedicated, clearly named Topway/DoFun compatibility variants may install as those package IDs.
- Do not require privileged/system UID or platform signing.
- Do not copy decompiled smali into app code.
- Do not spread TS18 conditionals through core playback/library code.
- Do not claim TS18 compatibility without runtime evidence.
- Do not add in-app probe frameworks or speculative default-off adapters.
- Do not add TWUtil/TWClient reflection scanners or vendor package scanners.
- Do not add vendor-service binders or fake Cardoor media services without an explicit approved design PR and proven binder/AIDL protocol.
- Do not add product-code calls to `com.tw.music.action.*` outside the isolated Topway bridge package/test scope.
- Do not add direct `com.tw.*` or `android.tw.john.*` imports in product code.
- Do not add TWTHEME private-resource loaders or hidden diagnostics modules.
- Do not say "private/native is permanently out of scope" — say "not for production by default; requires formal gap-and-promotion process".

- Inspect full CI logs before proposing build fixes; do not diagnose from summary lines only.
- Distinguish Codex environment limitations from GitHub Actions/Copilot runner failures.
- Do not treat Codex environment build limitations as final CI proof; GitHub Actions/Copilot CI is the final workflow proof point.
- Never claim tasks/build/test/lint success unless commands actually passed in this environment.
## Validation baseline
Run or document blockers for:
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

## CI reliability — known issues and rules

### Pre-Gradle preparation: one command for all environments

Run this before any Gradle command in any environment (local, CI, Codex, agent):

```bash
bash ./scripts/prepare-ci-environment.sh
```

This script (idempotent, safe to re-run) does everything in one step:
1. Detects ZIP/snapshot environments and exits with `SNAPSHOT_LIMITATION`
2. **Fast-path**: if required submodule files are already present (e.g. workflow used
   `submodules: recursive`), skips expensive `git submodule sync/update`.
3. Otherwise runs `git submodule sync --recursive` then
   `git submodule update --init --recursive --jobs 4` (soft-fail for git.ffmpeg.org)
4. Creates `media/libraries/common_ktx/proguard-rules.txt` stub (`UPSTREAM_MEDIA_QUIRK`)
5. Calls `bash ./scripts/check-submodules.sh` — validates all required paths
6. Verifies all required files exist; exits 0 only when Gradle is ready

GitHub Actions (`android.yml`, `lint.yml`, `manual-release.yml`) call this script instead of
duplicating the submodule sync/update/patch logic. Local and Codex builds run the same steps.

**Outcome classification used by the script:**

| Label | Meaning |
|-------|---------|
| `SNAPSHOT_LIMITATION` | No `.git` — ZIP/snapshot; Gradle cannot run |
| `SUBMODULE_BLOCKER` | `.git` present but required submodule files are missing |
| `UPSTREAM_MEDIA_QUIRK` | media submodule present but `common_ktx/proguard-rules.txt` absent; fixed by stub |
| `REAL_BUILD_FAILURE` | Prep passed; Gradle itself failed — real app/build issue |

### Submodule requirements and repair

This repo requires **recursive git submodules** to build. Gradle cannot configure at all if
`media/core_settings.gradle` is missing (applied unconditionally at `settings.gradle` line 19).

Required submodules:

| Path | Purpose | Remote | Reachable in sandbox? |
|------|---------|--------|-----------------------|
| `media/` | Patched Media3/ExoPlayer | `github.com/OxygenCobalt/media` | Yes |
| `media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg/` | FFmpeg decoder | `git.ffmpeg.org` | **No** (blocked) |
| `musikr/src/main/cpp/taglib/` | Taglib parser | `github.com/taglib/taglib` | Yes |

**Fresh clone:**
```
git clone --recurse-submodules https://github.com/cbkii/Auxio-TS.git
```

**Existing clone — one-command prep:**
```
bash ./scripts/prepare-ci-environment.sh
```

**Then run Gradle:**
```
./gradlew --no-daemon --stacktrace help
```

### Classifying submodule failures vs app build failures

1. Run `bash ./scripts/prepare-ci-environment.sh` before any Gradle command.
2. If it exits non-zero with `SNAPSHOT_LIMITATION`: ZIP/snapshot environment — Gradle validation impossible.
3. If it exits non-zero with `SUBMODULE_BLOCKER`: environment/setup issue — not an app code issue.
4. If it exits 0 but Gradle fails: classify as `REAL_BUILD_FAILURE` — inspect the first error above the stack trace.

### ZIP/snapshot environments (Codex, agent, archive-based)
ZIP snapshots without `.git` cannot run Gradle. Classify as `SNAPSHOT_LIMITATION`.
Do not try to work around this by copying submodule files manually.

### Known submodule quirks
- `media/libraries/common_ktx/proguard-rules.txt` is **absent** from the `OxygenCobalt/media`
  submodule (commit `0b01e32`). The `common_library_config.gradle` requires it via
  `consumerProguardFiles 'proguard-rules.txt'`. `prepare-ci-environment.sh` creates an empty
  stub file before Gradle runs (`UPSTREAM_MEDIA_QUIRK`). If a future submodule bump adds the
  file, this step becomes a no-op.
- The nested `ffmpeg` submodule resolves from `git.ffmpeg.org`, which is unreachable in this
  sandbox. `check-submodules.sh` reports this as `SUBMODULE_BLOCKER` with ffmpeg noted as the
  missing path. GitHub Actions CI handles ffmpeg initialization correctly with `fetch-depth: 0`.
  **`fetch-depth: 0` is required** in `android.yml` and `lint.yml` because ffmpeg uses an
  unadvertised object reference; shallow clones (`fetch-depth: 1`) break recursive submodule
  init with "unadvertised object" errors.  Do not change `fetch-depth` to 1 for those workflows.
- Local Gradle builds also fail in this sandbox because JetBrains JDK 21 toolchain downloads
  from `api.foojay.io` are unreachable (AGP plugin resolution fails).

### Quality workflow scoping
- `lint.yml` runs four **independent jobs**: `Formatting`, `Unit tests`, `Android lint`, and
  `Head-unit safety`. A formatting failure must not hide unit-test or lint status.
- `testDebugUnitTest` is scoped to `:app` and `:musikr` only. The media library test files
  have missing test-utility dependencies (after upstream "trim down module tree" commit) and
  will fail to compile if the bare `testDebugUnitTest` task is used.
- `lintDebug` is scoped to `:app` only. `app/build.gradle` sets `checkDependencies = false`
  so the app lint report does not aggregate media library lint errors.
- `app/lint.xml` suppresses all lint issues for the vendored Google Material backport package
  (`**/com/google/android/material/**`).  New issues in Auxio-owned source still fail CI.
- `:musikr lintDebug` can be added once musikr lint issues are resolved or baselined.

- `scripts/check-ts18-apk-reference-contracts.sh` is the compact APK-reference baseline check. Run it with the DoFun and head-unit safety checks whenever workflows, package identity, Topway broadcasts, or APK-reference docs change.
- `scripts/check-headunit-compat-safety.sh` is the canonical product-code TS18/Topway safety
  guardrail used by both Android Quality and TS18 Guardrails.

### CI audit methodology for agents
- Fetch **full** job logs, not tails. The root error is always above the Gradle stack trace dump.
- Separate root causes from cascade errors before fixing anything.
- Run `bash ./scripts/check-submodules.sh` first to rule out submodule issues before diagnosing Gradle.
- Run `./gradlew --no-daemon --stacktrace :app:assembleDebug` (not bare `assembleDebug`) if
  you want to limit the build to Auxio-TS code and skip media library sub-tasks.
- Do not claim `test`, `lint`, or `assembleDebug` passed unless the command actually ran
  and exited 0.

## UI screenshot workflow

For UI/UX tasks, do not rely only on code inspection. When runtime visual behaviour matters, use the manual screenshot workflow:

Agents should use the screenshot workflow when visual validation matters.

- Workflow: `UI Screenshots (Roborazzi)` (`.github/workflows/ui-screenshots.yml`)
- Run branch/ref selector on `dev`
- Set `target_ref` to the PR branch or commit SHA
- Use `variant=standard`, `variant=topway_twmusic`, or `variant=topway_twmedia`; use `roborazzi_task=record` for review artifacts, or `verify`/`compare` for regression checks
- Download and inspect artifacts `auxio-ts-roborazzi-outputs` and `auxio-ts-roborazzi-reports`

Screenshot tooling must remain development-only. Do not add screenshot probes, ADB logic, or visual-test fixtures to the production runtime path unless explicitly approved.

For TS18/head-unit UI work, screenshots must include or approximate:

- 1280x720 landscape
- LHD and RHD driver-side layouts where applicable
- playback controls
- queue panel
- shuffle/genre-random button state
- home/dashboard quick-access chips if touched

Roborazzi runs through Robolectric and does not require an emulator. If additional emulator screenshots are needed later, keep that tooling development-only and document the workflow/script path explicitly.

## Release/signing safety
- Treat release/signing workflow edits as security-sensitive.
- Never print secrets or commit keystores/signing artifacts.
- Keep decoded keystores only in runner temp paths.
- Initialize submodules recursively before Gradle; do not create fake submodule files.

- Phase 5G/6A requires evidence-pack capture (`scripts/ts18-create-evidence-pack.sh`) and conservative summarisation before any native/private investigation candidate is considered.

- Tier 0->4 flow reminder: evidence and candidate generation are allowed; production private/native integration still requires a future approved design PR.


## Large-Scope Implementation Delivery Protocol
1. Large tasks are delivery contracts, not suggestion lists.
2. Do not satisfy large tasks by touching many headings shallowly.
3. "Implemented" means runtime code is wired into real behavior or an executable workflow, not only a model/registry/doc/template/test.
4. If a task asks for app/runtime work, docs/tests/tooling are supporting work only.
5. Do not end with "Ready for Draft PR" while any core requested workstream is partial and locally implementable.
6. Environment blockers do not stop local implementation unless they prevent editing or reasoning about relevant files.
7. If Gradle cannot run, continue local implementation and static checks; report build proof as pending GitHub/Copilot CI.
8. Do not list original requested work as "next scope" unless it is genuinely outside the current task.
9. "Next scope" must include only work after current acceptance criteria are met.
10. Compatibility model/status/registry additions are not counted as implemented until wired to meaningful runtime call-sites.

## Implementation-status definitions
- Implemented: runtime code or executable workflow is wired and usable.
- Implemented — requires TS18 validation: implementation exists; runtime parity still needs TS18 hardware proof.
- Partially implemented: pieces exist but user-visible behavior or call-site wiring is incomplete.
- Scaffold only: models/docs/templates exist without runtime integration.
- Blocked: a specific external dependency prevents implementation; state exact blocker.
- Deferred: intentionally out of current scope, with reason.

## Ready for Draft PR / Ready for Merge Rules
- Ready for Draft PR: main implementation goal is complete enough for review.
- If any core requested workstream is partial and locally fixable, use: Needs another Codex pass.
- If branch is an early snapshot, use: Ready for Draft PR snapshot.
- Ready to merge is only for final GitHub/Copilot closure after checks/comments are resolved.
- Missing SDK/submodules are environment-limited validation, not merge proof and not automatic merge blocker.

## Auxio-TS app/runtime priority rules
- Auxio-TS is a TS18/TW/TWTHEME variant app; app/runtime behavior is the priority.
- Source-backed compatibility work should improve real runtime surfaces, not only validation tooling.
- Evidence/validation tooling is primary only when requested or when implementation cannot proceed safely.
- For app-feature tasks, implement visible behavior, route/action wiring, settings/runtime effects, metadata/session/widget improvements, or compatibility call-site wiring.
- Docs/tests/fixtures must not substitute for app code implementation.

## Compatibility-layer wiring rules
- A headunit/compat feature is not implemented until consumed by at least one meaningful runtime call-site.
- Registry entries alone do not count as implementation.
- Status models alone do not count as implementation.
- Metadata policy is not implemented until used by MediaSession, notification, widget, or another runtime publisher.
- Parity maps are not implemented until they drive or verify action/route completeness.
- Settings/status are not implemented until surfaced via existing UI/settings patterns.

## Final response discipline
Always report explicitly:
- which areas were wired into runtime code,
- which areas are scaffold-only,
- which areas remain partial,
- which partials are locally fixable and why not fixed,
- whether output is a review snapshot or complete,
- why any next scope is truly separate from current acceptance criteria.


2026-05-24 implementation note: isolated Topway bridge runtime wiring now exists; keep Topway strings limited to approved bridge/test/docs paths and preserve no-binder/no-impersonation safety boundaries.

## Seeded TS18 exact-device context

Agents must read these concise, redacted context files before exact-device TS18 install/runtime work:

- `docs/CODEX_TS18_DEVICE_CONTEXT.md`
- `docs/TS18_INSTALLATION_CONSTRAINTS.md`
- `docs/evidence/ts18-device-profile/s9863a1h10-android10-termone-2026-05-17.md`

Direct dependencies on external/vendor `com.tw.*` APIs remain forbidden in production code. Thin compatibility wrapper classes under approved Topway/DoFun source sets are allowed only to expose stock-compatible package/class/component names and delegate into Auxio-owned code. Approved wrapper areas include `app/src/topwayCompat/java/com/tw/music/**` (and any future explicitly shared Topway/DoFun wrapper equivalent).

`com.tw.media` is an alternate DoFun fixed-entry variant, not a general no-root bypass. It may conflict on some firmware and still requires real-device validation. Private/native integration remains not for production by default and requires the evidence-gated tier process. Runtime APKs must not include diagnostics/probe/capture tooling.
