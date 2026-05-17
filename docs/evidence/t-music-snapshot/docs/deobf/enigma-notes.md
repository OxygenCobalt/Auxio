# Enigma Notes — deobfuscation working log

Last updated: 2026-05-02 (UTC)

## Top-level package inventory (initial)

- `com/tw/music` — primary app package (entry components and core logic).
- `p060tw/...` — obfuscated TW/vendor-adjacent application logic surfaces (naming varies by class).
- `p011c/p015b/...` and similar short segments — heavily obfuscated collaborators/utilities.
- `android/support/...` — legacy support library classes (generally not rename targets).

## Confirmed class roles (resolved in this pass)

- `com/tw/music/b/a` (`MusicInfo.java`) is the in-memory track/playback info container used by `MusicService` for `isPlaying` checks and metadata fields.
- `com/tw/music/k` is the `MusicService` broadcast receiver handling `com.tw.music.action.prev|next|pp` plus widget update command routing.
- `com/eckom/xtlibrary/b/f/e/a` (`MusicPresenter.java`) is the presenter bridge invoked by `MusicService` for prev/next/play-pause command execution.
- `com/eckom/xtlibrary/b/f/d/a` (`BaseMusicMode.java`) is the abstract base model for music feature data operations.
- `com/eckom/xtlibrary/b/f/g/a` (`MusicModelView.java`) and `com/eckom/xtlibrary/b/f/g/b` (`MusicView.java`) define the MVP-facing music contracts.

Mapped in: `mappings/manual-enigma/music-core.mapping`.
- `com/tw/music/c/a` (`ListPlugin.java`) mapped as `com/tw/music/plugin/ListPlugin` (UI/plugin list resource holder).
- `com/tw/music/c/b` (`MainPlugin.java`) mapped as `com/tw/music/plugin/MainPlugin` (main plugin drawable bundle owner).
- `com/eckom/xtlibrary/b/f/f/s` (`TWMusic.java`) mapped as `com/eckom/xtlibrary/b/f/f/TWMusic` (core library/state holder used by model classes).
- `com/eckom/xtlibrary/b/f/f/t` (`TWMusicIikID3.java`) mapped as `com/eckom/xtlibrary/b/f/f/TWMusicIikID3` (ID3 helper singleton used by ID3 models).
- `com/eckom/xtlibrary/b/f/b/e` (`MusicBean.java`) mapped as `com/eckom/xtlibrary/b/f/b/MusicBean` (music grouping/data bean).
- `com/eckom/xtlibrary/b/f/a/c` (`ThreadPoolManager.java`) mapped as `com/eckom/xtlibrary/b/f/a/ThreadPoolManager` (thread-pool/task orchestration helper).
- `com/eckom/xtlibrary/b/f/b/a` (`AlbumMedia.java`) mapped as `.../AlbumMedia` (album grouping model).
- `com/eckom/xtlibrary/b/f/b/b` (`ArtistMedia.java`) mapped as `.../ArtistMedia` (artist grouping model).
- `com/eckom/xtlibrary/b/f/b/c` (`LMedia.java`) mapped as `.../LMedia` (per-track metadata/model record).
- `com/eckom/xtlibrary/b/f/b/d` (`MediaFolderBean.java`) mapped as `.../MediaFolderBean` (folder-to-track grouping container).
- `com/eckom/xtlibrary/b/f/f/c` (`MusicDataHolder.java`) mapped as `.../MusicDataHolder` (shared singleton data holder for music lists/maps).
- `com/eckom/xtlibrary/b/f/f/h` (`MusicUtils.java`) mapped as `.../MusicUtils` (utility owner with nested async tasks/callback helpers).
- `com/eckom/xtlibrary/b/f/f/a` (`CollectionUtils.java`) mapped as `.../CollectionUtils` (music-collection persistence helper).
- `com/eckom/xtlibrary/b/f/f/b` (`LrcTranscoding.java`) mapped as `.../LrcTranscoding` (lyric transcoding helper).
- `com/eckom/xtlibrary/b/f/f/l` (`SPUtils.java`) mapped as `.../SPUtils` (SharedPreferences utility wrapper).
- `com/eckom/xtlibrary/b/f/f/k` (`PinyinConv.java`) mapped as `.../PinyinConv` (pinyin conversion helper).
- `com/tw/music/c/a$a` (`ListPlugin.java` inner class) mapped as `com/tw/music/plugin/ListPlugin$PluginItem` (plugin item drawable/title holder).
- `com/eckom/xtlibrary/b/f/f/h$a` mapped as `MusicUtils$RecordCallback` (record/status callback contract used by MusicUtils async tasks).
- `com/eckom/xtlibrary/b/f/f/h$f` mapped as `MusicUtils$StringCallback` (string-result callback used by scan task).
- `com/eckom/xtlibrary/b/f/f/h$b` mapped as `MusicUtils$RecordPathTask` (`AsyncTask` for record/path processing).
- `com/eckom/xtlibrary/b/f/f/h$e` mapped as `MusicUtils$LibraryScanTask` (`AsyncTask` scanning path into `MusicBean` with callback).
- `com/eckom/xtlibrary/b/f/a/b` mapped as `ThreadPoolManager$QueueWatchHandler` (message-loop queue watcher; role proven by `handleMessage` body).
- `com/eckom/xtlibrary/b/f/f/d` mapped as `MusicUtils$ThreadFactoryImpl` (thread factory used by `MusicUtils.pool`).
- `com/eckom/xtlibrary/b/f/f/i` mapped as `MusicUtils$AudioFileFilter` (audio-only filter used by `RecordPathTask`).
- `com/eckom/xtlibrary/b/f/f/j` mapped as `MusicUtils$CollectingFileFilter` (collecting filter used in `h$c` background scan).
- `h$c`/`h$d` disambiguated to conservative pair names `MusicUtils$RecordTaskA/B` after call-site trace showed distinct entrypoints (`MusicUtils.a(...)` vs `MusicUtils.b(...)`) but close behaviour/callback patterns.
- `com/eckom/xtlibrary/b/f/f/f` mapped as `MusicUtils$PersistMusicStateThread` (writes state payload to `/data/tw/music`).
- `com/eckom/xtlibrary/b/f/f/g` mapped as `MusicUtils$PersistLikesThread` (writes liked paths to `/data/tw/.like`).


## Playback/session audit notes (MusicService path)

- Broadcast command intake:
  - Registered in `MusicService.onCreate` for `com.tw.music.action.cmd|prev|next|pp`.
  - Dispatched in both `MusicService.onStartCommand` and receiver `com/tw/music/k.onReceive`.
- Command to presenter mapping:
  - prev -> presenter `rb()`
  - next -> presenter `pb()`
  - play/pause toggle -> presenter `ba()` (pause) / `fa()` (play) based on `MusicInfo.isPlaying()`
- Metadata update hook:
  - `MusicService.a(String,String,String,Bitmap,String,String,int)` forwards to super and then calls widget refresh.
- Widget update hook:
  - receiver supports `cmd=update`, forwards `appWidgetIds` to `MusicWidgetProvider` updater.


## Media-session/notification ownership trace (extended static audit)

- `MusicService` command ingress remains thin and delegates transport controls to `MusicPresenter` (`rb/pb/ba/fa`) through `mPresenter`.
- `MusicPresenter` (`com/eckom/xtlibrary/b/f/e/a`) delegates transport calls directly into `BaseMusicModel` implementations (`rb/pb/Fb/Va` etc.).
- Candidate concrete model owners for playback + metadata operations are:
  - `com/eckom/xtlibrary/b/f/d/U` (`MusicIjkModel.java`)
  - `com/eckom/xtlibrary/b/f/d/ba` (`MusicModel.java`)
  - `com/eckom/xtlibrary/b/f/d/t` (`MusicID3Model.java`)
  - `com/eckom/xtlibrary/b/f/d/L` (`MusicIjkID3Model.java`)
- Metadata extraction pathways are confirmed in model-layer classes via `android/media/MediaMetadataRetriever` usage; runtime publication into `MediaSession`/notification still requires device evidence tracing.
- `MusicWidgetProvider` command wiring is confirmed through service `PendingIntent` actions into `com.tw.music.action.*` surfaces.

## Remaining investigation queue

1. Confirm exact `MediaSession` / `PlaybackState` publication owners under xtlibrary classes and capture runtime evidence (`dumpsys media_session`).
2. Confirm exact notification builder/action wiring classes and on-device action behavior.
3. Confirm metadata fidelity across all sources (local file tags, stream, malformed metadata).
4. Confirm TLink/CarPlay/Android Auto session consumers and suspend/resume interactions.

## Working rules

- Never guess mappings; confirm behavior in smali first.
- Record each confirmed rename in `mappings/manual-enigma/*.mapping`.
- Add `# TODO: behaviour unclear — investigate further` comments in smali where needed.


## Linked remediation tracker

- `docs/reports/jadx-remediation-checklist.md` is the method-level checklist for known JADX discrepancy follow-up in active migration scope.


## MediaSession/PlaybackState/MediaMetadata owner discovery (current static result)

- Static symbol scan across `app/apktool/smali*` found no direct non-support-library references to `MediaSession`, `PlaybackState`, or `MediaMetadata` publication APIs in current canonical smali.
- Implication: ownership likely sits behind obfuscated wrappers, alternate abstractions, or vendor-facing bridge paths that do not retain direct framework type names in obvious form.
- Next probe: trace from transport methods in `MusicPresenter` (`rb/pb/ba/fa`) into concrete model call chains and identify callback/event sinks that touch notification/service publication boundaries.
- Mapping evidence ledger: `mappings/manual-enigma/mapping-evidence.md`.

- Vendor hook ownership baseline: `docs/reports/vendor-hook-owners.md`.


## Stage-4 method owner findings (presenter/model call-chain)

Confirmed call-chain pattern:
- `MusicService` -> `MusicPresenter` (`rb/pb/ba/fa`) -> concrete `BaseMusicModel` implementations (`U`, `ba`, `L`, `t`) -> callback interface `com/eckom/xtlibrary/b/f/g/a` methods.

High-confidence callback publication owner sites identified:
- `U.smali` around lines `3224+`: invokes `g/a->b(String,String,String,Bitmap,String,String,int)` (metadata payload publication callback), then state/progress callbacks `c(Z)`, `D(I)`, `B(I)`, and player object callback `a(TWMediaPlayer)`. 
- `ba.smali` around lines `3498+`: invokes `g/a->b(String,String,String,Bitmap,String,String,int)`, then `c(Z)`, `D(I)`, `B(I)`, and player object callback `a(MediaPlayer)`.
- `L.smali` around lines `4272+` and `4414+`: invokes `g/a->b(String,String,String,Bitmap,String,String,int)` and related state/progress callbacks.
- `t.smali` around lines `4383+`: invokes `g/a->b(String,String,String,Bitmap,String,String,int)` and related state/progress callbacks.

Interpretation:
- Metadata publication within app-layer appears to be centered on `g/a->b(...)` callback dispatch from concrete model implementations.
- Playback/position/state update dispatch appears to flow through `g/a->c(Z)`, `g/a->D(I)`, `g/a->B(I)`, and `g/a->d(II)` callbacks.
- Direct framework-symbol publication calls for `MediaSession/PlaybackState/MediaMetadata` remain unresolved in canonical smali (likely abstracted/wrapped path).


## Low-confidence unresolved candidates (notification/foreground ownership)

- No direct non-support-library `startForeground/stopForeground` invocations were found in canonical app smali during current static scan.
- No direct non-support-library `NotificationCompat.Builder` call sites were found in canonical app smali during current static scan.
- Candidate ownership likely sits behind obfuscated wrappers or service abstractions in model/presenter callbacks and requires deeper trace + device/runtime evidence.


## Stage-4 bounded uncertainty record (this pass)

### Exact searches performed
- `rg -n "NotificationCompat\$Builder;->|MediaStyle;->setMediaSession|Service;->startForeground|Service;->stopForeground|MediaSessionCompat|PlaybackStateCompat|MediaMetadataCompat" app/apktool/smali* | rg -v "app/apktool/smali_classes3/android/support"`
- `rg -n "startForeground|stopForeground|Notification|onStartCommand|ServiceCompat|Foreground" app/apktool/smali_classes3/com/eckom/xtlibrary/twproject/service/*.smali`
- callback sink tracing from `MusicActivity` interface callbacks: `B(I)`, `D(I)`, `a(Boolean)`, `a(String,...,int)`, `d(II)`, `h(Z)`, `q(Z)`.

### Files/classes inspected
- `com/tw/music/MusicActivity.smali`
- `com/eckom/xtlibrary/twproject/activity/BaseMusicService.smali`
- `com/eckom/xtlibrary/twproject/activity/BaseMusicActivity.smali`
- `com/eckom/xtlibrary/twproject/service/XTService.smali`
- model classes previously traced: `U`, `ba`, `L`, `t`

### Findings
- Callback sinks in `MusicActivity` are strongly UI-state focused (artwork/text/progress/play-state visuals) and do not directly reveal notification builder or foreground promotion calls.
- `XTService` lifecycle (`onCreate/onStartCommand/onDestroy`) currently shows presenter lifecycle management but no direct foreground promotion/demotion APIs.
- No direct non-support-library `NotificationCompat.Builder`, `startForeground`, or `stopForeground` call sites found in canonical app smali by current static scan.

### Bounded unresolved set
- Notification MediaStyle/action construction and wiring owner remains unresolved (static).
- Foreground service start/stop owner remains unresolved (static).
- Remaining closure likely requires either:
  1) deeper static deobfuscation/readability of wrapper/service abstraction layers, or
  2) runtime/device evidence (`dumpsys`, logs, notification interaction) on TS18.

## 2026-05-03 — Slice S9 (R2 TWMusic anonymous helper batch)

- Continued R2 beyond S8 by tracing additional high-confidence anonymous helpers adjacent to mapped `TWMusic` / `MusicUtils` owners.
- Added `MusicUtils` sort-helper mapping from explicit comparator logic:
  - `e` -> `MusicUtils$NameComparator`.
- Added `TWMusic` anonymous helper mappings based on enclosed method and literal predicate behavior:
  - `m` -> `TWMusic$AudioCandidateFileFilter`
  - `n` -> `TWMusic$ExtSdRootFilter`
  - `o` -> `TWMusic$StorageRootFilter`
  - `p` -> `TWMusic$ExtSdAliasFilter` (kept conservative due to duplicate `extsd` predicate)
  - `q` -> `TWMusic$UsbRootFilter`
  - `r` -> `TWMusic$PersistStateThread`.
- Kept method/field semantics unresolved where not needed; class-level mapping only.

## 2026-05-03 — Slice S10 (R2 b/a cluster class-level mapping batch)

- Continued R2 from S9 into the next inventory-recommended non-`b/f/f` cluster (`b/a/*`).
- Promoted a larger, high-confidence class-level batch primarily anchored by explicit `.source` names and clear role-bearing supertypes/interfaces.
- Confirmed BT stack mappings:
  - `BroadcastManager` + callback inners (`StringPairCallback`, `StateCallback`)
  - `BTModel`, `BTModelView`, `BaseBTModel`, `BuildInBTModel`
  - `BTPresenter`, `BTView`
  - `VoiceCallView`
- Confirmed shared/supporting helpers in same cluster:
  - `CommonData`, `LogUtil`, `TWAT`
  - `ContactDBHelper`, `ContactDBManager`, `DBHelper`
- Preserved conservative policy: class-level mapping only; no method/field semantic promotions and no vendor-surface changes.

## 2026-05-03 — Slice S11 (R2 b/h radio cluster batch)

- Continued R2 from S10 with the inventory-prioritized `b/h/*` radio cluster.
- Added high-confidence class/interface mappings anchored by explicit `.source` names and presenter/model/view ownership relationships.
- Confirmed mappings:
  - `RadioDataHolder`, `FreqPs`
  - `RadioModel`, `RadioModelView`, `RadioPresenter`, `RadioView`
  - `TWRadio`, `TWRadio$Holder`
- Deferred anonymous/synthetic internals (`b/h/b/{a,b,c,d}`, `b/h/d/a`) for now because class-level owner mapping already provides readability without forcing speculative anonymous-role labels.

## 2026-05-03 — Slice S12 (R2 b/i theme cluster batch)

- Continued R2 from S11 with post-radio next high-confidence inventory cluster: `b/i/*`.
- Added 14 class-level mappings centered on theme/plugin/runtime helpers:
  - `DFLog`, `FileUtils`, `IThemeSwitchStatus`, `PluginContext`, `ReflectUtil`, `RunUtil`, `RunUtil$MainHandler`
  - `ThemeConfig`, `ThemeHelper`, `ThemeManager`, `ThemeManager$Holder`, `ThemePlugin`, `ThemeSwitchInfo`, `ThemeUtil`
- Kept comparator/anonymous helper internals under `ThemeManager` (`k$b`, `k$c`, `k$d`) deferred to avoid speculative role labels beyond interface type.

## 2026-05-04 — Method-boundary readability pass (R2 continuation, post-accelerator)

Scope guardrails followed this pass:
- no smali edits;
- no descriptor renames;
- no bulk method/field sweeps;
- no vendor/runtime contract surface changes.

### Candidate groups reviewed

1. Presenter/model/view interface contracts:
   - `MusicModelView` callback surface (`g/a`) and publisher call-sites in `U`, `ba`, `L`, `t`.
2. Callback/listener contracts:
   - BT/Radio/Video/Launcher callback interfaces with minified methods and boundary payload signatures.
3. Handler/runnable/task boundaries:
   - high-impact `handleMessage`, `run`, `doInBackground` candidates in mapped owners.
4. Service/activity/receiver/widget lifecycle boundaries:
   - `MusicService`, `MusicActivity`, service/receiver command wiring classes (`j`, `k`).
5. Media/session/playback publication candidates:
   - retained callback working labels at `MusicModelView` boundary; no new semantic promotions.

### Outcome summary

- Confirmed existing working labels at the `MusicModelView` callback boundary remain the best evidence-backed method-level labels for playback-state publication flow.
- Deferred additional method-level renames in BT/Radio/Video/Launcher interfaces due to medium-confidence semantics.
- Kept unresolved notification/foreground owner question unchanged (no speculation promoted).

## 2026-05-04 — Bounded interface-family trace (BT/Radio/Video/Launcher)

- Completed a concrete bounded method-boundary slice centered on interface contracts:
  - `BTModelView` (`b/a/d/g`)
  - `RadioModelView` (`b/h/b/f`)
  - `VideoView` (`b/k/c/c`)
  - `LauncherView` (`b/d/c/a`)
- Added compact evidence table with callers/implementers/payload interpretation and working-label confidence in:
  - `docs/reports/readability-method-boundary-review.md`
- Promoted only high-confidence working labels where payload type + forwarding path was explicit.
- Left medium/low-confidence callbacks deferred with missing-evidence notes (event-code/channel disambiguation still needed).

## 2026-05-04 — Larger boundary closure batch (BT/Radio/Video/Launcher)

- Upgraded from a small sample to a larger bounded closure pass across 4 families.
- `docs/reports/readability-method-boundary-review.md` now records 25 method/interface rows with:
  - caller -> presenter -> sink direction,
  - payload channel interpretation,
  - confidence and status,
  - exact next-evidence requirements for deferred channels.
- Launcher family is closed at current method-boundary depth.
- Video/Radio/BT families are partially closed with concrete grouped deferred sets instead of broad unresolved notes.

## 2026-05-04 — Persistent method-boundary triage system

- Added durable ledger: `docs/reports/readability-method-boundary-review.tsv`.
- Readability queue metric is now `remaining_actionable` (from generator output), not raw total candidates.
- Raw total remains broad for visibility; actionable queue now excludes confirmed/closed/deferred/runtime/low-value/generated rows.
### 2026-05-04 queue-collapse update
- Driver metric is now `remaining_actionable`, not raw candidate total.
- Added deterministic bulk triage rules for `low_value`, `generated_or_bridge`, `readable_framework_lifecycle`, and `readable_android_callback` via `tools/readability/04_high_impact_method_candidates.py`.
- Bulk classifications are written to `docs/reports/readability-method-triage-generated.tsv`; manual evidence stays in `docs/reports/readability-method-boundary-review.tsv`.
- Next review cycles should focus only on `unresolved_actionable` families grouped in `docs/reports/readability-high-impact-methods.md`.


### 2026-05-04 true-actionable queue collapse
- Success metric is `true_remaining_actionable` (not `reviewed_total`).
- Deterministic statuses now include: `simple_delegate`, `trivial_accessor`, `constructor_or_clinit`, `no_readability_value`, `reviewed_but_grouped`.
- Next pass should work representative unresolved groups only and close one family end-to-end.

### 2026-05-04 multi-family reduction (post-Launcher)
- Previous true_remaining_actionable: 199; current: 92.
- Video family closed to 0 active by grouping representative callback set in ledger.
- BT/Radio/Unknown reduced via grouped delegate/accessor/helper triage while preserving unresolved representatives.
- Next pass target: close or defer Music playback/session boundary set with evidence-first grouping.

### 2026-05-04 Radio closure pass
- Radio active queue collapsed from 23 to 0 by grouping `RadioModelView` callback overload family into representative closure entries.
- Global true_remaining_actionable reduced from 64 to 41.
- Remaining active families: Unknown app-owned and BT.

## 2026-05-06 PR#22 readiness patch note

- Scope is audit/readiness consistency only (no new broad readability implementation pass).
- Continuation chain is: PR#23 into PR#22 (`cx/continue-pr18-work-on-readability-ledger`), then PR#22 into PR#18 (`cx/high-impact-method-boundary-readability`).
- No smali/resource edits were required for this readiness slice.
- Tooling requirement for this slice is limited to repository Python/Git/Bash checks; Android/JADX installation is not required.

## 2026-05-07 Playback/control closure evidence
- Documented playback/control symbol map and architecture-only bridge plan.
- Confirmed no MediaSession/notification runtime implementation in this slice.
