# TS18 device profile

This file is a redacted, human-readable summary of the attached TS18 diagnostics. The structured version is `diagnostics/redacted/ts18_device_profile.json`.

## Identity

- Android release: 10
- SDK/API: 29
- Security patch: 2021-07-05
- Kernel: Linux 4.14.133 aarch64
- Build: `uis8581a2h10_Automotive-user 10 QP1A.190711.020 50216 release-keys`
- Product model: `s9863a1h10_Natv`
- Platform/board: `uis8581a2h10` / `sp9863a`
- ABI: `arm64-v8a`
- Build type/tags: `user` / `release-keys`
- Display-related props: `1280x720`
- Shell context of diagnostics: local untrusted app shell, not root; many package dumps denied.

Device serials and other direct identifiers are deliberately omitted.

## Key packages from diagnostics

| Package | Path / role | Notes |
|---|---|---|
| `com.tw.music` | `/system/priv-app/com.tw.music_a41e/com.tw.music_a41e.apk` | Stock music app; package list reports UID 1000. |
| `com.tw.service` | `/system/priv-app/com.tw.service_a5a4/com.tw.service_a5a4.apk` | TW service; captured as audio focus/volume actor. |
| `com.tw.service.xt` | `/system/priv-app/com.tw.xtservice/com.tw.xtservice.apk` | XT service package; likely integration lead. |
| `com.tw.radio` | `/system/priv-app/com.tw.radio_78cc/com.tw.radio_78cc.apk` | Native radio app. |
| `com.tw.eq` | `/system/priv-app/com.tw.eq_936a/com.tw.eq_936a.apk` | Native EQ app. |
| `com.tw.bt` | `/system/priv-app/com.tw.bt_305c/com.tw.bt_305c.apk` | Native Bluetooth app. |
| `com.tw.video` | `/system/priv-app/com.tw.video_869a/com.tw.video_869a.apk` | Native video app. |
| `com.tw.reverse` | `/system/priv-app/com.tw.reverse_3f4d/com.tw.reverse_3f4d.apk` | Reverse camera-related app. |
| `com.zjinnova.zlink` | `/data/app/.../base.apk` | Active phone-link app via `persist.phone_connect_app`. |
| `com.google.android.projection.gearhead` | `/data/app/...` | Android Auto app present. |
| `com.spotify.music` | `/data/app/...` | Existing third-party media app; useful comparison baseline. |
| `com.maxmpz.audioplayer` | `/data/app/...` | Existing third-party media app; useful comparison baseline. |

## TWTHEME files observed

- `/system/etc/theme/default/Launcher/LauncherTheme.apk`
- `/system/etc/theme/default/Sub/MusicTheme.apk`
- `/system/etc/theme/default/Sub/RadioTheme.apk`
- `/system/etc/theme/default/Sub/BluetoothTheme.apk`
- `/system/etc/theme/default/Sub/EQTheme.apk`
- `/system/etc/theme/default/Sub/VideoTheme.apk`
- `/system/etc/theme/theme_config.json`
- `/system/etc/theme_package/TW*.apk`

## Media/session observations

- `dumpsys media_session` captured **0 active sessions**.
- Restored media button receiver was `com.zjinnova.zlink/com.zjinnova.android.zlink.features.broadcast.MediaButtonReceiver`.
- Media button session was `null` at capture time.
- This does not prove TS18 ignores MediaSession; it only means no active session was captured.

## Audio observations

- `com.tw.service` requested `USAGE_MEDIA` audio focus as UID 1000.
- `com.tw.service` repeatedly adjusted `STREAM_MUSIC`, `STREAM_NOTIFICATION`, and `STREAM_ALARM` volumes.
- `audio_policy` service dump was unavailable, but `dumpsys audio` was captured.

## Input/key observations

- Keylayout search found `key 171 MUSIC` and `key 213 MUSIC`.
- Commented hints included `KEY_RADIO` and `KEY_TWEN`.
- Input devices included `gpio-keys`, `sprdphone Headset Keyboard`, `goodix_ts`, and `unisoc-rvc-det`.

## Development implications

- TS18 integration is real and package-rich; this is not just a skin over AOSP.
- The first Auxio-TS implementation should prove standard MediaSession behaviour on-device before adding TW-specific hooks.
- `com.tw.service` is probably a critical audio/vehicle mediator.
- Stock `com.tw.music` being system/priv UID 1000 means package replacement/impersonation is high risk and should not be first approach.

> **Tier 0 evidence note:** This device profile is Tier 0 evidence. It defines the parity target and feeds into Tier 2 validation scenarios. It does not justify any Tier 3/4 native contract work until Tier 2 on-device validation confirms a specific parity gap. See [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md).
