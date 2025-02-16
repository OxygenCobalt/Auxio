pub use super::bridge::CPPFLACFile;
pub use super::bridge::CPPFLACPicture;
use super::bridge::{CPPPictureList, FLACFile_pictureList, PictureList_to_vector, Picture_data, CPPByteVector};
use super::tk::{ByteVector, OwnedByteVector};
pub use super::xiph::XiphComment;
use super::this::{OwnedThis, RefThisMut, RefThis, This, ThisMut};
use cxx::UniquePtr;
use std::marker::PhantomData;
use std::pin::Pin;

pub struct FLACFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPFLACFile>
}

impl<'file_ref> FLACFile<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPFLACFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&mut self) -> Option<XiphComment<'file_ref>> {
        let tag = self.this.pin_mut().xiphComment(false);
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_this = tag_ref.map(|tag| unsafe { RefThisMut::new(tag) });
        tag_this.map(|this| XiphComment::new(this))
    }

    pub fn picture_list(&mut self) -> Option<PictureList<'file_ref>> {
        let pictures = FLACFile_pictureList(self.this.pin_mut());
        let this = unsafe { OwnedThis::new(pictures) };
        this.map(|this| PictureList::new(this))
    }
}

pub struct PictureList<'file_ref> {
    this: OwnedThis<'file_ref, CPPPictureList>,
}

impl<'file_ref> PictureList<'file_ref> {
    pub(super) fn new(this: OwnedThis<'file_ref, CPPPictureList>) -> Self {
        Self { this }
    }

    pub fn to_vec(&self) -> Vec<Picture<'file_ref>> {
        let pictures = PictureList_to_vector(self.this.pin());
        let mut result = Vec::new();
        for picture_ptr in pictures.iter() {
            let picture_ptr = picture_ptr.get();
            let picture_ref = unsafe {
                // SAFETY: This pointer is a valid type, and can only used and accessed
                // via this function and thus cannot be mutated, satisfying the aliasing rules.
                picture_ptr.as_ref().unwrap()
            };
            let picture_this = unsafe { RefThis::new(picture_ref) };
            result.push(Picture::new(picture_this));
        }
        result
    }
}

pub struct Picture<'file_ref> {
    this: RefThis<'file_ref, CPPFLACPicture>,
}

impl<'file_ref> Picture<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CPPFLACPicture>) -> Self {
        Self { this }
    }

    pub fn data(&self) -> Option<OwnedByteVector<'file_ref>> {
        let data = Picture_data(self.this.pin());
        let this = unsafe { OwnedThis::new(data) };
        this.map(|this| ByteVector::new(this))
    }
}
