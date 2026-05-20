#!/usr/bin/env bash
set -u; set -o pipefail
source "$(dirname "$0")/common.sh"
ROOT="$(repo_root)"; OUT="$ROOT/docs/codex/out"; mkdir -p "$OUT"; LOG_FILE="$OUT/repair-codex-env.log"; : >"$LOG_FILE"
run_optional "Canonical prepare" bash "$ROOT/scripts/prepare-ci-environment.sh"
run_optional "Submodule init/sync" bash -lc 'cd "$0" && git submodule sync --recursive && git submodule update --init --recursive --jobs 4' "$ROOT"
[[ "${CODEX_UPDATE_SUBMODULE_REMOTES:-0}" = "1" ]] && run_optional "Submodule remote update (opt-in)" bash -lc 'cd "$0" && git submodule update --remote --recursive' "$ROOT" || skip "Submodule --remote disabled by default"
run_optional "Submodule check" bash "$ROOT/scripts/check-submodules.sh"
run_optional "Restore script execute bits" bash -lc 'find "$0/scripts" "$0/docs/codex" -type f -name "*.sh" -exec chmod +x {} +' "$ROOT"
[[ "${CODEX_CLEAN:-0}" = "1" ]] && run_optional "Clean gradle locks (opt-in)" bash -lc 'find "$HOME/.gradle" -type f -name "*.lock" -delete' || skip "Gradle lock cleanup disabled by default"
write_summary
