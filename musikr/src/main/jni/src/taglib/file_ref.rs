use super::bridge::{self, CPPFileRef};
use super::file::File;
use super::iostream::{BridgedIOStream, IOStream};
use super::this::RefThisMut;
use cxx::UniquePtr;
use std::pin::Pin;

pub struct FileRef<'io> {
    stream: BridgedIOStream<'io>,
    this: UniquePtr<CPPFileRef>,
}

impl<'io> FileRef<'io> {
    pub fn new<T: IOStream + 'io>(stream: T) -> FileRef<'io> {
        let stream = BridgedIOStream::new(stream);
        let cpp_stream = stream.cpp_stream().as_mut_ptr();
        let file_ref = unsafe { bridge::new_FileRef(cpp_stream) };
        FileRef {
            stream,
            this: file_ref,
        }
    }

    pub fn file<'file_ref>(&'file_ref self) -> Option<File<'file_ref>> {
        let file_ptr = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The file data is a pointer that does not depend on the
            //   address of self.
            let this = Pin::new_unchecked(&*self.this);
            // Note: This is not the rust ptr "is_null", but a taglib isNull method
            // that checks for file validity. Without this check, we can get corrupted
            // file ptrs.
            if !this.isNull() {
                Some(this.file())
            } else {
                None
            }
        };
        let file_ref = file_ptr.and_then(|file| unsafe {
            // SAFETY:
            // - This points to a C++ FFI type ensured to be aligned by cxx's codegen.
            // - The null-safe version is being used.
            // - This points to a C++FFI type ensured to be valid by cxx's codegen.
            // - There are no datapaths that will yield any mutable pointers or references
            //   to this, ensuring that it will not be mutated as per the aliasing rules.
            file.as_mut()
        });
        let file_this = file_ref.map(|file| RefThisMut::new(file));
        file_this.map(|this| File::new(this))
    }
}
