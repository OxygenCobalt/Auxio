pub use super::bridge::{CPPOpusFile, CPPVorbisFile};
use super::xiph::XiphComment;
use super::this::{RefThisMut, RefThis, This};
use std::pin::Pin;

pub struct VorbisFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPVorbisFile>,
}

impl<'file_ref> VorbisFile<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPVorbisFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment<'file_ref>> {
        let tag = self.this.pin().vorbisTag();
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_this = tag_ref.map(|tag| unsafe { RefThisMut::new(tag) });
        tag_this.map(|this| XiphComment::new(this))
    }
}

pub struct OpusFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPOpusFile>,
}

impl<'file_ref> OpusFile<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPOpusFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&self) -> Option<XiphComment<'file_ref>> {
        let tag = self.this.pin().opusTag();
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_this = tag_ref.map(|tag| unsafe { RefThisMut::new(tag) });
        tag_this.map(|this| XiphComment::new(this))
    }
}
