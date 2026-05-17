# Agent task prompts

## Prompt 1 — Commit package and inspect Auxio baseline

```markdown
You are working on `cbkii/Auxio-TS`, a TS18-focused fork of Auxio.

First, read `AGENTS.md`, `docs/TS18_REQUIREMENTS.md`, `docs/TS18_DEVICE_PROFILE.md`, and `docs/TS18_INTEGRATION_ARCHITECTURE.md`.

Do not implement TS18 hooks yet.

Task:
1. Verify this support package is committed cleanly.
2. Inspect upstream Auxio's current Media3/MediaSession/MediaLibraryService architecture.
3. Identify where TS18-specific diagnostics can be added without changing playback behaviour.
4. Produce a short implementation plan for a first no-risk diagnostics PR.

Rules:
- No package rename.
- No privileged permissions.
- No replacement of `com.tw.music`.
- No vendor APKs or raw diagnostics committed.

Final response:
- Summary
- Files inspected
- Proposed first PR
- Risks
- Commands run
```

## Prompt 2 — Add TS18 passive diagnostics only

```markdown
Implement a passive TS18 diagnostics layer for Auxio-TS.

Scope:
- Runtime detect likely TS18 environment using packages/properties only.
- Add debug/export output for MediaSession state, package identity, device profile, Android version, audio focus-related visible state, and app settings.
- Do not change playback logic.
- Do not emit TW broadcasts.
- Do not add private permissions.

Acceptance:
- Build passes.
- Normal Auxio behaviour unchanged when TS18 mode disabled.
- On TS18, diagnostics can be exported and compared with `scripts/ts18_collect_auxio_ts_evidence.sh`.
```

## Prompt 3 — Standard MediaSession validation

```markdown
Verify and harden Auxio-TS standard Android media integration for the TS18 Android 10 target.

Scope:
- Confirm active MediaSession during playback.
- Confirm media notification controls.
- Confirm media-button routing.
- Confirm audio focus and noisy-intent behaviour.
- Confirm Android Auto / MediaLibraryService behaviour where supported by Auxio.
- Add/adjust tests or docs only where safe.

Do not add private TW/TWTHEME hooks yet.
```

## Prompt 4 — TS18 native contract investigation

```markdown
Using fresh TS18 evidence bundles, determine whether private TW/TWTHEME hooks are needed.

Compare stock `com.tw.music`, Spotify/Poweramp/other installed third-party player, upstream Auxio, and Auxio-TS.

Determine:
- Does launcher/home widget respond to standard MediaSession?
- Do media keys route through Android session dispatch?
- Does ZLink/TLink consume standard metadata?
- Is `com.tw.music` identity required?
- Are private broadcasts or `com.tw.service` APIs visible?

Output a recommendation before coding.
```
