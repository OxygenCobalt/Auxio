# `t-music` vs `Auxio-TS` (snapshot-driven comparison)

## Scope
This comparison is based on `docs/evidence/t-music-snapshot/` (curated private snapshot) and current Auxio-TS docs/source.

## 1) Architectural differences

| Dimension | `t-music` snapshot evidence | Auxio-TS direction | Classification |
|---|---|---|---|
| Core implementation base | Decompiled/smali-first stock `com.tw.music` workspace. | Kotlin/AndroidX upstream Auxio fork. | obsolete due to Auxio architecture (for direct code port) |
| Package/UID model | `com.tw.music` + `android.uid.system` in stock manifest. | Keep Auxio package identity and non-privileged install path. | unsafe to port / explicitly avoid |
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

## 4) Reclassification matrix for future agents

| Evidence item type | Auxio-TS default classification |
|---|---|
| Stock contract strings/actions/services | useful as evidence only + requires TS18 runtime validation |
| Manual ADB parity steps | reusable validation idea |
| Privileged/package assumptions | unsafe to port + should be explicitly avoided |
| Adapter/facade architecture lessons | directly reusable requirement |
| Decompiled implementation snippets | obsolete due to Auxio architecture |
