# Metadata Package

## Overview

The metadata package provides the native bridge to TagLib and handles extraction of metadata from music files. This is a critical component that enables accurate and comprehensive tag parsing.

## Key Components

### JNI Interface

**TagLibJNI** (`TagLibJNI.kt`)
- Loads the native library
- Provides JNI methods for interacting with TagLib
- Handles native memory management

**NativeTagMap** (`NativeTagMap.kt`)
- Maps between native C++ and Java tag structures
- Provides type conversion utilities

**NativeInputStream** (`NativeInputStream.kt`)
- Provides a stream interface for TagLib to read file data
- Bridges between Java I/O and native code

### Metadata Extraction

**MetadataExtractor** (`MetadataExtractor.kt`)
- Extracts metadata from music files using TagLib
- Handles error cases and format-specific extractions
- Manages native resource lifecycle

**Metadata** (`Metadata.kt`)
- Container for extracted audio metadata
- Includes both tags and audio properties

**Properties** (part of `Metadata.kt`)
- Audio-specific properties like duration, sample rate, etc.
- Format-specific information

## Native Implementation

The C++ side of the metadata extraction is implemented in the `cpp` directory:

- **taglib_jni.cpp** - Main JNI implementation
- **JMetadataBuilder** - Builds metadata objects from TagLib
- **JTagMap** - Maps between Java and C++ tag structures
- **JInputStream** - Input stream adapter for TagLib

## Usage Flow

1. File is discovered in the exploration step
2. MetadataExtractor is called with the file
3. NativeInputStream provides file access to TagLib
4. TagLib parses the file's metadata
5. JMetadataBuilder converts TagLib structures to Java objects
6. Metadata object is returned to the pipeline

## Extension Points

When extending the metadata functionality:

1. For new tag formats, extend the native JNI code in `taglib_jni.cpp`
2. For new metadata fields, add them to both Java and native interfaces
3. For performance improvements, consider caching strategies in MetadataExtractor
4. Always ensure proper resource management with native code