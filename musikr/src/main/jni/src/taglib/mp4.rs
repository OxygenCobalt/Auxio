pub use super::bridge::CPPMP4Tag;
use super::bridge::{CPPFile, CPPIntPair, CPPItemMap, CPPItemMapEntry, CPPMP4File, CPPMP4Item, ItemMap_to_entries, MP4ItemType};
use super::tk;
use super::this::{OwnedThis, RefThis, RefThisMut, ThisMut, This};
use std::collections::HashMap;
use std::pin::Pin;

pub struct MP4File<'file_ref> {
    this: RefThisMut<'file_ref, CPPMP4File>,
}

impl<'file_ref> MP4File<'file_ref> {
    pub fn new(this: RefThisMut<'file_ref, CPPMP4File>) -> Self {
        Self { this }
    }

    pub fn tag(&self) -> Option<MP4Tag<'file_ref>> {
        let this = self.this.as_ref();
        let tag = unsafe { this.MP4Tag() };
        let tag_ref = unsafe { tag.as_ref() };
        tag_ref.map(|tag| {
            // SAFETY: The tag pointer is guaranteed to be valid for the lifetime of self
            let tag_this = unsafe { RefThis::new(tag) };
            MP4Tag::new(tag_this)
        })
    }
}

pub struct MP4Tag<'file_ref> {
    this: RefThis<'file_ref, CPPMP4Tag>,
}

impl<'file_ref> MP4Tag<'file_ref> {
    pub fn new(this: RefThis<'file_ref, CPPMP4Tag>) -> Self {
        Self { this }
    }

    pub fn item_map(&self) -> ItemMap<'file_ref> {
        let map: &'file_ref CPPItemMap = self.this.as_ref().itemMap();
        let map_this = unsafe { RefThis::new(map) };
        ItemMap::new(map_this)
    }
}

pub struct ItemMap<'file_ref> {
    this: RefThis<'file_ref, CPPItemMap>,
}

impl<'file_ref> ItemMap<'file_ref> {
    pub fn new(this: RefThis<'file_ref, CPPItemMap>) -> Self {
        Self { this }
    }

    pub fn to_hashmap(&self) -> HashMap<String, MP4Item<'file_ref>> {
        let cxx_vec = ItemMap_to_entries(self.this.as_ref());
        let vec: Vec<(String, MP4Item<'file_ref>)> =
        cxx_vec.iter()
            .map(|property| {
                // SAFETY:
                // - This pin is only used in this unsafe scope.
                // - The pin is used as a C++ this pointer in the ffi call, which does
                //   not change address by C++ semantics.
                // - The values returned are copied and thus not dependent on the address
                //   of self.
                let key_ref = property.key();
                let key_this = unsafe { RefThis::new(key_ref) };
                let key = tk::String::new(key_this).to_string();
                
                let value_ref = property.value();
                let value_this = unsafe { RefThis::new(value_ref) };
                let value = MP4Item::new(value_this);
                
                (key, value)
            })
            .collect();

        HashMap::from_iter(vec)
    }
}

pub struct MP4Item<'file_ref> {
    this: RefThis<'file_ref, CPPMP4Item>,
}

impl<'file_ref> MP4Item<'file_ref> {
    pub fn new(this: RefThis<'file_ref, CPPMP4Item>) -> Self {
        Self { this }
    }

    pub fn data(&self) -> Option<MP4Data<'file_ref>> {
        if !self.this.as_ref().isValid() {
            return None;
        }

        let item_type = MP4ItemType::from_u32(super::bridge::Item_type(self.this.as_ref()));
        item_type.and_then(|item_type| match item_type {
            MP4ItemType::Void => Some(MP4Data::Void),
            MP4ItemType::Bool => Some(MP4Data::Bool(self.this.as_ref().toBool())),
            MP4ItemType::Int => Some(MP4Data::Int(self.this.as_ref().toInt())),
            MP4ItemType::IntPair => {
                let pair = super::bridge::Item_toIntPair(self.this.as_ref());
                let pair_this = unsafe { OwnedThis::new(pair) };
                pair_this.map(|this| MP4Data::IntPair(IntPair::new(this)))
            },
            MP4ItemType::Byte => Some(MP4Data::Byte(self.this.as_ref().toByte())),
            MP4ItemType::UInt => Some(MP4Data::UInt(self.this.as_ref().toUInt())),
            MP4ItemType::LongLong => Some(MP4Data::LongLong(super::bridge::Item_toLongLong(self.this.as_ref()))),
            MP4ItemType::StringList => {
                let string_list = super::bridge::Item_toStringList(self.this.as_ref());
                let string_list_this = unsafe { OwnedThis::new(string_list) };
                string_list_this.map(|this| MP4Data::StringList(tk::StringList::new(this)))
            },
            MP4ItemType::ByteVectorList => {
                let byte_vector_list = super::bridge::Item_toByteVectorList(self.this.as_ref());
                let byte_vector_list_this = unsafe { OwnedThis::new(byte_vector_list) };
                byte_vector_list_this.map(|this| MP4Data::ByteVectorList(tk::ByteVectorList::new(this)))
            },
            MP4ItemType::CoverArtList => {
                let cover_art_list = super::bridge::Item_toCoverArtList(self.this.as_ref());
                let cover_art_list_this = unsafe { OwnedThis::new(cover_art_list) };
                cover_art_list_this.map(|this| MP4Data::CoverArtList(CoverArtList::new(this)))
            }
        })
    }
}

pub struct CoverArtList<'file_ref> {
    this: OwnedThis<'file_ref, super::bridge::CPPCoverArtList>,
}

impl<'file_ref> CoverArtList<'file_ref> {
    pub fn new(this: OwnedThis<'file_ref, super::bridge::CPPCoverArtList>) -> Self {
        Self { this }
    }

    pub fn to_vec(&self) -> Vec<CoverArt> {
        let cover_arts = self.this.as_ref().to_vector();
        cover_arts
            .iter()
            .map(|ca| {
                let format = CoverArtFormat::from_u32(ca.format());
                let data = ca.data();
                let data_this = unsafe { RefThis::new(&*data) };
                let data = tk::ByteVector::new(data_this).to_vec();
                CoverArt { format, data }
            })
            .collect()
    }
}

pub struct IntPair<'file_ref> {
    this: OwnedThis<'file_ref, CPPIntPair>,
}

impl<'file_ref> IntPair<'file_ref> {
    pub fn new(this: OwnedThis<'file_ref, CPPIntPair>) -> Self {
        Self { this }
    }

    pub fn to_tuple(&self) -> Option<(i32, i32)> {
        let this = self.this.as_ref();
        let first = this.first();
        let second = this.second();
        Some((first, second))
    }
}

pub enum MP4Data<'file_ref> {
    Void,
    Bool(bool),
    Int(i32),
    Byte(u8),
    UInt(u32),
    LongLong(i64),
    IntPair(IntPair<'file_ref>),
    StringList(tk::OwnedStringList<'file_ref>),
    ByteVectorList(tk::OwnedByteVectorList<'file_ref>),
    CoverArtList(CoverArtList<'file_ref>),
}

#[derive(Debug, Clone, PartialEq)]
pub struct CoverArt {
    format: CoverArtFormat,
    data: Vec<u8>,
}

impl CoverArt {
    pub fn new(format: CoverArtFormat, data: Vec<u8>) -> Self {
        Self { format, data }
    }

    pub fn format(&self) -> CoverArtFormat {
        self.format
    }

    pub fn data(&self) -> &[u8] {
        &self.data
    }
}

#[derive(Debug, Clone, Copy, PartialEq)]
pub enum CoverArtFormat {
    Unknown,
    BMP,
    JPEG,
    GIF,
    PNG,
}

impl CoverArtFormat {
    fn from_u32(value: u32) -> Self {
        match value {
            0 => CoverArtFormat::Unknown,
            1 => CoverArtFormat::BMP,
            2 => CoverArtFormat::JPEG,
            3 => CoverArtFormat::GIF,
            4 => CoverArtFormat::PNG,
            _ => CoverArtFormat::Unknown,
        }
    }
}
