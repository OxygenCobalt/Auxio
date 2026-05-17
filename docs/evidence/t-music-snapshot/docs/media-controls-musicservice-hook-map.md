# Media Controls MusicService Hook Map (Gate M Readiness)

## Scope
Gate M tracking map with Stage 1 lifecycle wiring status and remaining callback-stage items.

## Evidence used
- `app/apktool/smali_classes3/com/tw/music/MusicService.smali`
- `reference/firstparty-jadx/` (read-only cross-check)
- `docs/playback-architecture.md`
- `docs/playback-symbol-map.md`

## Hook map

| Method / signature | Current purpose | Planned bridge interaction | Safe insertion point | Register/field delta forecast | Risk | Validation check |
|---|---|---|---|---|---|---|
| `onCreate()V` | Service startup: presenter helper init (`Pa`), widget singleton capture (`Qa`), TW command receiver registration for `com.tw.music.action.cmd/.prev/.next/.pp`. | Implemented Stage 1: instantiate `Lcom/tw/music/media/MediaControlBridge;` after existing receiver registration and store to service field (bridge ctor handles session activation). | After receiver registration (or immediately after `Qa` init), preserving existing action filter/receiver flow unchanged. | Add one service field for bridge instance; increase `.locals` as needed for new object construction/calls. | Medium | Smali diff keeps existing action strings and receiver registration unchanged; grep finds bridge type refs after import+wiring. |
| `onDestroy()V` | Teardown: unregister receiver, clear `Pa`, call `BaseMusicService.onDestroy()`. | Implemented Stage 1: release bridge (`bridge.release()`), null bridge field, then continue existing teardown semantics. | Inside try block before `iput-object v0, ...->Pa` or directly after it, before `invoke-super`. | Reuse existing `v0`/add temp register depending on patch shape; add bridge field nulling. | Medium | No exception-path regression; receiver unregister still executes; bridge release called exactly once. |
| `onStartCommand(Landroid/content/Intent;II)I` | Authoritative transport command ingress (`cmd` extra and explicit `.prev/.next/.pp` actions), delegates to presenter methods. | **No direct behavior change expected**; bridge callbacks must continue dispatching back into this existing TW command path. | None for Gate M minimal wiring unless strictly required for parity logging. | Usually none. | Low | Action parity retained for widget/hardware/bridge callbacks; no token drift in action literals. |
| `a(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;Ljava/lang/String;I)V` | Metadata callback sink: forwards to base service and triggers widget refresh via `Qa.a(this)`. | Map args through `MediaMetadataMapper.map(...)` then `bridge.publishMetadata(...)`; keep widget refresh call. | After `invoke-super/range {p0 .. p7}` and before/after existing widget update call (must preserve widget update). | `.locals` increase likely required; temporary registers for mapper result and bridge object load. | Medium | Widget refresh still occurs; metadata publication uses existing callback payload; no null deref if bridge absent. |
| `a(Ljava/lang/Boolean;)V` | Play-state callback sink: forwards to base and updates helper (`Pa.H(boolean)`). | Build `PlaybackStateCompat` via mapper with latest known playback/progress signals; call `bridge.publishState(...)` and `bridge.updateNotification(isPlaying)`. | After existing `Pa.H(...)` path, before return, guarded by bridge null check. | `.locals` increase likely; no mandatory new persistent field if current state available; otherwise may require small state cache fields in future PR. | Medium-High | Existing helper update remains intact; no behavior change to presenter/model path; notification update toggles only from callback-driven state. |
| `d(II)V` | Progress callback sink: forwards to base and refreshes widget via `Qa.a(this)`. | Publish playback state from progress ticks using cached last-known playing state updated by `a(Boolean)`; retain widget refresh call unchanged. | After `invoke-super {p0,p1,p2}` and around existing widget refresh, with bridge null guard. | `.locals` increase likely for long conversion and mapper invocation. | Medium | Widget update still fires; progress-to-session publication uses cached last-known playing flag and callback progress arg. |

## Gate constraints for future PR
- Gate S must be PASS first: runtime bridge smali present under `app/apktool/smali*/com/tw/music/media/` with static grep proof.
- Gate M Stage 1 + callback-stage static wiring are complete; TS18 runtime evidence is still required for Gate V.
- TW command ingress path in `onStartCommand` remains authoritative.
