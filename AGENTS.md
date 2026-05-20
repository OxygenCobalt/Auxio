# AGENTS.md — Auxio-TS coding authority

## Project stance
- Start documentation navigation from `docs/README.md`.
- Prefer consolidation/removal of stale docs over keeping historical wrappers.
- Auxio-TS is an Auxio fork; preserve upstream architecture.
- Keep TS18/TW integration in adapter/facade boundaries.
- Use `docs/evidence/t-music-snapshot/` as evidence only, not implementation source.

## TS18/TW/TWTHEME source-led policy

TS18/TW/TWTHEME work must begin with the curated TS18/Topway/DoFun/TW source corpus and battle-tested public head-unit projects.

Android/Media3 standards remain the baseline implementation authority, but they are not enough to answer TS18/TWTHEME-specific questions. For TS18-specific behaviour, agents must first search and classify TS18/TW/TWTHEME ecosystem sources before proposing implementation or validation changes.

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

## TS18 claim labeling (required)
For TS18/TW/TWTHEME claims, include both labels:

- **Evidence confidence**: Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported
- **Porting decision**: Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided

## Hard constraints
- Do not change package identity to `com.tw.music`.
- Do not require privileged/system UID or platform signing.
- Do not copy decompiled smali into app code.
- Do not spread TS18 conditionals through core playback/library code.
- Do not claim TS18 compatibility without runtime evidence.

- Inspect full CI logs before proposing build fixes; do not diagnose from summary lines only.
- Distinguish Codex environment limitations from GitHub Actions/Copilot runner failures.
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
2. Runs `git submodule sync --recursive`
3. Runs `git submodule update --init --recursive --jobs 4` (soft-fail for git.ffmpeg.org)
4. Prints `git submodule status --recursive`
5. Calls `bash ./scripts/check-submodules.sh` — validates all required paths
6. Creates `media/libraries/common_ktx/proguard-rules.txt` stub (`UPSTREAM_MEDIA_QUIRK`)
7. Verifies all required files exist; exits 0 only when Gradle is ready

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
- Local Gradle builds also fail in this sandbox because JetBrains JDK 21 toolchain downloads
  from `api.foojay.io` are unreachable (AGP plugin resolution fails).

### Quality workflow scoping
- `testDebugUnitTest` is scoped to `:app` and `:musikr` only. The media library test files
  have missing test-utility dependencies (after upstream "trim down module tree" commit) and
  will fail to compile if the bare `testDebugUnitTest` task is used.
- `lintDebug` is scoped to `:app` only. `app/build.gradle` sets `lint { checkDependencies = false }`
  so the app lint report does not aggregate media library lint errors.

### CI audit methodology for agents
- Fetch **full** job logs, not tails. The root error is always above the Gradle stack trace dump.
- Separate root causes from cascade errors before fixing anything.
- Run `bash ./scripts/check-submodules.sh` first to rule out submodule issues before diagnosing Gradle.
- Run `./gradlew --no-daemon --stacktrace :app:assembleDebug` (not bare `assembleDebug`) if
  you want to limit the build to Auxio-TS code and skip media library sub-tasks.
- Do not claim `test`, `lint`, or `assembleDebug` passed unless the command actually ran
  and exited 0.

## Release/signing safety
- Treat release/signing workflow edits as security-sensitive.
- Never print secrets or commit keystores/signing artifacts.
- Keep decoded keystores only in runner temp paths.
- Initialize submodules recursively before Gradle; do not create fake submodule files.
