<p align="center"><img src="fastlane/metadata/android/en-US/images/icon.png" width="150"></p>
<h1 align="center"><b>Auxio-TS</b></h1>
<h4 align="center">A simple, rational music player for Android Car Headunits.</h4>
<p align="center">
    <a href="https://github.com/cbkii/Auxio-TS/releases/">
        <img alt="Releases" src="https://img.shields.io/github/downloads/OxygenCobalt/Auxio/total.svg?color=4B95DE&style=flat">
    </a>
    <a href="https://www.gnu.org/licenses/gpl-3.0">
        <img src="https://img.shields.io/badge/license-GPL%20v3-2B6DBE.svg?style=flat">
    </a>
    <img alt="Minimum SDK Version" src="https://img.shields.io/badge/API-24%2B-1450A8?style=flat">
</br><a href="https://github.com/OxygenCobalt/Auxio#Donate">Donate</a>
</p>

## About

[Auxio](https://github.com/OxygenCobalt/Auxio) is a local music player with a fast, reliable UI/UX without the many useless features present in other music players. Built off of modern media playback libraries, Auxio has superior library support and listening quality compared to other apps that use outdated Android functionality. In short, **It plays music.**

## Auxio-TS
### Topway head-unit Auxio variant

Auxio-TS is a Topway/TS-series head-unit focused variant of [Auxio](https://github.com/OxygenCobalt/Auxio), built for Android automotive head units such as TS18/TW/TWTHEME/iLauncher environments.

The goal is not to create a generic Android music player fork. The goal is to preserve Auxio’s clean local-music architecture while adapting the app for practical use on Topway-style Android head units: landscape screens, launcher integration, media buttons, steering-wheel controls, persistent playback controls, and reliable local-library playback in a car environment.

Auxio-TS remains an ordinary Android app. It does **not** impersonate `com.tw.music`, does **not** require system UID or platform signing, and does **not** copy proprietary Topway/TW smali code.

## Screenshots
[coming soon]

---

## Project status

Auxio-TS is under active development.

Current focus areas:

- Topway/TS18 head-unit usability.
- Android-standard media session behaviour.
- Reliable notification/media transport controls.
- Launcher shortcuts and head-unit friendly entry points.
- AppWidget support for launcher/home-screen surfaces.
- Local-library playback, including FLAC and common offline music formats.
- Validation against TS18/TW/TWTHEME/iLauncher behaviour.
- Preserving upstream Auxio maintainability.

This repository currently tracks the development branch. Stable public releases may lag behind active development.

---

## What Auxio-TS is

Auxio-TS is:

- A local, offline music player for Android head units.
- A fork of Auxio, retaining its Media3/ExoPlayer-based playback model.
- A Topway head-unit variant intended for TS18/TW/TWTHEME/iLauncher ecosystems.
- Designed around standard Android media APIs first.
- Built to be maintainable, testable, and auditable.
- Focused on real head-unit behaviour rather than speculative vendor hooks.

---

## What Auxio-TS is not

Auxio-TS is **not**:

- A privileged/system app or root-only modification.
- A smali port or repackage of the stock Topway music player.
- A vendor-private framework experiment.
- A broad Android Auto replacement.
- A generic upstream Auxio feature fork.

Topway-specific behaviour should be implemented only when there is clear evidence, public precedent, or TS18 runtime validation showing that Android-standard behaviour is insufficient.

---

## Head-unit compatibility goals

Auxio-TS targets the practical integration points that matter on Topway-style Android head units:

### Media controls

- Android media notification controls.
- MediaSession transport controls.
- Hardware media key handling.
- Steering-wheel button compatibility where routed through standard Android media key paths.
- Playback state persistence across app restarts and head-unit sleep/resume scenarios.

### Launcher and TWTHEME surfaces

- Head-unit friendly launcher entry points.
- Shortcuts for common playback/library destinations.
- AppWidget compatibility through standard Android AppWidget APIs.
- Package-scoped deep links for Now Playing, Shuffle, Queue, Library, and settings flows.

### Car-use UX

- Landscape-first usability.
- Large touch targets.
- Persistent playback affordances.
- Reduced friction for common in-car actions.
- Local library browsing suitable for head-unit screens.

### Coexistence

- Coexistence with stock Topway media components.
- Coexistence with TLink/ZLink/phone-link apps.
- Audio focus correctness during navigation, Bluetooth, phone-link, and media-source transitions.

---

## Core playback features inherited from Auxio

Auxio-TS keeps the strengths of upstream Auxio, including but not limited to:

- Local music playback.
- Media3/ExoPlayer playback stack.
- Offline-first operation.
- Private local library handling.
- Embedded artwork support.
- Playlist support.
- ReplayGain support.
- Gapless playback where supported.
- Folder-aware library management.
- Metadata parsing through the existing Auxio/taglib stack.
- Modern Android media behaviour.
- See upstream repo (original Auxio project) for the full list.

---

## Permissions

- Storage (`READ_MEDIA_AUDIO`, `READ_EXTERNAL_STORAGE`) to read and play your music files
- Services (`FOREGROUND_SERVICE`, `WAKE_LOCK`) to keep the music playing in the background
- Notifications (`POST_NOTIFICATION`) to indicate ongoing playback and music loading

---

## Design principles

Auxio-TS development follows these rules:

1. Preserve upstream Auxio architecture wherever possible.
2. (Try to) keep Topway-specific logic isolated from core playback and library code.
3. Prefer Android-standard APIs before vendor-specific approaches.
4. Validate TS18/TW/TWTHEME behaviour on real hardware.
5. Avoid speculative in-app probe frameworks.
6. Avoid package impersonation and privileged-permission strategies.
7. Add compatibility features only when a specific gap is proven.

---

## Documentation

Start here for current project guidance:

- [`docs/README.md`](docs/README.md) — documentation index.
- [`docs/TS18_REQUIREMENTS.md`](docs/TS18_REQUIREMENTS.md) — Topway/TS18 requirements.
- [`docs/TS18_INTEGRATION_ARCHITECTURE.md`](docs/TS18_INTEGRATION_ARCHITECTURE.md) — architecture and boundary rules.
- [`docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md`](docs/TS18_SOURCE_LED_INTEGRATION_STRATEGY.md) — source-led TS18/TW/TWTHEME development policy.
- [`docs/TS18_VALIDATION_RUNBOOK.md`](docs/TS18_VALIDATION_RUNBOOK.md) — runtime validation flow.
- [`docs/DEVELOPMENT_ROADMAP.md`](docs/DEVELOPMENT_ROADMAP.md) — planned implementation work.
- [`AGENTS.md`](AGENTS.md) — coding-agent authority and guardrails.

Evidence snapshots and diagnostic material are retained for validation and comparison only. They are not implementation source.

## Building

Auxio relies on a patched version of Media3 that enables some extra playback features, alongside taglib for metadata
parsing. This adds some caveats to the build process:
1. `cmake` and `ninja-build` must be installed before building the project.
2. The project uses submodules, so when cloning initially, use `git clone --recurse-submodules` to properly
download the external code.
3. You are **unable** to build this project on windows, as the custom Media3 build runs shell scripts that
will only work on unix-based systems.

## Contributing

Auxio accepts most contributions as long as they follow the [Contribution Guidelines](/.github/CONTRIBUTING.md).

However, feature additions and major UI changes are less likely to be accepted. See [Why Are These Features Missing?](https://github.com/OxygenCobalt/Auxio/wiki/Why-Are-These-Features-Missing%3F) for more information.

## License

[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl-3.0.en.html)

Auxio is Free Software: You can use, study share and improve it at your will. Specifically you can redistribute and/or modify it under the terms of the [GNU General Public License](https://www.gnu.org/licenses/gpl.html) as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

More information can be found [here](https://github.com/OxygenCobalt/Auxio/wiki/Licenses).


---

## Donate

You can support Auxio's development through [my Github Sponsors page](https://github.com/sponsors/OxygenCobalt). Get the ability to prioritize features and have your profile added to the README, Release Changelogs, and even the app itself!

<h3 align="center">Legendary supporters:</h3>
<p align="center">
    <a href="https://github.com/bkkellyh"><img src="https://avatars.githubusercontent.com/u/248118457?v=4" width=250 /></a> 
</p>
<h3 align="center"><a href="https://github.com/bkkellyh">@bkkellyh</a> - <i>$500!</i></h3>

<hr />

<p align="center"><b>$8/month supporters:</b></p>

<p align="center">
    <a href="https://github.com/uku3lig"><img src="https://avatars.githubusercontent.com/u/61147779?v=4" width=50 /></a>
</p>
<p align="center">
    And **1** Private Sponsor!
</p>
