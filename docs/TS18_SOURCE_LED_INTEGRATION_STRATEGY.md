# TS18 Source-Led Integration Strategy (Pivot)

## Why probe/default-off in-app scaffolding was rejected
- It introduces speculative product code without a concrete user-facing feature.
- It risks dead paths and vendor lock-in pressure before proving actual gaps in Android-standard behavior.
- It blurs the boundary between evidence gathering and maintainable implementation.
- It weakens long-term maintainability of a clean Auxio fork.

## Source-led development principles
1. **Android docs are implementation authority** for media/session/library/car/audio-focus behavior.
2. **Public head-unit projects are architecture references**, not direct implementation templates.
3. **`t-music` snapshot is evidence**, not app code to port.
4. **TS18 runtime testing is acceptance validation**, not justification for speculative in-app probes.
5. **One explicit compatibility feature per PR** after source + runtime evidence justifies it.

## Official Android baseline
Auxio-TS product work should prioritize:
- MediaSessionService / MediaLibraryService correctness.
- External controller and Android Auto compatibility.
- Notification/media controls and media-button pathways.
- Audio focus and navigation mixing.
- Head-unit-safe browse and interaction UX.

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

## Source corpus and implications

| Source | URL | Source class | What it proves | What it suggests | What it cannot prove | Auxio-TS implementation implication | Confidence | Auxio-TS porting decision |
|---|---|---|---|---|---|---|---|---|
| Media3 overview | https://developer.android.com/media/media3 | official Android docs | Media3 is the standard app-layer media stack. | Stay aligned with standard service/session/library APIs. | TS18 vendor-private behavior. | Keep implementation standards-first. | Observed | Directly reusable requirement |
| MediaSession control playback | https://developer.android.com/media/media3/session/control-playback | official Android docs | Controller/session command model for transport controls. | External control issues should first be solved in session/controller contract. | OEM private remap logic. | Harden playback command handling using Media3 conventions. | Observed | Directly reusable requirement |
| MediaSessionService background playback | https://developer.android.com/media/media3/session/background-playback | official Android docs | Service-backed background playback lifecycle expectations. | Foreground service/session behavior remains primary path. | Launcher-specific metadata quirks. | Preserve and harden service lifecycle correctness. | Observed | Directly reusable requirement |
| MediaLibraryService serve content | https://developer.android.com/media/media3/session/serve-content | official Android docs | Browse tree and library serving model for external clients. | Android Auto compatibility depends on library correctness. | Vendor app-store/launcher quirks. | Prioritize browse stability and metadata completeness. | Observed | Directly reusable requirement |
| Android for Cars media apps | https://developer.android.com/media/implement/surfaces/cars | official Android docs | Car media app UX/surface expectations. | Head-unit UX should be car-safe and browse-first. | TS18 private launcher contracts. | Use as UX baseline for car/head-unit flows. | Observed | Directly reusable requirement |
| Mobile/system media controls | https://developer.android.com/media/implement/surfaces/mobile | official Android docs | Standard system controls and metadata presentation behavior. | Metadata quality strongly affects controllers/widgets. | Vendor-private widget contracts. | Ensure metadata/session signals are high quality. | Observed | Directly reusable requirement |
| Android audio focus | https://developer.android.com/media/optimize/audio-focus | official Android docs | Focus gain/loss/duck behavior for app media. | Navigation mixing issues should first be focus-policy fixes. | OEM overrides of focus routing. | Harden audio focus transitions before vendor-specific ideas. | Observed | Directly reusable requirement |
| AOSP automotive audio focus | https://source.android.com/docs/automotive/audio/audio-focus | official Android docs | AAOS focus interaction policy/matrix. | Coexistence scenarios should be validated with clear focus expectations. | TS18 daemon-specific behavior. | Add acceptance scenarios for nav/media coexistence. | Observed | Reusable validation idea |
| AOSP Car Media app | https://android.googlesource.com/platform/packages/apps/Car/Media/ | official Android docs | Reference AAOS media app architecture and browse expectations. | Useful comparator for browse/session integration quality. | Direct TS18 contract requirements. | Use as design reference only. | Observed | Useful as evidence only |
| Display-Media-Titles | https://github.com/vasyl91/Display-Media-Titles | public head-unit project | FYT-facing app demonstrates value of MediaController-visible metadata; notes system-app constraints. | Metadata/session quality is critical for HU overlays/widgets. | Universal behavior on all TS18 builds. | Prioritize metadata/session correctness and acceptance checks. | Observed | Reusable validation idea |
| Display-Media-Titles XDA thread | https://xdaforums.com/t/display-media-titles-for-fyt.4692979/ | community/firmware ecosystem | Community deployment patterns and constraints. | Validation should include launcher/system-app variance notes. | Formal API stability. | Use as validation context only. | Observed | Useful as evidence only |
| OpenRadioFM | https://github.com/kapi21/OpenRadioFM | public head-unit project | Multi-hardware separation and explicit OEM paths are used in practice. | Prefer explicit feature isolation over hidden generic probes. | Transferability to Auxio + TS18. | If needed, implement explicit compatibility feature per validated gap. | Observed | Reusable validation idea |
| OpenRadioFM XDA thread | https://xdaforums.com/t/openradiofm.4777033/ | community/firmware ecosystem | Community-tested deployment context for multi-HU builds. | Feature rollout should consider platform variance. | Formal API guarantees. | Evidence input for validation planning. | Observed | Useful as evidence only |
| FytHWOneKey | https://github.com/hvdwolf/FytHWOneKey | public head-unit project | Hardware button routing can rely on resolver/default-app/media key flows and work without root. | Standard media-key behavior can solve many HU control needs. | TS18-specific media stack parity. | Harden standard media-button compatibility first. | Observed | Reusable validation idea |
| FET | https://github.com/hvdwolf/FET | public head-unit project | FYT ecosystem tooling and integrations are active. | Community ecosystem context is useful for operational validation. | App-level media contract requirements. | Reference-only context. | Observed | Useful as evidence only |
| FYTuis7862BinRepo | https://github.com/hvdwolf/FYTuis7862BinRepo | public head-unit project | Firmware/mod variance across FYT units is real. | Test matrix should track firmware/theme variants. | Auxio API contract requirements. | Capture firmware/theme metadata in evidence reports. | Observed | Reusable validation idea |
| RK3066-headunit-service | https://github.com/petrows/RK3066-headunit-service | public head-unit project | Legacy HU service/key-routing precedent exists. | Prior Android HU stacks often used vendor service bindings. | TS18 applicability or modern Auxio parity requirements. | Historical context only; avoid direct service-porting assumptions. | Observed | Useful as evidence only |
| Headunit Revived | https://github.com/andreknieriem/headunit-revived | public head-unit project | Android Auto receiver-style behavior and controller expectations can be studied publicly. | Helps frame Android Auto validation expectations. | TS18-specific app contract needs. | Use for validation thinking, not direct TS18 implementation. | Observed | Useful as evidence only |
| mikereidis/headunit | https://github.com/mikereidis/headunit | public head-unit project | Head-unit receiver project provides AA protocol context. | Controller compatibility testing should be deliberate. | Music-app internal feature requirements. | Reference-only for AA test framing. | Observed | Useful as evidence only |
| Spotube AA issue | https://github.com/KRTirtho/spotube/issues/1232 | public issue evidence | Real-world AA compatibility failures occur without proper service/library architecture. | Acceptance criteria should include AA behavior checks. | Generalizable fix for Auxio-TS. | Use as cautionary validation evidence. | Observed | Reusable validation idea |
| CarRadio | https://github.com/ivvlev/CarRadio | public head-unit project | TWUtil/TWClient private hooks exist in ecosystem. | OEM-private contracts are real but vendor-specific. | Need for such hooks in Auxio-TS. | Reference-only; do not add TWUtil/TWClient probing code. | Observed | Unsafe to port |
| dvd-bt twUtil | https://github.com/asb72/dvd-bt/blob/master/app/src/main/java/com/tw/bt/twUtil.java | public OEM-linked sample | Direct TWUtil/open-write command pattern exists in OEM app code. | Private contract coupling can be heavy and brittle. | Safe applicability to third-party Auxio app. | Do not link/import this pattern in product code. | Observed | Unsafe to port |
| topwaytool | https://github.com/mkotyk/topwaytool | firmware/tooling ecosystem | Topway firmware tooling is publicly available. | Firmware analysis can stay external to app code. | Runtime app contract guarantees. | External research only; no in-app hooks. | Observed | Useful as evidence only |
| DoFun TS channel | https://t.me/s/dofun_app | community/firmware ecosystem | Theme/firmware ecosystem changes are publicly visible. | Variant tracking matters for test planning. | Formal API contracts. | Use for validation matrix inputs only. | Observed | Useful as evidence only |
| Mekede TS10-TS18 | https://mekede.com/TS10-TS18/ | community/firmware ecosystem | Commercial TS10/TS18 family segmentation exists. | Device family labels should be recorded in evidence. | App-layer API requirements. | Evidence metadata only. | Observed | Useful as evidence only |
| XDA TS18 thread | https://xdaforums.com/t/ts18-devices-branded-topway-alwinner-online-help-firmwares-and-other-stuff.4558675/ | community/firmware ecosystem | Community-reported TS18 variance and quirks. | Runtime checks should cover multiple variants when possible. | Deterministic implementation contract. | Community evidence for acceptance planning. | Observed | Useful as evidence only |
| XDA TS10 thread | https://xdaforums.com/t/new-topway-ts10-uis7862-6gb-ram-128gb-head-unit-q-as.4227947/ | community/firmware ecosystem | Additional Topway/TS-family ecosystem evidence. | Family overlap may affect validation matrix scope. | TS18-specific certainty. | Community context only. | Observed | Useful as evidence only |
| Magisk issue 4369 | https://github.com/topjohnwu/Magisk/issues/4369 | community/firmware ecosystem | Illustrates ecosystem/root mod realities on some units. | Root discussions are common but not product requirements. | Any app-level contract requirement. | Keep out of product implementation scope. | Observed | Should be explicitly avoided |
| t-music evidence snapshot | `docs/evidence/t-music-snapshot/` | local evidence corpus | Stock/private contracts and behaviors are visible as evidence. | Strong source for parity hypotheses and risk matrix. | Mandatory third-party Auxio implementation needs. | Use as evidence + validation checklist, not code source. | Observed | Useful as evidence only |
| Auxio-TS TS18 diagnostics | `diagnostics/redacted/ts18_device_profile.json` | local device evidence | Target package/theme environment markers exist (`com.tw.*`, `com.zjinnova.zlink`, TWTHEME artifacts). | Prioritizes acceptance scenarios and risk ranking. | Causal proof of needed code changes. | Use to prioritize test scenarios and feature proposals. | Observed | Reusable validation idea |

## How `t-music` evidence should be used
- As requirement clues, parity hypotheses, and validation scenario input.
- Not as source code or direct API contract authority.
- Always pair with confidence labels and porting-decision labels.

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

## Never add without explicit target + source justification
- Package impersonation (`com.tw.music`) or `android.uid.system` dependency — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
- In-app runtime TW probing/scanning frameworks — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Hidden diagnostics scaffolding in product code — Confidence: **Inferred**; Auxio-TS porting decision: **Should be explicitly avoided**.
- Vendor binder/service integration without concrete feature design — Confidence: **Inferred**; Auxio-TS porting decision: **Unsafe to port**.
- Copied decompiled smali/proprietary implementation — Confidence: **Observed**; Auxio-TS porting decision: **Should be explicitly avoided**.
