use jni::objects::{JClass, JObject};
use jni::sys::jobject;
use jni::JNIEnv;
use std::cell::RefCell;
use std::rc::Rc;

mod jbuilder;
mod jstream;
mod taglib;
mod tagmap;

use jbuilder::JMetadataBuilder;
use jstream::JInputStream;
use taglib::file_ref::FileRef;

type SharedEnv<'local> = Rc<RefCell<JNIEnv<'local>>>;

#[no_mangle]
pub extern "C" fn Java_org_oxycblt_musikr_metadata_MetadataJNI_openFile<'local>(
    env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JObject<'local>,
) -> jobject {
    // Create JInputStream from the Java input stream
    let shared_env = Rc::new(RefCell::new(env));
    let stream = JInputStream::new(shared_env.clone(), input);
    let file_ref = FileRef::new(stream);
    let file = file_ref.file();
    let mut jbuilder = JMetadataBuilder::new(shared_env.clone());
    match file {
        Some(mut file) => {
            if let Some(properties) = file.audio_properties() {
                jbuilder.set_properties(properties);
            }
            if let Some(vorbis) = file.as_vorbis() {
                jbuilder.set_mime_type("audio/ogg");
                if let Some(tag) = vorbis.xiph_comments() {
                    jbuilder.set_xiph(&tag);
                }
            }
            if let Some(opus) = file.as_opus() {
                jbuilder.set_mime_type("audio/opus");
                if let Some(tag) = opus.xiph_comments() {
                    jbuilder.set_xiph(&tag);
                }
            }
            if let Some(mut flac) = file.as_flac() {
                jbuilder.set_mime_type("audio/flac");
                if let Some(tag) = flac.xiph_comments() {
                    jbuilder.set_xiph(&tag);
                }
            }
            if let Some(mut mpeg) = file.as_mpeg() {
                jbuilder.set_mime_type("audio/mpeg");
                if let Some(tag) = mpeg.id3v1_tag() {
                    jbuilder.set_id3v1(&tag);
                }
                if let Some(tag) = mpeg.id3v2_tag() {
                    jbuilder.set_id3v2(&tag);
                }
            }
            if let Some(mp4) = file.as_mp4() {
                jbuilder.set_mime_type("audio/mp4");
                if let Some(tag) = mp4.tag() {
                    jbuilder.set_mp4(&tag);
                }
            }
            if let Some(mut wav) = file.as_wav() {
                jbuilder.set_mime_type("audio/wav");
                if let Some(tag) = wav.id3v2_tag() {
                    jbuilder.set_id3v2(&tag);
                }
            }
        }
        None => {}
    }

    jbuilder.build().into_raw()
}
