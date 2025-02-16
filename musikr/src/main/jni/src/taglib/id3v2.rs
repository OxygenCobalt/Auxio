use super::bridge::{
    self, CPPID3v2AttachedPictureFrame, CPPID3v2Frame, CPPID3v2FrameList, CPPID3v2Tag,
    CPPID3v2TextIdentificationFrame, CPPID3v2UserTextIdentificationFrame, CPPStringList, CPPByteVector,
};
use super::tk::{ByteVector, StringList, OwnedByteVector, OwnedStringList};
use super::this::{OwnedThis, RefThisMut, RefThis, This};

pub struct ID3v2Tag<'file_ref> {
    this: RefThisMut<'file_ref, CPPID3v2Tag>,
}   

impl<'file_ref> ID3v2Tag<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPID3v2Tag>) -> Self {
        Self { this }
    }

    pub fn frames(&self) -> Option<FrameList<'file_ref>> {
        let frames = bridge::Tag_frameList(self.this.pin());
        let this = unsafe { OwnedThis::new(frames) };
        this.map(|this| FrameList::new(this))
    }
}

pub struct FrameList<'file_ref> {
    this: OwnedThis<'file_ref, CPPID3v2FrameList>,
}

impl<'file_ref> FrameList<'file_ref> {
    pub(super) fn new(this: OwnedThis<'file_ref, CPPID3v2FrameList>) -> Self {
        Self { this }
    }

    pub fn to_vec(&self) -> Vec<Frame<'file_ref>> {
        let frames = bridge::FrameList_to_vector(self.this.pin());
        frames
            .iter()
            .map(|frame| {
                let frame_ptr = frame.get();
                let frame_ref = unsafe { frame_ptr.as_ref().unwrap() };
                let frame_this = unsafe { RefThis::new(frame_ref) };
                Frame::new(frame_this)
            })
            .collect()
    }
}

pub struct Frame<'file_ref> {
    this: RefThis<'file_ref, CPPID3v2Frame>,
}

impl<'file_ref> Frame<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CPPID3v2Frame>) -> Self {
        Self { this }
    }

    pub fn as_text_identification(&mut self) -> Option<TextIdentificationFrame<'file_ref>> {
        let frame = unsafe { bridge::Frame_asTextIdentification(self.this.ptr()) };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_this = frame_ref.map(|frame| unsafe { RefThis::new(frame) });
        frame_this.map(|this| TextIdentificationFrame::new(this))
    }

    pub fn as_user_text_identification(
        &mut self,
    ) -> Option<UserTextIdentificationFrame<'file_ref>> {
        let frame = unsafe { bridge::Frame_asUserTextIdentification(self.this.ptr()) };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_this = frame_ref.map(|frame| unsafe { RefThis::new(frame) });
        frame_this.map(|this| UserTextIdentificationFrame::new(this))
    }

    pub fn as_attached_picture(&mut self) -> Option<AttachedPictureFrame<'file_ref>> {
        let frame = unsafe { bridge::Frame_asAttachedPicture(self.this.ptr()) };
        let frame_ref = unsafe { frame.as_ref() };
        let frame_this = frame_ref.map(|frame| unsafe { RefThis::new(frame) });
        frame_this.map(|this| AttachedPictureFrame::new(this))
    }
}

pub struct TextIdentificationFrame<'file_ref> {
    this: RefThis<'file_ref, CPPID3v2TextIdentificationFrame>,
}

impl<'file_ref> TextIdentificationFrame<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CPPID3v2TextIdentificationFrame>) -> Self {
        Self { this }
    }

    pub fn field_list(&self) -> Option<OwnedStringList<'file_ref>> {
        let field_list = bridge::TextIdentificationFrame_fieldList(self.this.pin());
        let this = unsafe { OwnedThis::new(field_list) };
        this.map(|this| StringList::new(this))
    }
}

pub struct UserTextIdentificationFrame<'file_ref> {
    this: RefThis<'file_ref, CPPID3v2UserTextIdentificationFrame>,
}

impl<'file_ref> UserTextIdentificationFrame<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CPPID3v2UserTextIdentificationFrame>) -> Self {
        Self { this }
    }

    pub fn values(&self) -> Option<OwnedStringList<'file_ref>> {
        let values = bridge::UserTextIdentificationFrame_fieldList(self.this.pin());
        let this = unsafe { OwnedThis::new(values) };
        this.map(|this| StringList::new(this))
    }
}

pub struct AttachedPictureFrame<'file_ref> {
    this: RefThis<'file_ref, CPPID3v2AttachedPictureFrame>,
}

impl<'file_ref> AttachedPictureFrame<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CPPID3v2AttachedPictureFrame>) -> Self {
        Self { this }
    }

    pub fn picture(&self) -> Option<OwnedByteVector<'file_ref>> {
        let picture = bridge::AttachedPictureFrame_picture(self.this.pin());
        let this = unsafe { OwnedThis::new(picture) };
        this.map(|this| ByteVector::new(this))
    }
}
