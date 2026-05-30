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

find_merged_manifest() {
  local variant="$1"
  find app/build/intermediates/merged_manifests -path "*${variant}*AndroidManifest.xml" -print 2>/dev/null | sort | head -n 1
}

find_apk() {
  local variant_dir="$1"
  local build_type="$2"
  find "app/build/outputs/apk/${variant_dir}/${build_type}" -maxdepth 1 -type f -name '*.apk' -print 2>/dev/null | sort | head -n 1
}

check_apk_manifest() {
  local apk="$1"
  local expected_package="$2"
  local label="$3"
  local apkanalyzer_bin="${ANDROID_HOME:-}/cmdline-tools/latest/bin/apkanalyzer"
  if [[ ! -x "$apkanalyzer_bin" ]]; then
    apkanalyzer_bin="$(command -v apkanalyzer || true)"
  fi
  if [[ -z "$apkanalyzer_bin" || ! -x "$apkanalyzer_bin" ]]; then
    warn "apkanalyzer not found; skipping binary APK manifest checks for ${label}"
    return
  fi

  local actual_package
  actual_package="$($apkanalyzer_bin manifest application-id "$apk")"
  if [[ "$actual_package" == "$expected_package" ]]; then
    pass "${label} APK application id is ${expected_package}"
  else
    fail "${label} APK application id expected ${expected_package}, got ${actual_package}"
  fi

  local manifest_dump
  manifest_dump="$(mktemp)"
  "$apkanalyzer_bin" manifest print "$apk" > "$manifest_dump"
  if [[ "$label" == topwayTwMusicRelease ]]; then
    grep -Fq 'android:name="com.tw.music.MusicActivity"' "$manifest_dump" && pass "${label} APK manifest has com.tw.music.MusicActivity" || fail "${label} APK manifest lacks com.tw.music.MusicActivity"
    grep -Fq 'android:targetActivity="org.oxycblt.auxio.MainActivity"' "$manifest_dump" && pass "${label} APK alias targets MainActivity" || fail "${label} APK alias target mismatch"
    grep -Fq 'android:name="android.intent.action.MAIN"' "$manifest_dump" && pass "${label} APK alias has MAIN action" || fail "${label} APK alias lacks MAIN action"
    grep -Fq 'android:name="android.intent.action.MUSIC_PLAYER"' "$manifest_dump" && pass "${label} APK alias has MUSIC_PLAYER action" || fail "${label} APK alias lacks MUSIC_PLAYER action"
    grep -Fq 'android:name="android.intent.category.DEFAULT"' "$manifest_dump" && pass "${label} APK alias has DEFAULT category" || fail "${label} APK alias lacks DEFAULT category"
    grep -Fq 'android:name="android.intent.category.LAUNCHER"' "$manifest_dump" && pass "${label} APK alias has LAUNCHER category" || fail "${label} APK alias lacks LAUNCHER category"
    grep -Fq 'android:name="android.intent.category.APP_MUSIC"' "$manifest_dump" && pass "${label} APK alias has APP_MUSIC category" || fail "${label} APK alias lacks APP_MUSIC category"
    grep -Fq 'android:name="android.media.browse.MediaBrowserService"' "$manifest_dump" && pass "${label} APK manifest has MediaBrowserService" || fail "${label} APK manifest lacks MediaBrowserService"
    grep -Fq 'android:authorities="com.tw.music.image.CoverProvider"' "$manifest_dump" && pass "${label} APK manifest has Topway CoverProvider authority" || fail "${label} APK manifest lacks Topway CoverProvider authority"
  fi
  rm -f "$manifest_dump"
}

printf 'Checking source-level DoFun/Topway compatibility expectations...\n\n'

require_file_contains "app/build.gradle" "standard" "Gradle standard product flavour"
require_file_contains "app/build.gradle" "topwayTwMusic" "Gradle product flavour"
require_file_contains "app/build.gradle" "applicationId namespace" "Gradle standard applicationId"
require_file_contains "app/build.gradle" "applicationId \"com.tw.music\"" "Gradle Topway applicationId"

flavour_manifest="app/src/topwayTwMusic/AndroidManifest.xml"
if [[ ! -f "$flavour_manifest" ]]; then
  fail "missing topwayTwMusic flavour AndroidManifest.xml under app/src/"
else
  pass "found flavour manifest: ${flavour_manifest}"
  require_file_contains "$flavour_manifest" "com.tw.music.MusicActivity" "Topway activity alias"
  require_file_contains "$flavour_manifest" "org.oxycblt.auxio.MainActivity" "Topway alias target"
  require_file_contains "$flavour_manifest" "android.intent.action.MAIN" "Topway alias main action"
  require_file_contains "$flavour_manifest" "android.intent.action.MUSIC_PLAYER" "Topway alias music action"
  require_file_contains "$flavour_manifest" "android.intent.category.LAUNCHER" "Topway alias launcher category"
  require_file_contains "$flavour_manifest" "android.intent.category.DEFAULT" "Topway alias default category"
  require_file_contains "$flavour_manifest" "android.intent.category.APP_MUSIC" "Topway alias app music category"
fi

require_file_contains "app/src/main/AndroidManifest.xml" '${applicationId}.image.CoverProvider' "manifest applicationId-scoped CoverProvider authority"
require_file_contains "app/src/main/AndroidManifest.xml" "android.media.browse.MediaBrowserService" "base media browser service"
require_file_contains "app/src/main/AndroidManifest.xml" ".headunit.topway.TopwayMusicBridgeReceiver" "base Topway command receiver"
require_file_contains "app/src/topwayTwMusic/res/values/donottranslate.xml" "com.tw.music.image.CoverProvider" "Topway CoverProvider authority resource"
require_file_contains "app/src/topwayTwMusic/res/values/donottranslate.xml" ">Music<" "Topway label resource"
require_file_contains "app/src/topwayTwMusicDebug/res/values/donottranslate.xml" "com.tw.music.debug.image.CoverProvider" "Topway debug CoverProvider authority resource"
require_file_contains "app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt" "com.tw.music.info" "Topway metadata broadcast contract"
require_file_contains "app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt" "com.tw.launcher.music_progress_duration" "Topway progress broadcast contract"
require_file_contains "app/src/main/java/org/oxycblt/auxio/headunit/topway/TopwayMusicContract.kt" "com.tw.music.action.pp" "Topway play/pause action contract"
require_file_contains "app/src/main/java/org/oxycblt/auxio/widgets/WidgetComponent.kt" "topwayBridge.publishMetadata" "runtime Topway metadata publisher"
require_file_contains "app/src/main/java/org/oxycblt/auxio/widgets/WidgetComponent.kt" "topwayBridge.publishProgress" "runtime Topway progress publisher"
require_file_contains ".github/workflows/manual-release.yml" "assembleTopwayTwMusicRelease" "manual release builds Topway release"
require_file_contains ".github/workflows/manual-release.yml" "topway-twmusic-release.apk" "manual release names Topway APK asset"

printf '\nChecking generated merged manifests when present...\n'
standard_debug_manifest="$(find_merged_manifest standardDebug || true)"
topway_debug_manifest="$(find_merged_manifest topwayTwMusicDebug || true)"
topway_release_manifest="$(find_merged_manifest topwayTwMusicRelease || true)"

if [[ -z "$standard_debug_manifest" || -z "$topway_debug_manifest" || -z "$topway_release_manifest" ]]; then
  warn "one or more merged manifests are absent; run ./gradlew :app:processStandardDebugMainManifest :app:processTopwayTwMusicDebugMainManifest :app:processTopwayTwMusicReleaseMainManifest for output-level checks"
else
  if ! python3 - "$standard_debug_manifest" "$topway_debug_manifest" "$topway_release_manifest" <<'PY'
import sys
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
failures = 0

def ok(message):
    print(f"OK: {message}")

def fail(message):
    global failures
    failures += 1
    print(f"ERROR: {message}", file=sys.stderr)

def attr(element, name):
    return element.attrib.get(ANDROID + name)

def parse(path):
    return ET.parse(path).getroot()

def components_with_filter(application, tag, action, category):
    matches = []
    for component in application.findall(tag):
        for intent_filter in component.findall("intent-filter"):
            actions = {attr(action_el, "name") for action_el in intent_filter.findall("action")}
            categories = {attr(cat_el, "name") for cat_el in intent_filter.findall("category")}
            if action in actions and category in categories:
                matches.append(component)
                break
    return matches

def require_package(root, expected, label):
    actual = root.attrib.get("package")
    if actual == expected:
        ok(f"{label} package is {expected}")
    else:
        fail(f"{label} package expected {expected}, got {actual!r}")

def require_provider(application, authority, label):
    providers = [p for p in application.findall("provider") if attr(p, "authorities") == authority]
    if providers:
        ok(f"{label} has CoverProvider authority {authority}")
    else:
        fail(f"{label} lacks CoverProvider authority {authority}")

def require_media_browser(application, label):
    for service in application.findall("service"):
        for intent_filter in service.findall("intent-filter"):
            actions = {attr(action_el, "name") for action_el in intent_filter.findall("action")}
            if "android.media.browse.MediaBrowserService" in actions:
                ok(f"{label} has MediaBrowserService intent-filter")
                if attr(service, "exported") == "true":
                    ok(f"{label} MediaBrowserService is exported")
                else:
                    fail(f"{label} MediaBrowserService is not exported=true")
                return
    fail(f"{label} lacks MediaBrowserService intent-filter")

def require_topway_receiver(application, label):
    expected = {
        "com.tw.music.action.cmd",
        "com.tw.music.action.prev",
        "com.tw.music.action.next",
        "com.tw.music.action.pp",
        "com.android.launcher.widget_music_progress",
    }
    for receiver in application.findall("receiver"):
        if attr(receiver, "name") != "org.oxycblt.auxio.headunit.topway.TopwayMusicBridgeReceiver":
            continue
        actions = set()
        for intent_filter in receiver.findall("intent-filter"):
            actions.update(attr(action_el, "name") for action_el in intent_filter.findall("action"))
        missing = expected - actions
        if attr(receiver, "exported") != "true":
            fail(f"{label} Topway receiver is not exported=true")
        elif missing:
            fail(f"{label} Topway receiver missing actions: {sorted(missing)}")
        else:
            ok(f"{label} Topway receiver exposes expected command actions")
        return
    fail(f"{label} lacks Topway bridge receiver")

def require_single_launcher(application, expected_name, label):
    matches = components_with_filter(application, "activity", "android.intent.action.MAIN", "android.intent.category.LAUNCHER")
    matches += components_with_filter(application, "activity-alias", "android.intent.action.MAIN", "android.intent.category.LAUNCHER")
    names = [attr(component, "name") for component in matches]
    if names == [expected_name]:
        ok(f"{label} single MAIN/LAUNCHER entry is {expected_name}")
    else:
        fail(f"{label} MAIN/LAUNCHER entries expected [{expected_name}], got {names}")

def require_topway_alias(application, label, debug=False):
    aliases = [alias for alias in application.findall("activity-alias") if attr(alias, "name") == "com.tw.music.MusicActivity"]
    if not aliases:
        fail(f"{label} lacks com.tw.music.MusicActivity activity-alias")
        return
    alias = aliases[0]
    if attr(alias, "targetActivity") == "org.oxycblt.auxio.MainActivity":
        ok(f"{label} alias targets org.oxycblt.auxio.MainActivity")
    else:
        fail(f"{label} alias target is {attr(alias, 'targetActivity')!r}")
    if attr(alias, "exported") == "true":
        ok(f"{label} alias is exported")
    else:
        fail(f"{label} alias is not exported=true")
    if attr(alias, "label") == "@string/info_topway_music_app_name":
        ok(f"{label} alias uses Topway label resource")
    else:
        fail(f"{label} alias label is {attr(alias, 'label')!r}")
    filter_actions = set()
    filter_categories = set()
    for intent_filter in alias.findall("intent-filter"):
        filter_actions.update(attr(action_el, "name") for action_el in intent_filter.findall("action"))
        filter_categories.update(attr(cat_el, "name") for cat_el in intent_filter.findall("category"))
    for action_name in ["android.intent.action.MAIN", "android.intent.action.MUSIC_PLAYER"]:
        if action_name in filter_actions:
            ok(f"{label} alias has action {action_name}")
        else:
            fail(f"{label} alias lacks action {action_name}")
    for category_name in ["android.intent.category.LAUNCHER", "android.intent.category.DEFAULT", "android.intent.category.APP_MUSIC"]:
        if category_name in filter_categories:
            ok(f"{label} alias has category {category_name}")
        else:
            fail(f"{label} alias lacks category {category_name}")

standard, topway_debug, topway_release = [parse(path) for path in sys.argv[1:4]]
standard_app = standard.find("application")
topway_debug_app = topway_debug.find("application")
topway_release_app = topway_release.find("application")

require_package(standard, "org.oxycblt.auxio.debug", "standardDebug")
require_single_launcher(standard_app, "org.oxycblt.auxio.MainActivity", "standardDebug")
require_provider(standard_app, "org.oxycblt.auxio.debug.image.CoverProvider", "standardDebug")
require_media_browser(standard_app, "standardDebug")
require_topway_receiver(standard_app, "standardDebug")
if any(attr(alias, "name") == "com.tw.music.MusicActivity" for alias in standard_app.findall("activity-alias")):
    fail("standardDebug is polluted with com.tw.music.MusicActivity")
else:
    ok("standardDebug has no com.tw.music.MusicActivity alias")

require_package(topway_debug, "com.tw.music.debug", "topwayTwMusicDebug")
require_single_launcher(topway_debug_app, "com.tw.music.MusicActivity", "topwayTwMusicDebug")
require_topway_alias(topway_debug_app, "topwayTwMusicDebug", debug=True)
require_provider(topway_debug_app, "com.tw.music.debug.image.CoverProvider", "topwayTwMusicDebug")
require_media_browser(topway_debug_app, "topwayTwMusicDebug")
require_topway_receiver(topway_debug_app, "topwayTwMusicDebug")

require_package(topway_release, "com.tw.music", "topwayTwMusicRelease")
require_single_launcher(topway_release_app, "com.tw.music.MusicActivity", "topwayTwMusicRelease")
require_topway_alias(topway_release_app, "topwayTwMusicRelease")
require_provider(topway_release_app, "com.tw.music.image.CoverProvider", "topwayTwMusicRelease")
require_media_browser(topway_release_app, "topwayTwMusicRelease")
require_topway_receiver(topway_release_app, "topwayTwMusicRelease")

sys.exit(1 if failures else 0)
PY
  then
    failures=$((failures + 1))
  fi
fi

printf '\nChecking built APK presence when present...\n'
standard_debug_apk="$(find_apk standard debug || true)"
topway_debug_apk="$(find_apk topwayTwMusic debug || true)"
topway_release_apk="$(find_apk topwayTwMusic release || true)"

if [[ -n "$standard_debug_apk" ]]; then
  pass "found standard debug APK: ${standard_debug_apk}"
else
  warn "standard debug APK not found; run ./gradlew :app:assembleStandardDebug"
fi
if [[ -n "$topway_debug_apk" ]]; then
  pass "found Topway debug APK: ${topway_debug_apk}"
else
  warn "Topway debug APK not found; run ./gradlew :app:assembleTopwayTwMusicDebug"
fi
if [[ -n "$topway_release_apk" ]]; then
  pass "found Topway release APK: ${topway_release_apk}"
else
  warn "Topway release APK not found; run ./gradlew :app:assembleTopwayTwMusicRelease"
fi

if [[ -n "$standard_debug_apk" ]]; then
  check_apk_manifest "$standard_debug_apk" "org.oxycblt.auxio.debug" "standardDebug"
fi
if [[ -n "$topway_debug_apk" ]]; then
  check_apk_manifest "$topway_debug_apk" "com.tw.music.debug" "topwayTwMusicDebug"
fi
if [[ -n "$topway_release_apk" ]]; then
  check_apk_manifest "$topway_release_apk" "com.tw.music" "topwayTwMusicRelease"
fi

printf '\nResult: '
if (( failures == 0 )); then
  printf 'PASS\n'
else
  printf 'FAIL (%d issue(s))\n' "$failures"
  exit 1
fi
