use jni::{
    objects::{JObject, JValueGen},
    sys::jlong,
    JNIEnv,
};
use std::cell::RefCell;
use std::rc::Rc;

use crate::taglib::{audioproperties, id3v1, id3v2, mp4, xiph};

use crate::tagmap::JTagMap;

pub struct JMetadataBuilder<'local, 'file_ref> {
    env: Rc<RefCell<JNIEnv<'local>>>,
    id3v2: JTagMap<'local>,
    xiph: JTagMap<'local>,
    mp4: JTagMap<'local>,
    cover: Option<Vec<u8>>,
    properties: Option<audioproperties::AudioProperties<'file_ref>>,
    mime_type: Option<String>,
}

impl<'local, 'file_ref> JMetadataBuilder<'local, 'file_ref> {
    pub fn new(env: Rc<RefCell<JNIEnv<'local>>>) -> Self {
        Self {
            id3v2: JTagMap::new(env.clone()),
            xiph: JTagMap::new(env.clone()),
            mp4: JTagMap::new(env.clone()),
            env,
            cover: None,
            properties: None,
            mime_type: None,
        }
    }

    pub fn set_mime_type(&mut self, mime_type: impl Into<String>) {
        self.mime_type = Some(mime_type.into());
    }

    pub fn set_id3v1(&mut self, tag: &id3v1::ID3v1Tag) {
        self.id3v2.add_id("TIT2", tag.title().to_string());
        self.id3v2.add_id("TPE1", tag.artist().to_string());
        self.id3v2.add_id("TALB", tag.album().to_string());
        self.id3v2.add_id("TRCK", tag.track().to_string());
        self.id3v2.add_id("TYER", tag.year().to_string());

        let genre = tag.genre_index();
        if genre != 255 {
            self.id3v2.add_id("TCON", genre.to_string());
        }
    }

    pub fn set_id3v2(&mut self, tag: &id3v2::ID3v2Tag) {
        let mut first_pic = None;
        let mut front_cover_pic = None;

        if let Some(frames) = tag.frames() {
            for mut frame in frames.to_vec() {
                if let Some(text_frame) = frame.as_text_identification() {
                    if let Some(field_list) = text_frame.field_list() {
                        let values: Vec<String> = field_list
                            .to_vec()
                            .into_iter()
                            .map(|s| s.to_string())
                            .collect();
                        self.id3v2.add_id_list(frame.id().to_string_lossy(), values);
                    }
                } else if let Some(user_text_frame) = frame.as_user_text_identification() {
                    if let Some(values) = user_text_frame.values() {
                        let mut values = values.to_vec();
                        if !values.is_empty() {
                            let description = values.remove(0);
                            for value in values {
                                self.id3v2.add_combined(
                                    frame.id().to_string_lossy(),
                                    description.clone(),
                                    value.to_string(),
                                );
                            }
                        }
                    }
                } else if let Some(picture_frame) = frame.as_attached_picture() {
                    if first_pic.is_none() {
                        first_pic = picture_frame.picture().map(|p| p.to_vec());
                    }
                    // TODO: Check for front cover type when bindings are available
                    if front_cover_pic.is_none() {
                        front_cover_pic = picture_frame.picture().map(|p| p.to_vec());
                    }
                }
            }
        }

        // Prefer front cover, fall back to first picture
        self.cover = front_cover_pic.or(first_pic);
    }

    pub fn set_xiph(&mut self, tag: &xiph::XiphComment) {
        for (key, values) in tag.field_list_map().to_hashmap() {
            let values: Vec<String> = values.to_vec().into_iter().map(|s| s.to_string()).collect();
            self.xiph.add_id_list(key.to_uppercase(), values);
        }

        // TODO: Handle FLAC pictures when bindings are available
    }

    pub fn set_mp4(&mut self, tag: &mp4::MP4Tag) {
        let map = tag.item_map().to_hashmap();

        for (key, item) in map {
            if key == "covr" {
                if let Some(mp4::MP4Data::CoverArtList(cover_list)) = item.data() {
                    let covers = cover_list.to_vec();
                    // Prefer PNG/JPEG covers
                    let preferred_cover = covers.iter().find(|c| {
                        matches!(
                            c.format(),
                            mp4::CoverArtFormat::PNG | mp4::CoverArtFormat::JPEG
                        )
                    });

                    if let Some(cover) = preferred_cover {
                        self.cover = Some(cover.data().to_vec());
                    } else if let Some(first_cover) = covers.first() {
                        self.cover = Some(first_cover.data().to_vec());
                    }
                    continue;
                }
            }

            if let Some(data) = item.data() {
                match data {
                    mp4::MP4Data::StringList(list) => {
                        let values: Vec<String> =
                            list.to_vec().into_iter().map(|s| s.to_string()).collect();
                        if key.starts_with("----") {
                            if let Some(split_idx) = key.find(':') {
                                let (atom_name, atom_desc) = key.split_at(split_idx);
                                let atom_desc = &atom_desc[1..]; // Skip the colon
                                for value in values {
                                    self.mp4.add_combined(atom_name, atom_desc, value);
                                }
                            }
                        } else {
                            self.mp4.add_id_list(key, values);
                        }
                    }
                    mp4::MP4Data::Int(v) => self.mp4.add_id(key, v.to_string()),
                    mp4::MP4Data::UInt(v) => self.mp4.add_id(key, v.to_string()),
                    mp4::MP4Data::LongLong(v) => self.mp4.add_id(key, v.to_string()),
                    mp4::MP4Data::IntPair(pair) => {
                        if let Some((first, second)) = pair.to_tuple() {
                            self.mp4.add_id(key, format!("{}/{}", first, second));
                        }
                    }
                    _ => continue,
                }
            }
        }
    }

    pub fn set_properties(&mut self, properties: audioproperties::AudioProperties<'file_ref>) {
        self.properties = Some(properties);
    }

    pub fn build(&self) -> JObject {
        // Create Properties object
        let properties_class = self
            .env
            .borrow_mut()
            .find_class("org/oxycblt/musikr/metadata/Properties")
            .unwrap();
        let properties = if let Some(props) = &self.properties {
            let mime_type = self.mime_type.as_deref().unwrap_or("").to_string();
            let j_mime_type = self.env.borrow().new_string(mime_type).unwrap();

            self.env
                .borrow_mut()
                .new_object(
                    properties_class,
                    "(Ljava/lang/String;JII)V",
                    &[
                        JValueGen::from(&j_mime_type),
                        (props.length_in_milliseconds() as jlong).into(),
                        (props.bitrate() as i32).into(),
                        (props.sample_rate() as i32).into(),
                    ],
                )
                .unwrap()
        } else {
            let empty_mime = self.env.borrow().new_string("").unwrap();
            self.env
                .borrow_mut()
                .new_object(
                    properties_class,
                    "(Ljava/lang/String;JII)V",
                    &[
                        JValueGen::from(&empty_mime),
                        0i64.into(),
                        0i32.into(),
                        0i32.into(),
                    ],
                )
                .unwrap()
        };

        // Create cover byte array if present
        let cover_array = if let Some(cover_data) = &self.cover {
            let array = self
                .env
                .borrow()
                .new_byte_array(cover_data.len() as i32)
                .unwrap();
            self.env
                .borrow()
                .set_byte_array_region(&array, 0, bytemuck::cast_slice(cover_data))
                .unwrap();
            array.into()
        } else {
            JObject::null()
        };

        // Create Metadata object
        let metadata_class = self
            .env
            .borrow_mut()
            .find_class("org/oxycblt/musikr/metadata/Metadata")
            .unwrap();
        self.env.borrow_mut().new_object(
            metadata_class,
            "(Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;[BLorg/oxycblt/musikr/metadata/Properties;)V",
            &[
                JValueGen::from(&self.id3v2.get_object()),
                JValueGen::from(&self.xiph.get_object()),
                JValueGen::from(&self.mp4.get_object()),
                JValueGen::from(&cover_array),
                JValueGen::from(&properties),
            ],
        ).unwrap()
    }
}
