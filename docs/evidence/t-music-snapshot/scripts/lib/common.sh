#!/usr/bin/env bash
# Shared helpers for repo scripts — source this, do not execute directly.

# Resolve an aapt2 candidate to a real executable.
# In Termux, the aapt2 in PATH is often a shell wrapper; we need the ELF binary.
_resolve_aapt2() {
  local c="$1" resolved first libexec
  [ -n "$c" ] || return 1
  [ -e "$c" ] || return 1
  resolved="$c"
  command -v readlink >/dev/null 2>&1 \
    && resolved=$(readlink -f "$c" 2>/dev/null || printf '%s' "$c")
  if [ -f "$resolved" ]; then
    first=$(LC_ALL=C head -n 1 "$resolved" 2>/dev/null || true)
    case "$first" in
      '#!'*)
        libexec="$(cd "$(dirname "$resolved")/.." 2>/dev/null && pwd)/libexec/aapt2"
        [ -x "$libexec" ] && resolved="$libexec"
        ;;
    esac
  fi
  [ -x "$resolved" ] || return 1
  printf '%s\n' "$resolved"
}

find_aapt2_for_repo() {
  local root="$1" candidates=() c resolved
  [ -n "${AAPT2:-}" ] && candidates+=("$AAPT2")
  [ -x "${PREFIX:-}/libexec/aapt2" ] && candidates+=("${PREFIX:-}/libexec/aapt2")
  command -v aapt2 >/dev/null 2>&1 && candidates+=("$(command -v aapt2)")
  local sdk_roots=()
  [ -n "${ANDROID_SDK_ROOT:-}" ] && sdk_roots+=("$ANDROID_SDK_ROOT")
  [ -n "${ANDROID_HOME:-}" ]     && sdk_roots+=("$ANDROID_HOME")
  sdk_roots+=("$root/.tools/android-sdk" "$HOME/Android/Sdk" "$HOME/android-sdk")
  for r in "${sdk_roots[@]}"; do
    [ -d "$r/build-tools" ] || continue
    while IFS= read -r -d '' c; do
      candidates+=("$c")
    done < <(find "$r/build-tools" -mindepth 2 -maxdepth 2 \
              -type f -name aapt2 -perm -111 -print0 2>/dev/null | sort -z)
  done
  for c in "${candidates[@]}"; do
    resolved=$(_resolve_aapt2 "$c" 2>/dev/null || true)
    [ -n "$resolved" ] && { printf '%s\n' "$resolved"; return 0; }
  done
  return 1
}

find_build_tools_dir_for_repo() {
  local aapt2
  aapt2=$(find_aapt2_for_repo "$1") || return 1
  dirname "$aapt2"
}
