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

        #[namespace = "TagLib"]
        #[cxx_name = "FileRef"]
        type TFileRef;
        #[cxx_name = "isNull"]
        fn thisIsNull(self: Pin<&TFileRef>) -> bool;
        #[cxx_name = "file"]
        fn thisFile(self: Pin<&TFileRef>) -> *mut BaseFile;

        // Create a RustIOStream from a BridgeStream
        unsafe fn wrap_RsIOStream(stream: Pin<&mut DynIOStream>) -> UniquePtr<CPPIOStream>;
        // Create a FileRef from an iostream
        unsafe fn new_FileRef(stream: *mut CPPIOStream) -> UniquePtr<TFileRef>;

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
        #[cxx_name = "xiphComment"]
        unsafe fn flacThisXiphComment(self: Pin<&mut FLACFile>, create: bool) -> *mut XiphComment;

        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type MPEGFile;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type MP4File;

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type WAVFile;

        // #[namespace = "TagLib::WavPack"]
        // #[cxx_name = "File"]
        // type WavPackFile;

        // #[namespace = "TagLib::APE"]
        // #[cxx_name = "File"]
        // type APEFile;

        #[namespace = "taglib_shim"]
        unsafe fn File_asVorbis(file: *mut BaseFile) -> *mut VorbisFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asOpus(file: *mut BaseFile) -> *mut OpusFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMPEG(file: *mut BaseFile) -> *mut MPEGFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asFLAC(file: *mut BaseFile) -> *mut FLACFile;
        #[namespace = "taglib_shim"]
        unsafe fn File_asMP4(file: *mut BaseFile) -> *mut MP4File;
        #[namespace = "taglib_shim"]
        unsafe fn File_asWAV(file: *mut BaseFile) -> *mut WAVFile;
        // #[namespace = "taglib_shim"]
        // unsafe fn File_asWavPack(file: *mut BaseFile) -> *mut WavPackFile;
        // #[namespace = "taglib_shim"]
        // unsafe fn File_asAPE(file: *mut BaseFile) -> *mut APEFile;

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

pub use bridge_impl::*;
