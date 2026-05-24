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

- Requirement update: no TS18 parity requirement is considered satisfied without imported Tier 2 hardware evidence.

- 2026-05-23: Source-backed TS18/TW/TWTHEME compatibility candidates started in app runtime (app/src/main/java/org/oxycblt/auxio/headunit/compat), with Android Tier 1 fallback still active and native/private production hooks still not enabled.

- Delivery protocol update: large-scope TS18 tasks must deliver runtime-wired outcomes; scaffold-only work is not counted as implemented.


2026-05-23 runtime release-readiness update: Metadata/session/widget/notification consistency and head-unit route/action safety were hardened in app runtime code; validation tooling remains external to APK; no TS18 hardware parity success claimed; no Tier 4 private/native integration performed.


## Topway runtime requirements derived from apktool/JADX (Observed)
- Runtime package identity is `com.tw.music`; JADX aliases like `com.p060tw.music`/`com.p073tw.music` are artifacts.
- Widget update path: `MusicWidgetProvider.onUpdate()` starts `MusicService`, sends `com.tw.music.action.cmd` with `cmd=update` and `appWidgetIds`, and updates all instances (event-driven, `updatePeriodMillis=0`).
- Required compatibility actions/keys (isolated bridge constants only):
  - `com.tw.music.info`: `musicTitle`, `musicaArtist`, `musicAlbum`, `musicPath`
  - `com.tw.launcher.music_progress_duration`: `msg_music_progress`, `msg_music_duration`
  - `com.android.launcher.widget_music_progress`: `music_progress`
  - `com.tw.music.action.cmd`, `com.tw.music.action.prev`, `com.tw.music.action.next`, `com.tw.music.action.pp`; cmd values `prev`, `next`, `pp`, `update`
- XT/AIDL (`com.tw.service.xt`, `CommandService`, `ITWCommandAidl`, `IMusicCallBack`) is Observed evidence only and Unsafe to port directly in production by default.


2026-05-24 runtime status: Topway decompile-derived broadcast/command/seek contract is now implemented in isolated runtime bridge wiring (no package impersonation, no copied smali, no XT/AIDL production dependency). TS18/iLauncher runtime verification remains pending.
