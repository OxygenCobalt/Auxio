# TS18 validation runbook

## Test matrix

Run each stage against:

1. Stock `com.tw.music`.
2. Existing third-party app already on device, preferably Spotify and/or Poweramp if available.
3. Upstream Auxio if installable.
4. Auxio-TS debug build.

## Stage 0 — install and baseline

```sh
adb install -r app/build/outputs/apk/debug/*.apk
adb shell pm list packages | grep -Ei 'auxio|music|tw|zlink'
```

On-device fallback: install APK from file manager if ADB is unavailable.

## Stage 1 — idle capture

Run:

```sh
AUXIO_TS_PACKAGE=org.oxycblt.auxio ./scripts/ts18_collect_auxio_ts_evidence.sh idle
```

Expected:

- No active Auxio-TS session.
- Device/package/profile capture succeeds even if some dumps are denied.

## Stage 2 — stock music baseline

Manually play a local song in stock `com.tw.music`, then capture:

```sh
AUXIO_TS_PACKAGE=com.tw.music ./scripts/ts18_collect_auxio_ts_evidence.sh stock-music-playing
```

Record manually:

- Does launcher/home music widget update?
- Do steering wheel/media keys work?
- Does ZLink/TLink show metadata?
- Does notification media control appear?
- Does audio continue after backgrounding?

## Stage 3 — existing third-party baseline

Play Spotify/Poweramp/another installed media app, then capture with its package name.

Goal: determine whether TS18 launcher/TW surfaces already accept standard third-party MediaSession.

## Stage 4 — Auxio-TS MP3 playback

Play a normal MP3 from local storage.

Acceptance:

- `dumpsys media_session` shows Auxio-TS active session.
- Media notification controls work.
- Hardware/media keys work or failure is recorded.
- `dumpsys audio` shows expected focus ownership.
- Launcher/widget behaviour is recorded.

## Stage 5 — Auxio-TS FLAC playback

Test at least:

- 16-bit/44.1 kHz FLAC.
- 16-bit/48 kHz FLAC.
- 24-bit/48 kHz FLAC if available.

Acceptance:

- Playback starts reliably.
- Seek/pause/resume works.
- Metadata/cover art appears.
- No repeated underrun/buffering errors in logcat.

## Stage 6 — sleep/resume

While playing and while paused:

- turn screen off/on if available;
- simulate ACC/sleep/resume if safely available;
- leave app backgrounded;
- resume from notification/media key.

## Stage 7 — ZLink / Android Auto behaviour

Only when safe:

- Start ZLink/Android Auto.
- Play Auxio-TS.
- Capture metadata/control behaviour.
- Compare with stock `com.tw.music` and Spotify.

## Required evidence bundle naming

Use names like:

```text
auxio-ts-evidence_YYYYMMDD_HHMMSS_stock-music-playing.zip
auxio-ts-evidence_YYYYMMDD_HHMMSS_auxio-ts-flac-playing.zip
```

Do not commit raw bundles to the public repo. Upload them privately for analysis or commit only redacted summaries.
