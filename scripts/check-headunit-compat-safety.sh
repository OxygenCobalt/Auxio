#!/usr/bin/env bash
set -euo pipefail

allowed_topway_main='app/src/main/java/org/oxycblt/auxio/headunit/topway/'
allowed_topway_test='app/src/test/java/org/oxycblt/auxio/headunit/topway/'
manifest_path='app/src/main/AndroidManifest.xml'
topway_manifest_path='app/src/topwayTwMusic/AndroidManifest.xml'

search_matches() {
  local pattern="$1"
  shift
  if command -v rg >/dev/null 2>&1; then
    rg -n -H --no-messages "${pattern}" "$@" || true
  else
    grep -RInE "${pattern}" "$@" 2>/dev/null || true
  fi
}

product_sources=()
for path in app/src/main app/src/test app/src/androidTest musikr/src; do
  [ -e "${path}" ] && product_sources+=("${path}")
done

product_code_sources=()
for path in app/src/main/java app/src/test app/src/androidTest musikr/src; do
  [ -e "${path}" ] && product_code_sources+=("${path}")
done

identity_files=()
for path in app/build.gradle "${manifest_path}" musikr/build.gradle settings.gradle; do
  [ -f "${path}" ] && identity_files+=("${path}")
done

allowed_topway_identity_files=(
  'app/build.gradle'
  'app/src/topwayTwMusic/AndroidManifest.xml'
  'app/src/topwayTwMusic/res/values/donottranslate.xml'
  'app/src/topwayTwMusicDebug/res/values/donottranslate.xml'
)

identity_scan_files=("${identity_files[@]}")
for path in "${allowed_topway_identity_files[@]}"; do
  [ -f "${path}" ] && identity_scan_files+=("${path}")
done

if [ "${#product_sources[@]}" -eq 0 ] && [ "${#identity_files[@]}" -eq 0 ]; then
  echo "No product sources found; skipping headunit compat safety checks."
  exit 0
fi

forbidden_hits="$(search_matches 'android\.tw\.john|com\.tw\.service\.xt|ITWCommandAidl|cn\.cardoor\.libs\.media\.RemoteMediaService|android:sharedUserId=|android\.uid\.system' "${product_sources[@]}" "${identity_files[@]}")"
if [ -n "${forbidden_hits}" ]; then
  echo "${forbidden_hits}" >&2
  echo "Forbidden private/vendor hooks found in product code" >&2
  exit 1
fi

impersonation_hits="$(search_matches 'package="com\\.tw\\.music"|applicationId[[:space:]]+"com\\.tw\\.music"|namespace[[:space:]]+"com\\.tw\\.music"' "${identity_scan_files[@]}")"
if [ -n "${impersonation_hits}" ]; then
  while IFS= read -r line; do
    [ -z "${line}" ] && continue
    path="${line%%:*}"
    allowed="false"
    for allowed_path in "${allowed_topway_identity_files[@]}"; do
      if [ "${path}" = "${allowed_path}" ]; then
        allowed="true"
        break
      fi
    done
    if [ "${allowed}" != "true" ]; then
      echo "${line}" >&2
      echo "Package identity com.tw.music is only allowed in the isolated topwayTwMusic flavour" >&2
      exit 1
    fi
  done <<< "${impersonation_hits}"
fi

vendor_hits="$(search_matches 'com\\.tw\\.[A-Za-z0-9_.]+|com\\.android\\.launcher\\.widget_music_progress' "${product_code_sources[@]}")"
if [ -n "${vendor_hits}" ]; then
  while IFS= read -r line; do
    [ -z "${line}" ] && continue
    path="${line%%:*}"
    case "${path}" in
      ${allowed_topway_main}*|${allowed_topway_test}*)
        case "${line}" in
          *"com.tw.music.action.cmd"*|*"com.tw.music.action.prev"*|*"com.tw.music.action.next"*|*"com.tw.music.action.pp"*|*"com.tw.music.info"*|*"com.tw.launcher.music_progress_duration"*|*"com.android.launcher.widget_music_progress"*) ;;
          *)
            echo "${line}" >&2
            echo "Unexpected vendor string in isolated Topway bridge/test path" >&2
            exit 1
            ;;
        esac
        ;;
      *)
        echo "${line}" >&2
        echo "Vendor strings must stay in the isolated Topway bridge/test paths" >&2
        exit 1
        ;;
    esac
  done <<< "${vendor_hits}"
fi

manifest_scan_paths=()
[ -f "${manifest_path}" ] && manifest_scan_paths+=("${manifest_path}")
[ -f "${topway_manifest_path}" ] && manifest_scan_paths+=("${topway_manifest_path}")
if [ "${#manifest_scan_paths[@]}" -gt 0 ]; then
  manifest_tw_hits="$(search_matches 'com\\.tw\\.[A-Za-z0-9_.]+' "${manifest_scan_paths[@]}")"
  if [ -n "${manifest_tw_hits}" ]; then
    while IFS= read -r line; do
      [ -z "${line}" ] && continue
      case "${line}" in
        *"com.tw.music.action.cmd"*|*"com.tw.music.action.prev"*|*"com.tw.music.action.next"*|*"com.tw.music.action.pp"*|*"com.tw.music.MusicActivity"*|*"com.tw.music"*|*"com.tw.media"*) ;;
        *)
          echo "${line}" >&2
          echo "Manifest com.tw.* entries must be limited to approved Topway bridge actions or the isolated topwayTwMusic alias" >&2
          exit 1
          ;;
      esac
    done <<< "${manifest_tw_hits}"
  fi
fi

echo "headunit compat safety checks passed"
