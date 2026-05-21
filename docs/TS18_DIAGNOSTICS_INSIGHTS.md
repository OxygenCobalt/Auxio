# TS18 diagnostics insights

Derived from the attached `TS18_diagnostics.zip`. Raw diagnostics are not included in this package because they can contain identifiers and proprietary device/app data.

## What the diagnostics prove

1. The target is an Android 10 / API 29 TS18-like Unisoc/SPRD automotive build.
2. The TW ecosystem is present as real installed packages, not just documentation terms.
3. Stock `com.tw.music` is a system privileged app and package listings show UID 1000.
4. `com.tw.service` is active in audio management and holds/requested media audio focus in the capture.
5. TWTHEME assets exist under `/system/etc/theme/default/...`, including `Sub/MusicTheme.apk`.
6. ZLink is the active phone-link package via `persist.phone_connect_app=com.zjinnova.zlink`.
7. Android Auto/Gearhead and third-party players are installed, making comparison tests possible.
8. The current capture did not include an active media session, so MediaSession-based integration still needs runtime testing.

## What the diagnostics do not prove

- They do not prove whether the TS18 launcher/home widget reads Android MediaSession.
- They do not prove whether `com.tw.music` package identity is mandatory.
- They do not prove whether `MusicTheme.apk` is class/resource-coupled to `com.tw.music`.
- They do not prove whether ZLink reads standard media metadata or TW-specific state.
- They do not provide full package dumps because the local shell lacked permissions.
- They do not include decompiled APK internals.

## Priority unknowns

1. Does TS18 launcher/home media widget update from a normal third-party MediaSession?
2. Does it update from Spotify/Poweramp/stock music differently?
3. Are steering wheel/media keys delivered through Android media-button dispatch or through `com.tw.service` vendor callbacks?
4. Does `com.tw.service` require explicit source selection to allow third-party media focus?
5. Does ZLink consume standard MediaSession metadata?
6. Does stock `com.tw.music` broadcast private actions such as `com.tw.music.action.cmd`, `prev`, `next`, or `pp`?
7. Does TWTHEME load `MusicTheme.apk` resources only, or does it bind package/class names?

## First evidence bundle to capture after Auxio-TS install

Use `scripts/ts18_collect_auxio_ts_evidence.sh` while:

1. no music is playing;
2. stock `com.tw.music` is playing;
3. Spotify or another existing third-party player is playing;
4. Auxio-TS is playing MP3;
5. Auxio-TS is playing FLAC;
6. Auxio-TS is paused with notification visible;
7. ZLink/Android Auto is active if safe to test.

> **Tier 0 evidence note:** This diagnostics file is Tier 0 evidence. It informs Tier 2 validation scenarios and the parity gap matrix. Diagnostics findings do not justify Tier 3/4 native contract work unless Tier 2 hardware validation confirms a specific parity gap. See [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md).
