# TS18 Tier 1 operational parity hardening note (May 2026)

## Current Tier 1 surfaces audited
- Head-unit entry points and routing via app intents, dynamic shortcuts, and home quick-pick actions.
- Main activity warm-start/onNewIntent routing.
- MediaBrowser root/tab behavior and placeholder fallback semantics.
- MediaSession/notification/media-button/audio-focus/service lifecycle pathways (existing implementation retained in this pass).
- Widget and quick-access render-state consistency (existing implementation retained in this pass).

## Gaps addressed in this pass
- Added a canonical `HeadUnitRoute` + `HeadUnitRoutePolicy` model for action->route mapping and route->destination mapping.
- Consolidated action routing so `HeadUnitEntryPoints.destinationForAction` and `MainActivity` share one route model.
- Added unit tests for full action map coverage, unknown action safety, and queue quick-pick routing policy.

## What remains for TS18 validation
- All TS18-STD scenarios still require real hardware runtime evidence.
- No Tier 3/Tier 4 native/private integration was added in this pass.
- MediaBrowser and session publication behaviors remain Android-standard Tier 1 and require Tier 2 validation on TS18/TWTHEME hardware.
