use std::ffi::CStr;
use std::pin::Pin;
use std::string::ToString;
use std::collections::HashMap;

#[cxx::bridge]
pub(crate) mod bindings {
    unsafe extern "C++" {
        include!("taglib/taglib.h");
        include!("taglib/tstring.h");
        include!("taglib/tstringlist.h");
        include!("taglib/vorbisfile.h");
        include!("taglib/xiphcomment.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/tk_shim.hpp");

        #[namespace = "TagLib"]
        type FileRef;
        fn isNull(self: Pin<&FileRef>) -> bool;
        fn file(self: Pin<&FileRef>) -> *mut BaseFile;

        #[namespace = "taglib_shim"]
        type RustIOStream;
        // Create a FileRef from an iostream
        #[namespace = "taglib_shim"]
        unsafe fn new_rust_iostream(stream: *mut RustStream) -> UniquePtr<RustIOStream>;

        #[namespace = "taglib_shim"]
        type RustStream;
        #[namespace = "taglib_shim"]
        fn new_FileRef_from_stream(stream: UniquePtr<RustIOStream>) -> UniquePtr<FileRef>;

        #[namespace = "TagLib"]
        #[cxx_name = "File"]
        type BaseFile;
        fn audioProperties(self: Pin<&BaseFile>) -> *mut AudioProperties;

        #[namespace = "TagLib"]
        type AudioProperties;
        fn lengthInMilliseconds(self: Pin<&AudioProperties>) -> i32;
        fn bitrate(self: Pin<&AudioProperties>) -> i32;
        fn sampleRate(self: Pin<&AudioProperties>) -> i32;
        fn channels(self: Pin<&AudioProperties>) -> i32;

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "File"]
        type OggFile;

        #[namespace = "TagLib::Ogg"]
        type XiphComment;
        unsafe fn fieldListMap(self: Pin<&XiphComment>) -> &SimplePropertyMap;

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type VorbisFile;
        #[cxx_name = "tag"]
        unsafe fn tag(self: Pin<&VorbisFile>) -> *mut XiphComment;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type OpusFile;
        #[cxx_name = "tag"]
        unsafe fn opusTag(self: Pin<&OpusFile>) -> *mut XiphComment;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "File"]
        type FLACFile;
        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type MPEGFile;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type MP4File;

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type WAVFile;

        #[namespace = "TagLib::WavPack"]
        #[cxx_name = "File"]
        type WavPackFile;

        #[namespace = "TagLib::APE"]
        #[cxx_name = "File"]
        type APEFile;

        // File conversion functions
        #[namespace = "taglib_shim"]
        unsafe fn File_asOgg(file: *const BaseFile) -> *const OggFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asVorbis(file: *const BaseFile) -> *const VorbisFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asOpus(file: *const BaseFile) -> *const OpusFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMPEG(file: *const BaseFile) -> *const MPEGFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asFLAC(file: *const BaseFile) -> *const FLACFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMP4(file: *const BaseFile) -> *const MP4File;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWAV(file: *const BaseFile) -> *const WAVFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWavPack(file: *const BaseFile) -> *const WavPackFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asAPE(file: *const BaseFile) -> *const APEFile;

        #[namespace = "TagLib"]
        type SimplePropertyMap;
        #[namespace = "taglib_shim"]
        fn SimplePropertyMap_to_vector(
            field_list_map: Pin<&SimplePropertyMap>,
        ) -> UniquePtr<CxxVector<Property>>;

        #[namespace = "taglib_shim"]
        type Property;
        fn key(self: Pin<&Property>) -> &TagString;
        unsafe fn value(self: Pin<&Property>) -> &StringList;

        #[namespace = "TagLib"]
        type StringList;
        #[namespace = "taglib_shim"]
        fn StringList_to_vector(string_list: Pin<&StringList>) -> UniquePtr<CxxVector<TagString>>;

        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type TagString;
        #[namespace = "taglib_shim"]
        unsafe fn toCString(self: Pin<&TagString>, unicode: bool) -> *const c_char;
    }
}

impl bindings::FileRef {
    pub fn file_or(&self) -> Option<&bindings::BaseFile> {
        unsafe {
            // SAFETY: This pin only lasts for the scope of this function.
            // Nothing that can change the memory address of self is returned,
            // only the address of the file pointer.
            let pinned_self = Pin::new_unchecked(&*self);
            if !pinned_self.isNull() {
                pinned_self.file().as_ref()
            } else {
                None
            }
        }
    }
}

impl bindings::BaseFile {
    pub fn audio_properties(&self) -> Option<&bindings::AudioProperties> {
        let props = unsafe {
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.audioProperties()
        };
        unsafe {
            props.as_ref()
        }
    }
    
    pub fn as_opus(&self) -> Option<&bindings::OpusFile> {
        let opus_file = unsafe {
            bindings::File_asOpus(self as *const Self)
        };
        unsafe {
            opus_file.as_ref()
        }
    }

    pub fn as_vorbis(&self) -> Option<&bindings::VorbisFile> {
        let vorbis_file = unsafe {
            bindings::File_asVorbis(self as *const Self)
        };
        unsafe {
            vorbis_file.as_ref()
        }
    }
}

impl bindings::AudioProperties {
    pub fn length_ms(&self) -> i32 {
        unsafe {
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.lengthInMilliseconds()
        }
    }

    pub fn bitrate_kbps(&self) -> i32 {
        unsafe {
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.bitrate()
        }
    }

    pub fn sample_rate_hz(&self) -> i32 {
        unsafe {
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.sampleRate()
        }
    }

    pub fn channel_count(&self) -> i32 {
        unsafe {
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.channels()
        }
    }
}

impl bindings::OpusFile {
    pub fn xiph_comments(&self) -> Option<&bindings::XiphComment> {
        let tag = unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.opusTag()
        };
        unsafe { 
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        }
    }
}

impl bindings::VorbisFile {
    pub fn xiph_comments(&self) -> Option<&bindings::XiphComment> {
        let tag = unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.tag()
        };
        unsafe { 
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        }
    }
}

impl bindings::XiphComment {
    pub fn field_list_map(&self) -> &bindings::SimplePropertyMap {
        unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            pinned_self.fieldListMap()
        }
    }
}

impl bindings::SimplePropertyMap {
    pub fn to_hashmap(&self) -> HashMap<String, Vec<String>> {
        let cxx_vec = unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            bindings::SimplePropertyMap_to_vector(pinned_self)
        };
        cxx_vec.iter().map(|property| property.to_tuple()).collect()
    }
}

impl bindings::Property {
    pub fn to_tuple(&self) -> (String, Vec<String>) {
        unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            let key = pinned_self.key().to_string();
            let value = pinned_self.value().to_vec();
            (key, value)
        }
    }
}
impl ToString for bindings::TagString {
    fn to_string(&self) -> String {
        let c_str = unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let this = Pin::new_unchecked(self);
            this.toCString(true)
        };
        unsafe { 
            // SAFETY: This is an output from C++ with a null pointer
            // by design. It will not be mutated and is instantly copied
            // into rust.
            CStr::from_ptr(c_str) 
        }
            .to_string_lossy()
            .to_string()
    }
}

impl bindings::StringList {
    pub fn to_vec(&self) -> Vec<String> {
        let cxx_values = unsafe {
            // SAFETY: This will not exist beyond the scope of this function,
            // and will only be used over ffi as a c++ this pointer (which is
            // also pinned)
            let pinned_self = Pin::new_unchecked(self);
            bindings::StringList_to_vector(pinned_self)
        };
        cxx_values.iter().map(|value| value.to_string()).collect()
    }
}
