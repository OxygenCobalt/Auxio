use jni::objects::{JClass, JObject};
use jni::sys::jstring;
use jni::JNIEnv;
use std::cell::RefCell;
use std::rc::Rc;

mod jstream;
mod taglib;
mod tagmap;
mod jbuilder;

use jstream::JInputStream;
use taglib::file_ref::FileRef;
use jbuilder::JMetadataBuilder;

type SharedEnv<'local> = Rc<RefCell<JNIEnv<'local>>>;

#[no_mangle]
pub extern "C" fn Java_org_oxycblt_musikr_metadata_MetadataJNI_openFile<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JObject<'local>,
) -> jstring {
    // Create JInputStream from the Java input stream
    let shared_env = Rc::new(RefCell::new(env));
    let mut stream = JInputStream::new(shared_env.clone(), input);
    let file_ref = FileRef::new(stream);
    

    // Return the title
    let output = shared_env
        .borrow_mut()
        .new_string("title")
        .expect("Couldn't create string!");
    output.into_raw()
}
