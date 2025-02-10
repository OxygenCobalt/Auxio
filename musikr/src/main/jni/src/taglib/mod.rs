mod ffi;
mod stream;

use ffi::bindings;
use std::collections::HashMap;
pub use stream::{RustStream, TagLibStream};

type XiphComments = HashMap<String, Vec<String>>;

pub enum File {
    Unknown {
        audio_properties: Option<AudioProperties>,
    },
    MP3 {
        audio_properties: Option<AudioProperties>,
    },
    FLAC {
        audio_properties: Option<AudioProperties>,
        xiph_comments: Option<XiphComments>,
    },
    MP4 {
        audio_properties: Option<AudioProperties>,
    },
    OGG {
        audio_properties: Option<AudioProperties>,
        xiph_comments: Option<XiphComments>,
    },
    Opus {
        audio_properties: Option<AudioProperties>,
        xiph_comments: Option<XiphComments>,
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

/// Audio properties of a media file
#[derive(Default)]
pub struct AudioProperties {
    pub length_in_milliseconds: i32,
    pub bitrate_in_kilobits_per_second: i32,
    pub sample_rate_in_hz: i32,
    pub number_of_channels: i32,
}

// Safe wrapper for FileRef that owns extracted data
pub struct FileRef {
    file: Option<File>,
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
        let file = file_ref.file_or().and_then(|file| {
            let audio_properties = file.audio_properties().map(|props| AudioProperties {
                length_in_milliseconds: props.length_in_milliseconds(),
                bitrate_in_kilobits_per_second: props.bitrate(),
                sample_rate_in_hz: props.sample_rate(),
                number_of_channels: props.channels(),
            });

            if let Some(vorbis_file) = file.as_vorbis() {
                let xiph_comments = vorbis_file
                    .xiph_comments()
                    .map(|comments| comments.field_list_map().to_hashmap());

                Some(File::OGG {
                    audio_properties,
                    xiph_comments,
                })
            } else if let Some(opus_file) = file.as_opus() {
                let xiph_comments = opus_file
                    .xiph_comments()
                    .map(|comments| comments.field_list_map().to_hashmap());

                Some(File::Opus {
                    audio_properties,
                    xiph_comments,
                })
            } else {
                Some(File::Unknown { audio_properties })
            }
        });

        // Clean up C++ objects - they will be dropped when file_ref is dropped
        drop(file_ref);

        Some(FileRef { file })
    }

    pub fn file(&self) -> &Option<File> {
        &self.file
    }
}
