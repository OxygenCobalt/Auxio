use super::bridge::{
    self, CPPID3v2Frame, CPPID3v2TextIdentificationFrame,
    CPPID3v2UserTextIdentificationFrame, CPPID3v2AttachedPictureFrame, CPPID3v2Tag
};
use super::tk::{ByteVector, StringList};
use std::pin::Pin;

pub struct ID3v2Tag<'file_ref> {
    this: Pin<&'file_ref CPPID3v2Tag>
}

impl<'file_ref> ID3v2Tag<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPID3v2Tag>) -> Self {
        Self { this }
    }

    pub fn frames(&self) -> Vec<Frame<'file_ref>> {
        let frames = bridge::Tag_frameList(self.this.as_ref());
        frames.iter().map(|frame| {
            let frame_ptr = frame.get();
            let frame_ref = unsafe { frame_ptr.as_ref().unwrap() };
            let frame_pin = unsafe { Pin::new_unchecked(frame_ref) };
            Frame::new(frame_pin)
        }).collect()
    }
}

pub struct Frame<'file_ref> {
    this: Pin<&'file_ref CPPID3v2Frame>
}

impl<'file_ref> Frame<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPID3v2Frame>) -> Self {
        Self { this }
    }

    pub fn as_text_identification(&mut self) -> Option<TextIdentificationFrame<'file_ref>> {
        let frame = unsafe {
            bridge::Frame_asTextIdentification(self.this.as_ref().get_ref())
        };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_pin = frame_ref.map(|frame| unsafe { Pin::new_unchecked(frame) });
        frame_pin.map(|frame| TextIdentificationFrame::new(frame))
    }

    pub fn as_user_text_identification(&mut self) -> Option<UserTextIdentificationFrame<'file_ref>> {
        let frame = unsafe {
            bridge::Frame_asUserTextIdentification(self.this.as_ref().get_ref())
        };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_pin = frame_ref.map(|frame| unsafe { Pin::new_unchecked(frame) });
        frame_pin.map(|frame| UserTextIdentificationFrame::new(frame))
    }

    pub fn as_attached_picture(&mut self) -> Option<AttachedPictureFrame<'file_ref>> {
        let frame = unsafe {
            bridge::Frame_asAttachedPicture(self.this.as_ref().get_ref())
        };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_pin = frame_ref.map(|frame| unsafe { Pin::new_unchecked(frame) });
        frame_pin.map(|frame| AttachedPictureFrame::new(frame))
    }
}

pub struct TextIdentificationFrame<'file_ref> {
    this: Pin<&'file_ref CPPID3v2TextIdentificationFrame>
}

impl<'file_ref> TextIdentificationFrame<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPID3v2TextIdentificationFrame>) -> Self {
        Self { this }
    }

    pub fn field_list<'slf>(&'slf self) -> StringList<'file_ref> {
        let field_list = bridge::TextIdentificationFrame_fieldList(self.this);
        StringList::owned(field_list)
    }
}

pub struct UserTextIdentificationFrame<'file_ref> {
    this: Pin<&'file_ref CPPID3v2UserTextIdentificationFrame>
}

impl<'file_ref> UserTextIdentificationFrame<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPID3v2UserTextIdentificationFrame>) -> Self {
        Self { this }
    }

    pub fn values<'slf>(&'slf self) -> StringList<'file_ref> {
        let values = bridge::UserTextIdentificationFrame_fieldList(self.this);
        StringList::owned(values)
    }
}

pub struct AttachedPictureFrame<'file_ref> {
    this: Pin<&'file_ref CPPID3v2AttachedPictureFrame>
}

impl<'file_ref> AttachedPictureFrame<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPID3v2AttachedPictureFrame>) -> Self {
        Self { this }
    }

    pub fn picture(&self) -> ByteVector<'file_ref> {
        let picture = bridge::AttachedPictureFrame_picture(self.this.as_ref());
        ByteVector::new(picture)
    }
} 