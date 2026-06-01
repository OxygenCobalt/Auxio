# Codex Implementation Brief

Implement a production-quality Auxio-TS first-party persistent floating media-control overlay for the Topway/TS18 variant.

## Scope

Create an Auxio-TS feature tentatively called **Car Floating Controls**.

Required behaviour:

- provides an Android overlay above other apps/screens;
- previous/play-pause/next controls are always reachable;
- optionally includes an Auxio launch button;
- uses large, TS18/head-unit-safe touch targets;
- hides automatically whenever Auxio-TS itself is foreground;
- reappears when Auxio-TS is backgrounded if enabled;
- persists overlay position/size/opacity/settings;
- avoids duplicate overlays across service restarts, app restarts, and ACC sleep/wake;
- has user-facing enable/disable and reset controls;
- integrates with the actual Auxio playback/session/controller layer, not generic media key events as the primary path;
- is limited to the Topway/TS18 variant at first unless the repo architecture clearly supports clean generalisation.

## Development approach

1. Inspect the current Auxio-TS repo structure, flavours, package names, settings architecture, playback architecture, service architecture, DI/state architecture, Compose/XML conventions, tests, CI, release workflows, and Topway variant source sets.
2. Adapt this scaffold rather than blindly copying it. Rename packages, files, and APIs to match the repo.
3. Use the existing Auxio player/session/controller source of truth for playback commands and state.
4. Integrate settings into the existing settings UI and persistence system.
5. Use existing notification/foreground-service patterns if the app already has them.
6. Add manifest entries only where necessary and variant-scope them if possible.
7. Add tests where practical and keep CI passing.

## Non-goals

- Do not decompile or copy Stallware Music Control/Dashdow Music code/resources.
- Do not require root, Shizuku, Accessibility, UsageStats, or notification listener for Auxio's own playback control.
- Do not delete or depend on replacing `com.tw.music`.
- Do not break standard Android MediaSession/notification controls.
- Do not show over Auxio-TS itself unless the user explicitly disables the hide-while-Auxio-open option.

## Acceptance criteria

- `topwayTwMusic` debug and release variants build.
- Overlay permission flow is discoverable and recoverable.
- Overlay appears over launcher/navigation/radio-style screens.
- Overlay hides when Auxio-TS opens and returns when Auxio-TS backgrounds.
- Previous/play-pause/next work on Auxio-TS directly.
- Overlay position survives app restart/reboot.
- Disable setting fully removes overlay and stops any dedicated overlay service.
- Repeated start/stop/toggle calls do not create duplicate windows.
- CI, lint, unit tests, and relevant screenshot/Roborazzi checks pass or are updated appropriately.
- Topway release artefacts still build and are not regressed.
