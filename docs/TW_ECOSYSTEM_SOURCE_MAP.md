# TW Ecosystem Source Map (TS18/TW/TWTHEME)

This map tracks what each source can and cannot prove for Auxio-TS planning.

## Evidence legend
- **Observed:** directly present in captured diagnostics or source code.
- **Inferred:** reasonable extrapolation, not directly proven.
- **Hypothesis:** weak or unverified claim.
- **Requires TS18 validation:** must be tested on target hardware.

## 1) `cbkii/t-music` (currently inaccessible to this agent)

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| `cbkii/t-music` repository | https://github.com/cbkii/t-music | Access attempt currently returns 404 from this environment. | Repo may be private/renamed/permission-gated. | Any architectural or contract claims until access is granted. | Treat as required pending corpus import task; do not block Android-native work. |

### Human commands to provide a consumable `t-music` snapshot
1. Export a sanitized snapshot locally:
   - `git clone https://github.com/cbkii/t-music.git`
   - `cd t-music`
   - `git --no-pager log --oneline -n 100 > TMUSIC_LOG.txt`
   - `find . -maxdepth 3 -type f \( -name '*.md' -o -name '*.sh' -o -name '*.kt' -o -name '*.java' -o -name '*.xml' -o -name '*.smali' \) > TMUSIC_FILE_INDEX.txt`
2. Redact sensitive files and provide:
   - README/AGENTS/docs/scripts tree,
   - manifest/service/action references,
   - TW/TWTHEME/media-control findings.

## 2) TS18 diagnostics already in this repo

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| TS18 device profile | `diagnostics/redacted/ts18_device_profile.json` | Android 10/API 29 device class, TW package presence (`com.tw.music`, `com.tw.service`, `com.tw.service.xt`, `com.tw.radio`, `com.tw.eq`), TWTHEME files (`MusicTheme.apk`), ZLink package/property. | TW stack likely influences focus/source/widget behavior. | Third-party app compatibility outcomes. | Drive contract table and runtime validation plan. |
| Existing TS18 docs/runbook | `docs/TS18_DIAGNOSTICS_INSIGHTS.md`, `docs/TS18_VALIDATION_RUNBOOK.md` | Current evidence handling policy and baseline checks. | Priority validation sequence. | Runtime behavior not yet captured for Auxio-TS playback on TS18. | Keep evidence-first staged rollout. |

## 3) TS18 manuals / public docs / community material

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| TS18 launcher/phone-link community material (DoFun/iLauncher/DUDU) | Public forum/manual ecosystem (varies by firmware/vendor) | Launchers and link stacks are often OEM-customized and signature-sensitive. | Widget/plugin support may depend on package/signature whitelisting. | Exact policy on this TS18 build. | Keep launcher/TWTHEME work optional and evidence-gated. |
| DUDU/iLauncher signature discussions | Public posts/discussions (vendor/community channels) | Signature/privileged paths are common in HU launcher integrations. | Third-party media widgets may need OEM-approved contract. | Whether Auxio-TS can integrate without privileged signing. | Add package/signature risk section and explicit stop conditions. |

## 4) TWUtil/TWClient and related projects

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| `asb72/dvd-bt` | https://github.com/asb72/dvd-bt (`app/src/main/java/com/tw/bt/twUtil.java`) | `twUtil` extends `android.tw.john.TWUtil` and uses low-level open/start/write/stop/close command flow. | TW command channels are usable on some TW-family systems. | Applicability to this TS18 firmware/app sandbox. | Use only as contract/evidence reference; no blind API adoption. |
| `ivvlev/CarRadio` | https://github.com/ivvlev/CarRadio (`twutil/.../TWClient.java`, README) | Explicit dependence on `android.tw.john.TWUtil`/`TWClient`, with fallback/emulation when unavailable. | Runtime probing + fallback adapter is viable pattern. | Exact equivalence to TS18/TW service contract. | Strong architecture inspiration: optional hardware bridge behind capability checks. |
| `d51x/KaierUtils` | https://github.com/d51x/KaierUtils (`TWUtilEx.java`) | TWUtil handlers for keypress, sleep/wake, volume, audio-focus-tag style channels and broadcasts. | TW transport may carry events relevant to media integration (focus/keys/sleep). | Safe portable constants for TS18 without validation. | Model as optional advanced adapter module with strict runtime gating. |
| `Planqton/fytFM` | https://github.com/Planqton/fytFM (`TWUtilHelper.java` via code search) | Additional community TWUtil wrapper patterns exist. | Shared head-unit adaptation motifs. | TS18-specific behavior. | Add to background corpus only. |

## 5) Multi-hardware HU projects

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| `kapi21/OpenRadioFM` | https://github.com/kapi21/OpenRadioFM (`FYTOemEngine.java`, README) | Hardware-engine interface with per-platform implementations (K706, MT8163, QS6, FYT/OEM intents). | Adapter/facade architecture scales across heterogeneous HU firmware. | Direct TW/TWTHEME compatibility for Auxio-TS. | Use as blueprint for TS18 adapter facade and contract modules. |
| `dipcore/Radio-MST768` and similar | surfaced in code search | Legacy head-unit projects often bind private OEM channels. | Some contracts may require non-standard paths. | Modern TS18 behavior and compatibility guarantees. | Treat as evidence-only; avoid direct code transfer. |

## 6) Android official docs (authoritative for standard behavior)

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| Media3 overview | https://developer.android.com/guide/topics/media/media3 | Official Media3 integration model. | Best-practice architecture for modern playback apps. | TW-private contract behavior. | Keep Auxio core standards-compliant before TS18 adapters. |
| Media app for cars | https://developer.android.com/training/cars/media | Android Auto/Automotive media app expectations. | Media browser/session behavior for car clients. | OEM launcher-private behaviors. | Validate baseline compatibility using standard APIs first. |
| Foreground services | https://developer.android.com/guide/components/foreground-services | Foreground-service requirements and restrictions. | Required service/notification handling constraints. | HU vendor background policies. | Preserve robust service + notification lifecycle. |
| Audio focus | https://developer.android.com/media/optimize/audio-focus | Platform audio focus model. | Correct interruption/ducking strategies. | Vendor-specific `com.tw.service` arbitration internals. | Compare stock vs Auxio-TS focus traces before adding adapters. |

## 7) Upstream Auxio source in this fork

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Influence on Auxio-TS |
|---|---|---|---|---|---|
| Playback service root | `app/src/main/java/org/oxycblt/auxio/AuxioService.kt` | MediaBrowserServiceCompat + foreground orchestration is already structured. | Existing seam for adapter insertion around service boundaries. | TS18 launcher compatibility itself. | Preserve service architecture; extend via isolated facade. |
| Media session + notification | `.../playback/service/MediaSessionHolder.kt` | MediaSessionCompat + notification action lifecycle already implemented. | Add comparative observability without rewriting core logic. | Whether TW private actions are needed. | Keep core untouched; add optional adapter observer hooks only. |
| Playback/media-button flow | `.../PlaybackServiceFragment.kt`, `.../MediaButtonReceiver.kt` | Existing media-button handling and deferred playback flow. | Could host optional TW bridge invocation point. | Vendor key-routing path outcomes. | Validate standard dispatch on TS18 before custom key adapter. |
| Music browser/indexing | `.../music/service/MusicServiceFragment.kt`, `.../MusicBrowser.kt` | Existing media library session exposure for clients. | Good base for Android Auto behavior checks. | Stock launcher widget-specific behavior. | Keep as-is; add evidence-driven comparator tooling. |
