#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
APKTOOL_JAR="${APKTOOL_JAR:-$ROOT_DIR/.tools/apktool.jar}"
BASE_APK="${1:-}"
CANDIDATE_APK="${2:-}"

if [[ -z "$BASE_APK" || -z "$CANDIDATE_APK" ]]; then
  cat <<'USAGE' >&2
Usage: tools/parity/diff_manifest_resources.sh /path/to/baseline.apk /path/to/candidate.apk

Compares:
  - AndroidManifest.xml text
  - resource file inventory under res/
USAGE
  exit 1
fi

[[ -f "$BASE_APK" ]] || { echo "[ERR] Baseline APK not found: $BASE_APK" >&2; exit 1; }
[[ -f "$CANDIDATE_APK" ]] || { echo "[ERR] Candidate APK not found: $CANDIDATE_APK" >&2; exit 1; }
[[ -f "$APKTOOL_JAR" ]] || { echo "[ERR] Missing apktool jar: $APKTOOL_JAR" >&2; exit 1; }

WORK_DIR="$(mktemp -d "${TMPDIR:-/tmp}/parity-diff.XXXXXX")"
trap 'rm -rf "$WORK_DIR"' EXIT

BASE_OUT="$WORK_DIR/base"
CAND_OUT="$WORK_DIR/candidate"
REPORT_DIR="$ROOT_DIR/docs/reports"
mkdir -p "$REPORT_DIR"

echo "[INFO] Decoding baseline APK..."
java -jar "$APKTOOL_JAR" d -f -o "$BASE_OUT" "$BASE_APK" >/dev/null
echo "[INFO] Decoding candidate APK..."
java -jar "$APKTOOL_JAR" d -f -o "$CAND_OUT" "$CANDIDATE_APK" >/dev/null

MANIFEST_DIFF="$REPORT_DIR/parity-manifest.diff"
RES_DIFF="$REPORT_DIR/parity-resources.diff"

# Validate decoded outputs exist before diffing.
[[ -f "$BASE_OUT/AndroidManifest.xml" ]] || { echo "[ERR] Decoded baseline AndroidManifest.xml missing." >&2; exit 1; }
[[ -f "$CAND_OUT/AndroidManifest.xml" ]] || { echo "[ERR] Decoded candidate AndroidManifest.xml missing." >&2; exit 1; }

set +e
diff -u "$BASE_OUT/AndroidManifest.xml" "$CAND_OUT/AndroidManifest.xml" > "$MANIFEST_DIFF"
manifest_rc=$?

(
  cd "$BASE_OUT" && find res -type f | sort
) > "$WORK_DIR/base-res.txt"
(
  cd "$CAND_OUT" && find res -type f | sort
) > "$WORK_DIR/candidate-res.txt"
diff -u "$WORK_DIR/base-res.txt" "$WORK_DIR/candidate-res.txt" > "$RES_DIFF"
res_rc=$?
set -e

# diff exits 0=identical, 1=differences, 2=operational error.
# Treat exit 2 from diff as a hard failure rather than silently mapping it to "differences found".
if [[ "$manifest_rc" -eq 2 ]]; then
  echo "[ERR] diff encountered an operational error comparing manifests." >&2
  rm -f "$MANIFEST_DIFF"
  exit 1
fi
if [[ "$res_rc" -eq 2 ]]; then
  echo "[ERR] diff encountered an operational error comparing resource inventories." >&2
  rm -f "$RES_DIFF"
  exit 1
fi

# Remove empty report files for whichever comparison found no differences.
[[ "$manifest_rc" -eq 0 ]] && rm -f "$MANIFEST_DIFF"
[[ "$res_rc" -eq 0 ]] && rm -f "$RES_DIFF"

if [[ "$manifest_rc" -eq 0 && "$res_rc" -eq 0 ]]; then
  echo "[OK] No manifest/resource inventory differences detected."
  exit 0
fi

echo "[WARN] Differences detected."
[[ "$manifest_rc" -ne 0 ]] && echo "  - Manifest diff: $MANIFEST_DIFF"
[[ "$res_rc" -ne 0 ]] && echo "  - Resource inventory diff: $RES_DIFF"
exit 2
