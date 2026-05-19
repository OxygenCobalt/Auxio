# TW Ecosystem Source Map (supporting index)

This file is a concise supporting index. The authoritative source corpus with full classification is in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Canonical source-of-truth
- Authoritative source corpus table: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- Canonical TS18/TW/TWTHEME label taxonomies defined there.
- Source priority model defined in `AGENTS.md` and `.github/copilot-instructions.md`.

## TS18/TW/TWTHEME ecosystem quick-reference

Key sources added to canonical corpus for the first time this pass (not an exhaustive list):

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

## Coverage notes
- All sources above are fully classified in the canonical corpus table in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.
- This file does not duplicate those rows; it only provides a quick orientation index.

## Current hypotheses (not implementation requirements)
- Some TS18 launchers may privilege package-targeted metadata pathways.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- Some vendor stacks may alter focus/ownership behaviour under projection.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- TWTHEME variance may change visual expectations without changing media API requirements.
  Confidence: **Hypothesis**; Porting decision: **Reusable validation idea**.
- iLauncher media widget may require specific metadata fields or artwork dimensions.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
- `com.tw.carchoose` or a similar mechanism may control which media source takes foreground.
  Confidence: **Hypothesis**; Porting decision: **Requires TS18 runtime validation**.
