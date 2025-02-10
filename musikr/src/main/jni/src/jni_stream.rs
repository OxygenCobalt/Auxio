use crate::taglib::{IOStream, Whence};
use jni::objects::{JObject, JValue};
use jni::JNIEnv;
use alloc::string::String;

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

impl<'local, 'a> IOStream for JInputStream<'local, 'a> {
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

    fn read(&mut self, buf: &mut [u8]) -> Result<usize, String> {
        // Create a direct ByteBuffer from the Rust slice
        let byte_buffer = unsafe {
            self.env
                .new_direct_byte_buffer(buf.as_mut_ptr(), buf.len())
                .map_err(|_| String::from("Failed to create byte buffer"))?
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
            .map_err(|_| String::from("Failed to read block"))?;

        if !success {
            return Err(String::from("Failed to read block"));
        }

        Ok(buf.len())
    }

    fn seek(&mut self, pos: Whence) -> Result<(), String> {
        let (method, offset) = match pos {
            Whence::Start(offset) => ("seekFromBeginning", offset as i64),
            Whence::Current(offset) => ("seekFromCurrent", offset),
            Whence::End(offset) => ("seekFromEnd", offset),
        };

        // Call the appropriate seek method safely
        let success = self
            .env
            .call_method(&self.input, method, "(J)Z", &[JValue::Long(offset)])
            .and_then(|result| result.z())
            .map_err(|e| String::from("Failed to seek"))?;

        if !success {
            return Err(String::from("Failed to seek"));
        }

        Ok(())
    }

    fn tell(&mut self) -> Result<i64, String> {
         self
            .env
            .call_method(&self.input, "tell", "()J", &[])
            .and_then(|result| result.j())
            .map_err(|_| String::from("Failed to get position"))
    }

    fn length(&mut self) -> Result<i64, String> {
        self
            .env
            .call_method(&self.input, "length", "()J", &[])
            .and_then(|result| result.j())
            .map_err(|_| String::from("Failed to get length"))
    }
}
