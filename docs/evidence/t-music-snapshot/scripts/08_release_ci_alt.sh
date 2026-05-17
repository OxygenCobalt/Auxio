#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

export KEYSTORE="${KEYSTORE:?Set KEYSTORE=/path/to/keystore.jks}"
export KEYSTORE_PASSWORD="${KEYSTORE_PASSWORD:?Set KEYSTORE_PASSWORD=password}"
export KEY_ALIAS="${KEY_ALIAS:?Set KEY_ALIAS=alias}"
export KEY_PASSWORD="${KEY_PASSWORD:-}"

bash "$ROOT/scripts/02_build_unsigned.sh"
bash "$ROOT/scripts/05_sign_verify.sh"

echo "[OK] Alternative release CI flow completed."
