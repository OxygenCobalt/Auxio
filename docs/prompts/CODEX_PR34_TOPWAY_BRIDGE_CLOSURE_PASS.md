Bring senior Android/Kotlin app architecture, BroadcastReceiver/exported receiver design, AndroidManifest safety, AppWidget/RemoteViews, MediaSession, notification, playback service lifecycle, and reverse-engineered Topway integration expertise to this task.

Continue work for PR #34 on branch:

`cx/align-auxio-ts-docs-with-topway-compatibility`

Base branch:

`cx/prepare-auxio-ts-runtime-apk-for-ts18-release`

## Task title

Complete PR #34 Topway bridge runtime closure

## Core context

PR #34 currently contains the first isolated Topway bridge implementation:

- `TopwayMusicContract`
- `TopwayMusicIntentFactory`
- `TopwayMusicBroadcastBridge`
- `TopwayMusicCommandMapper`
- `TopwayMusicSeekMapper`
- dynamic wiring in `SystemPlaybackReceiver`
- Topway metadata/progress publication from `WidgetComponent`
- contract/factory/mapper tests
- guardrail/docs updates

The attached latest Codex export indicates additional work may exist or be intended beyond the live PR head:

- `TopwayMusicBridgeReceiver`
- AndroidManifest receiver filters
- widget current time/duration/progress layout fields
- widget timeline rendering
- guardrail manifest allowances

Verify the actual branch contents first.

## Required reading

Read these new second-pass support files before editing:

```text
docs/topway/TOPWAY_PR34_SECOND_PASS_REVIEW.md
docs/topway/TOPWAY_WIDGET_AND_RECEIVER_CLOSURE_SPEC.md
docs/topway/TOPWAY_BRIDGE_RUNTIME_TEST_MATRIX.md
docs/topway/reference/topway_pr34_second_pass_contract_inventory.json
docs/agent-instructions/PR34_TOPWAY_BRIDGE_CLOSURE_AGENT_RULES.md
```

## Main objective

Finish PR #34’s Topway bridge runtime closure.

This is not a docs-only task.

Do not stop after constants/mappers/factory tests.

Do not report ready unless receiver lifecycle, widget parity, broadcast lifecycle, and tests are genuinely improved.

## Mandatory deliverables

Implement or explicitly reject with technical rationale:

1. receiver lifecycle/cold-state strategy;
2. widget RemoteViews visual parity;
3. widget stale/no-session clearing;
4. widget update lifecycle parity;
5. metadata/progress broadcast lifecycle;
6. MediaSession/notification/widget/Topway metadata unification;
7. runtime edge-case tests;
8. guardrails/manifest safety;
9. docs status sync.

If any deliverable is missing, final recommendation must be:

`Needs another pass`

## Deliverable 1 — Receiver lifecycle/cold-state strategy

Audit:

```text
AndroidManifest.xml
SystemPlaybackReceiver
PlaybackService / AuxioService
TopwayMusicBridgeReceiver if present
TopwayMusicCommandMapper
TopwayMusicSeekMapper
TopwayMusicContract
WidgetProvider
WidgetComponent
```

Questions to answer in code/docs:

- Can Auxio-TS receive external `com.tw.music.action.*` broadcasts when service/process is cold?
- If yes, how?
- If not, is a static receiver required?
- If a static receiver exists, is it narrow, exported only where required, and safe?

If adding or retaining a manifest receiver:

- action filters only for approved Topway bridge actions;
- no broad vendor actions;
- no `com.tw.service.xt`;
- no `ITWCommandAidl`;
- no `sharedUserId`;
- no `android.uid.system`;
- no long-running `onReceive`;
- safe delegation to existing playback/widget paths;
- no forced autoplay from inert/no-song state.

If deliberately not adding one, document why and keep dynamic-only behaviour explicit.

## Deliverable 2 — Widget RemoteViews parity

Stock Topway widget layout includes:

```text
albumart
title
artist
control_prev
control_play
control_next
tv_current_time
tv_duration
seek_bar_progress
```

Auxio-TS widget must provide equivalent concepts where architecture permits:

- title;
- artist/subtitle;
- current time;
- duration;
- progress bar max/progress;
- album art/fallback;
- prev/play-pause/next controls;
- root/art tap to Now Playing.

Important unit rule:

- widget RemoteViews progress bar should use seconds for max/progress parity;
- Topway external progress broadcast should use milliseconds.

Do not conflate these units.

## Deliverable 3 — Widget stale/no-session clearing

Ensure no stale widget state survives when:

- song becomes null;
- queue becomes empty;
- playback session resets;
- app/process restarts;
- widget update happens before active playback;
- `cmd=update` arrives with no active song;
- artwork becomes unavailable.

Clear or reset:

- title;
- artist/subtitle;
- current time;
- duration;
- progress;
- album art;
- play/pause icon/state.

## Deliverable 4 — Widget update lifecycle parity

Stock widget has `updatePeriodMillis=0` and is event-driven.

Auxio-TS must not add unconditional background polling.

Implement/verify:

- AppWidgetProvider update renders current safe state;
- `cmd=update` refreshes widgets;
- metadata change refreshes widgets;
- progress change updates timeline without excessive churn;
- no-session clears stale state;
- all widget instances update consistently.

## Deliverable 5 — Metadata/progress broadcast lifecycle

Audit:

```text
TopwayMusicBroadcastBridge
TopwayMusicIntentFactory
WidgetComponent
HeadUnitMetadataPolicy
MediaSessionHolder
notification metadata path
WidgetRenderState
```

Implement/verify:

- `com.tw.music.info` only when effective metadata changes;
- `com.tw.launcher.music_progress_duration` while active or for deliberate clear/final state;
- stop progress after no-session/reset;
- no stale broadcasts after reset;
- metadata from same effective policy as MediaSession/widget/notification;
- progress/duration broadcast extras are milliseconds;
- widget progress bar uses seconds.

## Deliverable 6 — Metadata unification

Reduce divergence between:

- MediaSession metadata;
- notification metadata/content intent;
- WidgetRenderState;
- Topway metadata broadcast;
- Topway progress broadcast.

Focus on:

- title;
- subtitle;
- artist;
- album;
- album artist;
- duration;
- progress;
- artwork;
- no-session state;
- Now Playing tap route.

## Deliverable 7 — Runtime tests

Add tests beyond constants/factory/mappers.

Use `docs/topway/TOPWAY_BRIDGE_RUNTIME_TEST_MATRIX.md`.

Minimum new or expanded tests should cover:

- missing/unknown command;
- update command with widgets/no widgets where pure logic allows;
- seek Int/Long/missing/non-number/negative/beyond duration;
- widget no-session state;
- time formatting under/over one hour;
- progress clamping;
- metadata duplicate avoidance;
- request-code separation if touched;
- bridge duplicate/throttle behaviour if pure logic exists;
- manifest/guardrail allowed-path logic if feasible.

If Android framework tests are difficult, extract pure helper logic and test that.

## Deliverable 8 — Guardrails/manifest safety

Update `scripts/check-headunit-compat-safety.sh` if needed.

Allowed Topway strings only in:

```text
app/src/main/java/org/oxycblt/auxio/headunit/topway/
app/src/test/java/org/oxycblt/auxio/headunit/topway/
docs/
docs/topway/
AndroidManifest.xml only for the approved Topway bridge receiver action filters, if present
```

Still block:

```text
android.uid.system
sharedUserId
com.tw.service.xt
ITWCommandAidl
android.tw.john
direct vendor imports
reflection scanners
vendor package scanners
probe/evidence frameworks in APK
package impersonation
```

## Deliverable 9 — Docs sync after implementation

After code/tests, update concise status in:

```text
docs/DEVELOPMENT_ROADMAP.md
docs/TS18_REQUIREMENTS.md
docs/TS18_TIER1_HARDENING_NOTES.md
docs/TS18_NATIVE_PARITY_GAP_MATRIX.md
docs/TS18_INTEGRATION_ARCHITECTURE.md
docs/TW_ECOSYSTEM_SOURCE_MAP.md
docs/CODEX_TASK_PROMPTS.md
AGENTS.md
.github/copilot-instructions.md
```

Only state what is true.

Do not overclaim hardware validation.

## Non-goals

Do not add:

- package impersonation as `com.tw.music`;
- `android.uid.system`;
- `sharedUserId`;
- copied smali/resources;
- direct `com.tw.*` imports;
- direct `android.tw.john.*` imports;
- `com.tw.service.xt` runtime binder dependency;
- `ITWCommandAidl` runtime binding;
- TWUtil/TWClient reflection;
- vendor package scanners;
- in-app probes/evidence collectors;
- root/ADB-dependent APK logic;
- hidden diagnostics modules;
- full Media3 migration;
- broad UI rewrite.

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

Run Gradle where possible:

```sh
./gradlew --no-daemon --stacktrace spotlessCheck
./gradlew --no-daemon --stacktrace :app:testDebugUnitTest :musikr:testDebugUnitTest
./gradlew --no-daemon --stacktrace :app:lintDebug
./gradlew --no-daemon --stacktrace :app:assembleDebug
```

If local Gradle is blocked, classify the first real blocker and rely on GitHub/Copilot CI for final proof.

## Final response format

Your final response/comment must be provided as a triple-tilde unbroken markdown codeblock.

Use this structure:

## Executive summary

## Recommendation
Ready for PR #34 review / Needs another pass / Blocked by environment / Blocked by human decision

## PR #34 closure status

## Runtime implementation areas completed
- Receiver lifecycle/cold-state strategy
- Widget RemoteViews visual parity
- Widget stale/no-session clearing
- Widget update lifecycle parity
- Metadata/progress broadcast lifecycle
- MediaSession/notification/widget/Topway metadata unification
- Guardrails/manifest safety
- Tests
- Docs

## Files changed

## Receiver lifecycle/cold-state strategy

## Widget RemoteViews parity

## Widget stale/no-session clearing

## Widget update lifecycle

## Metadata/progress broadcast lifecycle

## Metadata unification

## Guardrails/manifest safety

## Tests added or updated

## Docs updates

## Product-code safety audit

## Validation commands and results

## Gradle/build status

## Remaining blockers or risks

## Recommended Copilot pre-merge focus
