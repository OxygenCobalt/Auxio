#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ ! -e ".git" ]]; then
  echo "::error::SNAPSHOT_LIMITATION: no .git directory detected (ZIP/snapshot checkout)."
  echo "::error::Gradle requires git submodules in this repository; use a real clone."
  exit 1
fi

if [[ -f gradlew ]]; then
  chmod +x gradlew || {
    echo "::error::REAL_BUILD_FAILURE: unable to make gradlew executable."
    exit 1
  }
else
  echo "::error::REAL_BUILD_FAILURE: gradlew is missing from repository root."
  exit 1
fi

echo "--- Syncing submodules ---"
git submodule sync --recursive

echo "--- Updating submodules ---"
if ! git submodule update --init --recursive --jobs 4; then
  echo "::warning::SUBMODULE_BLOCKER: recursive submodule update reported failures."
  echo "::warning::This is expected in restricted environments when git.ffmpeg.org is unreachable."
fi

echo "--- git submodule status --recursive ---"
git submodule status --recursive || true

if [[ ! -f media/libraries/common_ktx/proguard-rules.txt ]]; then
  mkdir -p media/libraries/common_ktx
  : > media/libraries/common_ktx/proguard-rules.txt
  echo "::warning::UPSTREAM_MEDIA_QUIRK: created media/libraries/common_ktx/proguard-rules.txt stub."
fi

bash ./scripts/check-submodules.sh

required_files=(
  "media/core_settings.gradle"
  "musikr/src/main/cpp/taglib/CMakeLists.txt"
  "media/libraries/common_ktx/proguard-rules.txt"
)

missing=0
for file in "${required_files[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "::error::SUBMODULE_BLOCKER: missing required file after preparation: $file"
    missing=1
  fi
done

if [[ "$missing" -ne 0 ]]; then
  exit 1
fi

echo "prepare-ci-environment: ready"
