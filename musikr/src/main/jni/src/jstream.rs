use crate::taglib::iostream::IOStream;
use crate::SharedEnv;
use jni::objects::{JObject, JValue};
use std::io::{Read, Seek, SeekFrom, Write};

pub struct JInputStream<'local> {
    env: SharedEnv<'local>,
    input: JObject<'local>,
}

impl<'local, 'a> JInputStream<'local> {
    pub fn new(env: SharedEnv<'local>, input: JObject<'local>) -> Self {
        Self { env, input }
    }
}

impl<'local> IOStream for JInputStream<'local> {
    fn read_block(&mut self, buf: &mut [u8]) -> usize {
        // Create a direct ByteBuffer from the Rust slice
        let byte_buffer = unsafe {
            self.env
                .borrow_mut()
                .new_direct_byte_buffer(buf.as_mut_ptr(), buf.len())
                .expect("Failed to create ByteBuffer")
        };

        // Call readBlock safely
        let success = self
            .env
            .borrow_mut()
            .call_method(
                &self.input,
                "readBlock",
                "(Ljava/nio/ByteBuffer;)Z",
                &[JValue::Object(&byte_buffer)],
            )
            .and_then(|result| result.z())
            .expect("Failed to call readBlock");

        if !success {
            return 0;
        }

        buf.len()
    }

    fn write_block(&mut self, _data: &[u8]) {
        panic!("JInputStream is read-only");
    }

    fn seek(&mut self, pos: SeekFrom) {
        let (method, offset) = match pos {
            SeekFrom::Start(offset) => ("seekFromBeginning", offset as i64),
            SeekFrom::Current(offset) => ("seekFromCurrent", offset),
            SeekFrom::End(offset) => ("seekFromEnd", offset),
        };

        // Call the appropriate seek method safely
        let success = self
            .env
            .borrow_mut()
            .call_method(&self.input, method, "(J)Z", &[JValue::Long(offset)])
            .and_then(|result| result.z())
            .expect("Failed to seek");

        if !success {
            panic!("Failed to seek");
        }
    }

    fn truncate(&mut self, _length: i64) {
        panic!("JInputStream is read-only");
    }

    fn tell(&self) -> i64 {
        let position = self
            .env
            .borrow_mut()
            .call_method(&self.input, "tell", "()J", &[])
            .and_then(|result| result.j())
            .expect("Failed to get position");

        if position == i64::MIN {
            panic!("Failed to get position");
        }

        position
    }

    fn length(&self) -> i64 {
        self.env
            .borrow_mut()
            .call_method(&self.input, "length", "()J", &[])
            .and_then(|result| result.j())
            .expect("Failed to get length")
    }

    fn name(&self) -> String {
        // Call the Java name() method safely
        let name = self
            .env
            .borrow_mut()
            .call_method(&self.input, "name", "()Ljava/lang/String;", &[])
            .and_then(|result| result.l())
            .expect("Failed to call name() method");

        self.env
            .borrow_mut()
            .get_string(&name.into())
            .expect("Failed to convert Java string")
            .into()
    }

    fn is_readonly(&self) -> bool {
        true // JInputStream is always read-only
    }
}
