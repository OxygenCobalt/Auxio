use core::ffi::{c_void, c_char};
use alloc::ffi::CString;
use alloc::boxed::Box;
use alloc::slice;
use super::{IOStream, Whence};

// Opaque type for C++
#[repr(C)]
pub struct RustStream<'a>(Box<dyn IOStream + 'a>);

impl<'a> RustStream<'a> {
    pub fn new<T: IOStream + 'a>(stream: T) -> Self {
        RustStream(Box::new(stream))
    }
}

#[no_mangle]
pub extern "C" fn rust_stream_name(stream: *mut c_void) -> *const c_char {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let name = stream.0.name();
    // Note: This leaks memory, but TagLib only calls this once during construction
    // and keeps the pointer, so it's fine
    CString::new(name).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn rust_stream_read(
    stream: *mut c_void,
    buffer: *mut u8,
    length: usize,
) -> usize {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let buffer = unsafe { slice::from_raw_parts_mut(buffer, length) };
    stream.0.read(buffer).unwrap_or(0)
}

#[no_mangle]
pub extern "C" fn rust_stream_seek(
    stream: *mut c_void,
    offset: i64,
    whence: i32,
) {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let pos = match whence {
        0 => Whence::Start(offset),
        1 => Whence::Current(offset),
        2 => Whence::End(offset),
        _ => panic!("Invalid seek whence"),
    };
    stream.0.seek(pos).unwrap();
}

#[no_mangle]
pub extern "C" fn rust_stream_tell(stream: *mut c_void) -> i64 {
    let stream: &mut RustStream<'_> = unsafe { &mut *(stream as *mut RustStream<'_>) };
    stream.0.tell().unwrap()
}

#[no_mangle]
pub extern "C" fn rust_stream_length(stream: *mut c_void) -> i64 {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    stream.0.length().unwrap()
}
