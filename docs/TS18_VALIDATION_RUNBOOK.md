# TS18 Validation Runbook (acceptance testing)

## Scope
This runbook validates product behavior on TS18 hardware. It does not depend on in-app probe modules.

## Acceptance scenarios

### TS18-ACC-001: MediaSession visibility
- Verify active session owner/state/actions/metadata via `dumpsys media_session` while Auxio plays.

### TS18-ACC-002: MediaLibrary/Android Auto browsing
- Validate browse tree visibility and playable items from external controller surface.

### TS18-ACC-003: Notification transport controls
- Validate play/pause/next/prev from notification repeatedly; record failures/latency.

### TS18-ACC-004: Media buttons / steering wheel
- Trigger hardware keys and confirm standard media key routing reaches Auxio reliably.

### TS18-ACC-005: ZLink/TLink coexistence
- Compare focus/session/metadata visibility when projection is idle vs active.

### TS18-ACC-006: Launcher widget behavior
- Compare metadata/control visibility for stock launcher and available alternatives.

### TS18-ACC-007: Audio focus + navigation mixing
- Trigger navigation prompts during playback; verify duck/pause/recover behavior.

### TS18-ACC-008: Sleep/resume continuity
- Validate session/focus/playback recovery after screen off/on and HU sleep-resume cycle.

### TS18-ACC-009: FLAC/local-library matrix
- Validate scan/index/play/seek/resume for 16/44.1, 16/48, 24/48 samples.

### TS18-ACC-010: TWTHEME visual/layout behavior
- Capture visual diffs across theme/launcher variants; classify as UI variance vs functional regression.

## Evidence format
Per scenario include:
- device fingerprint + firmware/theme variant labels
- Auxio build SHA/version
- exact commands run
- observed result
- confidence label + porting decision label
- unresolved risk and next action
