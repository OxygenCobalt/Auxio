pub use super::bridge::CPPFLACFile;
pub use super::bridge::CPPFLACPicture;
pub use super::bridge::PictureType;
use super::bridge::{
    CPPPictureList, FLACFile_pictureList, PictureList_to_vector, Picture_data, Picture_type,
};
use super::id3v1::ID3v1Tag;
use super::id3v2::ID3v2Tag;
use super::this::{OwnedThis, RefThis, RefThisMut, ThisMut};
use super::tk::{ByteVector, OwnedByteVector};
use super::xiph::XiphComment;

pub struct FLACFile<'file_ref> {
    this: RefThisMut<'file_ref, CPPFLACFile>,
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
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| XiphComment::new(this))
    }

    pub fn picture_list(&mut self) -> FLACPictureList<'file_ref> {
        let pictures = FLACFile_pictureList(self.this.pin_mut());
        let this = OwnedThis::new(pictures).unwrap();
        FLACPictureList::new(this)
    }

    pub fn id3v1_tag(&mut self) -> Option<ID3v1Tag<'file_ref>> {
        let tag = self.this.pin_mut().FLACID3v1Tag(false);
        let tag_ref = unsafe { tag.as_mut() };
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| ID3v1Tag::new(this))
    }

    pub fn id3v2_tag(&mut self) -> Option<ID3v2Tag<'file_ref>> {
        let tag = self.this.pin_mut().FLACID3v2Tag(false);
        let tag_ref = unsafe { tag.as_mut() };
        let tag_this = tag_ref.map(|tag| RefThisMut::new(tag));
        tag_this.map(|this| ID3v2Tag::new(this))
    }
}

pub struct FLACPictureList<'file_ref> {
    this: OwnedThis<'file_ref, CPPPictureList>,
}

impl<'file_ref> FLACPictureList<'file_ref> {
    pub(super) fn new(this: OwnedThis<'file_ref, CPPPictureList>) -> Self {
        Self { this }
    }

    pub fn to_vec(&self) -> Vec<FLACPicture<'file_ref>> {
        let pictures = PictureList_to_vector(self.this.as_ref());
        let mut result = Vec::new();
        for picture_ptr in pictures.iter() {
            let picture_ptr = picture_ptr.get();
            let picture_ref = unsafe {
                // SAFETY: This pointer is a valid type, and can only used and accessed
                // via this function and thus cannot be mutated, satisfying the aliasing rules.
                picture_ptr.as_ref().unwrap()
            };
            let picture_this = RefThis::new(picture_ref);
            result.push(FLACPicture { this: picture_this });
        }
        result
    }
}

pub struct FLACPicture<'file_ref> {
    this: RefThis<'file_ref, CPPFLACPicture>,
}

impl<'file_ref> FLACPicture<'file_ref> {
    pub fn picture_type(&self) -> Option<PictureType> {
        let picture_type = Picture_type(self.this.as_ref());
        PictureType::from_u32(picture_type)
    }

    pub fn data(&self) -> OwnedByteVector<'file_ref> {
        let data = Picture_data(self.this.as_ref());
        let this = OwnedThis::new(data).unwrap();
        ByteVector::new(this)
    }
}
