#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
FAILURES=0

check_token() {
  local token="$1"
  local target="$2"
  if grep -rqF "$token" "$target"; then
    echo "[PASS] token found: $token"
  else
    echo "[FAIL] token missing: $token" >&2
    FAILURES=$((FAILURES + 1))
  fi
}

echo "Running legacy parity smoke checks..."

check_token "com.tw.music.action.cmd" "${ROOT_DIR}/app/apktool"
check_token "com.tw.music.action.prev" "${ROOT_DIR}/app/apktool"
check_token "com.tw.music.action.next" "${ROOT_DIR}/app/apktool"
check_token "com.tw.music.action.pp" "${ROOT_DIR}/app/apktool"
check_token "com.tw.music.view.MusicWidgetProvider" "${ROOT_DIR}/app/apktool"
check_token "com.tw.eq.EQActivity" "${ROOT_DIR}/app/apktool"
check_token "com.tw.radio" "${ROOT_DIR}/app/apktool"
check_token "com.tw.service.xt.aidl.ITWCommandAidl" "${ROOT_DIR}/app/apktool"

if [[ ${FAILURES} -gt 0 ]]; then
  echo "Parity smoke checks failed: ${FAILURES} missing token(s)." >&2
  exit 1
fi

echo "Parity smoke checks passed."
