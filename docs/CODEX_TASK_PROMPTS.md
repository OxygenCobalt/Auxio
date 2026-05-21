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
2. Treat TS18/TW/TWTHEME behavior as the product target; use official Android APIs as the preferred first implementation layer.
3. Use TS18/TW/TWTHEME sources for compatibility expectations and validation scope.
4. Treat `docs/evidence/**` as evidence only, never direct implementation guidance.
5. Use diagnostics/probes only when user-provided or no reliable source exists.
6. Avoid private TS/TW runtime hooks in production by default; track parity gaps and only promote native contracts through future human-approved explicit design PRs.

## Current reusable prompt patterns
1. Source-led TS18 compatibility analysis and source-map updates.
2. Android-standard media/session/library/audio-focus hardening tasks.
3. TS18 validation runbook scenario additions/maintenance.
4. CI/build reliability triage (with full log inspection and truthful reporting).
5. Docs consolidation/cleanup tasks (prefer removal/merging over historical accumulation).
6. Copilot pre-merge hardening checks.

## TS18/TWTHEME launcher/widget tasks (baseline)
- TS18/TW/TWTHEME behavior defines product acceptance criteria.
- Android APIs are Tier 1 implementation, not a final ceiling.
- Start from existing `HeadUnitEntryPoints` + `widgets/WidgetProvider` architecture.
- Do not introduce a parallel widget provider unless a clear architectural blocker is proven.
- Use Android-standard APIs (`AppWidgetProvider`, `RemoteViews`, explicit `PendingIntent`, launcher shortcuts, MediaSession/service pathways).
- Treat TWTHEME/iLauncher/DoFun as compatibility and validation context only, not implementation authority.
- Preserve warm-start `onNewIntent` routing and queue routing to `openQueue()`.
- Extend evidence via external scripts/runbook updates, not in-app probe modules.
- If Tier 1 behavior is insufficient on TS18 hosts, log a native parity gap for future investigation instead of assuming work is complete.
- Report build/test/lint truthfully and separate Codex SDK/environment limits from real app build failures.

## Explicit non-targets
- Probe-first or diagnostics-first planning.
- Default-off vendor adapter skeleton planning.
- TWUtil/TWClient reflection scanners.
- Vendor package scanner frameworks.
- Vendor-service binder scaffolding without explicit approved design.
- Package impersonation (`com.tw.music`) or `android.uid.system`/`sharedUserId` assumptions.
- Copied decompiled smali in product code.
