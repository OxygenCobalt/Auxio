use super::bridge::{self, CPPMPEGFile};
use super::id3v2::ID3v2Tag;
use std::pin::Pin;

pub struct MPEGFile<'file_ref> {
    this: Pin<&'file_ref mut CPPMPEGFile>,
}

impl<'file_ref> MPEGFile<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPMPEGFile>) -> Self {
        Self { this }
    }

    pub fn id3v2_tag(&mut self) -> Option<ID3v2Tag<'file_ref>> {
        let tag = self.this.as_mut().ID3v2Tag(false);
        let tag_ref = unsafe { tag.as_ref() };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| ID3v2Tag::new(tag))
    }
}
