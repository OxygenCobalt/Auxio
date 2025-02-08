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

// Store extracted tag data instead of C++ reference
#[derive(Default)]
pub struct File {
    title: Option<String>,
    audio_properties: Option<AudioProperties>,
}

impl File {
    /// Get the title of the file, if available
    pub fn title(&self) -> Option<&str> {
        self.title.as_deref()
    }

    /// Get the audio properties of the file, if available
    pub fn audio_properties(&self) -> Option<&AudioProperties> {
        self.audio_properties.as_ref()
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

        // Clean up C++ objects - they will be dropped when inner is dropped
        drop(inner);

        // Create File with extracted data
        let file = File { 
            title,
            audio_properties,
        };

        Some(FileRef { file })
    }

    pub fn file(&self) -> &File {
        &self.file
    }
} 