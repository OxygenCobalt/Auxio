# Codex Task Prompts (source-led TS18 direction)

## Required first step for any TS18/TW/TWTHEME task

Before proposing any implementation, validation change, or probe:

1. Search the canonical source corpus in `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`.
2. Check whether the question is already answered by a Priority 1 or Priority 2 source.
3. Add any newly-found useful sources to the canonical corpus table before starting work.
4. Assign explicit `Confidence` and `Porting decision` labels to all TS18/TW/TWTHEME claims.
5. Prefer public equivalent project precedent over speculative probes.
6. Only use diagnostics if user provides fresh evidence or no source-led path exists.
7. Do not add speculative product-code probes, adapter skeletons, or vendor-service binders.

## Accepted task prompts

1. Android MediaSession/notification hardening pass.
2. MediaLibraryService / Android Auto browsing hardening pass.
3. Media button / steering-wheel standard API compatibility hardening pass.
4. Audio focus and navigation-mixing hardening pass.
5. Head-unit UI/UX landscape and large-touch-target hardening pass.
6. TS18 validation evidence analysis and prioritized gap ranking (source-led, not probe-led).
7. Explicit TWTHEME compatibility decision pass (validation evidence only, no speculative code).
8. Stock-vs-Auxio acceptance comparison report refresh.
9. iLauncher/TWTHEME media-widget acceptance validation scenario design.
10. ZLink/TLink coexistence acceptance scenario design and validation.
11. Package/signature/privilege risk refresh with explicit non-goals.
12. One explicit compatibility feature proposal (only after validated gap + source justification).

## De-emphasized / rejected prompt patterns
- TWUtil/TWClient reflection probe tasks.
- Vendor package probe framework tasks.
- Default-off adapter skeleton tasks.
- Hidden diagnostics module tasks inside product code.
- "Run probes first" as the default starting point for any TS18 question.
