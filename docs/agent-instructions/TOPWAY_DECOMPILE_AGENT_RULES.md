# Topway decompile-driven agent rules

Use this file as an active supplement to `AGENTS.md` and `.github/copilot-instructions.md` for all TS18/TWTHEME music compatibility tasks.

## Rule 1 — Topway decompile is now primary local evidence

For TS18 music/widget compatibility, the official Topway `com.tw.music` decompile is a primary local compatibility source.

Agents must read:

```text
docs/topway/TOPWAY_APKTOOL_AND_JADX_COMPATIBILITY_ANALYSIS.md
docs/topway/JADX_ALIAS_AND_APKTOOL_USAGE_GUIDE.md
docs/topway/TOPWAY_NATIVE_COMPATIBILITY_REQUIREMENTS.md
```

before proposing generic Android-only widget/media work.

## Rule 2 — Generic Android compatibility is necessary but insufficient

Android AppWidget, MediaSession, notification, media-button, shortcut, and audio-focus APIs remain the safe implementation surfaces.

But the required behaviours are now Topway-specific:

```text
com.tw.music.info
com.tw.launcher.music_progress_duration
com.android.launcher.widget_music_progress
com.tw.music.action.cmd/.prev/.next/.pp
Topway widget field parity
Topway update lifecycle parity
```

## Rule 3 — Use apktool for runtime identity and JADX for readability

Runtime identity is from apktool:

```text
package=com.tw.music
classes=com.tw.music.*
```

JADX aliases such as `com.p060tw.music` / `com.p073tw.music` are decompiler artefacts.

Do not implement alias package names.

## Rule 4 — Isolated bridge exception

Existing bans on `com.tw.music.action.*` product-code strings must be interpreted as:

```text
Forbidden outside the isolated Topway compatibility bridge.
Allowed only in app/src/main/java/org/oxycblt/auxio/headunit/topway/ and matching tests/docs.
```

## Rule 5 — Still forbidden

Never implement without explicit human-approved design PR:

```text
package impersonation as com.tw.music
sharedUserId=android.uid.system
platform signing assumptions
copied smali
com.tw.service.xt runtime binder dependency
ITWCommandAidl runtime binding
TWUtil/TWClient reflection scanners
vendor package scanners
root/ADB-dependent APK logic
in-app evidence/probe collection
```

## Rule 6 — Do not stop at docs or contracts

For implementation tasks, a `TopwayMusicContract` alone is scaffold-only. It becomes implemented only when consumed by runtime publishers/receivers/widgets/tests.

The implementation pass must wire the contract into real app behaviour.
