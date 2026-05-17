# Readability Closure Plan — “Everything Is Readable” (com.tw.music)

> **Authority note (2026-05-07):** Live phase/gate readiness is tracked only in `docs/migration-blueprint.md`; this document provides supporting detail/evidence only.


Last updated: 2026-05-02 (UTC)
Status: planning-only (no broad rename implementation started)

## 1) Investigation performed (this pass)

Reviewed:
- repo state (`git status`, `git branch`, recent commits, current diff)
- migration/deobf trackers
  - `docs/migration-blueprint.md`
  - `docs/deobf/enigma-notes.md`
- mapping sources
  - `mappings/manual-enigma/README.mapping`
  - `mappings/manual-enigma/music-core.mapping`
  - `mappings/manual-enigma/mapping-evidence.md`
- reports and release validation
  - `docs/reports/jadx-problems.txt`
  - `docs/reports/vendor-hooks.txt`
  - `docs/release-validation-matrix.md`
  - `docs/release-validation-results-2026-05.md`
- canonical editable app surfaces
  - `app/apktool/AndroidManifest.xml`
  - `app/apktool/smali*`
  - `app/apktool/res/*`
- existing scripts for build/reports/vendor-token guarding
  - `scripts/01_refresh_reports.sh`
  - `scripts/02_build_unsigned.sh`
  - `scripts/03_jadx_export_raw.sh`
  - `scripts/04_jadx_export_aliased.sh`
  - `scripts/08_verify_vendor_tokens.sh`

Quick static baseline sampled this pass:
- smali roots: 4
- class files: 3028
- heuristic obfuscated class files (filename-based heuristic): 1679

## 2) Current readability state

### Already readable / strongly improved
- Core entry components are readable (`MusicActivity`, `MusicService`, widget provider) via manifest and canonical package names.
- A first high-confidence mapping layer exists for playback-path classes in `music-core.mapping` (presenter/model contracts and model variants).
- A mapping evidence ledger exists with confidence levels and unresolved owner candidates.
- Tracker docs already bound key uncertainties (notification/foreground owner unresolved but explicit).

### Mapped but not renamed (current dominant model)
- Existing effort is mapping-first, not descriptor-renaming-first.
- Many symbols are semantically understood in docs/mappings, while canonical smali descriptors remain obfuscated.
- Method/field-level readability still trails class-level readability.

### What remains minified / unreadable at scale
- Large volume of short-name classes in `com/eckom/xtlibrary/...` and other obfuscated trees.
- Obfuscated method/field names still common (e.g., `a`, `b`, `rb`, `pb`, `D`, `B` style names).
- Interface/callback method semantics remain partially inferred in several high-impact paths.

### Highest-impact readability domains (priority order)
1. Playback/session/model/presenter callbacks that influence state publication.
2. Notification + widget + service ownership chain.
3. UI controllers/adapters/view holders tied to player/list behavior.
4. Data/model/preferences/storage keys affecting runtime behavior.

### Unsafe / vendor-sensitive domains (must not speculative-rename)
- `com.tw.music.action.*` command surfaces.
- `com.tw.service.xt.aidl.*` interface tokens and AIDL transaction surfaces.
- `persist.tw.*` / `persist.media.*` related hooks.
- `com.tw.eq/.EQActivity`, `com.tw.radio.*`, theme resources tied to TW environment.

### Low-value or optional full-readability domains
- Third-party/support-library trees (android/support, androidx, bundled/antlr/cpdetector-like vendor libs) where app-maintainer value is low.
- Synthetic/compiler-generated wrappers where readability can be documented/classified without invasive rename.

## 3) Inventory method (R0)

Produce a repeatable inventory artifact (`docs/reports/readability-inventory.md` + machine-readable TSV/CSV):

1. **Symbol census extraction** from canonical smali:
   - classes/interfaces/inners from `.class`, `.super`, `.implements`
   - methods from `.method`
   - fields from `.field`
2. **Readability heuristics tagging**:
   - short/minified names (`a`, `b`, `C0001a`, `aa`, `RunnableC...` etc.)
   - opaque descriptors (`Lc/b/a/...;`)
3. **Cross-evidence joins**:
   - manifest components
   - resource usage (layouts/ids/strings)
   - vendor token hits
   - mapping coverage (`*.mapping`)
   - JADX discrepancy flags (`jadx-problems` and remediation checklist)
4. **Impact ranking**:
   - call-frequency / cross-reference count
   - reachable from entry components (activity/service/receiver)
   - vendor-boundary proximity
5. **Ownership bucketization**:
   - app-owned vs support/third-party/synthetic.

## 4) Classification model (required per symbol)

Every unreadable symbol must be assigned one category:
1. confirmed rename candidate
2. mapping-only candidate
3. vendor token / external contract (do not rename)
4. unsafe due to reflection/JNI/serialization/resource/runtime coupling
5. generated/synthetic/support-library (ignore or low priority)
6. unknown (needs more evidence)
7. low-value tail (document and intentionally leave)

Classification is recorded in `docs/reports/readability-inventory.md` with rationale + evidence pointers.

## 5) Evidence standard before rename/mapping

Minimum evidence bar per promoted rename (class/method/field):
1. manifest/component role (if applicable)
2. resource/layout/id/string usage context
3. call-path provenance (who calls, who consumes)
4. framework API behavior semantics
5. callback registration + dispatch relationship
6. persistence coupling (prefs/db/serialization keys)
7. vendor hook check (token/report cross-check)
8. JADX-vs-smali discrepancy resolution note when relevant
9. runtime evidence requirement flag (Required / Not required)

No symbol is renamed purely from guessed decompiler alias.

## 6) Rename/mapping strategy

### Prefer mapping-only when
- symbol touches vendor/external contract or runtime-coupled reflection not yet proven,
- evidence is medium/low,
- rename blast-radius is high.

### Allow descriptor rename in smali when ALL are true
- high-confidence behavior evidence,
- no vendor/external-contract risk,
- full descriptor/reference rewrite plan validated,
- post-change build + vendor-token checks pass.

### Descriptor-correctness checklist for any smali rename
Must update/validate all affected references:
- `.class`, file path
- `.super`
- `.implements`
- field type descriptors
- method signatures
- invoke targets
- casts/check-cast/new-instance
- annotations/signatures
- `const-class`
- manifest/resource component refs where applicable
- reflection strings only when fully proven safe

Any uncertain coupling => mapping-only + TODO evidence note.

## 7) Work staging (safe, reviewable)

### R0 — Inventory + baseline metrics
- Build initial unreadable symbol census and heatmap.
- Publish baseline counts and high-impact candidate queue.

### R1 — Exclusion/guardrail set
- Generate explicit vendor-boundary exclusion list.
- Generate unsafe-coupling list (reflection/serialization/etc.).
- Mark support/synthetic ignore buckets.

### R2 — Playback/session high-confidence pass
- Complete mapping/rename for highest-confidence playback model/presenter/callback symbols.
- Promote method-level names where evidence is high.

### R3 — Notification/widget/foreground-service pass
- Resolve owner chain and callback semantics.
- Apply mapping/rename only after owner confidence is high.

### R4 — UI/resource/controller pass
- Focus on player/list/split/settings controllers/adapters/viewholders.
- Improve manifest/resource-facing readability safely.

### R5 — Data/model/preferences/storage pass
- Clarify persistence models, keys, and data transformers.

### R6 — Utilities/callback/listener pass
- Triage frequent utility/callback abstractions for maintainability.

### R7 — Unresolved queue / tail-risk triage
- Explicitly classify remaining unknowns and low-value tail.
- Document why leftovers remain untouched.

### R8 — Verification + sync + readiness
- Ensure mapping/docs/reports synchronized.
- Re-run build/report/vendor guards and finalize acceptance checklist.

## 8) Metrics and acceptance criteria

## Baseline metrics to track each slice
- unreadable class count (heuristic + reviewed)
- unreadable method count in high-impact packages
- unreadable field count in high-impact packages
- mapping coverage count (class/method/field)
- unresolved high-impact callback count
- explicitly excluded vendor-contract symbol count
- documented JADX discrepancy coverage count
- accidental vendor-token delta count (must remain 0)

## Final acceptance criteria for readability closure
1. All high-impact app-owned playback/session/notification/widget/UI symbols are readable via name or documented mapping.
2. Remaining unreadable symbols are explicitly classified (unknown/unsafe/vendor/synthetic/low-value).
3. No speculative high-risk rename merged without evidence.
4. Vendor/runtime contract surfaces unchanged.
5. Docs + mapping ledger + inventory remain synchronized.
6. Per-slice checks pass: build/report/vendor token guard.
7. Runtime-only unresolved items are clearly blocked and tracked separately from static readability completion.

## 9) Tooling/scripts (minimal additions)

Add small auditable scripts under `tools/readability/`:
1. `01_inventory_symbols.py`
   - export classes/methods/fields + heuristic readability tags.
2. `02_mapping_coverage.py`
   - compare symbol census vs manual mappings.
3. `03_descriptor_consistency_check.py`
   - detect stale descriptors/references after rename slices.
4. `04_vendor_guard_diff.py` (optional extension)
   - assert no drift in protected token-bearing files between commits.
5. `05_jadx_discrepancy_coverage.py`
   - tie `jadx-problems` entries to remediation status.

No bulk auto-rename tooling in this phase.

## 10) Documentation outputs required

- `docs/readability-closure-plan-2026-05.md` (this plan)
- `docs/reports/readability-inventory.md` (living inventory + classification)
- `docs/reports/readability-vendor-exclusions.md`
- `docs/reports/readability-unsafe-symbols.md`
- `docs/reports/readability-unresolved-queue.md`
- updates to:
  - `mappings/manual-enigma/mapping-evidence.md`
  - `docs/deobf/enigma-notes.md`
  - `docs/migration-blueprint.md` (status linkage)

## 11) First implementation slice (recommended next step only)

**Slice S1 (small/safe):** implement R0 + R1 artifacts without renaming app symbols.

Deliverables:
1. add `tools/readability/01_inventory_symbols.py` and generate first inventory report
2. add `docs/reports/readability-inventory.md` baseline counts + top-50 high-impact unreadable symbols
3. add `docs/reports/readability-vendor-exclusions.md` seeded from existing vendor-hooks report
4. add `docs/reports/readability-unsafe-symbols.md` with initial reflection/serialization/runtime-coupling suspects
5. run:
   - `bash scripts/02_build_unsigned.sh`
   - `bash scripts/01_refresh_reports.sh`
   - `bash scripts/08_verify_vendor_tokens.sh`

No broad rename/method signature edits in S1.


## 12) Execution progress log

### 2026-05-03 — Slice S1 (R0 + R1) completed
- Added tooling: `tools/readability/01_inventory_symbols.py`.
- Generated baseline inventory: `docs/reports/readability-inventory.md`.
- Added exclusion/unsafe reports:
  - `docs/reports/readability-vendor-exclusions.md`
  - `docs/reports/readability-unsafe-symbols.md`
- Verification run for this tooling/report slice:
  - `python3 tools/readability/01_inventory_symbols.py`
  - `bash scripts/08_verify_vendor_tokens.sh`
- No smali/resource/manifest descriptors renamed in this slice.


### 2026-05-03 — Slice S2 (R2 narrow mapping pass) completed
- Refined inventory tooling to exclude generated `R`/`R$*` classes from unreadable ranking metrics.
- Added two high-confidence playback-adjacent/UI plugin class mappings from `.source` evidence:
  - `com/tw/music/c/a` -> `com/tw/music/plugin/ListPlugin`
  - `com/tw/music/c/b` -> `com/tw/music/plugin/MainPlugin`
- Updated mapping evidence and deobf notes for these new confirmed mappings.
- Kept scope mapping-only (no smali descriptor rename in this slice).


### 2026-05-03 — Slice S3 (R2 larger mapping batch) completed
- Added four high-confidence playback/session-adjacent collaborator mappings using explicit `.source` anchors and call-path adjacency:
  - `com/eckom/xtlibrary/b/f/f/s` -> `.../TWMusic`
  - `com/eckom/xtlibrary/b/f/f/t` -> `.../TWMusicIikID3`
  - `com/eckom/xtlibrary/b/f/b/e` -> `.../MusicBean`
  - `com/eckom/xtlibrary/b/f/a/c` -> `.../ThreadPoolManager`
- Refined inventory tooling to de-prioritize already-mapped classes in the top-50 queue.
- Kept callback semantic promotions and notification/foreground ownership unresolved until stronger evidence.


### 2026-05-03 — Slice S4 (R2 model-cluster mapping batch) completed
- Added five high-confidence model/data-holder mappings in `b/f/b` + `b/f/f` clusters:
  - `a` -> `AlbumMedia`, `b` -> `ArtistMedia`, `c` -> `LMedia`, `d` -> `MediaFolderBean`
  - `b/f/f/c` -> `MusicDataHolder`
- Evidence used: explicit `.source` anchors, bean-like fields/getters, and parse/model call-path adjacency (`MusicMediaParseTask`, `F` helper path).
- Deferred inner class `b/f/a/b` (ThreadPoolManager inner `Handler`) because exact original inner-class name is not explicit (`InnerClass name = null`), keeping it for a later conservative inner-class naming pass.


### 2026-05-03 — Slice S5 (R2 helper/inner mapping pass) completed
- Added six high-confidence helper/inner mappings in `b/f/f` + plugin-inner surfaces:
  - `h` -> `MusicUtils`, `a` -> `CollectionUtils`, `b` -> `LrcTranscoding`, `l` -> `SPUtils`, `k` -> `PinyinConv`
  - `com/tw/music/c/a$a` -> `ListPlugin$PluginItem`
- Inner-class rule application: promoted only `ListPlugin$PluginItem` where non-null `InnerClass` metadata and role-bearing fields/getters were explicit.
- Kept anonymous/ambiguous helper inners (including `b/f/a/b` and most `MusicUtils` lettered inner helpers) deferred pending stronger non-speculative role evidence.


### 2026-05-03 — Slice S6 (R2 MusicUtils nested-callback batch) completed
- Added four high-confidence `MusicUtils` nested mappings with explicit non-null inner metadata and callback/task role signals:
  - `h$a` -> `MusicUtils$RecordCallback`
  - `h$f` -> `MusicUtils$StringCallback`
  - `h$b` -> `MusicUtils$RecordPathTask`
  - `h$e` -> `MusicUtils$LibraryScanTask`
- Deferred `h$c`/`h$d` because they remain semantically close and require additional call-site disambiguation before assigning distinct stable names.


### 2026-05-03 — Slice S7 (R2 deferred-helper disambiguation) completed
- Disambiguated and promoted four previously deferred/nearby helper classes using method-body role evidence:
  - `b/f/a/b` -> `ThreadPoolManager$QueueWatchHandler`
  - `b/f/f/d` -> `MusicUtils$ThreadFactoryImpl`
  - `b/f/f/i` -> `MusicUtils$AudioFileFilter`
  - `b/f/f/j` -> `MusicUtils$CollectingFileFilter`
- `h$c` and `h$d` remain deferred as distinct task semantics are still too close for stable naming without deeper call-site intent separation.


### 2026-05-03 — Slice S8 (R2 deeper task-trace + adjacent helpers) completed
- Completed deeper `h$c`/`h$d` call-site trace in `MusicUtils`:
  - `h$c` instantiated by `MusicUtils.a(record,path,flag,callback)` with callback setter `b(h$a)`
  - `h$d` instantiated by `MusicUtils.b(record,path,flag,callback)` with callback setter `a(h$a)`
- Because behaviour remains close, promoted conservative distinct names `RecordTaskA` / `RecordTaskB` rather than speculative semantic names.
- Added adjacent explicit-role helper threads from same cluster:
  - `f` -> `MusicUtils$PersistMusicStateThread`
  - `g` -> `MusicUtils$PersistLikesThread`



### 2026-05-03 — Slice S9 (R2 TWMusic anonymous helper batch) completed
- Continued from S8 with a larger reviewable batch focused on high-confidence anonymous helpers in `TWMusic` plus one adjacent `MusicUtils` comparator.
- Added seven confirmed mappings (class-level, mapping-only):
  - `e` -> `MusicUtils$NameComparator`
  - `m` -> `TWMusic$AudioCandidateFileFilter`
  - `n` -> `TWMusic$ExtSdRootFilter`
  - `o` -> `TWMusic$StorageRootFilter`
  - `p` -> `TWMusic$ExtSdAliasFilter`
  - `q` -> `TWMusic$UsbRootFilter`
  - `r` -> `TWMusic$PersistStateThread`
- Conservative naming maintained for duplicate `extsd` filters (`n`/`p`) to avoid speculative mount-role claims.



### 2026-05-03 — Slice S10 (R2 `b/a/*` high-confidence batch) completed
- Continued R2 from S9 with the next high-confidence non-`b/f/f` inventory cluster.
- Added 16 class-level mappings in `b/a/*` driven by explicit `.source` anchors and strong type-role signals:
  - `BroadcastManager`, `BroadcastManager$StringPairCallback`, `BroadcastManager$StateCallback`
  - `CommonData`, `LogUtil`, `TWAT`
  - `ContactDBHelper`, `ContactDBManager`, `DBHelper`
  - `BTModel`, `BTModelView`, `BaseBTModel`, `BuildInBTModel`
  - `BTPresenter`, `BTView`, `VoiceCallView`
- Kept callback/method semantics unresolved and mapping-only to avoid speculative behavior naming.



### 2026-05-03 — Slice S11 (R2 `b/h/*` radio cluster batch) completed
- Continued from S10 with inventory-top `b/h/*` candidates and delivered 8 confirmed class-level mappings:
  - `RadioDataHolder`, `FreqPs`
  - `RadioModel`, `RadioModelView`, `RadioPresenter`, `RadioView`
  - `TWRadio`, `TWRadio$Holder`
- Maintained conservative policy: class/interface-level mapping only; callback method semantics and anonymous/synthetic helper naming remain deferred.



### 2026-05-03 — Slice S12 (R2 `b/i/*` theme cluster batch) completed
- Continued from S11 into the next high-confidence inventory cluster after `b/h/*`.
- Added 14 confirmed class-level mappings in `b/i/*` (theme/plugin/runtime utility surface):
  - `DFLog`, `FileUtils`, `IThemeSwitchStatus`, `PluginContext`, `ReflectUtil`
  - `RunUtil`, `RunUtil$MainHandler`
  - `ThemeConfig`, `ThemeHelper`, `ThemeManager`, `ThemeManager$Holder`, `ThemePlugin`, `ThemeSwitchInfo`, `ThemeUtil`
- Maintained conservative mapping-only policy; deferred `ThemeManager` comparator inners (`k$b`,`k$c`,`k$d`) pending stronger semantic discrimination.

### Next stage pointer
- Continue R2 by evaluating the next post-`b/i/*` inventory cluster (likely `b/k/*` video presenter/view interfaces, then `b/j/*` utility set) and by selectively resolving deferred anonymous helpers only when role evidence is unambiguous.

## Accelerated closure mode

This section records the strategy shift from slow manual package-by-package
mapping toward tool-assisted candidate generation, bulk safe class promotion,
high-impact method review, tail classification, and targeted closure.

### Purpose

Finish practical readability closure in a smaller number of larger but still
reviewable passes.

Practical closure means:
- high-impact app-owned symbols are readable via confirmed mapping/name or
  explicitly blocked by runtime/device evidence;
- remaining unreadable symbols are classified as vendor/external,
  support/third-party, generated/synthetic, unsafe, runtime-only unresolved,
  unknown-low-impact, or high-impact unresolved;
- vendor/runtime contract surfaces remain unchanged;
- no speculative descriptor/method/field rename is merged.

### Tooling

- `tools/readability/02_generate_mapping_candidates.py`
  generates compact candidate reports from canonical smali and existing mappings.

- `tools/readability/03_promote_safe_class_mappings.py`
  dry-runs or applies strict safe class-level mappings only. It never edits smali.

- `tools/readability/04_high_impact_method_candidates.py`
  generates a review queue for method/interface boundary naming. It never renames.

- `tools/readability/05_tail_classification.py`
  classifies remaining unreadable symbols so low-value/synthetic/vendor/unsafe
  tail items can be closed deliberately.

- `tools/readability/06_diff_size_guard.sh`
  prevents oversized diffs and accidental binary/generated artifact commits.

### Accelerated pass sequence

1. Candidate generation and tail classification.
2. Bulk safe class-level promotion from strict `AUTO_CONFIRM_*` buckets.
3. High-impact method/interface/callback review only where evidence is strong.
4. Notification/widget/foreground targeted closure or runtime-blocked classification.
5. Final tail classification and verification.

### Guardrails

- No smali descriptor rename unless separately scoped and proven safe.
- No broad method/field sweep.
- No vendor token/runtime surface mutation.
- No raw JADX exports, APKs, DEX files, archives, or oversized generated reports.
- Reports must remain compact and reviewable.

## 2026-05-04 pass update — method/interface/callback boundary slice

### What this slice did
- Executed a constrained high-impact method boundary review using `readability-high-impact-methods.md`.
- Prioritized app-owned boundary owners and avoided method-sweep renames.
- Kept focus on evidence-backed working labels and deferrals.

### Grouped high-impact boundary owners (this slice)
- **Presenter/model/view contracts:** `MusicPresenter`, `MusicModelView`, and concrete model publishers (`MusicIjkModel`, `MusicModel`, `MusicIjkID3Model`, `MusicID3Model`).
- **Callback/listener contracts:** `BTModelView`, `RadioModelView`, `VideoView`, `LauncherView` (reviewed, deferred for method naming).
- **Handler/runnable/task boundaries:** high-signal `handleMessage`/`run`/`doInBackground` methods in mapped task/helper owners.
- **Lifecycle boundaries:** `MusicService`, `MusicActivity`, command receivers.
- **Media/session/playback publication candidates:** retained existing `MusicModelView` working labels without promoting additional uncertain names.

### Unresolved queue triage adjustment (policy)
- `antlr/*` and analogous third-party/library symbols are confirmed low-priority support debt for this phase and should not be treated as high-impact app-owned readability blockers.
- Keep unresolved queue focused on app-owned high-impact boundaries plus explicitly-blocked runtime-owner questions.

### Next pass recommendation
- Continue method-boundary readability for app-owned callback interfaces (BT/Radio/Video/Launcher) with targeted call-site tracing.
- Do **not** pivot yet to notification/widget/foreground ownership until additional static evidence or runtime capture path is prepared.

## 2026-05-04 pass update — bounded interface-family completion slice

- Shifted from broad note-only review to a concrete callback contract slice with explicit caller/implementer evidence.
- Completed compact method-boundary table for:
  - `LauncherView`
  - `VideoView`
  - `RadioModelView`
  - `BTModelView`
- Output artifact:
  - `docs/reports/readability-method-boundary-review.md`

### Result
- Resolved a focused set of high-signal labels (not descriptor renames) where payload type and forwarding chain are explicit.
- Captured medium/low-confidence remainder with exact missing evidence requirements.

## 2026-05-04 pass update — multi-family method-boundary closure batch

- Completed a larger bounded pass across 4 target families (`BTModelView`, `RadioModelView`, `VideoView`, `LauncherView`).
- Added a 25-row concrete evidence table with statuses (`confirmed/deferred/closed`) and explicit blockers.
- Met closure-value goal by combining:
  - confirmed labels for high-signal channels;
  - grouped deferred channels with exact missing evidence;
  - explicit family closure status for Launcher.

### Immediate next tactical step
- Continue method boundaries (not notification pivot yet) by resolving deferred BT/Radio integer-channel semantics using branch-level event-code mapping in model classes, then sink-side UI assignment confirmation.

## 2026-05-04 pass update — queue metric shift

- Introduced persistent triage ledger (`readability-method-boundary-review.tsv`) to avoid rediscovering the same queue every pass.
- Primary progress KPI is now `remaining_actionable` from the method-candidate generator output.
- Families can be left visible in raw totals while still considered reviewed when ledger status is `confirmed/closed/deferred_static/runtime_needed/low_value/generated_or_bridge`.
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

## 2026-05-06 PR#22 readiness finalisation note

- This update is a **readiness/finalisation patch** for the PR#22 continuation thread of PR#18 method-boundary readability work.
- Merge path is explicitly: **PR#23 -> PR#22 -> PR#18**.
- No smali/resource/vendor runtime contract surfaces are changed by this readiness patch; it is ledger/doc consistency only.
- Android/JADX tooling is **not required** for this readiness patch; fast Python/Git/Bash checks are sufficient.

Current closure state for method-boundary queue:
- Closed families (active=0): Launcher, Video, Music, Radio.
- Remaining active families: BT and Unknown app-owned (as reported by `readability-high-impact-methods.md` in this branch state).
- Remaining blockers are representative callback/event semantics requiring additional static branch evidence and (if needed) targeted runtime traces.
