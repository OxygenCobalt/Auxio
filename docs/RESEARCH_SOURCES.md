# Research Sources and Evidence Weighting

## Source priority for TS18/TW/TWTHEME work

For TS18/TWTHEME-specific questions, use this priority order:

### Priority 1 — TS18/TW/TWTHEME ecosystem sources (highest for TS18-specific questions)
- Topway / TS10 / TS18 firmware references (DoFun, XDA, Mekede, FCC filings).
- DoFun / iLauncher / TWTHEME material (DoFun Telegram, DoFun website, iLauncher website).
- TWUtil / TWClient public references (CarRadio, dvd-bt, KaierUtils).
- ZLink/TLink/carchoose ecosystem clues.
- TWTHEME theme/window/PiP/launcher behaviour sources.
- Full corpus: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`

### Priority 2 — Battle-tested public head-unit projects (for architecture precedent)
- Projects showing working Android head-unit integration patterns.
- OpenRadioFM, Display-Media-Titles, FytHWOneKey, FET, FYTuis7862BinRepo, RK3066-headunit-service.
- Full corpus: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`

### Priority 3 — Local repository evidence (highest target-device evidence authority)
- `diagnostics/redacted/ts18_device_profile.json` and related redacted captures.
- Proves observed package/property/theme ecosystem on captured device.
- Does not prove all firmware variants.

### Priority 4 — `t-music` snapshot corpus (private evidence snapshot)
- `docs/evidence/t-music-snapshot/*`.
- Proves stock-app contracts and assumptions in analyzed corpus (manifest, actions, vendor hooks, runbook patterns).
- Does not prove these contracts are required for Auxio third-party app behavior on this device.
- Must be paired with Auxio+TS18 runtime comparison before implementation decisions.

### Priority 5 — New probes/diagnostics (last resort)
- Allowed only when no reliable source, public equivalent, or user-provided evidence exists.
- Prefer external scripts/manual runbook steps.
- Do not add speculative probe frameworks to product app code.

## Android official docs (baseline implementation authority — not sufficient for TS18-specific questions)
- Media3, MediaSession/notification, audio focus, foreground service, Android Auto/cars guidance.
- Proves standard Android behavior and constraints.
- Does not prove TW-private contracts.
- Assumed accessible and authoritative for standard implementation; not the focus of TS18-specific research.

## Upstream Auxio source (highest architecture authority)
- Proves current maintainable playback/library/service boundaries.
- Does not prove TS18 vendor behavior.

## Mandatory claim format for TS18 docs/PRs
Each TW/TWTHEME claim must include:
1. **Confidence label**: Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported
2. **Porting decision**: Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided
3. **Validation reference**: exact command/scenario/artifact path

If validation reference is missing, the claim cannot drive implementation.
