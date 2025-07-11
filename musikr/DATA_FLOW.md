# Musikr Data Flow Diagram

This document illustrates how data flows through the Musikr music loading pipeline from file discovery to final library creation.

## Overview Flow

```
┌─────────────────┐
│  User Request   │
│  Musikr.run()   │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────┐
│                    EXPLORE PHASE                         │
│                                                          │
│  File System ──► SAF APIs ──► Audio Files               │
│       │                           │                      │
│       └──► Cached Files ◄─────────┘                     │
│                  │                                       │
│                  ▼                                       │
│           Cover Cache ──► Explored Items                │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    EXTRACT PHASE                         │
│                                                          │
│  Explored Items ──► JNI Bridge ──► TagLib               │
│                          │            │                  │
│                          ▼            ▼                  │
│                    Native Code   Tag Parsing            │
│                          │            │                  │
│                          └────────────┘                  │
│                               │                          │
│                               ▼                          │
│                        Metadata Maps                     │
│                               │                          │
│                               ▼                          │
│                      Extracted Items                     │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                   EVALUATE PHASE                         │
│                                                          │
│  Extracted Items ──► Graph Builder ──► Relationships    │
│                           │                │             │
│                           ▼                ▼             │
│                    Name Resolution   UID Generation      │
│                           │                │             │
│                           └────────────────┘             │
│                                  │                       │
│                                  ▼                       │
│                           MutableLibrary                 │
└─────────────────────────────────────────────────────────┘
```

## Detailed Phase Flows

### 1. Explore Phase Data Flow

```
START: FS.explore()
         │
         ▼
┌─────────────────┐
│ Storage Access  │
│   Framework     │
└────────┬────────┘
         │ DocumentFile
         ▼
┌─────────────────┐     ┌─────────────────┐
│  Audio Filter   │     │ Playlist Filter │
│ (audio/* MIME)  │     │  (M3U MIME)     │
└────────┬────────┘     └────────┬────────┘
         │                       │
         ▼                       ▼
┌─────────────────────────────────────────┐
│          Cache Lookup                    │
│  ┌─────────────┐  ┌──────────────┐     │
│  │ Cache Hit   │  │ Cache Miss   │     │
│  │ (Metadata)  │  │ (New File)   │     │
│  └──────┬──────┘  └──────┬───────┘     │
│         │                │              │
│         ▼                ▼              │
│  ┌─────────────┐  ┌──────────────┐     │
│  │Cover Lookup │  │  Mark for     │     │
│  │   if Hit    │  │  Extraction   │     │
│  └──────┬──────┘  └──────┬───────┘     │
└─────────┴─────────────────┴─────────────┘
                    │
                    ▼
            Explored Stream
          (Flow<Explored>)
```

### 2. Extract Phase Data Flow

```
START: Explored Items
         │
         ▼
┌─────────────────┐
│ Group by Type   │
│ - NewSong       │
│ - RawSong       │
│ - RawPlaylist   │
└────────┬────────┘
         │
    ┌────┴────┐
    │ NewSong │
    └────┬────┘
         │
         ▼
┌─────────────────────────────────────┐
│        JNI Metadata Call            │
│                                     │
│  Kotlin ──► JNI ──► C++ ──► TagLib │
│    │                         │      │
│    └─────── Results ◄────────┘      │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│      Metadata Processing            │
│  ┌──────────────┐ ┌──────────────┐ │
│  │ Properties   │ │    Tags      │ │
│  │ - Duration   │ │ - Title      │ │
│  │ - Bitrate    │ │ - Artist     │ │
│  │ - Format     │ │ - Album      │ │
│  └──────────────┘ └──────────────┘ │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│         Cache Update                │
│  - Write metadata to cache          │
│  - Store cover reference            │
└─────────────────────────────────────┘
         │
         ▼
    Extracted Stream
   (Flow<Extracted>)
```

### 3. Evaluate Phase Data Flow

```
START: Extracted Items
         │
         ▼
┌─────────────────────────────────────┐
│      Collect All Items              │
│  - Songs                            │
│  - Playlists                        │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│    UID Generation/Resolution        │
│  ┌──────────────┐ ┌──────────────┐ │
│  │ MusicBrainz  │ │ Hash-based   │ │
│  │    UIDs      │ │    UIDs      │ │
│  └──────────────┘ └──────────────┘ │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│      Name Interpretation            │
│  - Apply naming rules               │
│  - Parse separators                 │
│  - Resolve conflicts                │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│           Graph Building                     │
│                                              │
│  Songs ──► Albums ──► Artists               │
│    │         │          │                    │
│    └──────► Genres ◄────┘                    │
│                                              │
│  Relationships:                              │
│  - Song.album → Album                        │
│  - Song.artists → List<Artist>               │
│  - Album.artists → List<Artist>              │
│  - Artist.albums → List<Album>               │
└──────────────┬──────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Playlist Resolution            │
│  - Match paths to songs             │
│  - Create playlist objects          │
└────────┬────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│        Final Library                │
│  MutableLibrary {                   │
│    songs: List<Song>                │
│    albums: List<Album>              │
│    artists: List<Artist>            │
│    genres: List<Genre>              │
│    playlists: List<Playlist>       │
│  }                                  │
└─────────────────────────────────────┘
```

## Key Data Transformations

### File → Explored
```
DocumentFile {              Explored {
  uri: Uri         ──►       file: DocumentFile
  mimeType: String           // + cache status
}                           }
```

### Explored → Extracted
```
NewSong {                   ExtractedSong {
  file: DocumentFile  ──►     file: DocumentFile
  addedMs: Long               properties: Properties
}                             tags: Map<String, List<String>>
                              cover: Cover?
                              addedMs: Long
                            }
```

### Extracted → Library Items
```
ExtractedSong {             Song {
  tags: Map           ──►     uid: UID
  properties: Props           name: Name
}                             album: Album
                              artists: List<Artist>
                              // ... full metadata
                            }
```

## Caching Data Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   File URI  │ ──► │ Cache Key   │ ──► │   Cached    │
│             │     │ Generation  │     │  Metadata   │
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                          ┌────────────────────┴────────────┐
                          │                                 │
                    ┌─────▼─────┐                    ┌─────▼─────┐
                    │   Valid    │                    │   Stale   │
                    │ Use Cached │                    │ Re-extract│
                    └───────────┘                    └───────────┘
```

## Cover Art Data Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ Embedded    │     │  Cover ID   │     │   Cover     │
│ Cover Data  │ ──► │ Generation  │ ──► │   Cache     │
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │ Cover Ref   │
                                        │ in Library  │
                                        └─────────────┘
```

## Error Handling Flow

```
┌─────────────┐
│   Error     │
│  Occurs     │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────┐
│      Error Classification       │
│  ┌──────────┐  ┌─────────────┐ │
│  │ Critical │  │ Recoverable │ │
│  └────┬─────┘  └──────┬──────┘ │
└───────┴────────────────┴────────┘
        │                │
        ▼                ▼
┌──────────────┐ ┌───────────────┐
│ Stop Pipeline│ │ Log & Continue│
│ Throw Error  │ │ Skip Item     │
└──────────────┘ └───────────────┘
```

## Progress Reporting Flow

```
Pipeline Stage ──► Progress Event ──► Callback
                        │
                        ▼
                IndexingProgress
                ├── Songs(loaded, explored)
                └── Indeterminate
```