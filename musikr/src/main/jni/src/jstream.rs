use crate::taglib::stream::IOStream;
use jni::objects::{JObject, JValue};
use std::io::{Read, Seek, SeekFrom, Write};
use crate::SharedEnv;

pub struct JInputStream<'local> {
    env: SharedEnv<'local>,
    input: JObject<'local>,
}

impl<'local, 'a> JInputStream<'local> {
    pub fn new(
        env: SharedEnv<'local>,
        input: JObject<'local>,
    ) -> Self {
        Self { env, input }
    }
}

impl<'local> IOStream for JInputStream<'local> {
    fn name(&mut self) -> String {
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

impl<'local> Read for JInputStream<'local> {
    fn read(&mut self, buf: &mut [u8]) -> std::io::Result<usize> {
        // Create a direct ByteBuffer from the Rust slice
        let byte_buffer = unsafe {
            self.env
                .borrow_mut()
                .new_direct_byte_buffer(buf.as_mut_ptr(), buf.len())
                .map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e.to_string()))?
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
            .map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e.to_string()))?;

        if !success {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to read block",
            ));
        }

        Ok(buf.len())
    }
}

impl<'local> Write for JInputStream<'local> {
    fn write(&mut self, _buf: &[u8]) -> std::io::Result<usize> {
        Err(std::io::Error::new(
            std::io::ErrorKind::PermissionDenied,
            "JInputStream is read-only",
        ))
    }

    fn flush(&mut self) -> std::io::Result<()> {
        Ok(()) // Nothing to flush in a read-only stream
    }
}

impl<'local, 'a> Seek for JInputStream<'local> {
    fn seek(&mut self, pos: SeekFrom) -> std::io::Result<u64> {
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
            .map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e.to_string()))?;

        if !success {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to seek",
            ));
        }

        // Return current position safely
        let position = self
            .env
            .borrow_mut()
            .call_method(&self.input, "tell", "()J", &[])
            .and_then(|result| result.j())
            .map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e.to_string()))?;

        if position == i64::MIN {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to get position",
            ));
        }

        Ok(position as u64)
    }
}
