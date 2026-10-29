#!/usr/bin/env bash
# Runs clang-tidy (the exact copy bundled with the project's pinned NDK, so checks match
# what the real compiler sees) over one native module's own source files.
#
# Usage: tools/run-clang-tidy.sh <module-dir> [extra clang-tidy args...]
#   tools/run-clang-tidy.sh libusbdac
#   tools/run-clang-tidy.sh musikr
#
# Requires a prior debug native build of that module (so a compile_commands.json exists
# under <module-dir>/.cxx/Debug/*/arm64-v8a/) -- e.g. `./gradlew :libusbdac:externalNativeBuildDebug`.
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <module-dir> [extra clang-tidy args...]" >&2
  exit 1
fi

module="$1"
shift

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
module_dir="$repo_root/$module"

if [[ ! -d "$module_dir" ]]; then
  echo "error: no such module directory: $module_dir" >&2
  exit 1
fi

ndk_dir="${ANDROID_NDK_HOME:-$HOME/Android/Sdk/ndk/28.2.13676358}"
clang_tidy="$ndk_dir/toolchains/llvm/prebuilt/linux-x86_64/bin/clang-tidy"
if [[ ! -x "$clang_tidy" ]]; then
  echo "error: clang-tidy not found at $clang_tidy (set ANDROID_NDK_HOME?)" >&2
  exit 1
fi

# AGP hashes the CMake-args fingerprint into an unpredictable directory name under
# .cxx/Debug/<hash>/<abi>/ -- glob for it and take the most recently built one.
compile_db="$(find "$module_dir/.cxx/Debug" -path '*/arm64-v8a/compile_commands.json' 2>/dev/null \
  -printf '%T@ %p\n' | sort -rn | head -1 | cut -d' ' -f2-)"
if [[ -z "$compile_db" ]]; then
  echo "error: no compile_commands.json found under $module_dir/.cxx/Debug/*/arm64-v8a/" >&2
  echo "       run a debug native build first, e.g.:" >&2
  echo "       ./gradlew :$module:externalNativeBuildDebug" >&2
  exit 1
fi
compile_db_dir="$(dirname "$compile_db")"

mapfile -t sources < <(python3 -c "
import json
for e in json.load(open('$compile_db')):
    print(e['file'])
")

echo "Analyzing ${#sources[@]} file(s) in $module against $compile_db_dir"
"$clang_tidy" -p "$compile_db_dir" "${sources[@]}" "$@"
