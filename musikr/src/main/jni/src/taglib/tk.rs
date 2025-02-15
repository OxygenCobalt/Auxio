use super::bridge::{self, CPPByteVector, CPPString, CPPStringList};
use cxx::{memory::UniquePtrTarget, UniquePtr};
use std::marker::PhantomData;
use std::pin::Pin;
use std::{ffi::CStr, string::ToString};

enum This<'file_ref, T: UniquePtrTarget> {
    Owned {
        data: PhantomData<&'file_ref T>,
        this: UniquePtr<T>,
    },
    Ref {
        this: Pin<&'file_ref T>,
    },
}

pub struct String<'file_ref> {
    this: Pin<&'file_ref CPPString>,
}

impl<'file_ref> String<'file_ref> {
    pub(super) fn new(this: Pin<&'file_ref CPPString>) -> Self {
        Self { this }
    }
}

impl<'file_ref> ToString for String<'file_ref> {
    fn to_string(&self) -> std::string::String {
        let c_str = self.this.toCString(true);
        unsafe {
            // SAFETY:
            // - This is a C-string returned by a C++ method guaranteed to have
            //   a null terminator.
            // - This C-string is fully allocated and owned by the TagString instance,
            //   in a continous block from start to null terminator.
            // - This C-string will be non-null even if empty.
            // - This pointer will not be mutated before it's entirely copied into
            //   rust.
            // - This C-string is copied to a rust string before TagString is destroyed.
            CStr::from_ptr(c_str)
        }
        .to_string_lossy()
        .to_string()
    }
}

pub struct StringList<'file_ref> {
    this: This<'file_ref, CPPStringList>,
}

impl<'file_ref> StringList<'file_ref> {
    pub(super) fn owned(this: UniquePtr<CPPStringList>) -> Self {
        Self {
            this: This::Owned {
                data: PhantomData,
                this,
            },
        }
    }

    pub(super) fn reference(this: Pin<&'file_ref CPPStringList>) -> Self {
        Self {
            this: This::Ref { this },
        }
    }

    pub fn to_vec(&self) -> Vec<std::string::String> {
        let pin = match &self.this {
            This::Owned { this, .. } => unsafe { Pin::new_unchecked(this.as_ref().unwrap()) },
            This::Ref { this } => *this,
        };
        let cxx_values = bridge::StringList_to_vector(pin);
        cxx_values
            .iter()
            .map(|value| {
                let this = unsafe { Pin::new_unchecked(value) };
                String::new(this).to_string()
            })
            .collect()
    }
}

pub struct ByteVector<'file_ref> {
    // ByteVector is implicitly tied to the lifetime of the parent that owns it,
    // so we need to track that lifetime. Only reason why it's a UniquePtr is because
    // we can't marshal over ownership of the ByteVector by itself over cxx.
    _data: PhantomData<&'file_ref CPPByteVector>,
    this: UniquePtr<CPPByteVector>,
}

impl<'file_ref> ByteVector<'file_ref> {
    pub(super) fn new(this: UniquePtr<CPPByteVector>) -> Self {
        Self {
            _data: PhantomData,
            this,
        }
    }

    pub fn to_vec(&self) -> Vec<u8> {
        let this_ref = self.this.as_ref().unwrap();
        let this = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            Pin::new_unchecked(this_ref)
        };
        let size = this.size().try_into().unwrap();
        let data = this.data();
        // Re-cast to u8
        let data: *const u8 = data as *const u8;
        unsafe {
            // SAFETY:
            // - data points to a valid buffer of size 'size' owned by the C++ ByteVector
            // - we're creating a new Vec and copying the data, not taking ownership
            // - the source data won't be modified while we're reading from it
            std::slice::from_raw_parts(data, size).to_vec()
        }
    }
}
