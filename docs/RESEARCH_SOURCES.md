# Research sources and how to use them

## Official Android sources

- Android Media3 MediaSession docs: https://developer.android.com/media/media3/session/control-playback
  - Use for how playback is advertised externally and how external commands reach the app.
- Android Media3 MediaLibraryService docs: https://developer.android.com/media/media3/session/serve-content
  - Use for Android Auto-style media browsing and library exposure.
- Android for Cars media overview: https://developer.android.com/training/cars/media
  - Use for MediaBrowserService/MediaSession requirements for Android Auto/AAOS clients.
- Android platform supported media formats: https://developer.android.com/media/platform/supported-formats
  - Use for platform-level format expectations and API-version caveats.
- Media3 ExoPlayer supported formats: https://developer.android.com/media/media3/exoplayer/supported-formats
  - Use for ExoPlayer-specific playback support.

## Auxio sources

- Upstream Auxio: https://github.com/OxygenCobalt/Auxio
  - Auxio is a local Android music player built on Media3 ExoPlayer, with Android Auto support, FLAC-related ReplayGain support, widgets, SD-card-aware folder management, and GPLv3-or-later licensing.
- F-Droid Auxio: https://f-droid.org/packages/org.oxycblt.auxio/
  - Use as a public release/package reference.

## TS18/TW/TWTHEME sources

- TS18 product manual PDF: https://fcc.report/FCC-ID/2BECX-TS18/7046050.pdf
  - Confirms TS18 desktop/media surfaces, music app, TLink, and sound settings at a user-manual level.
- Manuals+ TS18 manual conversion: https://manuals.plus/m/100e1bccacb705c3706092d6bfeeef8c14ca3e25f1f4a6ff99bdfbd99f759348
  - Convenient text view; verify against the PDF if precision matters.

## TW/Topway community code leads

Treat these as investigation leads, not authoritative docs:

- `ivvlev/CarRadio`: https://github.com/ivvlev/CarRadio
  - Mentions `android.tw.john.TWUtil` / `TWClient` style integration for compatible firmware.
- `asb72/dvd-bt`: https://github.com/asb72/dvd-bt
  - Contains Java references to `android.tw.john.TWUtil` and command writing patterns.
- `kapi21/OpenRadioFM`: https://github.com/kapi21/OpenRadioFM
  - Useful precedent for hardware-family adapter architecture across Android head units.

## Local device evidence

The highest authority for TS18 compatibility is still the actual device:

- `diagnostics/redacted/ts18_device_profile.json`
- fresh `dumpsys media_session`
- fresh `dumpsys audio`
- fresh `logcat`
- stock vs Auxio-TS behavioural comparison
- decompiled stock APK/static references, when available privately
