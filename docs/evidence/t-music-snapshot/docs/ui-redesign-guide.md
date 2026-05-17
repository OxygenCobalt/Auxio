# UI Redesign Readiness Guide (TS18 Android 13)

## Target baseline

Future UI redesign work must target:

- TS18-class Android head unit
- Android 13 runtime environment
- 1280×720 landscape baseline
- Large, low-distraction in-car controls
- Glanceable playback info (title/artist/state)
- Minimal animation and memory-conscious rendering
- Full TWTHEME compatibility

## Allowed improvement areas

- Improve control sizing and spacing for driving ergonomics.
- Improve hierarchy/readability of now-playing metadata.
- Improve list readability and touch target consistency.
- Improve split-screen resilience for launcher/coexistence layouts.
- Improve contrast/readability while remaining theme-driven.

## Forbidden/high-risk changes (without explicit validation plan)

- Breaking `@style/AppTheme` / TWTHEME integration.
- Renaming/removing resource keys consumed by widget/vendor paths.
- Introducing heavy animated surfaces that increase distraction or memory churn.
- Introducing portrait-first assumptions or narrow-phone layout bias.

## Layout and interaction constraints

- Primary controls: at least 48dp touch targets; key transport controls preferably 56–64dp effective targets.
- Keep primary actions on stable, predictable zones for glance operation.
- Avoid dense nested layouts that increase render cost on 4GB-class devices.
- Keep animation subtle and optional; prioritize deterministic UI state.

## Widget consistency requirements

- Main app playback state and widget state must remain semantically aligned.
- Resource updates that affect shared drawables/strings/layout references must verify widget compatibility.
- Do not change widget-facing identifiers without explicit compatibility audit.

## Artwork and memory risk guidance

- Avoid large in-memory artwork decode spikes.
- Prefer bounded artwork dimensions appropriate to 1280×720 UI needs.
- Validate fallback placeholders for missing/invalid art.

## Split-screen / multi-window considerations

- TS18 launcher environments may present split contexts; layouts should degrade gracefully.
- Ensure controls remain operable and text remains legible at reduced effective width.

## Accessibility/readability guidance

- Favor high-contrast text and icon legibility.
- Ensure state changes (play/pause/buffering/error) are clearly perceivable.
- Avoid overloading one view with too many simultaneous cues.

## Future PR boundaries

UI redesign PRs should be split into small, reviewable scopes:

1. Layout-only adjustments.
2. Iconography/style refinements.
3. Widget parity checks.
4. Device validation evidence updates.

No runtime media-engine swaps or vendor-token changes in UI-focused PRs.
