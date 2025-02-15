pub use super::bridge::CPPFLACFile;
pub use super::bridge::CPPFLACPicture;
pub use super::xiph::XiphComment;
use super::bridge::{CPPPictureList, PictureList_to_vector, FLACFile_pictureList, Picture_data};
use super::tk::ByteVector;
use std::marker::PhantomData;
use cxx::UniquePtr;
use std::pin::Pin;

pub struct FLACFile<'a> {
    this: Pin<&'a mut CPPFLACFile>
}

impl<'a> FLACFile<'a> {
    pub(super) fn new(this: Pin<&'a mut CPPFLACFile>) -> Self {
        Self { this }
    }

    pub fn xiph_comments(&mut self) -> Option<XiphComment> {
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

    pub fn picture_list(&mut self) -> PictureList {
        let pictures = FLACFile_pictureList(self.this.as_mut());
        PictureList::new(pictures)
    }
}

pub struct PictureList<'a> {
    // PictureList is implicitly tied to the lifetime of the parent that owns it,
    // so we need to track that lifetime.
    _data: PhantomData<&'a CPPFLACPicture>,
    this: UniquePtr<CPPPictureList>,
}

impl<'a> PictureList<'a> {
    pub(super) fn new(this: UniquePtr<CPPPictureList>) -> Self {
        Self { _data: PhantomData, this }
    }

    pub fn to_vec(&self) -> Vec<Picture> {
        let pictures = PictureList_to_vector(unsafe {
            // SAFETY: This pin is only used in this unsafe scope.
            // The pin is used as a C++ this pointer in the ffi call, which does
            // not change address by C++ semantics.
            Pin::new_unchecked(self.this.as_ref().unwrap())
        });

        let mut result = Vec::new();
        for picture_ref in pictures.iter() {
            let picture_ptr = picture_ref.inner();
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

pub struct Picture<'a> {
    this: Pin<&'a CPPFLACPicture>
}

impl<'a> Picture<'a> {
    pub(super) fn new(this: Pin<&'a CPPFLACPicture>) -> Self {
        Self { this }
    }

    pub fn data(&self) -> ByteVector<'a> {
        let data = Picture_data(self.this);
        ByteVector::new(data)
    }
}
