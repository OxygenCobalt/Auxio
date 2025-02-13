use jni::objects::{JClass, JObject};
use jni::sys::jstring;
use jni::JNIEnv;

mod taglib;
mod jstream;

use taglib::file::FileRef;

use jstream::JInputStream;
pub use taglib::*;

#[no_mangle]
pub extern "C" fn Java_org_oxycblt_musikr_metadata_MetadataJNI_openFile<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JObject<'local>,
) -> jstring {
    // Create JInputStream from the Java input stream
    let stream = JInputStream::new(&mut env, input);
    let file_ref = FileRef::new(stream);
    // file_ref.file().and_then(|file| {
    //     let audio_properties = file.audio_properties().map(|props| AudioProperties {
    //         length_in_milliseconds: props.length_in_milliseconds(),
    //         bitrate_in_kilobits_per_second: props.bitrate(),
    //         sample_rate_in_hz: props.sample_rate(),
    //         number_of_channels: props.channels(),
    //     });

    //     if let Some(vorbis_file) = file.as_vorbis() {
    //         let xiph_comments = vorbis_file
    //             .xiph_comments()
    //             .map(|comments| comments.field_list_map().to_hashmap());

    //     } else if let Some(opus_file) = file.as_opus() {
    //         let xiph_comments = opus_file
    //             .xiph_comments()
    //             .map(|comments| comments.field_list_map().to_hashmap());

    //         Some(File::Opus {
    //             audio_properties,
    //             xiph_comments,
    //         })
    //     } else if let Some(flac_file) = file.as_flac() {
    //         let xiph_comments = flac_file
    //             .xiph_comments()
    //             .map(|comments| comments.field_list_map().to_hashmap());
    //         Some(File::FLAC {
    //             audio_properties,
    //             xiph_comments,
    //         })
    //     } else {
    //         Some(File::Unknown { audio_properties })
    //     }
    // });
    
    // Return the title
    let output = env.new_string("title").expect("Couldn't create string!");
    output.into_raw()
}
