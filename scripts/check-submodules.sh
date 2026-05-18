#!/usr/bin/env bash
set -euo pipefail

missing=0

check_required_file() {
  local path="$1"
  if [[ ! -f "${path}" ]]; then
    echo "::error::Missing required file: ${path}"
    missing=1
  fi
}

check_required_dir() {
  local path="$1"
  if [[ ! -d "${path}" ]]; then
    echo "::error::Missing required directory: ${path}"
    missing=1
  fi
}

check_required_file "media/core_settings.gradle"
check_required_dir "media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg"
check_required_dir "musikr/src/main/cpp/taglib"

if [[ "${missing}" -ne 0 ]]; then
  echo "::error::Submodules are not initialized. Do not create missing files manually. Run git submodule update --init --recursive or ensure actions/checkout uses submodules: recursive."
  exit 1
fi

echo "Submodule preflight passed."
