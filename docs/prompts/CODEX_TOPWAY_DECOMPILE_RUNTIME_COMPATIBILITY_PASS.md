# Codex prompt — implement Topway decompile-driven runtime compatibility bridge

Bring senior Android/Kotlin app architecture, reverse-engineered Android integration, AppWidget/RemoteViews, BroadcastReceiver, Service lifecycle, MediaSession, notification, and automotive/head-unit UX expertise to this task.

Work on branch:

`cx/prepare-auxio-ts-runtime-apk-for-ts18-release`

## Core instruction

The official Topway `com.tw.music` decompile is now the authoritative compatibility source for this phase.

Do not continue treating this as generic Android widget/media compatibility.

Use the repo docs under `docs/topway/` as the compatibility specification.

This is an app/runtime implementation task, not a docs-only pass.

## Required reading

Read first:

```text
docs/topway/TOPWAY_APKTOOL_AND_JADX_COMPATIBILITY_ANALYSIS.md
docs/topway/JADX_ALIAS_AND_APKTOOL_USAGE_GUIDE.md
docs/topway/TOPWAY_NATIVE_COMPATIBILITY_REQUIREMENTS.md
docs/agent-instructions/TOPWAY_DECOMPILE_AGENT_RULES.md
```

## Main goal

Implement a safe isolated Topway compatibility bridge for Auxio-TS.

This must materially implement the concrete behaviours found in the Topway decompile:

1. Topway contract constants;
2. outgoing `com.tw.music.info` metadata broadcast;
3. outgoing `com.tw.launcher.music_progress_duration` progress/duration broadcast;
4. incoming `com.tw.music.action.cmd/.prev/.next/.pp` transport commands;
5. incoming `com.android.launcher.widget_music_progress` seek handling;
6. Topway-like widget RemoteViews parity;
7. widget update lifecycle parity;
8. MediaSession/notification/widget/Topway metadata unification;
9. guardrail update for isolated bridge strings;
10. tests and concise docs updates.

If fewer than eight of these ten areas are implemented, final recommendation must be:

`Needs another Codex pass`

## Workstream A — Isolated Topway contract

Create:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt
```

Include constants for:

```text
com.tw.music.info
musicTitle
musicaArtist
musicAlbum
musicPath

com.tw.launcher.music_progress_duration
msg_music_progress
msg_music_duration

com.android.launcher.widget_music_progress
music_progress

com.tw.music.action.cmd
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp

cmd
appWidgetIds
prev
next
pp
update
```

Do not scatter these strings elsewhere.

## Workstream B — Outgoing metadata broadcast

Implement a bridge, for example:

```text
TopwayMusicBroadcastBridge.kt
```

Publish:

```text
action com.tw.music.info
extras musicTitle, musicaArtist, musicAlbum, musicPath
```

Requirements:

- use existing Auxio metadata policy;
- preserve `musicaArtist` typo;
- send when current track metadata changes;
- de-duplicate unchanged broadcasts where practical;
- do not use sticky broadcast;
- do not fake unavailable path values;
- no root/ADB/system UID assumptions.

Add tests for intent construction and de-duplication policy if extractable.

## Workstream C — Outgoing progress/duration broadcast

Publish:

```text
action com.tw.launcher.music_progress_duration
extras msg_music_progress, msg_music_duration
```

Requirements:

- publish while session/playback is active;
- throttle to around 1 second or reuse existing progress update cadence;
- stop/clear when playback stops or no session exists;
- do not add independent wakeful polling;
- do not update when service is not active.

Add tests for intent construction and throttling/publishing policy if extractable.

## Workstream D — Incoming Topway transport commands

Support:

```text
com.tw.music.action.cmd with cmd=prev/next/pp/update
com.tw.music.action.prev
com.tw.music.action.next
com.tw.music.action.pp
```

Map:

```text
prev -> previous
next -> next
pp -> play/pause
action prev/next/pp -> same mapping
cmd=update -> widget refresh
```

Requirements:

- use isolated receiver/service path;
- gate by session/current-song/focus safety;
- no unsafe autoplay from inert/no-song state;
- receiver/exported status must be explicit and narrow;
- keep `onReceive()` lightweight.

Add command mapping and safety tests.

## Workstream E — Incoming launcher seek

Support:

```text
com.android.launcher.widget_music_progress
extra music_progress
```

Requirements:

- seek only with active session and valid duration;
- clamp out-of-range values;
- ignore missing/bad extras;
- never crash on malformed broadcasts.

Add tests for valid seek, negative seek, beyond-duration seek, missing extra, and no-session state.

## Workstream F — Widget RemoteViews parity

Audit and harden:

```text
WidgetProvider
WidgetComponent
WidgetRenderState
WidgetUtil
widget layouts/resources/provider XML
```

Implement Topway-like widget parity where existing Auxio architecture allows:

```text
title
artist/subtitle
current time
duration
progress bar
album art or fallback
prev/play/next controls
root/album-art tap -> Now Playing
```

Requirements:

- no stale metadata after queue empty/no session;
- update all widget instances;
- update on metadata/state/progress and `cmd=update`;
- no second widget provider unless strongly justified;
- no private TWTHEME loaders.

## Workstream G — Widget update lifecycle parity

Mirror the safe parts of stock lifecycle:

```text
on widget update -> render current safe state
metadata change -> refresh widget
progress update -> update progress/time/duration while active
null/no-session -> clear stale state
Topway cmd=update -> widget refresh
```

No independent background polling loop.

## Workstream H — Metadata unification

Ensure Topway broadcasts, MediaSession, notification, and widget derive from one effective metadata policy:

```text
title
subtitle/artist
album artist
album
duration
progress
artwork
media id/uri/path equivalent
```

Avoid duplicate artist/subtitle text and stale state.

## Workstream I — Guardrails

Update `scripts/check-headunit-compat-safety.sh` so Topway strings are allowed only in:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/
app/src/test/java/org/oxycblt/auxio/headunit/topway/
docs/
```

Still fail on:

```text
sharedUserId
android.uid.system
package impersonation
com.tw.service.xt binder execution
ITWCommandAidl runtime binding
TWUtil/TWClient reflection
vendor scanners
runtime probes
```

## Workstream J — Docs after implementation

Update concise docs after runtime changes:

```text
docs/TW_ECOSYSTEM_SOURCE_MAP.md
docs/TS18_INTEGRATION_ARCHITECTURE.md
docs/TS18_TIER1_HARDENING_NOTES.md
docs/TS18_NATIVE_PARITY_GAP_MATRIX.md
docs/TS18_REQUIREMENTS.md
docs/DEVELOPMENT_ROADMAP.md
docs/CODEX_TASK_PROMPTS.md
AGENTS.md
.github/copilot-instructions.md
```

Docs must say:

- Topway decompile now defines concrete compatibility requirements;
- Auxio-TS implements safe broadcast/widget compatibility where implemented;
- XT/AIDL/system-UID paths are documented but not implemented;
- runtime APK remains clean;
- no package impersonation or copied smali;
- TS18 hardware validation still required.

## Non-goals

Do not add:

```text
package impersonation as com.tw.music
android.uid.system
sharedUserId
platform-signature assumptions
copied smali
direct com.tw.* imports
direct android.tw.john.* imports
com.tw.service.xt binder execution
ITWCommandAidl runtime binding
unbounded reflection scanners
vendor package scanners
in-app probe/evidence collection
root-only behaviour
ADB-dependent APK logic
hidden diagnostics modules
full Media3 migration
broad UI rewrite
```

Do not claim TS18 hardware validation has passed.

## Product-code safety audit

Run:

```sh
grep -RIn "android.uid.system\|sharedUserId\|Class.forName("android.tw\|android.tw.john.TWUtil\|android.tw.john.TWClient\|com.tw.service.xt\|ITWCommandAidl" app/src/main app/src/test || true

grep -RIn "com.tw.music.action\|com.tw.music.info\|com.tw.launcher.music_progress_duration\|com.android.launcher.widget_music_progress" app/src/main app/src/test || true
```

Expected:

- Topway strings appear only in isolated Topway bridge/tests.
- No system UID/sharedUserId.
- No direct vendor imports.
- No XT binder execution.
- No runtime scanners/probes.

## Verification

Run:

```sh
git status --short
git branch --show-current
git log --oneline --decorate -n 30
git diff --stat
git diff --name-status
git diff --check
find scripts -type f -name '*.sh' -print -exec bash -n {} \;
bash scripts/check-headunit-compat-safety.sh || true
python3 -m py_compile scripts/ts18-*.py || true
bash scripts/check-submodules.sh || true
```

Run Gradle where environment allows:

```sh
./gradlew --no-daemon --stacktrace spotlessCheck
./gradlew --no-daemon --stacktrace :app:testDebugUnitTest :musikr:testDebugUnitTest
./gradlew --no-daemon --stacktrace :app:lintDebug
./gradlew --no-daemon --stacktrace :app:assembleDebug
```

If Gradle is blocked, capture first real error and do not claim success.

## Final response/comment format

Your final response/comment must be provided as a triple-tilde unbroken markdown codeblock.

Use this structure:

## Executive summary

## Recommendation
Ready for Draft PR snapshot / Needs another Codex pass / Blocked by environment / Blocked by human decision

## Topway decompile-driven compatibility completion

## Runtime implementation areas completed
List each as Implemented / Partially implemented / Blocked:
- Topway contract
- Metadata broadcast
- Progress/duration broadcast
- Transport commands
- Launcher seek
- Widget RemoteViews parity
- Widget update lifecycle
- Metadata unification
- Guardrails
- Docs/instructions

## Runtime wiring classification

## App/runtime changes implemented

## Files changed

## Tests added or updated

## Product-code safety audit

## Validation commands and results

## Gradle/build status

## Explicitly not implemented

## Remaining blockers or risks

## Recommended Draft PR title and body
