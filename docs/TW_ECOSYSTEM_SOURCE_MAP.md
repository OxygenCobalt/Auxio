# TW Ecosystem Source Map (supporting index)

This file is a concise map that points to the canonical source corpus in
`docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Canonical source-of-truth
- Authoritative source corpus table: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- Canonical TS18/TW/TWTHEME label taxonomies are also defined there.

## Coverage notes
- `RK3066-headunit-service` is tracked in the canonical source corpus table and intentionally not duplicated here.
- This file keeps only non-authoritative, maintenance-focused context.

## Supplemental ecosystem context (non-authoritative)

| Context item | Scope | Confidence | Porting decision | Notes |
|---|---|---|---|---|
| Ecosystem sources continue to change quickly (XDA/Telegram/vendor pages). | Validation planning | Observed | Useful as evidence only | Treat as test-matrix context, not implementation authority. |
| Public TW-private examples (`android.tw.john.TWUtil`, `TWClient`) exist in ecosystem projects. | Risk framing | Observed | Unsafe to port | Do not add reflection/import/binding logic in product code. |
| Stock contracts (`com.tw.music.action.*`, `com.tw.service*`) can inform acceptance scenarios. | Validation inputs | Observed | Requires TS18 runtime validation | Keep as validation evidence only until a concrete feature gap is proven. |

## Current hypotheses (not implementation requirements)
- Some TS18 launchers may privilege package-targeted metadata pathways.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- Some vendor stacks may alter focus/ownership behavior under projection.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- TWTHEME variance may change visual expectations without changing media API requirements.
  Confidence: **Hypothesis**; Porting decision: **Reusable validation idea**.
