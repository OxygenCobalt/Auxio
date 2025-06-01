# Model Package

## Overview

The model package implements the core music entity model for Musikr. It provides the structures that represent songs, albums, artists, genres, and playlists, as well as their relationships.

## Key Components

### Core Interfaces

These interfaces are defined in the parent package (`org.oxycblt.musikr`) and implemented in the model package:

- **Library** - Read-only representation of the music library
- **MutableLibrary** - Modifiable version of the library
- **Song** - Represents a music track
- **Album** - Represents an album
- **Artist** - Represents an artist
- **Genre** - Represents a music genre
- **Playlist** - Represents a playlist

### Implementations

**LibraryImpl** (`LibraryImpl.kt`)
- Implementation of the Library and MutableLibrary interfaces
- Manages collections of songs, albums, artists, genres, and playlists
- Provides search and lookup functionality

**SongImpl** (`SongImpl.kt`)
- Implementation of the Song interface
- Contains metadata, file information, and relationships

**AlbumImpl** (`AlbumImpl.kt`)
- Implementation of the Album interface
- Contains songs, artists, and album metadata

**ArtistImpl** (`ArtistImpl.kt`)
- Implementation of the Artist interface
- Contains songs, albums, and artist metadata

**GenreImpl** (`GenreImpl.kt`)
- Implementation of the Genre interface
- Contains songs and genre metadata

**PlaylistImpl** (`PlaylistImpl.kt`)
- Implementation of the Playlist interface
- Contains ordered songs and playlist metadata

### Factory

**LibraryFactory** (`LibraryFactory.kt`)
- Creates library instances from pipeline data
- Builds the entity relationships
- Resolves duplicates and conflicts

## Entity Relationships

The music model implements a graph structure with these relationships:

- Songs belong to albums, artists, and genres
- Albums belong to artists
- Artists have songs and albums
- Genres have songs
- Playlists have songs

## Key Features

- **Identity resolution** - Properly identifies and merges duplicate entities
- **Relationship building** - Creates the connections between entities
- **Immutable interfaces** - Provides read-only access for safety
- **Mutable implementation** - Allows controlled modifications

## Extension Points

When extending the model:

1. For new entity types, create new interfaces and implementations
2. For new relationships, update the appropriate entities and LibraryFactory
3. For performance improvements, consider optimizing the graph structure
4. For custom sorting or grouping, extend the LibraryImpl functionality