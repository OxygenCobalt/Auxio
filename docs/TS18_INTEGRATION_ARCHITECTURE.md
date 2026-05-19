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

## Explicit non-targets in product code
- No in-app TS18 probe framework.
- No TWUtil/TWClient reflective scanner modules.
- No vendor-service binder scaffolding without a concrete feature.
- No package impersonation or privileged UID assumptions.

## Implementation flow
1. Harden Android-standard behaviors (session, library browse, notification, buttons, focus).
2. Validate on TS18 with runbook scenarios.
3. If a repeatable gap remains, draft one explicit compatibility feature PR.
4. Land only the minimal feature needed for that validated gap.
