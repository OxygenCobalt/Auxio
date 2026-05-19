# TS18 Native Contracts Catalog (source-led classification)

## Classification labels
- **implementation target**: implement in product code now.
- **validation expectation**: verify on device, but no product-code implementation yet.
- **reference-only**: useful context/evidence, not an implementation requirement.
- **do not implement**: explicitly out of scope.
- **requires explicit future design**: possible future feature only after evidence + design PR.

## Contract table

| Contract | Evidence basis | Classification | Current policy |
|---|---|---|---|
| Android MediaSession/Media3 session behavior | Android official media docs + existing Auxio architecture | implementation target | Continue hardening standards-based behavior. |
| MediaLibraryService browse/controller behavior | Android official media docs + Android Auto path | implementation target | Prioritize browse and controller compatibility. |
| Notification transport controls | Android official media docs | implementation target | Keep as primary control surface. |
| Standard media buttons / steering-wheel via controller APIs | Android docs + public HU app behavior patterns | implementation target | Validate with hardware keys; implement only standard API improvements. |
| Audio focus and duck/pause/mix behavior | Android docs + AAOS focus docs | implementation target | Harden focus policy + validate nav coexistence. |
| `com.tw.music.action.*` | t-music evidence + ecosystem references | validation expectation | Record and test as compatibility clue; no direct implementation by default. |
| `com.tw.service.xt` | diagnostics + t-music evidence | reference-only | No binding/integration in product code now. |
| `android.tw.john.TWUtil` / `TWClient` | public repo evidence | do not implement | No in-app reflection/probe modules. |
| `android.uid.system` / shared UID model | t-music manifest evidence | do not implement | Explicitly forbidden in Auxio-TS product code. |
| `com.tw.music` package identity | diagnostics + t-music manifest | do not implement | No package impersonation. |
| `MusicTheme.apk` / TWTHEME coupling | diagnostics + evidence docs | validation expectation | Validate visual behavior externally; no direct coupling code yet. |
| `com.zjinnova.zlink` coexistence | diagnostics + runbook scenarios | validation expectation | Validate session/focus coexistence via acceptance tests. |
| Vendor service or TW-private integration | mixed evidence | requires explicit future design | Only after repeatable gap + feature design doc. |
