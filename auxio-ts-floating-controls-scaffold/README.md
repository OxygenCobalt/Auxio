# Auxio-TS Car Floating Controls Scaffold

This scaffold gives Auxio-TS developers/agents a safe starting point for a first-party, persistent floating media-control overlay for the Topway/TS18 variant.

It is intentionally **not** a direct clone of Stallware Music Control/Dashdow Music. It is a clean implementation scaffold inspired by open-source floating media-control patterns, especially MediaFloat, and adapted for Auxio-TS as the music player itself.

## Goal

Implement an Auxio-TS native **Car Floating Controls** mode:

- persistent overlay over other apps/screens;
- previous / play-pause / next controls;
- optional Auxio open button;
- large TS18/head-unit touch targets;
- hides automatically while Auxio-TS itself is foreground;
- survives launcher/navigation/radio screen use;
- uses Auxio-TS playback state and commands directly where possible;
- avoids notification-listener dependency for first-party control;
- remains reversible and safe for the stock TS18/Topway environment.

## How to use this scaffold

From the Auxio-TS repo root on a new branch:

```sh
python3 path/to/auxio-ts-floating-controls-scaffold/scripts/install_scaffold.py --repo .
```

Then ask Codex/developers to integrate the copied files into the real project structure, package names, dependency graph, settings UI, and playback layer.

The scaffold files are deliberately placed under `app/src/topwayTwMusic/...` by default so the feature can be made variant-specific first. Codex may move shared pieces to `main` if that better fits Auxio-TS architecture.

## Contents

- `src/topwayTwMusic/java/.../CarFloatingControlsService.kt` - overlay runtime service skeleton.
- `src/topwayTwMusic/java/.../CarFloatingControlsView.kt` - simple large-button programmatic Android view.
- `src/topwayTwMusic/java/.../CarOverlayPlaybackBridge.kt` - integration seam for Auxio playback commands.
- `src/topwayTwMusic/java/.../CarOverlayPrefs.kt` - SharedPreferences state for enablement/position/opacity.
- `src/topwayTwMusic/java/.../CarOverlayPermissionActivity.kt` - overlay permission helper.
- `patches/AndroidManifest.overlay.snippet.xml` - manifest entries to merge/adapt.
- `docs/ARCHITECTURE.md` - implementation architecture.
- `docs/TEST_PLAN.md` - validation plan.
- `docs/CODEX_IMPLEMENTATION_BRIEF.md` - developer brief.
- `LICENSES/THIRD_PARTY_NOTICES.md` - reference/license notes.

## Important integration rule

The primary implementation should **not** send generic media key events as the main control path. Because this is built into Auxio-TS, it should call the real Auxio playback/controller/session layer directly. Generic media key dispatch is only a fallback.

## Stop/go points

Do not ship until these pass on the TS18/head unit:

1. Overlay permission request works.
2. Overlay appears over launcher/navigation/radio screens.
3. Overlay hides when Auxio-TS is opened.
4. Previous/play-pause/next operate Auxio-TS reliably.
5. Reboot and ACC sleep/wake do not leave duplicate overlays.
6. Reverse camera, CarPlay, Bluetooth, radio, and steering-wheel controls are not broken.
7. User can disable the feature and recover the normal app state.
