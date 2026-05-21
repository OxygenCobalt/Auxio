# TS18 Native/Private Contract Inventory (Evidence-only)

| Contract/surface | Known names | Evidence source | Privilege needed | Package identity dependency | Safe for 3P app | External testable | Isolated experiment possible | Production eligibility | Risks | Fallback | Status |
|---|---|---|---|---|---|---|---|---|---|---|---|
| TWUtil/TWClient | `android.tw.john.TWUtil`, `TWClient` | Source map + t-music evidence | Often vendor/system coupled | Likely yes in stock flows | No (unsafe baseline) | Partially | Yes (external only) | Future design required | API instability, hidden contracts | Stay on Tier 1 APIs | Unsafe to port |
| Stock command actions | `com.tw.music.action.*` | t-music snapshot docs | Unknown | Yes (`com.tw.music`) | No default | Yes (external validation) | Yes (Tier 3 only) | Rejected for production by default | Impersonation/coupling risk | MediaSession/media key APIs | Evidence only |
| Vendor service surfaces | `com.tw.service`, `com.tw.service.xt` | diagnostics + snapshot | likely privileged | likely coupled | No default | external observation only | Maybe | Future design required | binder/security/compat risk | no binder integration | Evidence only |
| TWTHEME/iLauncher widget behavior | private launcher/theme internals | ecosystem sources | Unknown | Unknown | Unknown | Yes (behavioral) | Yes (external) | Requires evidence + design | theme fragmentation | standard widget APIs | Requires TS18 validation |
