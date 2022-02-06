# Auxio - Frequently Asked Questions

This FAQ will be continually updated as new changes and updates are made to Auxio.

#### Where can I download Auxio?

Auxio is available on the [F-Droid](https://f-droid.org/en/packages/org.oxycblt.auxio/) repository.
Auxio is not and will never be on the play store due to it being a proprietary and draconian platform.

#### Why ExoPlayer?

ExoPlayer is far more flexible than the native MediaPlayer API, which allows consistent behavior across devices & OEMs and the
ability to be extended to music sources outside of local files. You can read more about the benefits (and drawbacks) of ExoPlayer
[Here](https://exoplayer.dev/pros-and-cons.html).

#### What formats does Auxio support?

As per the [Supported ExoPlayer Formats](https://exoplayer.dev/supported-formats.html), Auxio supports
MP4, MP3, MKA, OGG, WAV, MPEG, AAC on all versions of Android. Auxio also supports FLAC on all versions
of Android through the use of the ExoPlayer FLAC extension.

#### Auxio doesn't load my music correctly!

This is probably caused by one of two reasons:

1. If other players like Phonograph, Retro Music, or Music Player GO load it correctly, then Auxio has a bug and it should be [reported](https://github.com/OxygenCobalt/Auxio/issues).
2. If the aforementioned players don't work, but players like Vanilla Music and VLC do, then it's a problem with the Media APIs that Auxio relies on. There is nothing I can do about it.

#### I have a large library and Auxio takes really long to load it!

This is expected since reading from the audio database takes awhile, especially with libraries containing 10k songs or more.

#### Auxio does not detect new music!

This is a current limitation with the music loader. To remedy this, go to Settings -> Reload music whenever new songs are added.
I hope to make the app rescan music on the fly eventually.

#### ReplayGain isn't working on my music!

This is for a couple reason:
- Auxio doesn't extract ReplayGain tags for your format.
- Auxio doesn't recognize your ReplayGain tags. This is usually because of a non-standard tag like ID3v2's `RVAD` or
an unrecognized name.
- Your tags use a ReplayGain value higher than 0. Due to technical limitations, Auxio does not support this right now. 
I do plan to add it eventually.

#### What is dynamic ReplayGain?

Dynamic ReplayGain is a quirk setting based off the FooBar2000 plugin that dynamically switches from track gain to album
gain depending on if the current playback is from an album or not.

#### Why are accents lighter/less saturated in dark mode?

As per the [Material Design Guidelines](https://material.io/design/color/dark-theme.html), accents should be less
saturated on dark mode to reduce eye strain and to increase visual cohesion.

#### Does this app keep/send any information about myself or my device?

Auxio does not log any information about the device or its owner, and it has no internet access to send that information off in the first place.

#### How can I contribute/report issues?

Open an [Issue](https://github.com/OxygenCobalt/Auxio/issues) or a [Pull Request](https://github.com/OxygenCobalt/Auxio/pulls),
please note the [Contribution Guidelines](../.github/CONTRIBUTING.md) and [Accepted Additions](ADDITIONS.md) however.

#### Can I translate Auxio to my native language?

See the [Translations](https://github.com/OxygenCobalt/Auxio/issues/3) issue for guidance on how to create translations and submit them to the project.
Any contributions are appreciated and tend to always be accepted.
