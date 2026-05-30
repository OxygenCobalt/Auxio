#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${repo_root}"

failures=0
warn() { printf 'WARN: %s\n' "$*" >&2; }
fail() { printf 'ERROR: %s\n' "$*" >&2; failures=$((failures + 1)); }
pass() { printf 'OK: %s\n' "$*"; }

require_file_contains() {
  local file="$1"
  local pattern="$2"
  local desc="$3"
  if [[ ! -f "$file" ]]; then
    fail "missing ${desc}: ${file}"
    return
  fi
  if grep -Fq -- "$pattern" "$file"; then
    pass "${desc} contains ${pattern}"
  else
    fail "${desc} does not contain ${pattern}: ${file}"
  fi
}

printf 'Checking source-level DoFun/Topway compatibility expectations...\n\n'

require_file_contains "app/build.gradle" "topwayTwMusic" "Gradle product flavour"
require_file_contains "app/build.gradle" "com.tw.music" "Gradle Topway applicationId"

# Locate likely flavour manifest.
manifest_candidates=(
  "app/src/topwayTwMusic/AndroidManifest.xml"
  "app/src/topwayTwmusic/AndroidManifest.xml"
  "app/src/twmusic/AndroidManifest.xml"
)
flavour_manifest=""
for candidate in "${manifest_candidates[@]}"; do
  if [[ -f "$candidate" ]]; then
    flavour_manifest="$candidate"
    break
  fi
done

if [[ -z "$flavour_manifest" ]]; then
  fail "missing topwayTwMusic flavour AndroidManifest.xml under app/src/"
else
  pass "found flavour manifest: ${flavour_manifest}"
  require_file_contains "$flavour_manifest" "com.tw.music.MusicActivity" "Topway activity alias"
  require_file_contains "$flavour_manifest" "org.oxycblt.auxio.MainActivity" "Topway alias target"
  require_file_contains "$flavour_manifest" "android.intent.action.MUSIC_PLAYER" "Topway alias music action"
  require_file_contains "$flavour_manifest" "android.intent.category.APP_MUSIC" "Topway alias app music category"
fi

# Check source-set resources if present.
if grep -R "com.tw.music.image.CoverProvider\|com.tw.music.debug.image.CoverProvider" app/src 2>/dev/null | grep -q .; then
  pass "found Topway CoverProvider authority override"
else
  fail "no Topway CoverProvider authority override found under app/src"
fi

# Existing base requirements.
require_file_contains "app/src/main/AndroidManifest.xml" "android.media.browse.MediaBrowserService" "base media browser service"
require_file_contains "app/src/main/AndroidManifest.xml" "com.tw.music.action.cmd" "base Topway command receiver"
require_file_contains "app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt" "com.tw.music.info" "Topway metadata broadcast contract"
require_file_contains "app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt" "com.tw.launcher.music_progress_duration" "Topway progress broadcast contract"

printf '\nLooking for merged manifests, if any...\n'
mapfile -t merged_manifests < <(find app/build/intermediates -path '*topway*AndroidManifest.xml' -o -path '*Topway*AndroidManifest.xml' 2>/dev/null | sort || true)
if (( ${#merged_manifests[@]} == 0 )); then
  warn "no merged Topway manifests found; run ./gradlew :app:processTopwayTwMusicDebugMainManifest or assemble the variant first"
else
  for mf in "${merged_manifests[@]}"; do
    printf 'Inspecting %s\n' "$mf"
    grep -Fq "com.tw.music.MusicActivity" "$mf" && pass "merged manifest has com.tw.music.MusicActivity" || fail "merged manifest lacks com.tw.music.MusicActivity: $mf"
    grep -Fq "android.media.browse.MediaBrowserService" "$mf" && pass "merged manifest has MediaBrowserService" || fail "merged manifest lacks MediaBrowserService: $mf"
  done
fi

printf '\nLooking for built Topway APKs, if any...\n'
mapfile -t apk_candidates < <(find app/build/outputs/apk -iname '*topway*/*.apk' -o -iname '*topway*.apk' 2>/dev/null | sort || true)
if (( ${#apk_candidates[@]} == 0 )); then
  warn "no built Topway APK found; run ./gradlew :app:assembleTopwayTwMusicDebug or release equivalent first"
else
  printf '%s\n' "${apk_candidates[@]}"
fi

printf '\nResult: '
if (( failures == 0 )); then
  printf 'PASS\n'
else
  printf 'FAIL (%d issue(s))\n' "$failures"
  exit 1
fi
