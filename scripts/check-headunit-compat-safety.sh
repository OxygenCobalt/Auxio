#!/usr/bin/env bash
set -euo pipefail
allowed_topway_main='app/src/main/java/org/oxycblt/auxio/headunit/topway/'
allowed_topway_test='app/src/test/java/org/oxycblt/auxio/headunit/topway/'
if command -v rg >/dev/null 2>&1; then
  forbidden_hits="$(rg -n 'android\\.tw\\.john|com\\.tw\\.service\\.xt|ITWCommandAidl|sharedUserId|android\\.uid\\.system' app/src/main app/src/test || true)"
  topway_hits="$(rg -n 'com\\.tw\\.music\\.action|com\\.tw\\.music\\.info|com\\.tw\\.launcher\\.music_progress_duration|com\\.android\\.launcher\\.widget_music_progress' app/src/main app/src/test || true)"
else
  forbidden_hits="$(grep -RInE 'android\\.tw\\.john|com\\.tw\\.service\\.xt|ITWCommandAidl|sharedUserId|android\\.uid\\.system' app/src/main app/src/test || true)"
  topway_hits="$(grep -RInE 'com\\.tw\\.music\\.action|com\\.tw\\.music\\.info|com\\.tw\\.launcher\\.music_progress_duration|com\\.android\\.launcher\\.widget_music_progress' app/src/main app/src/test || true)"
fi
if [ -n "$forbidden_hits" ]; then
  echo "$forbidden_hits" >&2
  echo "Forbidden private/vendor hooks found in product code" >&2
  exit 1
fi
if [ -n "$topway_hits" ]; then
  while IFS= read -r line; do
    [ -z "$line" ] && continue
    path="${line%%:*}"
    case "$path" in
      $allowed_topway_main*|$allowed_topway_test*|docs/*) ;;
      *)
        echo "$line" >&2
        echo "Topway compatibility strings must stay in isolated topway bridge/test/docs paths" >&2
        exit 1
        ;;
    esac
  done <<< "$topway_hits"
fi
echo "headunit compat safety checks passed"
