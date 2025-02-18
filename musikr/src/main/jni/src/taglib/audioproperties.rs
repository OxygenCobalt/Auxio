use super::bridge::CppAudioProperties;
use super::this::RefThis;

pub struct AudioProperties<'file_ref> {
    this: RefThis<'file_ref, CppAudioProperties>,
}

impl<'file_ref> AudioProperties<'file_ref> {
    pub(super) fn new(this: RefThis<'file_ref, CppAudioProperties>) -> Self {
        Self { this }
    }

    pub fn length_in_milliseconds(&self) -> i32 {
        self.this.as_ref().lengthInMilliseconds()
    }

    pub fn bitrate(&self) -> i32 {
        self.this.as_ref().bitrate()
    }

    pub fn sample_rate(&self) -> i32 {
        self.this.as_ref().sampleRate()
    }

    pub fn channels(&self) -> i32 {
        self.this.as_ref().channels()
    }
}
