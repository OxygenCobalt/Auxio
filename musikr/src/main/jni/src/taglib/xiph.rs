pub use super::bridge::{OpusFile, VorbisFile, FLACFile, XiphComment};
use super::tk::SimplePropertyMap;
use std::pin::Pin;

impl OpusFile {
    pub fn xiph_comments(&self) -> Option<&XiphComment> {
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.opusThisTag()
        };
        unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        }
    }
}

impl VorbisFile {
    pub fn xiph_comments(&self) -> Option<&XiphComment> {
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.vorbisThisTag()
        };
        unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        }
    }
}

impl FLACFile {
    pub fn xiph_comments(&mut self) -> Option<&XiphComment> {
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            // SAFETY: This is a C++ FFI function ensured to call correctly.
            this.flacThisXiphComment(false)
        };
        unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_ref()
        }
    }
}

impl XiphComment {
    pub fn field_list_map(&self) -> &SimplePropertyMap {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a reference that does not depend on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisFieldListMap()
        }
    }
}
