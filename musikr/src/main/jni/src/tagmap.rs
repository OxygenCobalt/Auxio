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
        let map_class = self
            .env
            .borrow_mut()
            .find_class("java/util/HashMap")
            .unwrap();
        let map = self
            .env
            .borrow_mut()
            .new_object(&map_class, "()V", &[])
            .unwrap();

        for (key, values) in &self.map {
            let j_key = self.env.borrow().new_string(key).unwrap();
            let j_values: Vec<JObject> = values
                .iter()
                .map(|v| self.env.borrow().new_string(v).unwrap().into())
                .collect();

            // Create ArrayList for values
            let array_list_class = self
                .env
                .borrow_mut()
                .find_class("java/util/ArrayList")
                .unwrap();
            let array_list = self
                .env
                .borrow_mut()
                .new_object(array_list_class, "()V", &[])
                .unwrap();

            for value in j_values {
                self.env
                    .borrow_mut()
                    .call_method(
                        &array_list,
                        "add",
                        "(Ljava/lang/Object;)Z",
                        &[JValueGen::from(&value)],
                    )
                    .unwrap();
            }

            self.env
                .borrow_mut()
                .call_method(
                    &map,
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    &[JValueGen::from(&j_key), JValueGen::from(&array_list)],
                )
                .unwrap();
        }

        map
    }
}
