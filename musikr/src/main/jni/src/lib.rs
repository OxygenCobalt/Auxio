use jni::objects::{JClass, JObject};
use jni::sys::jstring;
use jni::JNIEnv;

mod jni_stream;
mod taglib;

use jni_stream::JInputStream;
pub use taglib::*;

#[no_mangle]
pub extern "C" fn Java_org_oxycblt_musikr_metadata_MetadataJNI_openFile<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JObject<'local>,
) -> jstring {
    // Create JInputStream from the Java input stream
    let stream = match JInputStream::new(&mut env, input) {
        Ok(stream) => stream,
        Err(e) => {
            let error = format!("Failed to create input stream: {}", e);
            let error_str = env
                .new_string(error)
                .expect("Couldn't create error string!");
            return error_str.into_raw();
        }
    };

    // Create FileRef from the stream
    let file_ref = match FileRef::from_stream(stream) {
        Some(file_ref) => file_ref,
        None => {
            let error = "Failed to create File";
            let error_str = env
                .new_string(error)
                .expect("Couldn't create error string!");
            return error_str.into_raw();
        }
    };

    // Return the title
    let output = env.new_string("title").expect("Couldn't create string!");
    output.into_raw()
}
