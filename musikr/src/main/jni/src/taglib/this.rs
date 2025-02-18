use std::marker::PhantomData;
use std::pin::Pin;
use cxx::{UniquePtr, memory::UniquePtrTarget};
use super::bridge::{TagLibAllocated, TagLibRef, TagLibShared};

/// A taglib-FFI-specific trait representing a C++ object returned by the library.
///
/// `This` instances must hold the following contract:
/// - This object will remain valid as long as TagLib's FileRef object is valid,
///   and will be dropped when the FileRef is dropped.
/// - This object will not move or be mutated over the FileRef's lifetime, this way
///   it can be temporarily pinned for use as a `this` pointer.
pub trait This<'file_ref, T: TagLibAllocated> : AsRef<T> {}

/// A taglib-FFI-specific trait representing a C++ object returned by the library.
///
/// This trait is used to provide a temporary pin of the object for use in C++
/// member function calls.
///
/// `ThisMut` instances must hold the following contract:
/// - This object will remain valid as long as TagLib's FileRef object is valid,
///   and will be dropped when the FileRef is dropped.
/// - This object will not move over the FileRef's lifetime, this way it can be
///   temporarily pinned for use as a `this` pointer.
pub trait ThisMut<'file_ref, T: TagLibAllocated> : This<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T>;
}

/// A [This] instance that is a reference to a C++ object.
pub struct RefThis<'file_ref, T: TagLibRef> {
    this: &'file_ref T
}

impl<'file_ref, T: TagLibRef> RefThis<'file_ref, T> {
    /// Create a new [RefThis] from a reference to a C++ object.
    /// 
    /// This is safe to call assuming the contract of [This] is upheld. Since this
    /// contract cannot be enforced by the Rust compiler, it is the caller's
    /// responsibility to ensure that the reference is valid for the lifetime of
    /// the `'file_ref` parameter. More or less, if it comes from the TagLib FFI
    /// interface, it is safe to use this.
    pub fn new(this: &'file_ref T) -> Self {
        // Rough informal contact is that the reference points to a C++ object
        // that will live and not move for as long as 'file_ref.
        Self { this }
    }

    /// Get a pointer to the C++ object.
    /// 
    /// This can be used to pass the object to FFI functions that take a pointer.
    ///     
    /// This is safe to call assuming the contract of [This] is upheld.
    pub fn ptr(&self) -> *const T {
        self.this as *const T
    }
}

impl<'file_ref, T: TagLibRef> AsRef<T> for RefThis<'file_ref, T> {
    fn as_ref(&self) -> &T {
        self.this
    }
}

impl<'file_ref, T: TagLibRef> This<'file_ref, T> for RefThis<'file_ref, T> {}

/// A [ThisMut] instance that is a reference to a C++ object.
/// 
/// This is similar to [RefThis], but allows mutating the object.
pub struct RefThisMut<'file_ref, T: TagLibRef> {
    this: &'file_ref mut T,
}

impl<'file_ref, T: TagLibRef> RefThisMut<'file_ref, T> {
    /// Create a new [RefThisMut] from a reference to a C++ object.
    /// 
    /// This is safe to call assuming the contract of [ThisMut] is upheld. Since
    /// this contract cannot be enforced by the Rust compiler, it is the caller's
    /// responsibility to ensure that the reference is valid for the lifetime of
    /// the `'file_ref` parameter. More or less, if it comes from the TagLib FFI
    /// interface, it is safe to use this.
    pub fn new(this: &'file_ref mut T) -> Self {
        Self { this }
    }

    /// Get a pointer to the C++ object.
    /// 
    /// This can be used to pass the object to FFI functions that take a pointer.
    /// 
    /// This is safe to call assuming the contract of [ThisMut] is upheld.  
    pub fn ptr(&self) -> *const T {
        self.this as *const T
    }

    /// Get a pointer to the C++ object.
    /// 
    /// This can be used to pass the object to FFI functions that take a pointer.
    /// 
    /// This is safe to call assuming the contract of [ThisMut] is upheld.  
    pub fn ptr_mut(&mut self) -> *mut T {
        self.this as *mut T
    }
}

impl<'file_ref, T: TagLibRef> AsRef<T> for RefThisMut<'file_ref, T> {
    fn as_ref(&self) -> &T {
        self.this
    }
}

impl<'file_ref, T: TagLibRef> This<'file_ref, T> for RefThisMut<'file_ref, T> {}

impl<'file_ref, T: TagLibRef> ThisMut<'file_ref, T> for RefThisMut<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T> {
        unsafe { Pin::new_unchecked(self.this) }
    }
}

/// A [This] instance that is "owned" by the caller.
/// 
/// "Owned" in this context only really means that the object is not a rust reference.
/// In practice, all "owned" taglib objects are actually shared references, and are
/// thus tied to the lifetime of the `'file_ref` parameter.
pub struct OwnedThis<'file_ref, T: TagLibShared + UniquePtrTarget> {
    _data: PhantomData<&'file_ref ()>,
    this: UniquePtr<T>,
}

impl<'file_ref, T: TagLibShared + UniquePtrTarget> OwnedThis<'file_ref, T> {
    /// Create a new [OwnedThis] from a [UniquePtr].
    /// 
    /// This is safe to call assuming the contract of [This] is upheld. Since this
    /// contract cannot be enforced by the Rust compiler, it is the caller's
    /// responsibility to ensure that the `UniquePtr` is valid for the lifetime of
    /// the `'file_ref` parameter. More or less, if it comes from the TagLib FFI
    /// interface, it is safe to use this.
    /// 
    /// This will return `None` if the `UniquePtr` is `null`.
    pub fn new(this: UniquePtr<T>) -> Option<Self> {
        if !this.is_null() {
            Some(Self {
                _data: PhantomData,
                this,
            })
        } else {
            None
        }
    }
}

impl<'file_ref, T: TagLibShared + UniquePtrTarget> AsRef<T> for OwnedThis<'file_ref, T> {
    fn as_ref(&self) -> &T {
        self.this.as_ref().unwrap()
    }
}

impl<'file_ref, T: TagLibShared + UniquePtrTarget> This<'file_ref, T> for OwnedThis<'file_ref, T> {}

impl<'file_ref, T: TagLibShared + UniquePtrTarget> ThisMut<'file_ref, T> for OwnedThis<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T> {
        self.this.as_mut().unwrap()
    }
}

