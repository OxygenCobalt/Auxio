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
        include!("taglib/flacpicture.h");
        include!("taglib/tbytevector.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/tk_shim.hpp");
        include!("shim/picture_shim.hpp");
        include!("shim/xiph_shim.hpp");
        include!("shim/id3_shim.hpp");
        include!("taglib/mpegfile.h");

        #[namespace = "TagLib"]
        #[cxx_name = "IOStream"]
        type CPPIOStream;
        // Create a RustIOStream from a BridgeStream
        fn wrap_RsIOStream(stream: Pin<&mut DynIOStream>) -> UniquePtr<CPPIOStream>;

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

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type CPPVorbisFile;
        #[cxx_name = "tag"]
        fn vorbisTag(self: Pin<&CPPVorbisFile>) -> *mut CPPXiphComment;
        #[namespace = "taglib_shim"]
        fn XiphComment_pictureList(comment: Pin<&mut CPPXiphComment>) -> UniquePtr<CPPPictureList>;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type CPPOpusFile;
        #[cxx_name = "tag"]
        fn opusTag(self: Pin<&CPPOpusFile>) -> *mut CPPXiphComment;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "File"]
        type CPPFLACFile;
        #[cxx_name = "xiphComment"]
        fn xiphComment(self: Pin<&mut CPPFLACFile>, create: bool) -> *mut CPPXiphComment;
        #[namespace = "taglib_shim"]
        fn FLACFile_pictureList(file: Pin<&mut CPPFLACFile>) -> UniquePtr<CPPPictureList>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "PictureList"]
        type CPPPictureList;
        #[namespace = "taglib_shim"]
        fn PictureList_to_vector(list: Pin<&CPPPictureList>) -> UniquePtr<CxxVector<CPPWrappedPicture>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "WrappedPicture"]
        type CPPWrappedPicture;
        fn inner(self: &CPPWrappedPicture) -> *const CPPFLACPicture;

        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type CPPMPEGFile;
        #[cxx_name = "ID3v2Tag"]
        fn ID3v2Tag(self: Pin<&mut CPPMPEGFile>, create: bool) -> *mut CPPID3v2Tag;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type CPPMP4File;

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type CPPWAVFile;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "Picture"]
        type CPPFLACPicture;
        #[namespace = "taglib_shim"]
        fn Picture_data(picture: Pin<&CPPFLACPicture>) -> UniquePtr<CPPByteVector>;

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "XiphComment"]
        type CPPXiphComment;
        #[cxx_name = "fieldListMap"]
        fn fieldListMap(self: Pin<& CPPXiphComment>) -> &CPPFieldListMap;

        #[namespace = "TagLib"]
        #[cxx_name = "SimplePropertyMap"]
        type CPPFieldListMap;
        #[namespace = "taglib_shim"]
        fn FieldListMap_to_entries(
            field_list_map: Pin<&CPPFieldListMap>,
        ) -> UniquePtr<CxxVector<CPPFieldListEntry>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "FieldListEntry"]
        type CPPFieldListEntry;
        #[cxx_name = "key"]
        fn key(self: Pin<&CPPFieldListEntry>) -> &CPPString;
        #[cxx_name = "value"]
        fn value(self: Pin<&CPPFieldListEntry>) -> &CPPStringList;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "Tag"]
        type CPPID3v2Tag;
        #[namespace = "taglib_shim"]
        fn Tag_frameList(tag: Pin<&CPPID3v2Tag>) -> UniquePtr<CxxVector<WrappedFrame>>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "Frame"]
        type CPPID3v2Frame;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asTextIdentification(frame: *const CPPID3v2Frame) -> *const CPPID3v2TextIdentificationFrame;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asUserTextIdentification(frame: *const CPPID3v2Frame) -> *const CPPID3v2UserTextIdentificationFrame;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asAttachedPicture(frame: *const CPPID3v2Frame) -> *const CPPID3v2AttachedPictureFrame;

        #[namespace = "taglib_shim"]
        type WrappedFrame;
        fn get(self: &WrappedFrame) -> *const CPPID3v2Frame;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "TextIdentificationFrame"]
        type CPPID3v2TextIdentificationFrame;
        #[namespace = "taglib_shim"]
        fn TextIdentificationFrame_fieldList(frame: Pin<&CPPID3v2TextIdentificationFrame>) -> UniquePtr<CPPStringList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "UserTextIdentificationFrame"]
        type CPPID3v2UserTextIdentificationFrame;
        #[namespace = "taglib_shim"]
        fn UserTextIdentificationFrame_fieldList(frame: Pin<&CPPID3v2UserTextIdentificationFrame>) -> UniquePtr<CPPStringList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "AttachedPictureFrame"]
        type CPPID3v2AttachedPictureFrame;
        #[namespace = "taglib_shim"]
        fn AttachedPictureFrame_picture(frame: Pin<&CPPID3v2AttachedPictureFrame>) -> UniquePtr<CPPByteVector>;

        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type CPPString;
        #[cxx_name = "toCString"]
        fn toCString(self: Pin<&CPPString>, unicode: bool) -> *const c_char;

        #[namespace = "TagLib"]
        #[cxx_name = "StringList"]
        type CPPStringList;
        #[namespace = "taglib_shim"]
        fn StringList_to_vector(string_list: Pin<&CPPStringList>) -> UniquePtr<CxxVector<CPPString>>;
        
        #[namespace = "TagLib"]
        #[cxx_name = "ByteVector"]
        type CPPByteVector;
        fn size(self: Pin<&CPPByteVector>) -> u32;
        fn data(self: Pin<&CPPByteVector>) -> *const c_char;
    }
}

pub use bridge_impl::*;
