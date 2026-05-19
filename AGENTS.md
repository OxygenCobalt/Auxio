# AGENTS.md — Auxio-TS coding authority

## Project stance
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

## Source priority model for TS18 work

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
  - Topway / TS10 / TS18 firmware references (DoFun, Mekede, FCC filing)
  - DoFun / iLauncher / TWTHEME ecosystem material
  - TWUtil / TWClient public references (CarRadio, KaierUtils, dvd-bt)
  - ZLink/TLink/carchoose ecosystem clues
  - TWTHEME theme/window/PiP/launcher behaviour sources

Priority 2: Battle-tested public head-unit projects
  - OpenRadioFM, Display-Media-Titles, FytHWOneKey, RK3066-headunit-service
  - Projects showing media metadata exposure, hardware-key routing, launcher/widget behaviour

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/ (vendor hook map, contracts, manifest)
  - diagnostics/redacted/ts18_device_profile.json

Priority 4: User-provided diagnostics
  - Fresh TS18 logs, dumpsys, bugreport extracts, package lists, theme APK listings

Priority 5: New probes/diagnostics
  - Allowed only when no reliable source, public equivalent, or user-provided evidence exists
  - Keep as external scripts/manual runbook steps
  - Do not add speculative probe frameworks to product app code
```

## Required agent behaviour for TS18 work

- Search the TS18/TW/TWTHEME source corpus in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` first.
- Add any newly-found useful sources to the canonical source map before implementing.
- Classify every TS18/TW/TWTHEME claim with both `Confidence` and `Porting decision` labels.
- Prefer public equivalent projects over speculative probes.
- Use diagnostics only when provided by the user or when no source-led path exists.
- Keep probe/diagnostics output as external scripts or runbook steps unless an approved feature explicitly requires code.
- Avoid TWUtil/TWClient reflection in product code.
- Avoid vendor binders and package impersonation.

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
