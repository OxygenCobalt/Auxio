# Readability Method Boundary Review

Generated: 2026-05-04 UTC

Scope: method-boundary closure batch for BT / Radio / Video / Launcher interface families.

| Boundary family | Owner/interface descriptor | Mapped owner | Obfuscated method signature | Caller(s) | Implementer/sink(s) | Payload/argument meaning | Working label | Confidence | Exact evidence | Status | Next evidence required |
|---|---|---|---|---|---|---|---|---|---|---|---|
| Launcher | `Lcom/eckom/xtlibrary/b/d/c/a;` | `LauncherView` | `b(Landroid/os/Bundle;)V` | `LauncherPresenter->b(Bundle)` | `BaseLauncherActivity` | launcher payload bundle passed from presenter to view | `onLauncherPayload(Bundle)` | High | single-method interface; presenter performs direct `invoke-interface` to `LauncherView->b(Bundle)` | Confirmed | — |
| Launcher | `Lcom/eckom/xtlibrary/b/d/b/a;` | `LauncherPresenter` | `b(Landroid/os/Bundle;)V` | model callback into presenter | `LauncherView` | presenter forwarding boundary entrypoint | `forwardLauncherPayload(Bundle)` | High | presenter `b(Bundle)` checks `get()!=null` and forwards unchanged to view interface | Closed | family closed unless model introduces new interface methods |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `onMediaView(Landroid/view/View;)V` | `VideoPresenter->onMediaView(View)` | `BaseVideoActivity`, `BaseMusicService` | media view attach/bind callback | `onMediaView(View)` | High | readable method name + direct presenter forwarding to interface | Confirmed | — |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `d(II)V` | `VideoPresenter->d(int,int)` | `BaseVideoActivity`, `BaseMusicService` | paired progress/index-like ints | `publishVideoProgressPair(int,int)` | Medium | direct pass-through from presenter; two-int channel unresolved beyond structural role | Deferred | inspect sink logic in BaseVideoActivity for each arg usage |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `a(Ljava/lang/Boolean;)V` | `VideoPresenter->c(boolean)` | `BaseVideoActivity`, `BaseMusicService` | boxed boolean state callback | `publishVideoStateBoolean(Boolean)` | Medium | presenter explicitly boxes boolean then invokes `a(Boolean)` | Deferred | identify true/false semantics by sink branch constants |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `fa(Ljava/lang/String;)V` | `VideoPresenter->fa(String)` | `BaseVideoActivity`, `BaseMusicService` | single text field callback | `publishVideoPrimaryText(String)` | Medium | direct forwarding of one string channel | Deferred | disambiguate title/path/message at sink |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `u(Ljava/lang/String;Ljava/lang/String;)V` | `VideoPresenter->u(String,String)` | `BaseVideoActivity`, `BaseMusicService` | dual-string metadata pair | `publishVideoTextPair(String,String)` | Medium | two-string pass-through channel from presenter to view | Deferred | resolve argument order by sink field assignment |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `q(I)V` | `VideoPresenter->setSource(int)` maps to `q(I)` | `BaseVideoActivity`, `BaseMusicService` | source selector integer | `publishVideoSource(int)` | Medium-High | method name `setSource(I)` in presenter forwards to `VideoView->q(I)` | Confirmed | — |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `l(I)V` | `VideoPresenter->u(I)` maps to `l(I)` | `BaseVideoActivity`, `BaseMusicService` | single int state/update | `publishVideoIndex(int)` | Medium | presenter rename alias indicates index-like setter to view callback | Deferred | sink-side variable target confirmation |
| Video | `Lcom/eckom/xtlibrary/b/k/c/c;` | `VideoView` | `f(Z)V` | `VideoPresenter->f(boolean)` | `BaseVideoActivity`, `BaseMusicService` | boolean toggle channel #1 | `publishVideoToggleF(boolean)` | Low-Medium | forwarding is clear; semantic meaning is not | Deferred | map to UI/state fields in sink |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `a([Lcom/eckom/xtlibrary/b/h/a/a;)V` | `RadioModel` helper callbacks | `RadioPresenter` then `RadioView` | station list (`FreqPs[]`) payload | `publishStationList(FreqPs[])` | High | DTO type is mapped `FreqPs[]`; presenter forwards unchanged | Confirmed | — |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `a(Landroid/graphics/drawable/Drawable;)V` | `RadioModel` helper callbacks | `RadioPresenter` then `RadioView` | radio icon/art payload | `publishRadioDrawable(Drawable)` | High | framework Drawable payload + direct callback path | Confirmed | — |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `ba(Ljava/lang/String;)V` | `RadioModel`, `RadioModel` helpers | `RadioPresenter` then `RadioView` | primary radio text channel | `publishRadioPrimaryText(String)` | Medium | frequent string callback from model to presenter to view | Deferred | separate PS/RDS/station-name channels by sink mapping |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `ha(Ljava/lang/String;)V` | `RadioModel` / helpers | `RadioPresenter` then `RadioView` | secondary radio text channel | `publishRadioSecondaryText(String)` | Medium | second distinct string callback family exists | Deferred | identify text source constants in model branches |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `r(I)V` | `RadioModel` + helpers | `RadioPresenter` then `RadioView` | selected index/frequency-like int channel | `publishRadioSelection(int)` | Medium | used as single-int callback in multiple model paths | Deferred | correlate with TWRadio event id constants |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `s(I)V` | `RadioModel` + helpers | `RadioPresenter` then `RadioView` | single-int update channel #2 | `publishRadioIndexS(int)` | Medium | numerous model callsites to `s(I)` show stable integer channel | Deferred | sink-side differentiation from `r(I)` |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `x(I)V` | `RadioModel` | `RadioPresenter` then `RadioView` | single-int update channel #3 | `publishRadioValueX(int)` | Low-Medium | pass-through clear but meaning unresolved | Deferred | event-code mapping in RadioModel switch blocks |
| Radio | `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `y(I)V` | `RadioModel` | `RadioPresenter` then `RadioView` | single-int update channel #4 | `publishRadioValueY(int)` | Low-Medium | pass-through clear but semantic ambiguity persists | Deferred | same as above |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(Ljava/util/ArrayList;)V` | `BTModel`, `BTModel$AsyncTask` (`f$a`) | `BTPresenter` then `BTView` | primary list payload channel | `publishBtPrimaryList(ArrayList)` | Medium | presenter forwards list callback directly; multiple model callsites | Deferred | inspect list element type construction sites |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `b(Ljava/util/ArrayList;)V` | `BTModel$AsyncTask` (`f$a`) | `BTPresenter` then `BTView` | secondary list payload channel | `publishBtSecondaryList(ArrayList)` | Medium | distinct list callback method called from async completion | Deferred | determine semantic split vs `a(ArrayList)` |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(ILjava/lang/String;)V` | `BTModel` event branches | `BTPresenter` then `BTView` | event code + text payload | `publishBtCodeText(int,String)` | Medium | stable `(int,String)` signature from model interface invokes | Deferred | map integer domain from BTModel switch/if branches |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(ILjava/lang/String;Ljava/lang/String;)V` | `BTModel` event branches | `BTPresenter` then `BTView` | event code + 2-text payload | `publishBtCodeTextPair(int,String,String)` | Medium | three-arg structured channel appears at multiple BT model sites | Deferred | confirm each text field role |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` | `BTModel` event branches | `BTPresenter` then `BTView` | triple-string payload channel | `publishBtTripleText(String,String,String)` | Low-Medium | consistent signature, no obvious constant naming nearby | Deferred | requires branch-local string ownership evidence |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `j(Z)V` (to view sink `o(Z)` in BTView) | `BTModel` event branches | `BTPresenter` then `BTView` | boolean event/toggle channel | `publishBtToggleJ(boolean)` | Low-Medium | presenter forwards `j(Z)` to BTView `o(Z)` showing alias mapping | Deferred | inspect BTView sink implementation semantics |
| BT | `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `Q(I)V` | `BTModel` event branches | `BTPresenter` then `BTView` | integer event/status channel | `publishBtStatusQ(int)` | Low-Medium | integer callback with repeat callsites in BTModel | Deferred | correlate with event-code constants in BTModel |

## Family closure summary

- **Launcher family:** closed for current method-boundary scope (single callback contract already high-confidence).
- **Video family:** partially closed (view attach/source callbacks confirmed; most state channels still deferred).
- **Radio family:** partially closed (list/drawable confirmed; text/index/toggle channels grouped with explicit deferrals).
- **BT family:** partially closed (list and structured payload channels grouped; event-code semantic mapping still pending).

## High-signal deferred blockers

- event code integer propagated but no constant/string owner found in reviewed branch slices (BT + Radio int channels);
- multiple callbacks share same payload types and require sink-side `Base*Activity` field/UI assignment mapping;
- a few channels likely require runtime trace to finalize semantic names without speculation.

## Durable triage ledger

Machine-readable source of truth for queue reduction:
- `docs/reports/readability-method-boundary-review.tsv`

Future passes should optimize for **remaining_actionable** from `readability-high-impact-methods.md`, not raw candidate count.
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

### 2026-05-04 Music representative closure table
| Descriptor | Mapped Owner | Method | Status | Working label | Caller/Sink evidence | Reason | Missing evidence |
|---|---|---|---|---|---|---|---|
| `Lcom/eckom/xtlibrary/b/f/d/F;` | `MusicIjkID3Model` | `handleMessage(Message):Z` | `reviewed_but_grouped` | `musicIjkId3Dispatch` | model handler dispatch chain | grouped with sibling model handlers | none for queue closure pass |
| `Lcom/eckom/xtlibrary/b/f/d/O;` | `MusicIjkModel` | `handleMessage(Message):Z` | `reviewed_but_grouped` | `musicIjkDispatch` | model handler dispatch chain | grouped representative callback family | none for queue closure pass |
| `Lcom/eckom/xtlibrary/b/f/d/V;` | `MusicModel` | `handleMessage(Message):Z` | `reviewed_but_grouped` | `musicModelDispatch` | model handler dispatch chain | grouped representative callback family | none for queue closure pass |
| `Lcom/eckom/xtlibrary/b/f/d/L;` | `MusicIjkID3Model` | `seekTo(I):V` | `simple_delegate` | `seekDelegateIjkId3` | presenter/model call-through | delegate to playback sink | none |
| `Lcom/eckom/xtlibrary/b/f/e/a;` | `MusicPresenter` | `seekTo(I):V` | `simple_delegate` | `presenterSeekDelegate` | UI -> presenter -> model path | wrapper delegate | none |
| `Lcom/tw/music/MusicActivity;` | `MusicActivity` | `dispatchKeyEvent(KeyEvent):Z` | `reviewed_but_grouped` | `activityKeyDispatch` | activity key routing path | grouped UI event boundary | none for this static pass |

### 2026-05-04 Radio representative closure table
| Descriptor | Mapped Owner | Method | Status | Working label | Caller/Sink evidence | Reason | Missing evidence |
|---|---|---|---|---|---|---|---|
| `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `ba(String):V` | `reviewed_but_grouped` | `radioTextChannel` | model->presenter->view callback family | grouped with sibling text/index/bool callbacks | none for queue closure pass |
| `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `r(I):V` | `reviewed_but_grouped` | `radioIndexChannel` | model->presenter->view callback family | grouped representative of index/int channels | none for queue closure pass |
| `Lcom/eckom/xtlibrary/b/h/b/f;` | `RadioModelView` | `e(Z):V` | `reviewed_but_grouped` | `radioToggleChannel` | model->presenter->view callback family | grouped representative of bool toggles | none for queue closure pass |

## BT final active-row closure

Representative BT callbacks were collapsed from per-method unresolved rows into grouped-reviewed entries where the boundary semantics are duplicate event-channel delegates with no additional static signal. Remaining BT active rows are the seven representative payload/event channels that still need branch-local event-code/source mapping.

| descriptor | mapped owner | method signature | family | status | evidence | missing evidence | why active |
|---|---|---|---|---|---|---|---|
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `Q(I)V` | BT | `deferred_static` | repeated integer callback across BTModel branches into presenter->view | correlate branch event-code constants | semantic label not safe from signature alone |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(ILjava/lang/String;)V` | BT | `deferred_static` | stable `(int,String)` callback shape | map integer domain from BTModel switch/if paths | code+text meaning unresolved |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(ILjava/lang/String;Ljava/lang/String;)V` | BT | `deferred_static` | structured int+2-string callback used multiple times | confirm role of each string argument | avoids speculative field naming |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `a(Ljava/util/ArrayList;)V` | BT | `deferred_static` | model/async list callback forwarded by presenter | inspect element construction type | list semantic unclear |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `b(Ljava/util/ArrayList;)V` | BT | `deferred_static` | distinct secondary list callback | split meaning vs primary list callback | requires list producer inspection |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V` | BT | `deferred_static` | consistent triple-string channel in BTModel branches | branch-local ownership/source evidence | text roles ambiguous |
| `Lcom/eckom/xtlibrary/b/a/d/g;` | `BTModelView` | `j(Z)V` | BT | `deferred_static` | presenter forwards to BTView `o(Z)` | inspect sink-side boolean handling | toggle semantics not yet proven |

## Unknown app-owned final triage

No active `Unknown app-owned` rows remain in the **method-boundary review ledger** after this pass. However, the `Unknown app-owned` family remains active with 22 unresolved rows in the generated triage queue, as tracked in `docs/reports/readability-high-impact-methods.md`.

## PR#22 readiness/finalisation context (2026-05-06)

- This is a final readiness patch for the PR#22 continuation work (originating from PR#18 readability-ledger closure).
- Merge order for this finalisation stream is: **PR#23 -> PR#22 -> PR#18**.
- No smali/resource/vendor runtime contract surfaces were changed in this patch.
- Android/JADX tooling is not required for this readiness check pass.

| Music | `Lcom/tw/music/k;` | `MusicCommandReceiver` | `onReceive(Context,Intent)` | `MusicService` broadcast intake | `MusicPresenter` command delegates | `com.tw.music.action.*` command routing | `dispatchMusicControlIntent` | High | action routing preserved and documented in closure docs | Confirmed | — |
