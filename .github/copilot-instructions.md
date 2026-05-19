# Copilot instructions for Auxio-TS

## Core stance
- Auxio-TS is an Auxio fork, not a clone of `com.tw.music`.
- Keep upstream playback/library/service design unless evidence requires targeted change.
- Keep TS18 integration in adapter/facade boundaries.
- Treat `docs/evidence/t-music-snapshot/` as evidence, not code to port.

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
  - Topway / TS10 / TS18 firmware references (DoFun, Mekede, FCC)
  - DoFun / iLauncher / TWTHEME material
  - TWUtil / TWClient public references (CarRadio, KaierUtils, dvd-bt)
  - ZLink/TLink/carchoose ecosystem clues

Priority 2: Battle-tested public head-unit projects
  - OpenRadioFM, Display-Media-Titles, FytHWOneKey, RK3066-headunit-service

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/ + diagnostics/redacted/ts18_device_profile.json

Priority 4: User-provided diagnostics (fresh logs/dumpsys)

Priority 5: New probes/diagnostics (external scripts only; never product-code scaffolding)
```

## Evidence labeling (required)
For each TS18/TW/TWTHEME claim, include:
1) confidence: **Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported**
2) porting decision label: **Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided**

## Never do
- Do not change package to `com.tw.music`.
- Do not assume privileged/system UID.
- Do not copy decompiled smali into Auxio code.
- Do not claim TS18 compatibility without TS18 runtime evidence.
- Do not add TWUtil/TWClient reflection or vendor-service binders to product code.
- Do not make probe/diagnostics the default approach for TS18 questions.

## Baseline checks
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

## Release/signing safety
- Treat release/signing workflow edits as security-sensitive.
- Never print or commit secret material.
- Keep decoded keystores in runner temp paths only.
- Initialize submodules recursively before Gradle; never create fake submodule files.
