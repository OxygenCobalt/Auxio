#!/usr/bin/env bash
# prepare-ci-environment.sh — prepare the repository environment before running Gradle
#
# Run this script once before any Gradle command, in any environment:
#   bash ./scripts/prepare-ci-environment.sh
#
# It is safe to run repeatedly (idempotent).
# It does NOT build the project or run Gradle tasks.
#
# Outcome classifications used in this script:
#   SNAPSHOT_LIMITATION   — no .git directory; ZIP/snapshot export without submodule contents.
#                           Gradle CANNOT run. Submodule validation is impossible.
#   SUBMODULE_BLOCKER     — .git present but required submodule files are missing or empty.
#                           Run git submodule sync/update to repair.
#   UPSTREAM_MEDIA_QUIRK  — media submodule is initialized but is missing
#                           common_ktx/proguard-rules.txt at the pinned commit.
#                           Fixed automatically by creating an empty stub file.
#   REAL_BUILD_FAILURE    — submodules and prep passed; Gradle itself failed.
#                           This is a real app/build issue, not an environment issue.
#
# After this script exits 0, you can run:
#   ./gradlew --no-daemon --stacktrace help
#   ./gradlew --no-daemon --stacktrace :app:assembleDebug
#   ./gradlew --no-daemon --stacktrace --continue spotlessCheck \
#     :app:testDebugUnitTest :musikr:testDebugUnitTest :app:lintDebug

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${REPO_ROOT}"

echo "=== Auxio-TS: pre-Gradle environment preparation ==="
echo "Working directory: ${REPO_ROOT}"
echo ""

# ── 1. ZIP/snapshot detection ────────────────────────────────────────────────

if [[ ! -e ".git" ]]; then
  echo "SNAPSHOT_LIMITATION: This directory has no .git directory."
  echo ""
  echo "This appears to be a ZIP/snapshot export (e.g. GitHub 'Download ZIP' or a"
  echo "Codex/agent environment that extracted an archive without submodule contents)."
  echo ""
  echo "Gradle CANNOT be configured because settings.gradle unconditionally applies"
  echo "media/core_settings.gradle, which only exists in the initialized media submodule."
  echo ""
  echo "To fully validate the project, clone the repository properly:"
  echo "  git clone --recurse-submodules <repo-url>"
  echo ""
  echo "There is no automated repair for ZIP/snapshot environments."
  exit 1
fi

# ── 2. Submodule sync and update ─────────────────────────────────────────────

echo "--- Step 1: Submodule sync ---"
git submodule sync --recursive
echo "Submodule URLs synced."
echo ""

echo "--- Step 2: Submodule init/update ---"
# || true because git.ffmpeg.org may be unreachable in air-gapped/sandbox environments.
# check-submodules.sh (called below) will detect if the required paths are still missing.
git submodule update --init --recursive --jobs 4 || {
  echo ""
  echo "WARNING: git submodule update exited non-zero."
  echo "  This is expected if git.ffmpeg.org is unreachable (air-gapped/sandbox)."
  echo "  Continuing — check-submodules.sh will verify which paths are actually present."
  echo ""
}

echo ""
echo "--- Step 3: Submodule status ---"
git submodule status --recursive
echo ""

# ── 3. Validate required submodule files ────────────────────────────────────

echo "--- Step 4: Submodule validation (check-submodules.sh) ---"
if ! bash "${SCRIPT_DIR}/check-submodules.sh"; then
  echo ""
  echo "SUBMODULE_BLOCKER: check-submodules.sh reported missing required files."
  echo "This is an environment/setup issue — not an app code issue."
  echo ""
  ORIGIN_URL="$(git config --get remote.origin.url 2>/dev/null || echo "<remote-url>")"
  echo "Repair commands:"
  echo "  git submodule sync --recursive"
  echo "  git submodule update --init --recursive --jobs 4"
  echo "  # Or re-run this script in repair mode:"
  echo "  bash ./scripts/check-submodules.sh --repair"
  echo "  bash ./scripts/prepare-ci-environment.sh"
  echo ""
  echo "Fresh clone:"
  echo "  git clone --recurse-submodules ${ORIGIN_URL}"
  exit 1
fi
echo ""

# ── 4. Apply known upstream media submodule quirks ──────────────────────────

echo "--- Step 5: Apply upstream media submodule workarounds ---"

# UPSTREAM_MEDIA_QUIRK: media/libraries/common_ktx/proguard-rules.txt is absent from the
# OxygenCobalt/media submodule at commit 0b01e32 ("remove deprecated ndk dir use").
# The shared common_library_config.gradle declares:
#   consumerProguardFiles 'proguard-rules.txt'
# for every library module. Without this file, Gradle fails at mergeDebugConsumerProguardFiles.
# Fix: create an empty stub. This is NOT committed to the superproject — it is a local workaround.
# If a future media submodule bump adds the file, this step becomes a no-op.
if [[ -d "media/libraries/common_ktx" ]] && [[ ! -f "media/libraries/common_ktx/proguard-rules.txt" ]]; then
  touch "media/libraries/common_ktx/proguard-rules.txt"
  echo "UPSTREAM_MEDIA_QUIRK: Created stub media/libraries/common_ktx/proguard-rules.txt"
  echo "  This is a local workaround for a missing file in OxygenCobalt/media at commit 0b01e32."
  echo "  It is not committed to the superproject."
elif [[ -f "media/libraries/common_ktx/proguard-rules.txt" ]]; then
  echo "OK: media/libraries/common_ktx/proguard-rules.txt already present."
else
  echo "SKIP: media/libraries/common_ktx directory not found — skipping proguard stub."
fi
echo ""

# ── 5. Final verification ────────────────────────────────────────────────────

echo "--- Step 6: Final path verification ---"
all_ok=1

verify_file() {
  local path="$1"
  local label="${2:-${path}}"
  if [[ -f "${path}" ]]; then
    echo "OK  ${label}"
  else
    echo "FAIL ${label} (MISSING)"
    all_ok=0
  fi
}

verify_file "media/core_settings.gradle"          "media/core_settings.gradle"
verify_file "media/libraries/common_ktx/proguard-rules.txt" "media/libraries/common_ktx/proguard-rules.txt"
verify_file "musikr/src/main/cpp/taglib/CMakeLists.txt"     "musikr/src/main/cpp/taglib/CMakeLists.txt"
echo ""

if [[ "${all_ok}" -ne 1 ]]; then
  echo "ERROR: One or more required files are still missing after preparation."
  echo "If this is a SUBMODULE_BLOCKER, re-run after repairing submodules."
  exit 1
fi

echo "=== Environment preparation complete. Gradle is ready to run. ==="
echo ""
echo "Next steps:"
echo "  ./gradlew --no-daemon --stacktrace help"
echo "  ./gradlew --no-daemon --stacktrace :app:assembleDebug"
echo "  ./gradlew --no-daemon --stacktrace --continue spotlessCheck \\"
echo "    :app:testDebugUnitTest :musikr:testDebugUnitTest :app:lintDebug"
echo ""
echo "If Gradle fails after this script exits 0, classify as: REAL_BUILD_FAILURE"
