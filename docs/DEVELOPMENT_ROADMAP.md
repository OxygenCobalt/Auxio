# Auxio-TS Development Roadmap (staged PR plan)

Each phase should be one primary variable per PR.

## Phase 0 — Documentation/source-corpus hardening
- **Goal:** finalize evidence taxonomy, source map, and planning guardrails.
- **Likely files:** `docs/*.md`, `AGENTS.md`, `.github/copilot-instructions.md`.
- **Validation:** markdown review + script syntax checks.
- **Stop conditions:** unresolved contradictions in policy/evidence model.
- **Human TS18 checks required:** none.
- **Expected artifacts:** updated source map, contract catalog, comparison docs.

## Phase 1 — Build/install baseline
- **Goal:** ensure reproducible local/CI build steps for Auxio-TS branch.
- **Likely files:** build docs, non-functional config fixes only.
- **Validation:** `./gradlew tasks assembleDebug test lint` + script checks.
- **Stop conditions:** environment/toolchain blockers not controlled by repo.
- **Human TS18 checks required:** optional install sanity check.
- **Expected artifacts:** baseline build report and blocker log.

## Phase 2 — Stock `com.tw.music` baseline capture
- **Goal:** collect stock playback evidence matrix on TS18.
- **Likely files:** runbook/docs/scripts only.
- **Validation:** successful evidence captures for stock scenarios.
- **Stop conditions:** capture scripts missing required permissions/data.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** redacted stock evidence bundle + summary table.

## Phase 3 — Auxio-TS standard Android MediaSession validation
- **Goal:** validate Auxio-TS behavior on TS18 using only standard Android media path.
- **Likely files:** docs/runbook and minimal observability hooks if needed.
- **Validation:** session active state, notification controls, media keys, audio focus.
- **Stop conditions:** baseline Android path not stable.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** Auxio-TS baseline evidence bundle.

## Phase 4 — Stock vs Auxio session/notification/audio-focus comparison
- **Goal:** produce gap analysis before any private contract work.
- **Likely files:** comparison docs/scripts.
- **Validation:** A/B evidence comparisons for equivalent playback scenarios.
- **Stop conditions:** ambiguous or non-reproducible differences.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** gap matrix with risk-ranked hypotheses.

## Phase 5 — Launcher/widget/TWTHEME evidence capture
- **Goal:** determine if launcher or theme integration requires non-standard contracts.
- **Likely files:** runbook, source map, contract table updates.
- **Validation:** stock vs Auxio launcher/widget behavior captures.
- **Stop conditions:** no reliable reproduction path.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** launcher/TWTHEME behavior report.

## Phase 6 — TS18 adapter skeleton
- **Goal:** add no-op adapter facade and contract registry.
- **Likely files:** new `integration/ts18` package and wiring docs.
- **Validation:** no behavior change when adapters disabled.
- **Stop conditions:** adapter leaks into core playback/indexing logic.
- **Human TS18 checks required:** recommended.
- **Expected artifacts:** adapter skeleton PR with default-off flags.

## Phase 7 — Targeted TW contract experiments
- **Goal:** test one contract at a time (e.g., launcher adapter or TW bridge).
- **Likely files:** isolated adapter module + diagnostics updates.
- **Validation:** pre/post evidence for single contract hypothesis.
- **Stop conditions:** requires privileged/system behavior or unstable side effects.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** experiment report, rollback notes, next-step recommendation.

## Phase 8 — FLAC/audio-quality/sleep-resume validation
- **Goal:** ensure robust playback quality and lifecycle behavior in TS18 context.
- **Likely files:** playback tuning docs/tests + optional guarded fixes.
- **Validation:** FLAC matrix, navigation-mixing, sleep/resume scenarios.
- **Stop conditions:** regressions in mainstream playback behavior.
- **Human TS18 checks required:** mandatory.
- **Expected artifacts:** validation matrix with pass/fail evidence.

## Phase 9 — Release-candidate hardening
- **Goal:** produce candidate with transparent risk ledger and reproducible validation record.
- **Likely files:** docs/release checklist/changelog.
- **Validation:** full required command set + latest TS18 evidence rerun.
- **Stop conditions:** unresolved high-risk hypotheses.
- **Human TS18 checks required:** final sign-off.
- **Expected artifacts:** release-readiness dossier and remaining blocker list.
