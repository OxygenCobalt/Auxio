# Release Validation Results — 2026-05

Live status authority: `docs/migration-blueprint.md`. This file is an evidence log only. (TS18 Android 13 track)

Execution date: 2026-05-02 (UTC)

> **Authority note (2026-05-07):** Live phase/gate readiness is tracked only in `docs/migration-blueprint.md`; this file is validation evidence and must not be used as the active execution tracker.

## P0 Results

| P0 Test Case | Status | Evidence | Notes |
|---|---|---|---|
| Unsigned build reproducibility | PASS | `bash scripts/02_build_unsigned.sh` exited 0 and produced `dist/com.tw.music-unsigned.apk`. | Local environment run completed successfully. |
| Shared UID signing compatibility | FAIL (not executed) | No signing run performed in this pass (blocker: no keystore lineage and no target TS18 device attached to this container). | Requires keystore lineage and device-side install-over-existing test per `docs/manual-steps/02-release-signing.md`. |
| Play/pause/next/prev command handling | PASS (code-path audit) | `MusicService.onStartCommand` and `com/tw/music/k.onReceive` dispatch `prev/next/pp` to presenter methods (`rb/pb/ba/fa`). | Runtime hardware/UI verification still pending on TS18 device. |
| PlaybackState transitions | FAIL (not yet evidenced) | No runtime `dumpsys media_session` capture or instrumentation evidence in this pass (blocker: container has no attached Android runtime/device shell). | Requires on-device playback lifecycle capture. |
| Track metadata publication | PASS (code-path audit) | `MusicService.a(String,String,String,Bitmap,String,String,int)` forwards to superclass then widget update call. | Runtime metadata surface verification pending (system card + session observers). |
| Notification action correctness | FAIL (not yet evidenced) | No direct notification action execution evidence captured in this pass (blocker: on-device notification UI interaction unavailable in this environment). | Requires on-device interaction test with foreground notification controls. |
| Widget render + command actions | PASS (code-path audit) | `MusicService` instantiates `MusicWidgetProvider`; receiver handles `update` and forwards `appWidgetIds` to provider update method. | Visual/widget host verification still pending on target launcher (requires TS18 launcher runtime). |

## Additional baseline checks

- PASS: `bash scripts/01_refresh_reports.sh`
- PASS: `bash scripts/08_verify_vendor_tokens.sh` (all protected tokens found)
- PENDING (dev-path): Pixel 9a compat smoke validation requires device-side run of `bash scripts/11_pixel_validation_harness.sh` with the new `com.tw.music-pixel9a-compat-no-uid-vX.Y.Z.apk`.

## Evidence log snippets

- Build: Apktool build completed and wrote `/workspace/t-music/dist/com.tw.music-unsigned.apk`.
- Vendor boundary token scan: `Scanned files: 4383`, `Protected tokens: 21`, `All protected tokens found.`


- Static trace update: repository-wide symbol scan found no direct non-support-library `MediaSession`/`PlaybackState`/`MediaMetadata` API references in canonical smali; deeper owner mapping remains open in deobf notes.
- Stage-4 static owner trace: callback publication hotspots identified in model classes `U/ba/L/t` via `g/a->b(...)`, `c(Z)`, `D(I)`, `B(I)`, `d(II)` dispatch sites; direct framework MediaSession symbols still unresolved.
- Signature policy clarification: new project signatures are acceptable; current signing blocker is specific to install-over-existing lineage checks on target devices, not a requirement to preserve the original vendor certificate.
- Bounded uncertainty update: static tracing through callback sinks and service lifecycle classes did not surface direct non-support-library notification-builder or foreground-service owner methods; unresolved set is now explicitly bounded in deobf/mapping docs.
- Current plan status: Stage 4 remains partially closed (bounded), with runtime/device-only validation still blocked for notification/foreground/session proof.


## 2026-05-07 — Media controls bridge scaffold status
- Status: **Static-only update; runtime validation pending**.
- Achieved: Level 2 scaffold (source-shim classes and integration documentation).
- Not yet validated: TS18 runtime MediaSession visibility, MediaStyle notification behavior, TLink/CarPlay/AA consumer parity.
- No release-go decision changed by this commit.


## 2026-05-07 (pass 2) — Media controls integration gate status
- Status: **Level 2.5**, static-only.
- Runtime class presence gate: failed (`app/apktool/smali*/com/tw/music/media/*` absent).
- Result: `MusicService` runtime hooks intentionally unchanged for safety.
- Added deterministic guarded build-path script: `scripts/build_source_shim.sh` (requires preinstalled `ANDROID_JAR` + `SUPPORT_V4_JAR`; no installs/downloads).

## 2026-05-07 (Phase C audit) — Runtime bridge import audit status
- Status: **Gate S PASS** (static-only audit + lifecycle wiring landed).
- Audit result:
  - `find app/apktool/smali_classes3/com/tw/music/media -type f -name '*.smali'` returned the expected runtime bridge smali file set.
  - `grep -Rn 'Lcom/tw/music/media/MediaControlBridge;' app/apktool/smali*` returned descriptor hits including bridge class and `MusicService` field/use sites.
- Support-library runtime classes (`android.support.v4.media/session/app`) are present in canonical smali tree, but bridge runtime class presence criterion is not met.
- Gate M Stage 1 lifecycle wiring **was** applied and callback-stage static wiring is now present: metadata callback publishes via `MediaMetadataMapper`; safety pass fixed metadata mapper register staging in `MusicService` and restored progress-callback publishState using cached last-known playing flag from `a(Boolean)`; forced false state publication is removed. TS18 runtime validation remains required.


## Static validation prerequisite

Before Gate V/RC claims, run `python3 tools/smali/validate_smali_static.py` as part of the required static validation suite documented in `docs/migration-blueprint.md`.
