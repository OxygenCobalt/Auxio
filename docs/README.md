# Auxio-TS Documentation Index

Start here for all TS18/TW/TWTHEME work.

## Strategic framing

Auxio-TS targets TS18/TW/TWTHEME parity. Android-standard APIs are the preferred **first implementation layer** (Tier 1), not the final authority. Where Tier 1 proves insufficient for TS18/TWTHEME parity, native/private investigation (Tier 3/4) is a valid future path through a formal gap-and-promotion process.

The product goal is TS18/TW/TWTHEME parity via:
1. TS18/TW/TWTHEME evidence corpus (Tier 0) defining the parity target.
2. Android-standard APIs (Tier 1) as the preferred first implementation layer — not the final authority.
3. On-device TS18/TWTHEME hardware validation (Tier 2) confirming which surfaces respond to Tier 1.
4. Evidence-gated native/private investigation (Tier 3/4) only where Tier 2 proves a parity gap.

See [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy) for the canonical tier model.

See [`docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md) for the current parity gap tracking matrix.

## Active guidance (canonical)
1. TS18/TW/TWTHEME parity is the product target; TS18/TW/TWTHEME evidence drives requirements.
2. Android-standard APIs are Tier 1 implementation — preferred first layer, not the ceiling.
3. Public TS18/Topway/DoFun/TW/TWTHEME/head-unit sources define compatibility expectations and parity gaps.
4. Local `t-music` and TS18 diagnostics are Tier 0 evidence and validation context.
5. External validation scripts/runbooks are Tier 2 acceptance evidence.
6. Native/private investigation (Tier 3/4) requires a formal gap-and-promotion process, not default production work.
7. Probe/diagnostics-driven investigation is last resort and stays external/runbook-based.

## Canonical live docs (active guidance)
- `TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
- `TW_ECOSYSTEM_SOURCE_MAP.md`
- `TS18_REQUIREMENTS.md`
- `TS18_INTEGRATION_ARCHITECTURE.md`
- `TS18_NATIVE_PARITY_GAP_MATRIX.md`
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
- `docs/evidence/**` is preserved provenance/evidence only (Tier 0).
- Evidence snapshots may contain private-hook references and legacy assumptions; they are not implementation guidance.
- Routine guardrail scans should exclude `docs/evidence/**` by default.
- Evidence-inclusive audits must be explicitly requested.

## Agent/developer entry points
- Repo authority: `../AGENTS.md`
- Copilot/Codex policy: `../.github/copilot-instructions.md`
- Codex environment setup/maintenance/validation: `codex/README.md`

## TS18 evidence pipeline docs
- [`TS18_NATIVE_PARITY_GAP_MATRIX.md`](TS18_NATIVE_PARITY_GAP_MATRIX.md) — parity gap tracking matrix (updated from validated evidence packs)
- [`TS18_HARDWARE_VALIDATION_CHECKLIST.md`](TS18_HARDWARE_VALIDATION_CHECKLIST.md) — step-by-step on-device capture checklist

## Evidence-only reference docs
- [`TS18_STOCK_TMUSIC_PARITY_LEDGER.md`](TS18_STOCK_TMUSIC_PARITY_LEDGER.md) — stock/t-music parity ledger (evidence-oriented)
- [`TS18_NATIVE_CONTRACT_INVENTORY.md`](TS18_NATIVE_CONTRACT_INVENTORY.md) — native/private contract inventory (evidence-only, not production approval)
- [`evidence/ts18/README.md`](evidence/ts18/README.md) — evidence pack index and registry
