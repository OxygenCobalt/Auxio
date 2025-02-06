use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[cxx::bridge]
mod ffi {
    unsafe extern "C++" {
        include!("taglib/taglib.h");

        type FileRef;

        type File;
    }
}

#[no_mangle]
pub extern "C" fn Java_org_oxycblt_musikr_metadata_MetadataJNI_rust(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    // Convert the Java string (JString) to a Rust string
    let input: String = env.get_string(&input).expect("Couldn't get java string!").into();

    // Process the input string (this is where your Rust logic would go)
    let output = format!("Hello from Rust, {}", input);

    // Convert the Rust string back to a Java string (jstring)
    let output = env.new_string(output).expect("Couldn't create java string!");

    // Return the Java string
    output.into_raw()
}
