#!/usr/bin/env bash
# check-submodules.sh — verify that required git submodules are initialized
#
# Usage:
#   bash ./scripts/check-submodules.sh          # read-only validation (default)
#   CHECK_SUBMODULES_REPAIR=1 bash ./scripts/check-submodules.sh
#       or
#   bash ./scripts/check-submodules.sh --repair # run git submodule sync+update
#
# Required submodules for a full build:
#   media/                             — patched OxygenCobalt/media (Media3 + ExoPlayer)
#     core_settings.gradle             — applied by settings.gradle; MUST exist before Gradle
#     libraries/decoder_ffmpeg/src/main/jni/ffmpeg/  — nested ffmpeg (from git.ffmpeg.org)
#   musikr/src/main/cpp/taglib/        — taglib (from github.com/taglib/taglib)
#
# ZIP/snapshot environments (no .git directory) cannot run Gradle successfully
# because settings.gradle unconditionally applies media/core_settings.gradle.
# Classification: SUBMODULE_BLOCKER — environment-limited, not an app code issue.

set -euo pipefail

# ── Environment detection ────────────────────────────────────────────────────

REPAIR=0
if [[ "${CHECK_SUBMODULES_REPAIR:-0}" == "1" ]] || [[ "${1:-}" == "--repair" ]]; then
  REPAIR=1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${REPO_ROOT}"

# Detect ZIP/snapshot: no .git means submodule commands are impossible
if [[ ! -e ".git" ]]; then
  echo "::error::SUBMODULE_BLOCKER: This directory has no .git — it appears to be a ZIP/snapshot extract."
  echo "::error::ZIP snapshots cannot run Gradle because settings.gradle requires media/core_settings.gradle from the submodule."
  echo "::error::Clone the repository properly:"
  echo "::error::  git clone --recurse-submodules https://github.com/cbkii/Auxio-TS.git"
  exit 1
fi

# ── Optional repair mode ─────────────────────────────────────────────────────

if [[ "${REPAIR}" -eq 1 ]]; then
  echo "--- Repair mode: syncing and updating submodules ---"
  git submodule sync --recursive
  git submodule update --init --recursive --jobs 4 || {
    echo "::warning::git submodule update completed with some failures (ffmpeg may be unreachable)."
    echo "::warning::If only ffmpeg is missing, the build may still succeed if CMake can skip it."
  }
fi

# ── Validation ───────────────────────────────────────────────────────────────

missing=0

check_required_file() {
  local path="$1"
  local hint="${2:-}"
  if [[ ! -f "${path}" ]]; then
    echo "::error::MISSING required file: ${path}"
    if [[ -n "${hint}" ]]; then
      echo "::error::  → ${hint}"
    fi
    missing=1
  else
    echo "OK  file: ${path}"
  fi
}

check_required_dir_nonempty() {
  local path="$1"
  local sentinel="$2"
  local hint="${3:-}"
  if [[ ! -d "${path}" ]]; then
    echo "::error::MISSING required directory: ${path}"
    if [[ -n "${hint}" ]]; then
      echo "::error::  → ${hint}"
    fi
    missing=1
  elif [[ ! -f "${path}/${sentinel}" ]]; then
    echo "::error::EMPTY submodule directory: ${path} (${sentinel} not found — submodule not initialized)"
    if [[ -n "${hint}" ]]; then
      echo "::error::  → ${hint}"
    fi
    missing=1
  else
    echo "OK  dir:  ${path} (${sentinel} present)"
  fi
}

echo "--- Submodule validation ---"

# media/core_settings.gradle: applied by settings.gradle at configuration time.
# If this file is missing, Gradle cannot configure the project at all.
check_required_file \
  "media/core_settings.gradle" \
  "media submodule not initialized — Gradle CANNOT run without this file."

# media ffmpeg nested submodule: required for CMake native build of decoder_ffmpeg.
# Note: git.ffmpeg.org may be unreachable in air-gapped/sandboxed environments.
# In GitHub Actions with fetch-depth: 0, this initialises successfully.
check_required_dir_nonempty \
  "media/libraries/decoder_ffmpeg/src/main/jni/ffmpeg" \
  "configure" \
  "ffmpeg nested submodule not initialized — native decoder build will fail."

# taglib: required for metadata parsing (CMake/musikr).
check_required_file \
  "musikr/src/main/cpp/taglib/CMakeLists.txt" \
  "taglib submodule not initialized — musikr native build will fail."

# ── Result ───────────────────────────────────────────────────────────────────

if [[ "${missing}" -ne 0 ]]; then
  echo ""
  echo "::error::SUBMODULE_BLOCKER: One or more required submodules are not initialized."
  echo "::error::This is an environment/setup issue — not an app code issue."
  echo "::error::"
  echo "::error::Repair commands:"
  echo "::error::  # For a fresh clone:"
  echo "::error::  git clone --recurse-submodules https://github.com/cbkii/Auxio-TS.git"
  echo "::error::"
  echo "::error::  # For an existing clone:"
  echo "::error::  git submodule sync --recursive"
  echo "::error::  git submodule update --init --recursive --jobs 4"
  echo "::error::"
  echo "::error::  # Or run this script in repair mode:"
  echo "::error::  bash ./scripts/check-submodules.sh --repair"
  echo "::error::"
  echo "::error::Note: the ffmpeg nested submodule requires git.ffmpeg.org to be reachable."
  echo "::error::      In air-gapped/sandbox environments it may fail — this is expected."
  exit 1
fi

echo ""
echo "Submodule validation passed."
if command -v git >/dev/null 2>&1; then
  echo "--- git submodule status --recursive ---"
  git submodule status --recursive 2>/dev/null || true
fi
