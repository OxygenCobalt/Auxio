# Auxio-TS Development Roadmap (pivot)

One primary variable per PR.

TS18/TW/TWTHEME parity is the product target. Android-standard (Tier 1) implementation is the first phase; TS18/TWTHEME hardware validation (Tier 2) and evidence-gated native investigation (Tier 3/4) follow. Native/private contracts are NOT permanently out of scope — they require the formal gap-and-promotion process (see [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)).

| Phase | Goal | Primary output |
|---|---|---|
| 5A | Source-led integration strategy correction | `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` + aligned docs |
| 5B | Android-standard MediaSession/notification hardening (Tier 1) | Session publication + transport-control robustness tasks |
| 5C | MediaLibraryService / Android Auto browsing hardening (Tier 1) | Browse tree/controller compatibility fixes |
| 5D | Media buttons / steering-wheel via standard APIs (Tier 1) | Hardware key acceptance pass and targeted standard-API fixes |
| 5E | Audio focus / navigation-mixing hardening (Tier 1) | Focus-policy and coexistence behavior hardening |
| 5F | Head-unit UI/UX layout hardening (Tier 1) | Landscape/touch-target/readability improvements |

| 5G | TS18 validation evidence import (Tier 2) | Scenario evidence packs + prioritized gap list |
| 6A | Populate TS18 Native Parity Gap Matrix from hardware evidence (Tier 2) | Updated `TS18_NATIVE_PARITY_GAP_MATRIX.md` with observed parity gaps |
| 6B | Investigate specific native/private gaps only where Tier 1 surfaces fail (Tier 3) | External investigation scripts; non-production branch experiments; formal gap documentation |
| 6C | Promote safe, isolated, non-impersonating native integrations (Tier 4) | One feature per PR via explicit human-approved design PR; meets all Tier 4 eligibility criteria |

Latest app/runtime hardening note: `docs/TS18_TIER1_HARDENING_NOTES.md` (canonical route policy consolidation and queue-routing consistency checks).

## Roadmap principles

- Phase 5A–5G covers Tier 1 Android-standard implementation surfaces. Completing this phase does **not** mean the TS18 goal is complete.
- Phase 5G/6A validates whether Tier 1 achieves TS18 parity on real hardware. Hardware validation is required before declaring parity.
- Phase 6B/6C are only entered if Tier 2 validation confirms a specific parity gap that Tier 1 cannot address.
- Each Tier 4 feature enters production only via explicit human-approved design PR meeting all 8 eligibility criteria.

## Guardrails
- No in-app runtime TW probe frameworks.
- No vendor-private bindings without explicit feature design.
- No package impersonation or privileged UID assumptions.
- No smali copying or com.tw.* imports in product code.
- Native/private contracts are NOT permanently out of scope — they require Tier 2 gap evidence and Tier 4 human-approved design PR.
  Evidence confidence: Inferred | Porting decision: Requires TS18 runtime validation

## Current execution note (Head-Unit Experience Mode)
Latest Tier 1 scope now includes a policy-driven head-unit dashboard model, queue empty-state recovery, playback large-control ergonomics, and routing/shortcut alignment updates. This remains pre-hardware-validation work and feeds Phase 5G/6A validation.
