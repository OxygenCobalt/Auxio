# TS18 Integration Architecture

## Preferred architecture (source-led)
```text
Auxio core
  -> Android-standard media layer
       -> MediaSession / MediaLibrary / notification / media buttons / audio focus
  -> car/head-unit UX layer
       -> large touch targets, landscape assumptions, steering-wheel compatibility via standard APIs
  -> TS18 validation profile
       -> expected behaviors + manual acceptance checks
  -> optional explicit compatibility features
       -> only when backed by official docs + public precedent + TS18 validation
```

## Architectural commitments
- Auxio playback/library core remains upstream-first and vendor-agnostic.
- Android media contracts are the implementation baseline.
- TS18 work is feature-driven, not probe-framework-driven.
- Validation evidence gates feature promotion.

### TS18/TW/TWTHEME claim labels for this architecture
- Confidence: **Observed** for Android-standard baseline claims; **Requires TS18 validation** for device-specific acceptance claims.
- Porting decision: **Directly reusable requirement** for Android-standard behavior; **Requires TS18 runtime validation** for TS18-specific acceptance-only expectations.

## Explicit non-targets in product code
- No in-app TS18 probe framework.
  Confidence: **Inferred**; Porting decision: **Should be explicitly avoided**.
- No TWUtil/TWClient reflective scanner modules.
  Confidence: **Observed**; Porting decision: **Unsafe to port**.
- No vendor-service binder scaffolding without a concrete feature.
  Confidence: **Inferred**; Porting decision: **Unsafe to port**.
- No package impersonation or privileged UID assumptions.
  Confidence: **Observed**; Porting decision: **Should be explicitly avoided**.

## Implementation flow
1. Harden Android-standard behaviors (session, library browse, notification, buttons, focus).
2. Validate on TS18 with runbook scenarios.
3. If a repeatable gap remains, draft one explicit compatibility feature PR.
4. Land only the minimal feature needed for that validated gap.
