# Media Controls Acceptance Tests (Design Phase)

## Test preconditions
- Target device: TS18 Android 13 profile.
- Existing command surfaces (`com.tw.music.action.*`) already functional before bridge integration.
- Use service + widget + launcher + hardware key ingress for parity validation.

## Static acceptance tests
1. Build/static checks pass with bridge classes added.
2. Source-shim classpath gate passes (`scripts/build_source_shim.sh`) with required support classes verified before compile.
3. No changes to protected action strings, AIDL tokens, `persist.tw.*`, `persist.media.*`.
4. No package/shared UID changes (`com.tw.music`, `android.uid.system`).
5. Existing presenter/model command path remains primary execution route.
6. Runtime bridge class presence gate proof is available before wiring:
   - `grep -Rn "Lcom/tw/music/media/MediaControlBridge;" app/apktool/smali*`

## Runtime acceptance tests (post-implementation phase)
1. Session visibility:
   - `dumpsys media_session` shows app session owner and stable state transitions.
2. Command parity:
   - prev/play-pause/next from notification, widget, and hardware all hit same transport behavior.
3. Metadata parity:
   - title/artist/album/duration/artwork update correctly across track changes and source switches.
4. Progress parity:
   - reported position/duration are coherent and monotonic during playback.
5. Widget/launcher parity:
   - existing widget and launcher progress updates remain correct with bridge enabled.
6. External consumer parity:
   - TLink / CarPlay / Android Auto still read valid playback state and metadata.

## Negative and resilience tests
1. Missing/malformed metadata does not break session publication.
2. Service restart/rebind does not leak stale session or notification state.
3. Source-change edge cases (USB/SD/BT handoff) do not desync session state from actual playback.
4. No inferred foreground-owner assumptions accepted without runtime proof.

## Evidence collection checklist (post-implementation)
- Capture `dumpsys media_session` snapshots for play/pause/next transitions.
- Capture notification action behavior with timestamps.
- Capture widget/launcher update behavior during track seek/skip.
- Record unresolved anomalies into `docs/reports/readability-method-boundary-review.md` follow-up notes.


## Implementation-status note (2026-05-07)
- Current branch provides Level 2.5 scaffold (source-shim + guarded build path + smali integration gate checks).
- Runtime acceptance section remains pending TS18 execution evidence; this pass is static callback wiring for metadata/play-state and cached progress-state publish are wired statically; runtime parity evidence remains pending.
- No runtime pass/fail claims are made in this update.
- Phase C audit update: Gate S passes in this branch: runtime bridge smali exists in `app/apktool/smali_classes3/com/tw/music/media/*` and bridge descriptor grep returns hits.

- Runtime class presence gate must pass (`app/apktool/smali*/com/tw/music/media/*`) before any `MusicService` bridge hook commit.


## Static validation prerequisite

Before Gate V/RC claims, run `python3 tools/smali/validate_smali_static.py` as part of the required static validation suite documented in `docs/migration-blueprint.md`.
