# Codex Task Prompts (source-led TS18 direction)

## Required workflow for all TS18/TW/TWTHEME tasks

Before proposing any implementation or validation change for TS18/TW/TWTHEME work:

1. **Search the TS18/TW/TWTHEME source corpus first** (`docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` Priority 1 sources before any diagnostics).
2. **Update the source table** in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md` with any new useful sources found.
3. **Classify each source** with explicit confidence and porting decision labels before proposing implementation.
4. **Cite public equivalent project precedent** before implementation (Priority 2 projects before custom code).
5. **Use user-provided diagnostics only when available** (Priority 4); do not default to new probes.
6. **Do not propose speculative product-code probes**; diagnostics must remain external scripts or runbook steps.

## Active task prompts

1. Android MediaSession/notification hardening pass.
2. MediaLibraryService / Android Auto browsing hardening pass.
3. Media button / steering-wheel standard API compatibility hardening pass.
4. Audio focus and navigation-mixing hardening pass.
5. Head-unit UI/UX landscape and large-touch-target hardening pass.
6. TS18 validation evidence analysis and prioritized gap ranking.
7. Explicit TWTHEME compatibility decision pass (validation evidence only, no speculative code).
8. Stock-vs-Auxio acceptance comparison report refresh.
9. Package/signature/privilege risk refresh with explicit non-goals.
10. One explicit compatibility feature proposal (only after validated gap + source justification).

## De-emphasized / rejected prompt patterns
- TWUtil/TWClient reflection probe tasks.
- Vendor package probe framework tasks.
- Default-off adapter skeleton tasks.
- Hidden diagnostics module tasks inside product code.
- Probe-first or diagnostics-first TS18 planning tasks.
