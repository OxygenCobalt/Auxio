use super::bridge::{TagLibAllocated, TagLibRef, TagLibShared};
use cxx::{memory::UniquePtrTarget, UniquePtr};
use std::marker::PhantomData;
use std::pin::Pin;

pub trait This<'file_ref, T: TagLibAllocated>: AsRef<T> {}

pub trait ThisMut<'file_ref, T: TagLibAllocated>: This<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T>;
}

pub struct RefThis<'file_ref, T: TagLibRef> {
    this: &'file_ref T,
}

impl<'file_ref, T: TagLibRef> RefThis<'file_ref, T> {
    pub fn new(this: &'file_ref T) -> Self {
        Self { this }
    }

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

pub struct RefThisMut<'file_ref, T: TagLibRef> {
    this: &'file_ref mut T,
}

impl<'file_ref, T: TagLibRef> RefThisMut<'file_ref, T> {
    pub fn new(this: &'file_ref mut T) -> Self {
        Self { this }
    }

    pub fn ptr(&self) -> *const T {
        self.this as *const T
    }

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

pub struct OwnedThis<'file_ref, T: TagLibShared + UniquePtrTarget> {
    _data: PhantomData<&'file_ref ()>,
    this: UniquePtr<T>,
}

impl<'file_ref, T: TagLibShared + UniquePtrTarget> OwnedThis<'file_ref, T> {
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

impl<'file_ref, T: TagLibShared + UniquePtrTarget> ThisMut<'file_ref, T>
    for OwnedThis<'file_ref, T>
{
    fn pin_mut(&mut self) -> Pin<&mut T> {
        self.this.as_mut().unwrap()
    }
}
