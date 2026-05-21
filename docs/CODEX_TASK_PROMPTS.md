# Codex Task Prompts (current only)

## Start point
Always start from `docs/README.md`.

## Strategic framing
1. Auxio-TS targets TS18/TW/TWTHEME parity — not generic Android compatibility.
2. Android-standard APIs are Tier 1 implementation — the preferred first implementation path, not the final authority. TS18/TW/TWTHEME parity is the product goal.
3. TS18/TW/TWTHEME evidence (t-music snapshot, firmware, DoFun/iLauncher/TWTHEME sources) defines compatibility expectations and identifies parity gaps.
4. Android-standard success does not automatically equal TS18-native parity. Tier 2 on-device validation is required.
5. Always distinguish: product requirement / Android-standard implementation / TS18 runtime validation / native-private investigation / production eligibility.
6. Native/private contracts are NOT permanently out of scope. They require the formal gap-and-promotion process (Tier 2 validated gap → Tier 3 investigation → Tier 4 design PR). Do NOT propose or merge native contract code without explicit human approval.

See canonical tier model: [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)

## Required TS18/TW/TWTHEME workflow
1. Read canonical live docs first:
   - `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`
   - `docs/TW_ECOSYSTEM_SOURCE_MAP.md`
   - `docs/TS18_REQUIREMENTS.md`
   - `docs/TS18_INTEGRATION_ARCHITECTURE.md`
   - `docs/TS18_VALIDATION_RUNBOOK.md`
   - `docs/TS18_NATIVE_PARITY_GAP_MATRIX.md`
2. Use official Android APIs as Tier 1 implementation (not final authority).
3. Use TS18/TW/TWTHEME sources for compatibility expectations, parity targets, and validation scope.
4. Treat `docs/evidence/**` as Tier 0 evidence only, never direct implementation guidance.
5. Use diagnostics/probes only when user-provided or no reliable source exists.
6. Native/private hooks require a formal gap-and-promotion process — not default production work.

## Current reusable prompt patterns
1. Source-led TS18 compatibility analysis and source-map updates.
2. Android-standard media/session/library/audio-focus hardening tasks (Tier 1).
3. TS18 validation runbook scenario additions/maintenance (Tier 2).
4. TS18 Native Parity Gap Matrix updates from hardware evidence.
5. CI/build reliability triage (with full log inspection and truthful reporting).
6. Docs consolidation/cleanup tasks (prefer removal/merging over historical accumulation).
7. Copilot pre-merge hardening checks.

## Explicit non-targets
- Probe-first or diagnostics-first planning.
- Default-off vendor adapter skeleton planning.
- TWUtil/TWClient reflection scanners.
- Vendor package scanner frameworks.
- Vendor-service binder scaffolding without explicit approved design.
- Package impersonation (`com.tw.music`) or `android.uid.system`/`sharedUserId` assumptions.
- Copied decompiled smali in product code.
- Claiming Codex environment build failures as final CI proof (GitHub Actions is the authoritative CI).
