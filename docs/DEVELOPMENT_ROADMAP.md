# Auxio-TS Development Roadmap

## Phase 0 — Repo/Instruction hardening
- **Goal:** lock safe agent guidance, requirements taxonomy, and validation workflow.
- **Likely files:** `AGENTS.md`, `.github/copilot-instructions.md`, `docs/*.md`, `scripts/*.sh` (light touch).
- **Validation:** docs consistency review, shell syntax checks, baseline gradle commands.
- **Stop conditions:** conflicting policy, unclear ownership, missing TS18 evidence baseline.
- **Human TS18 checks:** none required.

## Phase 1 — Baseline build and upstream preservation
- **Goal:** ensure repo builds/tests/lint and identify upstream seams to preserve.
- **Likely files:** build configs, minimal non-behavioural fixes.
- **Validation:** `tasks`, `assembleDebug`, `test`, `lint`.
- **Stop conditions:** changes require invasive architecture rewrites.
- **Human TS18 checks:** optional install sanity check.

## Phase 2 — TS18 evidence capture baseline
- **Goal:** gather reproducible stock vs Auxio-TS evidence with redacted outputs.
- **Likely files:** scripts/runbook/docs only.
- **Validation:** capture script dry-runs + real device captures.
- **Stop conditions:** no reliable capture path or privacy redaction gaps.
- **Human TS18 checks:** required (device runtime capture).

## Phase 3 — MediaSession/notification validation
- **Goal:** confirm Android-native media path works on TS18 before TW-private hooks.
- **Likely files:** media-session/service wiring, tests, diagnostics.
- **Validation:** runbook stages for session/notification/keys/audio focus.
- **Stop conditions:** requires privileged APIs.
- **Human TS18 checks:** required.

## Phase 4 — TS18 adapter skeleton
- **Goal:** introduce isolated TS18 adapter interfaces with default no-op behaviour.
- **Likely files:** new `integration/ts18` package + DI wiring.
- **Validation:** no regression in standard behaviour; feature flags default-off.
- **Stop conditions:** adapter leaks into core playback logic.
- **Human TS18 checks:** recommended.

## Phase 5 — Launcher/TWTHEME/TW compatibility experiments
- **Goal:** test minimal proven adapters where standard path fails.
- **Likely files:** TS18 bridge implementations, diagnostic toggles, docs.
- **Validation:** A/B evidence against stock and third-party media apps.
- **Stop conditions:** unproven private contract, package impersonation pressure.
- **Human TS18 checks:** mandatory.

## Phase 6 — FLAC/audio-quality validation
- **Goal:** verify robust FLAC/local-library playback and TS18 audio policy compatibility.
- **Likely files:** playback configuration/tuning, diagnostics, tests.
- **Validation:** format matrix + sleep/resume + navigation-mixing checks.
- **Stop conditions:** regressions in mainstream formats.
- **Human TS18 checks:** mandatory.

## Phase 7 — Release/test packaging
- **Goal:** produce reproducible candidate with clear risk ledger and validation evidence.
- **Likely files:** release notes, CI/workflow docs, packaging metadata.
- **Validation:** full regression + documented TS18 evidence summary.
- **Stop conditions:** unresolved high-risk TS18 contract assumptions.
- **Human TS18 checks:** final sign-off required.
