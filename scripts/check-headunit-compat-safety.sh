#!/usr/bin/env bash
set -euo pipefail
if rg -n "com\.tw\.music\.action|android\.tw\.john|com\.tw\.service|sharedUserId|android\.uid\.system" app/src/main app/src/test; then
  echo "Forbidden private/vendor hooks found in product code" >&2
  exit 1
fi
echo "headunit compat safety checks passed"
