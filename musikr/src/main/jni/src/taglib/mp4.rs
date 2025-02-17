pub use super::bridge::CPPMP4Tag;
use super::bridge::{CPPItemMap, ItemMap_to_entries, CPPItemMapEntry};
use super::tk;
use super::this::{OwnedThis, RefThis, RefThisMut, ThisMut, This};
use std::collections::HashMap;
use std::pin::Pin;

pub struct MP4Tag<'file_ref> {
    this: RefThisMut<'file_ref, CPPMP4Tag>,
}

impl<'file_ref> MP4Tag<'file_ref> {
    pub fn new(this: RefThisMut<'file_ref, CPPMP4Tag>) -> Self {
        Self { this }
    }

    pub fn item_map<'slf>(&'slf self) -> ItemMap<'file_ref> {
        let map: &'file_ref CPPItemMap = self.this.pin().itemMap();
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

    pub fn to_hashmap(&self) -> HashMap<String, ()> {
        let cxx_vec = ItemMap_to_entries(self.this.pin());
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
                // For now, we're just returning () as a placeholder for MP4::Item
                (key, ())
            })
            .collect()
    }
} 