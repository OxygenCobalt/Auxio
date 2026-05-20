# Codex environment bundle (Auxio-TS)

This is the practical entry point for Codex cloud environment reliability.

## Configure in Codex
- **Setup script:** `bash docs/codex/setup-codex-env.sh`
- **Maintenance script:** `bash docs/codex/maintenance-codex-env.sh`
- **First command for agents:** `bash docs/codex/run-codex-checks.sh quick`

## Canonical fallback path (default pathway)
1. `bash ./scripts/prepare-ci-environment.sh`
2. `bash ./scripts/check-submodules.sh`
3. `./gradlew --no-daemon --stacktrace help`
4. Scoped CI-equivalent Gradle tasks only if the above pass.

## Setup vs maintenance vs validation
- Setup: installs/verifies tools, warms Gradle, records durable hints in `docs/codex/out/codex-env.sh`.
- Maintenance: refreshes cached environments after branch/toolchain/submodule drift.
- Validation: quick/submodules/toolchain/android/docs/ts18/ci-parity/full modes.

## Command table
- setup: `bash docs/codex/setup-codex-env.sh`
- maintenance: `bash docs/codex/maintenance-codex-env.sh`
- quick validation: `bash docs/codex/run-codex-checks.sh quick`
- Android validation: `bash docs/codex/run-codex-checks.sh android`
- docs/TS18 validation: `bash docs/codex/run-codex-checks.sh ts18`
- CI parity validation: `bash docs/codex/run-codex-checks.sh ci-parity`
- repair: `bash docs/codex/run-codex-checks.sh repair`
- evidence bundle: `bash scripts/ts18_collect_auxio_ts_evidence.sh`

All generated logs/summaries go to `docs/codex/out/`.
