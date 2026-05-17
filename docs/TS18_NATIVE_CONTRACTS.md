# TS18 native contracts and integration leads

This document tracks known and suspected TS18/TW contracts for Auxio-TS.

## Proven installed TW packages

From diagnostics:

- `com.tw.music`
- `com.tw.service`
- `com.tw.service.xt`
- `com.tw.radio`
- `com.tw.eq`
- `com.tw.bt`
- `com.tw.video`
- `com.tw.reverse`
- `com.tw.net`
- `com.tw.core`
- `com.tw.coreservice`
- `com.tw.carchoose`
- `com.tw.carinfoservice`
- `com.tw.keypad`
- `com.tw.twfileexplore`

## High-priority contracts to investigate

| Contract | Status | Why it matters | How to verify |
|---|---|---|---|
| Android MediaSession | Required standard layer | Needed for notifications, media keys, Android Auto-style clients | `dumpsys media_session` during playback |
| Media notification controls | Required standard layer | User-visible transport controls | Notification shade/manual test/logcat |
| Audio focus | Required standard layer + TS18-specific behaviour | `com.tw.service` mediates focus/volume | `dumpsys audio`, logcat before/after |
| `com.tw.music` package identity | Unknown | Launcher/theme may hard-code it | Static grep decompiled launcher/theme/TW APKs; runtime logs |
| `android.uid.system` / UID 1000 | High-risk unknown | Stock app has privileged/system context | `pm list packages -U`, package dump from adb/root |
| `com.tw.music.action.*` broadcasts | Unknown | Could be launcher/control interface | Static grep stock APKs; `cmd activity broadcasts`; logcat |
| `com.tw.service` / `com.tw.service.xt` APIs | Unknown but important | TW service likely mediates car/audio/key behaviour | Decompiled APKs; service list; binder/service dumps |
| TWTHEME `MusicTheme.apk` | Unknown | May contain resources/layout assumptions | Extract/decompile theme APK; static refs |
| ZLink metadata/control | Unknown | Active phone-link system | Compare stock/Auxio-TS playback while ZLink active |
| Steering wheel/media keys | Unknown | Required vehicle UX | `getevent`, `dumpsys input`, `dumpsys media_session` |

## Do not assume

- Do not assume TW launcher observes only standard MediaSession.
- Do not assume TW launcher ignores standard MediaSession.
- Do not assume `com.tw.music` identity is required.
- Do not assume `com.tw.music` identity is replaceable.
- Do not assume ZLink consumes standard metadata.
- Do not assume package UID 1000 can be reproduced by a user-installed app.

## Evidence needed before private TW hooks

Private TW compatibility code requires at least one of:

- static reference from a TW/launcher/theme APK to a private action/package/class;
- logcat showing private TW control messages during playback/control;
- before/after test showing standard MediaSession is insufficient;
- stock `com.tw.music` decompile evidence showing emitted metadata/control actions;
- runtime service/broadcast evidence from adb/root-level capture.
