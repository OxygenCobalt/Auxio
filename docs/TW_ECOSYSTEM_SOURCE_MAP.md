# TW Ecosystem Source Map (supporting index)

This file is a concise supporting index. The authoritative source corpus with full classification is in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Canonical source-of-truth
- Authoritative source corpus table: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- Canonical TS18/TW/TWTHEME label taxonomies are also defined there.
- Source priority order (Priority 1–5) is defined in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
  and mirrored in `AGENTS.md` and `.github/copilot-instructions.md`.

## Source priority summary (see canonical doc for full detail)

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources (Topway/DoFun/iLauncher/TWTHEME/ZLink)
Priority 2: Battle-tested public head-unit projects
Priority 3: Local repository evidence (t-music-snapshot, ts18_device_profile.json)
Priority 4: User-provided diagnostics
Priority 5: New probes/diagnostics (last resort; external only)
```

## Coverage notes
- `RK3066-headunit-service` is tracked in the canonical source corpus table and intentionally not duplicated here.
- New sources added in the latest corpus pass: DoFun Telegram archive pages (before=26/47/128/135/169), telemetr.io DoFun index, dofun.cc additional product pages (xolio/car-equ/app portal/BBS), XDA TS18 firmware thread (4664566), TS18 manual (manuals.plus), OpenRadioFM/FytHWOneKey release pages, SC98531BinRepo, OpenMobileRadioInterface (EBU), headunit-revived wiki settings, GitHub headunit topic.
- This file keeps only non-authoritative, maintenance-focused quick-reference context.

## Launcher/widget compatibility interpretation (TS18/TWTHEME/iLauncher)
- Public sources (DoFun/iLauncher ecosystem and public launcher projects like CarWebGuru) support the expectation that head-unit desktops commonly expose music-control/widget-like surfaces.
- Public sources do **not** establish a formal/public TWTHEME widget SDK contract for third-party apps.
- Auxio-TS therefore treats compatibility as a **standard Android AppWidget/shortcut/deep-link** problem and validates runtime behavior on TS18 hosts.
- Implementation authority remains Android docs/APIs; TS18/TW/TWTHEME sources remain evidence/validation context.
- If Tier 1 Android-standard behavior is insufficient, record the gap in `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md` and evaluate native/private options only through explicit promotion gates.

| Source/context | Interpretation | Confidence | Porting decision |
|---|---|---|---|
| DoFun / iLauncher ecosystem pages and channels | Widget-like launcher surfaces are common in ecosystem UX | Observed | Useful as evidence only |
| CarWebGuru (public launcher/widget precedent) | Public precedent for head-unit launcher/widget interaction models | Inferred | Reusable validation idea |
| Android `AppWidgetProvider` / `RemoteViews` / shortcuts | Implementation authority for Auxio widget/launcher entry design | Observed | Directly reusable requirement |
| Auxio `HeadUnitEntryPoints` + `WidgetProvider` | Current Auxio-TS integration anchors for launcher/widget routing | Observed | Directly reusable requirement |
| TS18/TWTHEME runtime host behavior | Still requires hardware acceptance checks | Requires TS18 validation | Requires TS18 runtime validation |
| TWUtil/TWClient/private stock-app contracts | Keep as reference-only native-investigation evidence until explicit promotion | Observed | Unsafe to port |

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
