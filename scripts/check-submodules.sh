#!/usr/bin/env bash
set -euo pipefail

missing=0

check_required_file() {
  local path="$1"
  local hint="${2:-}"
  if [[ ! -f "${path}" ]]; then
    echo "::error::Missing required file: ${path}"
    if [[ -n "${hint}" ]]; then
      echo "::error::${hint}"
    fi
    missing=1
  fi
}

check_required_dir() {
  local path="$1"
  local hint="${2:-}"
  if [[ ! -d "${path}" ]]; then
    echo "::error::Missing required directory: ${path}"
    if [[ -n "${hint}" ]]; then
      echo "::error::${hint}"
    fi
    missing=1
  fi
}

check_required_file \
  "media/core_settings.gradle" \
  "media submodule is not initialized; run recursive submodule checkout/update."
check_required_dir \
  "media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg" \
  "nested media submodules are not initialized; run recursive submodule checkout/update."
check_required_file \
  "musikr/src/main/cpp/taglib/CMakeLists.txt" \
  "taglib submodule is not initialized; run recursive submodule checkout/update."

if [[ "${missing}" -ne 0 ]]; then
  echo "::error::Submodules are not initialized. Run git submodule update --init --recursive or use actions/checkout with submodules: recursive."
  exit 1
fi

echo "Submodule validation passed."
