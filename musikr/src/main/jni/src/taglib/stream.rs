use std::ffi::{c_void, CString};
use std::io::{Read, Seek, SeekFrom, Write};
use std::os::raw::c_char;

// Trait that must be implemented by Rust streams to be used with TagLib
pub trait TagLibStream: Read + Write + Seek {
    fn name(&mut self) -> String;
    fn is_readonly(&self) -> bool;
}

// Opaque type for C++
#[repr(C)]
pub struct RustStream<'a>(Box<dyn TagLibStream + 'a>);

impl<'a> RustStream<'a> {
    pub fn new<T: TagLibStream + 'a>(stream: T) -> Self {
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
    let buffer = unsafe { std::slice::from_raw_parts_mut(buffer, length) };
    stream.0.read(buffer).unwrap_or(0)
}

#[no_mangle]
pub extern "C" fn rust_stream_write(
    stream: *mut c_void,
    data: *const u8,
    length: usize,
) {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let data = unsafe { std::slice::from_raw_parts(data, length) };
    stream.0.write_all(data).unwrap();
}

#[no_mangle]
pub extern "C" fn rust_stream_seek(
    stream: *mut c_void,
    offset: i64,
    whence: i32,
) {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let pos = match whence {
        0 => SeekFrom::Start(offset as u64),
        1 => SeekFrom::Current(offset),
        2 => SeekFrom::End(offset),
        _ => panic!("Invalid seek whence"),
    };
    stream.0.seek(pos).unwrap();
}

#[no_mangle]
pub extern "C" fn rust_stream_truncate(stream: *mut c_void, length: i64) {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    stream.0.seek(SeekFrom::Start(length as u64)).unwrap();
    // TODO: Actually implement truncate once we have a better trait bound
}

#[no_mangle]
pub extern "C" fn rust_stream_tell(stream: *mut c_void) -> i64 {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    stream.0.seek(SeekFrom::Current(0)).unwrap() as i64
}

#[no_mangle]
pub extern "C" fn rust_stream_length(stream: *mut c_void) -> i64 {
    let stream = unsafe { &mut *(stream as *mut RustStream<'_>) };
    let current = stream.0.seek(SeekFrom::Current(0)).unwrap();
    let end = stream.0.seek(SeekFrom::End(0)).unwrap();
    stream.0.seek(SeekFrom::Start(current)).unwrap();
    end as i64
}

#[no_mangle]
pub extern "C" fn rust_stream_is_readonly(stream: *const c_void) -> bool {
    let stream = unsafe { &*(stream as *const RustStream<'_>) };
    stream.0.is_readonly()
} 