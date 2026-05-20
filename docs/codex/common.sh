#!/usr/bin/env bash
set -u

FAILED=0; STEP=0
PASSED=(); WARNINGS=(); BLOCKERS=(); SKIPPED=(); RESULTS=()
LOG_FILE=""

repo_root(){ cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd; }
resolve_path(){ command -v realpath >/dev/null 2>&1 && realpath "$1" || python3 - <<'PY' "$1"
import os,sys;print(os.path.abspath(sys.argv[1]))
PY
}
now_utc(){ date -u +"%Y-%m-%dT%H:%M:%SZ"; }
workers(){ command -v nproc >/dev/null 2>&1 && nproc || echo 2; }

log(){ echo "$*" | tee -a "$LOG_FILE"; }
warn(){ WARNINGS+=("$*"); RESULTS+=("WARN|$*"); log "[WARN] $*"; }
block(){ BLOCKERS+=("$*"); RESULTS+=("BLOCK|$*"); FAILED=1; log "[BLOCK] $*"; }
pass(){ PASSED+=("$*"); RESULTS+=("PASS|$*"); log "[PASS] $*"; }
skip(){ SKIPPED+=("$*"); RESULTS+=("SKIP|$*"); log "[SKIP] $*"; }

with_timeout(){ local sec="$1"; shift; if command -v timeout >/dev/null 2>&1; then timeout "$sec" "$@"; elif command -v gtimeout >/dev/null 2>&1; then gtimeout "$sec" "$@"; else "$@"; fi; }
run_step(){ STEP=$((STEP+1)); log "[$STEP] $1"; }
run_optional(){ local label="$1"; shift; run_step "$label"; "$@" >>"$LOG_FILE" 2>&1 && pass "$label" || warn "$label (non-fatal)"; }
run_required(){ local cls="$1"; local label="$2"; shift 2; run_step "$label"; "$@" >>"$LOG_FILE" 2>&1 && pass "$label" || block "$cls: $label"; }

write_summary(){
  log "--- Summary ---"
  log "Passed: ${#PASSED[@]} | Warnings: ${#WARNINGS[@]} | Blockers: ${#BLOCKERS[@]} | Skipped: ${#SKIPPED[@]}"
}
