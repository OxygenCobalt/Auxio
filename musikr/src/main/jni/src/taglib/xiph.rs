pub use super::bridge::CPPXiphComment;
use super::bridge::{CPPFieldListMap, FieldListMap_to_entries, XiphComment_pictureList};
pub use super::flac::PictureList;
use super::tk;
use super::this::{OwnedThis, RefThis, RefThisMut, ThisMut, This};
use std::collections::HashMap;
use std::pin::Pin;

pub struct XiphComment<'file_ref> {
    this: RefThisMut<'file_ref, CPPXiphComment>,
}

impl<'file_ref> XiphComment<'file_ref> {
    pub fn new(this: RefThisMut<'file_ref, CPPXiphComment>) -> Self {
        Self { this }
    }

    pub fn field_list_map<'slf>(&'slf self) -> FieldListMap<'file_ref> {
        let map: &'slf CPPFieldListMap = self.this.pin().fieldListMap();
        // CPPFieldListMap exists for as long as the XiphComment, so we can transmute it
        // to the file_ref lifetime.
        let extended_map: &'file_ref CPPFieldListMap = unsafe { std::mem::transmute(map) };
        let map_pin = unsafe { Pin::new_unchecked(extended_map) };
        FieldListMap::new(map_pin)
    }

    pub fn picture_list(&mut self) -> Option<PictureList<'file_ref>> {
        let pictures = XiphComment_pictureList(self.this.pin_mut());
        let pictures_this = unsafe { OwnedThis::new(pictures) };
        pictures_this.map(|this| PictureList::new(this))
    }
}

pub struct FieldListMap<'file_ref> {
    this: Pin<&'file_ref CPPFieldListMap>,
}

impl<'file_ref> FieldListMap<'file_ref> {
    pub fn new(this: Pin<&'file_ref CPPFieldListMap>) -> Self {
        Self { this }
    }
}

impl<'file_ref> FieldListMap<'file_ref> {
    pub fn to_hashmap(&self) -> HashMap<String, Vec<String>> {
        let cxx_vec = FieldListMap_to_entries(self.this);
        cxx_vec
            .iter()
            .map(|property| {
                // SAFETY:
                // - This pin is only used in this unsafe scope.
                // - The pin is used as a C++ this pointer in the ffi call, which does
                //   not change address by C++ semantics.
                // - The values returned are copied and thus not dependent on the address
                //   of self.
                let property_pin = unsafe { Pin::new_unchecked(property) };
                let key_ref = property_pin.key();
                let key_this = unsafe { RefThis::new(key_ref) };
                let key = tk::String::new(key_this).to_string();
                let value_ref = property_pin.value();
                let value_this = unsafe { RefThis::new(value_ref) };
                let value = tk::StringList::new(value_this).to_vec();
                (key, value)
            })
            .collect()
    }
}
