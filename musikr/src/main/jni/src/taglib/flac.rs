pub use super::bridge::CPPFLACFile;
pub use super::xiph::XiphComment;
use std::pin::Pin;

pub struct FLACFile<'a> {
    this: Pin<&'a mut CPPFLACFile>
}

impl<'a> FLACFile<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPFLACFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&mut self) -> Option<XiphComment> {
        let this = self.this.as_mut();
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            // SAFETY: This is a C++ FFI function ensured to call correctly.
            this.xiphComment(false)
        };
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }
}