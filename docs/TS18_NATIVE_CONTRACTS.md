# TS18 Native Contracts Catalog

## Classification legend
- **Observed/Proven:** present in repository diagnostics or directly captured runtime evidence.
- **Likely:** strong inference from observed data, still not fully proven.
- **Weak hypothesis:** plausible but low-confidence.
- **Requires TS18 runtime validation:** must be tested on hardware before implementation commitments.

## 1) Observed / Proven
- `com.tw.music` installed as system app path in diagnostics context.
- TW package ecosystem present, including: `com.tw.service`, `com.tw.service.xt`, `com.tw.radio`, `com.tw.eq`, `com.tw.bt`, `com.tw.video`, `com.tw.reverse`, `com.tw.net`, `com.tw.core`, `com.tw.coreservice`.
- TWTHEME asset path includes `Sub/MusicTheme.apk`.
- `persist.phone_connect_app=com.zjinnova.zlink` observed.
- Captured audio diagnostics show `com.tw.service` focus/volume involvement.

## 2) Likely contracts
- Launcher/home media surfaces may have special behaviour for stock `com.tw.music`.
- `com.tw.service` mediates portions of audio policy and/or source switching.
- ZLink presence can affect media metadata/control expectations.

## 3) Weak hypotheses
- Private `com.tw.music.action.*` broadcasts are required for third-party launcher metadata.
- Matching package name alone is sufficient for stock-equivalent integration.
- TWTHEME resources directly enforce package-level coupling.

## 4) Contracts requiring TS18 runtime validation

| Contract candidate | Current status | Required proof |
|---|---|---|
| Standard `MediaSession` enough for launcher metadata | Unproven | Stock vs third-party vs Auxio-TS comparison with captures |
| Steering-wheel keys route via standard media-button flow | Unproven | Input + media_session traces during key presses |
| `com.tw.service` API/broadcast contract needed | Unproven | Logcat/static references and before/after behaviour |
| Private `com.tw.music` broadcasts needed | Unproven | Static references and runtime observation |
| ZLink/TLink metadata interop for Auxio-TS | Unproven | Comparative playback session evidence |
| Package identity replacement required | High-risk, unproven | Demonstrate standard APIs fail and replacement is feasible/safe |

## 5) Integration policy
Do not implement any TW-private adapter without at least one of:
1. explicit runtime evidence,
2. static reference evidence tied to behaviour,
3. reproducible failure of standard Android path.
