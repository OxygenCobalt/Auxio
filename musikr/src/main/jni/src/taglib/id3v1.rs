use super::bridge::{self, CPPID3v1Tag};
use super::this::{RefThisMut, RefThis, This, OwnedThis};
use super::tk::{String, OwnedString};

pub struct ID3v1Tag<'file_ref> {
    this: RefThisMut<'file_ref, CPPID3v1Tag>,
}

impl<'file_ref> ID3v1Tag<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPID3v1Tag>) -> Self {
        Self { this }
    }

    pub fn title(&self) -> OwnedString<'file_ref> {
        let title = bridge::ID3v1Tag_title(self.this.as_ref());
        let this = OwnedThis::new(title).unwrap();
        String::new(this)
    }

    pub fn artist(&self) -> OwnedString<'file_ref> {
        let artist = bridge::ID3v1Tag_artist(self.this.as_ref());
        let this = OwnedThis::new(artist).unwrap();
        String::new(this)
    }

    pub fn album(&self) -> OwnedString<'file_ref> {
        let album = bridge::ID3v1Tag_album(self.this.as_ref());
        let this = OwnedThis::new(album).unwrap();
        String::new(this)
    }

    pub fn comment(&self) -> OwnedString<'file_ref> {
        let comment = bridge::ID3v1Tag_comment(self.this.as_ref());
        let this = OwnedThis::new(comment).unwrap();
        String::new(this)
    }

    pub fn genre_index(&self) -> u32 {
        bridge::ID3v1Tag_genreIndex(self.this.as_ref())
    }

    pub fn year(&self) -> u32 {
        bridge::ID3v1Tag_year(self.this.as_ref())
    }

    pub fn track(&self) -> u32 {
        bridge::ID3v1Tag_track(self.this.as_ref())
    }
} 