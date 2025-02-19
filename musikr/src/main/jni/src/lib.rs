extern crate link_cplusplus;
use android_logger::Config;
use jni::objects::{JClass, JObject};
use jni::sys::jobject;
use jni::JNIEnv;
use log::LevelFilter;
use std::cell::RefCell;
use std::panic;
use std::rc::Rc;

mod jbuilder;
mod jstream;
mod taglib;
mod tagmap;

use jbuilder::JMetadataBuilder;
use jstream::JInputStream;
use taglib::file_ref::FileRef;
use android_logger::Filter;
type SharedEnv<'local> = Rc<RefCell<JNIEnv<'local>>>;

// Initialize the logger and panic hook when the library is loaded
#[ctor::ctor]
fn init() {
    // Initialize Android logger
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Error)
            .with_tag("musikr")
    );

    // Set custom panic hook
    panic::set_hook(Box::new(|panic_info| {
        let message = if let Some(s) = panic_info.payload().downcast_ref::<String>() {
            s.clone()
        } else if let Some(s) = panic_info.payload().downcast_ref::<&str>() {
            s.to_string()
        } else {
            "Unknown panic message".to_string()
        };

        let location = if let Some(location) = panic_info.location() {
            format!("{}:{}:{}", location.file(), location.line(), location.column())
        } else {
            "Unknown location".to_string()
        };

        log::error!(target: "musikr", "Panic occurred at {}: {}", location, message);
    }));
}

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
                if let Some(mut tag) = vorbis.xiph_comments() {
                    jbuilder.set_xiph(&mut tag);
                }
            }
            if let Some(opus) = file.as_opus() {
                jbuilder.set_mime_type("audio/opus");
                if let Some(mut tag) = opus.xiph_comments() {
                    jbuilder.set_xiph(&mut tag);
                }
            }
            if let Some(mut flac) = file.as_flac() {
                jbuilder.set_mime_type("audio/flac");
                if let Some(tag) = flac.id3v1_tag() {
                    jbuilder.set_id3v1(&tag);
                }
                if let Some(tag) = flac.id3v2_tag() {
                    jbuilder.set_id3v2(&tag);
                }
                if let Some(mut tag) = flac.xiph_comments() {
                    jbuilder.set_xiph(&mut tag);
                }
                jbuilder.set_flac_pictures(&flac.picture_list());
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


    let metadata = jbuilder.build();
    metadata.into_raw()
}
