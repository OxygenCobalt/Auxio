#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$ROOT"

GH_REPO="${GH_REPO:-cbkii/t-music}"
ANDROID_BUILD_TOOLS_VERSION="${ANDROID_BUILD_TOOLS_VERSION:-35.0.0}"
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ROOT/.tools/android-sdk}"
ANDROID_HOME="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
JADX_VERSION="${JADX_VERSION:-1.5.5}"
JADX_HOME="${JADX_HOME:-$HOME/.local/opt/jadx-${JADX_VERSION}}"

export GH_REPO ANDROID_SDK_ROOT ANDROID_HOME JADX_HOME
export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION:$HOME/.local/bin:$PATH"

log(){ printf '\n[verify] %s\n' "$*"; }
warn(){ printf '\n[verify:WARN] %s\n' "$*" >&2; }

need(){ command -v "$1" >/dev/null 2>&1 || { warn "missing: $1"; return 1; }; }

log "Bash syntax checks"
bash -n scripts/codex/setup_readability_env.sh
bash -n scripts/codex/maintain_readability_env.sh
bash -n scripts/codex/verify_readability_env.sh

log "Core tool checks"
if need python3; then python3 --version; fi
if need git; then git --version; fi
if need java; then java -version; fi
if need gh; then gh --version; fi

if command -v gh >/dev/null 2>&1; then
  if [ -n "${GH_TOKEN:-}" ] || [ -n "${GITHUB_TOKEN:-}" ]; then
    gh auth status --hostname github.com || warn "gh auth status failed"
    gh repo view "$GH_REPO" --json nameWithOwner,viewerPermission,isPrivate,url || warn "gh repo view failed"
  else
    warn "GH_TOKEN/GITHUB_TOKEN absent; gh authenticated checks skipped"
  fi
fi

git ls-remote origin HEAD || warn "git ls-remote origin HEAD failed"

if command -v sdkmanager >/dev/null 2>&1; then sdkmanager --version; else warn "sdkmanager missing"; fi
if command -v aapt2 >/dev/null 2>&1; then aapt2 version; else warn "aapt2 missing"; fi
if command -v apksigner >/dev/null 2>&1; then apksigner --version || true; else warn "apksigner missing"; fi
if command -v jadx >/dev/null 2>&1; then jadx --version; else warn "jadx missing"; fi

[ -f tools/readability/01_inventory_symbols.py ] || warn "missing tools/readability/01_inventory_symbols.py"
[ -f tools/readability/02_generate_mapping_candidates.py ] || warn "missing tools/readability/02_generate_mapping_candidates.py"
[ -f scripts/08_verify_vendor_tokens.sh ] || warn "missing scripts/08_verify_vendor_tokens.sh"
[ -x tools/readability/06_diff_size_guard.sh ] || warn "missing/non-executable tools/readability/06_diff_size_guard.sh"

log "Verification complete"
