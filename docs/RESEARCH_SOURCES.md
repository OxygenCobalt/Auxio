# Research Sources and Evidence Weighting

## 1) Official Android documentation (authoritative for standard behaviour)
- Media3 session/control playback docs.
- Media3 MediaLibraryService/serve-content docs.
- Android for Cars media app guidance.
- Android platform and Media3 supported format docs.

**Can prove:** expected Android API behaviour, supported integration patterns, compatibility constraints.
**Cannot prove:** TS18 vendor-private contracts.

## 2) Upstream Auxio sources (authoritative for baseline app design)
- Upstream repo, release notes/changelog, and existing media architecture.

**Can prove:** intended Auxio design and maintainability boundaries.
**Cannot prove:** TS18-specific runtime behaviour.

## 3) TS18 local diagnostics in this repo (highest authority for this target device)
- `diagnostics/redacted/ts18_device_profile.json`
- redacted dumpsys/log summaries and script outputs.

**Can prove:** observed state on captured device/time.
**Cannot prove:** behaviour across all TS18 firmware variants or future updates.

## 4) TS18 manuals / community material
- Public TS18 manuals and forum/community notes.

**Can prove:** user-facing descriptions and possible feature surfaces.
**Cannot prove:** implementation contracts/APIs without runtime/static corroboration.

## 5) TW/TWTHEME reverse-engineering leads
- Public references mentioning TW utility classes/actions.
- Decompilation findings (kept private/redacted if proprietary).

**Can prove:** potential contract candidates and investigative directions.
**Cannot prove:** production-safe integration unless matched with runtime evidence.

## 6) Equivalent open-source projects
- Other Android head-unit media apps with adapter architectures.

**Can prove:** viable architectural patterns.
**Cannot prove:** direct compatibility with TS18/TW stack.

## Usage policy
For any TS18 integration claim, cite at least one of:
1. observed diagnostics/runtime evidence,
2. static reference tied to behaviour,
3. controlled A/B comparison (stock vs third-party vs Auxio-TS).

If evidence is missing, label as **inferred/hypothesis/requires TS18 validation**.
