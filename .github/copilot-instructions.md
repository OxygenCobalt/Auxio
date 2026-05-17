# Copilot instructions for Auxio-TS

## Core stance
- Auxio-TS is an **Auxio fork**, not a ground-up clone of `com.tw.music`.
- Preserve upstream playback/library/service design unless evidence requires targeted change.
- Keep TS18 integration in adapter/facade boundaries.
- Use `docs/evidence/t-music-snapshot/` as evidence only, not as code to port.

## Evidence labeling (required)
For each TS18/TW/TWTHEME claim, include:
1) confidence: **Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported**
2) porting decision label: **directly reusable requirement / reusable validation idea / evidence only / obsolete for Auxio / unsafe to port / explicitly avoid**

## Snapshot-specific guardrails
From `t-music` snapshot:
- `com.tw.music` + `android.uid.system` are observed in stock manifest.
- `com.tw.music.action.cmd|prev|next|pp` are observed in stock paths.
- `com.tw.service.xt.aidl.*`, `com.tw.eq`, `com.tw.radio`, `TWTHEME` coupling are observed in stock evidence.

For Auxio-TS, these imply investigation and validation planning, **not direct adoption**.

## Architectural boundary
```text
Auxio upstream core
  -> Android media integration layer
  -> Auxio-TS adapter facade
  -> optional TS18 modules (launcher/TW broadcast/TW service/TWTHEME notes/ZLink-TLink hooks/comparator)
```

## Never do in planning/implementation passes
- Do not change package to `com.tw.music`.
- Do not add privileged/system UID assumptions.
- Do not copy decompiled smali into Auxio app code.
- Do not claim TS18 compatibility without TS18 runtime evidence.

## Baseline checks
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`
