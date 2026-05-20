#!/usr/bin/env bash
set -u; set -o pipefail
source "$(dirname "$0")/common.sh"
ROOT="$(repo_root)"; OUT="$ROOT/docs/codex/out"; mkdir -p "$OUT"; LOG_FILE="$OUT/maintenance-codex-env.log"; : >"$LOG_FILE"
run_required SUBMODULE_BLOCKER "Canonical prepare" bash "$ROOT/scripts/prepare-ci-environment.sh"
run_required SUBMODULE_BLOCKER "Submodule check" bash "$ROOT/scripts/check-submodules.sh"
run_optional "Gradle wrapper sanity" with_timeout 600 bash -lc 'cd "$0" && ./gradlew --no-daemon --version' "$ROOT"
run_optional "Gradle help sanity" with_timeout 900 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace help' "$ROOT"
[[ "${CODEX_CLEAN:-0}" = "1" ]] && run_optional "Stop gradle daemons" bash -lc 'cd "$0" && ./gradlew --stop' "$ROOT" || skip "No destructive cleanup (CODEX_CLEAN!=1)"
write_summary
[[ $FAILED -eq 0 ]] || exit 1
