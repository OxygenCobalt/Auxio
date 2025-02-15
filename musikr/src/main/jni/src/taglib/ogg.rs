pub use super::bridge::{CPPOpusFile, CPPVorbisFile};
use super::xiph::XiphComment;
use std::pin::Pin;

pub struct VorbisFile<'a> {
    this: Pin<&'a mut CPPVorbisFile>
}

impl<'a> VorbisFile<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPVorbisFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment> {
        let this = self.this.as_ref();
        let tag = this.vorbisTag();
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }
}

pub struct OpusFile<'a> {
    this: Pin<&'a mut CPPOpusFile>
}

impl <'a> OpusFile<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPOpusFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment<'a>> {
        let this = self.this.as_ref();
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            this.opusTag()
        };
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }
}
