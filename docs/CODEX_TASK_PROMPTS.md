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
- Probe-first or diagnostics-first TS18 planning tasks.


# Codex environment setup for Auxio-TS
Use this when starting a Codex cloud task that needs to build, test, lint, or modify CI-sensitive Android code.

## Codex setup command
Configure the Codex environment setup command as:

```sh
bash scripts/codex/setup-auxio-ts.sh
```

The setup script is intentionally best-effort. It initialises toolchains and submodules, warms Gradle where possible, and writes logs under `.codex/logs`.

## Maintenance commands

```sh
bash scripts/codex/doctor-auxio-ts.sh
bash scripts/codex/repair-auxio-ts.sh
bash scripts/codex/run-auxio-ci-local.sh
```

Use strict mode only when you want a hard failure:

```sh
CODEX_STRICT_SETUP=1 bash scripts/codex/setup-auxio-ts.sh
CODEX_DOCTOR_STRICT=1 bash scripts/codex/doctor-auxio-ts.sh
CODEX_CI_STRICT=1 bash scripts/codex/run-auxio-ci-local.sh
```

## Expected checks before claiming success

```sh
bash scripts/check-submodules.sh
./gradlew assembleDebug
./gradlew test
./gradlew lint
```

If these cannot run inside Codex because of missing remotes, inaccessible submodules, unavailable secrets, or environment/toolchain restrictions, say so explicitly. Do not claim success from partial checks.

GitHub Actions / Copilot in GitHub’s runner context remains the source of truth for final CI reliability.
