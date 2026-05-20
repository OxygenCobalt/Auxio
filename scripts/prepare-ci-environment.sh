#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [[ -f gradlew ]]; then
  chmod +x gradlew || true
fi

if [[ -f .gitmodules ]]; then
  git submodule sync --recursive
  git submodule update --init --recursive --jobs 4
fi

if [[ ! -f media/libraries/common_ktx/proguard-rules.txt ]]; then
  echo "WARN: media/libraries/common_ktx/proguard-rules.txt missing in current checkout." >&2
fi

if [[ ! -f media/core_settings.gradle ]]; then
  echo "ERROR: media/core_settings.gradle missing after submodule preparation." >&2
  exit 1
fi

if [[ ! -f musikr/src/main/cpp/taglib/CMakeLists.txt ]]; then
  echo "ERROR: musikr taglib submodule missing after preparation." >&2
  exit 1
fi

echo "prepare-ci-environment: ready"
