# Tag Package

## Overview

The tag package handles parsing, interpreting, and managing music metadata tags. It provides structures for representing various tag types and interpreting them according to user preferences.

## Key Components

### Tag Parsing

**TagParser** (`parse/TagParser.kt`)
- Parses raw tag data from the metadata extractor
- Handles different tag formats and structures
- Normalizes tags across different formats

**ParsedTags** (`parse/ParsedTags.kt`)
- Container for parsed metadata tags
- Provides structured access to tag values

**TagFields** (`parse/TagFields.kt`)
- Defines standard tag fields and their mapping
- Handles format-specific field names

### Tag Interpretation

**TagInterpreter** (`interpret/TagInterpreter.kt`)
- Interprets tags based on user preferences
- Resolves ambiguities in tag data
- Creates structured music metadata

**Naming** (`interpret/Naming.kt`)
- Handles naming conventions for music entities
- Provides fallbacks for missing metadata

**Separators** (`interpret/Separators.kt`)
- Handles multi-value separators in tags
- Splits tags with multiple artists, genres, etc.

**PreMusic** (`interpret/PreMusic.kt`)
- Intermediate representation during tag interpretation
- Holds partially processed music data

### Tag Types

**Name** (`Name.kt`)
- Represents artist, album, and track names
- Handles normalization and comparison

**Date** (`Date.kt`)
- Represents release dates
- Handles various date formats and partial dates

**Disc** (`Disc.kt`)
- Represents disc and track numbers
- Handles various numbering formats

**ReleaseType** (`ReleaseType.kt`)
- Represents album release types (album, EP, single, etc.)
- Handles format-specific release type tags

### Format-Specific Handling

**ID3** (`format/ID3.kt`)
- Handles ID3v1 and ID3v2 tag specifics

**Vorbis** (`format/Vorbis.kt`)
- Handles Vorbis Comment tag specifics

## Tag Flow

1. Raw tags extracted by the metadata extractor
2. TagParser normalizes tags into ParsedTags
3. TagInterpreter interprets tags based on user preferences
4. Interpreted tags are used to build the music library

## Extension Points

When extending the tag functionality:

1. For new tag types, extend the TagParser and relevant format handlers
2. For new interpretation rules, modify the TagInterpreter
3. For format-specific handling, add new format classes
4. For new metadata types, add appropriate structures and update the tag flow