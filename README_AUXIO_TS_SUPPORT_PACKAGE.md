# Auxio-TS support package

Commit this package into `cbkii/Auxio-TS` as the initial TS18/TW integration planning and agent-instruction layer.

This package intentionally contains **documentation, instructions, and validation scripts only**. It does not modify Auxio application code yet. That is deliberate: the attached TS18 diagnostics prove there is a real TW/TWTHEME integration surface, but the first code changes should be evidence-driven and small.

## What this package is for

Auxio-TS aims to become a TS18-focused fork of Auxio that preserves Auxio's maintainable Android media-player core while adding a dedicated TS18/TW compatibility layer.

Target outcomes:

- Android native media notifications and controls.
- MediaSession/MediaLibraryService behaviour suitable for Android Auto-style clients.
- FLAC and local library playback, preserving Auxio's Media3/ExoPlayer strengths.
- TS18 launcher/home widget compatibility.
- TS18 media keys / steering-wheel key compatibility.
- TWTHEME awareness, including MusicTheme/resource-path investigation.
- ZLink/TLink/phone-link metadata/control validation.
- No broad, blind porting of `com.tw.music` smali into Auxio.

## Important source material

- `docs/TS18_DIAGNOSTICS_INSIGHTS.md` summarises the attached TS18 diagnostics.
- `diagnostics/redacted/ts18_device_profile.json` is a structured, redacted device profile.
- `docs/TS18_REQUIREMENTS.md` is the high-level requirement set.
- `docs/TS18_INTEGRATION_ARCHITECTURE.md` defines the intended adapter-layer approach.
- `AGENTS.md` and `.github/copilot-instructions.md` define how agents should work on this fork.

## Non-goals for the first implementation PRs

- Do not rename the package to `com.tw.music` until evidence proves it is needed and safe.
- Do not request `android.uid.system` or privileged permissions in a normal installable app.
- Do not replace stock `com.tw.music` yet.
- Do not introduce vendor APKs, copied APKs, raw diagnostics, serial numbers, or proprietary binaries into the repo.
- Do not degrade upstream Auxio's standard Android media behaviour to chase TS18-specific behaviour.

## Suggested first PR sequence

1. Commit this package unchanged.
2. Run `scripts/ts18_collect_auxio_ts_evidence.sh` against stock `com.tw.music`, upstream Auxio, and Auxio-TS debug builds.
3. Add a feature-flagged TS18 compatibility module only after the evidence shows which surface is missing.
4. Validate every TS18-specific behaviour against a before/after evidence bundle.
