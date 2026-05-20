#!/usr/bin/env bash
# Diagnose whether Codex can run Auxio-TS CI-equivalent tasks.
# This script is intentionally non-fatal: it prints a report and exits 0 unless CODEX_DOCTOR_STRICT=1.

set -u
set -o pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OUT_DIR="$ROOT_DIR/.codex/doctor"
REPORT="$OUT_DIR/report.md"
STRICT="${CODEX_DOCTOR_STRICT:-0}"

mkdir -p "$OUT_DIR"

FAILS=0
WARNS=0

section() {
  printf '\n## %s\n\n' "$1" | tee -a "$REPORT"
}

cmd() {
  local name="$1"
  shift
  local log_file="$OUT_DIR/${name//[^A-Za-z0-9_.-]/_}.log"
  printf '\n### %s\n\n' "$name" >>"$REPORT"
  printf '```sh\n' >>"$REPORT"
  printf '$'
  printf ' %q' "$@" >>"$REPORT"
  printf '\n```\n\n' >>"$REPORT"
  "$@" >"$log_file" 2>&1
  local rc=$?
  if [ "$rc" -eq 0 ]; then
    printf 'Result: ✅ passed. Log: `%s`\n\n' "$log_file" >>"$REPORT"
  else
    FAILS=$((FAILS + 1))
    printf 'Result: ⚠️ failed with exit code %s. Log: `%s`\n\n' "$rc" "$log_file" >>"$REPORT"
  fi
  return 0
}

probe_file() {
  local path="$1"
  if [ -e "$ROOT_DIR/$path" ]; then
    printf -- '- ✅ `%s`\n' "$path" >>"$REPORT"
  else
    WARNS=$((WARNS + 1))
    printf -- '- ⚠️ missing `%s`\n' "$path" >>"$REPORT"
  fi
}

rm -f "$REPORT"
{
  echo "# Auxio-TS Codex doctor report"
  echo ""
  echo "- Date: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "- Root: \`$ROOT_DIR\`"
} >>"$REPORT"

cd "$ROOT_DIR" || exit 0

section "Toolchain"
cmd uname uname -a
cmd java-version bash -lc 'java -version'
cmd git-version git --version
cmd gradle-wrapper bash -lc './gradlew --version --no-daemon'
cmd android-env bash -lc 'printf "ANDROID_HOME=%s\nANDROID_SDK_ROOT=%s\nPATH=%s\n" "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" "$PATH"; command -v sdkmanager || true; command -v adb || true'

section "Repository"
cmd git-status git status --short
cmd git-branch git branch --show-current
cmd git-log git log --oneline --decorate -n 20
cmd git-remotes git remote -v
cmd submodule-status git submodule status --recursive

section "Required files"
probe_file ".gitmodules"
probe_file "gradlew"
probe_file "settings.gradle"
probe_file "media/core_settings.gradle"
probe_file "media/libraries/common_ktx/proguard-rules.txt"
probe_file "scripts/check-submodules.sh"
probe_file ".github/workflows"

section "Repository checks"
if [ -f scripts/check-submodules.sh ]; then
  cmd check-submodules bash scripts/check-submodules.sh
else
  printf '⚠️ `scripts/check-submodules.sh` not found.\n' >>"$REPORT"
  WARNS=$((WARNS + 1))
fi
cmd shell-syntax bash -lc 'find scripts -type f -name "*.sh" -print -exec sh -n {} \;'
cmd diff-check git diff --check

section "Safety greps"
cmd vendor-private-grep bash -lc 'grep -RIn "android.tw.john.TWUtil\|android.tw.john.TWClient\|Class.forName(\"android.tw\|com.tw.service\|com.tw.service.xt\|com.tw.music.action\|android.uid.system\|sharedUserId" app/src/main app/src/test || true'
cmd probe-framework-grep bash -lc 'grep -RIn "TS18IntegrationProbe\|TS18EnvironmentSnapshot\|TS18ContractRegistry\|TS18AdapterCapability\|TWUtil reflection\|TWClient reflection\|vendor package scanner\|default-off adapter\|default-off probe\|diagnostics-only app module\|runtime contract registry\|TS18 probe layer\|probe-only layer\|adapter skeleton\|TWUtil scanner\|TWClient scanner" app/src/main app/src/test docs .github AGENTS.md README.md || true'

section "Summary"
{
  echo "- Failed diagnostic commands: $FAILS"
  echo "- Missing-file warnings: $WARNS"
  echo "- Report path: \`$REPORT\`"
} >>"$REPORT"

cat "$REPORT"

if [ "$STRICT" = "1" ] && [ "$FAILS" -gt 0 ]; then
  exit 1
fi
exit 0
