#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${repo_root}"

failures=0
fail() { printf 'ERROR: %s\n' "$*" >&2; failures=$((failures + 1)); }
pass() { printf 'OK: %s\n' "$*"; }

require_file() {
  local file="$1"
  if [ -f "$file" ]; then pass "found $file"; else fail "missing $file"; fi
}

require_contains() {
  local file="$1"
  local pattern="$2"
  local desc="$3"
  if [ ! -f "$file" ]; then
    fail "missing ${desc}: ${file}"
    return
  fi
  if grep -Fq -- "$pattern" "$file"; then
    pass "${desc}: ${pattern}"
  else
    fail "${desc} missing: ${pattern} (${file})"
  fi
}

require_file docs/DOFUN_VARIETY_COMPATIBILITY.md
require_file docs/TS18_APK_REFERENCE.md
require_file docs/reference/ts18-apk/README.md
require_file docs/reference/ts18-apk/reference-contracts.json
require_file docs/reference/ts18-apk/dofun-variety/apps_match_config.music-excerpts.json
require_file docs/reference/ts18-apk/dofun-variety/apps_config.music-excerpts.json
require_file docs/reference/ts18-apk/dofun-variety/manifest.string-hits.txt
require_file docs/reference/ts18-apk/twmusic/manifest.string-hits.txt
require_file docs/reference/ts18-apk/twmusic/classes.string-hits.txt

require_contains docs/reference/ts18-apk/reference-contracts.json '"package": "com.dofun.variety"' 'DoFun package reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"package_name": "com.tw.music"' 'DoFun music hotseat package reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"class_name": "com.tw.music.MusicActivity"' 'DoFun music activity reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"com.tw.music.info"' 'Topway metadata broadcast reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"com.tw.launcher.music_progress_duration"' 'Topway progress broadcast reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"com.tw.music.action.pp"' 'Topway play/pause command reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"cn.cardoor.libs.media.RemoteMediaService"' 'Cardoor remote service evidence reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"android.tw.john.TWUtil"' 'forbidden TWUtil reference'
require_contains docs/reference/ts18-apk/reference-contracts.json '"com.tw.service.xt.aidl.ITWCommandAidl"' 'forbidden ITWCommandAidl reference'

require_contains AGENTS.md 'DoFun Variety / stock twmusic compatibility authority' 'agent DoFun/twmusic authority section'
require_contains AGENTS.md "only a dedicated, clearly named Topway/DoFun compatibility variant may install as \`com.tw.music\`" "agent package-identity exception"
require_contains .github/copilot-instructions.md 'DoFun Variety / stock twmusic compatibility authority' 'Copilot DoFun/twmusic authority section'
require_contains docs/README.md 'DoFun Variety / TS18 APK reference baseline' 'docs index reference section'

if (( failures > 0 )); then
  printf 'Result: FAIL (%d issue(s))\n' "$failures" >&2
  exit 1
fi

printf 'Result: PASS\n'
