# TS18 requirements for Auxio-TS

## Product objective

Auxio-TS is a maintainable Android local music player for the TS18 head-unit ecosystem. It should use Auxio as the player/library/UI base and add a TS18 compatibility layer only where standard Android media APIs are insufficient.

## Primary functional requirements

### Local music playback

- Play local music stored on internal storage, SD card, and USB/media-mounted locations where Android storage APIs permit access.
- Preserve Auxio's library indexing, playlisting, metadata handling, ReplayGain, embedded cover support, and folder-awareness where compatible.
- Support FLAC playback as a first-class requirement, including testing realistic TS18/Android 10 device behaviour.
- Support common formats expected from Auxio/Media3/Android platform support: MP3, AAC/M4A, OGG/OPUS, FLAC, WAV where available.

### Standard Android media integration

- Provide a correct MediaSession exposed to external clients.
- Provide a media notification with play/pause/previous/next/seek where appropriate.
- Support media-button events from hardware, Bluetooth, headset, and system dispatch.
- Use correct audio focus handling and noisy-intent behaviour.
- Keep background playback in an appropriate service.
- Support Android Auto-style browsing/control where feasible via MediaBrowserService/MediaLibraryService architecture.

### TS18/TW integration

- Detect TS18/TW environment conservatively using non-sensitive package/property signals.
- Validate launcher/home music widget behaviour against stock `com.tw.music`.
- Validate steering wheel/media key behaviour.
- Validate interaction with `com.tw.service`, audio focus, and volume policy.
- Validate interaction with ZLink/TLink or Android Auto projection where present.
- Investigate TWTHEME resource coupling, especially `MusicTheme.apk` and launcher theme APKs.
- Preserve standard Android behaviour even if TS18-specific hooks are disabled.

## Non-functional requirements

- Maintainable by coding agents.
- Reproducible debug and release builds.
- Small, reviewable PRs.
- No proprietary TS18 APKs committed.
- No raw diagnostics or device serials committed.
- Clear separation of standard Android code and TS18-specific adapters.
- Every TS18 feature must have a validation record.

## Acceptance criteria for first working TS18 build

A build is not considered TS18-ready until all of these are recorded:

1. App installs on TS18 Android 10 without replacing stock `com.tw.music`.
2. Local MP3 and FLAC files play.
3. `dumpsys media_session` shows an active Auxio-TS session during playback.
4. Android media notification appears and controls playback.
5. Hardware/media keys or steering-wheel-equivalent keys are either working or documented as not visible to third-party apps.
6. TS18 launcher/home media widget behaviour is recorded against stock `com.tw.music` and Auxio-TS.
7. `dumpsys audio` records expected focus ownership/transitions during playback.
8. ZLink/TLink/Android Auto metadata/control behaviour is recorded.
9. Sleep/resume behaviour is tested.
10. Any behaviour gap is tracked as a TS18 integration issue, not hidden behind UI changes.
