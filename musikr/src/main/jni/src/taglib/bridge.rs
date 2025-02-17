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
        include!("taglib/mp4file.h");
        include!("taglib/mp4tag.h");
        include!("taglib/mp4item.h");
        include!("shim/iostream_shim.hpp");
        include!("shim/file_shim.hpp");
        include!("shim/tk_shim.hpp");
        include!("shim/picture_shim.hpp");
        include!("shim/xiph_shim.hpp");
        include!("shim/id3v2_shim.hpp");
        include!("shim/id3v1_shim.hpp");
        include!("shim/mp4_shim.hpp");
        include!("taglib/mpegfile.h");

        #[namespace = "TagLib"]
        #[cxx_name = "IOStream"]
        type CPPIOStream;
        fn wrap_RsIOStream(stream: Pin<&mut DynIOStream>) -> UniquePtr<CPPIOStream>;

        #[namespace = "TagLib"]
        #[cxx_name = "FileRef"]
        type CPPFileRef;
        unsafe fn new_FileRef(stream: *mut CPPIOStream) -> UniquePtr<CPPFileRef>;
        fn isNull(self: &CPPFileRef) -> bool;
        fn file(self: &CPPFileRef) -> *mut CPPFile;

        #[namespace = "TagLib"]
        #[cxx_name = "File"]
        type CPPFile;
        fn audioProperties(self: &CPPFile) -> *mut CppAudioProperties;
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
        fn lengthInMilliseconds(self: &CppAudioProperties) -> i32;
        fn bitrate(self: &CppAudioProperties) -> i32;
        fn sampleRate(self: &CppAudioProperties) -> i32;
        fn channels(self: &CppAudioProperties) -> i32;

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type CPPVorbisFile;
        #[cxx_name = "tag"]
        fn vorbisTag(self: &CPPVorbisFile) -> *mut CPPXiphComment;
        #[namespace = "taglib_shim"]
        fn XiphComment_pictureList(comment: Pin<&mut CPPXiphComment>) -> UniquePtr<CPPPictureList>;

        #[namespace = "TagLib::Ogg::Opus"]
        #[cxx_name = "File"]
        type CPPOpusFile;
        #[cxx_name = "tag"]
        fn opusTag(self: &CPPOpusFile) -> *mut CPPXiphComment;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "File"]
        type CPPFLACFile;
        fn xiphComment(self: Pin<&mut CPPFLACFile>, create: bool) -> *mut CPPXiphComment;
        #[cxx_name = "ID3v1Tag"]
        fn FLACID3v1Tag(self: Pin<&mut CPPFLACFile>, create: bool) -> *mut CPPID3v1Tag;
        #[cxx_name = "ID3v2Tag"]
        fn FLACID3v2Tag(self: Pin<&mut CPPFLACFile>, create: bool) -> *mut CPPID3v2Tag;
        #[namespace = "taglib_shim"]
        fn FLACFile_pictureList(file: Pin<&mut CPPFLACFile>) -> UniquePtr<CPPPictureList>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "PictureList"]
        type CPPPictureList;
        #[namespace = "taglib_shim"]
        fn PictureList_to_vector(
            list: &CPPPictureList,
        ) -> UniquePtr<CxxVector<CPPFLACPicturePointer>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "PicturePointer"]
        type CPPFLACPicturePointer;
        fn get(self: &CPPFLACPicturePointer) -> *const CPPFLACPicture;

        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type CPPMPEGFile;
        #[cxx_name = "ID3v1Tag"]
        fn MPEGID3v1Tag(self: Pin<&mut CPPMPEGFile>, create: bool) -> *mut CPPID3v1Tag;
        #[cxx_name = "ID3v2Tag"]
        fn MPEGID3v2Tag(self: Pin<&mut CPPMPEGFile>, create: bool) -> *mut CPPID3v2Tag;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type CPPMP4File;
        #[cxx_name = "tag"]
        fn MP4Tag(self: &CPPMP4File) -> *mut CPPMP4Tag;

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type CPPWAVFile;
        #[cxx_name = "ID3v2Tag"]
        fn WAVID3v2Tag(self: &CPPWAVFile) -> *mut CPPID3v2Tag;

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "Picture"]
        type CPPFLACPicture;
        #[namespace = "taglib_shim"]
        fn Picture_data(picture: &CPPFLACPicture) -> UniquePtr<CPPByteVector>;

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "XiphComment"]
        type CPPXiphComment;
        // Explicit lifecycle definition to state while the Pin is temporary, the CPPFieldListMap
        // ref returned actually has the same lifetime as the CPPXiphComment.
        fn fieldListMap<'slf, 'file_ref>(self: &'slf CPPXiphComment) -> &'file_ref CPPFieldListMap;

        #[namespace = "TagLib"]
        #[cxx_name = "SimplePropertyMap"]
        type CPPFieldListMap;
        #[namespace = "taglib_shim"]
        fn FieldListMap_to_entries(
            field_list_map: &CPPFieldListMap,
        ) -> UniquePtr<CxxVector<CPPFieldListEntry>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "FieldListEntry"]
        type CPPFieldListEntry;
        fn key<'slf, 'file_ref>(self: &'slf CPPFieldListEntry) -> &'file_ref CPPString;
        fn value<'slf, 'file_ref>(self: &'slf CPPFieldListEntry) -> &'file_ref CPPStringList;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "Tag"]
        type CPPID3v2Tag;
        #[namespace = "taglib_shim"]
        fn Tag_frameList(tag: &CPPID3v2Tag) -> UniquePtr<CPPID3v2FrameList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "FrameList"]
        type CPPID3v2FrameList;
        #[namespace = "taglib_shim"]
        fn FrameList_to_vector(
            list: &CPPID3v2FrameList,
        ) -> UniquePtr<CxxVector<CPPFramePointer>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "FramePointer"]
        type CPPFramePointer;
        fn get(self: &CPPFramePointer) -> *const CPPID3v2Frame;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "Frame"]
        type CPPID3v2Frame;
        #[namespace = "taglib_shim"]
        fn Frame_id(frame: &CPPID3v2Frame) -> UniquePtr<CPPByteVector>;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asTextIdentification(
            frame: *const CPPID3v2Frame,
        ) -> *const CPPID3v2TextIdentificationFrame;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asUserTextIdentification(
            frame: *const CPPID3v2Frame,
        ) -> *const CPPID3v2UserTextIdentificationFrame;
        #[namespace = "taglib_shim"]
        unsafe fn Frame_asAttachedPicture(
            frame: *const CPPID3v2Frame,
        ) -> *const CPPID3v2AttachedPictureFrame;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "TextIdentificationFrame"]
        type CPPID3v2TextIdentificationFrame;
        #[namespace = "taglib_shim"]
        fn TextIdentificationFrame_fieldList(
            frame: &CPPID3v2TextIdentificationFrame,
        ) -> UniquePtr<CPPStringList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "UserTextIdentificationFrame"]
        type CPPID3v2UserTextIdentificationFrame;
        #[namespace = "taglib_shim"]
        fn UserTextIdentificationFrame_fieldList(
            frame: &CPPID3v2UserTextIdentificationFrame,
        ) -> UniquePtr<CPPStringList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "AttachedPictureFrame"]
        type CPPID3v2AttachedPictureFrame;
        #[namespace = "taglib_shim"]
        fn AttachedPictureFrame_picture(
            frame: &CPPID3v2AttachedPictureFrame,
        ) -> UniquePtr<CPPByteVector>;

        #[namespace = "TagLib"]
        #[cxx_name = "String"]
        type CPPString;
        fn toCString(self: &CPPString, unicode: bool) -> *const c_char;

        #[namespace = "TagLib"]
        #[cxx_name = "StringList"]
        type CPPStringList;
        #[namespace = "taglib_shim"]
        fn StringList_to_vector(
            string_list: &CPPStringList,
        ) -> UniquePtr<CxxVector<CPPString>>;

        #[namespace = "TagLib"]
        #[cxx_name = "ByteVectorList"]
        type CPPByteVectorList;
        #[namespace = "taglib_shim"]
        fn ByteVectorList_to_vector(list: &CPPByteVectorList) -> UniquePtr<CxxVector<CPPByteVector>>;

        #[namespace = "TagLib"]
        #[cxx_name = "ByteVector"]
        type CPPByteVector;
        fn size(self: &CPPByteVector) -> u32;
        fn data(self: &CPPByteVector) -> *const c_char;

        #[namespace = "TagLib::ID3v1"]
        #[cxx_name = "Tag"]
        type CPPID3v1Tag;

        fn ID3v1Tag_title(tag: &CPPID3v1Tag) -> UniquePtr<CPPString>;
        fn ID3v1Tag_artist(tag: &CPPID3v1Tag) -> UniquePtr<CPPString>;
        fn ID3v1Tag_album(tag: &CPPID3v1Tag) -> UniquePtr<CPPString>;
        fn ID3v1Tag_comment(tag: &CPPID3v1Tag) -> UniquePtr<CPPString>;
        fn ID3v1Tag_genreIndex(tag: &CPPID3v1Tag) -> u32;
        fn ID3v1Tag_year(tag: &CPPID3v1Tag) -> u32;
        fn ID3v1Tag_track(tag: &CPPID3v1Tag) -> u32;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "Tag"]
        type CPPMP4Tag;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "ItemMap"]
        type CPPItemMap;
        fn itemMap<'slf, 'file_ref>(self: &'slf CPPMP4Tag) -> &'file_ref CPPItemMap;
        fn ItemMap_to_entries(map: &CPPItemMap) -> UniquePtr<CxxVector<CPPItemMapEntry>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "ItemMapEntry"]
        type CPPItemMapEntry;
        fn key<'slf, 'file_ref>(self: &'slf CPPItemMapEntry) -> &'file_ref CPPString;
        fn value<'slf, 'file_ref>(self: &'slf CPPItemMapEntry) -> &'file_ref CPPMP4Item;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "Item"]
        type CPPMP4Item;
        fn isValid(self: &CPPMP4Item) -> bool;
        fn toBool(self: &CPPMP4Item) -> bool;
        fn toInt(self: &CPPMP4Item) -> i32;
        fn toByte(self: &CPPMP4Item) -> u8;
        fn toUInt(self: &CPPMP4Item) -> u32;

        fn Item_type(item: &CPPMP4Item) -> u32;
        #[namespace = "taglib_shim"]
        fn Item_toIntPair(item: &CPPMP4Item) -> UniquePtr<CPPIntPair>;
        #[namespace = "taglib_shim"]
        fn Item_toStringList(item: &CPPMP4Item) -> UniquePtr<CPPStringList>;
        #[namespace = "taglib_shim"]
        fn Item_toByteVectorList(item: &CPPMP4Item) -> UniquePtr<CPPByteVectorList>;
        #[namespace = "taglib_shim"]
        fn Item_toCoverArtList(item: &CPPMP4Item) -> UniquePtr<CPPCoverArtList>;
        #[namespace = "taglib_shim"]
        fn Item_toLongLong(item: &CPPMP4Item) -> i64; 

        #[namespace = "taglib_shim"]
        #[cxx_name = "IntPair"]
        type CPPIntPair;
        fn first(self: &CPPIntPair) -> i32;
        fn second(self: &CPPIntPair) -> i32;

        #[namespace = "taglib_shim"]
        #[cxx_name = "CoverArtList"]
        type CPPCoverArtList;
        fn to_vector(self: &CPPCoverArtList) -> UniquePtr<CxxVector<CPPCoverArt>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "CoverArt"]
        type CPPCoverArt;
        fn format(self: &CPPCoverArt) -> u32;
        fn data(self: &CPPCoverArt) -> UniquePtr<CPPByteVector>;
    }
}

#[repr(u8)]
pub enum MP4ItemType {
    Void,
    Bool,
    Int,
    IntPair,
    Byte,
    UInt,
    LongLong,
    StringList,
    ByteVectorList,
    CoverArtList,
}

impl MP4ItemType {
    pub fn from_u32(value: u32) -> Option<Self> {
        match value {
            0 => Some(Self::Void),
            1 => Some(Self::Bool),
            2 => Some(Self::Int),
            3 => Some(Self::IntPair),
            4 => Some(Self::Byte),
            5 => Some(Self::UInt),
            6 => Some(Self::LongLong),
            7 => Some(Self::StringList),
            8 => Some(Self::ByteVectorList),
            9 => Some(Self::CoverArtList),
            _ => None,
        }
    }
}

pub use bridge_impl::*;
