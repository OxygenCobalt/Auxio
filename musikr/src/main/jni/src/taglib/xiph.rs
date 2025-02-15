pub use super::bridge::CPPXiphComment;
pub use super::flac::PictureList;
use super::bridge::{CPPFieldListMap, FieldListMap_to_entries, XiphComment_pictureList};
use super::tk;
use std::pin::Pin;
use std::collections::HashMap;

pub struct XiphComment<'a> {
    this: Pin<&'a mut CPPXiphComment>
}

impl<'a> XiphComment<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPXiphComment>) -> Self {
        Self { this }
    }

    pub fn field_list_map(&'a self) -> FieldListMap<'a> {
        let map = self.this.as_ref().fieldListMap();
        let map_pin = unsafe { Pin::new_unchecked(map) };
        FieldListMap::new(map_pin)
    }

    pub fn picture_list(&mut self) -> PictureList<'a> {
        let pictures = XiphComment_pictureList(self.this.as_mut());
        PictureList::new(pictures)
    }
}

pub struct FieldListMap<'a> {
    this: Pin<&'a CPPFieldListMap>,
}

impl<'a> FieldListMap<'a> {
    pub(super) fn new(this: Pin<&'a CPPFieldListMap>) -> Self {
        Self { this }
    }
}

impl<'a> FieldListMap<'a> {
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
                let key_pin = unsafe { Pin::new_unchecked(key_ref) };
                let key = tk::String::new(key_pin).to_string();
                let value_ref = property_pin.value();
                let value_pin = unsafe { Pin::new_unchecked(value_ref) };
                let value = tk::StringList::new(value_pin).to_vec();
                (key, value)
            })
            .collect()
    }
}
