#!/usr/bin/env bash
set -euo pipefail

allowed_topway_main='app/src/main/java/org/oxycblt/auxio/headunit/topway/'
allowed_topway_test='app/src/test/java/org/oxycblt/auxio/headunit/topway/'
allowed_topway_flavour='app/src/topwayTwMusic/java/com/tw/music/'
manifest_path='app/src/main/AndroidManifest.xml'
topway_flavour_manifest='app/src/topwayTwMusic/AndroidManifest.xml'

search_matches() {
  local pattern="$1"
  shift
  if [ "$#" -eq 0 ]; then
    return 0
  fi
  if command -v rg >/dev/null 2>&1; then
    rg -n -H --no-messages "${pattern}" "$@" || true
  else
    grep -RInE "${pattern}" "$@" 2>/dev/null || true
  fi
}

product_sources=()
for path in app/src/main app/src/topwayTwMusic app/src/test app/src/androidTest musikr/src; do
  [ -e "${path}" ] && product_sources+=("${path}")
done

product_code_sources=()
for path in app/src/main/java app/src/topwayTwMusic/java app/src/test app/src/androidTest musikr/src; do
  [ -e "${path}" ] && product_code_sources+=("${path}")
done

identity_files=()
for path in app/build.gradle "${manifest_path}" "${topway_flavour_manifest}" app/src/topwayTwMusic/res/values/donottranslate.xml app/src/topwayTwMusicDebug/res/values/donottranslate.xml musikr/build.gradle settings.gradle; do
  [ -f "${path}" ] && identity_files+=("${path}")
done

if [ "${#product_sources[@]}" -eq 0 ] && [ "${#identity_files[@]}" -eq 0 ]; then
  echo "No product sources found; skipping headunit compat safety checks."
  exit 0
fi

# Private/vendor implementation hooks are forbidden in product code. Cardoor remote media
# services are observed in DoFun but must not be faked without a proven binder/AIDL protocol.
forbidden_hits="$(search_matches 'android\\.tw\\.john|com\\.tw\\.service\\.xt|ITWCommandAidl|ITWCommandCallbackAidl|android:sharedUserId=|android\\.uid\\.system|cn\\.cardoor\\.libs\\.media\\.RemoteMediaService|cn\\.cardoor\\.libs\\.media\\.impl\\.MediaSourceService|cn\\.cardoor\\.basic\\.media\\.NotifyService' "${product_sources[@]}" "${identity_files[@]}")"
if [ -n "${forbidden_hits}" ]; then
  echo "${forbidden_hits}" >&2
  echo "Forbidden private/vendor hooks found in product code" >&2
  exit 1
fi

# Standard package identity must not be replaced. A dedicated topwayTwMusic flavour is
# allowed to install as com.tw.music because DoFun Variety uses fixed stock music matching.
identity_hits="$(search_matches 'package="com\\.tw\\.music"|applicationId[[:space:]]+"com\\.tw\\.music"|namespace[[:space:]]+"com\\.tw\\.music"' "${identity_files[@]}")"
if [ -n "${identity_hits}" ]; then
  while IFS= read -r line; do
    [ -z "${line}" ] && continue
    path="${line%%:*}"
    case "${path}" in
      app/build.gradle)
        if grep -Fq 'topwayTwMusic' app/build.gradle && grep -Fq 'applicationId "com.tw.music"' app/build.gradle; then
          continue
        fi
        ;;
      app/src/topwayTwMusic/*|app/src/topwayTwMusicDebug/*)
        continue
        ;;
    esac
    echo "${line}" >&2
    echo "Unexpected package identity impersonation outside the dedicated Topway/DoFun flavour" >&2
    exit 1
  done <<< "${identity_hits}"
fi

vendor_hits="$(search_matches 'com\\.tw\\.[A-Za-z0-9_.]+|com\\.android\\.launcher\\.widget_music_progress' "${product_code_sources[@]}")"
if [ -n "${vendor_hits}" ]; then
  while IFS= read -r line; do
    [ -z "${line}" ] && continue
    path="${line%%:*}"
    case "${path}" in
      ${allowed_topway_main}*|${allowed_topway_test}*|${allowed_topway_flavour}*)
        case "${line}" in
          *"com.tw.music.action.cmd"*|*"com.tw.music.action.prev"*|*"com.tw.music.action.next"*|*"com.tw.music.action.pp"*|*"com.tw.music.info"*|*"com.tw.launcher.music_progress_duration"*|*"com.android.launcher.widget_music_progress"*|*"com.tw.music.MusicActivity"*) ;;
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

if [ -f "${manifest_path}" ]; then
  manifest_tw_hits="$(search_matches 'com\\.tw\\.[A-Za-z0-9_.]+' "${manifest_path}")"
  if [ -n "${manifest_tw_hits}" ]; then
    while IFS= read -r line; do
      [ -z "${line}" ] && continue
      case "${line}" in
        *"com.tw.music.action.cmd"*|*"com.tw.music.action.prev"*|*"com.tw.music.action.next"*|*"com.tw.music.action.pp"*) ;;
        *)
          echo "${line}" >&2
          echo "Base manifest com.tw.* intent filters must be limited to the approved Topway actions" >&2
          exit 1
          ;;
      esac
    done <<< "${manifest_tw_hits}"
  fi
fi

if [ -f "${topway_flavour_manifest}" ]; then
  require_topway_identity() {
    local pattern="$1"
    local label="$2"
    if ! grep -Fq "${pattern}" "${topway_flavour_manifest}"; then
      echo "Missing ${label} in ${topway_flavour_manifest}: ${pattern}" >&2
      exit 1
    fi
  }
  require_topway_identity 'com.tw.music.MusicActivity' 'Topway activity alias'
  require_topway_identity 'org.oxycblt.auxio.MainActivity' 'Auxio alias target'
  require_topway_identity 'com.tw.music.MusicService' 'Topway MusicService fallback'
  require_topway_identity 'com.tw.music.view.MusicWidgetProvider' 'Topway MusicWidgetProvider fallback'
  require_topway_identity 'android.intent.action.MAIN' 'MAIN action'
  require_topway_identity 'android.intent.action.MUSIC_PLAYER' 'MUSIC_PLAYER action'
  require_topway_identity 'android.intent.category.LAUNCHER' 'LAUNCHER category'
  require_topway_identity 'android.intent.category.DEFAULT' 'DEFAULT category'
  require_topway_identity 'android.intent.category.APP_MUSIC' 'APP_MUSIC category'
fi

echo "headunit compat safety checks passed"
