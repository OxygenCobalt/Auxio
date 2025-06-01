# Covers Package

## Overview

The covers package provides functionality for discovering, extracting, and managing album cover art. It handles embedded covers in music files, external cover images, and persistent storage.

## Key Components

### Core Interfaces

**Covers** (`Covers.kt`)
- Generic interface for cover art management
- Provides methods to obtain and manage covers

**Cover** (`Covers.kt`)
- Represents a cover image
- Contains cover metadata and access methods

### Cover Sources

**EmbeddedCovers** (`embedded/EmbeddedCovers.kt`)
- Extracts covers embedded in music files
- Uses metadata extraction to access embedded images

**FSCovers** (`fs/FSCovers.kt`)
- Discovers cover art in the filesystem
- Searches for common cover image filenames

**ChainedCovers** (`chained/ChainedCovers.kt`)
- Combines multiple cover sources
- Provides fallback mechanisms

### Storage

**StoredCovers** (`stored/StoredCovers.kt`)
- Manages persistent storage of covers
- Handles caching and retrieval

**CoverStorage** (`stored/CoverStorage.kt`)
- Low-level cover storage operations
- Manages the cover storage directory

**Transcoding** (`stored/Transcoding.kt`)
- Handles image format conversion
- Optimizes covers for storage and display

### Identification

**CoverIdentifier** (`embedded/CoverIdentifier.kt`)
- Generates unique identifiers for covers
- Used for caching and referencing

## Cover Flow

1. During music indexing, covers are extracted or discovered
2. Covers are identified and stored
3. Songs reference covers by identifier
4. When needed, covers are retrieved from storage

## Key Features

- **Multiple sources** - Embedded, filesystem, and custom sources
- **Efficient storage** - Avoids duplicates through identification
- **Format optimization** - Transcodes images for optimal storage
- **Lazy loading** - Loads covers only when needed

## Extension Points

When extending the cover functionality:

1. For new cover sources, implement the Covers interface
2. For custom storage, extend or replace StoredCovers
3. For format-specific handling, update the Transcoding class
4. For performance improvements, optimize the identification and storage mechanisms