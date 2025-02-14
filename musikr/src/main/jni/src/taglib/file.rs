use cxx::UniquePtr;
use super::bridge::{self, TFileRef, TIOStream};
pub use super::bridge::{BaseFile as File, AudioProperties};
use super::xiph::{OpusFile, VorbisFile, FLACFile};
use std::io::{Read, Write, Seek};
use std::pin::Pin;

pub struct FileRef<'a> {
    stream: Pin<Box<TIOStream<'a>>>,
    file_ref: UniquePtr<TFileRef>
}

impl <'a> FileRef<'a> {
    pub fn new<T : IOStream + 'a>(stream: T) -> FileRef<'a> {
        let mut bridge_stream  = TIOStream::new(stream);
        let iostream = unsafe { bridge::new_RustIOStream(bridge_stream.as_mut()) };
        let file_ref = bridge::new_FileRef_from_stream(iostream);
        FileRef {
            stream: bridge_stream,
            file_ref
        }
    }

    pub fn file(&self) -> Option<&mut File> {
        let file = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The file data is a pointer that does not depend on the
            //   address of self.
            let this = Pin::new_unchecked(&*self.file_ref);
            // Note: This is not the rust ptr "is_null", but a taglib isNull method
            // that checks for file validity. Without this check, we can get corrupted
            // file ptrs.
            if !this.thisIsNull() {
                Some(this.thisFile())
            } else {
                None
            }
        };
        file.and_then(|file| unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            file.as_mut()
        })
    }
}

impl File {
    pub fn audio_properties(&self) -> Option<&AudioProperties> {
        let props = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The audio properties data is a pointer that does not depend on the
            //   address of self.
            let this: Pin<&File> = Pin::new_unchecked(self);
            this.thisAudioProperties()
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            props.as_ref()
        }
    }

    pub fn as_opus(&mut self) -> Option<&mut OpusFile> {
        let ptr_self = self as *mut Self;
        let opus_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asOpus(ptr_self)
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            opus_file.as_mut()
        }
    }

    pub fn as_vorbis(&mut self) -> Option<&VorbisFile> {
        let ptr_self = self as *mut Self;
        let vorbis_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asVorbis(ptr_self)
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            vorbis_file.as_ref()
        }
    }

    pub fn as_flac(&mut self) -> Option<&mut FLACFile> {
        let ptr_self = self as *mut Self;
        let flac_file = unsafe {
            // SAFETY:
            // This FFI function will be a simple C++ dynamic_cast, which checks if
            // the file can be cased down to an opus file. If the cast fails, a null
            // pointer is returned, which will be handled by as_ref's null checking.
            bridge::File_asFLAC(ptr_self)
        };
        unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            flac_file.as_mut()
        }
    }
}

impl AudioProperties {
    pub fn length_in_milliseconds(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisLengthInMilliseconds()
        }
    }

    pub fn bitrate(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisBitrate()
        }
    }

    pub fn sample_rate(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisSampleRate()
        }
    }

    pub fn channels(&self) -> i32 {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is copied and thus not dependent on the address of self.
            let this = Pin::new_unchecked(self);
            this.thisChannels()
        }
    }
}

impl <'a> Drop for FileRef<'a> {
    fn drop(&mut self) {
        // First drop the file, since it has a pointer to the stream.
        // Then drop the stream
        unsafe {
            std::ptr::drop_in_place(&mut self.file_ref);
            std::ptr::drop_in_place(&mut self.stream);
        }
    }
}

pub trait IOStream: Read + Write + Seek {
    fn name(&mut self) -> String;
    fn is_readonly(&self) -> bool;
}
