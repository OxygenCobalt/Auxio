# Patch snippet: add to `AGENTS.md` and `.github/copilot-instructions.md`

Add this section after the TS18/TW/TWTHEME source-led policy sections.

```markdown
## Topway decompile-driven compatibility rule

The official Topway `com.tw.music` decompile is now a primary local compatibility source for TS18 music/widget compatibility.

Before TS18 music/widget implementation work, read:

- `docs/topway/TOPWAY_APKTOOL_AND_JADX_COMPATIBILITY_ANALYSIS.md`
- `docs/topway/JADX_ALIAS_AND_APKTOOL_USAGE_GUIDE.md`
- `docs/topway/TOPWAY_NATIVE_COMPATIBILITY_REQUIREMENTS.md`

Generic Android AppWidget/MediaSession/notification compatibility is necessary but not sufficient. The decompile defines concrete safe compatibility requirements for:

- `com.tw.music.info` metadata broadcast;
- `com.tw.launcher.music_progress_duration` progress/duration broadcast;
- `com.android.launcher.widget_music_progress` launcher seek input;
- `com.tw.music.action.cmd/.prev/.next/.pp` transport command inputs;
- Topway-like widget title/artist/current-time/duration/progress/art/control parity;
- widget update lifecycle parity.

JADX package names such as `com.p060tw.music` and `com.p073tw.music` are decompiler aliases. Runtime identity comes from apktool and remains `com.tw.music`.

Topway strings are allowed in product code only inside an isolated bridge package such as:

- `app/src/main/java/org/oxycblt/auxio/headunit/topway/`
- `app/src/test/java/org/oxycblt/auxio/headunit/topway/`

They must not be scattered through core playback, widget, dashboard, or service code.

Still forbidden without explicit human-approved design PR:

- package impersonation as `com.tw.music`;
- `sharedUserId` / `android.uid.system`;
- platform-signature assumptions;
- copied smali;
- direct vendor imports;
- `com.tw.service.xt` runtime binder dependency;
- `ITWCommandAidl` runtime binding;
- TWUtil/TWClient reflection scanners;
- vendor package scanners;
- root/ADB-dependent APK logic;
- in-app probe/evidence collectors.
```
