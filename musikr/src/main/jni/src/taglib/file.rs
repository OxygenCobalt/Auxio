use cxx::UniquePtr;
use super::bridge::{self, TFileRef};
pub use super::bridge::{BaseFile as File, AudioProperties};
use super::xiph::{OpusFile, VorbisFile, FLACFile};
use super::stream::BridgeStream;
use super::stream::IOStream;
use std::pin::Pin;
use std::marker::PhantomData;

pub struct FileRef<'a, T: IOStream + 'a> {
    data: PhantomData<&'a T>,
    ptr: UniquePtr<TFileRef>
}

impl <'a, T: IOStream + 'a> FileRef<'a, T> {
    pub fn new(stream: T) -> FileRef<'a, T> {
        let bridge_stream = BridgeStream::new(stream);
        let raw_stream = Box::into_raw(Box::new(bridge_stream)) as *mut bridge::RustStream;
        let iostream = unsafe { bridge::new_rust_iostream(raw_stream) };
        let file_ref = bridge::new_FileRef_from_stream(iostream);
        FileRef {
            data: PhantomData::<&'a T>,
            ptr: file_ref
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
            let this = Pin::new_unchecked(&*self.ptr);
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
