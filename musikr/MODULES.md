# Musikr Module Breakdown

This document provides a detailed breakdown of all modules and packages within the Musikr library.

## Package Structure

```
org.oxycblt.musikr/
├── cache/          # Metadata caching system
├── covers/         # Cover art management
├── fs/             # File system abstraction
├── graph/          # Music relationship graph
├── metadata/       # JNI metadata extraction
├── model/          # Concrete implementations
├── pipeline/       # Processing pipeline
├── playlist/       # Playlist management
├── tag/            # Tag interpretation
├── track/          # Change tracking
└── util/           # Utility functions
```

## Core Modules

### Main Entry Points

#### `Musikr.kt`
- Main interface for the library
- Factory methods for creating instances
- Defines `LibraryResult` and `IndexingProgress`

#### `Config.kt`
- Configuration data classes
- `Config`: Main configuration container
- `Storage`: Side-effect components
- `Interpretation`: Tag parsing rules

#### `Music.kt`
- Core music data interfaces
- `Music`: Base interface with UID system
- `Song`, `Album`, `Artist`, `Genre`, `Playlist` interfaces
- UID implementation with MusicBrainz support

#### `Library.kt`
- Library container interfaces
- `Library`: Read-only music library
- `MutableLibrary`: Mutable library for updates

## Pipeline Modules (`pipeline/`)

### `PipelineItem.kt`
- Data classes for pipeline communication
- `Explored`: Items found during exploration
- `Extracted`: Items with extracted metadata
- `NewSong`, `RawSong`, `RawPlaylist` types

### `ExploreStep.kt`
- First pipeline stage
- Discovers music files using FS module
- Integrates with cache for optimization
- Handles cover pre-loading

### `ExtractStep.kt`
- Second pipeline stage
- Calls native metadata extraction
- Manages metadata caching
- Handles extraction failures

### `EvaluateStep.kt`
- Final pipeline stage
- Builds music graph from extracted data
- Resolves relationships between items
- Creates final library structure

### `FlowUtil.kt`
- Coroutine flow utilities
- `distributedMap`: Parallel processing helper

## File System Module (`fs/`)

### `FS.kt`
- File system abstraction interface
- SAF integration for Android
- Multi-location support

### `Path.kt`
- Path representation and manipulation
- Volume and document separation
- Display-friendly path formatting

### `Location.kt`
- Storage location management
- Permission handling
- URI to path conversion

### `Format.kt`
- Audio format enumeration
- MIME type mapping
- Format detection utilities

## Metadata Module (`metadata/`)

### `TagLibJNI.kt`
- JNI interface to native code
- Native method declarations

### `MetadataExtractor.kt`
- High-level metadata extraction
- Format-specific extraction routing
- Error handling and fallbacks

### `Metadata.kt`
- Metadata container classes
- Properties and tag maps
- Type-safe metadata access

### Native Integration
- `NativeInputStream.kt`: Java InputStream to C++ bridge
- `NativeTagMap.kt`: Tag data transfer object

## Cache Module (`cache/`)

### `Cache.kt`
- Cache interfaces and data types
- `CacheResult`: Hit/Miss/Stale states
- Read/write operations

### Implementation (not shown in detail)
- Room database integration
- Metadata persistence
- Cache invalidation logic

## Cover Module (`covers/`)

### `Covers.kt`
- Cover art management interfaces
- `CoverResult`: Cover loading states
- Cleanup operations

### `Cover.kt`
- Cover identifier types
- Format information
- Collection utilities

## Tag Module (`tag/`)

### Tag Types
- `Date.kt`: Date parsing and representation
- `Disc.kt`: Disc number handling
- `Name.kt`: Name types (Known/Unknown)
- `ReleaseType.kt`: Album type classification
- `ReplayGainAdjustment.kt`: ReplayGain data

### Interpretation (`tag/interpret/`)
- `Naming.kt`: Name construction rules
- `Separators.kt`: Multi-value tag parsing

## Model Module (`model/`)

Concrete implementations of music interfaces:
- `SongImpl.kt`: Song data implementation
- `AlbumImpl.kt`: Album with song relationships
- `ArtistImpl.kt`: Artist with album relationships
- `GenreImpl.kt`: Genre grouping implementation
- `PlaylistImpl.kt`: Playlist container
- `LibraryImpl.kt`: Complete library implementation
- `LibraryFactory.kt`: Factory for creating library instances

## Graph Module (`graph/`)

### `MusicGraph.kt`
- Relationship graph builder
- Artist-album-song connections
- Genre associations
- Duplicate resolution

## Playlist Module (`playlist/`)

### `ExternalPlaylistManager.kt`
- External playlist loading
- Format detection and parsing

### `PlaylistFile.kt`
- Playlist file abstraction
- Common playlist operations

### `m3u/M3U.kt`
- M3U/M3U8 playlist support
- Path resolution
- Encoding handling

### `db/StoredPlaylists.kt`
- Database playlist interface
- CRUD operations
- Persistence management

## Track Module (`track/`)

### `UpdateTracker.kt`
- File system change detection
- Modification tracking
- Update notification

### `LocationObserver.kt`
- Storage location monitoring
- Permission change handling

## Utility Module (`util/`)

### `LangUtil.kt`
- Language/locale utilities
- String manipulation helpers

### `ParseUtil.kt`
- Parsing utilities
- Number parsing with validation
- UUID parsing

## Native Layer (C++)

### JNI Wrappers
- `JByteArrayRef`: Byte array management
- `JClassRef`: Class reference caching
- `JInputStream`: Stream adapter
- `JMetadataBuilder`: Metadata assembly
- `JObjectRef`: Object lifecycle management
- `JStringRef`: String conversion
- `JTagMap`: Tag data transfer

### Main JNI Entry
- `taglib_jni.cpp`: Native method implementations
- Format-specific parsing functions
- Error handling and logging

### Build System
- `CMakeLists.txt`: Native build configuration
- `build_taglib.sh`: TagLib compilation script
- `android.toolchain.cmake`: Android-specific toolchain

## Testing

### Unit Tests (`test/`)
- Tag parsing tests
- Date/Disc/ReleaseType validation
- Utility function tests

### Integration
- Full pipeline testing
- Cache behavior verification
- Cover loading tests