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
| `android.tw.john.TWUtil` / `TWClient` | public repo evidence (CarRadio, dvd-bt) | Observed | Unsafe to port | No in-app reflection/probe modules. |
| `android.uid.system` / shared UID model | t-music manifest evidence | Observed | Should be explicitly avoided | Explicitly forbidden in Auxio-TS product code. |
| `com.tw.music` package identity | diagnostics + t-music manifest | Observed | Should be explicitly avoided | No package impersonation. |
| `MusicTheme.apk` | diagnostics (`/system/etc/theme/default/Sub/MusicTheme.apk`) | Observed | Requires TS18 runtime validation | Validate visual behavior externally; no direct coupling code yet. |
| `LauncherTheme.apk` | diagnostics (`/system/etc/theme/default/Launcher/LauncherTheme.apk`) | Observed | Requires TS18 runtime validation | Validate launcher theme behavior externally; no coupling in product code. |
| `theme_config.json` / TWTHEME config | diagnostics (`/system/etc/theme/theme_config.json`) | Observed | Requires TS18 runtime validation | Reference-only; captures theme routing logic on device. |
| TWTHEME private resource paths (`/system/etc/theme/`) | diagnostics + DoFun/iLauncher ecosystem | Observed | Requires TS18 runtime validation | Validate visual behavior externally; no path assumptions in product code. |
| `com.zjinnova.zlink` coexistence | diagnostics + runbook scenarios | Requires TS18 validation | Requires TS18 runtime validation | Validate session/focus coexistence via acceptance tests. |
| `com.tw.carchoose` / carchoose package | diagnostics ecosystem; ZLink/TLink interaction | Hypothesis | Requires TS18 runtime validation | Record as ecosystem term; validate if and when observed on device. |
| `com.zjinnova.android.zlink.features.broadcast.MediaButtonReceiver` | diagnostics (restored media button receiver) | Observed | Requires TS18 runtime validation | Known to be active as media button receiver at capture time; validate media key routing via standard APIs first. |
| iLauncher (`com.cml.ilauncher` or similar) | iLauncher website + XDA iLauncher TS18 fix thread | Hypothesis | Requires TS18 runtime validation | Launcher replacement that may affect widget/image display; validate metadata quality first. |
| `com.tw.radio` / `com.tw.bt` / `com.tw.eq` / `com.tw.video` | diagnostics (system priv-app packages) | Observed | Useful as evidence only | System app co-tenancy context; no direct coupling to these packages. |
| Vendor service or TW-private integration | mixed evidence | Inferred | Unsafe to port | Only after repeatable gap + feature design doc. |
