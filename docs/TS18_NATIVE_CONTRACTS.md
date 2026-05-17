# TS18 Native Contracts Catalog

## Classification legend
### Confidence
- Observed
- Inferred
- Hypothesis
- Requires TS18 validation
- Unsupported

### Porting decision
- directly reusable requirement
- reusable validation idea
- useful as evidence only
- obsolete due to Auxio architecture
- requires TS18 runtime validation
- unsafe to port
- should be explicitly avoided

## Contract table

| Contract / identifier | Evidence source | Confidence | Porting decision | Expected behavior | Auxio-TS strategy | Validation method | Risk |
|---|---|---|---|---|---|---|---|
| `com.tw.music` package + `android.uid.system` | `docs/evidence/t-music-snapshot/app/apktool/AndroidManifest.xml` | Observed | unsafe to port; should be explicitly avoided | Stock app uses privileged identity. | Track as compatibility risk only. | Install/coexistence matrix on TS18. | High |
| `com.tw.music.action.cmd` | snapshot `vendor-hooks.txt`, `MusicService.smali`, `MusicWidgetProvider.smali` | Observed (stock) | useful as evidence only; requires TS18 runtime validation | Command ingress path for stock service/widget/receiver. | Optional TW broadcast adapter only if standard media path fails. | A/B broadcast tests (stock vs Auxio) on TS18. | High |
| `com.tw.music.action.prev` | same | Observed (stock) | useful as evidence only; requires TS18 runtime validation | Previous-track command path. | Same as above. | Same as above. | High |
| `com.tw.music.action.next` | same | Observed (stock) | useful as evidence only; requires TS18 runtime validation | Next-track command path. | Same as above. | Same as above. | High |
| `com.tw.music.action.pp` | same | Observed (stock) | useful as evidence only; requires TS18 runtime validation | Play/pause command path. | Same as above. | Same as above. | High |
| `com.tw.service` | `diagnostics/redacted/ts18_device_profile.json` | Observed | useful as evidence only; requires TS18 runtime validation | TW service present in target ecosystem. | Observe first; no direct integration by default. | `dumpsys audio`, process/session/focus capture during playback. | High |
| `com.tw.service.xt` + `CommandService.Bind` | snapshot `vendor-hooks.txt` + xtlibrary smali | Observed (stock corpus) | useful as evidence only; requires TS18 runtime validation | Vendor command service and AIDL stack likely mediate TW integrations. | Optional TW service adapter module, default off. | Binder/service availability + behavior probes on TS18. | High |
| `com.tw.radio.*` + `com.tw.radio.theme/state/av` | snapshot hook reports + xtlibrary smali; diagnostics package list | Observed | useful as evidence only; requires TS18 runtime validation | Radio handoff and state signaling in stock ecosystem. | Comparator behavior only unless concrete gap appears. | stock radio/music interaction tests. | Medium |
| `com.tw.eq.EQActivity` | snapshot `MusicActivity.smali` + diagnostics package list | Observed | useful as evidence only | Stock app launches OEM EQ app. | Validate coexistence; no hard dependency for Auxio core. | manual EQ coexistence scenario. | Medium |
| `android.tw.john.TWUtil` | snapshot xtlibrary smali + public repos | Observed (ecosystem) | useful as evidence only; requires TS18 runtime validation | Possible low-level TW bridge API. | Probe reflectively in optional adapter only. | class/binder availability and safe no-op fallback tests. | High |
| `android.tw.john.TWClient` | public repos + snapshot references | Inferred for target device | requires TS18 runtime validation | Command-client API may exist on some firmware. | Optional bridge only after proof. | runtime class/service probing. | High |
| `TWTHEME` / `MusicTheme.apk` | diagnostics + snapshot docs (`resource-compatibility-map.md`, `target-device-*`) | Observed | reusable validation idea; requires TS18 runtime validation | Theme resources may affect launcher/widget appearance. | Keep as compatibility note; avoid hard-coded coupling. | launcher/theme A/B captures stock vs Auxio. | High |
| ZLink (`com.zjinnova.zlink`) | diagnostics property/package evidence | Observed | directly reusable requirement (validation coverage) | Phone-link stack may compete for media state/control. | Add explicit coexistence tests in runbook. | ZLink/TLink active-vs-idle comparisons. | High |
| MediaSession/MediaSessionCompat publication in stock app | snapshot docs + `com/tw/music/media/*` smali | Observed (stock corpus) | useful as evidence only | Stock app exposes media session + notification bridge logic. | Use as parity reference only; keep Auxio Media3 path authoritative. | session/notification comparison captures. | Medium |

## Explicitly rejected/out-of-scope
1. Package impersonation (`com.tw.music`) as default strategy.
2. shared UID/system-signature dependency as prerequisite.
3. Copying stock smali/TW implementation into Auxio core.
