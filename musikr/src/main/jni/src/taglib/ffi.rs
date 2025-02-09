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
        fn file(self: Pin<&FileRef>) -> *mut File;

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
        type File;
        fn audioProperties(self: Pin<&File>) -> *mut AudioProperties;

        #[namespace = "TagLib"]
        type AudioProperties;
        fn lengthInMilliseconds(self: Pin<&AudioProperties>) -> i32;
        fn bitrate(self: Pin<&AudioProperties>) -> i32;
        fn sampleRate(self: Pin<&AudioProperties>) -> i32;
        fn channels(self: Pin<&AudioProperties>) -> i32;

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type VorbisFile;
        unsafe fn tag(self: Pin<&VorbisFile>) -> *mut XiphComment;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "File"]
        type FLACFile;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type OpusFile;
        unsafe fn tag(self: Pin<&OpusFile>) -> *mut XiphComment;

        #[namespace = "TagLib::Ogg"]
        type XiphComment;
        unsafe fn fieldListMap(self: Pin<&XiphComment>) -> &SimplePropertyMap;

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
        unsafe fn File_asVorbis(file: *mut File) -> *mut VorbisFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asOpus(file: *mut File) -> *mut OpusFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMPEG(file: *mut File) -> *mut MPEGFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asFLAC(file: *mut File) -> *mut FLACFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMP4(file: *mut File) -> *mut MP4File;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWAV(file: *mut File) -> *mut WAVFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWavPack(file: *mut File) -> *mut WavPackFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asAPE(file: *mut File) -> *mut APEFile;

        #[namespace = "TagLib"]
        type SimplePropertyMap;
        #[namespace = "taglib_shim"]
        fn SimplePropertyMap_to_vector(field_list_map: Pin<&SimplePropertyMap>) -> UniquePtr<CxxVector<Property>>;

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
        #[namespace = "taglib_shim"]
        fn isEmpty(self: Pin<&TagString>) -> bool;
    }
}
