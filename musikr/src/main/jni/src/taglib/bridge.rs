use super::iostream::DynIOStream;

pub trait TagLibAllocated {}

pub trait TagLibRef {}

pub trait TagLibShared {}

#[cxx::bridge]
mod bridge_impl {
    // Expose Rust IOStream to C++
    extern "Rust" {
        #[cxx_name = "RsIOStream"]
        type DynIOStream<'io_stream>;

        fn name(self: &DynIOStream<'_>) -> String;
        fn read(self: &mut DynIOStream<'_>, buffer: &mut [u8]) -> usize;
        fn write(self: &mut DynIOStream<'_>, data: &[u8]);
        fn seek(self: &mut DynIOStream<'_>, offset: i64, whence: i32);
        fn truncate(self: &mut DynIOStream<'_>, length: i64);
        fn tell(self: &DynIOStream<'_>) -> i64;
        fn length(self: &DynIOStream<'_>) -> i64;
        fn is_readonly(self: &DynIOStream<'_>) -> bool;
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

        // CORE

        #[namespace = "TagLib"]
        #[cxx_name = "IOStream"]
        type CPPIOStream<'io_stream>;
        unsafe fn wrap_RsIOStream<'io_stream>(
            stream: *mut DynIOStream<'io_stream>,
        ) -> UniquePtr<CPPIOStream<'io_stream>>;

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

        // XIPH

        #[namespace = "TagLib::Ogg::Vorbis"]
        #[cxx_name = "File"]
        type CPPVorbisFile;
        #[cxx_name = "tag"]
        fn vorbisTag(self: &CPPVorbisFile) -> *mut CPPXiphComment;

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

        #[namespace = "TagLib::FLAC"]
        #[cxx_name = "Picture"]
        type CPPFLACPicture;
        #[namespace = "taglib_shim"]
        fn Picture_type(picture: &CPPFLACPicture) -> u32;
        #[namespace = "taglib_shim"]
        fn Picture_data(picture: &CPPFLACPicture) -> UniquePtr<CPPByteVector>;
        // XIPHComment

        #[namespace = "TagLib::Ogg"]
        #[cxx_name = "XiphComment"]
        type CPPXiphComment;
        // Explicit lifecycle definition to state while the Pin is temporary, the CPPFieldListMap
        // ref returned actually has the same lifetime as the CPPXiphComment.
        fn fieldListMap(self: &CPPXiphComment) -> &CPPFieldListMap;
        #[namespace = "taglib_shim"]
        fn XiphComment_pictureList(comment: Pin<&mut CPPXiphComment>) -> UniquePtr<CPPPictureList>;

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
        fn key(self: &CPPFieldListEntry) -> UniquePtr<CPPString>;
        fn value(self: &CPPFieldListEntry) -> UniquePtr<CPPStringList>;

        // MPEG

        #[namespace = "TagLib::MPEG"]
        #[cxx_name = "File"]
        type CPPMPEGFile;
        #[cxx_name = "ID3v1Tag"]
        fn MPEGID3v1Tag(self: Pin<&mut CPPMPEGFile>, create: bool) -> *mut CPPID3v1Tag;
        #[cxx_name = "ID3v2Tag"]
        fn MPEGID3v2Tag(self: Pin<&mut CPPMPEGFile>, create: bool) -> *mut CPPID3v2Tag;

        // MP4

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "File"]
        type CPPMP4File;
        #[cxx_name = "tag"]
        fn MP4Tag(self: &CPPMP4File) -> *mut CPPMP4Tag;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "Tag"]
        type CPPMP4Tag;

        #[namespace = "TagLib::MP4"]
        #[cxx_name = "ItemMap"]
        type CPPItemMap;
        fn itemMap(self: &CPPMP4Tag) -> &CPPItemMap;
        fn ItemMap_to_entries(map: &CPPItemMap) -> UniquePtr<CxxVector<CPPItemMapEntry>>;

        #[namespace = "taglib_shim"]
        #[cxx_name = "ItemMapEntry"]
        type CPPItemMapEntry;
        fn key(self: &CPPItemMapEntry) -> UniquePtr<CPPString>;
        fn value(self: &CPPItemMapEntry) -> UniquePtr<CPPMP4Item>;

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

        #[namespace = "TagLib::RIFF::WAV"]
        #[cxx_name = "File"]
        type CPPWAVFile;
        #[cxx_name = "ID3v2Tag"]
        fn WAVID3v2Tag(self: &CPPWAVFile) -> *mut CPPID3v2Tag;

        // ID3v1

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

        // ID3v2

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "Tag"]
        type CPPID3v2Tag;
        #[namespace = "taglib_shim"]
        fn Tag_frameList(tag: &CPPID3v2Tag) -> UniquePtr<CPPID3v2FrameList>;

        #[namespace = "TagLib::ID3v2"]
        #[cxx_name = "FrameList"]
        type CPPID3v2FrameList;
        #[namespace = "taglib_shim"]
        fn FrameList_to_vector(list: &CPPID3v2FrameList) -> UniquePtr<CxxVector<CPPFramePointer>>;

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
        fn AttachedPictureFrame_type(frame: &CPPID3v2AttachedPictureFrame) -> u32;
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
        fn StringList_to_vector(string_list: &CPPStringList) -> UniquePtr<CxxVector<CPPString>>;

        #[namespace = "TagLib"]
        #[cxx_name = "ByteVector"]
        type CPPByteVector;
        fn size(self: &CPPByteVector) -> u32;
        fn data(self: &CPPByteVector) -> *const c_char;

        #[namespace = "TagLib"]
        #[cxx_name = "ByteVectorList"]
        type CPPByteVectorList;
        #[namespace = "taglib_shim"]
        fn ByteVectorList_to_vector(
            list: &CPPByteVectorList,
        ) -> UniquePtr<CxxVector<CPPByteVector>>;
    }
}

#[repr(u8)]
pub enum PictureType {
    Other,
    FileIcon,
    OtherFileIcon,
    FrontCover,
    BackCover,
    LeafletPage,
    Media,
    LeadArtist,
    Artist,
    Conductor,
    Band,
    Composer,
    Lyricist,
    RecordingLocation,
    DuringRecording,
    DuringPerformance,
    MovieScreenCapture,
    ColoredFish,
    Illustration,
    BandLogo,
    PublisherLogo,
}

impl PictureType {
    pub fn from_u32(value: u32) -> Option<Self> {
        match value {
            0 => Some(Self::Other),
            1 => Some(Self::FileIcon),
            2 => Some(Self::OtherFileIcon),
            3 => Some(Self::FrontCover),
            4 => Some(Self::BackCover),
            5 => Some(Self::LeafletPage),
            6 => Some(Self::Media),
            7 => Some(Self::LeadArtist),
            8 => Some(Self::Artist),
            9 => Some(Self::Conductor),
            10 => Some(Self::Band),
            11 => Some(Self::Composer),
            12 => Some(Self::Lyricist),
            13 => Some(Self::RecordingLocation),
            14 => Some(Self::DuringRecording),
            15 => Some(Self::DuringPerformance),
            16 => Some(Self::MovieScreenCapture),
            17 => Some(Self::ColoredFish),
            18 => Some(Self::Illustration),
            19 => Some(Self::BandLogo),
            20 => Some(Self::PublisherLogo),
            _ => None,
        }
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

impl TagLibAllocated for CPPFileRef {}

impl TagLibRef for CPPFile {}

impl TagLibRef for CppAudioProperties {}

// All of the File implementations are also TagLibRef and TagLibAllocated

impl TagLibRef for CPPVorbisFile {}

impl TagLibRef for CPPOpusFile {}

impl TagLibRef for CPPFLACFile {}

impl TagLibShared for CPPPictureList {}

impl TagLibShared for CPPFLACPicture {}

impl TagLibRef for CPPMPEGFile {}

impl TagLibRef for CPPMP4File {}

impl TagLibRef for CPPMP4Tag {}

impl TagLibRef for CPPItemMap {}

impl TagLibRef for CPPItemMapEntry {}

impl TagLibRef for CPPFieldListMap {}

impl TagLibRef for CPPFieldListEntry {}

impl TagLibRef for CPPWAVFile {}

impl TagLibRef for CPPXiphComment {}

impl TagLibRef for CPPID3v1Tag {}

impl TagLibRef for CPPID3v2Tag {}

impl TagLibShared for CPPID3v2FrameList {}

impl TagLibRef for CPPID3v2Frame {}

impl TagLibRef for CPPID3v2TextIdentificationFrame {}

impl TagLibRef for CPPID3v2UserTextIdentificationFrame {}

impl TagLibRef for CPPID3v2AttachedPictureFrame {}

impl TagLibShared for CPPMP4Item {}

impl TagLibShared for CPPCoverArt {}

impl TagLibShared for CPPIntPair {}

impl TagLibShared for CPPString {}

impl TagLibShared for CPPStringList {}

impl TagLibShared for CPPByteVector {}

impl TagLibShared for CPPByteVectorList {}

impl TagLibShared for CPPCoverArtList {}

impl<T: TagLibShared> TagLibRef for T {}
impl<T: TagLibRef> TagLibAllocated for T {}
