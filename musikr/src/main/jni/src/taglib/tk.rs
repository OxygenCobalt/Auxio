use super::bridge::{self, CPPByteVector, CPPByteVectorList, CPPString, CPPStringList};
use super::this::{RefThis, RefThisMut, This, OwnedThis};
use cxx::{memory::UniquePtrTarget, UniquePtr};
use std::marker::PhantomData;
use std::pin::Pin;
use std::{ffi::CStr, string::ToString};

pub(super) struct String<'file_ref, T: This<'file_ref, CPPString>> {
    _data: PhantomData<&'file_ref ()>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, CPPString>> String<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self { _data: PhantomData, this }
    }
}

impl<'file_ref, T: This<'file_ref, CPPString>> ToString for String<'file_ref, T> {
    fn to_string(&self) -> std::string::String {
        let c_str = self.this.pin().toCString(true);
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

pub type OwnedString<'file_ref> = String<'file_ref, OwnedThis<'file_ref, CPPString>>;
pub type RefString<'file_ref> = String<'file_ref, RefThis<'file_ref, CPPString>>;
pub type RefStringMut<'file_ref> = String<'file_ref, RefThisMut<'file_ref, CPPString>>;
pub(super) struct StringList<'file_ref, T: This<'file_ref, CPPStringList>> {
    _data: PhantomData<&'file_ref ()>,
    this: T,
}

pub type OwnedStringList<'file_ref> = StringList<'file_ref, OwnedThis<'file_ref, CPPStringList>>;
pub type RefStringList<'file_ref> = StringList<'file_ref, RefThis<'file_ref, CPPStringList>>;
pub type RefStringListMut<'file_ref> = StringList<'file_ref, RefThisMut<'file_ref, CPPStringList>>;

impl<'file_ref, T: This<'file_ref, CPPStringList>> StringList<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self { _data: PhantomData, this }
    }

    pub fn to_vec(&self) -> Vec<std::string::String> {
        let cxx_values = bridge::StringList_to_vector(self.this.pin());
        cxx_values
            .iter()
            .map(|value| {
                let this = unsafe { RefThis::new(value) };
                String::new(this).to_string()
            })
            .collect()
    }
}

pub struct ByteVector<'file_ref, T: This<'file_ref, CPPByteVector>> {
    _data: PhantomData<&'file_ref CPPByteVector>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, CPPByteVector>> ByteVector<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self { _data: PhantomData, this }
    }

    pub fn to_vec(&self) -> Vec<u8> {
        let this = self.this.pin();
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

pub type OwnedByteVector<'file_ref> = ByteVector<'file_ref, OwnedThis<'file_ref, CPPByteVector>>;
pub type RefByteVector<'file_ref> = ByteVector<'file_ref, RefThis<'file_ref, CPPByteVector>>;
pub type RefByteVectorMut<'file_ref> = ByteVector<'file_ref, RefThisMut<'file_ref, CPPByteVector>>;

pub struct ByteVectorList<'file_ref, T: This<'file_ref, CPPByteVectorList>> {
    _data: PhantomData<&'file_ref CPPByteVectorList>,
    this: T,
}

impl<'file_ref, T: This<'file_ref, CPPByteVectorList>> ByteVectorList<'file_ref, T> {
    pub(super) fn new(this: T) -> Self {
        Self { _data: PhantomData, this }
    }

    pub fn to_vec(&self) -> Vec<Vec<u8>> {
        let cxx_values = bridge::ByteVectorList_to_vector(self.this.pin());
        cxx_values
            .iter()
            .map(|value| ByteVector::new(unsafe { RefThis::new(value) }).to_vec())
            .collect()
    }
}

pub type OwnedByteVectorList<'file_ref> = ByteVectorList<'file_ref, OwnedThis<'file_ref, CPPByteVectorList>>;
pub type RefByteVectorList<'file_ref> = ByteVectorList<'file_ref, RefThis<'file_ref, CPPByteVectorList>>;
pub type RefByteVectorListMut<'file_ref> = ByteVectorList<'file_ref, RefThisMut<'file_ref, CPPByteVectorList>>;

