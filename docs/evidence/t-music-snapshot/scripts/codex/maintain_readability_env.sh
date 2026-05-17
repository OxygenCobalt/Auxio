#!/usr/bin/env bash
# Lightweight Codex maintenance for cached t-music environments.
#
# This script never runs apt, never downloads large tools, and never regenerates
# reports unless RUN_READABILITY_REFRESH=1.

set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd -P)"
ROOT="$(cd -- "$SCRIPT_DIR/../.." >/dev/null 2>&1 && pwd -P)"
cd "$ROOT"

GH_REPO="${GH_REPO:-cbkii/t-music}"
ANDROID_BUILD_TOOLS_VERSION="${ANDROID_BUILD_TOOLS_VERSION:-35.0.0}"
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ROOT/.tools/android-sdk}"
ANDROID_HOME="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
JADX_VERSION="${JADX_VERSION:-1.5.5}"
JADX_HOME="${JADX_HOME:-$ROOT/.tools/jadx-${JADX_VERSION}}"

RUN_READABILITY_REFRESH="${RUN_READABILITY_REFRESH:-0}"
RUN_VENDOR_TOKEN_GUARD="${RUN_VENDOR_TOKEN_GUARD:-0}"
RUN_DIFF_SIZE_GUARD="${RUN_DIFF_SIZE_GUARD:-0}"
REQUIRE_GH_AUTH="${REQUIRE_GH_AUTH:-0}"
REQUIRE_ANDROID_TOOLS="${REQUIRE_ANDROID_TOOLS:-0}"
REQUIRE_JADX="${REQUIRE_JADX:-0}"

export GH_REPO ANDROID_SDK_ROOT ANDROID_HOME JADX_HOME
export GH_PROMPT_DISABLED="${GH_PROMPT_DISABLED:-1}"
export GH_NO_UPDATE_NOTIFIER="${GH_NO_UPDATE_NOTIFIER:-1}"
export GH_NO_EXTENSION_UPDATE_NOTIFIER="${GH_NO_EXTENSION_UPDATE_NOTIFIER:-1}"

LOCAL_BIN="$HOME/.local/bin"
mkdir -p "$LOCAL_BIN"
export PATH="$LOCAL_BIN:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION:$JADX_HOME/bin:$PATH"

log() { printf '\n[maintain] %s\n' "$*"; }
warn() { printf '\n[maintain:WARN] %s\n' "$*" >&2; }
fail() { printf '\n[maintain:ERR] %s\n' "$*" >&2; exit 1; }
have() { command -v "$1" >/dev/null 2>&1; }
is_enabled() { case "${1:-0}" in 1|true|TRUE|yes|YES|on|ON) return 0 ;; *) return 1 ;; esac; }

write_credential_helper() {
  cat > "$LOCAL_BIN/git-credential-gh-token" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail
case "${1:-}" in
  get)
    while IFS= read -r line; do [ -z "$line" ] && break; done
    token="${GH_TOKEN:-${GITHUB_TOKEN:-}}"
    if [ -n "$token" ]; then
      printf 'username=x-access-token\n'
      printf 'password=%s\n' "$token"
    fi
    ;;
  store|erase) exit 0 ;;
  *) exit 0 ;;
esac
EOF
  chmod 700 "$LOCAL_BIN/git-credential-gh-token"
}

refresh_git_auth() {
  log "Refreshing runtime GitHub auth helper"
  write_credential_helper
  git config --local credential.https://github.com.helper "$LOCAL_BIN/git-credential-gh-token" || true
  git config --global --add safe.directory "$ROOT" >/dev/null 2>&1 || true

  if [ -n "${GH_TOKEN:-${GITHUB_TOKEN:-}}" ]; then
    if have gh; then
      export GITHUB_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-}}"
      gh auth status --hostname github.com >/dev/null 2>&1 || warn "gh auth status failed"
    else
      warn "GH token present but gh missing"
    fi
  elif is_enabled "$REQUIRE_GH_AUTH"; then
    fail "REQUIRE_GH_AUTH=1 but no GH_TOKEN/GITHUB_TOKEN is set"
  else
    warn "No GH_TOKEN/GITHUB_TOKEN present; continuing"
  fi
}

write_shell_hints() {
  cat > "$HOME/.codex-t-music-env" <<EOF
# t-music Codex environment hints. No secrets.
export GH_REPO='${GH_REPO}'
export GH_PROMPT_DISABLED='${GH_PROMPT_DISABLED}'
export GH_NO_UPDATE_NOTIFIER='${GH_NO_UPDATE_NOTIFIER}'
export GH_NO_EXTENSION_UPDATE_NOTIFIER='${GH_NO_EXTENSION_UPDATE_NOTIFIER}'
export ANDROID_SDK_ROOT='${ANDROID_SDK_ROOT}'
export ANDROID_HOME='${ANDROID_HOME}'
export ANDROID_BUILD_TOOLS_VERSION='${ANDROID_BUILD_TOOLS_VERSION}'
export JADX_HOME='${JADX_HOME}'
export PATH="\$HOME/.local/bin:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$ANDROID_SDK_ROOT/build-tools/${ANDROID_BUILD_TOOLS_VERSION}:\$JADX_HOME/bin:\$PATH"
EOF
  touch "$HOME/.bashrc"
  grep -Fq 'source "$HOME/.codex-t-music-env"' "$HOME/.bashrc" ||
    printf '\n# t-music Codex environment\nsource "$HOME/.codex-t-music-env"\n' >> "$HOME/.bashrc"
}

verify_scripts() {
  log "Syntax-checking scripts"
  bash -n scripts/codex/setup_readability_env.sh
  bash -n scripts/codex/maintain_readability_env.sh
}

verify_commands() {
  log "Checking lightweight commands"
  have git || fail "git required"
  have python3 || fail "python3 required"
  have rg || warn "rg missing"
  have jq || warn "jq missing"
  have gh || warn "gh missing"

  if is_enabled "$REQUIRE_ANDROID_TOOLS"; then
    [ -x "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/aapt2" ] || fail "aapt2 required but missing"
    [ -x "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/apksigner" ] || fail "apksigner required but missing"
  fi

  if is_enabled "$REQUIRE_JADX"; then
    if have jadx; then
      jadx --version >/dev/null
    elif [ -x "$JADX_HOME/bin/jadx" ]; then
      "$JADX_HOME/bin/jadx" --version >/dev/null
    else
      fail "JADX required but missing"
    fi
  fi
}

verify_repo_helpers() {
  log "Checking repo helper scripts"
  [ -f tools/readability/04_high_impact_method_candidates.py ] || fail "missing high-impact method tool"
  [ -f tools/readability/05_tail_classification.py ] || fail "missing tail classification tool"
  [ -f scripts/08_verify_vendor_tokens.sh ] || fail "missing vendor token guard"
  [ -f tools/readability/06_diff_size_guard.sh ] || fail "missing diff size guard"
}

optional_checks() {
  if is_enabled "$RUN_READABILITY_REFRESH"; then
    log "Refreshing readability reports"
    python3 tools/readability/04_high_impact_method_candidates.py
    python3 tools/readability/05_tail_classification.py
  else
    log "Skipping report refresh. Set RUN_READABILITY_REFRESH=1 to enable."
  fi

  if is_enabled "$RUN_VENDOR_TOKEN_GUARD"; then
    bash scripts/08_verify_vendor_tokens.sh
  else
    log "Skipping vendor guard. Set RUN_VENDOR_TOKEN_GUARD=1 to enable."
  fi

  if is_enabled "$RUN_DIFF_SIZE_GUARD"; then
    bash tools/readability/06_diff_size_guard.sh
  else
    log "Skipping diff guard. Set RUN_DIFF_SIZE_GUARD=1 to enable."
  fi
}

main() {
  write_shell_hints
  refresh_git_auth
  verify_scripts
  verify_commands
  verify_repo_helpers
  optional_checks
  log "Maintenance complete"
  git status --short --branch || true
}

main "$@"
