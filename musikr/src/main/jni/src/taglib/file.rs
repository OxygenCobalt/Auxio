use super::audioproperties::AudioProperties;
use super::bridge::{self, CPPFile};
use super::flac::FLACFile;
use super::mp4::MP4File;
use super::mpeg::MPEGFile;
use super::ogg::OpusFile;
use super::ogg::VorbisFile;
use super::riff::WAVFile;
use super::this::{RefThis, RefThisMut};

pub struct File<'file_ref> {
    this: RefThisMut<'file_ref, CPPFile>,
}

impl<'file_ref> File<'file_ref> {
    pub(super) fn new(this: RefThisMut<'file_ref, CPPFile>) -> Self {
        Self { this }
    }

    pub fn audio_properties(&self) -> Option<AudioProperties<'file_ref>> {
        let props_ptr = self.this.as_ref().audioProperties();
        let props_ref = unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            props_ptr.as_ref()
        };
        let props_this = props_ref.map(|props| RefThis::new(props));
        props_this.map(|this| AudioProperties::new(this))
    }

    pub fn as_opus(&mut self) -> Option<OpusFile<'file_ref>> {
        let opus_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asOpus(self.this.ptr_mut())
        };
        let opus_ref = unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            opus_file.as_mut()
        };
        let opus_this = opus_ref.map(|opus| RefThisMut::new(opus));
        opus_this.map(|this| OpusFile::new(this))
    }

    pub fn as_vorbis(&mut self) -> Option<VorbisFile<'file_ref>> {
        let vorbis_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asVorbis(self.this.ptr_mut())
        };
        let vorbis_ref = unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            vorbis_file.as_mut()
        };
        let vorbis_this = vorbis_ref.map(|vorbis| RefThisMut::new(vorbis));
        vorbis_this.map(|this| VorbisFile::new(this))
    }

    pub fn as_flac(&mut self) -> Option<FLACFile<'file_ref>> {
        let flac_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asFLAC(self.this.ptr_mut())
        };
        let flac_ref = unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            flac_file.as_mut()
        };
        let flac_this = flac_ref.map(|flac| RefThisMut::new(flac));
        flac_this.map(|this| FLACFile::new(this))
    }

    pub fn as_mpeg(&mut self) -> Option<MPEGFile<'file_ref>> {
        let mpeg_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an MPEG file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asMPEG(self.this.ptr_mut())
        };
        let mpeg_ref = unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            mpeg_file.as_mut()
        };
        let mpeg_this = mpeg_ref.map(|mpeg| RefThisMut::new(mpeg));
        mpeg_this.map(|this| MPEGFile::new(this))
    }

    pub fn as_mp4(&mut self) -> Option<MP4File<'file_ref>> {
        let mp4_file = unsafe { bridge::File_asMP4(self.this.ptr_mut()) };
        let mp4_ref = unsafe { mp4_file.as_mut() };
        let mp4_this = mp4_ref.map(|mp4| RefThisMut::new(mp4));
        mp4_this.map(|this| MP4File::new(this))
    }

    pub fn as_wav(&mut self) -> Option<WAVFile<'file_ref>> {
        let wav_file = unsafe { bridge::File_asWAV(self.this.ptr_mut()) };
        let wav_ref = unsafe { wav_file.as_mut() };
        let wav_this = wav_ref.map(|wav| RefThisMut::new(wav));
        wav_this.map(|this| WAVFile::new(this))
    }
}
