# Target Device Profile — TS18 / Android 13

## Status of this document

This document encodes **working assumptions** for the deployment target of `com.tw.music`. TS18 hardware ships in many variants from different sellers and firmware branches. Treat every item below as a reasonable working assumption for a **TS18 4GB Android 13 head unit**, not as an absolute claim that applies to every possible TS18 build.

When in doubt, prefer compatibility-preserving decisions over assumptions that could break functionality on a slightly different variant.

---

## Platform summary

| Property | Working assumption |
|---|---|
| Platform family | TS18 (Topway / TW-series head unit) |
| Android version | **Android 13** |
| RAM class | **4 GB** |
| Storage class | **64 GB** (typical; 32 GB variants may exist) |
| CPU class | Octa-core ARM Cortex-A55 ~1.6 GHz class |
| GPU class | PowerVR GE8322-class or equivalent TS18 graphics tier |
| Screen resolution | **1280 × 720** (QLED / HD class) |
| Screen orientation | **Landscape fixed** — in-dash head unit |
| Form factor | 7 / 9 / 10 inch in-dash family; assume landscape in all cases |
| Bluetooth | BT 5.0-class |
| Wi-Fi | Dual-band (2.4 GHz + 5 GHz) |
| Wireless CarPlay / Android Auto | Via **TLink** |
| GNSS | GPS + GLONASS; BeiDou support common in Android 13 builds |
| USB | USB-A for media playback, device connection, and APK sideloading |
| Other connectors | RCA, reversing-camera input, 4G antenna (build-dependent), microphone, GPS antenna |
| Steering-wheel control | Steering-wheel learning / SWC support present |
| Split-screen | Supported |
| 360-view | Commonly available on 4 GB+ RAM variants |
| FOTA update | Present on some TS18 Android 13 builds |

---

## TW vendor environment

This device runs a **TW (Topway) vendor firmware layer**. All `com.tw.*` packages, services, properties, and theme hooks are part of this vendor environment.

### Key vendor namespace assumptions

| Namespace / surface | Role |
|---|---|
| `com.tw.music` | Music app (this app) |
| `com.tw.eq` | EQ application — launched by this app via `ComponentName` |
| `com.tw.radio` | Radio application — hand-off boundary |
| `com.tw.service.xt` | Vendor AIDL service hub — IPC surface for TW apps |
| `com.tw.service.xt.aidl.*` | AIDL interface definitions (ITWCommandAidl, IMusicCallBack, etc.) |
| `persist.tw.*` | System property namespace for TW/vendor feature flags |
| `persist.tw.ijk`, `persist.tw.ijk.noerror`, `persist.tw.ijk.opensles` | IjkMediaPlayer / playback engine selection properties |
| `twtheme` / `TWTHEME` | TW theme switching surface — must remain compatible |
| `@style/AppTheme` | Root app theme, must remain compatible with TW theme layer |

### TWTHEME / theme switching

The TS18 firmware includes a **theme switching system** (`TWTHEME`). This app participates in the TW theme layer via `@style/AppTheme` and theme-referenced resources. Any change that breaks the theme hook will break the visual coherence of the app when themes are switched at the firmware level.

**Never:**
- Remove or rename `@style/AppTheme`
- Remove or rename any resource with a `tw_` prefix
- Remove or rename resources referenced by the launcher widget (`MusicWidgetProvider`)
- Hardcode colour values that should be supplied by the TW theme layer

**Always:**
- Use theme attributes (`?attr/colorPrimary`, etc.) for colour, so the TW theme layer controls the palette
- Preserve any style that is inherited from or extends a `tw_` or `AppTheme` style

---

## Launcher environment

The TS18 launcher surfaces multiple vendor modules as home-screen entries. The music app (`com.tw.music`) is one of the primary launcher targets. Other co-resident modules typically include:

- Themes (`TWTHEME`)
- TLink (wireless CarPlay / Android Auto)
- Music (`com.tw.music` — this app)
- Video
- EQ (`com.tw.eq`)
- Broadcast / Radio (`com.tw.radio`)
- Car Settings
- Bluetooth
- Navigation
- APK installer / file manager

This app must coexist cleanly with all of these. Do not break launch intents, widget state, or media session state in ways that would corrupt the launcher's view of the music state.

---

## TLink / CarPlay / Android Auto coexistence

TLink bridges wireless CarPlay and Android Auto on TS18 devices. When TLink is active:

- The TW launcher may suspend or background the native music app.
- Media session state published by this app may be read by TLink or the CarPlay/AA mirroring layer.
- Changes to `MediaSession`, `PlaybackState`, and `MediaMetadata` publication must not produce stale or incorrect state that misleads TLink or the head unit's system UI.
- Do not assume media session behaviour follows the phone lock-screen paradigm — the TS18 vendor firmware may surface media state in custom panels or widgets.

---

## UI and UX constraints (derived from hardware)

### Screen

- **1280 × 720 landscape** is the key design baseline.
- All layouts must be tested/verified at this resolution.
- No portrait-only or portrait-fallback qualifiers.

### Touch targets

- Primary playback controls (prev, play/pause, next): minimum **56–64 dp**, ideally aligned to a touch-friendly grid.
- Secondary controls (shuffle, repeat, playlist, settings): minimum **48 dp**.
- Drivers interact while in motion; oversized targets are always better than minimum-size targets.

### Glanceability

- Track title and artist must be legible at a glance from 40–60 cm at driving-safe font sizes.
- Playing/paused state must be visually unambiguous without requiring close inspection.
- Progress / position indicator is supplementary; it must not dominate screen space.

### Performance

- **4 GB RAM class**: this is capable, but the device also runs the vendor firmware, launcher, TLink, navigation, and other co-resident TW apps simultaneously. Avoid gratuitously heavy UI patterns, large bitmaps in memory, or excessive layout nesting.
- Avoid allocating large off-screen buffers or continuous animations that burn CPU/GPU while the music app is in the background.

---

## Playback and media session environment

### Expected media session behaviour

- The app runs as a **foreground service** during active playback — this is critical on Android 13.
- `MediaSession` / `MediaSessionCompat` tokens must be created, maintained, and released correctly.
- `PlaybackState` must be updated on every meaningful transition (play, pause, stop, seek, track change, error).
- `MediaMetadata` must be published on every track change (title, artist, album, duration, artwork URI at minimum).
- Notification `MediaStyle` actions must use correct icons and semantics for prev / play-pause / next.

### Android 13 media notification behaviour

On Android 13, the system media notification UI has been updated. Key implications:
- The system-provided media player notification now uses `MediaSession` metadata directly for its presentation.
- `MediaStyle` notification actions and session state are surfaced in the notification shade and in media player surfaces.
- TW vendor firmware may additionally surface session state in custom widgets or the launcher panel — treat metadata publication as externally visible, not just notification-bound.

### Do not assume phone-only behaviour

- The TS18 system UI is vendor-customised — do not assume standard AOSP notification shade behaviour.
- Media button routing, audio focus handling, and notification appearance may differ from stock Android.
- Preserve all `com.tw.music.action.*` broadcast handling — this is the TW control surface for hardware keys and steering-wheel controls.

---

## Release and update environment

- The app ships as a system package (`android.uid.system`, `sharedUserId`).
- FOTA update paths exist on some TS18 Android 13 builds — same-package APK update viability must be maintained.
- Signature must match the original system signing key for update-in-place to succeed.
- Do not change `sharedUserId`, add permissions that alter the effective UID, or rename the package.

---

## Resources

### TS18 device references
- FCC filing (TS18): https://fcc.report/FCC-ID/2BECX-TS18/7046050.pdf
- TS18 manual reference (AE): https://manuals.plus/ae/1005008549939734
- TS18 manual reference (AE alt): https://manuals.plus/ae/1005007988665007
- TS18 manual reference (AE alt 2): https://manuals.plus/ae/1005008631349333
- Topway TS18 product page: https://www.carmp5.com/product/topway-ts18-android/
- TS18 1280×720 4/64G listing: https://www.carsolutionlk.com/product/ts18-1280x720-464g-9-10-inch-android-player/

### Android media / notification
- Android legacy media overview: https://developer.android.com/media/legacy
- Legacy audio guide: https://developer.android.com/media/legacy/audio
- MediaSession guide: https://developer.android.com/media/legacy/mediasession
- Media buttons: https://developer.android.com/media/legacy/media-buttons
- MediaBrowserService: https://developer.android.com/media/legacy/audio/mediabrowserservice
- MediaBrowser: https://developer.android.com/media/legacy/audio/mediabrowser
- Background playback: https://developer.android.com/media/platform/mediaplayer/background
- NotificationCompat.MediaStyle: https://developer.android.com/reference/androidx/media/app/NotificationCompat.MediaStyle
- MediaButtonReceiver: https://developer.android.com/reference/androidx/media/session/MediaButtonReceiver
- Notifications guide: https://developer.android.com/develop/ui/views/notifications
- Media controls blog post (2020): https://android-developers.googleblog.com/2020/08/playing-nicely-with-media-controls.html

### GitHub Copilot / agent instruction references
- Custom instructions: https://docs.github.com/copilot/customizing-copilot/adding-custom-instructions-for-github-copilot
- Add custom instructions: https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/add-custom-instructions
- Custom instructions support reference: https://docs.github.com/en/copilot/reference/custom-instructions-support
- Copilot code review: https://docs.github.com/en/copilot/how-tos/copilot-on-github/use-copilot-agents/copilot-code-review
- About coding agent: https://docs.github.com/en/copilot/concepts/agents/coding-agent/about-coding-agent
- Create custom agents: https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/customize-cloud-agent/create-custom-agents

### MT Manager / reverse-engineering
- MT Manager VIP: https://mt2.cn/vip/
- MT Manager releases: https://mt2.cn/releases/
- BinMT thread 153991: https://bbs.binmt.cc/thread-153991-1-1.html
- BinMT thread 158373: https://bbs.binmt.cc/thread-158373-1-1.html
