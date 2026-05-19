# TS18 Source-Led Integration Strategy

## TS18/TW/TWTHEME source-led policy

TS18/TW/TWTHEME work must begin with the curated TS18/Topway/DoFun/TW source corpus and battle-tested public head-unit projects.

Android/Media3 standards remain the baseline implementation authority, but they are not enough to answer TS18/TWTHEME-specific questions. For TS18-specific behaviour, agents must first search and classify TS18/TW/TWTHEME ecosystem sources before proposing implementation or validation changes.

This strategy exists because TS18/TW/TWTHEME evidence is fragmented across public repos, firmware/tooling pages, DoFun/iLauncher ecosystem material, XDA threads, and local `t-music` evidence. Official Android/Media3/car/audio docs are the assumed baseline implementation authority and remain available; this document focuses on the harder-to-find TS18/TW/TWTHEME corpus.

Probe/diagnostics-driven work is secondary. It is allowed only when:
- the user provides fresh diagnostics;
- the repo already contains relevant captured evidence;
- no reliable public TS18/TW/TWTHEME or equivalent head-unit source exists;
- the output remains an external validation/runbook step, not speculative product-code scaffolding.

Do not add in-app TS18 probe frameworks, default-off vendor adapter skeletons, TWUtil/TWClient reflection scanners, vendor-service binders, package impersonation, `android.uid.system`/`sharedUserId` strategies, copied smali, or hidden diagnostics modules.

## Required source priority order

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
  - Topway / TS10 / TS18 firmware references
  - DoFun / iLauncher / TWTHEME material
  - TWUtil / TWClient public references
  - ZLink/TLink/carchoose ecosystem clues
  - TWTHEME theme/window/PiP/launcher behaviour sources

Priority 2: Battle-tested public head-unit projects
  - Projects showing working Android head-unit integration patterns
  - Media metadata exposure, hardware-key routing, launcher/widget behaviour,
    and platform isolation precedents

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/
  - diagnostics/redacted/ts18_device_profile.json
  - Existing Auxio-TS TS18 docs

Priority 4: User-provided diagnostics
  - Fresh TS18 logs, dumpsys, bugreport extracts, package lists,
    theme APK listings, launcher behaviour captures

Priority 5: New probes/diagnostics
  - Allowed only when no reliable source, public equivalent, or
    user-provided evidence exists
  - Prefer external scripts/manual runbook steps
  - Do not add speculative probe frameworks to product app code
```

## Why probe/default-off in-app scaffolding was rejected
- It introduces speculative product code without a concrete user-facing feature.
- It risks dead paths and vendor lock-in pressure before proving actual gaps in Android-standard behavior.
- It blurs the boundary between evidence gathering and maintainable implementation.
- It weakens long-term maintainability of a clean Auxio fork.

## Source-led development principles
1. **Android docs are implementation authority** for media/session/library/car/audio-focus behavior.
2. **TS18/TW/TWTHEME ecosystem sources are the first resort** for TS18-specific behaviour questions.
3. **Public head-unit projects are architecture references**, not direct implementation templates.
4. **`t-music` snapshot is evidence**, not app code to port.
5. **TS18 runtime testing is acceptance validation**, not justification for speculative in-app probes.
6. **One explicit compatibility feature per PR** after source + runtime evidence justifies it.

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

---

## TS18/TW/TWTHEME source corpus

> Treat DoFun/Telegram/XDA/YouTube/forum material as ecosystem evidence, not formal API documentation.
> Use it to shape compatibility dimensions and acceptance validation.
> Do not use it to justify copied smali, package impersonation, vendor-private binders, or product-code probes.

### Priority 1A — TS18/Topway/DoFun/iLauncher/TWTHEME ecosystem sources

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| DoFun Telegram channel | https://t.me/s/dofun_app | community/firmware ecosystem | Theme/firmware release notes; TS18.1.2/TS18.2.2 naming; iLauncher update announcements; TWTHEME packaging notes | DoFun distributes TS18 firmware and related ecosystem apps; iLauncher/TWTHEME are active components | Variant tracking matters for acceptance test scope | Formal API contracts or runtime guarantees | Use as ecosystem taxonomy and variant-tracking input | Observed | Useful as evidence only | Community channel; no code to import |
| DoFun Telegram (archive posts ≤26) | https://t.me/s/dofun_app?before=26 | community/firmware ecosystem | Earliest DoFun ecosystem posts; initial iLauncher/TWTHEME release framing | DoFun ecosystem bootstrapped alongside Topway hardware release cycle | Firmware/launcher were co-developed from an early stage | API-level guarantees | Historical context for ecosystem timeline | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤36) | https://t.me/s/dofun_app?before=36 | community/firmware ecosystem | Early firmware drop context for TS18 family | Firmware lineage and ecosystem bootstrap history | Establishes which app versions co-exist | API-level guarantees | Historical context for variant tracking | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤47) | https://t.me/s/dofun_app?before=47 | community/firmware ecosystem | DoFun app / CarMate app early release notes; iLauncher theme pairing | DoFun's own app (CarMate/DoFun Play) is part of the same ecosystem stack | App-to-app coexistence scope includes DoFun Play/CarMate | API-level guarantees | Add DoFun Play/CarMate as a named coexistence variant in acceptance matrix | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤55) | https://t.me/s/dofun_app?before=55 | community/firmware ecosystem | iLauncher / TWTHEME compatibility notes in early builds | Theme/launcher co-dependency observed early in ecosystem | TWTHEME/iLauncher coupling likely predates tested builds | Causal API proof | Variant-tracking context | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤110) | https://t.me/s/dofun_app?before=110 | community/firmware ecosystem | TWTHEME update cycles; ZLink/carchoose coexistence notes | Ecosystem churn is real; TWTHEME updates frequently | Acceptance scenarios should cover multiple theme states | Stable or frozen behaviour | Inform multi-variant acceptance matrix | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤114) | https://t.me/s/dofun_app?before=114 | community/firmware ecosystem | TS18.2.2 horizontal firmware family confirmation | Horizontal/vertical screen split confirmed in firmware naming | TS18.1.2 = vertical, TS18.2.2 = horizontal — separate validation targets | Device-specific API differences | Capture firmware variant in device evidence fingerprint | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram (archive posts ≤128, ≤135, ≤169) | https://t.me/s/dofun_app?before=128 | community/firmware ecosystem | Later DoFun ecosystem posts covering CarMate v2/DoFun Play releases and ZLink5 coexistence; continued TWTHEME updates | DoFun ecosystem is actively maintained with regular release cycles post-TS18.2.2 | ZLink5 / CarMate coexistence scope extends into more recent firmware versions | Post-release API stability | Track DoFun release version in acceptance evidence headers | Observed | Useful as evidence only | Community channel; no code |
| DoFun Telegram index (telemetr.io) | https://telemetr.io/en/channels/1661110355-dofun_app/posts | community/ecosystem index | Archive index with post dates; useful for identifying when specific DoFun/TWTHEME features were introduced | DoFun channel post history is externally indexed and searchable | Post timeline helps correlate TS18 firmware releases with iLauncher/TWTHEME feature changes | Authoritative API documentation | Discovery and timeline tool; cross-reference with specific archive pages | Observed | Useful as evidence only | Third-party index; no code |
| DoFun website — xolio product | https://www.dofun.cc/xolio/index-en.html | vendor product page | DoFun Xolio product line description (distinct from iLauncher/car-desktop); positioned as a broader DoFun ecosystem brand | DoFun has multiple product sub-brands; Xolio may ship with different TS18 builds | Acceptance evidence should note which DoFun sub-brand ships on device under test | Whether Xolio vs iLauncher creates distinct API requirements | Record DoFun product variant (iLauncher vs Xolio) in device evidence | Observed | Useful as evidence only | Vendor page; no code to port |
| DoFun website — ecology/ecosystem | https://www.dofun.cc/car-equ/dofun-echology-en.html | vendor product page | DoFun ecosystem description; app/service stack overview including CarMate and connectivity apps | DoFun frames a multi-app connectivity ecosystem around TS18 hardware | Ecosystem scope is wider than just iLauncher; includes phone-link and navigation apps | Internal SDK or API specs | Use to scope full ecosystem coexistence surface for acceptance matrix | Observed | Useful as evidence only | Vendor page; no code to port |
| DoFun app portal | https://app.dofuncar.com/ | vendor product page | DoFun cloud app portal; app download and OTA update hub for DoFun-branded devices | DoFun provides an OTA/app-distribution service for TS18 ecosystem apps | App updates may change coexistence behavior without device firmware changes | Whether Auxio-TS needs special handling for DoFun-distributed updates | Track DoFun app portal version numbers as part of acceptance evidence | Observed | Useful as evidence only | Vendor portal; no code to port |
| DoFun website — car-desktop | https://www.dofun.cc/car-desktop/car-desktop-en.html | vendor product page | DoFun product family description; theme/app ecosystem framing; iLauncher/TWTHEME product positioning | DoFun/iLauncher/TWTHEME is a commercial product ecosystem, not just community mods | Ecosystem has an owner with versioning; acceptance should track DoFun release cycles | Internal SDK or API specs | Use product positioning to scope ecosystem taxonomy | Observed | Useful as evidence only | Vendor page; no code to port |
| DoFun BBS article | https://bbs.dofun.cc/article/630128 | community/firmware ecosystem | DoFun community article (content varies); ecosystem discussion and user reports | DoFun BBS is an active user community for TWTHEME/iLauncher behavior reports | Community reports may preemptively surface app-compat issues seen on real TS18 devices | Authoritative API documentation | Discovery source; verify content on access | Observed | Useful as evidence only | Community forum; no code |
| iLauncher website | https://www.ilauncher.net/ | vendor product page | iLauncher product description; supported head-unit families including TS-series; theme/widget feature list | iLauncher is a dedicated head-unit launcher replacement that targets TS/Topway units | Launcher may expose widget/theme surfaces distinct from stock Topway launcher | Whether Auxio-TS needs iLauncher-specific adaptation | Record as launcher variant in acceptance matrix | Observed | Useful as evidence only | Vendor page; no code to port |
| Mekede TS10/TS18 page | https://www.mekede.com/TS10-TS18/ | community/firmware ecosystem | TS10/TS18 hardware family segmentation; SoC/platform notes | Commercial segmentation of TS10 vs TS18 family exists and is publicly documented | Device family labels should be in evidence fingerprints | App-layer API requirements | Device evidence metadata only | Observed | Useful as evidence only | Community/vendor page; no code |
| XDA — TS18 main thread | https://xdaforums.com/t/ts18-devices-branded-topway-alwinner-online-help-firmwares-and-other-stuff.4558675/ | community/firmware ecosystem | Community-reported TS18 quirks; firmware variant list; TWTHEME/iLauncher compatibility issues; media app behavior reports | Multiple firmware/theme variants with inconsistent behavior are widely reported | Runtime acceptance should cover multiple variants | Deterministic behavior across all units | Use as acceptance test scope input | Observed | Useful as evidence only | Community thread; no code to import |
| XDA — TS18 short thread | https://xdaforums.com/t/ts18.4488709/ | community/firmware ecosystem | Additional TS18 device reports and quirks | Further corroboration of TS18 ecosystem variance | Media/launcher behavior differs across devices | Definitive per-device API mapping | Variant-tracking context | Observed | Useful as evidence only | Community thread; no code |
| XDA — TS18 firmware thread | https://xdaforums.com/t/firmware-ts18.4664566/ | community/firmware ecosystem | TS18-specific firmware update thread; community testing of new firmware builds | Firmware updates are distributed and tested by the community | New firmware versions may change media/launcher behavior; track firmware version in acceptance evidence | API stability between firmware versions | Include firmware version in all acceptance evidence headers | Observed | Useful as evidence only | Community thread; no code |
| XDA — TS10 UIS7862 thread | https://xdaforums.com/t/new-topway-ts10-uis7862-6gb-ram-128gb-head-unit-q-as.4227947/ | community/firmware ecosystem | Topway TS10 (UIS7862 SoC) ecosystem evidence | TS10/TS18 share Topway firmware lineage; TS10 evidence informs TS18 hypothesis generation | Family-level overlap may affect acceptance scope | TS18-specific certainty | Cross-reference when TS10 evidence is stronger | Observed | Useful as evidence only | Community thread; no code |
| XDA — iLauncher / Infinite Launcher TS18 fix | https://xdaforums.com/t/new-car-launcher-infinite-ilauncher-last-update-version-v7-5-0-fix-ts18-head-unit-cant-read-image-problem.4484461/ | community/firmware ecosystem | iLauncher v7.5.0 fixed a TS18 image-reading regression; thread discusses TS18/launcher widget behavior | iLauncher has a public history of TS18-specific fixes; widget image display is a known TS18 issue area | Metadata/artwork display from Auxio-TS may be affected by same image-handling paths | Whether standard MediaSession covers this gap | Add artwork/metadata quality scenario to acceptance matrix | Observed | Reusable validation idea | Community thread; no code to port |
| XDA — Topway tag | https://xdaforums.com/tags/topway/ | community/firmware ecosystem | Index of Topway-related threads | Topway has an active XDA community with multiple firmware/app threads | Discovery source for new TS18/TWTHEME evidence | Any authoritative API documentation | Discovery index for additional sources | Observed | Useful as evidence only | Community index |
| TS18 product manual (manuals.plus) | https://manuals.plus/ae/1005004816467197 | vendor/community docs | TS18 product manual extract; hardware/software feature list; interface specs | TS18 hardware and software features are documented in user-facing manuals | Hardware feature set can serve as acceptance scope input (e.g., media button layout, BT/CarPlay sources) | App-layer API contracts | Use as hardware feature reference for acceptance scenario design | Observed | Useful as evidence only | Third-party manual repost; cross-check with FCC filing |
| FCC report — TS18 (2BECX-TS18) | https://fcc.report/FCC-ID/2BECX-TS18/7046050.pdf | regulatory/hardware documentation | FCC hardware filing for TS18 device; hardware specs; RF/antenna info | TS18 is a real FCC-certified hardware family; device identity confirmed | Hardware fingerprint context for device evidence | App-layer or firmware behavior | Hardware identity reference only | Observed | Useful as evidence only | Public FCC filing; no code |
| Magisk issue 4369 | https://github.com/topjohnwu/Magisk/issues/4369 | community/firmware ecosystem | Root/Magisk compatibility discussion on Topway/TS units | Root discussions are common in ecosystem but are not product requirements | Some test users may have rooted units — note in evidence, not as requirement | Any app-level contract need | Keep out of product implementation scope | Observed | Should be explicitly avoided | Community issue; root out of scope |

### Priority 1B — TWUtil / TWClient / Topway private framework references

These prove that TW private framework classes and serial/hardware command patterns exist. They are critical for understanding the ecosystem, but must be classified unsafe/reference-only for Auxio-TS product code.

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| CarRadio | https://github.com/ivvlev/CarRadio | public head-unit project | Direct `android.tw.john.TWUtil` / `TWClient` usage; fallback emulation class when firmware classes absent | TWUtil/TWClient private hooks exist in ecosystem; fallback pattern for non-TW devices exists | OEM-private contracts are real but vendor-specific | Safe applicability to third-party Auxio app | Documents TWUtil/TWClient existence; do not add reflection or dependency to product code | Observed | Useful as evidence only | Open source; do not copy without licence review; TWUtil/TWClient unsafe to port |
| dvd-bt twUtil.java | https://github.com/asb72/dvd-bt/blob/master/app/src/main/java/com/tw/bt/twUtil.java | public OEM-linked sample | Direct `TWUtil` import; `open(...)` hardware command style | Direct TWUtil usage and open-command pattern exist in OEM app code | Private contract coupling can be heavy and brittle | Safe applicability to third-party Auxio app | Do not import, copy, or compile against this pattern in product code | Observed | Unsafe to port | OEM-linked sample; do not copy; TWUtil/TWClient unsafe to port |
| KaierUtils | https://github.com/d51x/KaierUtils | public head-unit project | Vendor utility helpers for Kaier/Topway head-units; serial/hardware command patterns; `TWUtil`-adjacent helper methods | Similar vendor-private serial/hardware command patterns exist across multiple OEM app families | Pattern is consistent: direct serial/hardware control is not the Android standard approach | Safe applicability to Auxio-TS | Reference-only for ecosystem understanding; do not import or copy | Observed | Useful as evidence only | Open source; do not copy without licence review; TWUtil/hardware commands unsafe to port |
| topwaytool | https://github.com/mkotyk/topwaytool | firmware/tooling ecosystem | Topway firmware image extraction and manipulation tooling | Firmware analysis can stay external to app code | Useful for firmware/source-map methodology | Runtime app contract guarantees | External research only; no in-app hooks | Observed | Useful as evidence only | Open source; external tooling only |

### Priority 2 — Battle-tested public head-unit projects

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| OpenRadioFM | https://github.com/kapi21/OpenRadioFM | public head-unit project | Multi-hardware separation; radio engine abstraction; FYT/Teyes `com.syu.radio` intent integration; explicit OEM path isolation | Multi-hardware separation and explicit OEM paths are used in practice | Prefer explicit feature isolation over hidden generic probes | Transferability to Auxio + TS18 | If needed, implement explicit compatibility feature per validated gap | Observed | Reusable validation idea | Open source; review licence before copying patterns |
| OpenRadioFM releases | https://github.com/kapi21/OpenRadioFM/releases | public head-unit project | Release history showing incremental platform-specific fixes and multi-platform support additions | Platform-specific improvements are incremental; validates the release-per-feature approach | Confirms importance of version-tracking in evidence/acceptance reports | API stability between releases | Use release history as precedent for one-feature-per-PR discipline | Observed | Reusable validation idea | Open source; licence review |
| OpenRadioFM XDA thread | https://xdaforums.com/t/openradiofm.4777033/ | community/firmware ecosystem | Community-tested deployment context for multi-HU builds | Community deployment and multi-platform testing confirms the approach works | Feature rollout should consider platform variance | Formal API guarantees | Evidence input for validation planning | Observed | Useful as evidence only | Community thread; no code |
| Display-Media-Titles | https://github.com/vasyl91/Display-Media-Titles | public head-unit project | FYT-facing app using MediaController-visible metadata; notes system-app constraints; title/artist/status-bar/widget display | MediaController-visible metadata quality is critical for HU overlays/widgets; system vs non-system app limitations documented | Metadata/session quality strongly affects HU overlays | Universal behavior on all TS18 builds | Prioritize metadata/session correctness and acceptance checks | Observed | Reusable validation idea | Open source; review licence |
| Display-Media-Titles XDA thread | https://xdaforums.com/t/display-media-titles-for-fyt.4692979/ | community/firmware ecosystem | Community deployment patterns and constraints | Validation should include launcher/system-app variance notes | FYT patterns may differ from TS18 but are informative | Formal API stability | Use as validation context only | Observed | Useful as evidence only | Community thread; no code |
| FytHWOneKey | https://github.com/hvdwolf/FytHWOneKey | public head-unit project | Hardware-button remapping without root; media key events; Android resolver/default-app flow | Standard media-key behavior and resolver flow work for HU control without root | Harden standard media-key compatibility first | TS18-specific media stack parity | Validate hardware keys via standard media-button APIs | Observed | Reusable validation idea | Open source; review licence |
| FytHWOneKey releases | https://github.com/hvdwolf/FytHWOneKey/releases | public head-unit project | Release history for FytHWOneKey; incremental platform-specific button-mapping fixes | Platform-specific button mapping requires iterative validation | Version-specific behavior differences reinforce one-feature-per-PR discipline | API stability across FYT devices | Use release history as precedent for incremental compatibility fix tracking | Observed | Useful as evidence only | Open source; licence review |
| FET | https://github.com/hvdwolf/FET | public head-unit project | FYT ecosystem tooling and integrations | Community ecosystem context for operational validation | Useful for external ecosystem operational reference | App-level media contract requirements | Reference-only context | Observed | Useful as evidence only | Open source; reference only |
| FYTuis7862BinRepo | https://github.com/hvdwolf/FYTuis7862BinRepo | public head-unit project | Firmware/mod variance across FYT units | Firmware/theme variant matrix is real and non-trivial | Test matrix should track firmware/theme variants | Auxio API contract requirements | Capture firmware/theme metadata in evidence reports | Observed | Reusable validation idea | Open source; reference only |
| SC98531BinRepo | https://github.com/hvdwolf/SC98531BinRepo | public head-unit project | Binary/firmware content for SC98531 SoC head units (different from FYT UIS7862); platform-specific binary conventions | Platform diversity across head-unit SoC families requires per-platform evidence | Acceptance matrix may need separate tracks for SC98531, UIS7862, and Allwinner-based TS18 | Applicability to TS18-specific Allwinner stack | Use as contrast reference for platform diversity; not directly applicable to TS18 | Observed | Useful as evidence only | Open source; reference only |
| RK3066-headunit-service / Microntek | https://github.com/petrows/RK3066-headunit-service | public head-unit project | Legacy HU service/key-routing; media button remapping; CAN-key bridge via background service | Prior Android HU stacks used vendor service bindings; prior art for media key CAN-bridge | Background service patterns may inform risk framing | TS18 applicability or modern Auxio parity requirements | Historical context only; avoid direct service-porting assumptions | Observed | Useful as evidence only | Open source; licence review needed |
| Microntek service XDA thread | https://xdaforums.com/t/app-microntek-headunit-service.3559283/ | community/firmware ecosystem | Community use of Microntek/RK service approach; deployment constraints | Community deployment of vendor service approach confirms complexity | Complexity and maintenance burden of vendor-service approach | TS18 applicability | Background risk reference; prefer Android-standard paths | Observed | Useful as evidence only | Community thread; no code |
| Microntek on F-Droid | https://f-droid.org/forums/topic/microntek-headunit-service/ | community/firmware ecosystem | F-Droid listing and community notes on Microntek service | Open-source deployment of head-unit service is feasible but constrained | F-Droid constraints mirror Auxio-TS maintainability goals | TS18-specific contracts | Reference context for open-source HU service design | Observed | Useful as evidence only | Community page; no code |
| OpenMobileRadioInterface (EBU) | https://github.com/ebu/OpenMobileRadioInterface | public head-unit project | EBU (European Broadcasting Union) open mobile radio interface specification and reference implementation; formal radio API standard | A formal public standard for mobile radio tuner APIs exists; contrasts with vendor-private head-unit radio hooks | Prefer open standards over vendor-private APIs where applicable; reinforces the source-led approach | TS18-specific radio behavior; direct applicability to music playback | Use as formal-API reference point and contrast against TS18 vendor-private radio hooks | Observed | Useful as evidence only | EBU open standard; Apache 2.0 compatible licence |
| Spotube AA issue | https://github.com/KRTirtho/spotube/issues/1232 | public issue evidence | Real-world Android Auto compatibility failures without proper service/library architecture | AA compatibility failures occur without standards-compliant implementation | Acceptance criteria must include AA behavior checks | Generalizable fix for Auxio-TS | Use as cautionary validation evidence | Observed | Reusable validation idea | Public issue; reference only |

### Priority 2 — Android Auto / head-unit receiver context (secondary)

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| Headunit Revived | https://github.com/andreknieriem/headunit-revived | public head-unit project | Android Auto receiver-style behavior; controller expectations | Android Auto receiver patterns can be studied publicly | Helps frame Android Auto validation expectations | TS18-specific app contract needs | Use for validation thinking, not direct TS18 implementation | Observed | Useful as evidence only | Open source; licence review |
| Headunit Revived wiki settings | https://github.com/andreknieriem/headunit-revived/wiki/Settings | public head-unit project | Detailed settings/configuration documentation for headunit-revived; AA receiver-side tuning parameters | Receiver-side configuration knobs are documented; useful for understanding AA receiver expectations | Helps design AA test scenarios for Auxio-TS controller testing | TS18-specific app contract needs | Use as reference for AA receiver configuration expectations | Observed | Useful as evidence only | Open source; wiki content |
| mikereidis/headunit | https://github.com/mikereidis/headunit | public head-unit project | Head-unit receiver project; AA protocol context | Controller compatibility testing should be deliberate | Prior art for AA controller testing | Music-app internal feature requirements | Reference-only for AA test framing | Observed | Useful as evidence only | Open source; licence review |
| headunit-desktop | https://github.com/viktorgino/headunit-desktop | public head-unit project | Desktop-based HU simulation environment | External AA/HU testing can be done from a desktop environment | Useful for controlled AA testing without physical hardware | TS18-specific behavior | External testing reference | Observed | Useful as evidence only | Open source; licence review |
| hudiy | https://github.com/wiboma/hudiy | public head-unit project | HU display integration patterns | Additional AA receiver pattern examples | Controller/display integration approaches | TS18-specific contracts | Reference for AA/display integration | Observed | Useful as evidence only | Open source; licence review |
| AAGateway | https://github.com/qhuyduong/AAGateway | public head-unit project | Android Auto gateway/relay patterns | AA protocol bridging approaches | May inform Android Auto integration test strategies | TS18-specific contracts | Reference for AA testing context | Observed | Useful as evidence only | Open source; licence review |

### Priority 2 — Broader ecosystem indexes (discovery only)

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| XDA Android head units forum | https://xdaforums.com/f/android-head-units.4267/ | community index | Comprehensive head-unit ecosystem index | Active and diverse community with ongoing HU ecosystem development | Discovery source for additional platform-specific evidence | Any authoritative implementation guidance | Discovery index only; verify each source individually | Observed | Useful as evidence only | Community index |
| Head unit apps gist (limkhashing) | https://gist.github.com/limkhashing/6d1fd9f7443982c33df5d2a5846d90bc | community index | Curated list of head-unit Android apps and projects | Community-curated cross-platform taxonomy | Useful for discovery of additional head-unit projects | Implementation authority | Discovery only | Observed | Useful as evidence only | Community gist |
| Head units index (kamilbrk) | https://kamilbrk.github.io/headunits/ | community index | Platform/firmware taxonomy for Android head units | Ecosystem segmentation of TS10/TS18/FYT/MTCD/MTCB etc. | Family separation important for test matrix scope | API-level certainty | Taxonomy reference for device evidence labeling | Observed | Useful as evidence only | Community index |
| GitHub headunit topic | https://github.com/topics/headunit | community index | GitHub projects tagged `headunit`; includes OpenRadioFM, headunit-revived, and others | Active open-source head-unit project community exists and is discoverable | Discovery source for new public head-unit projects to add to corpus | Any authoritative implementation guidance | Use as discovery index; evaluate each project individually before adding to corpus | Observed | Useful as evidence only | Community index |
| 4PDA Android head unit forum | https://4pda.to/forum/index.php?showforum=704 | community index | Russian-language HU ecosystem with Topway/TS18 coverage | Additional community evidence pool for TS18/Topway | May contain unique TS18/TWTHEME firmware evidence not found on XDA | Authoritative API documentation | Discovery index; language barrier reduces direct utility | Observed | Useful as evidence only | Community index |
| 4PDA TS18 topic | https://4pda.to/forum/index.php?showtopic=1072972 | community/firmware ecosystem | TS18-specific 4PDA thread; community firmware/theme reports | Additional TS18 ecosystem evidence from Russian community | Cross-corroborates XDA/Telegram TWTHEME/firmware findings | Authoritative implementation guidance | Cross-reference for TS18 variant taxonomy | Observed | Useful as evidence only | Community thread |

### Priority 3 — Local repository evidence

| Source name | URL | Source class | Specific evidence extracted | What it proves | What it suggests | What it cannot prove | Auxio-TS implication | Confidence | Porting decision | Licence/porting caution |
|---|---|---|---|---|---|---|---|---|---|---|
| t-music evidence snapshot | `docs/evidence/t-music-snapshot/` | local evidence corpus | Stock/private contracts and behaviors; manifest; actions; vendor hooks; runbook patterns; `com.tw.music.action.*`; `com.tw.service.xt` coupling; UID 1000 model | Private contracts and assumptions used by stock app are documented | Strong source for parity hypotheses and risk matrix | Mandatory third-party Auxio-TS implementation requirements | Use as evidence + validation checklist, not code source; do not port smali | Observed | Useful as evidence only | Proprietary snapshot; do not port smali or copy |
| Auxio-TS TS18 diagnostics | `diagnostics/redacted/ts18_device_profile.json` | local device evidence | Target package/theme environment markers: `com.tw.*`, `com.zjinnova.zlink`, TWTHEME artifacts; UID 1000 for `com.tw.music`; ZLink as active phone-link app | Target device ecosystem is package-rich and TW-private; diagnostics confirm TWTHEME/iLauncher coexistence | Priorities acceptance scenarios and risk ranking | Causal proof of needed code changes | Use to prioritize test scenarios and feature proposals | Observed | Reusable validation idea | Redacted evidence; no raw identifiers to commit |

### Android standard baseline (assumed authority — secondary for TS18-specific questions)

> Official Android/Media3/car/audio docs are the assumed baseline implementation authority and remain available. This section is compact and secondary because standard docs are well-known; the TS18/TW/TWTHEME corpus above is the harder-to-find knowledge this document exists to capture.

| Source name | URL | Source class | What it proves | Auxio-TS implication | Confidence | Porting decision |
|---|---|---|---|---|---|---|
| Media3 overview | https://developer.android.com/media/media3 | official Android docs | Media3 is the standard app-layer media stack | Keep implementation standards-first | Observed | Directly reusable requirement |
| MediaSession control playback | https://developer.android.com/media/media3/session/control-playback | official Android docs | Controller/session command model for transport controls | Harden playback command handling using Media3 conventions | Observed | Directly reusable requirement |
| MediaSessionService background playback | https://developer.android.com/media/media3/session/background-playback | official Android docs | Service-backed background playback lifecycle expectations | Preserve and harden service lifecycle correctness | Observed | Directly reusable requirement |
| MediaLibraryService serve content | https://developer.android.com/media/media3/session/serve-content | official Android docs | Browse tree and library serving model | Prioritize browse stability and metadata completeness | Observed | Directly reusable requirement |
| Android for Cars media apps | https://developer.android.com/media/implement/surfaces/cars | official Android docs | Car media app UX/surface expectations | Use as UX baseline for car/head-unit flows | Observed | Directly reusable requirement |
| Mobile/system media controls | https://developer.android.com/media/implement/surfaces/mobile | official Android docs | Standard system controls and metadata presentation | Ensure metadata/session signals are high quality | Observed | Directly reusable requirement |
| Android audio focus | https://developer.android.com/media/optimize/audio-focus | official Android docs | Focus gain/loss/duck behavior | Harden audio focus transitions before vendor-specific ideas | Observed | Directly reusable requirement |
| AOSP automotive audio focus | https://source.android.com/docs/automotive/audio/audio-focus | official Android docs | AAOS focus interaction policy/matrix | Add acceptance scenarios for nav/media coexistence | Observed | Reusable validation idea |
| AOSP Car Media app | https://android.googlesource.com/platform/packages/apps/Car/Media/ | official Android docs | Reference AAOS media app architecture | Use as design reference only | Observed | Useful as evidence only |

---

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
- TWTHEME private resource path assumptions — Confidence: **Requires TS18 validation**; Auxio-TS porting decision: **Requires TS18 runtime validation**.
- ZLink/TLink side effects beyond standard session/focus observations — Confidence: **Requires TS18 validation**; Auxio-TS porting decision: **Requires TS18 runtime validation**.
- iLauncher widget/image display behavior — Confidence: **Hypothesis**; Auxio-TS porting decision: **Requires TS18 runtime validation**.

## Never add without explicit target + source justification
- Package impersonation (`com.tw.music`) or `android.uid.system` dependency — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
- In-app runtime TW probing/scanning frameworks — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Hidden diagnostics scaffolding in product code — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Vendor binder/service integration without concrete feature design — Confidence: **Inferred**; Auxio-TS porting decision: **Unsafe to port**.
- Copied decompiled smali/proprietary implementation — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
- `TWUtil`/`TWClient` reflection or import in product code — Confidence: **Observed**; Auxio-TS porting decision: **Unsafe to port**.
