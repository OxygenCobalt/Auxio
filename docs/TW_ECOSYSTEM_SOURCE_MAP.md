# TW Ecosystem Source Map (supporting index)

This file is a concise supporting index. The authoritative source corpus with full classification is in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Canonical source-of-truth
- Authoritative source corpus table: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- Canonical TS18/TW/TWTHEME label taxonomies are also defined there.
- Source priority order (Priority 1–5) is defined in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
  and mirrored in `AGENTS.md` and `.github/copilot-instructions.md`.
- Tier model (Tier 0–4): [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)
- Parity gap tracking: [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md)

## Source priority summary (see canonical doc for full detail)

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources (Topway/DoFun/iLauncher/TWTHEME/ZLink)
Priority 2: Battle-tested public head-unit projects
Priority 3: Local repository evidence (t-music-snapshot, ts18_device_profile.json)
Priority 4: User-provided diagnostics
Priority 5: New probes/diagnostics (last resort; external only)
```

## Tier implications by source class

| Source class | Tier 0 evidence role | Tier 1 Android implementation implication | Tier 2 remaining TS18 validation | Native/private investigation eligibility | Production eligibility |
|---|---|---|---|---|---|
| DoFun/iLauncher/TWTHEME ecosystem sources | Defines parity target: launcher widget expectations, TWTHEME visual surfaces, firmware variant scope | Standard AppWidget, shortcuts, MediaSession, deep-links should work for Tier 1 | Validate on hardware: which TWTHEME/iLauncher surfaces respond to standard Tier 1 APIs? | Maybe: if Tier 2 confirms standard AppWidget/MediaSession are invisible to iLauncher | Not until Tier 2 gap + Tier 4 design PR |
| TWUtil/TWClient/private framework references | Documents that private TW contracts exist in ecosystem; not API documentation | No Tier 1 implication — do not import or call | Validate whether TWUtil/TWClient is needed or whether standard focus/session suffices | Maybe: if Tier 2 proves specific function is inaccessible via standard API | Not in current production code; requires Tier 4 design PR |
| Stock com.tw.music / t-music contracts | Documents stock app behavior and private actions; defines parity gap hypotheses | No Tier 1 implication — do not broadcast com.tw.music.action.* in product code | Validate whether standard MediaSession/intent routing achieves equivalent function | Maybe for com.tw.music.action.*; No for package impersonation / UID 1000 | Not in production by default; com.tw.music package identity and UID 1000 are explicitly forbidden |
| ZLink/TLink coexistence | Documents focus/session competition from active phone-link apps | Standard AudioFocusRequest + MediaSession session management is Tier 1 | Validate focus recovery and session discovery during and after ZLink/TLink activity | No — standard focus management should suffice unless Tier 2 shows active interference | N/A for current scope |
| Hardware evidence (FCC, manual, keylayout) | Documents hardware button layout, key codes, and MCU interface | Standard media key handling (KEYCODE_MEDIA_*) via MediaButtonReceiver is Tier 1 | Validate steering-wheel key routing on real hardware | No — standard key handling should suffice unless Tier 2 shows vendor interception | N/A |

## TWTHEME/iLauncher widgets — explicit position

- Public sources (DoFun website, iLauncher.net, XDA threads) **support** launcher/music-control/widget-like expectations on TS18.
- Public sources do **not** prove a formal public TWTHEME widget SDK exists.
- Current Auxio-TS implementation uses standard Android AppWidget / shortcut / deep-link surfaces (Tier 1).
- Remaining parity gaps (artwork display, metadata richness, widget visibility) must be validated on real TS18/TWTHEME hardware (Tier 2).
- Native TWTHEME widget coupling may only be investigated after Tier 2 confirms a specific gap (Tier 3 → Tier 4).

## TWUtil/TWClient/private contracts — explicit position

- TWUtil/TWClient hooks are **real evidence** where supported by public ecosystem sources (CarRadio, dvd-bt, KaierUtils) and local evidence.
- They are **unsafe to port directly** — they are OEM-private, platform-specific, and would create a vendor lock-in path.
- They **may inform future isolated investigation** (Tier 3) if Tier 2 validation proves a specific parity gap that standard APIs cannot bridge.
- They are **not current production implementation** — no reflection, imports, or binders in Auxio-TS product code.

## Coverage notes
- `RK3066-headunit-service` is tracked in the canonical source corpus table and intentionally not duplicated here.
- New sources added in the latest corpus pass: DoFun Telegram archive pages (before=26/47/128/135/169), telemetr.io DoFun index, dofun.cc additional product pages (xolio/car-equ/app portal/BBS), XDA TS18 firmware thread (4664566), TS18 manual (manuals.plus), OpenRadioFM/FytHWOneKey release pages, SC98531BinRepo, OpenMobileRadioInterface (EBU), headunit-revived wiki settings, GitHub headunit topic.
- This file keeps only non-authoritative, maintenance-focused quick-reference context.

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
| Public TW-private examples (`android.tw.john.TWUtil`, `TWClient`) exist in ecosystem projects. | Risk framing | Observed | Unsafe to port | Do not add reflection/import/binding logic in product code. TWUtil/TWClient may inform Tier 3 investigation only. |
| Stock contracts (`com.tw.music.action.*`, `com.tw.service*`) can inform acceptance scenarios. | Validation inputs | Observed | Requires TS18 runtime validation | Keep as validation evidence only until a concrete feature gap is proven via Tier 2. |
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
