# TS18 Tier 1 operational parity hardening note (May 2026)

## Current Tier 1 surfaces audited
- Head-unit entry points and routing via app intents, dynamic shortcuts, and home quick-pick actions.
- Main activity warm-start/onNewIntent routing.
- MediaBrowser root/tab behavior and placeholder fallback semantics.
- MediaSession/notification/media-button/audio-focus/service lifecycle pathways.
- Widget and quick-access render-state consistency.

## Gaps addressed in this pass
- Kept canonical `HeadUnitRoute` + `HeadUnitRoutePolicy` action→route→destination mapping as the single route authority for `MainActivity`, `HeadUnitEntryPoints`, and quick-pick handling.
- Reconfirmed route handling safety for unknown actions and queue/now-playing/favourites/settings destinations.
- Revalidated policy-driven dashboard entries, queue empty-state recovery action, and large-controls behavior wiring.
- Re-ran product-code safety checks for prohibited private TW/TS18 integrations in product code paths.

## What remains for TS18 validation
- All TS18-STD scenarios still require real hardware runtime evidence.
- No Tier 3/Tier 4 native/private integration was added in this pass.
- MediaBrowser and session publication behaviors remain Android-standard Tier 1 and require Tier 2 validation on TS18/TWTHEME hardware.

## TS18 Head-Unit Experience Mode batch (current baseline)
- `HeadUnitDashboardPolicy` + `HeadUnitDashboardState` drive dashboard entries (label/icon/route/enabled state) from one policy layer.
- Home dashboard chips are policy-driven and include playback, queue, shuffle, recently added, artists, albums, genres, playlists, favourites, and head-unit settings.
- Queue UX includes actionable empty-state recovery (`Shuffle`) and larger list padding when large controls are enabled.
- Playback panel scales primary control touch targets when large controls mode is enabled.
- Routing alignment includes playlists and head-unit settings fallback from dashboard.
- `FOLDERS` and `DECADES` stay explicit metadata/home filters, not fake head-unit route destinations.

Status: Tier 1 implementation only. No TS18 hardware validation success is claimed.

## Tier 1 implementation completion matrix (pre-next-phase baseline)

| Surface | Status | Notes |
|---|---|---|
| Dashboard/home | Implemented — requires TS18 validation | Policy-driven entries with runtime enabled/disabled state from library/index/favourites state. |
| Entry routing / warm-start | Implemented — requires TS18 validation | `MainActivity` intent action mapping preserves explicit entry destinations via `EXTRA_ENTRY_DESTINATION`; unknown actions fail safely. |
| Dynamic shortcuts | Implemented — requires TS18 validation | Dynamic shortcuts are generated from canonical entry points and route through `MainActivity`. |
| Widget entry/actions | Implemented — requires TS18 validation | Widget root/action intents use deterministic request-code policy and standard app/service intents. |
| Queue/up-next | Implemented | Queue empty-state recovery and queue route opening are wired and tested. |
| Playback panel / large controls | Implemented | Large-control mode affects runtime control target sizing/spacing without changing playback semantics. |
| Settings/runtime preferences | Implemented | Head-unit dashboard visibility and large-control preferences have direct runtime UI effects. |
| MediaSession/notification | Implemented — requires TS18 validation | Standard publication/clearing behavior present; requires TS18 launcher/widget confirmation on hardware. |
| MediaBrowser/controller browsing | Implemented — requires TS18 validation | Stable root/common nodes plus placeholder fallback for unknown/malformed IDs. |
| Media buttons | Implemented — requires TS18 validation | Standard key handling includes headset hook, action-down only, repeat filtering, and safe no-song behavior. |
| Audio focus | Implemented — requires TS18 validation | Duck/pause/resume policy prevents autoplay on gain when stopped/no-song. |
| Validation/runbook readiness | Implemented | Runbook + gap matrix can now be used as Tier 2 baseline with no Tier 3/4 production changes. |

## Before Tier 3/4 investigation
- Complete Tier 2 TS18 runtime validation evidence for all `Requires TS18 validation` rows above.
- Promote only evidence-backed gaps through formal gap-and-promotion process (Tier 2 → Tier 3 → Tier 4 design PR).
- Keep private/native contracts not for production by default unless explicit human-approved design PR confirms production eligibility criteria.
