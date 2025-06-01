# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Auxio is a modern Android music player written in Kotlin that emphasizes simplicity and performance. It uses Media3 ExoPlayer for playback and includes a custom native music indexing library called musikr.

## Build System & Commands

### Prerequisites
- `cmake` and `ninja-build` must be installed before building
- Project uses git submodules - clone with `git clone --recurse-submodules`
- **Cannot build on Windows** - requires Unix-based system for Media3 build scripts

### Common Build Commands
```bash
# Build debug version
./gradlew assembleDebug

# Build release version  
./gradlew assembleRelease

# Run tests
./gradlew test

# Run connected tests
./gradlew connectedAndroidTest

# Code formatting (Spotless)
./gradlew spotlessApply

# Check code formatting
./gradlew spotlessCheck
```

### Development Modules
- `app/` - Main Android application
- `musikr/` - Native music indexing library with TagLib integration
- `media/` - Patched Media3 ExoPlayer libraries (git submodule)

## Architecture Overview

### Core Architecture Pattern
- **MVVM** with ViewModels for each major screen (Home, Detail, Playback, Search)
- **Repository Pattern** - MusicRepository as single source of truth for music data
- **Dependency Injection** - Hilt/Dagger for modular dependency management
- **Single Activity Architecture** with Navigation Components
- **Service-Oriented** background music processing

### Key Modules & Components

#### Music Management (`app/src/main/java/org/oxycblt/auxio/music/`)
- `MusicRepository` - Central music library state management
- `MusicViewModel` - UI layer for music data access
- `service/` - MediaBrowserServiceCompat integration
- `shim/` - Abstraction layer for musikr integration

#### Playback Engine (`app/src/main/java/org/oxycblt/auxio/playback/`)
- `PlaybackViewModel` - Main playback UI interface
- `PlaybackStateManager` - Core playback state management
- `service/` - Background ExoPlayer integration
- `queue/` - Playback queue management
- `persist/` - Playback state persistence (Room database)
- `replaygain/` - Audio processing for volume normalization

#### UI Structure (`app/src/main/java/org/oxycblt/auxio/`)
- `home/` - Main library browsing with customizable tabs
- `detail/` - Album, artist, genre, and playlist detail views
- `search/` - Music search functionality
- `list/` - Shared list components with sorting/filtering
- `ui/` - Custom UI components, theming, animations

#### Native Music Library (`musikr/`)
- **Pipeline-based indexing** - Multi-stage scanning (Explore → Extract → Evaluate)
- **TagLib integration** - Native C++ metadata extraction
- **File system caching** - Device storage access optimization
- **Cover art management** - Embedded and external cover support

### Data Flow
1. musikr scans and indexes music files from device storage
2. MusicRepository manages indexed library and exposes to UI
3. ViewModels provide reactive state for UI components using StateFlow
4. PlaybackService handles audio playback via ExoPlayer
5. UI observes ViewModels and updates reactively

## Code Conventions

### Language & Libraries
- **Kotlin** - Primary language with coroutines for async operations
- **Hilt/Dagger** - Dependency injection
- **Room** - Local database for persistence
- **Navigation Components** - Fragment navigation
- **ViewBinding** - Type-safe view access
- **StateFlow/LiveData** - Reactive state management
- **Coil** - Image loading for cover art

### Code Style
- Uses Spotless with ktfmt Dropbox style formatting
- License header required (see NOTICE file)
- C++ code uses Eclipse CDT formatting (eclipse-cdt.xml)

## Testing

### Test Structure
- Unit tests: `src/test/` directories
- Integration tests: `src/androidTest/` directories
- musikr module includes Robolectric tests

### Test Dependencies
- JUnit 4 for unit testing
- MockK for mocking
- Robolectric for Android unit tests
- Espresso for UI tests

## Key Development Notes

- **Custom Media3**: Uses patched Media3 ExoPlayer for enhanced playback features
- **Native Dependencies**: musikr module builds TagLib from source during compilation
- **Permission Handling**: Requires storage permissions for music file access
- **Background Services**: Foreground service for continuous music playback
- **Widget Support**: Multiple widget layouts with automatic sizing adaptation
- **ReplayGain**: Full support for MP3, FLAC, OGG, OPUS, and MP4 files