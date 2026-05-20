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

## Release/signing safety
- Treat release/signing workflow edits as security-sensitive.
- Never print secrets or commit keystores/signing artifacts.
- Keep decoded keystores only in runner temp paths.
- Initialize submodules recursively before Gradle; do not create fake submodule files.
