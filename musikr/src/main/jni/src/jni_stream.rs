use crate::taglib::TagLibStream;
use jni::objects::{JObject, JValue};
use jni::JNIEnv;
use std::io::{Read, Seek, SeekFrom, Write};

pub struct JInputStream<'local, 'a> {
    env: &'a mut JNIEnv<'local>,
    input: JObject<'local>,
}

impl<'local, 'a> JInputStream<'local, 'a> {
    pub fn new(
        env: &'a mut JNIEnv<'local>,
        input: JObject<'local>,
    ) -> Result<Self, jni::errors::Error> {
        Ok(JInputStream {
            env,
            input,
        })
    }
}

impl<'local, 'a> TagLibStream for JInputStream<'local, 'a> {
    fn name(&mut self) -> String {
        // Call the Java name() method safely
        let name = self
            .env
            .call_method(&self.input, "name", "()Ljava/lang/String;", &[])
            .and_then(|result| result.l())
            .expect("Failed to call name() method");

        self.env
            .get_string(&name.into())
            .expect("Failed to convert Java string")
            .into()
    }

    fn is_readonly(&self) -> bool {
        true // JInputStream is always read-only
    }
}

impl<'local, 'a> Read for JInputStream<'local, 'a> {
    fn read(&mut self, buf: &mut [u8]) -> std::io::Result<usize> {
        // Create a direct ByteBuffer from the Rust slice
        let byte_buffer = unsafe {
            self.env
                .new_direct_byte_buffer(buf.as_mut_ptr(), buf.len())
                .map_err(|e| std::io::Error::new(std::io::ErrorKind::Other, e.to_string()))?
        };

        // Call readBlock safely
        let success = self
            .env
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

impl<'local, 'a> Write for JInputStream<'local, 'a> {
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

impl<'local, 'a> Seek for JInputStream<'local, 'a> {
    fn seek(&mut self, pos: SeekFrom) -> std::io::Result<u64> {
        let (method, offset) = match pos {
            SeekFrom::Start(offset) => ("seekFromBeginning", offset as i64),
            SeekFrom::Current(offset) => ("seekFromCurrent", offset),
            SeekFrom::End(offset) => ("seekFromEnd", offset),
        };

        // Call the appropriate seek method safely
        let success = self
            .env
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
