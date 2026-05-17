# Playback/Control Path Symbol Map

## Scope and evidence surfaces
- Canonical implementation: `app/apktool/smali_classes3/com/tw/music/` and `app/apktool/smali_classes3/com/eckom/xtlibrary/`.
- Read-only evidence helpers: `reference/firstparty-jadx/`, `reference/vendor-jadx/`, `reference/jadx-raw/`, `reference/jadx-aliased/`.
- Method-boundary ledger: `docs/reports/readability-method-boundary-review.tsv`.

## First-party classes and working labels
- `com/tw/music/MusicService` (service command ingress + callback sink bridge).
- `com/tw/music/MusicActivity` (UI ingress and presenter-bound transport operations).
- `com/tw/music/MusicApplication` (app scope init/context owner).
- `com/tw/music/AudioPreview` (preview path owner; not main transport route).
- `com/tw/music/view/MusicWidgetProvider` (widget command + metadata projection).
- `C0769a` -> `MusicInfo` / PlaybackInfo working label.
- `C0781k` -> `MusicCommandReceiver` (broadcast command ingestion).
- `C0780j` -> `LauncherProgressReceiver`.
- `ServiceConnectionC0774d` -> `MusicServiceConnection`.
- `C0767c` -> `MusicListAdapter`.
- `C0773c` -> `MusicThemeInfo`.

## Vendor/model/playback layer classes
- `C0635a` -> `MusicPresenter` (`com/eckom/xtlibrary/b/f/e/a`).
- `AbstractC0607a` -> `BaseMusicModel` (`com/eckom/xtlibrary/b/f/d/a`).
- `C0593L` -> `MusicIjkID3Model` (`com/eckom/xtlibrary/b/f/d/L`).
- `C0601U` -> `MusicIjkModel` (`com/eckom/xtlibrary/b/f/d/U`).
- `C0610ba` -> model variant (`MusicModel` working label).
- `C0628t` -> model variant (`MusicID3Model` working label).
- `C0654s` -> `TWMusic` (`com/eckom/xtlibrary/b/f/f/s`).
- Engine/interop surfaces: `TWMediaPlayer`, `TWMediaPlayerView`, `IjkMediaPlayer`.

## Control ingress map (smali-owned path)
- `MusicActivity` click/key routing -> presenter transport methods.
- `MusicService.onStartCommand(Intent,...)` handles `com.tw.music.action.cmd/.prev/.next/.pp`.
- `MusicCommandReceiver.onReceive(...)` mirrors command actions and delegates to service/presenter.
- `MusicWidgetProvider` `PendingIntent` actions route into same `com.tw.music.action.*` surfaces.
- Launcher progress route enters through `C0780j`/launcher receiver path.

## Metadata/state egress map
- Model callbacks publish via `MusicModelView` interface (`com/eckom/xtlibrary/b/f/g/a`).
- Service/activity sinks update UI projection and widget state.
- `MusicWidgetProvider` refresh path consumes playback metadata updates.
- Future MediaSession/notification publication point is documented, not implemented in this pass.

## Method semantics register (working/evidence-backed)
- Control intents: `play`, `pause`, `playPause`, `previous`, `next`, `seekTo`.
- Projection callbacks: `updateMetadata`, `updateProgress`, `updateWidget`.
- Library/state: `selectTrack`, `scanLibrary`, `changeSource`, `setRepeatShuffle`.
- Metadata getters: `getCurrentPosition`, `getDuration`, `getTitle`, `getArtist`, `getAlbum`, `getAlbumArt`, `getPath`.

## Remaining evidence gaps
- Exact ownership for foreground/notification session publication remains runtime-needed.
- Where confidence is medium, labels stay working-label only and remain tracked in ledger status (`deferred_static` / `runtime_needed`).
