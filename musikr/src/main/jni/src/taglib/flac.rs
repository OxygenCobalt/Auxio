pub use super::bridge::CPPFLACFile;
pub use super::bridge::CPPFLACPicture;
pub use super::xiph::XiphComment;
use super::bridge::{FLACFile_pictureList_to_vector, String_to_string, ByteVector_to_bytes, Picture_mimeType, Picture_description, Picture_data};
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
        let tag = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a pointer that does not depend on the address of self.
            // SAFETY: This is a C++ FFI function ensured to call correctly.
            this.xiphComment(false)
        };
        let tag_ref = unsafe {
            // SAFETY: This pointer is a valid type, and can only used and accessed
            // via this function and thus cannot be mutated, satisfying the aliasing rules.
            tag.as_mut()
        };
        let tag_pin = tag_ref.map(|tag| unsafe { Pin::new_unchecked(tag) });
        tag_pin.map(|tag| XiphComment::new(tag))
    }

    pub fn picture_list(&mut self) -> Vec<Picture<'a>> {
        let pictures = FLACFile_pictureList_to_vector(self.this.as_mut());
        let mut result = Vec::new();
        for picture_ref in pictures.iter() {
            let picture_ptr = picture_ref.get();
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

    pub fn mime_type(&self) -> String {
        String_to_string(Picture_mimeType(self.this.get_ref()).as_ref().unwrap())
    }

    pub fn description(&self) -> String {
        String_to_string(Picture_description(self.this.get_ref()).as_ref().unwrap())
    }

    pub fn width(&self) -> i32 {
        self.this.width()
    }

    pub fn height(&self) -> i32 {
        self.this.height()
    }

    pub fn color_depth(&self) -> i32 {
        self.this.colorDepth()
    }

    pub fn num_colors(&self) -> i32 {
        self.this.numColors()
    }

    pub fn data(&self) -> Vec<u8> {
        ByteVector_to_bytes(Picture_data(self.this.get_ref()).as_ref().unwrap()).iter().map(|b| *b).collect()
    }
}
