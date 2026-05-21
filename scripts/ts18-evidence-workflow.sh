#!/usr/bin/env bash
set -euo pipefail

usage(){ cat <<USAGE
Usage: $0 [--capture] [--summarise <pack>] [--propose-matrix <pack>] [--generate-candidates <pack>] [--redact <pack>] [--fixture <pack>] [--include-ecosystem-context] [--dry-run]
USAGE
}

DRY=false
INCLUDE=false
MODE=""
PACK=""
while [ $# -gt 0 ]; do
  case "$1" in
    --capture) MODE="capture"; shift;;
    --summarise|--propose-matrix|--generate-candidates|--redact|--fixture) MODE="${1#--}"; PACK="$2"; shift 2;;
    --include-ecosystem-context) INCLUDE=true; shift;;
    --dry-run) DRY=true; shift;;
    -h|--help) usage; exit 0;;
    *) echo "Unknown arg: $1" >&2; usage; exit 2;;
  esac
done
run() { echo "+ $*"; if [ "$DRY" = true ]; then return 0; fi; "$@"; }

case "$MODE" in
  capture)
    if [ "$INCLUDE" = true ]; then
      run bash scripts/ts18-create-evidence-pack.sh --include-ecosystem-context
    else
      run bash scripts/ts18-create-evidence-pack.sh
    fi
    ;;
  fixture)
    run python3 scripts/ts18-summarise-evidence-pack.py "$PACK"
    run python3 scripts/ts18-propose-gap-matrix-update.py "$PACK"
    run python3 scripts/ts18-generate-native-candidates.py "$PACK"
    ;;
  summarise)
    run python3 scripts/ts18-summarise-evidence-pack.py "$PACK"
    ;;
  propose-matrix)
    run python3 scripts/ts18-propose-gap-matrix-update.py "$PACK"
    ;;
  generate-candidates)
    run python3 scripts/ts18-generate-native-candidates.py "$PACK"
    ;;
  redact)
    run bash scripts/redact_ts18_evidence_summary.sh "$PACK"
    ;;
  "") usage; exit 2 ;;
  *) echo "Unsupported mode: $MODE"; exit 2;;
esac

echo "Done. Next: review derived outputs and manually update docs/TS18_NATIVE_PARITY_GAP_MATRIX.md if warranted."
