#!/usr/bin/env bash
set -u
set -o pipefail
source "$(dirname "$0")/common.sh"
ROOT="$(repo_root)"; OUT="$ROOT/docs/codex/out"; mkdir -p "$OUT"; LOG_FILE="$OUT/setup-codex-env.log"; : >"$LOG_FILE"
ENV_FILE="$OUT/codex-env.sh"

run_optional "Environment evidence" bash -lc 'echo "date=$(date -u)"; echo "shell=$SHELL"; echo "path=$PATH"; uname -a; id'
run_required SUBMODULE_BLOCKER "Canonical prepare" bash "$ROOT/scripts/prepare-ci-environment.sh"
run_required SUBMODULE_BLOCKER "Submodule check" bash "$ROOT/scripts/check-submodules.sh"
run_optional "Check media/core_settings.gradle" test -f "$ROOT/media/core_settings.gradle"
run_optional "Check taglib CMakeLists" test -f "$ROOT/musikr/src/main/cpp/taglib/CMakeLists.txt"
run_optional "Check nested ffmpeg" test -d "$ROOT/media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg"
run_optional "Detect package manager" bash -lc 'command -v apt-get || command -v dnf || command -v yum || command -v apk || command -v pacman || command -v brew || echo none'
run_optional "Git version" git -C "$ROOT" --version
run_optional "Java version" bash -lc 'java -version'
run_optional "Gradle version" bash -lc 'cd "$0" && ./gradlew --no-daemon --version' "$ROOT"

if command -v sdkmanager >/dev/null 2>&1; then
  run_optional "Accept SDK licences" bash -lc 'yes | sdkmanager --licenses >/dev/null'
else
  warn "ANDROID_SDK_BLOCKER: sdkmanager not found; SDK auto-install skipped"
fi

cat >"$ENV_FILE" <<EOT
# source this in agent phase if needed
export ANDROID_HOME="
export ANDROID_SDK_ROOT="
export JAVA_HOME="
export GRADLE_USER_HOME="\${HOME}/.gradle"
EOT

run_optional "Gradle help smoke" with_timeout 900 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace help' "$ROOT"
if [[ "${CODEX_SETUP_FULL_BUILD:-0}" = "1" ]]; then
  run_optional "Optional full build" with_timeout 2400 bash -lc 'cd "$0" && ./gradlew --no-daemon --stacktrace :app:assembleDebug'
else
  skip "Full build skipped (set CODEX_SETUP_FULL_BUILD=1 to enable)"
fi
write_summary
echo "Next recommended command: bash docs/codex/run-codex-checks.sh quick" | tee -a "$LOG_FILE"
[[ $FAILED -eq 0 ]] || exit 1
