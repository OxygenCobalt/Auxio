#[cxx::bridge]
pub(crate) mod bindings {
    unsafe extern "C++" {
        include!("taglib/taglib.h");
        include!("taglib/tstring.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/string_shim.hpp");
        include!("shim/audioproperties_shim.hpp");

        #[namespace = "TagLib"]
        type FileRef;
        #[namespace = "TagLib"]
        type File;
        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type TagString;
        #[namespace = "TagLib"]
        type AudioProperties;

        #[namespace = "taglib_shim"]
        type RustIOStream;
        #[namespace = "taglib_shim"]
        type RustStream;

        // Create a FileRef from an iostream
        #[namespace = "taglib_shim"]
        unsafe fn new_rust_iostream(stream: *mut RustStream) -> UniquePtr<RustIOStream>;
        #[namespace = "taglib_shim"]
        fn new_FileRef_from_stream(stream: UniquePtr<RustIOStream>) -> UniquePtr<FileRef>;

        // FileRef helper functions
        #[namespace = "taglib_shim"]
        fn FileRef_isNull(ref_: &FileRef) -> bool;
        #[namespace = "taglib_shim"]
        fn FileRef_file(ref_: &FileRef) -> &File;

        // File tag methods
        #[namespace = "taglib_shim"]
        fn File_tag_title(file: &File) -> &TagString;

        // File type checking functions
        #[namespace = "taglib_shim"]
        fn File_isMPEG(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isFLAC(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isMP4(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isOgg(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isOpus(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isWAV(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isWavPack(file: &File) -> bool;
        #[namespace = "taglib_shim"]
        fn File_isAPE(file: &File) -> bool;

        // Audio Properties methods
        #[namespace = "taglib_shim"]
        unsafe fn File_audioProperties(file: &File) -> *const AudioProperties;
        #[namespace = "taglib_shim"]
        unsafe fn AudioProperties_lengthInMilliseconds(properties: *const AudioProperties) -> i32;
        #[namespace = "taglib_shim"]
        unsafe fn AudioProperties_bitrateInKilobitsPerSecond(properties: *const AudioProperties) -> i32;
        #[namespace = "taglib_shim"]
        unsafe fn AudioProperties_sampleRateInHz(properties: *const AudioProperties) -> i32;
        #[namespace = "taglib_shim"]
        unsafe fn AudioProperties_numberOfChannels(properties: *const AudioProperties) -> i32;

        // String conversion utilities
        #[namespace = "taglib_shim"]
        unsafe fn to_string(s: &TagString) -> *const c_char;
        #[namespace = "taglib_shim"]
        fn isEmpty(s: &TagString) -> bool;
    }
}
