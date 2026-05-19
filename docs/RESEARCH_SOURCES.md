# Research Sources and Evidence Weighting

## TS18/TW/TWTHEME source priority model

For any TS18-specific question, consult sources in this priority order:

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
  - Topway / TS10 / TS18 firmware references
  - DoFun / iLauncher / TWTHEME material
  - TWUtil / TWClient public references (for classification only; unsafe to port)
  - ZLink/TLink/carchoose ecosystem clues
  - TWTHEME theme/window/PiP/launcher behaviour sources
  (Canonical table: docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md)

Priority 2: Battle-tested public head-unit projects
  - Projects showing working media metadata, hardware-key routing, launcher/widget behaviour
  (Canonical table: docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md)

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/ (vendor hooks, manifest, contracts)
  - diagnostics/redacted/ts18_device_profile.json

Priority 4: User-provided diagnostics
  - Fresh TS18 logs, dumpsys, bugreport extracts, package lists

Priority 5: New probes/diagnostics
  - Allowed only when no reliable source or user-provided evidence exists
  - Keep external (scripts/runbook); do not add to product code
```

## Standard authority hierarchy (for implementation decisions)

1. **Android official docs** (Media3, MediaSession, audio focus, car/auto) — implementation baseline.
2. **Upstream Auxio source** — architecture baseline.
3. **TS18/TW/TWTHEME corpus** (Priority 1+2 above) — TS18-specific evidence.
4. **Local evidence** (Priority 3+4 above) — device-specific confirmation.

## Canonical source corpus location

The full source corpus with confidence/porting labels is maintained in:

```text
docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md
```

Do not maintain a second authoritative source table elsewhere.

## Mandatory claim format for TS18 docs/PRs
Each TW/TWTHEME claim must include:
1. **Confidence label**: Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported
2. **Porting decision**: Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided
3. **Validation reference**: exact command/scenario/artifact path

If validation reference is missing, the claim cannot drive implementation.
