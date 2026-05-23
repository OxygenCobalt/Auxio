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

For Phase 5G/6A evidence packs, use scripts/ts18-evidence-workflow.sh as the
canonical entrypoint. This wrapper stays for validation-pack capture/tests and
delegates evidence-pack summarise/propose steps when evidence-manifest.json is present.
H
}
pack=""
require_pack() {
  if [ -z "$pack" ]; then
    echo "missing required --pack argument" >&2
    help
    exit 1
  fi
}
is_evidence_pack() {
 [ -f "$pack/evidence-manifest.json" ] || {
   [ ! -f "$pack/manifest.json" ] &&
     [ -d "$pack/raw" ] &&
     [ -f "$pack/raw/action_open_now_playing.txt" ]
 }
}
while [ $# -gt 0 ]; do case "$1" in --pack) pack="$2"; shift 2;; --out|--scenario|--label|--zip|--single|--before|--after|--action|--no-logcat|--logcat-seconds|--dry-run|--verbose) break;; *) shift;; esac; done
case "$cmd" in
 capture) bash scripts/ts18-capture-validation-pack.sh "$@"; echo "next: validate -> summarise -> classify -> propose-matrix (evidence packs: use scripts/ts18-evidence-workflow.sh)";;
 validate) require_pack; python3 scripts/ts18-validate-evidence-pack.py "$pack";;
 summarise)
 require_pack
 if is_evidence_pack; then
   bash scripts/ts18-evidence-workflow.sh --summarise "$pack"
 else
   python3 scripts/ts18-summarise-validation-pack.py "$pack"
 fi
 ;;
 classify) require_pack; python3 scripts/ts18-classify-parity-gaps.py "$pack" docs/templates/TS18_VALIDATION_SCENARIO_MAP.json;;
 propose-matrix)
 require_pack
 if is_evidence_pack; then
   bash scripts/ts18-evidence-workflow.sh --propose-matrix "$pack"
 else
   python3 scripts/ts18-propose-matrix-update.py "$pack"
 fi
 ;;
 fixture-test) bash scripts/test-ts18-validation-tools.sh;;
 help|--help|-h) help;;
 *) help; exit 1;;
esac
