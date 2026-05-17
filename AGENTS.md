# AGENTS.md — Auxio-TS development instructions

You are working on `cbkii/Auxio-TS`, a TS18-focused fork of Auxio. Treat this as Android media engineering plus automotive head-unit integration, not generic Android app development.

## Authoritative device target

The target device is a TS18 Android head unit captured in `diagnostics/redacted/ts18_device_profile.json`.

Known facts from diagnostics:

- Android 10 / API 29.
- Unisoc/SPRD `uis8581a2h10` / `s9863a1h10_Natv`, arm64-v8a.
- User/release-keys build, not a normal debug platform.
- Stock `com.tw.music` is installed under `/system/priv-app` and appears as UID 1000 in package listings.
- `com.tw.service`, `com.tw.service.xt`, `com.tw.eq`, `com.tw.bt`, `com.tw.video`, `com.tw.reverse`, `com.tw.net`, and related TW packages are present.
- `com.zjinnova.zlink` is the active phone-link package via `persist.phone_connect_app`.
- TWTHEME files exist under `/system/etc/theme/default/...`, including `Sub/MusicTheme.apk`.
- `dumpsys media_session` captured no active sessions at the time of diagnostics.
- `com.tw.service` owned audio focus and volume changes in the captured audio dump.

## Working posture

Separate these layers:

1. Standard Android media layer: Auxio/Media3/ExoPlayer, MediaSession, MediaLibraryService, notifications, audio focus, media keys.
2. TS18 native integration layer: TWTHEME, TW services, stock launcher/home music widget, ZLink/TLink, vendor key/audio behaviours.
3. Evidence/validation layer: dumpsys/logcat/scripts/manual test records.

Do not mix observations and inferences.

## Rules for agents

- Prefer small, evidence-driven PRs.
- Preserve upstream Auxio behaviour unless a TS18-specific feature flag gates a change.
- Keep TS18-specific code isolated behind clear package/module boundaries and runtime detection.
- Do not make broad rewrites of Auxio core playback, library indexing, or UI without a specific TS18 acceptance requirement.
- Do not port smali blindly from `com.tw.music` into Auxio.
- Do not use privileged/system permissions, shared UID, or package-name impersonation unless explicitly proven necessary and separately approved.
- Do not commit raw TS18 diagnostics, serial numbers, copied APKs, firmware blobs, logs with personal data, or proprietary binaries.
- Prefer official Android/AndroidX docs for standard media behaviours.
- Treat public TS/TW/TWTHEME community material as investigation leads, not final proof.
- Every TS18 feature must have a reproducible validation command or manual runbook.

## Required response format for coding agents

When completing a task, respond with:

1. Summary.
2. Files changed.
3. Behaviour changed.
4. TS18 assumptions used.
5. Validation commands run and results.
6. Remaining TS18 runtime checks.
7. Risks/blockers.
8. Next recommended task.

## STOP conditions

Stop and report rather than coding if:

- A change requires privileged/system signing or `android.uid.system`.
- Package replacement of `com.tw.music` is being proposed without runtime proof.
- A TS18 native contract is inferred only from names, not logs/static references/runtime behaviour.
- The implementation would break Android media notifications/MediaSession behaviour.
- The implementation requires committing proprietary APKs or raw firmware.
