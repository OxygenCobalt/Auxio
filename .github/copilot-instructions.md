# Copilot instructions for Auxio-TS

## Core stance
- Auxio-TS is an Auxio fork, not a clone of `com.tw.music`.
- Keep upstream playback/library/service design unless evidence requires targeted change.
- Keep TS18 integration in adapter/facade boundaries.
- Treat `docs/evidence/t-music-snapshot/` as evidence, not code to port.

## TS18 evidence labels (required)
For TS18/TW/TWTHEME claims include:
- confidence: **Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported**
- porting decision: **directly reusable requirement / reusable validation idea / evidence only / obsolete for Auxio / unsafe to port / explicitly avoid**

## Never do
- Do not change package to `com.tw.music`.
- Do not assume privileged/system UID.
- Do not copy decompiled smali into Auxio code.
- Do not claim TS18 compatibility without TS18 runtime evidence.

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
