#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OUT_DIR="${ROOT_DIR}/docs/reports"
APK_PATH="${ROOT_DIR}/dist/com.tw.music-unsigned.apk"

mkdir -p "${OUT_DIR}"

if [[ ! -f "${APK_PATH}" ]]; then
  echo "Missing artifact: ${APK_PATH}" >&2
  echo "Run scripts/02_build_unsigned.sh before hashing artifacts." >&2
  exit 1
fi

sha256sum "${APK_PATH}" > "${OUT_DIR}/baseline-apk.sha256"
sha256sum "${ROOT_DIR}/app/apktool/AndroidManifest.xml" > "${OUT_DIR}/baseline-manifest.sha256"
sha256sum "${ROOT_DIR}/docs/maps/activities.txt" > "${OUT_DIR}/baseline-activities.sha256"
sha256sum "${ROOT_DIR}/docs/maps/services.txt" > "${OUT_DIR}/baseline-services.sha256"
sha256sum "${ROOT_DIR}/docs/maps/receivers.txt" > "${OUT_DIR}/baseline-receivers.sha256"

cat <<MSG
Wrote artifact hashes:
- ${OUT_DIR}/baseline-apk.sha256
- ${OUT_DIR}/baseline-manifest.sha256
- ${OUT_DIR}/baseline-activities.sha256
- ${OUT_DIR}/baseline-services.sha256
- ${OUT_DIR}/baseline-receivers.sha256
MSG
