# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Auxio is a simple, rational music player for Android built on Media3 ExoPlayer. It features gapless playback, ReplayGain support, Android Auto integration, and advanced music indexing capabilities.

## Essential Build Commands

### Prerequisites
- Unix-based system (cannot build on Windows)
- Install `cmake` and `ninja-build`
- Clone with submodules: `git clone --recurse-submodules`

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean build

# Install debug build on device
./gradlew installDebug
```

### Code Quality Commands
```bash
# Check code formatting (MUST pass before committing)
./gradlew spotlessCheck

# Apply code formatting fixes
./gradlew spotlessApply

# Run Android lint
./gradlew lint

# Run lint with automatic fixes
./gradlew lintFix
```

### Testing Commands
```bash
# Run unit tests
./gradlew test

# Run debug unit tests
./gradlew testDebugUnitTest

# Run musikr module tests specifically
./gradlew musikr:testDebug

# Run instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest
```

### Development Workflow
```bash
# Before committing: format code and run checks
./gradlew spotlessApply
./gradlew check

# Run a single test class
./gradlew test --tests "org.oxycblt.auxio.YourTestClass"

# Run a single test method
./gradlew test --tests "org.oxycblt.auxio.YourTestClass.yourTestMethod"
```

## Architecture Overview

### High-Level Architecture
- **Pattern**: MVVM (Model-View-ViewModel) with Dagger Hilt dependency injection
- **Navigation**: Single Activity architecture with Navigation Component
- **UI**: Material Design 3 with edge-to-edge display
- **Data**: Repository pattern with Room database and custom Musikr library
- **Playback**: MediaBrowserServiceCompat with vendored Media3/ExoPlayer

### Key Components

#### Service Architecture
- `AuxioService` - Main MediaBrowserService for background playback
- `PlaybackServiceFragment` - Handles all playback operations
- `MusicServiceFragment` - Manages music browsing and library

#### Data Layer
- `MusicRepository` - Primary interface for music data access
- `PersistenceRepository` - Handles playback state and playlists
- `Musikr` - Custom music indexing library (separate module)
  - Bypasses MediaStore limitations
  - Supports advanced metadata and tags
  - Background indexing with progress

#### UI Layer Structure
```
app/src/main/java/org/oxycblt/auxio/
├── MainActivity.kt          # Single activity host
├── MainFragment.kt         # Main container with bottom sheets
├── home/                   # Home screen tabs (songs, albums, artists, etc.)
├── detail/                 # Detail screens for albums/artists
├── playback/              # Playback UI and controls
├── search/                # Search functionality
├── settings/              # Preferences screens
├── list/                  # Reusable list components
└── music/                 # Music data models and repository
```

#### State Management
- StateFlow/MutableStateFlow for reactive state
- Shared ViewModels between fragments
- Event wrapper pattern for one-time UI events
- Lifecycle-aware observers

### Navigation Flow
1. `MainActivity` hosts all navigation
2. Two navigation graphs: `inner.xml` (main) and `outer.xml` (onboarding)
3. Bottom sheet for playback UI
4. Dialog destinations for popups

### Dependency Injection Setup
- `@HiltAndroidApp` on Auxio application class
- `@AndroidEntryPoint` on activities/fragments
- Modules: `MusicModule`, `PlaybackModule`, `UIModule`
- Scoped with `@Singleton` for app-wide instances

## Important Development Notes

### Code Style
- Kotlin code formatted with ktfmt (Dropbox style)
- C++ code uses Eclipse CDT formatting
- Spotless enforces formatting automatically
- License headers required (enforced by Spotless)

### Git Workflow
- Default branch: `dev` (development)
- PRs should target `dev` branch
- `master` contains stable releases only

### Architectural Constraints
- Stick to F-Droid inclusion guidelines
- No proprietary dependencies
- Feature additions unlikely to be accepted
- Major UI changes discouraged

### Testing Requirements
- Test all changes thoroughly
- Unit tests for business logic
- Consider edge cases for music metadata
- Test on different Android versions (min SDK 24)

### Key Libraries
- AndroidX Navigation, Room, Media3
- Dagger Hilt for DI
- Kotlin Coroutines for async
- Coil for image loading
- Material Design Components 3

### Custom Modifications
- Vendored/patched Media3 in `media/` directory
- Custom patches for ExoPlayer features
- Modified libraries shouldn't be updated directly