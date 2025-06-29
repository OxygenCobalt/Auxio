# Musikr Architecture Overview

Musikr is a highly opinionated, multi-threaded music loading library for Android that bypasses Android's MediaStore and uses the Storage Access Framework (SAF) with TagLib for enhanced music indexing capabilities.

## Core Design Principles

1. **Stateless API**: Side-effects are contained within the Storage layer
2. **No Defaults**: All parameters must be explicitly configured
3. **Pipeline Architecture**: Three-step processing pipeline for music loading
4. **Native Integration**: JNI bridge to TagLib for metadata extraction
5. **Coroutine-Based**: Uses Kotlin coroutines for async operations

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Application Layer                       │
│                    (Auxio or other apps)                     │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                         Musikr API                           │
│                    (Musikr.kt interface)                     │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Pipeline Architecture                      │
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────┐   │
│  │ ExploreStep  │ → │ ExtractStep  │ → │ EvaluateStep │   │
│  └──────────────┘   └──────────────┘   └──────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                ┌───────────┴───────────┐
                │                       │
                ▼                       ▼
┌──────────────────────┐   ┌──────────────────────────────┐
│   Storage Layer      │   │     Native Layer (JNI)       │
│  - Cache             │   │  - TagLib integration        │
│  - Covers            │   │  - Metadata extraction       │
│  - Playlists         │   │  - Format support            │
└──────────────────────┘   └──────────────────────────────┘
```

## Pipeline Steps

### 1. ExploreStep
- **Purpose**: Discover music files on the device
- **Process**:
  - Uses FS module to explore file system via SAF
  - Filters for audio MIME types and playlists
  - Checks cache for previously indexed files
  - Manages cover art retrieval
- **Output**: Stream of `Explored` items (songs/playlists to process)

### 2. ExtractStep
- **Purpose**: Extract metadata from discovered files
- **Process**:
  - Uses JNI bridge to TagLib for metadata extraction
  - Handles multiple tag formats (ID3v1/v2, MP4, Xiph, etc.)
  - Extracts audio properties (bitrate, duration, etc.)
  - Manages cover art extraction
- **Output**: Stream of `Extracted` items with full metadata

### 3. EvaluateStep
- **Purpose**: Build the music graph from extracted metadata
- **Process**:
  - Creates relationships between songs, albums, artists, genres
  - Resolves naming conflicts and duplicates
  - Applies user interpretation preferences
  - Builds final library structure
- **Output**: Complete `MutableLibrary` with all relationships

## Key Components

### Config & Storage
- **Config**: Main configuration container
  - `fs`: File system access configuration
  - `storage`: Persistent storage components
  - `interpretation`: Tag interpretation rules

- **Storage**: Side-effect laden components
  - `cache`: Metadata caching (Room database)
  - `covers`: Cover art storage and retrieval
  - `storedPlaylists`: User playlist management

### Data Models
- **Music**: Base interface for all music items
  - Uses UID system for unique identification
  - Supports MusicBrainz IDs
- **Song**: Individual track with full metadata
- **Album**: Collection of songs (includes EPs, singles, etc.)
- **Artist**: Can have explicit (album artist) and implicit (track artist) albums
- **Genre**: Grouping by musical genre
- **Playlist**: User-created or imported playlists

### File System (FS)
- Abstraction over Android's Storage Access Framework
- Supports multiple storage locations
- Handles permissions and URI management

### Native Layer (C++)
- TagLib integration for metadata extraction
- JNI wrappers for Java/Kotlin interop
- Support for various audio formats
- Custom patches for enhanced functionality

## Threading Model
- **Coroutine-based**: All operations use Kotlin coroutines
- **Multi-threaded extraction**: Parallel metadata extraction
- **Buffered channels**: For efficient pipeline communication
- **Dispatcher control**: Explicit IO dispatcher usage

## Error Handling
- **Pipeline exceptions**: Custom exception types
- **Graceful degradation**: Continue on individual file errors
- **Logging**: Comprehensive error logging
- **Cache invalidation**: Automatic stale cache detection

## Performance Optimizations
- **Caching**: Aggressive metadata caching
- **Parallel processing**: Multi-threaded file processing
- **Lazy evaluation**: On-demand cover loading
- **Memory efficiency**: Streaming pipeline architecture