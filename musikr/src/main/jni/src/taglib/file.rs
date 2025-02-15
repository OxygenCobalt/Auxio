use super::audioproperties::AudioProperties;
use super::bridge::{self, CPPFile, CPPMPEGFile};
use super::flac::FLACFile;
use super::id3v2::ID3v2Tag;
use super::mpeg::MPEGFile;
use super::ogg::OpusFile;
use super::ogg::VorbisFile;
use std::pin::Pin;

pub struct File<'file_ref> {
    this: Pin<&'file_ref mut CPPFile>,
}

impl<'file_ref> File<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref mut CPPFile>) -> Self {
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
        let props_pin = props_ref.map(|props| unsafe { Pin::new_unchecked(props) });
        props_pin.map(|props| AudioProperties::new(props))
    }

    pub fn as_opus(&mut self) -> Option<OpusFile<'file_ref>> {
        let opus_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asOpus(self.this.as_mut().get_unchecked_mut() as *mut CPPFile)
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
        let opus_pin = opus_ref.map(|opus| unsafe { Pin::new_unchecked(opus) });
        opus_pin.map(|opus| OpusFile::new(opus))
    }

    pub fn as_vorbis(&mut self) -> Option<VorbisFile<'file_ref>> {
        let vorbis_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asVorbis(self.this.as_mut().get_unchecked_mut() as *mut CPPFile)
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
        let vorbis_pin = vorbis_ref.map(|vorbis| unsafe { Pin::new_unchecked(vorbis) });
        vorbis_pin.map(|vorbis| VorbisFile::new(vorbis))
    }

    pub fn as_flac(&mut self) -> Option<FLACFile<'file_ref>> {
        let flac_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asFLAC(self.this.as_mut().get_unchecked_mut() as *mut CPPFile)
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
        let flac_pin = flac_ref.map(|flac| unsafe { Pin::new_unchecked(flac) });
        flac_pin.map(|flac| FLACFile::new(flac))
    }

    pub fn as_mpeg(&mut self) -> Option<MPEGFile<'file_ref>> {
        let mpeg_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an MPEG file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asMPEG(self.this.as_mut().get_unchecked_mut() as *mut CPPFile)
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
        let mpeg_pin = mpeg_ref.map(|mpeg| unsafe { Pin::new_unchecked(mpeg) });
        mpeg_pin.map(|mpeg| MPEGFile::new(mpeg))
    }
}
