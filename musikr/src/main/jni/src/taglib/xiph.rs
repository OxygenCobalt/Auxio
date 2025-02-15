pub use super::bridge::CPPXiphComment;
use super::tk::SimplePropertyMap;
use std::pin::Pin;

pub struct XiphComment<'a> {
    this: Pin<&'a CPPXiphComment>
}

impl<'a> XiphComment<'a> {
    pub(super) fn new(this: Pin<&'a CPPXiphComment>) -> Self {
        Self { this }
    }

    pub fn field_list_map(&self) -> SimplePropertyMap<'a> {
        let map = self.this.fieldListMap();
        let map_pin = unsafe { Pin::new_unchecked(map) };
        SimplePropertyMap::new(map_pin)
    }
}
