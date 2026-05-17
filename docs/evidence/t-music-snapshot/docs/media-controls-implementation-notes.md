# Media Controls Implementation Notes (Initial Bridge Scaffold)

> **Authority note (2026-05-07):** Live phase/gate readiness is tracked only in `docs/migration-blueprint.md`; this document provides supporting detail/evidence only.


## Implementation level
Level 2.5 — source-shim scaffold hardened with deterministic build/integration guard path; runtime smali wiring still deferred.

## Implemented in this branch
- Added source-shim bridge classes under `src-shim/java/com/tw/music/media/`:
  - `MediaControlBridge`
  - `MediaMetadataMapper`
  - `PlaybackStateMapper`
  - `MediaNotificationController`
- Preserved TW transport authority by delegating media control callbacks back to:
  - `com.tw.music.action.prev`
  - `com.tw.music.action.pp`
  - `com.tw.music.action.next`
  - optional `cmd` extra values (`prev`, `pp`, `next`) for existing command handling parity.

## Deliberately not implemented
- Minimal smali edits in `MusicService` are now applied for lifecycle-only bridge ownership.
- Runtime `MediaSessionCompat` lifecycle owner wiring is now present via `MediaControlBridge` create/release in `onCreate()/onDestroy()`.
- No claim of TS18 runtime pass for notification/MediaSession behavior.

## Inspected service hook points (smali evidence)
`app/apktool/smali_classes3/com/tw/music/MusicService.smali` contains these candidate hooks:
- `onCreate()` — safe place to initialize bridge and set active.
- `a(String,String,String,Bitmap,String,String,int)` — metadata callback sink; candidate for `MediaMetadataMapper.map(...)` publication.
- `a(Boolean)` — play/pause callback sink; candidate for state publication updates.
- `d(int,int)` — progress callback sink; state publication uses cached last-known playing state sourced from `a(Boolean)`.
- `onDestroy()` — safe place to release bridge resources.
- `onStartCommand(Intent,int,int)` — command ingress remains unchanged and authoritative.

## Exact planned smali hook patch (follow-up)
1. Add private field in `MusicService` for bridge owner instance.
2. `onCreate()`:
   - instantiate bridge with `this` service
   - call `setActive(true)`
3. In metadata callback:
   - read existing `MusicInfo` / callback args
   - map and publish via `MediaMetadataMapper`
4. In play/progress callbacks:
   - build `PlaybackStateCompat` using current play/progress flags
   - publish state + notification visibility update
5. `onDestroy()`:
   - release bridge before/around existing receiver teardown

## Runtime evidence still required (TS18)
- `dumpsys media_session` state transitions across play/pause/next/prev.
- Notification action parity with steering-wheel and widget controls.
- TLink/CarPlay/Android Auto metadata/readback parity.
- Foreground notification/channel behavior on the target firmware skin.

## Validation guidance
- Keep existing widget and `com.tw.music.action.*` flows untouched while integrating.
- Verify no changes to package identity, shared UID, or vendor tokens.


## Runtime class presence gate (this pass)
- Phase C audit rerun result: `app/apktool/smali_classes3/com/tw/music/media/` contains imported runtime bridge smali classes.
- `grep -Rn 'Lcom/tw/music/media/MediaControlBridge;' app/apktool/smali*` returns descriptor hits in bridge and service wiring paths.
- Gate S passed in this branch; `MusicService.smali` contains lifecycle wiring plus safe metadata/play-state callback publishing. Progress-callback state publishing is deferred to avoid forced false state on progress ticks.

## Source-shim integration path added
- Added `scripts/build_source_shim.sh` as a conservative guard script.
- Script behavior:
  - does not install/download tools or dependencies;
  - requires preinstalled `javac`;
  - requires caller-provided `ANDROID_JAR` and legacy support classpath (`SUPPORT_CLASSPATH`, `SUPPORT_AARS`, or fallback `SUPPORT_V4_JAR`);
  - verifies required legacy support classes before compile (`MediaMetadataCompat`, `MediaSessionCompat`, `PlaybackStateCompat`, `NotificationCompat`, `NotificationCompat.MediaStyle`);
  - compiles shim sources to local `.local/source-shim-build/classes`;
  - documents manual next step for verified class->smali import into `app/apktool/smali_classes*/com/tw/music/media/`.

## Current defer reason for smali integration
- Blocker: no validated source-shim-to-smali conversion toolchain path in-repo and no runtime bridge smali currently present.
- Safety decision: defer `MusicService` hook edits until bridge classes exist in canonical Apktool tree and can be referenced safely.

## Runtime bridge import gate
Before any `MusicService` bridge lifecycle wiring:
- Bridge runtime smali classes must exist under `app/apktool/smali*/com/tw/music/media/`.
- Static grep must find `Lcom/tw/music/media/MediaControlBridge;` via:
  - `grep -Rn "Lcom/tw/music/media/MediaControlBridge;" app/apktool/smali*`
- Imported bridge smali must not contain unresolved/missing support-library class references.
- No package/manifest/sharedUserId identity changes are allowed.
- No widget behavior-path changes or TW command-path changes are allowed.
- Only after all above pass can `MusicService` lifecycle hooks be added.

## Source-shim classpath note
- `scripts/build_source_shim.sh` now prefers `SUPPORT_CLASSPATH` (colon-separated multi-jar classpath) over a single `SUPPORT_V4_JAR` to cover split Support Library artefacts.
- Optional `SUPPORT_AARS` is supported for local-only extraction of `classes.jar` into `.local/source-shim-build/classpath/`.
- Runtime import gate is unchanged: do not wire `MusicService` until bridge smali exists in `app/apktool/smali*/com/tw/music/media/`.
