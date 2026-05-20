## Summary
Pivot TS18/TW/TWTHEME planning from speculative in-app probe/adaptor direction to a source-led, Android-standard-first integration strategy PR.

## Motivation
- Keep Auxio-TS a clean, maintainable Auxio fork.
- Prevent speculative product-code lock-in around vendor-private contracts.
- Prioritize official Android Media3/MediaSession/MediaLibrary/notification/media-button/audio-focus behavior first.

## What changed
- Added `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` with:
  - rejection rationale for in-app probe/default-off scaffolding,
  - structured source corpus (official/public/community/local evidence),
  - confidence tagging and implementation implications,
  - implement-first vs validation-only boundaries.
- Updated strategy docs for internal consistency:
  - `docs/TS18_INTEGRATION_ARCHITECTURE.md`
  - `docs/TS18_NATIVE_CONTRACTS.md`
  - `docs/TW_ECOSYSTEM_SOURCE_MAP.md`
  - `docs/TS18_REQUIREMENTS.md`
  - `docs/TS18_VALIDATION_RUNBOOK.md`
  - `docs/DEVELOPMENT_ROADMAP.md`
  - `docs/CODEX_TASK_PROMPTS.md`
- Kept `docs/TS18_PUBLIC_REFERENCE_RESEARCH.md` as archived traceability notes with explicit supersession pointer.

## What was removed/rejected
- Rejected direction: in-app TS18 probe frameworks, default-off vendor adapter skeletons, TWUtil/TWClient reflection scanners, and speculative runtime contract registries.
- Rejected constraints/anti-patterns: package impersonation (`com.tw.music`), `android.uid.system`, copied stock smali, vendor-service binding without explicit feature design.

## Source corpus
Covers:
- Official Android docs (Media3/session/library/cars/mobile controls/audio focus + AOSP references).
- Public head-unit projects (Display-Media-Titles, OpenRadioFM, FytHWOneKey, FET/FYTuis7862BinRepo, Headunit Revived, mikereidis/headunit).
- Reference-only TW/Topway ecosystem sources (CarRadio, dvd-bt, topwaytool, DoFun/Mekede/XDA threads).
- Local evidence anchors (`t-music` snapshot, TS18 diagnostics).

## Validation
Executed branch-readiness checks:
- `git diff --check`
- `find docs -type f -name '*.md' -print | sort`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`
- strategy-term and product-code safety grep audits.

## Scope boundaries
This PR is docs/planning finalization only.
- No app runtime feature implementation.
- No TS18 probe/adaptor product code.
- No vendor-private integration.

## Remaining runtime validation
TS18 on-device acceptance remains required for:
- MediaSession visibility — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- MediaLibrary/Android Auto browsing — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- Notification controls — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- Media-button/steering-wheel behavior — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- Audio-focus/navigation-mixing — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- ZLink/TLink coexistence — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- Launcher/widget and TWTHEME visual behavior — Confidence: **Requires TS18 validation**; Porting decision: **Requires TS18 runtime validation**.
- Sleep/resume and FLAC matrix — Confidence: **Requires TS18 validation**; Porting decision: **Reusable validation idea**.

## Recommended next PR
**Phase 5B only**: Android-standard MediaSession / notification / audio-focus hardening.

Explicitly exclude in next PR:
- vendor-private APIs,
- TS18 probes,
- TWUtil/TWClient reflection,
- TWTHEME adapters,
- package impersonation,
- copied smali.
