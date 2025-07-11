# Musikr Quick Reference Guide

## What is Musikr?
A custom Android music indexing library that bypasses MediaStore, using SAF + TagLib for enhanced metadata extraction and music organization.

## Key Features
- **No MediaStore**: Direct file access via Storage Access Framework
- **Advanced Metadata**: Full TagLib integration for rich tag support
- **Multi-threaded**: Parallel processing with Kotlin coroutines
- **Smart Caching**: Efficient metadata and cover art caching
- **MusicBrainz Support**: Proper handling of MB IDs
- **Flexible Architecture**: Pipeline-based processing

## Quick Start

```kotlin
// 1. Configure storage components
val storage = Storage(
    cache = myCache,
    covers = myCoverManager,
    storedPlaylists = myPlaylistDb
)

// 2. Set interpretation rules
val interpretation = Interpretation(
    naming = Naming.SYSTEM,  // or DISPLAY
    separators = Separators.default()
)

// 3. Create config
val config = Config(
    fs = myFileSystem,
    storage = storage,
    interpretation = interpretation
)

// 4. Run musikr
val musikr = Musikr.new(context, config)
val result = musikr.run { progress ->
    // Handle progress updates
}

// 5. Use the library
val library = result.library
println("Found ${library.songs.size} songs")

// 6. Clean up
result.cleanup()
```

## Core Concepts

### Pipeline Architecture
1. **Explore**: Find music files
2. **Extract**: Get metadata via TagLib
3. **Evaluate**: Build music graph

### Data Model
- **Song**: Individual tracks with full metadata
- **Album**: Collections of songs (includes EPs, singles)
- **Artist**: Can have both album and track credits
- **Genre**: Musical style groupings
- **Playlist**: User-created or imported (M3U)

### UID System
- Unique identifiers for all music items
- Supports MusicBrainz IDs
- Hash-based fallback for untagged files

## File Structure
```
musikr/
├── src/main/
│   ├── java/           # Kotlin sources
│   └── cpp/            # Native TagLib integration
├── ARCHITECTURE.md     # System design overview
├── MODULES.md          # Detailed module docs
└── DATA_FLOW.md        # Data flow diagrams
```

## Common Tasks

### Custom Cache Implementation
```kotlin
class MyCache : MutableCache {
    override suspend fun read(file: DocumentFile): CacheResult {
        // Your implementation
    }
    
    override suspend fun write(extracted: List<ExtractedSong>) {
        // Your implementation
    }
}
```

### Custom Cover Storage
```kotlin
class MyCovers : MutableCovers<MyCover> {
    override suspend fun obtain(id: String): CoverResult<MyCover> {
        // Your implementation
    }
    
    override suspend fun write(cover: Cover): String {
        // Your implementation
    }
}
```

### Progress Monitoring
```kotlin
musikr.run { progress ->
    when (progress) {
        is IndexingProgress.Songs -> {
            println("Loaded ${progress.loaded}/${progress.explored}")
        }
        is IndexingProgress.Indeterminate -> {
            println("Building library...")
        }
    }
}
```

## Important Notes

1. **No Windows Support**: Requires Unix-based build system
2. **GPL v3 License**: Viral license - entire project must be GPL
3. **Unstable API**: Interface may change between versions
4. **Thread Safety**: All operations are coroutine-safe
5. **Memory Usage**: Streaming architecture for large libraries

## Debugging Tips

- Enable TagLib debug output for metadata issues
- Check cache validity for performance problems
- Monitor coroutine dispatchers for threading issues
- Use flow operators for custom processing

## Performance Tuning

- Adjust `distributedMap` parallelism (default: 8)
- Configure channel buffer sizes
- Optimize cache implementation
- Pre-warm cover cache for common albums

## See Also
- [ARCHITECTURE.md](ARCHITECTURE.md) - System design
- [MODULES.md](MODULES.md) - Module details
- [DATA_FLOW.md](DATA_FLOW.md) - Data flow diagrams
- [README.md](README.md) - Basic information