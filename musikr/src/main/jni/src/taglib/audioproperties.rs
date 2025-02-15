use super::bridge::CppAudioProperties;
use std::pin::Pin;

pub struct AudioProperties<'a> {
    this: Pin<&'a CppAudioProperties>
}

impl<'a> AudioProperties<'a> {
    pub(super) fn new(this: Pin<&'a CppAudioProperties>) -> Self {
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