use super::bridge::{self, CPPWAVFile};
use super::id3v2::ID3v2Tag;
use super::this::{RefThisMut, This, ThisMut};
use std::pin::Pin;

pub struct WAVFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPWAVFile>,
}

impl<'file_ref> WAVFile<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPWAVFile>) -> Self {
        Self { this }
    }

    pub fn id3v2_tag(&mut self) -> Option<ID3v2Tag<'file_ref>> {
        let tag = self.this.as_ref().WAVID3v2Tag();
        let tag_ref = unsafe { tag.as_mut() };
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| ID3v2Tag::new(this))
    }
} 