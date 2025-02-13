use std::{ffi::CStr, string::ToString};
use std::pin::Pin;
use std::collections::HashMap;

use super::bridge::{self, Property};
pub use super::bridge::{TString, TStringList, SimplePropertyMap}; 

impl SimplePropertyMap {
    pub fn to_hashmap(&self) -> HashMap<String, Vec<String>> {
        let cxx_vec = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value is a unique_ptr to a copied vector that is not dependent
            //   on the address of self.
            let this = Pin::new_unchecked(self);
            bridge::SimplePropertyMap_to_vector(this)
        };
        cxx_vec.iter().map(|property| property.to_tuple()).collect()
    }
}

impl Property {
    pub fn to_tuple(&self) -> (String, Vec<String>) {
        unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The values returned are copied and thus not dependent on the address
            //   of self.
            let this = Pin::new_unchecked(self);
            let key = this.thisKey().to_string();
            let value = this.thisValue().to_vec();
            (key, value)
        }
    }
}

impl ToString for TString {
    fn to_string(&self) -> String {
        let c_str = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value returned are pointers and thus not dependent on the address
            //   of self.
            let this: Pin<&TString> = Pin::new_unchecked(self);
            this.thisToCString(true)
        };
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

impl TStringList {
    pub fn to_vec(&self) -> Vec<String> {
        let cxx_values = unsafe {
            // SAFETY:
            // - This pin is only used in this unsafe scope.
            // - The pin is used as a C++ this pointer in the ffi call, which does
            //   not change address by C++ semantics.
            // - The value returned is a unique ptr to a copied vector that is not
            //   dependent on the address of self.
            let this = Pin::new_unchecked(self);
            bridge::StringList_to_vector(this)
        };
        cxx_values.iter().map(|value| value.to_string()).collect()
    }
}
