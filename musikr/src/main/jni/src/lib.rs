use jni::objects::{JClass, JObject};
use jni::sys::jstring;
use jni::JNIEnv;
use std::cell::RefCell;
use std::rc::Rc;

mod jstream;
mod taglib;

use jstream::JInputStream;
use taglib::file_ref::FileRef;

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
    let title = file_ref.file().and_then(|mut file| {
        let audio_properties = file.audio_properties();

        if let Some(vorbis_file) = file.as_vorbis() {
            vorbis_file
                .xiph_comments()
                .map(|comments| comments.field_list_map().to_hashmap())
                .and_then(|comments| comments.get("TITLE").cloned())
                .and_then(|title| title.first().cloned())
                .map(|s| s.to_string())
        } else if let Some(opus_file) = file.as_opus() {
            opus_file
                .xiph_comments()
                .map(|comments| comments.field_list_map().to_hashmap())
                .and_then(|comments| comments.get("TITLE").cloned())
                .and_then(|title| title.first().cloned())
                .map(|s| s.to_string())
        } else if let Some(mut flac_file) = file.as_flac() {
            flac_file
                .xiph_comments()
                .map(|comments| comments.field_list_map().to_hashmap())
                .and_then(|comments| comments.get("TITLE").cloned())
                .and_then(|title| title.first().cloned())
                .map(|s| s.to_string())
        } else {
            None
        }
    });

    // Return the title
    let output = shared_env
        .borrow_mut()
        .new_string("title")
        .expect("Couldn't create string!");
    output.into_raw()
}
