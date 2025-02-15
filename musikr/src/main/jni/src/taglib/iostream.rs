
use super::bridge::{self, CPPIOStream};
use std::io::{Read, Write, Seek, SeekFrom};
use std::pin::Pin;
use cxx::UniquePtr;

pub trait IOStream : Read + Write + Seek {
    fn name(&self) -> String;
    fn is_readonly(&self) -> bool;
}


pub(super) struct BridgedIOStream<'a> {
    rs_stream: Pin<Box<DynIOStream<'a>>>,
    cpp_stream: UniquePtr<CPPIOStream>
}

impl<'a> BridgedIOStream<'a> {
    pub fn new<T : IOStream + 'a>(stream: T) -> Self {
        let mut rs_stream = Box::pin(DynIOStream(Box::new(stream)));
        let cpp_stream = unsafe { bridge::wrap_RsIOStream(rs_stream.as_mut()) };
        BridgedIOStream {
            rs_stream,
            cpp_stream
        }
    }

    pub fn cpp_stream(&self) -> &UniquePtr<CPPIOStream> {
        &self.cpp_stream
    }
}

impl<'a> Drop for BridgedIOStream<'a> {
    fn drop(&mut self) {
        unsafe { 
            // CPP stream references the rust stream, so it must be dropped first
            std::ptr::drop_in_place(&mut self.cpp_stream);
            std::ptr::drop_in_place(&mut self.rs_stream);
        };
    }
}

#[repr(C)]
pub(super) struct DynIOStream<'a>(Box<dyn IOStream + 'a>);

impl<'a> DynIOStream<'a> {
    pub fn new<T: IOStream + 'a>(stream: T) -> Self {
        DynIOStream(Box::new(stream))
    }

    // Implement the exposed functions for cxx bridge
    pub fn name(&mut self) -> String {
        self.0.name()
    }

    pub fn read(&mut self, buffer: &mut [u8]) -> usize {
        self.0.read(buffer).unwrap_or(0)
    }

    pub fn write(&mut self, data: &[u8]) {
        self.0.write_all(data).unwrap();
    }

    pub fn seek(&mut self, offset: i64, whence: i32) {
        let pos = match whence {
            0 => SeekFrom::Start(offset as u64),
            1 => SeekFrom::Current(offset),
            2 => SeekFrom::End(offset),
            _ => panic!("Invalid seek whence"),
        };
        self.0.seek(pos).unwrap();
    }

    pub fn truncate(&mut self, length: i64) {
        self.0.seek(SeekFrom::Start(length as u64)).unwrap();
        // TODO: Actually implement truncate once we have a better trait bound
    }

    pub fn tell(&mut self) -> i64 {
        self.0.seek(SeekFrom::Current(0)).unwrap() as i64
    }

    pub fn length(&mut self) -> i64 {
        let current = self.0.seek(SeekFrom::Current(0)).unwrap();
        let end = self.0.seek(SeekFrom::End(0)).unwrap();
        self.0.seek(SeekFrom::Start(current)).unwrap();
        end as i64
    }

    pub fn is_readonly(&self) -> bool {
        self.0.is_readonly()
    }
}
