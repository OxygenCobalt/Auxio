mod ffi;
mod stream;

use ffi::bindings;
use std::ffi::CStr;
use std::pin::{pin, Pin};
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
            let mpeg_file = ffi::bindings::File_asMPEG(file_ptr);
            if !mpeg_file.is_null() {
                return Some(FileRef {
                    file: File::MP3 { audio_properties }
                });
            }

            let flac_file = ffi::bindings::File_asFLAC(file_ptr);
            if !flac_file.is_null() {
                return Some(FileRef {
                    file: File::FLAC { audio_properties, xiph_comments: None }
                });
            }

            let mp4_file = ffi::bindings::File_asMP4(file_ptr);
            if !mp4_file.is_null() {
                return Some(FileRef {
                    file: File::MP4 { audio_properties }
                });
            }

            let wav_file = ffi::bindings::File_asWAV(file_ptr);
            if !wav_file.is_null() {
                return Some(FileRef {
                    file: File::WAV { audio_properties }
                });
            }

            let wavpack_file = ffi::bindings::File_asWavPack(file_ptr);
            if !wavpack_file.is_null() {
                return Some(FileRef {
                    file: File::WavPack { audio_properties }
                });
            }

            let ape_file = ffi::bindings::File_asAPE(file_ptr);
            if !ape_file.is_null() {
                return Some(FileRef {
                    file: File::APE { audio_properties }
                });
            }

            let vorbis_file = ffi::bindings::File_asVorbis(file_ptr);
            if !vorbis_file.is_null() {
                let pinned_vorbis_file =  Pin::new_unchecked(&*vorbis_file);
                let xiph_comments = pinned_vorbis_file.tag();
                let pinned_xiph_comments = Pin::new_unchecked(&*xiph_comments);
                let xiph_map = pinned_xiph_comments.fieldListMap();
                let pinned_xiph_map = Pin::new_unchecked(xiph_map);
                let xiph_comments = ffi::bindings::SimplePropertyMap_to_vector(pinned_xiph_map);
                
                let mut xiph_safe_map = XiphComments::new();
                for property in xiph_comments.iter() {
                    let pinned_property = Pin::new_unchecked(property);
                    let tag_key = pinned_property.key();
                    let pinned_key = Pin::new_unchecked(&*tag_key);
                    let c_str = pinned_key.toCString(true);
                    let key = CStr::from_ptr(c_str).to_string_lossy().to_string();

                    let tag_values = pinned_property.value();
                    let pinned_values = Pin::new_unchecked(&*tag_values);
                    let cxx_vec_values = ffi::bindings::StringList_to_vector(pinned_values);
                    let values = cxx_vec_values.iter().map(|value|  {
                        let pinned_value = Pin::new_unchecked(value);
                        let c_str = pinned_value.toCString(true);
                        CStr::from_ptr(c_str).to_string_lossy().to_string()
                    }).collect();

                    xiph_safe_map.insert(key, values);
                }

                return Some(FileRef {
                    file: File::OGG { audio_properties, xiph_comments: Some(xiph_safe_map) }
                });
            }

            let opus_file = ffi::bindings::File_asOpus(file_ptr);
            if !opus_file.is_null() {
                let pinned_opus_file = Pin::new_unchecked(&*opus_file);
                let xiph_comments = pinned_opus_file.tag();
                let pinned_xiph_comments = Pin::new_unchecked(&*xiph_comments);
                let xiph_map = pinned_xiph_comments.fieldListMap();
                let pinned_xiph_map = Pin::new_unchecked(xiph_map);
                let xiph_comments = ffi::bindings::SimplePropertyMap_to_vector(pinned_xiph_map);
                
                let mut xiph_safe_map = XiphComments::new();
                for property in xiph_comments.iter() {
                    let pinned_property = Pin::new_unchecked(property);
                    let tag_key = pinned_property.key();
                    let pinned_key = Pin::new_unchecked(&*tag_key);
                }
                
                return Some(FileRef {
                    file: File::Opus { audio_properties, xiph_comments: Some(xiph_safe_map) }
                });
            }

            File::Unknown { audio_properties }
        };

        // Clean up C++ objects - they will be dropped when file_ref is dropped
        drop(file_ref);

        Some(FileRef { file })
    }

    pub fn file(&self) -> &File {
        &self.file
    }
}
