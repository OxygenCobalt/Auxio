#[cxx::bridge]
pub(crate) mod bindings {
    unsafe extern "C++" {
        include!("taglib/taglib.h");
        include!("taglib/tstring.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");

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
        fn isNull(self: Pin<&FileRef>) -> bool;
        fn file(self: Pin<&FileRef>) -> *mut File;

        fn audioProperties(self: Pin<&File>) -> *mut AudioProperties;

        // File type checking functions
        #[namespace = "taglib_shim"]
        unsafe fn File_isMPEG(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isFLAC(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isMP4(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isOgg(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isOpus(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isWAV(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isWavPack(file: *mut File) -> bool;
        #[namespace = "taglib_shim"]
        unsafe fn File_isAPE(file: *mut File) -> bool;

        // AudioProperties methods
        fn lengthInMilliseconds(self: Pin<&AudioProperties>) -> i32;
        fn bitrate(self: Pin<&AudioProperties>) -> i32;
        fn sampleRate(self: Pin<&AudioProperties>) -> i32;
        fn channels(self: Pin<&AudioProperties>) -> i32;

        // String conversion utilities
        #[namespace = "taglib_shim"]
        unsafe fn toCString(self: Pin<&TagString>, unicode: bool) -> *const c_char;
        #[namespace = "taglib_shim"]
        fn isEmpty(self: Pin<&TagString>) -> bool;
    }
}
