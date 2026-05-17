# TS18 Requirements for Auxio-TS

## Scope
This document defines product requirements for evolving Auxio into a TS18-friendly app without sacrificing upstream maintainability.

## A) Android-native requirements

### Hard requirements
- Correct Media3 `MediaSession` lifecycle and transport command handling.
- Foreground playback service + media notification controls (play/pause/next/prev/seek where supported).
- Correct audio-focus request/abandon behaviour and noisy-device handling.
- Reliable local-library indexing/playback from accessible storage.
- FLAC playback validation on Android 10 / API 29 device target.

### Preferred requirements
- Media browser/library integration for Android Auto clients where baseline supports it.
- Stable resume behaviour after app backgrounding, screen off/on, and process reclaim.

### Experimental requirements
- Alternate notification/channel tuning for automotive UX.

## B) TS18-native requirements

### Hard requirements
- Reproducible baseline comparison: stock `com.tw.music` vs Auxio-TS.
- Evidence-backed determination of whether launcher/widget/media-key behaviour works through standard Android APIs.
- TS18 adapter architecture isolated from Auxio core.

### Preferred requirements
- Steering-wheel/media-key compatibility without privileged APIs.
- Coexistence with ZLink/TLink flows when active.
- Sleep/resume behaviour characterization (ACC-like conditions where safely testable).

### Experimental requirements
- TW/TWTHEME compatibility adapters (broadcasts, metadata projection, theme coupling) only after proof.

## Non-goals
- Immediate package replacement of `com.tw.music`.
- Privileged/system UID emulation.
- Blind porting of decompiled TW app logic.
- Large UI rewrites before media integration proof.

## Assumptions
- Target baseline is TS18 Android 10 variant represented by `diagnostics/redacted/ts18_device_profile.json`.
- TS18 variants may differ; findings are not universally portable.
- Standard Android media interfaces should be attempted first.

## Open questions (requires TS18 runtime evidence)
1. Does TS18 launcher/home widget consume standard MediaSession metadata from third-party apps?
2. Are steering-wheel keys routed via standard media-button dispatch for third-party apps?
3. Is any private `com.tw.*` contract required for launcher integration?
4. Does ZLink/TLink consume third-party MediaSession metadata reliably?
5. Are there TS18-specific audio-priority policies that require adapter behaviour?
