use std::io::{Read, Seek, SeekFrom, Write};
use jni::objects::{JObject, JValue};
use jni::JNIEnv;
use crate::taglib::TagLibStream;

pub struct JInputStream<'local, 'a> {
    env: &'a mut JNIEnv<'local>,
    input: JObject<'local>,
    // Cache the method IDs
    name_method: jni::objects::JMethodID,
    read_block_method: jni::objects::JMethodID,
    is_open_method: jni::objects::JMethodID,
    seek_from_beginning_method: jni::objects::JMethodID,
    seek_from_current_method: jni::objects::JMethodID,
    seek_from_end_method: jni::objects::JMethodID,
    tell_method: jni::objects::JMethodID,
    length_method: jni::objects::JMethodID,
}

impl<'local, 'a> JInputStream<'local, 'a> {
    pub fn new(env: &'a mut JNIEnv<'local>, input: JObject<'local>) -> Result<Self, jni::errors::Error> {
        // Get the class reference
        let class = env.find_class("org/oxycblt/musikr/metadata/NativeInputStream")?;
 
        // Cache all method IDs
        Ok(JInputStream {
            name_method: env.get_method_id(&class, "name", "()Ljava/lang/String;")?,
            read_block_method: env.get_method_id(&class, "readBlock", "(Ljava/nio/ByteBuffer;)Z")?,
            is_open_method: env.get_method_id(&class, "isOpen", "()Z")?,
            seek_from_beginning_method: env.get_method_id(&class, "seekFromBeginning", "(J)Z")?,
            seek_from_current_method: env.get_method_id(&class, "seekFromCurrent", "(J)Z")?,
            seek_from_end_method: env.get_method_id(&class, "seekFromEnd", "(J)Z")?,
            tell_method: env.get_method_id(&class, "tell", "()J")?,
            length_method: env.get_method_id(&class, "length", "()J")?,
            env,
            input,
        })
    }
}

impl<'local, 'a> TagLibStream for JInputStream<'local, 'a> {
    fn name(&mut self) -> String {
        // Call the Java name() method
        let name = unsafe { 
            self.env
                .call_method_unchecked(
                    &self.input,
                    self.name_method,
                    jni::signature::ReturnType::Object,
                    &[]
                )
                .unwrap()
                .l()
                .unwrap()
        };
        
        self.env
            .get_string(&name.into())
            .unwrap()
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

        // Call readBlock
        let success = unsafe {
            self.env
                .call_method_unchecked(
                    &self.input,
                    self.read_block_method,
                    jni::signature::ReturnType::Primitive(jni::signature::Primitive::Boolean),
                    &[JValue::Object(&byte_buffer).as_jni()]
                )
                .unwrap()
                .z()
                .unwrap()
        };

        if !success {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to read block"
            ));
        }

        Ok(buf.len())
    }
}

impl<'local, 'a> Write for JInputStream<'local, 'a> {
    fn write(&mut self, _buf: &[u8]) -> std::io::Result<usize> {
        Err(std::io::Error::new(
            std::io::ErrorKind::PermissionDenied,
            "JInputStream is read-only"
        ))
    }

    fn flush(&mut self) -> std::io::Result<()> {
        Ok(()) // Nothing to flush in a read-only stream
    }
}

impl<'local, 'a> Seek for JInputStream<'local, 'a> {
    fn seek(&mut self, pos: SeekFrom) -> std::io::Result<u64> {
        let (method, offset) = match pos {
            SeekFrom::Start(offset) => (self.seek_from_beginning_method, offset as i64),
            SeekFrom::Current(offset) => (self.seek_from_current_method, offset),
            SeekFrom::End(offset) => (self.seek_from_end_method, offset),
        };

        // Call the appropriate seek method
        let success = unsafe {
            self.env
                .call_method_unchecked(
                    &self.input,
                    method,
                    jni::signature::ReturnType::Primitive(jni::signature::Primitive::Boolean),
                    &[JValue::Long(offset).as_jni()]
                )
                .unwrap()
                .z()
                .unwrap()
        };

        if !success {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to seek"
            ));
        }

        // Return current position
        let position = unsafe {
            self.env
                .call_method_unchecked(
                    &self.input,
                    self.tell_method,
                    jni::signature::ReturnType::Primitive(jni::signature::Primitive::Long),
                    &[]
                )
                .unwrap()
                .j()
                .unwrap()
        };

        if position == i64::MIN {
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                "Failed to get position"
            ));
        }

        Ok(position as u64)
    }
} 