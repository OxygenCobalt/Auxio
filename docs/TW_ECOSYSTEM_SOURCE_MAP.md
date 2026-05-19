# TW Ecosystem Source Map (supporting index)

This file is a concise supporting index. The authoritative source corpus with full classification is in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Canonical source-of-truth
- Authoritative source corpus table: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- Canonical TS18/TW/TWTHEME label taxonomies are also defined there.
- Source priority order (Priority 1–5) is defined in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
  and mirrored in `AGENTS.md` and `.github/copilot-instructions.md`.

## Source priority summary (see canonical doc for full detail)

```
Priority 1: TS18/TW/TWTHEME ecosystem sources (Topway/DoFun/iLauncher/TWTHEME/ZLink)
Priority 2: Battle-tested public head-unit projects
Priority 3: Local repository evidence (t-music-snapshot, ts18_device_profile.json)
Priority 4: User-provided diagnostics
Priority 5: New probes/diagnostics (last resort; external only)
```

## Coverage notes
- `RK3066-headunit-service` is tracked in the canonical source corpus table and intentionally not duplicated here.
- New sources (DoFun website, iLauncher site, FCC filing, KaierUtils, XDA threads, 4PDA, headunit-desktop, AAGateway, hudiy, head-unit indexes) are added to the canonical corpus table.
- This file keeps only non-authoritative, maintenance-focused context.

| Source | Ecosystem role | Confidence | Porting decision |
|---|---|---|---|
| DoFun Telegram (`t.me/s/dofun_app`) | TS18.1.2/TS18.2.2 firmware families; iLauncher/TWTHEME conventions; ZLink compatibility notes | Observed | Useful as evidence only |
| DoFun website (`dofun.cc/car-desktop`) | iLauncher media widget, PiP, split-screen, TWTHEME integration description | Observed | Reusable validation idea |
| iLauncher.net | Theme APK naming; launcher widget/home integration details | Observed | Requires TS18 runtime validation |
| FCC TS18 manual | Hardware button layout and MCU interface certification | Observed | Useful as evidence only |
| KaierUtils (`github.com/d51x/KaierUtils`) | TWUtil usage on KSW/ZXW platforms; volume control via firmware-private API | Observed | Unsafe to port |
| ZLink5 (`zlink5.com`, `com.zjinnova.zlink`) | Active phone-link app on captured TS18; audio focus competitor | Observed | Requires TS18 runtime validation |
| XDA TLink versions thread | TLink/ZLink interchangeability; version-specific TS18 compat notes | Observed | Reusable validation idea |
| XDA iLauncher thread | TS18 v7.5 iLauncher image-display fix; media artwork quirks | Observed | Reusable validation idea |

| Context item | Scope | Confidence | Porting decision | Notes |
|---|---|---|---|---|
| Ecosystem sources continue to change quickly (XDA/Telegram/vendor pages). | Validation planning | Observed | Useful as evidence only | Treat as test-matrix context, not implementation authority. |
| Public TW-private examples (`android.tw.john.TWUtil`, `TWClient`) exist in ecosystem projects. | Risk framing | Observed | Unsafe to port | Do not add reflection/import/binding logic in product code. |
| Stock contracts (`com.tw.music.action.*`, `com.tw.service*`) can inform acceptance scenarios. | Validation inputs | Observed | Requires TS18 runtime validation | Keep as validation evidence only until a concrete feature gap is proven. |
| DoFun/iLauncher are active commercial ecosystem players for TS18/TWTHEME. | Ecosystem taxonomy | Observed | Useful as evidence only | Use DoFun/iLauncher material for variant tracking and acceptance scope. |
| TS18.1.2 (vertical) and TS18.2.2 (horizontal) are separate firmware families. | Device evidence | Observed | Useful as evidence only | Record firmware family in device evidence fingerprints. |

## Current hypotheses (not implementation requirements)
- Some TS18 launchers may privilege package-targeted metadata pathways.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- Some vendor stacks may alter focus/ownership behaviour under projection.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- TWTHEME variance may change visual expectations without changing media API requirements.
  Confidence: **Hypothesis**; Porting decision: **Reusable validation idea**.
- iLauncher may expose widget image-display paths that depend on metadata quality beyond standard MediaSession.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
