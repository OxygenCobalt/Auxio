#[cxx::bridge]
mod bridge_impl {
    // Expose Rust IOStream to C++
    extern "Rust" {
        #[cxx_name = "BridgeStream"]
        type TIOStream<'a>;

        fn name(self: &mut TIOStream<'_>) -> String;
        fn read(self: &mut TIOStream<'_>, buffer: &mut [u8]) -> usize;
        fn write(self: &mut TIOStream<'_>, data: &[u8]);
        fn seek(self: &mut TIOStream<'_>, offset: i64, whence: i32);
        fn truncate(self: &mut TIOStream<'_>, length: i64);
        fn tell(self: &mut TIOStream<'_>) -> i64;
        fn length(self: &mut TIOStream<'_>) -> i64;
        fn is_readonly(self: &TIOStream<'_>) -> bool;
    }

    #[namespace = "taglib_shim"]
    unsafe extern "C++" {
        include!("taglib/taglib.h");
        include!("taglib/tstring.h");
        include!("taglib/tstringlist.h");
        include!("taglib/vorbisfile.h");
        include!("taglib/xiphcomment.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/tk_shim.hpp");

        #[cxx_name = "RustIOStream"]
        type RustIOStream;

        #[namespace = "TagLib"]
        #[cxx_name = "FileRef"]
        type TFileRef;
        #[cxx_name = "isNull"]
        fn thisIsNull(self: Pin<&TFileRef>) -> bool;
        #[cxx_name = "file"]
        fn thisFile(self: Pin<&TFileRef>) -> *mut BaseFile;

        // Create a RustIOStream from a BridgeStream
        unsafe fn new_RustIOStream(stream: Pin<&mut TIOStream>) -> UniquePtr<RustIOStream>;
        // Create a FileRef from an iostream
        fn new_FileRef_from_stream(stream: UniquePtr<RustIOStream>) -> UniquePtr<TFileRef>;

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

use std::io::SeekFrom;
use std::pin::Pin;
use super::file::IOStream;

#[repr(C)]
pub(super) struct TIOStream<'a>(Box<dyn IOStream + 'a>);

impl<'a> TIOStream<'a> {
    pub fn new<T: IOStream + 'a>(stream: T) -> Pin<Box<Self>> {
        Box::pin(TIOStream(Box::new(stream)))
    }


    // Implement the exposed functions for cxx bridge
    pub fn name(&mut self) -> String {
        self.0.name()
    }

    pub fn read(&mut self, buffer: &mut [u8]) -> usize {
        self.0.read(buffer).unwrap_or(0)
    }

    pub fn write(&mut self, data: &[u8]) {
        self.0.write_all(data).unwrap();
    }

    pub fn seek(&mut self, offset: i64, whence: i32) {
        let pos = match whence {
            0 => SeekFrom::Start(offset as u64),
            1 => SeekFrom::Current(offset),
            2 => SeekFrom::End(offset),
            _ => panic!("Invalid seek whence"),
        };
        self.0.seek(pos).unwrap();
    }

    pub fn truncate(&mut self, length: i64) {
        self.0.seek(SeekFrom::Start(length as u64)).unwrap();
        // TODO: Actually implement truncate once we have a better trait bound
    }

    pub fn tell(&mut self) -> i64 {
        self.0.seek(SeekFrom::Current(0)).unwrap() as i64
    }

    pub fn length(&mut self) -> i64 {
        let current = self.0.seek(SeekFrom::Current(0)).unwrap();
        let end = self.0.seek(SeekFrom::End(0)).unwrap();
        self.0.seek(SeekFrom::Start(current)).unwrap();
        end as i64
    }

    pub fn is_readonly(&self) -> bool {
        self.0.is_readonly()
    }
}
