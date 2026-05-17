# TS18 Integration Architecture

## Required shape
```text
Auxio upstream core
        |
Android media integration layer
        |
Auxio-TS adapter/facade layer
        |
optional TS18-specific modules:
  - launcher/media-widget adapter
  - TW broadcast adapter
  - TW service adapter
  - TWTHEME/resource compatibility notes
  - ZLink/TLink validation hooks
  - stock com.tw.music comparison harness
```

## What remains upstream/pure Auxio
- Playback engine lifecycle and queue/state logic.
- Library scanning/indexing and baseline local playback behavior.
- Android-native MediaSession/notification/audio-focus path.

## What becomes Auxio-TS
- Runtime TS18 environment probing.
- Contract registry (which TS18 modules are enabled and why).
- Optional adapter modules above, all default-off.
- Comparator/evidence capture hooks for stock-vs-Auxio behavior.

## Isolation rules
1. No direct `com.tw.*` references in core playback/library classes.
2. TS18 logic enters via adapter interfaces and event snapshots.
3. Each optional module ships as no-op when disabled/unavailable.
4. No module assumes privileged UID/package identity.

## Compile-time optional vs runtime-detected

### Compile-time optional
- Adapter packages/modules and diagnostics helpers.
- Comparator utilities and validation scripts.

### Runtime-detected
- TW classes/services/package presence (`TWUtil`, `TWClient`, `com.tw.service.xt`, launcher packages).
- ZLink/TLink active stack conditions.
- Whether module activation improves observed parity without regressions.

## Evidence from `t-music` snapshot applied here
- Stock uses command broadcasts and vendor service/AIDL surfaces.
- Stock has widget/theme integration points and privileged identity.
- Therefore Auxio-TS must treat TW contracts as optional, test-gated modules, not core rewrites.

## Test split
- **Off-device:** adapter wiring, contract registry decisions, no-op safety, docs/tooling.
- **Requires TS18 runtime:** launcher/widget behavior, TW command/service effects, ZLink/TLink coexistence, navigation-mixing, sleep/resume.
