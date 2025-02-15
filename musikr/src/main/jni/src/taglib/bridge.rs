use super::iostream::DynIOStream;

#[cxx::bridge]
mod bridge_impl {
    // Expose Rust IOStream to C++
    extern "Rust" {
        #[cxx_name = "RsIOStream"]
        type DynIOStream<'a>;

        fn name(self: &mut DynIOStream<'_>) -> String;
        fn read(self: &mut DynIOStream<'_>, buffer: &mut [u8]) -> usize;
        fn write(self: &mut DynIOStream<'_>, data: &[u8]);
        fn seek(self: &mut DynIOStream<'_>, offset: i64, whence: i32);
        fn truncate(self: &mut DynIOStream<'_>, length: i64);
        fn tell(self: &mut DynIOStream<'_>) -> i64;
        fn length(self: &mut DynIOStream<'_>) -> i64;
        fn is_readonly(self: &mut DynIOStream<'_>) -> bool;
    }

    #[namespace = "taglib_shim"]
    unsafe extern "C++" {
        include!("taglib/taglib.h");
        include!("taglib/tstring.h");
        include!("taglib/tstringlist.h");
        include!("taglib/vorbisfile.h");
        include!("taglib/xiphcomment.h");
        include!("taglib/tiostream.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/tk_shim.hpp");

        #[namespace = "TagLib"]
        #[cxx_name = "IOStream"]
        type CPPIOStream;
        // Create a RustIOStream from a BridgeStream
        unsafe fn wrap_RsIOStream(stream: Pin<&mut DynIOStream>) -> UniquePtr<CPPIOStream>;

        #[namespace = "TagLib"]
        #[cxx_name = "FileRef"]
        type CPPFileRef;
        #[cxx_name = "isNull"]
        fn isNull(self: Pin<&CPPFileRef>) -> bool;
        #[cxx_name = "file"]
        fn file(self: Pin<&CPPFileRef>) -> *mut CPPFile;

        // Create a FileRef from an iostream
        unsafe fn new_FileRef(stream: *mut CPPIOStream) -> UniquePtr<CPPFileRef>;

        #[namespace = "TagLib"]
        #[cxx_name = "File"]
        type CPPFile;
        #[cxx_name = "audioProperties"]
        fn audioProperties(self: Pin<&CPPFile>) -> *mut CppAudioProperties;

        #[namespace = "TagLib"]
        #[cxx_name = "AudioProperties"]
        type CppAudioProperties;
        #[cxx_name = "lengthInMilliseconds"]
        fn lengthInMilliseconds(self: Pin<&CppAudioProperties>) -> i32;
        #[cxx_name = "bitrate"]
        fn bitrate(self: Pin<&CppAudioProperties>) -> i32;
        #[cxx_name = "sampleRate"]
        fn sampleRate(self: Pin<&CppAudioProperties>) -> i32;
        #[cxx_name = "channels"]
        fn channels(self: Pin<&CppAudioProperties>) -> i32;

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "XiphComment"]
        type CPPXiphComment;
        #[cxx_name = "fieldListMap"]
        fn fieldListMap(self: Pin<&CPPXiphComment>) -> &CPPSimplePropertyMap;

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type CPPVorbisFile;
        #[cxx_name = "tag"]
        unsafe fn vorbisTag(self: Pin<&CPPVorbisFile>) -> *mut CPPXiphComment;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type CPPOpusFile;
        #[cxx_name = "tag"]
        unsafe fn opusTag(self: Pin<&CPPOpusFile>) -> *mut CPPXiphComment;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "File"]
        type CPPFLACFile;
        #[cxx_name = "xiphComment"]
        unsafe fn xiphComment(self: Pin<&mut CPPFLACFile>, create: bool) -> *mut CPPXiphComment;

        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type CPPMPEGFile;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type CPPMP4File;

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type CPPWAVFile;

        // #[namespace = "TagLib::WavPack"]
        // #[cxx_name = "File"]
        // type WavPackFile;

        // #[namespace = "TagLib::APE"]
        // #[cxx_name = "File"]
        // type APEFile;

        #[namespace = "taglib_shim"]
        unsafe fn File_asVorbis(file: *mut CPPFile) -> *mut CPPVorbisFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asOpus(file: *mut CPPFile) -> *mut CPPOpusFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMPEG(file: *mut CPPFile) -> *mut CPPMPEGFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asFLAC(file: *mut CPPFile) -> *mut CPPFLACFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMP4(file: *mut CPPFile) -> *mut CPPMP4File;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWAV(file: *mut CPPFile) -> *mut CPPWAVFile;

        #[namespace = "TagLib"]
        #[cxx_name = "SimplePropertyMap"]
        type CPPSimplePropertyMap;
        #[namespace = "taglib_shim"]
        fn SimplePropertyMap_to_vector(
            field_list_map: Pin<&CPPSimplePropertyMap>,
        ) -> UniquePtr<CxxVector<CPPProperty>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "Property"]
        type CPPProperty;
        #[cxx_name = "key"]
        fn key(self: Pin<&CPPProperty>) -> &CPPString;
        #[cxx_name = "value"]
        unsafe fn value(self: Pin<&CPPProperty>) -> &CPPStringList;

        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type CPPString;
        #[cxx_name = "toCString"]
        unsafe fn thisToCString(self: Pin<&CPPString>, unicode: bool) -> *const c_char;

        #[namespace = "TagLib"]
        #[cxx_name = "StringList"]
        type CPPStringList;
        #[namespace = "taglib_shim"]
        fn StringList_to_vector(string_list: Pin<&CPPStringList>) -> UniquePtr<CxxVector<CPPString>>;
    }
}

pub use bridge_impl::*;
