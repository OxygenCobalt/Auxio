# Auxio - Frequently Asked Questions

For a list of **supported formats** read [Supported Formats](FORMATS.md)

This FAQ will be continually updated as new changes and updates are made to Auxio.

## What is Auxio?

Auxio is local music player for android that I built for myself, primarily. Its meant to avoid alot of the feature-bloat and frustrating UX that other FOSS music players have, while still remaining customizable.

## Where can I download Auxio?

Nowhere currently, I still need to make some changes before its ready to be put up on public-facing stores. See the [Releases](https://github.com/oxygencobalt/Auxio/releases) to download it for now. It will arrive on F-Droid eventually.

## Can I translate Auxio to my native language?

I still need to set up Weblate, but you can open a [Pull Request](https://github.com/enricocid/Music-Player-GO/pulls) with any translations you want to be added for now.

## Why ExoPlayer?

ExoPlayer is far more flexible than the native MediaPlayer API, which allows Auxio to have consistent behavior across devices & OEMs, along with allowing Auxio to be extended to music sources outside of local files. You can read more about the benefits (and drawbacks) of ExoPlayer [Here](https://exoplayer.dev/pros-and-cons.html).

## Why is there no black mode?

Android does not support a black mode directly, only dark and light. Therefore I have to implement the mode myself, which will take some time. I plan to add it however.

## Why cant I have a custom accent?

Custom accents would require some changes to the accent system in the app, which will take some time. I plan to add them however.

## Why are accents lighter/less saturated in dark mode?

As per the [Material Guidelines](https://material.io/design/color/dark-theme.html), accents should be less saturated on dark mode to reduce eye strain and to make it look better in general.

You will be able to set the accent to something less saturated when I implement custom accents.

## Why isn't edge-to-edge enabled on versions below Oreo?

The APIs for changing system bar colors were only added in API Level 27 (Oreo MR1), meaning that edge-to-edge will not work below that version.

I could possibly extend edge-to-edge support to earlier versions, but it would take awhile.

## Why doesnt edge-to-edge work when my phone is in landscape?

When a *phone* [Not a tablet] is in landscape, the insets meant to make the UI fit dont work properly, therefore the feature is mostly disabled.

## How can I contribute/report issues?

Open an [Issue](https://github.com/OxygenCobalt/Auxio/issues) or a [Pull Request](https://github.com/OxygenCobalt/Auxio/pulls), please note the [Contribution Guidelines](../.github/CONTRIBUTING.md) and [Accepted Additions](ADDITIONS.md).

## Does this app keep/send any information about myself or my device?

Auxio does not log any information about the device or its owner, and it has no internet access to send that information off in the first place.