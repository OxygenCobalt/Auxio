#!/system/bin/sh
# Compare active media sessions for stock, third-party, and Auxio-TS runs.
# This is a helper: start playback manually in the target app before running.

PKG="${1:-org.oxycblt.auxio}"
echo "### Package: $PKG"
pm path "$PKG" 2>&1 || true

echo
echo "### Media sessions containing package or common media terms"
dumpsys media_session 2>&1 | grep -Ei "$PKG|auxio|music|spotify|zlink|tlink|MediaSession|Sessions Stack|state=|metadata|queue|package=" | head -250 || true

echo
echo "### Audio focus stack"
dumpsys audio 2>&1 | grep -Ei "$PKG|auxio|music|spotify|focus|AudioFocus|USAGE_MEDIA|STREAM_MUSIC|com.tw.service" | head -250 || true

echo
echo "### Recent filtered logcat"
logcat -d -v threadtime 2>&1 | grep -Ei "$PKG|auxio|music|spotify|MediaSession|AudioFocus|MEDIA_BUTTON|com.tw.service|zlink|tlink|launcher|theme" | tail -300 || true
