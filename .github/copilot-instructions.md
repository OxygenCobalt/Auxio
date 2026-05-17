# Copilot instructions for Auxio-TS

## What this fork is for
Auxio-TS extends upstream Auxio toward TS18 head-unit compatibility while keeping Android/Media3 behaviour correct and maintainable.

## Core implementation stance
- This is an **Auxio fork**, not a ground-up replacement player.
- Preserve upstream playback/library/service architecture unless evidence proves a change is necessary.
- TS18 support belongs behind isolated adapter/facade boundaries.
- `cbkii/t-music` is a reference/evidence corpus, not the implementation base.
- Public TW/Topway repos are research inputs, not APIs to copy blindly.

## Evidence discipline
Classify each TS18/TW/TWTHEME claim as one of:
- **Observed**,
- **Inferred**,
- **Hypothesis**,
- **Requires TS18 validation**,
- **Unsupported**.

Never claim TS18 compatibility without reproducible TS18 runtime evidence.

## TS18 change approach
1. Validate baseline Android behaviour first.
2. Record stock `com.tw.music` vs Auxio-TS evidence.
3. Add isolated adapter/facade code for TS18-specific behaviour.
4. Gate TS18 features behind runtime detection/flags.
5. Validate on TS18 hardware before broad claims.

## Preserve from upstream
- Playback/library architecture.
- Media3/ExoPlayer lifecycle correctness.
- MediaSession + notification correctness.
- Local-library and FLAC support behaviour.

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
- Do not bypass evidence classification for TW/TWTHEME claims.
