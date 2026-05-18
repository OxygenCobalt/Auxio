# Randomise-by-genre design (Pass 1)

## Scope

- **Feature:** Add a third shuffle state: `off -> shuffle all -> genre-filtered shuffle -> off`.
- **Primary UI target:** expanded playback panel shuffle control (`PlaybackPanelFragment` + `playback_shuffle`).
- **Pass 1 boundary:** investigation + architecture + minimal scaffolding only.

## 1) Existing playback/shuffle architecture found

- `PlaybackPanelFragment` binds `playbackShuffle` to `playbackModel.toggleShuffled()` and visual state to `PlaybackViewModel.isShuffled` (boolean).
- `PlaybackBarFragment` can expose shuffle via `ActionMode.SHUFFLE` and also calls `toggleShuffled()`.
- `PlaybackViewModel` currently exposes only boolean shuffle state and toggles via `playbackManager.shuffled(!isShuffled)`.
- `PlaybackCommand` uses `ShuffleMode` (`ON`, `OFF`, `IMPLICIT`) as **command-time** queue-construction intent; this is not a persistent three-state UI scope.
- `PlaybackStateHolder.RawQueue` represents queue using `heap + shuffledMapping`; queue shuffledness is currently inferred from non-empty mapping.
- ExoPlayer integration (`ExoPlaybackStateHolder`) uses `setShuffleModeEnabled(Boolean)` and `BetterShuffleOrder` for shuffled mapping.
- Persistence stores queue heap + shuffled mapping (`QueueShuffledMappingItem`) and recovers shuffledness from mapping presence.
- `MediaSessionHolder` maps shuffle to platform binary shuffle mode and notification action icon state.

## 2) Exact UI target

- Expanded panel button in all playback panel layouts:
  - `app/src/main/res/layout/fragment_playback_panel.xml`
  - `app/src/main/res/layout-h360dp/fragment_playback_panel.xml`
  - `app/src/main/res/layout-h520dp/fragment_playback_panel.xml`
- Keep mini-player/bar as secondary impact surface only.

## 3) Mini-player impact assessment

- `PlaybackBarFragment` uses `ActionMode.SHUFFLE` and only has one icon slot (`playbackSecondaryAction`).
- Recommendation: Pass 2 should keep bar behavior compatible by toggling through the same three-state cycle when `ActionMode.SHUFFLE` is active, but visual differentiation (“G” badge) is only required on expanded panel for this feature.

## 4) Internal state design recommendation

- Introduce a dedicated persistent state: `ShuffleScope` with `{OFF, ALL, GENRE}`.
- Keep existing `ShuffleMode` untouched for command-time queue initialization.
- Add `ShuffleScope` to playback state mirror + persistence in Pass 2.
- **Pass 1 scaffolding added:** `ShuffleScope` enum in playback state package.

## 5) Queue-building algorithm (genre shuffle)

When user activates genre shuffle from expanded panel:

1. Read current song from playback state.
2. Read `Song.genres` (parsed canonical model from Musikr/PR#4 prerequisite).
3. If current song has no genres, fallback behavior should be deterministic (recommended: transition to `ALL` shuffle with user feedback toast, or no-op + message; decide in Pass 2).
4. Build candidate set from library songs where `candidate.genres` shares at least one genre with current song (OR semantics).
5. Deduplicate by `Song.uid` to avoid duplicates from multi-genre overlap.
6. Ensure current song exists exactly once in candidate queue.
7. Build final queue as `currentSong + shuffled(remainingCandidates)` to preserve current playback continuity.
8. Preserve repeat mode and current playback position while replacing queue.
9. Keep queue parent as `null` (all songs context) unless a dedicated virtual parent is introduced later.

## 6) Current song preservation

- Use explicit queue construction where current song is index 0 (or mapped current index) before enabling shuffled playback ordering for remainder.
- Avoid triggering song switch on mode transition whenever possible.

## 7) Duplicate match removal

- Deduplicate candidates by `Song.uid` while collecting union matches across current-song genres.
- Do not deduplicate by title/artist strings.

## 8) Interaction with normal shuffle and ExoPlayer shuffle

- `ShuffleScope.ALL`: existing behavior (`playbackManager.shuffled(true)` and `BetterShuffleOrder`) remains authoritative.
- `ShuffleScope.GENRE`: queue is pre-filtered first, then shuffled normally via existing ExoPlayer shuffle mechanism.
- `ShuffleScope.OFF`: existing non-shuffled queue behavior.
- This keeps Media3/ExoPlayer binary shuffle flag semantics intact while scope is handled at app-layer queue construction.

## 9) MediaSession/notification/Android Auto view of state

- Platform/media session shuffle remains binary for compatibility (`SHUFFLE_MODE_ALL` vs `NONE`).
- Genre scope is app-private state for UI semantics; do not expose non-standard platform shuffle constants.
- Notification action remains standard shuffle action; expanded panel provides genre badge differentiation.

## 10) Persistence/resume decision

- `ShuffleScope` is persisted alongside queue payload state.
- Resume logic restores the persisted queue heap + shuffled mapping exactly as saved, then restores
  persisted `ShuffleScope` with safety normalization:
  - if restored queue is not shuffled, scope is forced to `OFF`;
  - if restored queue is shuffled and stored scope is `OFF`, scope is normalized to `ALL`;
  - if restored queue is shuffled and stored scope is `GENRE`, `GENRE` is preserved.
- Current implementation restores the previously generated genre queue directly (it does not
  regenerate from library snapshot on startup).

## 11) Test plan

Pass 1 added a pure unit-test target (`GenreShuffleQueueSelectorTest`) and Pass 2 should expand coverage with fixtures covering:

- current song genres: Pop, Rock, Pop-punk, Emo;
- candidates with Pop, Rock, Pop-punk, Emo, Pop+Rock, Jazz, unknown/empty;
- OR/union matching;
- dedupe across multi-genre overlaps;
- current song appears once;
- deterministic ordering by injecting seeded `Random` or by splitting “selection” and “shuffle” phases.

## 12) Pass 2 implementation checklist

1. Add `shuffleScope` to playback mirror state and listener callbacks.
2. Add persistence column/field + migration for `ShuffleScope`.
3. Add `PlaybackViewModel` APIs for 3-state cycle (expanded panel target first).
4. Add queue-construction helper for genre scope using `Song.genres` and `MusicRepository.library.songs`.
5. Wire expanded panel shuffle button to cycle through 3 states.
6. Add expanded panel icon badge “G” and 3 content descriptions.
7. Keep mini-player behavior consistent with state cycle (no major redesign).
8. Ensure MediaSession/notification uses binary shuffle exposure while remaining internally consistent.
9. Add unit tests for candidate selection + dedupe + current-song preservation.
10. Validate with Gradle test/assemble/lint and targeted playback checks.

## 13) Pass 2 status

- Implemented 3-state shuffle cycle in playback controls: `OFF -> ALL -> GENRE -> OFF`.
- Genre queue generation now uses parsed model relationships (`Song.genres`) and union matching.
- Genre queue candidate gathering uses `Genre.songs` relationships (plus UID dedupe) to avoid
  scanning the full library on activation.
- Added genre shuffle indicator icon variant for expanded panel control and distinct accessibility labels.
- Persist/restore path now retains `GENRE` as an explicit runtime/source-of-truth scope in
  `PlaybackStateManager` and mirrors that scope to UI listeners.

## 14) Manual validation checklist

1. Prepare a library containing Pop, Rock, Pop-punk, Emo, Pop+Rock, and Jazz tracks.
2. Start playback of a song tagged with Pop/Rock/Pop-punk/Emo via parsed model genres.
3. Open expanded playback panel.
4. Tap shuffle once: verify normal shuffle is enabled.
5. Tap shuffle again: verify genre-filtered shuffle is enabled and shuffle icon shows genre badge.
6. Confirm queue contains only tracks sharing at least one current-song genre.
7. Confirm Jazz-only tracks are excluded.
8. Confirm current song appears exactly once.
9. Confirm play/pause/next/previous still operate correctly.
10. Confirm notification/media controls continue to function.
11. On TS18 validation pass, confirm steering-wheel/media keys and launcher widget behavior remains stable.

## PR#4 dependency check (Pass 1 status)

- **Observed:** codebase already uses parsed `Song.genres` and typed `Genre` relationships (`Song.genres`, `Genre.songs`, `PlaybackCommand.songFromGenre`).
- **Porting decision:** **Directly reusable requirement** for this feature’s queue selection.
- **TS18 runtime status:** **Requires TS18 validation** for final UX parity only; core algorithm design is upstream-style and platform-agnostic.

- Added selector tests for deterministic seeded random order and no-match fallback behavior
  (`current` remains the sole queue item).
- Playback-state persistence cycle tests are still pending due current lack of lightweight playback
  state test fixtures in `app` unit tests.
