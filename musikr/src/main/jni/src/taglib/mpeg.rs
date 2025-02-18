use super::bridge::{CPPMPEGFile};
use super::id3v1::ID3v1Tag;
use super::id3v2::ID3v2Tag;
use super::this::{RefThisMut, ThisMut};

pub struct MPEGFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPMPEGFile>,
}

impl<'file_ref> MPEGFile<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPMPEGFile>) -> Self {
        Self { this }
    }

    pub fn id3v1_tag(&mut self) -> Option<ID3v1Tag<'file_ref>> {
        let tag = self.this.pin_mut().MPEGID3v1Tag(false);
        let tag_ref = unsafe { tag.as_mut() };
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| ID3v1Tag::new(this))
    }

    pub fn id3v2_tag(&mut self) -> Option<ID3v2Tag<'file_ref>> {
        let tag = self.this.pin_mut().MPEGID3v2Tag(false);
        let tag_ref = unsafe { tag.as_mut() };
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| ID3v2Tag::new(this))
    }
}
