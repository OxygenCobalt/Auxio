use super::bridge::{self, CPPIOStream};
use cxx::UniquePtr;
use std::io::{Read, Seek, SeekFrom, Write};
use std::pin::Pin;

pub trait IOStream {
    fn read_block(&mut self, buffer: &mut [u8]) -> usize;
    fn write_block(&mut self, data: &[u8]);
    fn seek(&mut self, pos: SeekFrom);
    fn truncate(&mut self, length: i64);
    fn tell(&self) -> i64;
    fn length(&self) -> i64;
    fn name(&self) -> String;
    fn is_readonly(&self) -> bool;
}

pub(super) struct BridgedIOStream<'io_stream> {
    cpp_stream: UniquePtr<CPPIOStream<'io_stream>>,
}

impl<'io_stream> BridgedIOStream<'io_stream> {
    pub fn new<T: IOStream + 'io_stream>(stream: T) -> Self {
        let rs_stream: Box<DynIOStream<'io_stream>> = Box::new(DynIOStream(Box::new(stream)));
        let cpp_stream: UniquePtr<CPPIOStream<'io_stream>> = bridge::wrap_RsIOStream(rs_stream);
        BridgedIOStream {
            cpp_stream,
        }
    }

    pub fn cpp_stream(&self) -> &UniquePtr<CPPIOStream> {
        &self.cpp_stream
    }
}

#[repr(C)]
pub(super) struct DynIOStream<'io_stream>(Box<dyn IOStream + 'io_stream>);

impl<'io_stream> DynIOStream<'io_stream> {
    // Implement the exposed functions for cxx bridge
    pub fn name(&self) -> String {
        self.0.name()
    }

    pub fn read(&mut self, buffer: &mut [u8]) -> usize {
        self.0.read_block(buffer)
    }

    pub fn write(&mut self, data: &[u8]) {
        self.0.write_block(data);
    }

    pub fn seek(&mut self, offset: i64, whence: i32) {
        let pos = match whence {
            0 => SeekFrom::Start(offset as u64),
            1 => SeekFrom::Current(offset),
            2 => SeekFrom::End(offset),
            _ => panic!("Invalid seek whence"),
        };
        self.0.seek(pos);
    }

    pub fn truncate(&mut self, length: i64) {
        self.0.truncate(length);
    }

    pub fn tell(&self) -> i64 {
        self.0.tell()
    }

    pub fn length(&self) -> i64 {
        self.0.length()
    }

    pub fn is_readonly(&self) -> bool {
        self.0.is_readonly()
    }
}
