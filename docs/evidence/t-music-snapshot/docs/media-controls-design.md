# Media Controls Bridge Design (Documentation Only)

This document is design-only. No MediaSession/notification runtime behavior is implemented in this branch.

## Targeted architecture
`MusicService`
-> initializes `MediaControlBridge`
-> bridge publishes `MediaSessionCompat`
-> bridge publishes `PlaybackStateCompat`
-> bridge publishes `MediaMetadataCompat`
-> bridge forwards notification projection to `MediaNotificationController`
-> command callbacks are delegated back into existing `com.tw.music.action.*` / `MusicPresenter` control path

## Planned source-shim paths (not yet created)
- `src-shim/java/com/tw/music/media/MediaControlBridge.java`
- `src-shim/java/com/tw/music/media/MediaNotificationController.java`
- `src-shim/java/com/tw/music/media/PlaybackStateMapper.java`
- `src-shim/java/com/tw/music/media/MediaMetadataMapper.java`

## Concrete hook points to preserve existing behavior
- Service lifecycle hook: create/release bridge with `MusicService` start/destroy lifecycle.
- Command ingress hook: media callbacks call existing presenter delegates (`prev/next/playPause/seek`).
- Metadata hook: map existing model callback payloads (`title/artist/album/duration/art/progress`) to compat metadata/state.
- Widget parity hook: widget updates remain sourced from existing service/model path; bridge must not become exclusive source.

## Compatibility and protection constraints
- Do not alter `com.tw.music.action.cmd/.prev/.next/.pp` semantics.
- Do not alter TW AIDL tokens (`com.tw.service.xt.aidl.*`).
- Do not alter `persist.tw.*` / `persist.media.*` access patterns.
- Do not replace `TWMediaPlayer`/Ijk playback engine in this phase.
- Preserve `android.uid.system` shared UID behavior.
- Preserve TLink / CarPlay / Android Auto external-read expectations on metadata/state surfaces.

## Explicit non-goals in this pass
- No notification style/action runtime implementation.
- No MediaSession lifecycle runtime implementation.
- No foreground-service ownership changes.
