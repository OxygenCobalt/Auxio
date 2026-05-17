# TS18 Integration Architecture

## Design intent
Preserve upstream Auxio playback/library behavior and add TS18 capabilities through a strict adapter facade.

## Target layer shape

```text
Auxio core / upstream-preserved player
        |
Android media integration layer
        |
TS18 adapter facade
        |
optional contract modules:
  - launcher/media-widget adapter
  - TW broadcast adapter
  - TWUtil/TWClient bridge, only if needed
  - ZLink/TLink/media-projection validation hooks
  - TWTHEME/resource compatibility layer
  - stock com.tw.music comparison harness
```

## Concrete mapping to current Auxio-TS code

| Layer | Current anchor points | Keep unchanged vs evolve |
|---|---|---|
| Auxio core | playback state manager, queue/indexing, library domain (`playback/state`, `music/*`) | Keep unchanged unless non-TS18 regression fix is needed |
| Android media integration layer | `AuxioService`, `PlaybackServiceFragment`, `MediaSessionHolder`, `MusicServiceFragment` | Validate and harden behavior first; avoid TS18 conditionals here |
| TS18 adapter facade | new isolated package (proposed: `app/src/main/java/org/oxycblt/auxio/integration/ts18/...`) | Introduce interfaces + no-op implementation behind feature flags |
| Optional contract modules | launcher/TW/ZLink/TWTHEME adapters | Add one module per PR after evidence proves a gap |

## Adapter facade contracts (proposed)
- `Ts18EnvironmentProbe`: detects TS18/TW context from package/property/runtime cues.
- `Ts18ContractRegistry`: records which optional contracts are enabled and why.
- `Ts18LauncherWidgetAdapter`: optional metadata/broadcast path for launcher/home card.
- `Ts18TwBridgeAdapter`: optional TW broadcast/TWUtil/TWClient integration bridge.
- `Ts18LinkCoexistenceAdapter`: diagnostic hooks for ZLink/TLink interactions.
- `Ts18ThemeAdapter`: non-invasive resource/theme compatibility observability.
- `Ts18StockComparator`: tooling hooks to compare stock `com.tw.music` vs Auxio-TS.

## Compile-time optional vs runtime-detected

### Compile-time optional (prefer)
- TS18 adapter source set/module itself (can be no-op in non-TS builds).
- advanced diagnostics helpers and evidence export helpers.

### Runtime-detected (mandatory)
- Presence of TW packages/services/classes.
- Presence of launcher/phone-link packages.
- Device/property heuristics indicating TS18-like environment.
- Whether each contract module should remain disabled by default.

## Guardrails to avoid core contamination
1. No direct `com.tw.*` references inside core playback/indexing classes.
2. No TS18 branching in queue/state machine logic.
3. Adapter APIs consume stable internal abstractions (session events, metadata snapshots), not deep internals.
4. Every adapter module defaults to no-op and is explicitly feature-gated.

## Testability split

### Off-device / CI-testable
- Contract registry and feature-gating logic.
- Adapter wiring/no-op behavior.
- Evidence serialization and diagnostics formatting.
- Android-native service/session unit tests.

### Requires TS18 runtime validation
- launcher media-widget behavior,
- steering-wheel/media-key routing,
- TW service/focus interactions,
- ZLink/TLink coexistence,
- TWTHEME-visible UI compatibility,
- sleep/resume and navigation-mixing behavior.

## Stop conditions
Stop TS18 adapter coding and escalate when:
- standard Android media path has not been conclusively validated yet,
- a proposed contract requires privileged/system permissions,
- evidence is inferred-only with no reproducible capture,
- change would force broad rewrite of Auxio core.
