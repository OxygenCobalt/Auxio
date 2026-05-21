# `t-music` vs `Auxio-TS` (snapshot-driven comparison)

## Scope
This comparison is based on `docs/evidence/t-music-snapshot/` (curated private snapshot) and current Auxio-TS docs/source.

> **Parity gap framing:** The differences documented here define the parity gap matrix between stock TS18/t-music behaviour and Auxio-TS Tier 1 Android-standard implementation. These gaps are the input to [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md). Gaps confirmed by Tier 2 on-device validation may trigger Tier 3 investigation or Tier 4 production native integration — see [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy).

## 1) Architectural differences

| Dimension | `t-music` snapshot evidence | Auxio-TS direction | Classification |
|---|---|---|---|
| Core implementation base | Decompiled/smali-first stock `com.tw.music` workspace. | Kotlin/AndroidX upstream Auxio fork. | obsolete due to Auxio architecture (for direct code port) |
| Package/UID model | `com.tw.music` + `android.uid.system` in stock manifest. | Keep Auxio package identity and non-privileged install path. | should be explicitly avoided |
| Control surface | Broadcast actions `com.tw.music.action.cmd|prev|next|pp` heavily used. | Keep as comparator evidence; implement only if standard path proven insufficient. | requires TS18 runtime validation |
| Vendor service coupling | `com.tw.service.xt` + AIDL tokens in stock. | Optional, isolated TS18 service adapter only after proof of necessity. | useful as evidence only |
| Widget/theme coupling | `MusicWidgetProvider`, TW theme/TWTHEME paths in stock. | Validate launcher/TWTHEME behavior first; optional adapter if required. | reusable validation idea |

## 2) Findings promoted into Auxio-TS requirements/plans

1. Build a stock-vs-Auxio comparator workflow before TW-private coding.
2. Track package/signature/UID constraints as risk, not as implementation target.
3. Keep TW actions/services/theme references in a contract catalog with validation gates.
4. Use stock manual validation scenarios (keys/widget/navigation mixing/sleep-resume) as runbook inputs.
5. Keep one-variable-per-PR strategy for TS18 experiments.

## 3) Findings intentionally not promoted

| Snapshot finding | Why not promoted as implementation |
|---|---|
| `android.uid.system` / shared UID model | Conflicts with Auxio-TS maintainable third-party app posture; privileged path is high risk. |
| Package identity `com.tw.music` lock-in | Out of scope and unsafe without explicit human-approved migration strategy. |
| Smali/source-shim implementation details from stock app | Not compatible with preserving upstream Auxio architecture. |
| Vendor-token preservation rules from reverse-engineering workflow | Useful for evidence interpretation, not direct Auxio coding rules. |

## 5) Gap-to-tier mapping

Each gap in this comparison document maps to a tier in the TS18 Native Parity Strategy. Gaps in this table feed into [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md).

| t-music behavior | Current gap vs Auxio-TS | Porting decision | Native/private investigation tier |
|---|---|---|---|
| `com.tw.music.action.*` broadcast actions | Auxio does not send/receive these; uses standard MediaSession | Requires TS18 runtime validation | Tier 3 maybe — only if Tier 2 validation proves standard MediaSession routing is insufficient |
| `com.tw.service.xt` AIDL coupling | Not implemented; Auxio uses standard audio focus | Useful as evidence only | Tier 3 maybe — only if Tier 2 confirms TW service intercepts standard audio focus |
| `com.tw.music` package identity | Explicitly not implemented; Auxio keeps its own package | Should be explicitly avoided | Never — package impersonation is prohibited |
| `android.uid.system` / shared UID | Not required; Auxio is a standard third-party app | Should be explicitly avoided | Never — platform-signature dependency is prohibited |
| `MusicTheme.apk` coupling | Not implemented; Auxio uses standard UI | Requires TS18 runtime validation | Tier 3 maybe — only if Tier 2 shows standard AppWidget/UI is invisible in TWTHEME |
| Manual ADB parity steps | Reusable as Tier 2 acceptance scenarios | Reusable validation idea | N/A — used as Tier 2 runbook scenarios |

## 4) Reclassification matrix for future agents
|---|---|
| Stock contract strings/actions/services | useful as evidence only + requires TS18 runtime validation |
| Manual ADB parity steps | reusable validation idea |
| Privileged/package assumptions | unsafe to port + should be explicitly avoided |
| Adapter/facade architecture lessons | directly reusable requirement |
| Decompiled implementation snippets | obsolete due to Auxio architecture |
