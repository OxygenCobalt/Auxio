#!/usr/bin/env bash
# Bootstrap cbkii/Auxio-TS for Codex cloud tasks.
#
# Purpose:
# - maximise the chance Codex can run repo CI-equivalent checks;
# - initialise submodules and Android/Gradle prerequisites;
# - avoid misleading early exits;
# - write a clear environment report for later agent turns.
#
# Intended Codex setup command:
#   bash scripts/codex/setup-auxio-ts.sh

set -u
set -o pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CODEX_DIR="$ROOT_DIR/.codex"
LOG_DIR="$CODEX_DIR/logs"
REPORT="$CODEX_DIR/setup-report.md"

mkdir -p "$LOG_DIR"

STRICT="${CODEX_STRICT_SETUP:-0}"
ANDROID_API="${ANDROID_API:-35}"
ANDROID_BUILD_TOOLS="${ANDROID_BUILD_TOOLS:-35.0.0}"
ANDROID_CMDLINE_TOOLS_VERSION="${ANDROID_CMDLINE_TOOLS_VERSION:-13114758}"
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
export ANDROID_HOME="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
export ANDROID_SDK_ROOT

PASSED=0
WARNED=0
FAILED=0

log() { printf '[codex-setup] %s\n' "$*"; }
warn() { WARNED=$((WARNED + 1)); printf '[codex-setup][WARN] %s\n' "$*" >&2; }
fail() {
  FAILED=$((FAILED + 1))
  printf '[codex-setup][FAIL] %s\n' "$*" >&2
  if [ "$STRICT" = "1" ]; then
    exit 1
  fi
}
pass() { PASSED=$((PASSED + 1)); printf '[codex-setup][OK] %s\n' "$*"; }

run_step() {
  local name="$1"
  shift
  local log_file="$LOG_DIR/${name//[^A-Za-z0-9_.-]/_}.log"
  log "START: $name"
  {
    printf '## %s\n\n' "$name"
    printf '$'
    printf ' %q' "$@"
    printf '\n\n'
    "$@"
  } >"$log_file" 2>&1
  local rc=$?
  if [ "$rc" -eq 0 ]; then
    pass "$name"
  else
    fail "$name failed with exit code $rc; see $log_file"
  fi
  return 0
}

append_report() {
  {
    printf '%s\n' "$*"
  } >>"$REPORT"
}

detect_pm() {
  if command -v apt-get >/dev/null 2>&1; then
    echo apt
  elif command -v apk >/dev/null 2>&1; then
    echo apk
  elif command -v dnf >/dev/null 2>&1; then
    echo dnf
  else
    echo none
  fi
}

install_packages() {
  local pm
  pm="$(detect_pm)"
  case "$pm" in
    apt)
      local sudo_cmd=""
      if command -v sudo >/dev/null 2>&1; then sudo_cmd="sudo"; fi
      run_step "apt-update" $sudo_cmd apt-get update -y
      run_step "apt-install-core" $sudo_cmd apt-get install -y \
        bash ca-certificates curl wget unzip zip git git-lfs jq file findutils coreutils \
        openssh-client rsync python3 python3-pip perl sed gawk grep procps \
        openjdk-21-jdk-headless cmake ninja-build pkg-config make gcc g++ \
        autoconf automake libtool patch xz-utils
      ;;
    apk)
      run_step "apk-add-core" apk add --no-cache \
        bash ca-certificates curl wget unzip zip git git-lfs jq file findutils coreutils \
        openssh-client rsync python3 py3-pip perl sed gawk grep procps \
        openjdk21 cmake ninja pkgconf make gcc g++ autoconf automake libtool patch xz
      ;;
    dnf)
      local sudo_cmd=""
      if command -v sudo >/dev/null 2>&1; then sudo_cmd="sudo"; fi
      run_step "dnf-install-core" $sudo_cmd dnf install -y \
        bash ca-certificates curl wget unzip zip git git-lfs jq file findutils coreutils \
        openssh-clients rsync python3 python3-pip perl sed gawk grep procps-ng \
        java-21-openjdk-devel cmake ninja-build pkgconf-pkg-config make gcc gcc-c++ \
        autoconf automake libtool patch xz
      ;;
    *)
      warn "No supported package manager found. Continuing with existing tools."
      ;;
  esac
}

setup_git() {
  cd "$ROOT_DIR" || return 0

  git config --global --add safe.directory "$ROOT_DIR" || true
  git config --global advice.detachedHead false || true
  git config --global fetch.recurseSubmodules on-demand || true
  git config --global submodule.recurse true || true

  if command -v git-lfs >/dev/null 2>&1; then
    run_step "git-lfs-install" git lfs install --skip-repo
  else
    warn "git-lfs not available"
  fi

  run_step "git-status-initial" git status --short
  run_step "git-submodule-status-initial" git submodule status --recursive
}

repair_submodules() {
  cd "$ROOT_DIR" || return 0

  if [ ! -f ".gitmodules" ]; then
    warn "No .gitmodules found; skipping submodule initialisation."
    return 0
  fi

  run_step "git-submodule-sync" git submodule sync --recursive

  # First normal recursive update.
  run_step "git-submodule-update-recursive" git submodule update --init --recursive --jobs 4

  # Retry once with lower parallelism for flaky network/submodule servers.
  if ! bash scripts/check-submodules.sh >/tmp/codex-submodule-check.log 2>&1; then
    warn "Submodule check failed after first update; retrying serial update."
    cp /tmp/codex-submodule-check.log "$LOG_DIR/submodule-check-after-first-update.log" || true
    run_step "git-submodule-update-serial-retry" git submodule update --init --recursive --jobs 1 --force
  fi

  if [ -x scripts/check-submodules.sh ] || [ -f scripts/check-submodules.sh ]; then
    run_step "repo-check-submodules" bash scripts/check-submodules.sh
  else
    warn "scripts/check-submodules.sh missing; cannot run repo submodule check."
  fi
}

install_android_sdk() {
  mkdir -p "$ANDROID_SDK_ROOT"

  if command -v sdkmanager >/dev/null 2>&1; then
    pass "sdkmanager already on PATH"
  else
    local tools_dir="$ANDROID_SDK_ROOT/cmdline-tools"
    local latest_dir="$tools_dir/latest"
    mkdir -p "$tools_dir"

    if [ ! -x "$latest_dir/bin/sdkmanager" ]; then
      local zip_path="$CODEX_DIR/android-commandlinetools.zip"
      local url="https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS_VERSION}_latest.zip"
      run_step "download-android-commandline-tools" curl -fsSL --retry 3 --retry-delay 5 -o "$zip_path" "$url"
      rm -rf "$tools_dir/cmdline-tools" "$latest_dir.tmp"
      mkdir -p "$latest_dir.tmp"
      run_step "unzip-android-commandline-tools" unzip -q "$zip_path" -d "$latest_dir.tmp"
      rm -rf "$latest_dir"
      if [ -d "$latest_dir.tmp/cmdline-tools" ]; then
        mv "$latest_dir.tmp/cmdline-tools" "$latest_dir"
      else
        warn "Unexpected Android commandline tools layout"
      fi
      rm -rf "$latest_dir.tmp"
    fi

    export PATH="$latest_dir/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
  fi

  if ! command -v sdkmanager >/dev/null 2>&1; then
    warn "sdkmanager unavailable; Android SDK install skipped."
    return 0
  fi

  yes | sdkmanager --licenses >"$LOG_DIR/android-sdk-licenses.log" 2>&1 || warn "Some Android SDK licences may not have been accepted"

  run_step "android-sdk-install" sdkmanager \
    "platform-tools" \
    "platforms;android-${ANDROID_API}" \
    "build-tools;${ANDROID_BUILD_TOOLS}" \
    "cmdline-tools;latest" \
    "extras;google;m2repository" \
    "extras;android;m2repository"

  {
    echo "ANDROID_HOME=$ANDROID_HOME"
    echo "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"
    echo "PATH=$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:\$PATH"
  } > "$CODEX_DIR/android-env"
}

prepare_gradle() {
  cd "$ROOT_DIR" || return 0

  if [ -f gradlew ]; then
    chmod +x gradlew || true
  else
    warn "gradlew not found"
    return 0
  fi

  mkdir -p "$HOME/.gradle"
  cat > "$HOME/.gradle/gradle.properties" <<'EOF'
org.gradle.daemon=false
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=false
kotlin.incremental=true
android.useAndroidX=true
EOF

  # Warm dependencies if possible. Do not fail setup on this; it may require submodules or SDK details.
  run_step "gradle-version" ./gradlew --version
  run_step "gradle-tasks-warmup" ./gradlew tasks --all --stacktrace --no-daemon
}

write_repo_doctor() {
  cat > "$CODEX_DIR/env-summary.txt" <<EOF
Auxio-TS Codex setup summary
============================

Date: $(date -u +"%Y-%m-%dT%H:%M:%SZ")
Root: $ROOT_DIR
ANDROID_HOME: ${ANDROID_HOME:-}
ANDROID_SDK_ROOT: ${ANDROID_SDK_ROOT:-}
Java: $(java -version 2>&1 | head -1 || true)
Git: $(git --version 2>/dev/null || true)
Gradle wrapper: $([ -f "$ROOT_DIR/gradlew" ] && echo present || echo missing)

Quick maintenance commands:
  bash scripts/codex/doctor-auxio-ts.sh
  bash scripts/codex/repair-auxio-ts.sh
  bash scripts/codex/run-auxio-ci-local.sh
EOF
}

main() {
  rm -f "$REPORT"
  append_report "# Auxio-TS Codex setup report"
  append_report ""
  append_report "- Date: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  append_report "- Root: \`$ROOT_DIR\`"
  append_report ""

  log "Bootstrapping Auxio-TS Codex environment at $ROOT_DIR"

  install_packages
  setup_git
  repair_submodules
  install_android_sdk
  prepare_gradle
  write_repo_doctor

  append_report "## Result"
  append_report ""
  append_report "- Passed steps: $PASSED"
  append_report "- Warnings: $WARNED"
  append_report "- Failed steps: $FAILED"
  append_report "- Logs: \`$LOG_DIR\`"
  append_report ""

  if [ "$FAILED" -gt 0 ]; then
    warn "Setup completed with $FAILED failed best-effort step(s). See $REPORT and $LOG_DIR."
    if [ "$STRICT" = "1" ]; then
      exit 1
    fi
  else
    pass "Setup completed"
  fi
}

main "$@"
