# Codex TS18 Device Context — Redacted

This file is the concise, repo-ready device profile for the current TS18 hardening work. It is derived from redacted user-provided facts only; raw diagnostics, serial values, private logs, and unredacted package dumps are intentionally not committed.

## Device profile

| Area | Redacted fact |
| --- | --- |
| Target device | TS18/Topway `s9863a1h10_Natv` / `s9863a1h10` |
| Runtime | Android 10 / SDK 29 |
| Display | 1280x720 landscape |
| UI insets | Top status bar about 55px; right navigation bar about 55px |
| Stock music app | `com.tw.music` system priv-app at `/system/priv-app/com.tw.music_a41e/com.tw.music_a41e.apk` |
| DoFun theme | `com.dofun.variety` installed |
| Capture context | TermOne normal app UID, approximately `u0_a177`, under `u:r:untrusted_app` |
| ADB state | USB gadget state includes `mtp,adb`; no TCP/local ADB listener was observed |
| User setup | USB/OTG ADB is physically unavailable |
| Music storage | `/storage/usbdisk0` exists and must be included in runtime library validation |

## Compatibility implications

- **Evidence confidence:** Observed. **Porting decision:** Directly reusable requirement. DoFun evidence supports both fixed local-music entries: `com.tw.music/com.tw.music.MusicActivity` and `com.tw.media/com.tw.music.MusicActivity`.
- **Evidence confidence:** Observed. **Porting decision:** Requires TS18 runtime validation. Stock `com.tw.music` is a system priv-app and appears integrated with system/vendor configuration and UID/system behaviour.
- **Evidence confidence:** Observed. **Porting decision:** Reusable validation idea. TermOne-only diagnostics can observe normal app-visible state, but cannot disable/remove stock system packages or prove privileged install flows.
- **Evidence confidence:** Observed. **Porting decision:** Directly reusable requirement. Android 10 overlay/runtime paths must avoid Android 14-only foreground-service assumptions and clamp floating controls within the 1280x720 visible area.

## Boundaries

Production code must not require platform signing, system UID, `sharedUserId`, fake Cardoor services, TWUtil reflection, vendor binders, copied smali, or private/native integration. Private/native work is not for production by default; it requires the formal gap-and-promotion process.
