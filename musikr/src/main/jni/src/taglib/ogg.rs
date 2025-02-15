pub use super::bridge::{CPPOpusFile, CPPVorbisFile};
use super::xiph::XiphComment;
use std::pin::Pin;

pub struct VorbisFile<'file_ref> {
    this: Pin<&'file_ref mut CPPVorbisFile>
}

impl<'file_ref> VorbisFile<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPVorbisFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment<'file_ref>> {
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

pub struct OpusFile<'file_ref> {
    this: Pin<&'file_ref mut CPPOpusFile>
}

impl<'file_ref> OpusFile<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPOpusFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment<'file_ref>> {
        let this = self.this.as_ref();
        let tag = this.opusTag();
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }
}
