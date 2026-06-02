# TS18 Installation Constraints

This page describes install lanes for the redacted TS18 `s9863a1h10` Android 10 device profile. It does not include raw diagnostics.

## APK lanes

| APK / variant | Package | Purpose | Stock-conflict note |
| --- | --- | --- | --- |
| `standardRelease` | `org.oxycblt.auxio` | Normal Auxio-TS identity | Does not replace stock `com.tw.music` |
| `topwayTwMusicRelease` | `com.tw.music` | Exact stock `twmusic` replacement identity | Conflicts with stock system priv-app unless package state/signing is managed |
| `topwayTwMediaRelease` | `com.tw.media` | DoFun alternate fixed entry for `com.tw.media/com.tw.music.MusicActivity` | Not a universal no-root bypass; `com.tw.media` may itself conflict on some firmware |

## Install-lane distinction

| Lane | What it can do | Constraints |
| --- | --- | --- |
| Normal app context / TermOne only | Run app-local diagnostics, inspect app-visible storage, verify overlay permission UI, validate `/sdcard/Music` and `/storage/usbdisk0` media visibility | Cannot disable/remove system priv-app packages; cannot assume install over stock `com.tw.music` |
| ADB shell | Install APKs, inspect package state, disable/enable packages for a user, collect `dumpsys` evidence | Requires physical USB/OTG ADB or another shell path; unavailable in the reported user setup |
| Shizuku | User-mediated package-management and shell-like operations from an app context | Requires Shizuku to be installed, authorized, and working on the head unit |
| Root/system image/firmware control | Manage system priv-app package state, firmware contents, or matching signing path | Device/firmware-specific and outside normal APK expectations |

A user-signed `topwayTwMusicRelease` cannot be assumed installable over `/system/priv-app/com.tw.music_a41e/com.tw.music_a41e.apk`. Package state/signing must be managed by ADB shell, Shizuku, root, firmware/system-image control, matching OEM signature, or prior stock-package removal/disable.

## Reversible recovery notes

Prefer reversible disable before uninstall-for-user when testing package conflicts:

```sh
adb shell pm disable-user --user 0 com.tw.music
adb shell pm enable com.tw.music
adb shell cmd package install-existing --user 0 com.tw.music
```

Apply the same pattern cautiously to `com.tw.media` only after confirming that package exists and understanding the firmware role it plays.
