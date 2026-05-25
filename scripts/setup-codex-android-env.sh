#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="$(git rev-parse --show-toplevel)"
cd "$ROOT"

log() {
  printf '[codex-android-setup] %s\n' "$*"
}

log "Repo: $ROOT"

log "System info"
uname -a || true
id || true

log "Checking base tools"
for cmd in git bash curl unzip zip python3 java javac cmake ninja; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    log "MISSING: $cmd"
  else
    log "FOUND: $cmd -> $(command -v "$cmd")"
  fi
done

log "Java"
java -version
javac -version

# Prefer preinstalled Android SDK if present.
if [ -z "${ANDROID_HOME:-}" ]; then
  if [ -d "$HOME/android-sdk" ]; then
    export ANDROID_HOME="$HOME/android-sdk"
  elif [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
  elif [ -d "$HOME/Library/Android/sdk" ]; then
    export ANDROID_HOME="$HOME/Library/Android/sdk"
  elif [ -d "/opt/android-sdk" ]; then
    export ANDROID_HOME="/opt/android-sdk"
  fi
fi

# If no SDK exists, bootstrap command-line tools if network is available.
if [ -z "${ANDROID_HOME:-}" ]; then
  export ANDROID_HOME="$HOME/android-sdk"
fi

export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

mkdir -p "$ANDROID_HOME"

log "ANDROID_HOME=$ANDROID_HOME"
log "ANDROID_SDK_ROOT=$ANDROID_SDK_ROOT"

if ! command -v sdkmanager >/dev/null 2>&1; then
  log "sdkmanager not found; attempting Android command-line tools bootstrap"

  mkdir -p "$ANDROID_HOME/cmdline-tools"
  TMP_ZIP="$(mktemp -t android-cmdline-tools.XXXXXX.zip)"

  curl -fsSL \
    https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip \
    -o "$TMP_ZIP"

  rm -rf "$ANDROID_HOME/cmdline-tools/latest"
  unzip -q "$TMP_ZIP" -d "$ANDROID_HOME/cmdline-tools"
  mv "$ANDROID_HOME/cmdline-tools/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
  rm -f "$TMP_ZIP"

  export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
fi

if ! command -v sdkmanager >/dev/null 2>&1; then
  log "ERROR: sdkmanager still unavailable after bootstrap"
  exit 20
fi

log "sdkmanager=$(command -v sdkmanager)"

log "Accepting SDK licences"
yes | sdkmanager --licenses >/tmp/codex-sdk-licenses.log 2>&1 || true

# Repo-specific values from build.gradle:
# target_sdk = 36
# ndk_version = 28.2.13676358
log "Installing Android SDK packages"
sdkmanager \
  "platform-tools" \
  "platforms;android-36" \
  "build-tools;36.0.0" \
  "cmake;3.22.1" \
  "ndk;28.2.13676358"

printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
log "Wrote local.properties"

log "Syncing submodules"
git submodule sync --recursive
git submodule update --init --recursive --jobs 4

log "Running repo CI preparation"
bash scripts/prepare-ci-environment.sh

log "Checking submodules"
bash scripts/check-submodules.sh

log "Checking head-unit safety"
bash scripts/check-headunit-compat-safety.sh

log "Gradle smoke test"
chmod +x ./gradlew
./gradlew --no-daemon --stacktrace tasks >/tmp/codex-gradle-tasks.log

log "Setup complete"
