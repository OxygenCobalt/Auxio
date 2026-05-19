# Research Sources and Evidence Weighting

## 1) Android official docs (highest implementation authority)
- Media3, MediaSession/notification, audio focus, foreground service, Android Auto/cars guidance.
- Proves standard Android behavior and constraints.
- Does not prove TW-private contracts.

## 2) Upstream Auxio source (highest architecture authority)
- Proves current maintainable playback/library/service boundaries.
- Does not prove TS18 vendor behavior.

## 3) TS18 diagnostics in this repo (highest target-device evidence authority)
- `diagnostics/redacted/ts18_device_profile.json` and related redacted captures.
- Proves observed package/property/theme ecosystem on captured device.
- Does not prove all firmware variants.

## 4) `t-music` snapshot corpus (private evidence snapshot)
- `docs/evidence/t-music-snapshot/*`.
- Proves stock-app contracts and assumptions in analyzed corpus (manifest, actions, vendor hooks, runbook patterns).
- Does not prove these contracts are required for Auxio third-party app behavior on this device.
- Must be paired with Auxio+TS18 runtime comparison before implementation decisions.

## 5) Public TW/Topway ecosystem projects + community/manual material
- Proves available patterns and candidate contracts.
- Mostly suggests directions; cannot replace direct TS18 runtime evidence.

## Mandatory claim format for TS18 docs/PRs
Each TW/TWTHEME claim must include:
1. **Confidence label**: Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported
2. **Porting decision**: Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided
3. **Validation reference**: exact command/scenario/artifact path

If validation reference is missing, the claim cannot drive implementation.
