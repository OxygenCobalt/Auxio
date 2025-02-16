use std::marker::PhantomData;
use std::pin::Pin;
use cxx::{UniquePtr, memory::UniquePtrTarget};

pub trait This<'file_ref, T> {
    fn pin(&self) -> Pin<&T>;
}

pub trait ThisMut<'file_ref, T> : This<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T>;
}

pub struct RefThis<'file_ref, T> {
    this: &'file_ref T
}

impl<'file_ref, T> RefThis<'file_ref, T> {
    pub unsafe fn new(this: &'file_ref T) -> Self {
        // Rough informal contact is that the reference points to a C++ object
        // that will live and not move for as long as 'file_ref.
        Self { this }
    }

    pub fn ptr(&self) -> *const T {
        self.this as *const T
    }
}

impl<'file_ref, T> This<'file_ref, T> for RefThis<'file_ref, T> {
    fn pin(&self) -> Pin<&T> {
        unsafe { Pin::new_unchecked(self.this) }
    }
}

pub struct RefThisMut<'file_ref, T> {
    this: &'file_ref mut T,
}

impl<'file_ref, T> RefThisMut<'file_ref, T> {
    pub unsafe fn new(this: &'file_ref mut T) -> Self {
        Self { this }
    }

    pub fn ptr(&self) -> *const T {
        self.this as *const T
    }

    pub fn ptr_mut(&mut self) -> *mut T {
        self.this as *mut T
    }
}

impl<'file_ref, T> This<'file_ref, T> for RefThisMut<'file_ref, T> {
    fn pin(&self) -> Pin<&T> {
        unsafe { Pin::new_unchecked(self.this) }
    }
}

impl<'file_ref, T> ThisMut<'file_ref, T> for RefThisMut<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T> {
        unsafe { Pin::new_unchecked(self.this) }
    }
}

pub struct OwnedThis<'file_ref, T : UniquePtrTarget> {
    _data: PhantomData<&'file_ref ()>,
    this: UniquePtr<T>,
}

impl<'file_ref, T : UniquePtrTarget> OwnedThis<'file_ref, T> {
    pub unsafe fn new(this: UniquePtr<T>) -> Option<Self> {
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

impl<'file_ref, T : UniquePtrTarget> This<'file_ref, T> for OwnedThis<'file_ref, T> {
    fn pin(&self) -> Pin<&T> {
        unsafe { Pin::new_unchecked(self.this.as_ref().unwrap()) }
    }
}

impl<'file_ref, T : UniquePtrTarget> ThisMut<'file_ref, T> for OwnedThis<'file_ref, T> {
    fn pin_mut(&mut self) -> Pin<&mut T> {
        self.this.as_mut().unwrap()
    }
}

