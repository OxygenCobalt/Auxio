#!/usr/bin/env bash
set -u
MODE="${1:-quick}"
case "$MODE" in
  repair) bash docs/codex/repair-codex-env.sh ;;
  quick|submodules|toolchain|android|docs|ts18|ci-parity|full) bash docs/codex/validate-codex-env.sh "$MODE" ;;
  *) echo "Usage: $0 {quick|submodules|toolchain|android|docs|ts18|ci-parity|full|repair}"; exit 2 ;;
esac
RC=$?
if [[ $RC -ne 0 ]]; then echo "Recommended: bash docs/codex/repair-codex-env.sh && bash docs/codex/run-codex-checks.sh quick"; fi
exit $RC
