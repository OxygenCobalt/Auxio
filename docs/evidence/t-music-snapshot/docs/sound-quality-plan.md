# Sound Quality Preservation and Validation Plan

## Scope

This plan defines how to preserve audio quality while implementing future media/session/UI improvements. It does not alter runtime behaviour directly.

## Stack protection boundaries

- Preserve TW playback path (`TWMediaPlayer` / `IjkMediaPlayer`) as the core decode/output stack.
- Preserve property-driven behaviour boundaries:
  - `persist.tw.ijk`
  - `persist.tw.ijk.noerror`
  - `persist.tw.ijk.opensles`
  - any active `persist.media.*` interactions discovered in runtime/code review
- Do not replace TW/IJK path (e.g., ExoPlayer/Media3 swap) without explicit compatibility proof on TS18.

## Audio path assumptions to validate

1. OpenSLES path enablement/disablement behavior remains controlled by TW property flags.
2. EQ/DSP integration (including `com.tw.eq/.EQActivity` hand-off assumptions) remains functional.
3. Audio session ID continuity remains compatible with EQ and vendor hooks.
4. Audio focus behaviour supports navigation prompt ducking without false state regressions.

## Quality-focused validation topics

- Sample-rate handling (common 44.1/48 kHz and higher-rate files where available).
- Bit-depth handling (16-bit baseline and 24-bit FLAC where available).
- Duration accuracy and seek precision across formats.
- Metadata and artwork consistency between service, widget, and media session projection.
- Gapless/transition behaviour where discoverable from current player path.

## Required runtime/log evidence before quality claims

Do not claim codec or quality support until evidence includes:

1. Runtime playback verification on TS18 Android 13.
2. Logcat evidence around decoder/path selection and seek/duration behavior.
3. User-visible validation for metadata/artwork and control coherence.
4. Regression comparison against existing MP3/AAC/M4A baseline behaviour.

## Validation checklist

- [ ] FLAC 16-bit playback start/stop/seek/duration/metadata/artwork validated.
- [ ] FLAC 24-bit playback start/stop/seek/duration/metadata/artwork validated.
- [ ] ALAC sample playback validated (if test media available).
- [ ] Seek accuracy confirmed (forward/backward near boundaries and mid-track).
- [ ] Duration accuracy confirmed against known-good references.
- [ ] Metadata accuracy confirmed (title/artist/album/track fields).
- [ ] Album art extraction/display behaviour confirmed.
- [ ] EQ still works after playback/session changes.
- [ ] Navigation prompt ducking does not corrupt playback state publication.
- [ ] No regressions in MP3/AAC/M4A playback behavior.
