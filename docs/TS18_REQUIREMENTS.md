# TS18 Requirements for Auxio-TS (source-led)

## Framing

TS18/TW/TWTHEME parity is the product target. Requirements are written as parity acceptance criteria derived from TS18/TW/TWTHEME evidence. Android-standard (Tier 1) implementation is the preferred first path for each requirement; Android-standard success alone does not guarantee TS18-native parity until validated on real hardware (Tier 2). A parity gap confirmed in Tier 2 may trigger Tier 3/4 native investigation.

See canonical tier model: [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)



| Requirement | Confidence | Porting decision |
|---|---|---|
| Standards-compliant MediaSession behavior. | Observed | Directly reusable requirement |
| Standards-compliant MediaLibraryService / Android Auto browsing. | Observed | Directly reusable requirement |
| Reliable media notification transport controls. | Observed | Directly reusable requirement |
| Reliable media button support through standard Android APIs. | Observed | Directly reusable requirement |
| Audio focus correctness (gain/loss/duck/pause/recover). | Observed | Directly reusable requirement |
| Head-unit UI suitability (landscape readability, large touch targets, persistent playback affordances). | Inferred | Directly reusable requirement |
| Stable local library scanning/playback including FLAC baseline matrix. | Observed | Directly reusable requirement |
| Steering-wheel behavior via standard media key/controller pathway where possible. | Inferred | Reusable validation idea |

## Validation requirements (acceptance testing)

| Requirement | Confidence | Porting decision |
|---|---|---|
| Validate TS18 launcher/TWTHEME surfaces for metadata/control visibility. | Requires TS18 validation | Requires TS18 runtime validation |
| Validate ZLink/TLink coexistence with focus/session stability. | Requires TS18 validation | Requires TS18 runtime validation |
| Validate sleep/resume and navigation-mixing behavior. | Requires TS18 validation | Requires TS18 runtime validation |
| Validate stock coexistence and default-player switching. | Requires TS18 validation | Reusable validation idea |

## Explicit constraints

| Constraint | Confidence | Porting decision |
|---|---|---|
| No package impersonation (`com.tw.music`). | Observed | Should be explicitly avoided |
| No shared/system UID requirements. | Observed | Should be explicitly avoided |
| No copied proprietary/decompiled implementation. | Observed | Should be explicitly avoided |
| No speculative runtime TW probe framework inside app code. | Inferred | Should be explicitly avoided |
| No vendor service binding unless a concrete feature is explicitly designed and justified. | Inferred | Unsafe to port |
| Launcher shortcuts and deep-link entry points (Now Playing, Shuffle, Queue, library destinations, head-unit settings) must remain package-scoped and vendor-agnostic. | Inferred | Directly reusable requirement |
| Home-screen/head-unit widget compatibility should be delivered through standard Android AppWidget APIs only. | Inferred | Directly reusable requirement |

Launcher/widget standards validation scenarios TS18-STD-010..017 are defined in `TS18_VALIDATION_RUNBOOK.md` and remain **Requires TS18 validation** until hardware evidence is captured.

## Requirement refinements from Head-Unit Experience Mode batch
- Dashboard/quick-access entries should be policy-driven (stable route/label/icon/enabled behavior) and avoid dead entries.
- Queue surface should provide safe empty-state recovery (e.g., shuffle/browse action) in head-unit mode.
- Large-controls setting should materially change playback control ergonomics at runtime.
- Warm-start entry routing should preserve intended destination when app process/activity is already alive.

Confidence: Inferred / Requires TS18 validation. Porting decision: Directly reusable requirement.
