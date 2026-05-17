# Playback Architecture (Readability-Closure Baseline)

## Evidence roots used for this map
- Canonical smali: `app/apktool/smali_classes3/com/tw/music/`, `app/apktool/smali_classes3/com/eckom/xtlibrary/`.
- Read-only cross-check: `reference/firstparty-jadx/` + `reference/vendor-jadx/`.
- Boundary ledger: `docs/reports/readability-method-boundary-review.{md,tsv}`.

## Component map
`MusicActivity`
-> binds/interacts with `MusicService`
-> delegates transport/UI commands to `MusicPresenter`

`MusicService`
-> receives command intents (`com.tw.music.action.*`)
-> routes transport control through presenter/model path
-> updates `MusicInfo` + widget projection sink

`MusicPresenter (C0635a)`
-> delegates to `BaseMusicModel (AbstractC0607a)`
-> fans into concrete models (`MusicIjkModel`, `MusicIjkID3Model`, `MusicModel`, `MusicID3Model`)

Model path
-> uses `TWMusic` / parser/state holders
-> owns callback publication to `MusicModelView`

Engine path
-> `TWMediaPlayer` / `IjkMediaPlayer` (+ `TWMediaPlayerView` UI attach point)

## Concrete control ingress paths
1. Activity button/key ingress:
   - `MusicActivity` onClick/dispatch routes.
2. Service intent ingress:
   - `MusicService.onStartCommand(...)` action dispatch for `.prev/.pp/.next/.cmd`.
3. Receiver ingress:
   - `MusicCommandReceiver (C0781k).onReceive(...)` command mirror path.
4. Widget ingress:
   - `MusicWidgetProvider` PendingIntent actions targeting same action strings.
5. Launcher ingress:
   - `LauncherProgressReceiver (C0780j)` route for progress/token update signaling.
6. Future insertion point (design-only):
   - media-button/session command adapter that reuses existing action/presenter path.

## Metadata/state egress paths
1. Model callback egress:
   - concrete model classes invoke `MusicModelView` callback methods for metadata/progress/state.
2. Activity UI egress:
   - callback sinks update title/artist/progress/play-state visuals.
3. Widget egress:
   - service/widget provider update path publishes current playback snapshot.
4. Launcher egress:
   - launcher progress/tokens receive playback progress projections.
5. Future egress (design only):
   - MediaSession metadata and system media notification publication.

## Protected vendor/TW surfaces (compat boundary)
- Broadcast actions: `com.tw.music.action.cmd`, `.prev`, `.next`, `.pp`.
- AIDL stack: `com.tw.service.xt.aidl.*`.
- Radio handoff: `com.tw.radio.*`.
- EQ launch: `com.tw.eq/.EQActivity`.
- Theme path/surface: `com.tw.music.theme`, `/data/tw/theme/default/Sub/MusicTheme.apk`.
- Properties: `persist.tw.ijk*`, `persist.media.*`.
- UID contract: `android.uid.system`.

## Implementation hook points for next phase (no implementation here)
- Service init hook: `MusicService` lifecycle initialization point for bridge owner.
- Command hook: wrapper that maps MediaSession callbacks -> existing presenter/action routes.
- Metadata hook: model callback sink where metadata/progress are already unified.
- Notification hook: service-owned controller tied to existing playback state publication.

## Runtime uncertainties (explicit)
- Active MediaSession owner in current runtime build is unconfirmed/absent by static audit.
- Foreground/notification owner class is unconfirmed/absent by static audit.
- PlaybackState/metadata fidelity and action semantics require TS18 device evidence.
- Shared UID update/rollback behavior requires target-device validation.
