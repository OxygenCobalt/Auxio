pub use super::bridge::CPPFLACFile;
pub use super::bridge::CPPFLACPicture;
pub use super::xiph::XiphComment;
use super::bridge::{CPPPictureList, PictureList_to_vector, FLACFile_pictureList, Picture_data};
use super::tk::ByteVector;
use std::marker::PhantomData;
use cxx::UniquePtr;
use std::pin::Pin;

pub struct FLACFile<'file_ref> {
    this: Pin<&'file_ref mut CPPFLACFile>
}

impl<'file_ref> FLACFile<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPFLACFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&mut self) -> Option<XiphComment<'file_ref>> {
        let this = self.this.as_mut();
        let tag = this.xiphComment(false);
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }

    pub fn picture_list(&mut self) -> PictureList<'file_ref> {
        let pictures = FLACFile_pictureList(self.this.as_mut());
        PictureList::new(pictures)
    }
}

pub struct PictureList<'file_ref> {
    // PictureList is implicitly tied to the lifetime of the file_ref, despite us technically
    // """""owning"""" it.
    _data: PhantomData<&'file_ref CPPFLACPicture>,
    // Only in a UniquePtr because we can't marshal over ownership of the PictureList by itself over cxx.
    this: UniquePtr<CPPPictureList>,
}

impl<'file_ref> PictureList<'file_ref> {
    pub(super) fn new(this: UniquePtr<CPPPictureList>) -> Self {
        Self { _data: PhantomData, this }
    }

    pub fn to_vec(&self) -> Vec<Picture<'file_ref>> {
        let pictures = PictureList_to_vector(unsafe {
            // SAFETY: This pin is only used in this unsafe scope.
            // The pin is used as a C++ this pointer in the ffi call, which does
            // not change address by C++ semantics.
            Pin::new_unchecked(self.this.as_ref().unwrap())
        });

        let mut result = Vec::new();
        for picture_ptr in pictures.iter() {
            let picture_ptr = picture_ptr.get();
            let picture_ref = unsafe {
                // SAFETY: This pointer is a valid type, and can only used and accessed
                // via this function and thus cannot be mutated, satisfying the aliasing rules.
                picture_ptr.as_ref().unwrap()
            };
            let picture_pin = unsafe { Pin::new_unchecked(picture_ref) };
            result.push(Picture::new(picture_pin));
        }
        result
    }
}

pub struct Picture<'file_ref> {
    this: Pin<&'file_ref CPPFLACPicture>
}

impl<'file_ref> Picture<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPFLACPicture>) -> Self {
        Self { this }
    }

    pub fn data(&self) -> ByteVector<'file_ref> {
        let data = Picture_data(self.this);
        ByteVector::new(data)
    }
}
