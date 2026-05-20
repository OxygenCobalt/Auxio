#!/usr/bin/env bash
set -u; set -o pipefail
source "$(dirname "$0")/common.sh"
MODE="${1:-quick}"
ROOT="$(repo_root)"; OUT="$ROOT/docs/codex/out"; mkdir -p "$OUT"
LOG_FILE="$OUT/validate-codex-env.log"; SUMMARY="$OUT/validate-summary.txt"; JSON_SUMMARY="$OUT/validate-summary.json"
: >"$LOG_FILE"; : >"$SUMMARY"

check(){ local cls="$1"; local name="$2"; shift 2; run_step "$name"; "$@" >>"$LOG_FILE" 2>&1; local rc=$?; if [[ $rc -eq 0 ]]; then echo "OK|$name" >>"$SUMMARY"; pass "$name"; else echo "$cls|$name|exit=$rc" >>"$SUMMARY"; block "$cls: $name"; fi; return 0; }
quick(){ check SUBMODULE_BLOCKER prepare bash "$ROOT/scripts/prepare-ci-environment.sh"; check SUBMODULE_BLOCKER submodules bash "$ROOT/scripts/check-submodules.sh"; check REAL_BUILD_FAILURE shell-syntax bash -lc 'find "$0/scripts" "$0/docs/codex" -type f -name "*.sh" -print -exec bash -n {} \;' "$ROOT"; check JAVA_GRADLE_COMPAT_BLOCKER gradle-version with_timeout 600 bash -lc 'cd "$0" && ./gradlew --no-daemon --version' "$ROOT"; check ANDROID_SDK_BLOCKER gradle-help with_timeout 900 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace help' "$ROOT"; }
android(){ check ANDROID_SDK_BLOCKER app-assemble with_timeout 2400 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace :app:assembleDebug' "$ROOT"; }
docs_mode(){ check REQUIRED_DOC ts18-source-led-doc test -f "$ROOT/docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md"; check REQUIRED_DOC ts18-runbook test -f "$ROOT/docs/TS18_VALIDATION_RUNBOOK.md"; }
ci_parity(){ check REAL_BUILD_FAILURE ci-parity-check with_timeout 3600 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace --continue spotlessCheck :app:testDebugUnitTest :musikr:testDebugUnitTest :app:lintDebug' "$ROOT"; }
unexpected_structure(){
  [[ ! -f "$ROOT/scripts/prepare-ci-environment.sh" ]] || [[ ! -f "$ROOT/scripts/check-submodules.sh" ]] || [[ ! -x "$ROOT/gradlew" ]]
}
fallback_canonical(){
  warn "UNEXPECTED_STRUCTURE: falling back to canonical prepare/submodule/gradle-help checks"
  check SUBMODULE_BLOCKER fallback-prepare bash "$ROOT/scripts/prepare-ci-environment.sh"
  check SUBMODULE_BLOCKER fallback-submodules bash "$ROOT/scripts/check-submodules.sh"
  check ANDROID_SDK_BLOCKER fallback-gradle-help with_timeout 900 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace help' "$ROOT"
}

case "$MODE" in
 quick) quick ;;
 submodules|toolchain|android|docs|ts18|ci-parity|full)
  if unexpected_structure; then
    fallback_canonical
  else
    case "$MODE" in
      submodules) check SUBMODULE_BLOCKER submodules bash "$ROOT/scripts/check-submodules.sh" ;;
      toolchain) check JAVA_GRADLE_COMPAT_BLOCKER gradle-version with_timeout 600 bash -lc 'cd "$0" && ./gradlew --no-daemon --version' "$ROOT" ;;
      android) android ;;
      docs|ts18) docs_mode; skip "TS18 runtime validation is hardware-only" ;;
      ci-parity) ci_parity ;;
      full) quick; android; docs_mode; ci_parity ;;
    esac
  fi
  ;;
 *) echo "unknown mode: $MODE"; exit 2 ;;
esac

if command -v jq >/dev/null 2>&1; then
  jq -Rn --argfile lines <(awk -F'|' '{print $0}' "$SUMMARY" | jq -R -s 'split("\n")[:-1]') '{summary:$lines}' > "$JSON_SUMMARY" 2>>"$LOG_FILE" || true
fi
write_summary
cat "$SUMMARY"
[[ $FAILED -eq 0 ]] || exit 1
