# Filesystem Package

## Overview

The filesystem package provides abstraction layers for accessing music files on Android devices. It handles the complexities of the Storage Access Framework (SAF) and provides efficient caching mechanisms.

## Key Components

### Core Types

**MusicLocation** (`MusicLocation.kt`)
- Represents a location to search for music
- Can be a device storage location or content URI

**Path** (`Path.kt`)
- Abstract representation of a file path
- Handles both traditional file paths and content URIs

**Format** (`Format.kt`)
- Represents file formats and their properties
- Maps MIME types to file extensions

### Device Access

**DeviceFS** (`DeviceFS.kt`)
- Provides access to device storage
- Handles SAF permissions and access
- Performs filesystem traversal

**DeviceFSEntries** (`DeviceFSEntries.kt`)
- Represents filesystem entries (files and directories)
- Provides metadata about filesystem items

**DeviceFile** (in `DeviceFSEntries.kt`)
- Represents a music file on the device
- Contains file metadata needed for processing

### Caching

**FileTreeCache** (`FileTreeCache.kt`)
- Caches filesystem structure for faster indexing
- Provides incremental updates to avoid full rescans

### Path Interpretation

**Path Package** (`path/` directory)
- **DocumentPathFactory** - Creates paths from document URIs
- **MediaStorePathInterpreter** - Interprets MediaStore paths
- **VolumeManager** - Manages storage volumes

## Usage Flow

1. User provides `MusicLocation`s to scan
2. `DeviceFS` traverses these locations
3. `FileTreeCache` optimizes subsequent scans
4. Discovered files are passed to the pipeline for processing

## Key Features

- **Storage Access Framework** integration for modern Android storage access
- **Incremental scanning** to minimize resource usage
- **Path abstraction** to handle different Android storage mechanisms
- **Volume mapping** to resolve relative paths

## Extension Points

When extending the filesystem functionality:

1. For new storage types, implement appropriate Path implementations
2. For performance improvements, consider optimizing the FileTreeCache
3. For new file formats, update the Format class
4. For custom traversal strategies, extend DeviceFS