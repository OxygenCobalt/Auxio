/*
 * Copyright (c) 2024 Auxio Project
 * JVMTagMap.cpp is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
#include "JVMTagMap.h"

JVMTagMap::JVMTagMap(JNIEnv *env) : env(env) {
	jclass hashMapClass = env->FindClass("java/util/HashMap");
	jmethodID init = env->GetMethodID(hashMapClass, "<init>", "()V");
	hashMap = env->NewObject(hashMapClass, init);
	hashMapGetMethod = env->GetMethodID(hashMapClass, "get",
			"(Ljava/lang/Object;)Ljava/lang/Object;");
	hashMapPutMethod = env->GetMethodID(hashMapClass, "put",
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
	env->DeleteLocalRef(hashMapClass);

	jclass arrayListClass = env->FindClass("java/util/ArrayList");
	arrayListInitMethod = env->GetMethodID(arrayListClass, "<init>", "()V");
	arrayListAddMethod = env->GetMethodID(arrayListClass, "add",
			"(Ljava/lang/Object;)Z");
	env->DeleteLocalRef(arrayListClass);
}

JVMTagMap::~JVMTagMap() {
	env->DeleteLocalRef(hashMap);
}

void JVMTagMap::add(TagLib::String &key, std::string_view value) {
	jstring jKey = env->NewStringUTF(key.toCString(true));
	jstring jValue = env->NewStringUTF(value.data());

	// check if theres already a value arraylist in the map
	jobject existingValue = env->CallObjectMethod(hashMap, hashMapGetMethod,
			jKey);
	// if there is, add to the value to the existing arraylist
	if (existingValue != nullptr) {
		env->CallBooleanMethod(existingValue, arrayListAddMethod, jValue);
	} else {
		// if there isn't, create a new arraylist and add the value to it
		jclass arrayListClass = env->FindClass("java/util/ArrayList");
		jobject arrayList = env->NewObject(arrayListClass, arrayListInitMethod);
		env->CallBooleanMethod(arrayList, arrayListAddMethod, jValue);
		env->CallObjectMethod(hashMap, hashMapPutMethod, jKey, arrayList);
		env->DeleteLocalRef(arrayListClass);
	}
}

void JVMTagMap::add(TagLib::String &key, TagLib::StringList &value) {
	if (value.isEmpty()) {
		// Nothing to add
		return;
	}
	jstring jKey = env->NewStringUTF(key.toCString(true));

	// check if theres already a value arraylist in the map
	jobject existingValue = env->CallObjectMethod(hashMap, hashMapGetMethod,
			jKey);
	// if there is, add to the value to the existing arraylist
	if (existingValue != nullptr) {
		for (auto &val : value) {
			jstring jValue = env->NewStringUTF(val.toCString(true));
			env->CallBooleanMethod(existingValue, arrayListAddMethod, jValue);
		}
	} else {
		// if there isn't, create a new arraylist and add the value to it
		jclass arrayListClass = env->FindClass("java/util/ArrayList");
		jobject arrayList = env->NewObject(arrayListClass, arrayListInitMethod);
		for (auto &val : value) {
			jstring jValue = env->NewStringUTF(val.toCString(true));
			env->CallBooleanMethod(arrayList, arrayListAddMethod, jValue);
		}
		env->CallObjectMethod(hashMap, hashMapPutMethod, jKey, arrayList);
		env->DeleteLocalRef(arrayListClass);
	}
}

jobject JVMTagMap::getObject() {
	return hashMap;
}
