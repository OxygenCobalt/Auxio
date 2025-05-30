# Pipeline Package

## Overview

The pipeline package implements a three-stage processing pipeline for music indexing in Musikr. It provides a concurrent, efficient way to discover, extract metadata from, and process music files.

## Key Components

### Pipeline Steps

1. **ExploreStep** (`ExploreStep.kt`)
   - Discovers music files on the device
   - Interfaces with filesystem caches
   - Produces `Explored` items for processing

2. **ExtractStep** (`ExtractStep.kt`)
   - Extracts metadata from discovered files
   - Uses TagLib via JNI for metadata extraction
   - Produces `Extracted` items with parsed tags

3. **EvaluateStep** (`EvaluateStep.kt`)
   - Processes extracted data to build the music library
   - Creates the song/album/artist/genre relationships
   - Resolves cover art references

### Pipeline Items

The `PipelineItem.kt` defines the data types that flow through the pipeline:

- **Explored** - Files discovered in the filesystem
  - `NewSong` - A newly discovered music file
  - `RawPlaylist` - A discovered playlist file

- **Extracted** - Files with extracted metadata
  - `RawSong` - A song with extracted metadata and tags
  - `InvalidSong` - A file that couldn't be processed

### Exception Handling

`PipelineException.kt` defines exceptions specific to the pipeline process.

## Flow Utilities

`FlowUtil.kt` provides utility functions for working with Kotlin Flows in the pipeline context, including:

- `distributedMap` - Parallel mapping operations
- Flow merging and buffering utilities

## Pipeline Flow

```
MusicLocation → ExploreStep → ExtractStep → EvaluateStep → Library
```

Data flows through the pipeline using Kotlin's Flow API, with extensive use of parallel processing and buffering to optimize performance.

## Extension Points

When extending the pipeline:

1. Consider the stage where your functionality belongs
2. Follow the existing pattern of `Flow<In> → Flow<Out>` transformations
3. Use the appropriate dispatchers for I/O vs CPU work
4. Buffer appropriately to manage backpressure