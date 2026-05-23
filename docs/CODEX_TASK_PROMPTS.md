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
- Claiming Codex environment build failures as final CI proof (GitHub Actions is the authoritative CI).


## Large-Scope Implementation Delivery Protocol
1. Large tasks are delivery contracts, not suggestion lists.
2. Do not satisfy large tasks by touching many headings shallowly.
3. "Implemented" means runtime code is wired into real behavior or an executable workflow, not only a model/registry/doc/template/test.
4. If a task asks for app/runtime work, docs/tests/tooling are supporting work only.
5. Do not end with "Ready for Draft PR" while any core requested workstream is partial and locally implementable.
6. Environment blockers do not stop local implementation unless they prevent editing or reasoning about relevant files.
7. If Gradle cannot run, continue local implementation and static checks; report build proof as pending GitHub/Copilot CI.
8. Do not list original requested work as "next scope" unless it is genuinely outside the current task.
9. "Next scope" must include only work after current acceptance criteria are met.
10. Compatibility model/status/registry additions are not counted as implemented until wired to meaningful runtime call-sites.

## Implementation-status definitions
- Implemented: runtime code or executable workflow is wired and usable.
- Implemented — requires TS18 validation: implementation exists; runtime parity still needs TS18 hardware proof.
- Partially implemented: pieces exist but user-visible behavior or call-site wiring is incomplete.
- Scaffold only: models/docs/templates exist without runtime integration.
- Blocked: a specific external dependency prevents implementation; state exact blocker.
- Deferred: intentionally out of current scope, with reason.

## Ready for Draft PR / Ready for Merge Rules
- Ready for Draft PR: main implementation goal is complete enough for review.
- If any core requested workstream is partial and locally fixable, use: Needs another Codex pass.
- If branch is an early snapshot, use: Ready for Draft PR snapshot.
- Ready to merge is only for final GitHub/Copilot closure after checks/comments are resolved.
- Missing SDK/submodules are environment-limited validation, not merge proof and not automatic merge blocker.

## Auxio-TS app/runtime priority rules
- Auxio-TS is a TS18/TW/TWTHEME variant app; app/runtime behavior is the priority.
- Source-backed compatibility work should improve real runtime surfaces, not only validation tooling.
- Evidence/validation tooling is primary only when requested or when implementation cannot proceed safely.
- For app-feature tasks, implement visible behavior, route/action wiring, settings/runtime effects, metadata/session/widget improvements, or compatibility call-site wiring.
- Docs/tests/fixtures must not substitute for app code implementation.

## Compatibility-layer wiring rules
- A headunit/compat feature is not implemented until consumed by at least one meaningful runtime call-site.
- Registry entries alone do not count as implementation.
- Status models alone do not count as implementation.
- Metadata policy is not implemented until used by MediaSession, notification, widget, or another runtime publisher.
- Parity maps are not implemented until they drive or verify action/route completeness.
- Settings/status are not implemented until surfaced via existing UI/settings patterns.

## Final response discipline
Always report explicitly:
- which areas were wired into runtime code,
- which areas are scaffold-only,
- which areas remain partial,
- which partials are locally fixable and why not fixed,
- whether output is a review snapshot or complete,
- why any next scope is truly separate from current acceptance criteria.


2026-05-23 runtime release-readiness update: Metadata/session/widget/notification consistency and head-unit route/action safety were hardened in app runtime code; validation tooling remains external to APK; no TS18 hardware parity success claimed; no Tier 4 private/native integration performed.
