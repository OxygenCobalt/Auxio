#!/usr/bin/env bash
# Run Auxio-TS CI-equivalent checks locally/Codex-side.
#
# Default mode is comprehensive but non-misleading:
# - keeps going after failures;
# - writes logs;
# - summarises first errors;
# - exits non-zero only if CODEX_CI_STRICT=1.
#
# Usage:
#   bash scripts/codex/run-auxio-ci-local.sh
#   CODEX_CI_STRICT=1 bash scripts/codex/run-auxio-ci-local.sh

set -u
set -o pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OUT_DIR="$ROOT_DIR/.codex/ci-local"
REPORT="$OUT_DIR/report.md"
STRICT="${CODEX_CI_STRICT:-0}"

mkdir -p "$OUT_DIR"
rm -f "$REPORT"

PASS=0
FAIL=0
SKIP=0

log() { printf '[codex-ci] %s\n' "$*"; }

record() {
  local status="$1"
  local name="$2"
  local log_file="$3"
  case "$status" in
    pass) PASS=$((PASS + 1));;
    fail) FAIL=$((FAIL + 1));;
    skip) SKIP=$((SKIP + 1));;
  esac
  printf '| %s | %s | `%s` |\n' "$name" "$status" "$log_file" >>"$REPORT"
}

first_error_summary() {
  local file="$1"
  grep -nEi 'FAILURE:|BUILD FAILED|FAILED|error:|exception|unresolved reference|cannot find symbol|lint found|Task .* failed|Process completed with exit code|Could not|No such file|not found|permission denied' "$file" | head -40 || true
}

run_check() {
  local name="$1"
  shift
  local log_file="$OUT_DIR/${name//[^A-Za-z0-9_.-]/_}.log"
  log "START: $name"
  {
    printf '$'
    printf ' %q' "$@"
    printf '\n\n'
    "$@"
  } >"$log_file" 2>&1
  local rc=$?
  if [ "$rc" -eq 0 ]; then
    log "PASS: $name"
    record pass "$name" "$log_file"
  else
    log "FAIL: $name exit $rc"
    record fail "$name" "$log_file"
    {
      printf '\n### First-error summary: %s\n\n' "$name"
      printf '```text\n'
      first_error_summary "$log_file"
      printf '```\n'
    } >>"$REPORT"
  fi
  return 0
}

cd "$ROOT_DIR" || exit 0

{
  echo "# Auxio-TS local CI report"
  echo ""
  echo "- Date: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "- Root: \`$ROOT_DIR\`"
  echo ""
  echo "| Check | Result | Log |"
  echo "|---|---|---|"
} >>"$REPORT"

run_check git-status git status --short
run_check git-diff-check git diff --check
run_check shell-syntax bash -lc 'find scripts -type f -name "*.sh" -print -exec sh -n {} \;'
run_check submodule-status git submodule status --recursive

if [ -f scripts/check-submodules.sh ]; then
  run_check check-submodules bash scripts/check-submodules.sh
else
  record skip "check-submodules" "scripts/check-submodules.sh missing"
fi

if [ -f gradlew ]; then
  chmod +x gradlew || true
  run_check gradle-version ./gradlew --version --no-daemon
  run_check assemble-debug ./gradlew assembleDebug --stacktrace --no-daemon
  run_check unit-tests ./gradlew test --stacktrace --no-daemon
  run_check lint ./gradlew lint --stacktrace --no-daemon
else
  record skip "gradle" "gradlew missing"
fi

run_check product-code-private-api-safety bash -lc 'grep -RIn "android.tw.john.TWUtil\|android.tw.john.TWClient\|Class.forName(\"android.tw\|com.tw.service\|com.tw.service.xt\|com.tw.music.action\|android.uid.system\|sharedUserId" app/src/main app/src/test || true'
run_check probe-framework-safety bash -lc 'grep -RIn "TS18IntegrationProbe\|TS18EnvironmentSnapshot\|TS18ContractRegistry\|TS18AdapterCapability\|TWUtil reflection\|TWClient reflection\|vendor package scanner\|default-off adapter\|default-off probe\|diagnostics-only app module\|runtime contract registry\|TS18 probe layer\|probe-only layer\|adapter skeleton\|TWUtil scanner\|TWClient scanner" app/src/main app/src/test docs .github AGENTS.md README.md || true'

{
  echo ""
  echo "## Summary"
  echo ""
  echo "- Passed: $PASS"
  echo "- Failed: $FAIL"
  echo "- Skipped: $SKIP"
  echo "- Logs: \`$OUT_DIR\`"
} >>"$REPORT"

cat "$REPORT"

if [ "$STRICT" = "1" ] && [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
