# Auxio-TS Development Roadmap (pivot)

One primary variable per PR.

| Phase | Goal | Primary output |
|---|---|---|
| 5A | Source-led integration strategy correction | `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` + aligned docs |
| 5B | Android-standard MediaSession/notification hardening | Session publication + transport-control robustness tasks |
| 5C | MediaLibraryService / Android Auto browsing hardening | Browse tree/controller compatibility fixes |
| 5D | Media buttons / steering-wheel via standard APIs | Hardware key acceptance pass and targeted standard-API fixes |
| 5E | Audio focus / navigation-mixing hardening | Focus-policy and coexistence behavior hardening |
| 5F | Head-unit UI/UX layout hardening | Landscape/touch-target/readability improvements |
| 5G | TS18 validation evidence import | Scenario evidence packs + prioritized gap list |
| 6+ | Explicit compatibility features only when justified | One feature per PR with source + validation justification |

## Guardrails
- No in-app runtime TW probe frameworks.
- No vendor-private bindings without explicit feature design.
- No package impersonation or privileged UID assumptions.

- TS18-STD-010..017 track launcher shortcut + widget routing/readability validation (see TS18_VALIDATION_RUNBOOK).

- Track Tier 1 vs TS18 runtime parity gaps in `TS18_NATIVE_PARITY_GAP_MATRIX.md` before any native-contract promotion.
