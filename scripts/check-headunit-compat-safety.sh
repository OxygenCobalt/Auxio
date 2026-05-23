#!/usr/bin/env bash
set -euo pipefail
if command -v rg >/dev/null 2>&1; then
  checker=(rg -n "com\\.tw\\.music\\.action|android\\.tw\\.john|com\\.tw\\.service|sharedUserId|android\\.uid\\.system")
else
  checker=(grep -RInE "com\\.tw\\.music\\.action|android\\.tw\\.john|com\\.tw\\.service|sharedUserId|android\\.uid\\.system")
fi
if "${checker[@]}" app/src/main app/src/test; then
  echo "Forbidden private/vendor hooks found in product code" >&2
  exit 1
fi
echo "headunit compat safety checks passed"
