# TS18 Integration Architecture

## Launcher/widget architecture baseline (implemented)
- TS18/TW/TWTHEME behavior and UX parity define product acceptance targets for this fork.
- Auxio-TS implements TS18/TWTHEME/iLauncher compatibility through **standard Android surfaces**:
  - app-scoped entry actions via `HeadUnitEntryPoints`
  - launcher shortcuts
  - `AppWidgetProvider` + `RemoteViews`
  - explicit `PendingIntent` routing
  - existing playback/session/notification/media-button service paths
- Warm-start routing via `MainActivity.onNewIntent()` is part of this baseline, including explicit queue routing (`openQueue`).
- Existing `widgets/WidgetProvider` is preserved/extended; no parallel widget provider is the default path.
- No public formal TWTHEME widget SDK is assumed, and no private TW/TWTHEME runtime API is used in product code.

Confidence: **Inferred** (architecture strategy), **Requires TS18 validation** (device-host behavior).
Porting decision: **Directly reusable requirement** (Android-standard implementation), **Reusable validation idea** (TS18 host compatibility checks).

## TS18 Native Parity Strategy
Tier 0 — Evidence only
- `t-music` snapshot evidence, TWTHEME resources, diagnostics captures, public repos/forums, firmware notes.

Tier 1 — Android-standard implementation (preferred first layer)
- MediaSession/MediaBrowser/notification/audio focus/media buttons/AppWidget/shortcuts/deep links.

Tier 2 — TS18-aware runtime validation
- On-device validation proving which TS18/TWTHEME surfaces honor or ignore Tier 1 behavior.

Tier 3 — Isolated native compatibility experiments
- External scripts or non-production branches evaluating concrete TW/TWTHEME native contracts.

Tier 4 — Production native integration (promotion-gated)
- Only through a future explicit human-approved design PR showing necessity, safety, maintainability, and non-impersonating implementation.

Android-standard implementation is Tier 1, not the ceiling. If Tier 1 does not achieve TS18/TWTHEME parity, gaps are tracked for native investigation rather than treated as out-of-scope.

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

## Validation and source links
- Runtime validation scenarios: `docs/TS18_VALIDATION_RUNBOOK.md`
- Requirements and constraints: `docs/TS18_REQUIREMENTS.md`
- Ecosystem/source context map: `docs/TW_ECOSYSTEM_SOURCE_MAP.md`
- Native parity gap matrix: `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`

## Launcher/widget compatibility model (TWTHEME/iLauncher)
- Auxio treats TWTHEME/iLauncher desktop integration as standard Android launcher compatibility, with AppWidgets, app shortcuts, explicit PendingIntents, and package-scoped deep-link actions.
- No formal public TWTHEME widget SDK is assumed in product code.
- Validation focus is whether generic Android entry points are discoverable and usable from TS18/TWTHEME launcher surfaces.
- Confidence: **Inferred / Requires TS18 validation**.
- Porting decision: **Directly reusable requirement / Reusable validation idea**.
