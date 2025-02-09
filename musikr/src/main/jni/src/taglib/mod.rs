mod ffi;
mod stream;

use std::pin::{pin, Pin};

use cxx::UniquePtr;
pub use stream::{RustStream, TagLibStream};
use ffi::bindings;

/// Audio properties of a media file
#[derive(Default)]
pub struct AudioProperties {
    pub length_in_milliseconds: i32,
    pub bitrate_in_kilobits_per_second: i32,
    pub sample_rate_in_hz: i32,
    pub number_of_channels: i32,
}

pub enum File {
    Unknown {
        audio_properties: Option<AudioProperties>,
    },
    MP3 {
        audio_properties: Option<AudioProperties>,
    },
    FLAC {
        audio_properties: Option<AudioProperties>,
    },
    MP4 {
        audio_properties: Option<AudioProperties>,
    },
    OGG {
        audio_properties: Option<AudioProperties>,
    },
    Opus {
        audio_properties: Option<AudioProperties>,
    },
    WAV {
        audio_properties: Option<AudioProperties>,
    },
    WavPack {
        audio_properties: Option<AudioProperties>,
    },
    APE {
        audio_properties: Option<AudioProperties>,
    },
}

impl Default for File {
    fn default() -> Self {
        File::Unknown {
            audio_properties: None,
        }
    }
}

impl File {
    /// Get the audio properties of the file, if available
    pub fn audio_properties(&self) -> Option<&AudioProperties> {
        match self {
            File::Unknown { audio_properties, .. } |
            File::MP3 { audio_properties, .. } |
            File::FLAC { audio_properties, .. } |
            File::MP4 { audio_properties, .. } |
            File::OGG { audio_properties, .. } |
            File::Opus { audio_properties, .. } |
            File::WAV { audio_properties, .. } |
            File::WavPack { audio_properties, .. } |
            File::APE { audio_properties, .. } => audio_properties.as_ref()
        }
    }
}

// Safe wrapper for FileRef that owns extracted data
pub struct FileRef {
    file: File,
}

impl FileRef {
    /// Create a new FileRef from a stream implementing TagLibStream
    pub fn from_stream<'a, T: TagLibStream + 'a>(stream: T) -> Option<Self> {
        // Create the RustStream wrapper
        let rust_stream = stream::RustStream::new(stream);
        
        // Convert to raw pointer for FFI
        let raw_stream = Box::into_raw(Box::new(rust_stream)) as *mut bindings::RustStream;
        
        // Create the RustIOStream C++ wrapper
        let iostream = unsafe { ffi::bindings::new_rust_iostream(raw_stream) };
        
        // Create FileRef from iostream
        let file_ref = ffi::bindings::new_FileRef_from_stream(iostream);
        if file_ref.is_null() {
            return None;
        }

        // Extract data from C++ objects
        let pinned_file_ref = unsafe { Pin::new_unchecked(file_ref.as_ref().unwrap()) };
        let file_ptr = pinned_file_ref.file();

        // Extract audio properties
        let audio_properties = {
            let pinned_file = unsafe { Pin::new_unchecked(&*file_ptr) };
            let props_ptr = pinned_file.audioProperties();
            if !props_ptr.is_null() {
                let props = unsafe { Pin::new_unchecked(&*props_ptr) };
                Some(AudioProperties {
                    length_in_milliseconds: props.lengthInMilliseconds(),
                    bitrate_in_kilobits_per_second: props.bitrate(),
                    sample_rate_in_hz: props.sampleRate(),
                    number_of_channels: props.channels(),
                })
            } else {
                None
            }
        };

        // Determine file type and create appropriate variant
        let file = unsafe {
            if ffi::bindings::File_isMPEG(file_ptr) {
                File::MP3 { audio_properties }
            } else if ffi::bindings::File_isFLAC(file_ptr) {
                File::FLAC { audio_properties }
            } else if ffi::bindings::File_isMP4(file_ptr) {
                File::MP4 { audio_properties }
            } else if ffi::bindings::File_isOpus(file_ptr) {
                File::Opus { audio_properties }
            } else if ffi::bindings::File_isOgg(file_ptr) {
                File::OGG { audio_properties }
            } else if ffi::bindings::File_isWAV(file_ptr) {
                File::WAV { audio_properties }
            } else if ffi::bindings::File_isWavPack(file_ptr) {
                File::WavPack { audio_properties }
            } else if ffi::bindings::File_isAPE(file_ptr) {
                File::APE { audio_properties }
            } else {
                File::Unknown { audio_properties }
            }
        };

        // Clean up C++ objects - they will be dropped when file_ref is dropped
        drop(file_ref);

        Some(FileRef { file })
    }

    pub fn file(&self) -> &File {
        &self.file
    }
} 