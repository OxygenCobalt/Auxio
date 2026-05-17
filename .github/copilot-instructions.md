# Copilot instructions for Auxio-TS

## What this fork is for
Auxio-TS extends upstream Auxio toward TS18 head-unit compatibility while keeping Android/Media3 behaviour correct and maintainable.

## How this differs from upstream Auxio
- Adds TS18-focused validation, diagnostics, and integration planning.
- May add TS18 adapter modules **only when evidence shows standard Android integration is insufficient**.
- Should avoid invasive core rewrites.

## Preserve from upstream
- Playback/library architecture.
- Media3/ExoPlayer lifecycle correctness.
- MediaSession + notification correctness.
- Local-library and FLAC support behaviour.

## TS18 change approach
1. Validate baseline Android behaviour first.
2. Record stock `com.tw.music` vs Auxio-TS evidence.
3. Add isolated adapter/facade code for TS18-specific behaviour.
4. Gate TS18 features behind runtime detection/flags.

## Checks to run
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

## Do not do
- Do not change package to `com.tw.music`.
- Do not require privileged/system UID/permissions.
- Do not commit proprietary APKs/raw diagnostics/PII.
- Do not claim TS18 compatibility without reproducible TS18 evidence.
