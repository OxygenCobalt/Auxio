# Auxio-TS Documentation Index

This is the primary docs entry point for contributors and agents.

## Canonical precedence for TS18/TW/TWTHEME work
1. **TS18/TW/TWTHEME source-led development policy**.
2. **Official Android/Media3/MediaSession/audio-focus APIs** as implementation baseline.
3. **Public TS18/Topway/DoFun/TW/TWTHEME/head-unit sources** as compatibility evidence.
4. **Local `t-music` snapshot + TS18 diagnostics** as evidence/validation context.
5. **External validation scripts and runbooks** as acceptance evidence.
6. **Probe/diagnostics-driven investigation only as last resort**.

## Live canonical docs (active guidance)
- `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- `TW_ECOSYSTEM_SOURCE_MAP.md`
- `TS18_REQUIREMENTS.md`
- `TS18_INTEGRATION_ARCHITECTURE.md`
- `TS18_VALIDATION_RUNBOOK.md`
- `DEVELOPMENT_ROADMAP.md`
- `CODEX_TASK_PROMPTS.md`

## Companion live docs
- `TS18_NATIVE_CONTRACTS.md` (validation/reference context; not implementation authority)
- `TS18_DEVICE_PROFILE.md`
- `TS18_DIAGNOSTICS_INSIGHTS.md`
- `TMUSIC_TO_AUXIO_TS_COMPARISON.md`
- `RESEARCH_SOURCES.md`
- `RELEASE_WORKFLOW.md`

## Live docs vs evidence snapshots vs archive policy
- **Live docs** in `docs/*.md` (excluding `docs/archive/**`) are active source-of-truth.
- **Evidence snapshots** in `docs/evidence/**` are preserved provenance and may include private-hook references.
- Evidence snapshots are **not** implementation guidance and must not override live docs.
- **Archive docs** in `docs/archive/**` are historical/provenance only.
- Routine guardrail scans should usually exclude `docs/evidence/**` and `docs/archive/**`.
- Evidence-inclusive and archive-inclusive audits must be explicit.

## Structure
- Live docs: `docs/*.md`
- Archive: `docs/archive/README.md`
- Evidence: `docs/evidence/`
- Templates: `docs/templates/`
- Historical draft redirects: `docs/pr-drafts/README.md`

## Agent/developer pointers
- Repo coding authority: `../AGENTS.md`
- Copilot/Codex policy: `../.github/copilot-instructions.md`
- Optional Codex setup notes: `CODEX_ENVIRONMENT_SETUP.md`
