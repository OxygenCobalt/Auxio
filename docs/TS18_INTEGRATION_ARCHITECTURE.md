# TS18 Integration Architecture

## Design goal
Keep upstream Auxio core mostly platform-neutral. Introduce TS18 behaviour via adapter/facade seams, not core scattering.

## Layer model

1. **Upstream Auxio Core (preserve)**
   - Library/indexing/metadata domain logic.
   - Playback orchestration and queue semantics.
   - Existing UI and feature behaviour unless a validated TS18 requirement says otherwise.

2. **Android Media Integration Layer (harden first)**
   - Media3 player/session/service wiring.
   - Notification and media-button routing.
   - Audio-focus lifecycle.
   - Android Auto/media-browser compliance where supported.

3. **TS18 Adapter Layer (optional, evidence-gated)**
   - Runtime TS18 environment detector.
   - TS18 diagnostics reporter.
   - Optional TW compatibility adapters (broadcast/interface shims) behind feature flags.

4. **Validation/Evidence Layer**
   - Scripts, runbooks, and redacted output bundles.
   - Stock-vs-Auxio-TS comparison artifacts.

## Boundary rules
- Core playback engine and library model should remain TS18-agnostic.
- TS18-specific code should live in dedicated package/module paths (for example `integration/ts18/*`).
- Adapter interfaces should depend on stable internal abstractions, not deep internal classes.

## Candidate adapter interfaces
- `Ts18EnvironmentProbe` (detects likely TW/TS18 context).
- `Ts18CompatibilityReporter` (emits evidence logs/diagnostic snapshots).
- `Ts18LauncherBridge` (no-op by default; optional proven metadata/broadcast bridge).
- `Ts18InputBridge` (optional handling/telemetry for hardware key edge cases).

## Validation boundaries
- **Must pass before TS18 adapter work:** standard MediaSession/notification/audio-focus behaviour.
- **Must prove before enabling TW bridge behaviour:** concrete observed contract from diagnostics/logcat/static references.

## What may become TS18-specific
- Runtime heuristics for environment detection.
- Compatibility bridge implementations for proven private contracts.
- TS18-specific UX tuning that does not break default Android behaviour.

## What should remain platform-neutral
- Playback queue/state machine.
- Media source parsing and metadata model.
- Library scanning/indexing semantics.
- Format capability logic (except device-specific bug workarounds with clear gating).
