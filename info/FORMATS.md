# Supported Formats

Auxio is based off [ExoPlayer](https://exoplayer.dev/), which provides greater flexibility and consistency with how Auxio plays music.

Here are the music formats that Auxio supports, as per the [Supported ExoPlayer Formats](https://exoplayer.dev/supported-formats.html):

✅ = Supported

👎 = Not supported well

| Format | Supported | Comments |
|--------|-----------|-----------
| MP4    | ✅ | |
| MP3    | ✅ | |
| MKA    | ✅ | |
| OGG    | ✅ | Containing Vorbis, Opus, and FLAC |
| WAV    | ✅ | |
| MPEG   | ✅ | |
| AAC    | ✅ | |
| FLAC   | 👎 | Supported on Android 8.1 or newer. Auxio must be patched with the [FLAC Extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac) on lower versions. |
