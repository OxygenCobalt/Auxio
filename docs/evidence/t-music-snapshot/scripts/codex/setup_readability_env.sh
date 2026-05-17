#!/usr/bin/env bash
# Fast Codex setup for t-music.
#
# Default mode is intentionally minimal:
#   - no apt-get update/install
#   - no Java install
#   - no Android SDK install
#   - no JADX install
#
# Heavy modes are explicit:
#   INSTALL_SYSTEM_PACKAGES=1
#   INSTALL_JAVA=1
#   INSTALL_JDK=1
#   INSTALL_ANDROID_TOOLS=1
#   INSTALL_PINNED_JADX=1
#
# Recommended Codex setup command:
#   bash scripts/codex/setup_readability_env.sh
#
# Full APK tooling setup only when needed:
#   INSTALL_SYSTEM_PACKAGES=1 INSTALL_JAVA=1 INSTALL_ANDROID_TOOLS=1 INSTALL_PINNED_JADX=1 \
#     bash scripts/codex/setup_readability_env.sh

set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd -P)"
ROOT="$(cd -- "$SCRIPT_DIR/../.." >/dev/null 2>&1 && pwd -P)"
cd "$ROOT"

export DEBIAN_FRONTEND="${DEBIAN_FRONTEND:-noninteractive}"

GH_REPO="${GH_REPO:-cbkii/t-music}"
ANDROID_BUILD_TOOLS_VERSION="${ANDROID_BUILD_TOOLS_VERSION:-35.0.0}"
ANDROID_CMDLINE_TOOLS_ZIP_URL="${ANDROID_CMDLINE_TOOLS_ZIP_URL:-https://dl.google.com/android/repository/commandlinetools-linux-14742923_latest.zip}"
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ROOT/.tools/android-sdk}"
ANDROID_HOME="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"

JADX_VERSION="${JADX_VERSION:-1.5.5}"
JADX_ZIP_URL="${JADX_ZIP_URL:-https://github.com/skylot/jadx/releases/download/v${JADX_VERSION}/jadx-${JADX_VERSION}.zip}"
JADX_HOME="${JADX_HOME:-$ROOT/.tools/jadx-${JADX_VERSION}}"

INSTALL_SYSTEM_PACKAGES="${INSTALL_SYSTEM_PACKAGES:-0}"
INSTALL_GH="${INSTALL_GH:-0}"
INSTALL_JAVA="${INSTALL_JAVA:-0}"
INSTALL_JDK="${INSTALL_JDK:-0}"
INSTALL_ANDROID_TOOLS="${INSTALL_ANDROID_TOOLS:-0}"
INSTALL_PINNED_JADX="${INSTALL_PINNED_JADX:-0}"
RUN_REPO_BOOTSTRAP="${RUN_REPO_BOOTSTRAP:-0}"
VERIFY_ONLY="${VERIFY_ONLY:-0}"
REQUIRE_GH_AUTH="${REQUIRE_GH_AUTH:-0}"

export GH_REPO ANDROID_SDK_ROOT ANDROID_HOME JADX_HOME
export GH_PROMPT_DISABLED="${GH_PROMPT_DISABLED:-1}"
export GH_NO_UPDATE_NOTIFIER="${GH_NO_UPDATE_NOTIFIER:-1}"
export GH_NO_EXTENSION_UPDATE_NOTIFIER="${GH_NO_EXTENSION_UPDATE_NOTIFIER:-1}"

LOCAL_BIN="$HOME/.local/bin"
TMP_ROOT="$ROOT/.tmp/codex-setup"
mkdir -p "$LOCAL_BIN" "$TMP_ROOT"

export PATH="$LOCAL_BIN:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION:$JADX_HOME/bin:$PATH"

log() { printf '\n[setup] %s\n' "$*"; }
warn() { printf '\n[setup:WARN] %s\n' "$*" >&2; }
fail() { printf '\n[setup:ERR] %s\n' "$*" >&2; exit 1; }
have() { command -v "$1" >/dev/null 2>&1; }

is_enabled() {
  case "${1:-0}" in
    1|true|TRUE|yes|YES|on|ON) return 0 ;;
    *) return 1 ;;
  esac
}

as_root() {
  if [ "$(id -u)" = "0" ]; then
    "$@"
  elif have sudo; then
    sudo "$@"
  else
    fail "Need root/sudo for: $*"
  fi
}

run_timeout() {
  local seconds="$1"; shift
  if have timeout; then
    timeout "$seconds" "$@"
  else
    "$@"
  fi
}

apt_get_safe() {
  local apt_opts=(
    -o Acquire::Retries=2
    -o Acquire::http::Timeout=20
    -o Acquire::https::Timeout=20
    -o DPkg::Lock::Timeout=30
  )
  run_timeout "${APT_TIMEOUT_SECONDS:-240}" apt-get "${apt_opts[@]}" "$@"
}

install_system_packages() {
  is_enabled "$INSTALL_SYSTEM_PACKAGES" || {
    log "Skipping apt package installation. Set INSTALL_SYSTEM_PACKAGES=1 to enable."
    return 0
  }

  have apt-get || fail "apt-get not available"

  local packages=()
  have git     || packages+=(git)
  have curl    || packages+=(curl)
  have unzip   || packages+=(unzip)
  have zip     || packages+=(zip)
  have jq      || packages+=(jq)
  have rg      || packages+=(ripgrep)
  have file    || packages+=(file)
  have python3 || packages+=(python3)
  packages+=(ca-certificates)

  if is_enabled "$INSTALL_GH" && ! have gh; then
    packages+=(gh)
  fi

  if is_enabled "$INSTALL_JAVA" && ! have java; then
    if is_enabled "$INSTALL_JDK"; then
      packages+=(openjdk-17-jdk-headless)
    else
      packages+=(openjdk-17-jre-headless)
    fi
  fi

  local unique=()
  local p u found
  for p in "${packages[@]}"; do
    found=0
    for u in "${unique[@]:-}"; do
      [ "$u" = "$p" ] && found=1 && break
    done
    [ "$found" = "0" ] && unique+=("$p")
  done

  [ "${#unique[@]}" -gt 0 ] || { log "No apt packages needed."; return 0; }

  log "Installing packages with --no-install-recommends: ${unique[*]}"
  as_root apt_get_safe update -y
  as_root apt_get_safe install -y --no-install-recommends "${unique[@]}"
}

install_gh_if_requested() {
  have gh && return 0
  is_enabled "$INSTALL_GH" || {
    warn "gh is not installed. Set INSTALL_SYSTEM_PACKAGES=1 INSTALL_GH=1 to install."
    return 0
  }
  is_enabled "$INSTALL_SYSTEM_PACKAGES" || fail "INSTALL_GH=1 requires INSTALL_SYSTEM_PACKAGES=1"
  have apt-get || fail "apt-get not available"
  have curl || fail "curl required to install gh repository key"

  log "Installing gh from GitHub CLI apt repo"
  local keyring="/usr/share/keyrings/githubcli-archive-keyring.gpg"
  curl -fsSL --connect-timeout 20 --max-time 120 https://cli.github.com/packages/githubcli-archive-keyring.gpg |
    as_root dd "of=$keyring" >/dev/null
  as_root chmod go+r "$keyring"
  echo "deb [arch=$(dpkg --print-architecture) signed-by=$keyring] https://cli.github.com/packages stable main" |
    as_root tee /etc/apt/sources.list.d/github-cli.list >/dev/null
  as_root apt_get_safe update -y
  as_root apt_get_safe install -y --no-install-recommends gh
}

write_credential_helper() {
  mkdir -p "$LOCAL_BIN"
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

configure_github_auth() {
  log "Configuring GitHub auth helpers"
  write_credential_helper

  git config --local credential.https://github.com.helper "$LOCAL_BIN/git-credential-gh-token" || true
  git config --global --add safe.directory "$ROOT" >/dev/null 2>&1 || true
  git config --local user.name "${GIT_AUTHOR_NAME:-CB via Codex}" || true
  git config --local user.email "${GIT_AUTHOR_EMAIL:-41898282+github-actions[bot]@users.noreply.github.com}" || true

  if git remote get-url origin >/dev/null 2>&1; then
    current="$(git remote get-url origin)"
    case "$current" in
      https://github.com/*|git@github.com:*|ssh://git@github.com/*)
        git remote set-url origin "https://github.com/${GH_REPO}.git"
        ;;
      *) warn "Origin is not a GitHub URL; leaving unchanged: $current" ;;
    esac
  else
    git remote add origin "https://github.com/${GH_REPO}.git"
  fi

  if [ -n "${GH_TOKEN:-${GITHUB_TOKEN:-}}" ]; then
    if have gh; then
      export GITHUB_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-}}"
      gh auth status --hostname github.com >/dev/null 2>&1 ||
        warn "gh auth status failed; git credential helper may still work"
      gh repo view "$GH_REPO" --json nameWithOwner,viewerPermission,isPrivate,url >/dev/null 2>&1 ||
        warn "gh cannot view $GH_REPO with current token"
    else
      warn "GH token is present but gh is not installed"
    fi
    git ls-remote origin HEAD >/dev/null 2>&1 ||
      warn "git ls-remote origin HEAD failed; check token/repo access"
  else
    is_enabled "$REQUIRE_GH_AUTH" && fail "REQUIRE_GH_AUTH=1 but no GH_TOKEN/GITHUB_TOKEN is set"
    warn "No GH_TOKEN/GITHUB_TOKEN present; continuing without authenticated GitHub checks"
  fi
}

link_tool() {
  local name="$1" target="$2"
  [ -x "$target" ] || return 0
  mkdir -p "$LOCAL_BIN"
  ln -sf "$target" "$LOCAL_BIN/$name"
}

install_android_tools() {
  is_enabled "$INSTALL_ANDROID_TOOLS" || {
    log "Skipping Android SDK install. Set INSTALL_ANDROID_TOOLS=1 to enable."
    return 0
  }
  have curl || fail "curl required for Android SDK install"
  have unzip || fail "unzip required for Android SDK install"

  log "Installing Android command-line tools/build-tools"
  mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
  local sdkmanager="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

  if [ ! -x "$sdkmanager" ]; then
    local tmpdir="$TMP_ROOT/android-cmdline-tools"
    rm -rf "$tmpdir"; mkdir -p "$tmpdir"
    curl -fL --retry 2 --retry-delay 2 --connect-timeout 20 --max-time 300 \
      -o "$tmpdir/commandlinetools-linux.zip" "$ANDROID_CMDLINE_TOOLS_ZIP_URL"
    unzip -q "$tmpdir/commandlinetools-linux.zip" -d "$tmpdir"
    rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    mv "$tmpdir/cmdline-tools/"* "$ANDROID_SDK_ROOT/cmdline-tools/latest/"
  fi

  export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION:$PATH"
  yes | "$sdkmanager" --licenses >/dev/null || true
  "$sdkmanager" --install "platform-tools" "build-tools;$ANDROID_BUILD_TOOLS_VERSION"

  link_tool sdkmanager "$sdkmanager"
  link_tool adb "$ANDROID_SDK_ROOT/platform-tools/adb"
  link_tool aapt2 "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/aapt2"
  link_tool aapt "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/aapt"
  link_tool zipalign "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/zipalign"
  link_tool apksigner "$ANDROID_SDK_ROOT/build-tools/$ANDROID_BUILD_TOOLS_VERSION/apksigner"
}

install_jadx() {
  is_enabled "$INSTALL_PINNED_JADX" || {
    log "Skipping JADX install. Set INSTALL_PINNED_JADX=1 to enable."
    return 0
  }
  have java || fail "INSTALL_PINNED_JADX=1 requires Java. Enable INSTALL_SYSTEM_PACKAGES=1 INSTALL_JAVA=1 or preinstall Java."
  have curl || fail "curl required for JADX install"
  have unzip || fail "unzip required for JADX install"

  if [ -x "$JADX_HOME/bin/jadx" ]; then
    log "JADX already installed at $JADX_HOME"
    link_tool jadx "$JADX_HOME/bin/jadx"
    return 0
  fi

  log "Installing JADX $JADX_VERSION"
  local tmpdir="$TMP_ROOT/jadx"
  rm -rf "$tmpdir"; mkdir -p "$tmpdir"

  curl -fL --retry 2 --retry-delay 2 --connect-timeout 20 --max-time 300 \
    -o "$tmpdir/jadx.zip" "$JADX_ZIP_URL"

  if [ -n "${JADX_SHA256:-}" ]; then
    printf '%s  %s\n' "$JADX_SHA256" "$tmpdir/jadx.zip" | sha256sum -c -
  else
    warn "JADX_SHA256 not set; skipping checksum enforcement"
  fi

  rm -rf "$JADX_HOME"
  mkdir -p "$JADX_HOME"
  unzip -q "$tmpdir/jadx.zip" -d "$JADX_HOME"
  chmod +x "$JADX_HOME/bin/jadx" || true
  link_tool jadx "$JADX_HOME/bin/jadx"
  "$JADX_HOME/bin/jadx" --version >/dev/null
}

repo_bootstrap_if_requested() {
  is_enabled "$RUN_REPO_BOOTSTRAP" || {
    log "Skipping repo bootstrap. Set RUN_REPO_BOOTSTRAP=1 to enable."
    return 0
  }
  if [ -f scripts/00_bootstrap_tools.sh ]; then
    bash scripts/00_bootstrap_tools.sh
  else
    warn "No scripts/00_bootstrap_tools.sh found"
  fi
}

write_shell_hints() {
  log "Writing non-secret shell hints"
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
  grep -Fq 'source "$HOME/.codex-t-music-env"' "$HOME/.bashrc" || {
    printf '\n# t-music Codex environment\nsource "$HOME/.codex-t-music-env"\n' >> "$HOME/.bashrc"
  }
}

verify_environment() {
  log "Verifying environment"
  bash -n scripts/codex/setup_readability_env.sh
  bash -n scripts/codex/maintain_readability_env.sh

  have git || fail "git is required"
  have python3 || warn "python3 missing; readability tooling requires it"
  have rg || warn "rg missing; grep fallback may be needed"
  have jq || warn "jq missing; some GH JSON handling may be unavailable"
  have gh || warn "gh missing; PR operations unavailable"
  have java || warn "java missing; OK for readability-only work"

  if have gh && [ -n "${GH_TOKEN:-${GITHUB_TOKEN:-}}" ]; then
    export GITHUB_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-}}"
    gh auth status --hostname github.com >/dev/null 2>&1 || warn "gh auth status failed"
  fi

  [ -f tools/readability/04_high_impact_method_candidates.py ] || warn "missing high-impact method tool"
  [ -f tools/readability/05_tail_classification.py ] || warn "missing tail classification tool"
  [ -f scripts/08_verify_vendor_tokens.sh ] || warn "missing vendor token guard"
  [ -f tools/readability/06_diff_size_guard.sh ] || warn "missing diff size guard"
}

main() {
  if is_enabled "$VERIFY_ONLY"; then
    verify_environment
    exit 0
  fi

  install_system_packages
  install_gh_if_requested
  configure_github_auth
  install_android_tools
  install_jadx
  repo_bootstrap_if_requested
  write_shell_hints
  verify_environment

  log "Setup complete: fast default mode used unless install flags were set"
  git status --short || true
}

main "$@"
