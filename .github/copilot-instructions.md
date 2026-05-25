# Copilot instructions for Auxio-TS

## Core stance
- Start documentation navigation from `docs/README.md`.
- Prefer consolidation/removal of stale docs over keeping historical wrappers.
- **Auxio-TS is a TS18/TW/TWTHEME variant app.** TS18/TW/TWTHEME parity is the product target.
- Auxio-TS is an Auxio fork, not a clone of `com.tw.music`.
- Keep upstream playback/library/service design unless evidence requires targeted change.
- Android-standard APIs are the preferred **first implementation layer** (Tier 1), not the final authority.
- Keep TS18 integration in adapter/facade boundaries.
- Treat `docs/evidence/t-music-snapshot/` as evidence, not code to port.
- **Native/private contracts are NOT permanently out of scope.** They require the formal gap-and-promotion process (Tier 2 → Tier 3 → Tier 4). Do NOT say "private/native is out of scope" — say "not for production by default; requires formal gap-and-promotion process."

## TS18 Native Parity Tier Model

See canonical definition: [`docs/TS18_INTEGRATION_ARCHITECTURE.md` — TS18 Native Parity Strategy](docs/TS18_INTEGRATION_ARCHITECTURE.md#ts18-native-parity-strategy)

| Tier | Name | Scope |
|------|------|-------|
| 0 | Evidence only | t-music snapshot, TWTHEME resources, diagnostics, public repos, firmware notes |
| 1 | Android-standard implementation | MediaSession, MediaBrowser, notification, audio focus, media buttons, AppWidget, shortcuts |
| 2 | TS18-aware validation | On-device evidence proving which TS18/TWTHEME surfaces see or ignore Tier 1 behaviour |
| 3 | Isolated native experiments | External scripts or non-production branches testing specific TW/TWTHEME contracts |
| 4 | Production native integration | Only via explicit human-approved design PR meeting all 8 production eligibility criteria |

## TS18/TW/TWTHEME source-led policy

TS18/TW/TWTHEME work must begin with the curated TS18/Topway/DoFun/TW source corpus and battle-tested public head-unit projects.

Android/Media3 standards are the Tier 1 implementation baseline. They are the preferred first implementation path but are not the final authority for TS18/TWTHEME-specific questions. Native parity gap investigation is allowed through the formal gap-and-promotion process. Production safety rules remain intact.

Probe/diagnostics-driven work is secondary. It is allowed only when:
- the user provides fresh diagnostics;
- the repo already contains relevant captured evidence;
- no reliable public TS18/TW/TWTHEME or equivalent head-unit source exists;
- the output remains an external validation/runbook step, not speculative product-code scaffolding.

Do not add in-app TS18 probe frameworks, default-off vendor adapter skeletons, TWUtil/TWClient reflection scanners, vendor-service binders, package impersonation, `android.uid.system`/`sharedUserId` strategies, copied smali, or hidden diagnostics modules.

### Required source priority order for TS18/TW/TWTHEME work

```text
Priority 1: TS18/TW/TWTHEME ecosystem sources
  - Topway / TS10 / TS18 firmware references
  - DoFun / iLauncher / TWTHEME material
  - TWUtil / TWClient public references
  - ZLink/TLink/carchoose ecosystem clues
  - TWTHEME theme/window/PiP/launcher behaviour sources

Priority 2: Battle-tested public head-unit projects
  - Projects showing working Android head-unit integration patterns
  - Media metadata exposure, hardware-key routing, launcher/widget behaviour,
    and platform isolation precedents

Priority 3: Local repository evidence
  - docs/evidence/t-music-snapshot/
  - diagnostics/redacted/ts18_device_profile.json
  - Existing Auxio-TS TS18 docs

Priority 4: User-provided diagnostics
  - Fresh TS18 logs, dumpsys, bugreport extracts, package lists,
    theme APK listings, launcher behaviour captures

Priority 5: New probes/diagnostics
  - Allowed only when no reliable source, public equivalent, or
    user-provided evidence exists
  - Prefer external scripts/manual runbook steps
  - Do not add speculative probe frameworks to product app code
```

Canonical source corpus: `docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`

### Required agent workflow for TS18/TW/TWTHEME tasks

1. Search the TS18/TW/TWTHEME source corpus first (Priority 1 before Priority 5).
2. Add any new useful sources to the canonical source map.
3. Classify source confidence and porting decision before proposing implementation.
4. Prefer public equivalent projects over speculative probes.
5. Use diagnostics only when provided by the user or when no source-led path exists.
6. Keep diagnostics as external scripts/runbook steps unless a later approved feature explicitly requires code.
7. Avoid TWUtil/TWClient reflection in product code.
8. Avoid vendor binders and package impersonation.


## Topway decompile-driven compatibility rule
- The official Topway `com.tw.music` apktool/JADX decompile is a primary local compatibility source.
- Generic Android MediaSession/AppWidget/shortcut compatibility is necessary but not sufficient.
- Agents must consult `docs/topway/` before proposing Topway/TS18 music compatibility work.
- JADX alias package names such as `com.p060tw.music` are not runtime package names.
- Safe Topway broadcast/action/seek/widget compatibility may be implemented only through an isolated bridge package.
- Existing broad prohibitions against `com.tw.music.action.*` are refined: these strings are forbidden generally, but allowed as constants inside the isolated Topway bridge, tests, and docs.
- Runtime APK must stay clean and must not include evidence/probe/capture tooling.

## Evidence labeling (required)
For each TS18/TW/TWTHEME claim, include:
1) confidence: **Observed / Inferred / Hypothesis / Requires TS18 validation / Unsupported**
2) porting decision label: **Directly reusable requirement / Reusable validation idea / Useful as evidence only / Obsolete due to Auxio architecture / Requires TS18 runtime validation / Unsafe to port / Should be explicitly avoided**

Always distinguish between: product requirement / Android-standard implementation / TS18 runtime validation / native-private investigation / production eligibility.

## Never do
- Do not change package to `com.tw.music`.
- Do not assume privileged/system UID.
- Do not copy decompiled smali into Auxio code.
- Do not claim TS18 compatibility without TS18 runtime evidence.
- Do not add TWUtil/TWClient reflection or vendor-service binders to product code.
- Do not make probe/diagnostics the default approach for TS18 questions.
- Do not add in-app probe frameworks or speculative default-off adapters.
- Do not add product-code calls to `com.tw.music.action.*` outside the isolated Topway bridge package/test scope.
- Do not add direct `com.tw.*` or `android.tw.john.*` imports in product code.
- Do not add TWTHEME private-resource loaders or hidden diagnostics modules in the app.
- Do not say "private/native is permanently out of scope" — say "not for production by default; requires formal gap-and-promotion process".

- Inspect full CI logs before proposing build fixes; do not diagnose from summary lines only.
- Distinguish Codex environment limitations from GitHub Actions/Copilot runner failures.
- Do not treat Codex environment build limitations as final CI proof; GitHub Actions/Copilot CI is the final workflow proof point.
- Never claim tasks/build/test/lint success unless commands actually passed in this environment.
## Baseline checks
- `./gradlew tasks`
- `./gradlew assembleDebug`
- `./gradlew test`
- `./gradlew lint`
- `find scripts -type f -name '*.sh' -print -exec sh -n {} \;`

## Release/signing safety
- Treat release/signing workflow edits as security-sensitive.
- Never print or commit secret material.
- Keep decoded keystores in runner temp paths only.
- Initialize submodules recursively before Gradle; never create fake submodule files.
- `Android Quality` is expected to expose formatting, unit-test, lint, and head-unit safety
  status independently; do not accept a workflow shape where `spotlessCheck` skips the other gates.
- `scripts/check-headunit-compat-safety.sh` is the canonical product-code TS18/Topway safety
  guardrail; prefer it over duplicating stale inline `grep` logic in workflows.

- Phase 5G/6A: use evidence-pack tooling (`scripts/ts18-create-evidence-pack.sh`, `scripts/ts18-summarise-evidence-pack.py`) before proposing native/private investigations; no production private hooks without explicit approval.

- Tier pipeline reminder: Tier 2 evidence + summariser + matrix proposal can generate Tier 3 candidate drafts, but Tier 4 production native/private integration requires a separate approved design PR.


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


2026-05-24 implementation note: isolated Topway bridge runtime wiring now exists; keep Topway strings constrained to approved bridge/test/docs scope and continue blocking private/native binder production paths.
