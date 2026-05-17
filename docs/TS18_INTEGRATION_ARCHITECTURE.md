# TS18 integration architecture for Auxio-TS

## Recommended architecture

Do not turn Auxio into a decompiled `com.tw.music` clone. Use this structure instead:

```text
Auxio upstream core
  - Media3/ExoPlayer playback
  - library/indexing/metadata
  - playlists/ReplayGain/widgets/UI

Auxio-TS standard media layer
  - explicit validation of MediaSession
  - media notification behaviour
  - media-button routing
  - Android Auto/MediaLibraryService behaviour

Auxio-TS TS18 adapter layer
  - runtime environment detection
  - TS18 diagnostic logging
  - optional TW/TWTHEME compatibility broadcasts/hooks if proven necessary
  - ZLink/TLink comparison hooks if proven necessary

Validation layer
  - scripts and manual runbooks
  - before/after stock vs Auxio-TS evidence bundles
```

## Runtime detection principles

Use low-risk indicators only:

- `ro.product.model=s9863a1h10_Natv`
- `ro.hardware=uis8581a2h10`
- presence of `com.tw.service`, `com.tw.music`, `com.tw.radio`, `com.tw.eq`
- theme paths such as `/system/etc/theme/default/Sub/MusicTheme.apk` where readable
- `persist.phone_connect_app=com.zjinnova.zlink`

Do not rely on serials, user-specific paths, or exact package install hashes.

## Feature flags

Every TS18-specific feature should be individually gated, for example:

- `ts18.diagnostics.enabled`
- `ts18.log.mediaSession.enabled`
- `ts18.broadcastCompat.enabled`
- `ts18.zlinkCompat.enabled`
- `ts18.themeCompat.enabled`
- `ts18.packageCompat.experimental`

Start with diagnostics and standard MediaSession validation before enabling any private TW compatibility behaviour.

## Package identity policy

The initial Auxio-TS app should remain a normal installable app. Do not change it to `com.tw.music` and do not request shared UID.

Package replacement can only be considered after evidence proves all of these:

1. Standard MediaSession is insufficient.
2. TW launcher/TWTHEME explicitly targets `com.tw.music`.
3. A same-package replacement can be installed safely on the device.
4. Signing/UID/privileged-permission consequences are understood.
5. There is a rollback path.

## TS18 adapter candidates

Implement only as evidence requires:

- Diagnostic logger around MediaSession state transitions.
- Broadcast receiver/emitter compatibility with stock actions if discovered.
- Metadata projection shim if stock launcher ignores standard MediaSession.
- Optional “source selected / now playing” integration if `com.tw.service` requires it.
- Theme-aware layout mode if TWTHEME constraints are discovered.

## Standard Android layer must remain correct

Auxio-TS should pass normal Android expectations first:

- active MediaSession during playback;
- media buttons route through the session;
- media notification controls work;
- audio focus request/release are correct;
- playback survives backgrounding;
- no stuck foreground service;
- Android Auto-style browser service remains valid if present.

## FLAC policy

FLAC support is a product requirement. Media3/ExoPlayer and Android platform support should be validated on this API 29 device with real files:

- 16-bit/44.1 kHz FLAC;
- 16-bit/48 kHz FLAC;
- 24-bit/48 kHz FLAC;
- high-rate FLAC only after baseline succeeds.

For TS18, prefer observable playback correctness over theoretical format support.
