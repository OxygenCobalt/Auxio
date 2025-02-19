use jni::{
    objects::{JObject, JValueGen},
    JNIEnv,
};
use std::cell::RefCell;
use std::collections::HashMap;
use std::rc::Rc;

pub struct JTagMap<'local> {
    env: Rc<RefCell<JNIEnv<'local>>>,
    map: HashMap<String, Vec<String>>,
}

impl<'local> JTagMap<'local> {
    pub fn new(env: Rc<RefCell<JNIEnv<'local>>>) -> Self {
        Self {
            env,
            map: HashMap::new(),
        }
    }

    pub fn add_id(&mut self, id: impl Into<String>, value: impl Into<String>) {
        let id = id.into();
        let value = value.into();
        self.map.entry(id).or_default().push(value);
    }

    pub fn add_id_list(&mut self, id: impl Into<String>, values: Vec<String>) {
        let id = id.into();
        self.map.entry(id).or_default().extend(values);
    }

    pub fn add_combined(
        &mut self,
        id: impl Into<String>,
        description: impl Into<String>,
        value: impl Into<String>,
    ) {
        let id = id.into();
        let description = description.into();
        let value = value.into();
        let combined_key = format!("{}:{}", id, description);
        self.map.entry(combined_key).or_default().push(value);
    }

    pub fn get_object(&self) -> JObject {
        let mut env = self.env.borrow_mut();
        
        let map_class = env.find_class("java/util/HashMap").unwrap();
        let map = env.new_object(&map_class, "()V", &[]).unwrap();
        let array_list_class = env.find_class("java/util/ArrayList").unwrap();

        for (key, values) in &self.map {
            let j_key = env.new_string(key).unwrap();
            
            // Create ArrayList for values
            let array_list = env.new_object(&array_list_class, "()V", &[]).unwrap();

            // Convert all values to Java strings first
            let j_values: Vec<JObject> = values
                .iter()
                .map(|v| env.new_string(v).unwrap().into())
                .collect();

            // Add all values to ArrayList
            for value in j_values {
                env.call_method(
                    &array_list,
                    "add",
                    "(Ljava/lang/Object;)Z",
                    &[JValueGen::from(&value)],
                ).unwrap();
            }

            env.call_method(
                &map,
                "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                &[JValueGen::from(&j_key), JValueGen::from(&array_list)],
            ).unwrap();
        }

        map
    }
}
