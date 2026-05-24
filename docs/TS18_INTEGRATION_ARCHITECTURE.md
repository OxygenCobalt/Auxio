# TS18 Integration Architecture

## Strategic framing

Auxio-TS targets TS18/TW/TWTHEME parity. Android-standard APIs are the preferred first implementation layer (Tier 1), not the final authority. Where Tier 1 implementation is proven insufficient for TS18/TWTHEME parity via on-device validation (Tier 2), native/private investigation (Tier 3/4) is a valid next step through a formal gap-and-promotion process.

## TS18 Native Parity Strategy

This is the canonical tier model for Auxio-TS TS18/TW/TWTHEME parity work.

### Tier model
Tier pipeline: Tier 0 evidence -> Tier 1 implementation -> Tier 2 evidence pack + summariser -> parity proposal -> Tier 3 candidate drafts (non-production) -> future approved Tier 4 design PR.

| Tier | Name | Scope |
|------|------|-------|
| **0** | **Evidence only** | t-music snapshot, TWTHEME resources, diagnostics, public repos, firmware notes. Defines parity target. |
| **1** | **Android-standard implementation** | MediaSession, MediaBrowser/MediaLibrary, notification, audio focus, media buttons, AppWidget, shortcuts, explicit app actions/deep links. Preferred first implementation layer. |
| **2** | **TS18-aware validation** | On-device evidence proving which TS18/TWTHEME surfaces see or ignore Tier 1 behaviour. Identifies parity gaps. |
| **3** | **Isolated native compatibility experiments** | External scripts or non-production branches testing specific TW/TWTHEME contracts. Allowed only after a Tier 2-validated parity gap. |
| **4** | **Production native integration** | Only if a specific contract is proven safe, maintainable, non-impersonating, and necessary via an explicit human-approved design PR. |

### Production eligibility for Tier 4

A native/private contract may enter production code **only** after an explicit human-approved design PR proves all of:

1. Product need — a specific, reproducible parity gap exists that Tier 1 cannot address.
2. Evidence-backed contract — a reliable public or local source documents the contract.
3. No package impersonation — does not require `com.tw.music` identity.
4. No copied smali — no decompiled proprietary code used.
5. No platform-signature/system-UID dependency — works as a normal third-party app.
6. Safe fallback — app remains functional on non-TS18 or non-TW devices.
7. Isolated implementation — confined to an adapter/facade boundary; does not spread into core.
8. Validation and rollback path — on-device test plan and rollback documented.

### What is NOT permanently forbidden

Native/private contracts are **not** permanently out of scope. They require the formal gap-and-promotion process above. Do not say "native is out of scope" — say "not for production by default; requires Tier 2 gap evidence and human-approved design PR."

See also: [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md) for current gap tracking.

## Preferred architecture (source-led)
```text
Auxio core
  -> Android-standard media layer (Tier 1)
       -> MediaSession / MediaLibrary / notification / media buttons / audio focus / AppWidget / shortcuts
  -> car/head-unit UX layer
       -> large touch targets, landscape assumptions, steering-wheel compatibility via standard APIs
  -> TS18 validation profile (Tier 2)
       -> expected behaviors + manual acceptance checks on TS18/TWTHEME hardware
  -> optional explicit compatibility features (Tier 3/4)
       -> only when Tier 2 identifies a specific parity gap
       -> only after evidence-backed contract + human-approved design PR
```

## Architectural commitments
- Auxio playback/library core remains upstream-first and vendor-agnostic.
- Android media contracts are the Tier 1 implementation baseline — preferred first, not the ceiling.
- TS18/TW/TWTHEME parity is the product target; Android-standard success does not automatically equal TS18-native parity.
- TS18 work is feature-driven, not probe-framework-driven.
- Validation evidence gates feature promotion.
- Native/private contracts are NOT permanently out of scope; they require the formal gap-and-promotion process.

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
1. Harden Android-standard behaviors (session, library browse, notification, buttons, focus) — **Tier 1**.
2. Validate on TS18/TWTHEME hardware with runbook scenarios — **Tier 2**.
3. If a repeatable parity gap remains after Tier 2 validation, draft one explicit compatibility feature PR — **Tier 3 → Tier 4**.
4. Land only the minimal feature needed for that validated gap, meeting all Tier 4 eligibility criteria.

## Validation and source links
- Runtime validation scenarios: `docs/TS18_VALIDATION_RUNBOOK.md`
- Requirements and constraints: `docs/TS18_REQUIREMENTS.md`
- Ecosystem/source context map: `docs/TW_ECOSYSTEM_SOURCE_MAP.md`
- Native parity gap matrix: `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`

## Launcher/widget compatibility model (TWTHEME/iLauncher)
- Auxio treats TWTHEME/iLauncher desktop integration as standard Android launcher compatibility, with AppWidgets, app shortcuts, explicit PendingIntents, and package-scoped deep-link actions.
- No formal public TWTHEME widget SDK is assumed in product code.
- Validation focus is whether generic Android entry points are discoverable and usable from TS18/TWTHEME launcher surfaces.
- Remaining parity gaps (e.g. widget image display quality, metadata richness) should be validated on hardware before considering any native path.
- Confidence: **Inferred / Requires TS18 validation**.
- Porting decision: **Directly reusable requirement / Reusable validation idea**.

- 2026-05-23: Source-backed TS18/TW/TWTHEME compatibility candidates started in app runtime (app/src/main/java/org/oxycblt/auxio/headunit/compat), with Android Tier 1 fallback still active and native/private production hooks still not enabled.

- Delivery protocol update: large-scope TS18 tasks must deliver runtime-wired outcomes; scaffold-only work is not counted as implemented.


2026-05-23 runtime release-readiness update: Metadata/session/widget/notification consistency and head-unit route/action safety were hardened in app runtime code; validation tooling remains external to APK; no TS18 hardware parity success claimed; no Tier 4 private/native integration performed.


## Topway decompile-driven compatibility rule
The official Topway `com.tw.music` apktool/JADX decompile is a primary local source for TS18/TW compatibility expectations. Android Tier 1 APIs remain the first implementation layer, but are not sufficient by themselves for Topway parity.

Safe compatibility strings may be implemented only inside an isolated bridge package (`app/src/main/java/org/oxycblt/auxio/headunit/topway/`) and corresponding tests/docs. These strings remain forbidden elsewhere in product code.


2026-05-24 architecture note: isolated Topway bridge package has been introduced and wired into runtime service/widget update paths. Private/native XT binder route remains non-production and blocked by default.
