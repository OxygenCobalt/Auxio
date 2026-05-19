# TS18 Native Contracts Catalog (source-led classification)

Use canonical labels from `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.

## Contract table

| Contract | Evidence basis | Confidence | Porting decision | Current policy |
|---|---|---|---|---|
| Android MediaSession/Media3 session behavior | Android official media docs + existing Auxio architecture | Observed | Directly reusable requirement | Continue hardening standards-based behavior. |
| MediaLibraryService browse/controller behavior | Android official media docs + Android Auto path | Observed | Directly reusable requirement | Prioritize browse and controller compatibility. |
| Notification transport controls | Android official media docs | Observed | Directly reusable requirement | Keep as primary control surface. |
| Standard media buttons / steering-wheel via controller APIs | Android docs + public HU app behavior patterns | Observed | Directly reusable requirement | Validate with hardware keys; implement only standard API improvements. |
| Audio focus and duck/pause/mix behavior | Android docs + AAOS focus docs | Observed | Directly reusable requirement | Harden focus policy + validate nav coexistence. |
| `com.tw.music.action.*` | t-music evidence + ecosystem references | Observed | Requires TS18 runtime validation | Record and test as compatibility clue; no direct implementation by default. |
| `com.tw.service.xt` | diagnostics + t-music evidence | Observed | Useful as evidence only | No binding/integration in product code now. |
| `android.tw.john.TWUtil` / `TWClient` | public repo evidence | Observed | Unsafe to port | No in-app reflection/probe modules. |
| `android.uid.system` / shared UID model | t-music manifest evidence | Observed | Should be explicitly avoided | Explicitly forbidden in Auxio-TS product code. |
| `com.tw.music` package identity | diagnostics + t-music manifest | Observed | Should be explicitly avoided | No package impersonation. |
| `MusicTheme.apk` / TWTHEME coupling | diagnostics + evidence docs | Requires TS18 validation | Requires TS18 runtime validation | Validate visual behavior externally; no direct coupling code yet. |
| `com.zjinnova.zlink` coexistence | diagnostics + runbook scenarios | Requires TS18 validation | Requires TS18 runtime validation | Validate session/focus coexistence via acceptance tests. |
| Vendor service or TW-private integration | mixed evidence | Inferred | Unsafe to port | Only after repeatable gap + feature design doc. |
