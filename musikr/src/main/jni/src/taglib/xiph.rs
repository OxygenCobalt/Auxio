pub use super::bridge::CPPXiphComment;
pub use super::flac::PictureList;
use super::bridge::{CPPFieldListMap, FieldListMap_to_entries, XiphComment_pictureList};
use super::tk;
use std::pin::Pin;
use std::collections::HashMap;

pub struct XiphComment<'file_ref> {
    this: Pin<&'file_ref mut CPPXiphComment>
}

impl<'file_ref> XiphComment<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPXiphComment>) -> Self {
        Self { this }
    }

    pub fn field_list_map<'slf>(&'slf self) -> FieldListMap<'file_ref> {
        // To call the method we need, we have to get our mut reference down to an immutable
        // reference. The safe API can do this, but shortens the lifecycle to at most self, even
        // though the reference really lives as long as file_ref. Sadly, this requires us to transmute 
        // to extend the lifecycle back. This new pointer is really unsafe (we now have both a mut
        // and an immutable reference to the same object), but it's dropped after this call.
        // The value returned is unable to actually mutate this object, so it's safe.
        let this_ref: &'slf CPPXiphComment = self.this.as_ref().get_ref();
        let extended_ref: &'file_ref CPPXiphComment = unsafe { std::mem::transmute(this_ref) };
        let this: Pin<&'file_ref CPPXiphComment> = unsafe { Pin::new_unchecked(extended_ref) };
        let map = this.fieldListMap();
        let map_pin = unsafe { Pin::new_unchecked(map) };
        FieldListMap::new(map_pin)
    }

    pub fn picture_list(&mut self) -> PictureList<'file_ref> {
        let pictures = XiphComment_pictureList(self.this.as_mut());
        PictureList::new(pictures)
    }
}

pub struct FieldListMap<'file_ref> {
    this: Pin<&'file_ref CPPFieldListMap>,
}

impl<'file_ref> FieldListMap<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPFieldListMap>) -> Self {
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
                let key_pin = unsafe { Pin::new_unchecked(key_ref) };
                let key = tk::String::new(key_pin).to_string();
                let value_ref = property_pin.value();
                let value_pin = unsafe { Pin::new_unchecked(value_ref) };
                let value = tk::StringList::reference(value_pin).to_vec();
                (key, value)
            })
            .collect()
    }
}
