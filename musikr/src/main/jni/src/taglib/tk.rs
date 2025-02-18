use super::bridge;
use super::this::{OwnedThis, RefThis, RefThisMut, This};
use std::marker::PhantomData;
use std::{ffi::CStr, string::ToString};

pub use bridge::CPPByteVector as InnerByteVector;
pub use bridge::CPPByteVectorList as InnerByteVectorList;
pub use bridge::CPPString as InnerString;
pub use bridge::CPPStringList as InnerStringList;

pub struct String<'file_ref, T: This<'file_ref, InnerString>> {
    _data: PhantomData<&'file_ref ()>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, InnerString>> String<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self {
            _data: PhantomData,
            this,
        }
    }
}

impl<'file_ref, T: This<'file_ref, InnerString>> ToString for String<'file_ref, T> {
    fn to_string(&self) -> std::string::String {
        let c_str = self.this.as_ref().toCString(true);
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

pub type OwnedString<'file_ref> = String<'file_ref, OwnedThis<'file_ref, InnerString>>;
pub type RefString<'file_ref> = String<'file_ref, RefThis<'file_ref, InnerString>>;
pub type RefStringMut<'file_ref> = String<'file_ref, RefThisMut<'file_ref, InnerString>>;
pub struct StringList<'file_ref, T: This<'file_ref, InnerStringList>> {
    _data: PhantomData<&'file_ref ()>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, InnerStringList>> StringList<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self {
            _data: PhantomData,
            this,
        }
    }

    pub fn to_vec(&self) -> Vec<std::string::String> {
        let cxx_values = bridge::StringList_to_vector(self.this.as_ref());
        cxx_values
            .iter()
            .map(|value| {
                let this = RefThis::new(value);
                String::new(this).to_string()
            })
            .collect()
    }
}

pub type OwnedStringList<'file_ref> = StringList<'file_ref, OwnedThis<'file_ref, InnerStringList>>;
pub type RefStringList<'file_ref> = StringList<'file_ref, RefThis<'file_ref, InnerStringList>>;
pub type RefStringListMut<'file_ref> =
    StringList<'file_ref, RefThisMut<'file_ref, InnerStringList>>;

pub struct ByteVector<'file_ref, T: This<'file_ref, InnerByteVector>> {
    _data: PhantomData<&'file_ref InnerByteVector>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, InnerByteVector>> ByteVector<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self {
            _data: PhantomData,
            this,
        }
    }

    pub fn to_vec(&self) -> Vec<u8> {
        let this = self.this.as_ref();
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

    pub fn to_string_lossy(&self) -> std::string::String {
        let this = self.this.as_ref();
        let size = this.size().try_into().unwrap();
        let data = this.data();
        let data: *const u8 = data as *const u8;
        let slice = unsafe { std::slice::from_raw_parts(data, size) };
        std::string::String::from_utf8_lossy(slice).to_string()
    }
}

pub type OwnedByteVector<'file_ref> = ByteVector<'file_ref, OwnedThis<'file_ref, InnerByteVector>>;
pub type RefByteVector<'file_ref> = ByteVector<'file_ref, RefThis<'file_ref, InnerByteVector>>;
pub type RefByteVectorMut<'file_ref> =
    ByteVector<'file_ref, RefThisMut<'file_ref, InnerByteVector>>;

pub struct ByteVectorList<'file_ref, T: This<'file_ref, InnerByteVectorList>> {
    _data: PhantomData<&'file_ref InnerByteVectorList>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, InnerByteVectorList>> ByteVectorList<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self {
            _data: PhantomData,
            this,
        }
    }

    pub fn to_vec(&self) -> Vec<Vec<u8>> {
        let cxx_values = bridge::ByteVectorList_to_vector(self.this.as_ref());
        cxx_values
            .iter()
            .map(|value| ByteVector::new(unsafe { RefThis::new(value) }).to_vec())
            .collect()
    }
}

pub type OwnedByteVectorList<'file_ref> =
    ByteVectorList<'file_ref, OwnedThis<'file_ref, InnerByteVectorList>>;
pub type RefByteVectorList<'file_ref> =
    ByteVectorList<'file_ref, RefThis<'file_ref, InnerByteVectorList>>;
pub type RefByteVectorListMut<'file_ref> =
    ByteVectorList<'file_ref, RefThisMut<'file_ref, InnerByteVectorList>>;
