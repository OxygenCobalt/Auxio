# TS18 Requirements for Auxio-TS (source-led)

## Phase 1 implementation requirements (product code)

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
