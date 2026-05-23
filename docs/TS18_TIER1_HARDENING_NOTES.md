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

## TS18 Head-Unit Experience Mode batch (current pass)
- Added `HeadUnitDashboardPolicy` + `HeadUnitDashboardState` to drive head-unit dashboard entries (label/icon/route/enabled state) from one policy layer.
- Home dashboard chips are now policy-driven and include playback, queue, shuffle, recently added, artists, albums, genres, playlists, favourites, and head-unit settings.
- Queue UX now includes actionable empty-state recovery (`Shuffle`) and larger list padding when large controls are enabled.
- Playback panel now scales primary control touch targets when large controls mode is enabled.
- Routing alignment improved for playlists, and head-unit settings route fallback from dashboard.

Status: Tier 1 implementation only. No TS18 hardware validation success is claimed.

- FOLDERS and DECADES remain metadata filters, not dashboard routes; dashboard settings now uses explicit HEAD_UNIT_SETTINGS action semantics.
