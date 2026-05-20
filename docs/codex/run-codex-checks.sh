#!/usr/bin/env bash
set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODE="${1:-quick}"
case "$MODE" in
  repair) bash "$SCRIPT_DIR/repair-codex-env.sh" ;;
  quick|submodules|toolchain|android|docs|ts18|ci-parity|full) bash "$SCRIPT_DIR/validate-codex-env.sh" "$MODE" ;;
  *) echo "Usage: $0 {quick|submodules|toolchain|android|docs|ts18|ci-parity|full|repair}"; exit 2 ;;
esac
RC=$?
if [[ $RC -ne 0 ]]; then echo "Recommended: bash $SCRIPT_DIR/repair-codex-env.sh && bash $SCRIPT_DIR/run-codex-checks.sh quick"; fi
exit $RC
