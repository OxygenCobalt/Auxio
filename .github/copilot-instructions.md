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
- Do not add product-code calls to `com.tw.music.action.*`.
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
