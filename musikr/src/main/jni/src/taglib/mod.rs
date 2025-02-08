mod ffi;
mod stream;

pub use stream::{RustStream, TagLibStream};
use ffi::bindings;

/// Audio properties of a media file
#[derive(Default)]
pub struct AudioProperties {
    pub length: i32,
    pub bitrate: i32,
    pub sample_rate: i32,
    pub channels: i32,
}

pub enum File {
    Unknown {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    MP3 {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    FLAC {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    MP4 {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    OGG {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    Opus {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    WAV {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    WavPack {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
    APE {
        title: Option<String>,
        audio_properties: Option<AudioProperties>,
    },
}

impl Default for File {
    fn default() -> Self {
        File::Unknown {
            title: None,
            audio_properties: None,
        }
    }
}

impl File {
    /// Get the title of the file, if available
    pub fn title(&self) -> Option<&str> {
        match self {
            File::Unknown { title, .. } |
            File::MP3 { title, .. } |
            File::FLAC { title, .. } |
            File::MP4 { title, .. } |
            File::OGG { title, .. } |
            File::Opus { title, .. } |
            File::WAV { title, .. } |
            File::WavPack { title, .. } |
            File::APE { title, .. } => title.as_deref()
        }
    }

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
        let inner = ffi::bindings::new_FileRef_from_stream(iostream);
        if ffi::bindings::FileRef_isNull(&inner) {
            return None;
        }

        // Extract data from C++ objects
        let file_ref = &inner;
        let file_ptr = ffi::bindings::FileRef_file(&file_ref);
        
        // Extract title
        let title = {
            let title = ffi::bindings::File_tag_title(file_ptr);
            if ffi::bindings::isEmpty(title) {
                None
            } else {
                let cstr = unsafe { ffi::bindings::to_string(title) };
                unsafe { std::ffi::CStr::from_ptr(cstr) }
                    .to_str()
                    .ok()
                    .map(|s| s.to_owned())
            }
        };

        // Extract audio properties
        let audio_properties = unsafe {
            let props_ptr = ffi::bindings::File_audioProperties(file_ptr);
            if !props_ptr.is_null() {
                Some(AudioProperties {
                    length: ffi::bindings::AudioProperties_length(props_ptr),
                    bitrate: ffi::bindings::AudioProperties_bitrate(props_ptr),
                    sample_rate: ffi::bindings::AudioProperties_sampleRate(props_ptr),
                    channels: ffi::bindings::AudioProperties_channels(props_ptr),
                })
            } else {
                None
            }
        };

        // Determine file type and create appropriate variant
        let file = {
            if ffi::bindings::File_isMPEG(file_ptr) {
                File::MP3 { title, audio_properties }
            } else if ffi::bindings::File_isFLAC(file_ptr) {
                File::FLAC { title, audio_properties }
            } else if ffi::bindings::File_isMP4(file_ptr) {
                File::MP4 { title, audio_properties }
            } else if ffi::bindings::File_isOpus(file_ptr) {
                File::Opus { title, audio_properties }
            } else if ffi::bindings::File_isOgg(file_ptr) {
                File::OGG { title, audio_properties }
            } else if ffi::bindings::File_isWAV(file_ptr) {
                File::WAV { title, audio_properties }
            } else if ffi::bindings::File_isWavPack(file_ptr) {
                File::WavPack { title, audio_properties }
            } else if ffi::bindings::File_isAPE(file_ptr) {
                File::APE { title, audio_properties }
            } else {
                File::Unknown { title, audio_properties }
            }
        };

        // Clean up C++ objects - they will be dropped when inner is dropped
        drop(inner);

        Some(FileRef { file })
    }

    pub fn file(&self) -> &File {
        &self.file
    }
} 