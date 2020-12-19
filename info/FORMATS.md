# Supported Formats

Auxio is based off [ExoPlayer](https://exoplayer.dev/), which provides greater flexibility and consistency with how Auxio plays music.

Here are the music formats that Auxio supports, as per the [Supported ExoPlayer Formats](https://exoplayer.dev/supported-formats.html):

✅ = Supported

👎 = Not fully supported

❌ = Not supported

| Format | Supported | Comments |
|--------|-----------|-----------
| M4A | ✅ | |
| MP3 | ✅ | Some files may not be seekable |
| MKA | ✅ | |
| OGG | ✅ | Containing Vorbis, Opus, and FLAC |
| WAV | ✅ |  |
| MPEG_TS | ✅ | |
| MPEG_TS | ✅ | |
| AAC  | 👎 | Not seekable |
| FLAC | ❌ | Auxio must be patched with the [FLAC Extension](https://github.com/google/ExoPlayer/tree/release-v2/extensions/flac) |
