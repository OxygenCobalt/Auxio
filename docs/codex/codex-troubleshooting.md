# Codex troubleshooting

- Exports not persisting: run setup first, then `source docs/codex/out/codex-env.sh` (generated file)
- Cached branch drift: `bash docs/codex/maintenance-codex-env.sh`
- Agent no internet: run full warmup in setup phase.
- ZIP snapshot/no .git: rerun in git clone with submodules.
- Missing `media/core_settings.gradle` / taglib / ffmpeg / proguard stub: run `bash scripts/prepare-ci-environment.sh && bash scripts/check-submodules.sh`.
- Missing JDK/Gradle JVM mismatch: install JDK 21 and re-run quick checks.
- Missing Android SDK/NDK/CMake/ninja: install via `sdkmanager` and package manager.
- Gradle daemon memory: use `--no-daemon` and limited workers.
- Offline dependency failure: run `bash docs/codex/setup-codex-env.sh` with internet.
- Hardware-only checks skipped: expected in Codex; run `docs/TS18_VALIDATION_RUNBOOK.md` on device.
