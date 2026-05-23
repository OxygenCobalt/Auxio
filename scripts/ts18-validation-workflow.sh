#!/usr/bin/env bash
set -euo pipefail
cmd=${1:-help}; shift || true
help(){ cat <<H
usage:
  $0 capture --out DIR --scenario TS18-STD-001 --label run1 [--zip]
  $0 validate --pack PACK
  $0 summarise --pack PACK
  $0 classify --pack PACK
  $0 propose-matrix --pack PACK
  $0 fixture-test
H
}
pack=""
while [ $# -gt 0 ]; do case "$1" in --pack) pack="$2"; shift 2;; --out|--scenario|--label|--zip|--single|--before|--after|--action|--no-logcat|--logcat-seconds|--dry-run|--verbose) break;; *) shift;; esac; done
case "$cmd" in
 capture) bash scripts/ts18-capture-validation-pack.sh "$@"; echo "next: validate -> summarise -> classify -> propose-matrix";;
 validate) python3 scripts/ts18-validate-evidence-pack.py "$pack";;
 summarise) python3 scripts/ts18-summarise-validation-pack.py "$pack";;
 classify) python3 scripts/ts18-classify-parity-gaps.py "$pack" docs/templates/TS18_VALIDATION_SCENARIO_MAP.json;;
 propose-matrix) python3 scripts/ts18-propose-matrix-update.py "$pack";;
 fixture-test) bash scripts/test-ts18-validation-tools.sh;;
 help|--help|-h) help;;
 *) help; exit 1;;
esac
