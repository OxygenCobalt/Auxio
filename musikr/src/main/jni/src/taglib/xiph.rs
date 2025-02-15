pub use super::bridge::CPPXiphComment;
pub use super::flac::Picture;
use super::bridge::XiphComment_pictureList_to_vector;
use super::tk::SimplePropertyMap;
use std::pin::Pin;

pub struct XiphComment<'a> {
    this: Pin<&'a mut CPPXiphComment>
}

impl<'a> XiphComment<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPXiphComment>) -> Self {
        Self { this }
    }

    pub fn field_list_map(&'a self) -> SimplePropertyMap<'a> {
        let map = self.this.as_ref().fieldListMap();
        let map_pin = unsafe { Pin::new_unchecked(map) };
        SimplePropertyMap::new(map_pin)
    }

    pub fn picture_list(&mut self) -> Vec<Picture<'a>> {
        let pictures = XiphComment_pictureList_to_vector(self.this.as_mut());
        let mut result = Vec::new();
        for picture_ref in pictures.iter() {
            let picture_ptr = picture_ref.get();
            let picture_ref = unsafe {
                // SAFETY: This pointer is a valid type, and can only used and accessed
                // via this function and thus cannot be mutated, satisfying the aliasing rules.
                picture_ptr.as_ref().unwrap()
            };
            let picture_pin = unsafe { Pin::new_unchecked(picture_ref) };
            result.push(Picture::new(picture_pin));
        }
        result
    }
}
