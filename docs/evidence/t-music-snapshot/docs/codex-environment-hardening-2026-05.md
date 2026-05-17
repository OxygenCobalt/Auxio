# Codex environment hardening notes (2026-05)

This repo uses:
- `scripts/codex/setup_readability_env.sh` for first-time setup/install.
- `scripts/codex/maintain_readability_env.sh` for light verification in resumed/cached environments.
- `scripts/codex/verify_readability_env.sh` for deterministic self-test checks.

## Intent

- Setup installs base dependencies, Android command-line tools, GH CLI, and optional pinned JADX (`INSTALL_PINNED_JADX=1`, `JADX_VERSION=1.5.5`).
- Maintenance avoids heavy rebuild/report churn by default.
- Verify script checks syntax/tooling/auth/readability script availability without creating large diffs.

## GitHub auth policy

- Prefer `GH_TOKEN`; `GITHUB_TOKEN` is supported as fallback.
- Tokens are never written into `~/.codex-t-music-env` or repo files.
- Git credential helper reads token env vars at runtime.
- Remote rewrite is conservative:
  - converts matching SSH GitHub remote for `$GH_REPO` to HTTPS;
  - refuses to silently rewrite to a different repo.

## Android/JADX policy

- Android SDK path: `$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager`
- Installed packages: `platform-tools`, `build-tools;$ANDROID_BUILD_TOOLS_VERSION`.
- Build-tools default remains `35.0.0`.
- `jadx-gui` is linked if present but not required for headless runs.

## Recommended periodic checks

```bash
bash scripts/codex/maintain_readability_env.sh
bash scripts/codex/verify_readability_env.sh
bash tools/readability/06_diff_size_guard.sh
bash scripts/08_verify_vendor_tokens.sh
```
