# Codex Task Prompts (current only)

## Start point
Always start from `docs/README.md`.

## Required TS18/TW/TWTHEME workflow
1. Read canonical live docs first:
   - `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
   - `docs/TW_ECOSYSTEM_SOURCE_MAP.md`
   - `docs/TS18_REQUIREMENTS.md`
   - `docs/TS18_INTEGRATION_ARCHITECTURE.md`
   - `docs/TS18_VALIDATION_RUNBOOK.md`
2. Use official Android APIs as implementation authority.
3. Use TS18/TW/TWTHEME sources for compatibility expectations and validation scope.
4. Treat `docs/evidence/**` as evidence only, never direct implementation guidance.
5. Use diagnostics/probes only when user-provided or no reliable source exists.
6. Avoid private TS/TW runtime hooks unless a future human-approved explicit design PR allows it.

## Current reusable prompt patterns
1. Source-led TS18 compatibility analysis and source-map updates.
2. Android-standard media/session/library/audio-focus hardening tasks.
3. TS18 validation runbook scenario additions/maintenance.
4. CI/build reliability triage (with full log inspection and truthful reporting).
5. Docs consolidation/cleanup tasks (prefer removal/merging over historical accumulation).
6. Copilot pre-merge hardening checks.

## Explicit non-targets
- Probe-first or diagnostics-first planning.
- Default-off vendor adapter skeleton planning.
- TWUtil/TWClient reflection scanners.
- Vendor package scanner frameworks.
- Vendor-service binder scaffolding without explicit approved design.
- Package impersonation (`com.tw.music`) or `android.uid.system`/`sharedUserId` assumptions.
- Copied decompiled smali in product code.

## Phase 5G/6A Codex task scope
Implement evidence-pack capture, summariser, and parity-gap proposal tooling first. Do not add production private hooks without approved Tier 3 candidate template evidence.

- Use `scripts/ts18-evidence-workflow.sh` as the single orchestration entry point for Phase 5G/6A.
