# TW Ecosystem Source Map (TS18/TW/TWTHEME)

## Evidence legend
- **Observed**: directly present in source or captured diagnostics.
- **Inferred**: plausible extrapolation, not directly proven.
- **Hypothesis**: weak/unverified.
- **Requires TS18 validation**: must be runtime-tested.

## Porting-decision legend
- directly reusable requirement
- reusable validation idea
- useful as evidence only
- obsolete due to Auxio architecture
- requires TS18 runtime validation
- unsafe to port
- should be explicitly avoided

## 1) `cbkii/t-music` snapshot (in-repo evidence)

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Auxio-TS influence | Porting decision |
|---|---|---|---|---|---|---|
| Snapshot root | `docs/evidence/t-music-snapshot/README.md` | Snapshot is curated evidence corpus, not implementation source. | Prior migration ideas may still be useful. | That old implementation strategy fits Auxio architecture. | Keep evidence-first planning. | useful as evidence only |
| Snapshot AGENTS | `docs/evidence/t-music-snapshot/AGENTS.md` | Stock app assumptions: `com.tw.music`, `android.uid.system`, TW AIDL boundaries. | Vendor contracts may matter for TS18 parity. | That Auxio should replicate package/UID model. | Explicitly separate stock constraints from Auxio goals. | useful as evidence only; unsafe to port (package/UID) |
| Stock manifest | `docs/evidence/t-music-snapshot/app/apktool/AndroidManifest.xml` | `package="com.tw.music"`, `android:sharedUserId="android.uid.system"`, widget receiver, service/activity surfaces. | Some launcher/theme behaviors may depend on stock identity. | Third-party-app requirements on target TS18. | Add package/signature risk ledger and coexistence checks. | useful as evidence only; should be explicitly avoided for direct adoption |
| Vendor hook report | `docs/evidence/t-music-snapshot/docs/reports/vendor-hooks.txt` | Explicit actions/services/properties (`com.tw.music.action.*`, `com.tw.service.xt`, `com.tw.radio`, `com.tw.eq`, `persist.tw.*`). | Which subset is mandatory for Auxio. | Runtime behavior on this user TS18 without tests. | Drive contract table + experiment queue. | directly reusable requirement (as investigation targets); requires TS18 runtime validation |
| Manual runbook | `docs/evidence/t-music-snapshot/docs/manual-validation-runbook.md` | Practical ADB parity/test procedure exists. | Some steps may need Auxio adaptation. | Direct applicability to API29 Auxio stack without adjustment. | Reuse as validation matrix template. | reusable validation idea |

## 2) TS18 diagnostics in this repo

| Source | URL/path | What it proves | What it suggests only | What it cannot prove | Auxio-TS influence |
|---|---|---|---|---|---|
| Device profile | `diagnostics/redacted/ts18_device_profile.json` | TS18-class package ecosystem includes `com.tw.music`, `com.tw.service`, `com.tw.service.xt`, `com.tw.eq`, `com.tw.radio`; TWTHEME `MusicTheme.apk`; ZLink property. | TW ecosystem may influence media focus/launcher behavior. | Exact third-party integration requirements. | Define runtime validation priorities and comparator scenarios. |

## 3) Android official docs

| Source | URL | What it proves | What it cannot prove | Auxio-TS influence |
|---|---|---|---|---|
| Media3/session/notification/audio focus/cars docs | `developer.android.com` media + cars references | Standard Android behavior contracts. | TW private service/launcher contracts. | Keep Android-native path as baseline and first implementation target. |

## 4) Public TW/Topway ecosystem projects

| Source | URL | What it proves | What it suggests only | What it cannot prove | Auxio-TS influence |
|---|---|---|---|---|---|
| `ivvlev/CarRadio` | https://github.com/ivvlev/CarRadio | TWUtil/TWClient usage with fallback patterns. | Optional runtime-probed bridge pattern is viable. | Exact TS18 compatibility for Auxio. | Architecture inspiration only. |
| `asb72/dvd-bt` | https://github.com/asb72/dvd-bt | `twUtil.java` command-channel style TW integration. | Some TW channels may map to key/media events. | Requirement for Auxio integration. | Evidence-only until runtime-proven need. |
| `d51x/KaierUtils` | https://github.com/d51x/KaierUtils | TW utility wrappers for HU events. | Sleep/volume/focus signals may be available. | Stable API compatibility on user TS18. | Candidate optional adapter experiment target. |
| `kapi21/OpenRadioFM` | https://github.com/kapi21/OpenRadioFM | Multi-hardware abstraction pattern. | Adapter-per-platform design scales for HU ecosystem. | TW/TWTHEME parity guarantees. | Direct architectural inspiration for Auxio-TS facade/modules. |

## 5) Upstream Auxio source (in this fork)

| Source | Path | What it proves | Auxio-TS influence |
|---|---|---|---|
| Playback + service + media session architecture | `app/src/main/java/org/oxycblt/auxio/...` | Existing maintainable Android-native architecture already exists. | Preserve and extend via isolated adapters, not wholesale rewrites. |
