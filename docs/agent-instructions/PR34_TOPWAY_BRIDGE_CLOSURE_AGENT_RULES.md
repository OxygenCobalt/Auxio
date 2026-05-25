# PR #34 Topway Closure Agent Rules

Use these rules for PR #34 completion work.

## Primary objective

Finish the isolated Topway bridge runtime implementation, not another documentation-only pass.

## Mandatory closure areas

1. Receiver lifecycle strategy.
2. Widget RemoteViews parity.
3. Widget stale/no-session clearing.
4. Widget update lifecycle parity.
5. Metadata/progress broadcast lifecycle.
6. MediaSession/notification/widget/Topway metadata unification.
7. Runtime edge tests.
8. Guardrails/manifest safety.
9. Docs status sync after code.

## Important implementation distinction

The official Topway widget uses two different progress units:

- widget `RemoteViews` progress bar: seconds;
- `com.tw.launcher.music_progress_duration` broadcast extras: milliseconds.

Do not conflate them.

## Receiver guidance

Stock Topway:

- starts `MusicService` from the widget provider;
- uses explicit service PendingIntents for widget controls;
- dynamically registers the music service command receiver;
- dynamically registers the launcher seek receiver in `MusicActivity`.

Auxio-TS may need a static/exported receiver for compatibility when external iLauncher broadcasts arrive while Auxio-TS is cold, but this must be explicit, narrow, and safe.

## Safety rules

Never add:

- `android.uid.system`
- `sharedUserId`
- package impersonation as `com.tw.music`
- copied smali/resources
- direct vendor imports
- XT/AIDL runtime binder dependency
- `ITWCommandAidl` binding
- vendor scanners
- in-app probe/evidence capture code

Allowed Topway action/extra strings must stay isolated in the bridge/test/docs scope, plus manifest receiver filters if required.

## Stop condition

Do not report ready if only constants/mappers are added.

Do not report ready if widget time/duration/progress is absent.

Do not report ready if no tests are added for runtime edges.
