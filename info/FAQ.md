# Auxio - Frequently Asked Questions
This FAQ will be continually updated as new changes and updates are made to Auxio.

#### Where can I download Auxio?
Auxio is available on the [F-Droid](https://f-droid.org/en/packages/org.oxycblt.auxio/) repository.
Auxio is not and will never be on the play store due to it being a proprietary and draconian platform.

#### Why ExoPlayer?
Unlike the stock `MediaPlayer` API that most music apps use, ExoPlayer is independent from the android system's
audio framework. This allows for the following:
- Consistent behavior across devices and OS versions
- Features that are normally not possible with a normal audio stream (such as positive ReplayGain values)
- Playback to be (theoretically) be extended beyond local files in the future

You can read more about the benefits (and drawbacks) of ExoPlayer [Here](https://exoplayer.dev/pros-and-cons.html).

#### What formats does Auxio support?
As per the [Supported ExoPlayer Formats](https://exoplayer.dev/supported-formats.html), Auxio supports
MP4, MP3, MKA, OGG, WAV, MPEG, AAC on all versions of Android. Auxio also supports FLAC on all versions
of Android through the use of the ExoPlayer FLAC extension.

#### Auxio doesn't load my music correctly!
This depends on the context:
1. If "Ignore MediaStore Tags" is enabled, please create a bug report.
2. If "Ignore MediaStore Tags" is not enabled, please check below to make sure your issue is not already
awknowledged before reporting a bug.
	- Moreso, if the issue encountered does not appear in other apps like Music Player GO, Phonograph,
or Retro Music then it should definitely be reported, as it is a logic bug on Auxio's part.

***Known unfixable music loading issues***

These are a list of unfixable music loading issues that can only be fixed by enabling "Ignore MediaStore Tags":

**My FLAC/OGG/OPUS/MP3 files don't have dates:** Android does not read the `DATE` tag from vorbis files. It reads the `YEAR` tag.
Similarly, Android does not read ID3v2.4's `TDRC` and actually reads `TYER` regardless of the version. This is because android's
metadata parser is stuck in 2008.

**Some files with accented/symbolic characters have corrupted tags:** When Android extracts metadata, at some point it tries to convert the bytes it extracted to a
java string, which apparently involves detecting the encoding of the data dynamically and then converting it to Java's Unicode dialect. Of course, trying to detect
codings on the fly like that is a [terrible idea](https://en.wikipedia.org/wiki/Bush_hid_the_facts), and more often than not it results in UTF-8 tags (Seen on
FLAC/OGG/OPUS files most often) being corrupted. It also affects MP3 files with ID3v2.4.0 tags that use the UTF-8 encoding in text-based tags.

**I have a large library and Auxio takes really long to load it:** This is expected since reading from the audio database takes awhile, especially with libraries
containing 10k songs or more.

**Auxio does not detect disc numbers:** If your device runs Android 10, then Auxio cannot parse a disc from the media database due to
a regression introduced by Google in that version. If this issue appears in another android version, please file an issue. 

***Other music loading issues***

**There should be one artist, but instead I get a bunch of "Artist & Collaborator" artists:** This likely means your tags are wrong. By default, Auxio will use the
"album artist" tag for grouping if present, falling back to the "artist" tag otherwise. If your music does not have such a field, it will result in fragmented artists.
The reason why Auxio does not simply parse for separators and then extract artists that way is that it risks mangling artists that don't actually have collaborators,
such as "Black Country, New Road" becoming "Black Country". I understand that some users may leverage MusicBrainz tags that enable a song to be referenced by several
artists, but such functionality in Auxio will likely take awhile to implement to to UI and technical constraints.

**Auxio does not detect new music:** This is Auxio's default behavior due to limitations regarding android's filesystem APIs. To enable such behavior, turn on
"Automatic reloading" in settings. Note that this option does require a persistent notification and higher battery usage.

#### What does "Ignore MediaStore Tags" even do?
"Ignore MediaStore Tags" configures Auxio's music loader to extract metadata manually using ExoPlayer, which enables the following:
- Fixes for most of the annoying, unfixable issues with `MediaStore` that were elaborated on above
- Sort tag support
	- For example, a title written in Japanese could have a phonetic version in their sort tags. This will be used in sorting and search.
- Better date support
	- If an artist released several albums in a single year, you can tag your music to have a particular date and time it was released on, and Auxio will
	sort the albums accordingly. Examples include `YYYY-MM-DD` or even `YYYY-MM-DD HH:MM:SS`
	- Auxio is also capable of supporting original dates. If a remastered album was released in 2020, but the original album was released in 2000,
	you can tag your music with `TDOR`/`TORY` for MP3 and `ORIGINALDATE` for Vorbis with the year 2000, and Auxio will display 2000 in-app.
- Release type support from `TXXX:MusicBrainz Release Type`/`GRP1` in MP3 files, and `RELEASETYPE` in OGG/OPUS/FLAC
	- Auxio specifically expects something formatted like `<primary> + <secondary>`, `<primary>`, or `<secondary>`. This should be contained in a single tag.
	- `<primary`> corresponds to `album`, `ep`, or `single`
	- `<secondary>` corresponds to `compilation`, `soundtrack`, `mixtape`, `live`, or `remix`. The first three will override the primary type,
		(ex. `album + compilation` -> "Compilation"), but the latter two will be used to augment the primary type (ex. `album + live` -> "Live Album").

#### Why does search return songs that don't match my query?
Auxio actually takes several types of metadata in account in searching:
- The name, normalized so that any accented/symbolic characters are converted to normal characters. For example, Ü -> U.
- The sort tag of a particular song/album/artist, as such often contain latinized/translated versions of a given title.
- The file name, as some users don't have usable title metadata, and instead use the file name as the title.

#### Why does playback pause whenever music is reloaded?
Whenever the music library signifigantly changes, updating the player's data while it is still playing may result in
unwanted bugs or unexpected music playing. To safeguard against this, Auxio will pause whenever it reloads a new
music library. 

#### ReplayGain isn't working on my music!
This is for a couple reason:
- Auxio doesn't extract ReplayGain tags for your format. This is a problem on ExoPlayer's end and should be
investigated there.
- Auxio doesn't recognize your ReplayGain tags. This is usually because of a non-standard tag like ID3v2's `RVAD` or
an unrecognized name.

#### My lossless audio sounds lower-quality in Auxio!
This is a current limitation with the ExoPlayer. Basically, all audio is downsampled to 16-bit PCM audio, even
if the source audio is higher quality. I can enable something that might be able to remedy such, but implementing it
fully may take some time.

#### Why is playback distorted when I play my FLAC/WAV files?
ExoPlayer, while powerful, does add some overhead when playing exceptionally high-quality files (2000+ KB/s bitrate,
90000+ Hz sample rate). This is worsened by the ReplayGain system, as it has to copy the audio buffer no matter what.
This results in choppy, distorted playback in some cases as audio data cannot be delivered in time. I can also mitigate
this similarly to the above issue, but again, it may take some time.

#### Why are accents lighter/less saturated in dark mode?
As per the [Material Design Guidelines](https://material.io/design/color/dark-theme.html), accents should be less
saturated on dark mode to reduce eye strain and to increase visual cohesion.

#### Does this app keep/send any information about myself or my device?
Auxio does not log any information about the device or its owner, and it has no internet access to send that information off in the first place.

#### How can I contribute/report issues?
Open an [Issue](https://github.com/OxygenCobalt/Auxio/issues) or a [Pull Request](https://github.com/OxygenCobalt/Auxio/pulls),
please note the [Contribution Guidelines](../.github/CONTRIBUTING.md) and [Accepted Additions](ADDITIONS.md) however.

#### Can I translate Auxio to my native language?
Please go to Auxio's [Weblate Project](https://hosted.weblate.org/engage/auxio/) to create new translations for the project.
