# TS18 Source-Led Integration Strategy

## Purpose and scope

This is a TS18/TW/TWTHEME source-led strategy document. Official Android/Media3/car/audio documentation is assumed to be accessible and authoritative as an implementation baseline and is not exhaustively repeated here.

This document exists because TS18/TW/TWTHEME-specific knowledge is fragmented across public firmware ecosystem pages, DoFun/iLauncher ecosystem material, XDA threads, TWUtil/TWClient public repos, and local `t-music` evidence. That specific knowledge gap is what this document fills.

## TS18/TW/TWTHEME source-led policy

TS18/TW/TWTHEME work must begin with the curated TS18/Topway/DoFun/TW source corpus and battle-tested public head-unit projects.

Android/Media3 standards remain the baseline implementation authority, but they are not enough to answer TS18/TWTHEME-specific questions. For TS18-specific behaviour, agents must first search and classify TS18/TW/TWTHEME ecosystem sources before proposing implementation or validation changes.

Probe/diagnostics-driven work is secondary. It is allowed only when:
- the user provides fresh diagnostics;
- the repo already contains relevant captured evidence;
- no reliable public TS18/TW/TWTHEME or equivalent head-unit source exists;
- the output remains an external validation/runbook step, not speculative product-code scaffolding.

Do not add in-app TS18 probe frameworks, default-off vendor adapter skeletons, TWUtil/TWClient reflection scanners, vendor-service binders, package impersonation, `android.uid.system`/`sharedUserId` strategies, copied smali, or hidden diagnostics modules.

## Source priority model

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
- Topway / TS10 / TS18 firmware references
- DoFun / iLauncher / TWTHEME material
- TWUtil / TWClient public references
- ZLink/TLink/carchoose ecosystem clues
- TWTHEME theme/window/PiP/launcher behaviour sources

Priority 2: Battle-tested public head-unit projects
- Projects showing working Android head-unit integration patterns
- Projects showing media metadata exposure, hardware-key routing, launcher/widget behaviour

Priority 3: Local repository evidence
- docs/evidence/t-music-snapshot/
- diagnostics/redacted/ts18_device_profile.json
- Existing Auxio-TS TS18 docs

Priority 4: User-provided diagnostics
- Fresh TS18 logs, dumpsys, bugreport extracts, package lists, theme APK listings

Priority 5: New probes/diagnostics
- Allowed only when no reliable source, public equivalent, or user-provided evidence exists
- Prefer external scripts/manual runbook steps
- Do not add speculative probe frameworks to product app code
```

## Canonical evidence taxonomy for TS18/TW/TWTHEME claims

### Confidence

## Canonical evidence taxonomy for TS18/TW/TWTHEME claims

### Confidence
- **Observed**
- **Inferred**
- **Hypothesis**
- **Requires TS18 validation**
- **Unsupported**

### Auxio-TS porting decision
- **Directly reusable requirement**
- **Reusable validation idea**
- **Useful as evidence only**
- **Obsolete due to Auxio architecture**
- **Requires TS18 runtime validation**
- **Unsafe to port**
- **Should be explicitly avoided**

## Source corpus

The table below is the canonical source corpus. It is ordered by priority. The `Source class` column reflects the 5-priority model above.

| Source | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| **— Priority 1: TS18/TW/TWTHEME ecosystem —** | | | | | | | | | | |
| DoFun TS channel (main) | https://t.me/s/dofun_app | TS18/TWTHEME ecosystem | TS18.1.2 vertical / TS18.2.2 horizontal firmware families; DoFun iLauncher/TWTHEME theme structure; ZLink5 compatibility notes; firmware update cadence | TS18 firmware family split and theme conventions exist and are actively maintained | Firmware variant must be tracked in acceptance evidence; iLauncher and TWTHEME are first-class surfaces | Formal API contracts; applicability to every TS18 vendor clone | Record firmware variant in all evidence reports; test against both screen orientations | Observed | Useful as evidence only | Public Telegram; content is community/vendor posts. Do not copy or redistribute firmware files. |
| DoFun channel (before=55) | https://t.me/s/dofun_app?before=55 | TS18/TWTHEME ecosystem | DoFun iLauncher split-screen / PiP launcher behaviour; TWTHEME window management clues | iLauncher supports PiP and multi-window on TS18 | TS18 launcher UX should be validated for split-screen and PiP behaviour | Media-app API requirements from PiP hosting | Include PiP/split-screen as acceptance validation scenarios | Observed | Reusable validation idea | Public Telegram channel posts. |
| DoFun channel (before=110) | https://t.me/s/dofun_app?before=110 | TS18/TWTHEME ecosystem | ZLink/carchoose interaction with launcher foreground; audio focus / media-handoff behaviour clues | ZLink actively competes for audio focus and foreground on TS18 | ZLink coexistence must be an acceptance scenario, not a guess | Root cause of all ZLink/Auxio-TS interaction issues | Design TS18-ACC-005 (ZLink coexistence) as a first-class test scenario | Observed | Requires TS18 runtime validation | |
| DoFun website — iLauncher | https://www.dofun.cc/car-desktop/car-desktop-en.html | TS18/TWTHEME ecosystem | iLauncher feature list: media widget, PiP, split-screen, TWTHEME integration, launcher home page | DoFun/iLauncher is a first-class TS18 media-presentation surface | Launcher widget will only update from a MediaSession it can discover | Widget/session coupling mechanism or required package names | Media metadata quality must be validated against iLauncher widget as a named acceptance check | Observed | Reusable validation idea | Public product page. Do not copy text or screenshots. |
| iLauncher.net | https://www.ilauncher.net/ | TS18/TWTHEME ecosystem | Theme APK naming conventions; theme switching; iLauncher widget/home integration | iLauncher is a distinct theme/launcher ecosystem used across TS18 and related head units | Third-party app metadata must be discoverable by iLauncher's media widget | Whether standard MediaSession is sufficient for iLauncher widget updates | Validate iLauncher media-widget refresh with Auxio-TS playing (TS18-ACC-006 extended) | Observed | Requires TS18 runtime validation | Public site. |
| Mekede TS10-TS18 | https://www.mekede.com/TS10-TS18/ | TS18/TWTHEME ecosystem | TS10/TS18 product family segmentation; hardware variants; official support page | TS18 is a distinct product family with explicit firmware naming | Device family label (TS10 vs TS18) should be captured in evidence | Shared API contracts across all family members | Record full firmware/family label in every evidence report | Observed | Useful as evidence only | Public commercial page. |
| XDA TS18 main thread | https://xdaforums.com/t/ts18-devices-branded-topway-alwinner-online-help-firmwares-and-other-stuff.4558675/ | TS18/TWTHEME ecosystem | Community TS18 variance; iLauncher vs stock launcher differences; firmware quirks; ZLink vs TLink community experience | TS18 community reports real variance across firmware versions; launcher/theme issues are common third-party app pain points | Acceptance validation should cover at least two firmware versions | Reproducible deterministic contracts | Use as acceptance test scenario brainstorming and risk ranking input | Observed | Useful as evidence only | XDA community posts. |
| XDA iLauncher thread | https://xdaforums.com/t/new-car-launcher-infinite-ilauncher-last-update-version-v7-5-0-fix-ts18-head-unit-cant-read-image-problem.4484461/ | TS18/TWTHEME ecosystem | iLauncher version history; TS18-specific image/artwork display fix in v7.5; media metadata display quirks | iLauncher has TS18-targeted fixes for media art display | Media artwork quality and format may affect iLauncher widget display | Root API cause of the issue; applicability to all Auxio-TS installations | Include artwork format/resolution as a validation data point for iLauncher | Observed | Reusable validation idea | XDA community thread. |
| XDA TS18 short thread | https://xdaforums.com/t/ts18.4488709/ | TS18/TWTHEME ecosystem | Community-identified TS18 quirks and third-party app compat notes | Confirms TS18 is a documented community device | Additional acceptance scenario inputs | Deterministic implementation contract | Scanning thread for further acceptance scenario inputs is worthwhile | Observed | Useful as evidence only | XDA community posts. |
| XDA TS10 thread | https://xdaforums.com/t/new-topway-ts10-uis7862-6gb-ram-128gb-head-unit-q-as.4227947/ | TS18/TWTHEME ecosystem | TS10 (same Topway family, UIS7862 variant) quirks; firmware; community notes | TS10 and TS18 share a vendor/ecosystem, so TS10 evidence is partially applicable | Validation matrix should note TS10/TS18 differences explicitly | That TS10 findings directly transfer to TS18 | Use as supplemental evidence and test matrix framing, label per device | Observed | Useful as evidence only | XDA community posts. |
| FCC TS18 product manual | https://fcc.report/FCC-ID/2BECX-TS18/7046050.pdf | TS18/TWTHEME ecosystem | FCC-certified TS18 hardware specification; physical button layout; MCU/firmware update flow | TS18 is a certified automotive unit with defined hardware interface | Physical hardware button layout is a known quantity for acceptance testing | Software API contracts; OEM firmware variants | Use as authoritative hardware context for media-button acceptance scenarios | Observed | Useful as evidence only | FCC public filing. |
| **— Priority 2: TWUtil/TWClient/Topway private framework —** | | | | | | | | | | |
| CarRadio (ivvlev) | https://github.com/ivvlev/CarRadio | TWUtil/TWClient reference | `android.tw.john.TWUtil` and `android.tw.john.TWClient` class names; TWUtil/TWClient fallback pattern when firmware classes absent; hardware command open() style | TWUtil/TWClient exist in public OEM-adjacent codebases; fallback emulation pattern is documented | Private OEM classes exist and are expected by some vendor integrations | Need for these hooks in Auxio-TS third-party app | Reference-only; do not add TWUtil/TWClient reflection or dependency to product code | Observed | Unsafe to port | Apache 2.0 license. Code is unsafe to port because it depends on firmware-private classes. |
| dvd-bt TWUtil.java | https://github.com/asb72/dvd-bt/blob/master/app/src/main/java/com/tw/bt/twUtil.java | TWUtil/TWClient reference | Direct TWUtil import/compile dependency; open() command pattern; hardware write-through usage | Direct TWUtil usage pattern exists in OEM app code; the pattern is identifiable and heavy | Private contract coupling is real and brittle | Safe portability to third-party Auxio app | Do not import, compile against, or copy this pattern | Observed | Unsafe to port | Licence not clearly stated in repo; treat as proprietary reference. Do not copy code. |
| KaierUtils (d51x) | https://github.com/d51x/KaierUtils | TWUtil/TWClient reference | TWUtil usage on KSW/ZXW head units; volume control via TW-private API; compilation against firmware classes | TWUtil is used across multiple vendor head unit platforms (KSW/ZXW/TopWay) for privileged volume/system control | The pattern is reused but not standardised across vendor platforms | Whether TS18 requires the same TWUtil hooks | Document TWUtil usage as a known pattern; validate whether standard Android AudioManager is sufficient before considering any TW-private path | Observed | Unsafe to port | Apache 2.0 license. Code is unsafe to port because it depends on firmware-private classes. |
| topwaytool | https://github.com/mkotyk/topwaytool | firmware/tooling ecosystem | Topway firmware image extraction and analysis tooling; MCU/ADB manipulation | Topway firmware can be analyzed externally; tooling exists for firmware manipulation | Firmware-level analysis can stay external to app code | App-runtime contract guarantees | External research tool only; no in-app hooks | Observed | Useful as evidence only | MIT license. Useful as firmware-analysis reference; do not embed firmware tooling in app. |
| **— Priority 3: Battle-tested public head-unit projects —** | | | | | | | | | | |
| OpenRadioFM | https://github.com/kapi21/OpenRadioFM | public head-unit project | Multi-hardware separation; `com.syu.radio` FYT intent integration; explicit platform-specific OEM paths; hardware abstraction layer | Explicit platform-specific feature isolation is a proven head-unit app pattern | Prefer explicit feature flags/paths over hidden probes for OEM compat | Transferability of FYT APIs to TS18 | If needed, implement explicit compatibility feature per validated gap, not generic probe | Observed | Reusable validation idea | GPL. Do not copy code directly. Architecture pattern is reusable as design evidence. |
| Display-Media-Titles | https://github.com/vasyl91/Display-Media-Titles | public head-unit project | MediaController metadata discovery; title/artist display; status-bar/widget display; system vs non-system app limitations | MediaController-visible metadata is a viable approach for head-unit metadata widgets | FYT/TS18 system-level overlays may require system-app status to show metadata | That non-system MediaSession is always visible to HU overlays | Validate metadata visibility for Auxio-TS (non-system) against iLauncher widget | Observed | Reusable validation idea | GPL. Do not copy code directly. Validation approach is reusable. |
| FytHWOneKey | https://github.com/hvdwolf/FytHWOneKey | public head-unit project | Hardware-button remapping without root; media key events via Android resolver/default-app flow; standard media key chain | Standard media key routing can be used for HU hardware button assignment | Media key assignment may be sufficient for steering wheel/hardware buttons | TS18-specific button routing differences | Test media-key routing as primary strategy; add TS18-specific notes if standard path fails | Observed | Reusable validation idea | GPL. Architecture pattern is reusable as validation evidence; do not copy code. |
| FET | https://github.com/hvdwolf/FET | public head-unit project | FYT ecosystem tooling and conventions | FYT ecosystem is active and diverse | Community tooling context is useful for validation planning | App-level media contract requirements | Reference-only context | Observed | Useful as evidence only | GPL. |
| FYTuis7862BinRepo | https://github.com/hvdwolf/FYTuis7862BinRepo | public head-unit project | Firmware/mod variance across FYT UIS7862 units; package and binary content reference | Firmware variance is real and must be tracked in acceptance evidence | Test matrix should note firmware/theme variant per evidence run | Auxio API contract requirements | Capture firmware/theme variant labels in all evidence reports | Observed | Reusable validation idea | Public repo; content is firmware/binary reference. |
| RK3066-headunit-service | https://github.com/petrows/RK3066-headunit-service | public head-unit project | Legacy RK3066 HU service; media-button remapping; CAN-key bridge concept | Vendor service-based key remapping was used in older HU stacks | Legacy patterns suggest vendor service assumptions are risky on newer units | TS18 applicability or modern Auxio parity requirements | Historical context only; avoid service-binding assumptions | Observed | Useful as evidence only | GPL. |
| Headunit Revived | https://github.com/andreknieriem/headunit-revived | public head-unit project | Android Auto receiver-side behaviour; controller expectations | AA receiver/controller expectations can be studied publicly | Helps frame Android Auto validation expectations | TS18-specific app contract needs | Use for validation thinking; not direct TS18 implementation | Observed | Useful as evidence only | GPL. |
| mikereidis/headunit | https://github.com/mikereidis/headunit | public head-unit project | Head-unit receiver AA protocol context | Controller compatibility testing should be deliberate | Useful for AA test framing | Music-app internal feature requirements | Reference-only | Observed | Useful as evidence only | Check repo for license. |
| Spotube AA issue | https://github.com/KRTirtho/spotube/issues/1232 | public issue evidence | Real-world AA failures from improper service/library architecture | AA compatibility failures occur without proper session/library setup | Acceptance criteria should include AA behaviour checks | Generalizable fix for Auxio-TS | Use as cautionary validation evidence | Observed | Reusable validation idea | Issue thread; discussion content only. |
| **— Priority 4: ZLink/TLink/carchoose ecosystem —** | | | | | | | | | | |
| ZLink5 / Dingwei (com.zjinnova.zlink) | https://zlink5.com/ | ZLink/TLink ecosystem | ZLink5 is the active phone-link app on captured TS18 (`persist.phone_connect_app=com.zjinnova.zlink`); ZLink occupies foreground during projection | ZLink is a first-class competitor for audio focus and MediaSession foreground on TS18 | ZLink/Auxio-TS coexistence must be an explicit acceptance scenario | Whether standard MediaSession arbitration is sufficient to survive ZLink | Test TS18-ACC-005 (ZLink coexistence) including: focus handoff, session visibility during/after projection, metadata recovery | Observed | Requires TS18 runtime validation | Public product site; no code to port. ZLink APK is proprietary. |
| XDA TLink versions thread | https://xdaforums.com/t/list-of-tlink-versions-availables.4387577/ | ZLink/TLink ecosystem | TLink is an alternative/older phone-link app; version history; TS18 compatibility notes | TLink and ZLink are interchangeable on some TS18 builds; version matters for coexistence behaviour | Acceptance tests should note which phone-link app is active | Which app is active on a given unit without testing | Record active phone-link package in every evidence header | Observed | Reusable validation idea | XDA community posts. |
| **— Priority 5: Local evidence —** | | | | | | | | | | |
| t-music evidence snapshot | `docs/evidence/t-music-snapshot/` | local evidence corpus | Stock manifest (`com.tw.music` + `android.uid.system`); TW broadcast actions; AIDL surfaces; TWTHEME paths; vendor hook ownership map | Stock-private contracts and behaviours are visible; vendor-hook surface map is documented | Strong source for parity hypotheses and validation scenario input | Mandatory third-party Auxio implementation needs | Use as evidence + validation checklist; do not port smali or copy proprietary code | Observed | Useful as evidence only | Proprietary evidence; do not redistribute or copy. |
| t-music vendor hook map | `docs/evidence/t-music-snapshot/docs/reports/vendor-hook-owners.md` | local evidence corpus | Maps `com.tw.music.action.*`, TW AIDL descriptors, EQ/Radio/BT handoff surfaces to specific smali owners | Vendor contract boundary ownership is documented | Each hook must be independently validated before any Auxio-TS consideration | That any of these hooks are needed or safe for Auxio-TS | Use as contract surface inventory for acceptance test design only | Observed | Useful as evidence only | Proprietary evidence. |
| Auxio-TS TS18 diagnostics | `diagnostics/redacted/ts18_device_profile.json` | local device evidence | `com.tw.*` packages at priv-app UID 1000; `com.zjinnova.zlink` active; TWTHEME assets at `/system/etc/theme/`; `key 171/213 MUSIC` in keylayout | Target package/theme environment markers exist on captured device | Prioritises acceptance scenarios and risk ranking | Causal proof of needed code changes; all firmware variants | Use to prioritise test scenarios and feature proposals | Observed | Reusable validation idea | Redacted device capture; do not re-identify device. |
| **— Android standards (assumed baseline) —** | | | | | | | | | | |
| Android Media3 + session/library docs | https://developer.android.com/media/media3 | official Android docs (baseline) | Standard media stack contracts for session, library, notification, auto, audio focus | Android standard behavior for all media contracts | Stay aligned with standard APIs first | TS18 vendor-private behaviour | Keep implementation standards-first; consult when TS18 validation reveals a gap with standard behavior | Observed | Directly reusable requirement | Public Android docs. |
| AOSP Car / automotive audio docs | https://source.android.com/docs/automotive/audio/audio-focus | official Android docs (baseline) | AAOS focus interaction matrix; car media surface expectations | Coexistence scenarios should align with AAOS expectations | Useful comparator for nav/media coexistence | TS18 daemon-specific overrides | Use for coexistence acceptance scenario framing | Observed | Reusable validation idea | Public AOSP docs. |

## How to use TS18/TW/TWTHEME sources

1. **Start with Priority 1 and Priority 2 sources** for any TS18-specific question before proposing product-code changes or probes.
2. Add any newly-found useful sources to this table before implementing.
3. Classify every claim with both `Confidence` and `Porting decision` labels from the canonical taxonomy above.
4. Use Priority 3–4 (local evidence + user diagnostics) to refine acceptance scenarios.
5. Only resort to Priority 5 (new probes) when no source-led path exists, and keep probe output external to product code.

## How `t-music` evidence should be used
- As requirement clues, parity hypotheses, and validation scenario input.
- Not as source code or direct API contract authority.
- Always pair with confidence labels and porting-decision labels.

## Official Android baseline
Auxio-TS product work should prioritize:
- MediaSessionService / MediaLibraryService correctness.
- External controller and Android Auto compatibility.
- Notification/media controls and media-button pathways.
- Audio focus and navigation mixing.
- Head-unit-safe browse and interaction UX.

## Integrations to implement first
1. Android-standard MediaSession + notification hardening.
2. MediaLibraryService/Android Auto browsing hardening.
3. Media button and steering-wheel compatibility through standard APIs.
4. Audio focus and navigation mixing hardening.
5. Head-unit UI/UX layout and readability hardening.

## Validation-only areas (for now)
- `com.tw.music.action.*` private actions — Confidence: **Observed**; Auxio-TS porting decision: **Requires TS18 runtime validation**.
- `com.tw.service` / `com.tw.service.xt` service assumptions — Confidence: **Observed**; Auxio-TS porting decision: **Useful as evidence only**.
- `android.tw.john.TWUtil` / `TWClient` behavior — Confidence: **Observed**; Auxio-TS porting decision: **Unsafe to port**.
- TWTHEME private resource path assumptions (`MusicTheme.apk`, `LauncherTheme.apk`, `theme_config.json`) — Confidence: **Requires TS18 validation**; Auxio-TS porting decision: **Requires TS18 runtime validation**.
- ZLink (`com.zjinnova.zlink`) / TLink coexistence beyond standard session/focus observations — Confidence: **Observed** (package seen on device); Auxio-TS porting decision: **Requires TS18 runtime validation**.
- `com.tw.carchoose` / carchoose package selection mechanism — Confidence: **Hypothesis**; Auxio-TS porting decision: **Requires TS18 runtime validation**.
- iLauncher/TWTHEME media widget update mechanism — Confidence: **Requires TS18 validation**; Auxio-TS porting decision: **Requires TS18 runtime validation**.

## Never add without explicit target + source justification
- Package impersonation (`com.tw.music`) or `android.uid.system` dependency — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
- In-app runtime TW probing/scanning frameworks — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Hidden diagnostics scaffolding in product code — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Vendor binder/service integration without concrete feature design — Confidence: **Inferred**; Auxio-TS porting decision: **Unsafe to port**.
- Copied decompiled smali/proprietary implementation — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
- `TWUtil`/`TWClient` reflection or import in product code — Confidence: **Observed**; Auxio-TS porting decision: **Unsafe to port**.
