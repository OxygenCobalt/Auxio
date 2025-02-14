use std::ffi::{c_void, CString};
use std::io::{Read, Seek, SeekFrom, Write};
use std::os::raw::c_char;
use cxx::CxxString;

pub trait IOStream: Read + Write + Seek {
    fn name(&mut self) -> String;
    fn is_readonly(&self) -> bool;
}

#[repr(C)]
pub(super) struct BridgeStream<'a>(Box<dyn IOStream + 'a>);

impl<'a> BridgeStream<'a> {
    pub fn new<T: IOStream + 'a>(stream: T) -> Self {
        BridgeStream(Box::new(stream))
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
