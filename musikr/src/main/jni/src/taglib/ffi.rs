use std::collections::HashMap;
use std::ffi::CStr;
use std::pin::Pin;
use std::string::ToString;

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
        #[cxx_name = "isNull"]
        fn thisIsNull(self: Pin<&FileRef>) -> bool;
        #[cxx_name = "file"]
        fn thisFile(self: Pin<&FileRef>) -> *mut BaseFile;

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
        #[cxx_name = "audioProperties"]
        fn thisAudioProperties(self: Pin<&BaseFile>) -> *mut AudioProperties;

        #[namespace = "TagLib"]
        type AudioProperties;
        #[cxx_name = "lengthInMilliseconds"]
        fn thisLengthInMilliseconds(self: Pin<&AudioProperties>) -> i32;
        #[cxx_name = "bitrate"]
        fn thisBitrate(self: Pin<&AudioProperties>) -> i32;
        #[cxx_name = "sampleRate"]
        fn thisSampleRate(self: Pin<&AudioProperties>) -> i32;
        #[cxx_name = "channels"]
        fn thisChannels(self: Pin<&AudioProperties>) -> i32;

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "File"]
        type OggFile;

        #[namespace = "TagLib::Ogg"]
        type XiphComment;
        #[cxx_name = "fieldListMap"]
        unsafe fn thisFieldListMap(self: Pin<&XiphComment>) -> &SimplePropertyMap;

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type VorbisFile;
        #[cxx_name = "tag"]
        unsafe fn vorbisThisTag(self: Pin<&VorbisFile>) -> *mut XiphComment;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type OpusFile;
        #[cxx_name = "tag"]
        unsafe fn opusThisTag(self: Pin<&OpusFile>) -> *mut XiphComment;

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
        #[cxx_name = "key"]
        fn thisKey(self: Pin<&Property>) -> &TString;
        #[cxx_name = "value"]
        unsafe fn thisValue(self: Pin<&Property>) -> &TStringList;

        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type TString;
        #[cxx_name = "toCString"]
        unsafe fn thisToCString(self: Pin<&TString>, unicode: bool) -> *const c_char;

        #[namespace = "TagLib"]
        #[cxx_name = "StringList"]
        type TStringList;
        #[namespace = "taglib_shim"]
        fn StringList_to_vector(string_list: Pin<&TStringList>) -> UniquePtr<CxxVector<TString>>;
    }
}

impl bindings::FileRef {
    pub fn file_or(&self) -> Option<&bindings::BaseFile> {
        let file = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The file data is a pointer that does not depend on the
            //   address of self.
            let this = Pin::new_unchecked(&*self);
            // Note: This is not the rust ptr "is_null", but a taglib isNull method
            // that checks for file validity. Without this check, we can get corrupted
            // file ptrs.
            if !this.thisIsNull() {
                Some(this.thisFile())
            } else {
                None
            }
        };
        file.and_then(|file| unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            file.as_ref()
        })
    }
}

impl bindings::BaseFile {
    pub fn audio_properties(&self) -> Option<&bindings::AudioProperties> {
        let props = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The audio properties data is a pointer that does not depend on the
            //   address of self.
            let this: Pin<&bindings::BaseFile> = Pin::new_unchecked(self);
            this.thisAudioProperties()
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            props.as_ref()
        }
    }

    pub fn as_opus(&self) -> Option<&bindings::OpusFile> {
        let ptr_self = self as *const Self;
        let opus_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bindings::File_asOpus(ptr_self)
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            opus_file.as_ref()
        }
    }

    pub fn as_vorbis(&self) -> Option<&bindings::VorbisFile> {
        let ptr_self = self as *const Self;
        let vorbis_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bindings::File_asVorbis(ptr_self)
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            vorbis_file.as_ref()
        }
    }
}

impl bindings::AudioProperties {
    pub fn length_in_milliseconds(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisLengthInMilliseconds()
        }
    }

    pub fn bitrate(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisBitrate()
        }
    }

    pub fn sample_rate(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisSampleRate()
        }
    }

    pub fn channels(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisChannels()
        }
    }
}

impl bindings::OpusFile {
    pub fn xiph_comments(&self) -> Option<&bindings::XiphComment> {
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.opusThisTag()
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
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.vorbisThisTag()
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
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a reference that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisFieldListMap()
        }
    }
}

impl bindings::SimplePropertyMap {
    pub fn to_hashmap(&self) -> HashMap<String, Vec<String>> {
        let cxx_vec = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a unique_ptr to a copied vector that is not dependent
            //   on the address of self.
            let this = Pin::new_unchecked(self);
            bindings::SimplePropertyMap_to_vector(this)
        };
        cxx_vec.iter().map(|property| property.to_tuple()).collect()
    }
}

impl bindings::Property {
    pub fn to_tuple(&self) -> (String, Vec<String>) {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The values returned are copied and thus not dependent on the address
            //   of self.
            let this = Pin::new_unchecked(self);
            let key = this.thisKey().to_string();
            let value = this.thisValue().to_vec();
            (key, value)
        }
    }
}
impl ToString for bindings::TString {
    fn to_string(&self) -> String {
        let c_str = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value returned are pointers and thus not dependent on the address
            //   of self.
            let this: Pin<&bindings::TString> = Pin::new_unchecked(self);
            this.thisToCString(true)
        };
        unsafe {
            // SAFETY:
            // - This is a C-string returned by a C++ method guaranteed to have
            //   a null terminator.
            // - This C-string is fully allocated and owned by the TagString instance,
            //   in a continous block from start to null terminator.
            // - This C-string will be non-null even if empty.
            // - This pointer will not be mutated before it's entirely copied into
            //   rust.
            // - This C-string is copied to a rust string before TagString is destroyed.
            CStr::from_ptr(c_str)
        }
        .to_string_lossy()
        .to_string()
    }
}

impl bindings::TStringList {
    pub fn to_vec(&self) -> Vec<String> {
        let cxx_values = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value returned is a unique ptr to a copied vector that is not
            //   dependent on the address of self.
            let this = Pin::new_unchecked(self);
            bindings::StringList_to_vector(this)
        };
        cxx_values.iter().map(|value| value.to_string()).collect()
    }
}
