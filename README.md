<p align="center"><img src="fastlane/metadata/android/en-US/images/icon.png" width="150"></p>
<h1 align="center"><b>Auxio</b></h1>
<h4 align="center">A simple, rational music player for android.</h4>
<p align="center">
    <a href="https://github.com/oxygencobalt/Auxio/releases/tag/v3.5.1">
        <img alt="Latest Version" src="https://img.shields.io/static/v1?label=tag&message=v3.5.1&color=64B5F6&style=flat">
    </a>
    <a href="https://github.com/oxygencobalt/Auxio/releases/">
        <img alt="Releases" src="https://img.shields.io/github/downloads/OxygenCobalt/Auxio/total.svg?color=4B95DE&style=flat">
    </a>
    <a href="https://www.gnu.org/licenses/gpl-3.0">
        <img src="https://img.shields.io/badge/license-GPL%20v3-2B6DBE.svg?style=flat">
    </a>
    <img alt="Minimum SDK Version" src="https://img.shields.io/badge/API-24%2B-1450A8?style=flat">
</p>
<h4 align="center"><a href="/CHANGELOG.md">Changelog</a> | <a href="https://github.com/OxygenCobalt/Auxio/wiki">Wiki</a> | <a href="https://github.com/OxygenCobalt/Auxio#Donate">Donate</a></h4>
<p align="center">
    <a href="https://f-droid.org/app/org.oxycblt.auxio"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" width="170"></a>
    <a href="https://hosted.weblate.org/engage/auxio/"><img height=64 src="https://hosted.weblate.org/widgets/auxio/-/strings/287x66-grey.png" alt="Translation status" /></a>
</p>

## About

Auxio is a local music player with a fast, reliable UI/UX without the many useless features present in other music players. Built off of modern media playback libraries, Auxio has superior library support and listening quality compared to other apps that use outdated android functionality. In short, **It plays music.**

**The default branch is the development version of the repository. For a stable version, see the master branch.**

## Screenshots

<p align="center">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot0.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot1.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot2.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot3.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot4.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot5.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot6.png" width=200>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/shot7.png" width=200>
</p>


## Features

- Playback based on [Media3 ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer)
- Snappy UI derived from the latest Material Design guidelines
- Opinionated UX that prioritizes ease of use over edge cases
- Customizable behavior
- Support for disc numbers, multiple artists, release types,
precise/original dates, sort tags, and more
- Advanced artist system that unifies artists and album artists
- SD Card-aware folder management
- Reliable playlisting functionality
- Playback state persistence
- Android auto support
- Automatic gapless playback
- Full ReplayGain support (On MP3, FLAC, OGG, OPUS, and MP4 files)
- External equalizer support (ex. Wavelet)
- Edge-to-edge
- Embedded covers support
- Search functionality
- Headset autoplay
- Stylish widgets that automatically adapt to their size
- Completely private and offline
- No rounded album covers (by default)

## Permissions

- Storage (`READ_MEDIA_AUDIO`, `READ_EXTERNAL_STORAGE`) to read and play your music files
- Services (`FOREGROUND_SERVICE`, `WAKE_LOCK`) to keep the music playing in the background
- Notifcations (`POST_NOTIFICATION`) to indicate ongoing playback and music loading

## Donate

You can support Auxio's development through [my Github Sponsors page](https://github.com/sponsors/OxygenCobalt). Get the ability to prioritize features and have your profile added to the README, Release Changelogs, and even the app itself!

<p align="center"><b>$8/month supporters:</b></p>

<p align="center">
    <a href="https://github.com/alanorth"><img src="https://avatars.githubusercontent.com/u/191754?v=4" width=50 /></a>
    <a href="https://github.com/dmint789"><img src="https://avatars.githubusercontent.com/u/53250435?v=4" width=50 /></a>
    <a href="https://github.com/gtsiam"><img src="https://avatars.githubusercontent.com/u/7459196?v=4" width=50 /></a>
    <a href="https://github.com/yrliet"><img src="https://avatars.githubusercontent.com/u/151430565?v=4" width=50 /></a>
</p>

## Building

Auxio relies on a custom version of Media3 that enables some extra features. This adds some caveats to the build process:
1. `cmake` and `ninja-build` must be installed before building the project.
2. The project uses submodules, so when cloning initially, use `git clone --recurse-submodules` to properly
download the external code.
3. You are **unable** to build this project on windows, as the custom Media3 build runs shell scripts that
will only work on unix-based systems.

## Contributing

Auxio accepts most contributions as long as they follow the [Contribution Guidelines](/.github/CONTRIBUTING.md).

However, feature additions and major UI changes are less likely to be accepted. See
[Why Are These Features Missing?](https://github.com/OxygenCobalt/Auxio/wiki/Why-Are-These-Features-Missing%3F)
for more information.



## License

[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl-3.0.en.html)

Auxio is Free Software: You can use, study share and improve it at your
will. Specifically you can redistribute and/or modify it under the terms of the
[GNU General Public License](https://www.gnu.org/licenses/gpl.html) as
published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

More information can be found [here](https://github.com/OxygenCobalt/Auxio/wiki/Licenses).
