# Native Code (C++) Component

## Overview

This directory contains the native C++ code that bridges between Java and TagLib. It enables fast and accurate metadata extraction from music files.

## Key Components

### JNI Implementation

**taglib_jni.cpp**
- Main JNI implementation
- Registers native methods
- Handles JNI environment setup

**JMetadataBuilder**
- Builds metadata objects from TagLib
- Extracts tags and properties
- Maps between TagLib and Java structures

**JTagMap**
- Maps between Java and C++ tag structures
- Provides bidirectional conversion

**JInputStream**
- Input stream adapter for TagLib
- Bridges between Java I/O and TagLib I/O

### Utility Classes

**JStringRef**
- Manages JNI string references
- Handles UTF conversions

**JByteArrayRef**
- Manages JNI byte array references
- Provides safe access to Java byte arrays

**JObjectRef**
- Manages JNI object references
- Provides RAII-style resource management

**JClassRef**
- Manages JNI class references
- Caches class and method lookups

**util.h**
- Utility functions for JNI
- Error handling and logging

### Build Configuration

**CMakeLists.txt**
- CMake build configuration
- Configures TagLib integration

**build_taglib.sh**
- Script to build TagLib from source
- Configures platform-specific options

**android.toolchain.cmake**
- Android-specific CMake toolchain

## TagLib Integration

TagLib is included as a subproject and built from source. The `taglib` directory contains the TagLib source code, which is a powerful and feature-rich library for reading and writing audio metadata.

## Build Process

1. The build starts with `build_taglib.sh`
2. TagLib is configured and built with CMake
3. The JNI wrapper is built against TagLib
4. The resulting library is packaged into the AAR

## JNI Interface

The native methods are exposed through `TagLibJNI.kt` in the Java code. The main functionalities include:

- Reading metadata from audio files
- Extracting embedded cover art
- Accessing audio properties (bitrate, sample rate, etc.)

## Extension Points

When extending the native code:

1. For new tag formats, update the TagLib integration
2. For new metadata fields, add them to JMetadataBuilder
3. For performance improvements, optimize the JNI bridge
4. For platform-specific optimizations, update the build configuration