# Auxio-TS Documentation Index

Start here for all TS18/TW/TWTHEME work.

## Active guidance (canonical)
1. TS18/TW/TWTHEME source-led development policy for TS18-specific source/workflow ordering.
2. Official Android/MediaSession/MediaBrowser/audio-focus APIs as implementation baseline.
3. Public TS18/Topway/DoFun/TW/TWTHEME/head-unit sources as compatibility/context evidence for TS18-specific behaviours.
4. Local `t-music` and TS18 diagnostics as evidence and validation context.
5. External validation scripts/runbooks as acceptance evidence.
6. Probe/diagnostics-driven investigation only as last resort.

## Canonical live docs (active guidance)
- `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- `TW_ECOSYSTEM_SOURCE_MAP.md`
- `TS18_REQUIREMENTS.md`
- `TS18_INTEGRATION_ARCHITECTURE.md`
- `TS18_VALIDATION_RUNBOOK.md`
- `TS18_NATIVE_CONTRACTS.md`
- `DEVELOPMENT_ROADMAP.md`
- `CODEX_TASK_PROMPTS.md`
- `RESEARCH_SOURCES.md`

## Other current docs (supporting)
- `TS18_DEVICE_PROFILE.md`
- `TS18_DIAGNOSTICS_INSIGHTS.md`
- `TMUSIC_TO_AUXIO_TS_COMPARISON.md`
- `RELEASE_WORKFLOW.md`
- `RANDOMISE_BY_GENRE_DESIGN.md`

## Live docs vs evidence policy
- Live docs in `docs/*.md` are active source-of-truth.
- `docs/evidence/**` is preserved provenance/evidence only.
- Evidence snapshots may contain private-hook references and legacy assumptions; they are not implementation guidance.
- Routine guardrail scans should exclude `docs/evidence/**` by default.
- Evidence-inclusive audits must be explicitly requested.

## Agent/developer entry points
- Repo authority: `../AGENTS.md`
- Copilot/Codex policy: `../.github/copilot-instructions.md`
- Codex environment setup/maintenance/validation: `codex/README.md`

- `TS18_NATIVE_PARITY_GAP_MATRIX.md`
- `TS18_HARDWARE_VALIDATION_CHECKLIST.md`

- `TS18_STOCK_TMUSIC_PARITY_LEDGER.md`
- `TS18_NATIVE_CONTRACT_INVENTORY.md`
- `evidence/ts18/README.md`
