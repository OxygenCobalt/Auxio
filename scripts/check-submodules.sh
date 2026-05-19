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
  "Missing media/core_settings.gradle means the media submodule was not initialised. Do not create this file manually. The workflow must checkout submodules recursively."
check_required_dir \
  "media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg" \
  "Missing FFmpeg sources usually means nested media submodules were not initialised. Do not create these files manually. The workflow must checkout submodules recursively."
check_required_file \
  "musikr/src/main/cpp/taglib/CMakeLists.txt" \
  "Missing musikr/src/main/cpp/taglib/CMakeLists.txt means the taglib submodule was not initialised. Do not create this file manually. The workflow must checkout submodules recursively."

if [[ "${missing}" -ne 0 ]]; then
  echo "::error::Submodules are not initialized. Do not create missing files manually. Run git submodule update --init --recursive or ensure actions/checkout uses submodules: recursive."
  exit 1
fi

echo "Submodule validation passed."
