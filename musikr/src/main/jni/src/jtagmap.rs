use jni::{
    objects::{JClass, JObject, JString, JValueGen},
    JNIEnv,
};
use std::cell::RefCell;
use std::rc::Rc;

pub struct JTagMap<'local> {
    env: Rc<RefCell<JNIEnv<'local>>>,
    tag_map: JObject<'local>,
    array_list_class: JClass<'local>,
}

impl<'local> JTagMap<'local> {
    pub fn new(env: Rc<RefCell<JNIEnv<'local>>>) -> Self {
        // Get NativeTagMap class and create instance
        let tag_map_class = env.borrow_mut().find_class("org/oxycblt/musikr/metadata/NativeTagMap").unwrap();
        let tag_map = env.borrow_mut().new_object(&tag_map_class, "()V", &[]).unwrap();

        // Get ArrayList class
        let array_list_class = env.borrow_mut().find_class("java/util/ArrayList").unwrap();

        Self {
            env,
            tag_map,
            array_list_class,
        }
    }

    fn create_array_list(&self, values: &[String]) -> JObject<'local> {
        let mut env = self.env.borrow_mut();
        let array_list = env.new_object(&self.array_list_class, "()V", &[]).unwrap();
        
        // Create all JString values first
        let j_values: Vec<JString> = values.iter()
            .map(|value| env.new_string(value).unwrap())
            .collect();
        
        // Then add them to the ArrayList
        for j_value in j_values {
            env.call_method(
                &array_list,
                "add",
                "(Ljava/lang/Object;)Z",
                &[JValueGen::Object(&j_value)],
            ).unwrap();
        }
        
        array_list
    }

    pub fn add_id(&self, id: impl Into<String>, value: impl Into<String>) {
        let mut env = self.env.borrow_mut();
        let j_id = env.new_string(id.into()).unwrap();
        let j_value = env.new_string(value.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addID",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            &[JValueGen::Object(&j_id), JValueGen::Object(&j_value)],
        ).unwrap();
    }

    pub fn add_id_list(&self, id: impl Into<String>, values: Vec<String>) {
        // Create array list first while holding the borrow
        let j_values = self.create_array_list(&values);
        
        // Then create the id and make the call with a new borrow
        let mut env = self.env.borrow_mut();
        let j_id = env.new_string(id.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addID",
            "(Ljava/lang/String;Ljava/util/List;)V",
            &[JValueGen::Object(&j_id), JValueGen::Object(&j_values)],
        ).unwrap();
    }

    pub fn add_custom(&self, description: impl Into<String>, value: impl Into<String>) {
        let mut env = self.env.borrow_mut();
        let j_description = env.new_string(description.into()).unwrap();
        let j_value = env.new_string(value.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addCustom",
            "(Ljava/lang/String;Ljava/lang/String;)V",
            &[JValueGen::Object(&j_description), JValueGen::Object(&j_value)],
        ).unwrap();
    }

    pub fn add_custom_list(&self, description: impl Into<String>, values: Vec<String>) {
        let j_values = self.create_array_list(&values);
        
        let mut env = self.env.borrow_mut();
        let j_description = env.new_string(description.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addCustom",
            "(Ljava/lang/String;Ljava/util/List;)V",
            &[JValueGen::Object(&j_description), JValueGen::Object(&j_values)],
        ).unwrap();
    }

    pub fn add_combined(&self, id: impl Into<String>, description: impl Into<String>, value: impl Into<String>) {
        let mut env = self.env.borrow_mut();
        let j_id = env.new_string(id.into()).unwrap();
        let j_description = env.new_string(description.into()).unwrap();
        let j_value = env.new_string(value.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addCombined",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            &[JValueGen::Object(&j_id), JValueGen::Object(&j_description), JValueGen::Object(&j_value)],
        ).unwrap();
    }

    pub fn add_combined_list(&self, id: impl Into<String>, description: impl Into<String>, values: Vec<String>) {
        let j_values = self.create_array_list(&values);
        
        let mut env = self.env.borrow_mut();
        let j_id = env.new_string(id.into()).unwrap();
        let j_description = env.new_string(description.into()).unwrap();
        
        env.call_method(
            &self.tag_map,
            "addCombined",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
            &[JValueGen::Object(&j_id), JValueGen::Object(&j_description), JValueGen::Object(&j_values)],
        ).unwrap();
    }

    pub fn get_object(&self) -> JObject<'local> {
        let mut env = self.env.borrow_mut();
        env.call_method(
            &self.tag_map,
            "getObject",
            "()Ljava/util/Map;",
            &[],
        ).unwrap().l().unwrap()
    }
}
