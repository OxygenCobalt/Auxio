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

#include "util.h"

JVMTagMap::JVMTagMap(JNIEnv *env) : env(env) {
    jclass tagMapClass = env->FindClass(
            "org/oxycblt/musikr/metadata/NativeTagMap");
    jmethodID init = env->GetMethodID(tagMapClass, "<init>", "()V");
    tagMap = env->NewObject(tagMapClass, init);
    tagMapAddIdSingleMethod = env->GetMethodID(tagMapClass, "addID",
            "(Ljava/lang/String;Ljava/lang/String;)V");
    tagMapAddIdListMethod = env->GetMethodID(tagMapClass, "addID",
            "(Ljava/lang/String;Ljava/util/List;)V");
    tagMapAddCustomSingleMethod = env->GetMethodID(tagMapClass, "addCustom",
            "(Ljava/lang/String;Ljava/lang/String;)V");
    tagMapAddCustomListMethod = env->GetMethodID(tagMapClass, "addCustom",
            "(Ljava/lang/String;Ljava/util/List;)V");
    tagMapAddCombinedSingleMethod = env->GetMethodID(tagMapClass, "addCombined",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    tagMapAddCombinedListMethod = env->GetMethodID(tagMapClass, "addCombined",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V");
    tagMapGetObjectMethod = env->GetMethodID(tagMapClass, "getObject",
            "()Ljava/util/Map;");
    env->DeleteLocalRef(tagMapClass);

    arrayListClass = env->FindClass("java/util/ArrayList");
    arrayListInitMethod = env->GetMethodID(arrayListClass, "<init>", "()V");
    arrayListAddMethod = env->GetMethodID(arrayListClass, "add",
            "(Ljava/lang/Object;)Z");
}

JVMTagMap::~JVMTagMap() {
    env->DeleteLocalRef(tagMap);
    env->DeleteLocalRef(arrayListClass);
}

void JVMTagMap::add_id(TagLib::String &id, TagLib::String &value) {
    env->CallVoidMethod(tagMap, tagMapAddIdSingleMethod,
            env->NewStringUTF(id.toCString(true)),
            env->NewStringUTF(value.toCString(true)));
}

void JVMTagMap::add_id(TagLib::String &id, TagLib::StringList &value) {
    jobject arrayList = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : value) {
        env->CallBooleanMethod(arrayList, arrayListAddMethod,
                env->NewStringUTF(item.toCString(true)));
    }
    env->CallVoidMethod(tagMap, tagMapAddIdListMethod,
            env->NewStringUTF(id.toCString(true)), arrayList);
}

void JVMTagMap::add_custom(TagLib::String &description, TagLib::String &value) {
    env->CallVoidMethod(tagMap, tagMapAddCustomSingleMethod,
            env->NewStringUTF(description.toCString(true)),
            env->NewStringUTF(value.toCString(true)));
}

void JVMTagMap::add_custom(TagLib::String &description,
        TagLib::StringList &value) {
    jobject arrayList = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : value) {
        env->CallBooleanMethod(arrayList, arrayListAddMethod,
                env->NewStringUTF(item.toCString(true)));
    }
    env->CallVoidMethod(tagMap, tagMapAddCustomListMethod,
            env->NewStringUTF(description.toCString(true)), arrayList);
}

void JVMTagMap::add_combined(TagLib::String &id, TagLib::String &description,
        TagLib::String &value) {
    env->CallVoidMethod(tagMap, tagMapAddCombinedSingleMethod,
            env->NewStringUTF(id.toCString(true)),
            env->NewStringUTF(description.toCString(true)),
            env->NewStringUTF(value.toCString(true)));
}

void JVMTagMap::add_combined(TagLib::String &id, TagLib::String &description,
        TagLib::StringList &value) {
    jobject arrayList = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : value) {
        env->CallBooleanMethod(arrayList, arrayListAddMethod,
                env->NewStringUTF(item.toCString(true)));
    }
    env->CallVoidMethod(tagMap, tagMapAddCombinedListMethod,
            env->NewStringUTF(id.toCString(true)),
            env->NewStringUTF(description.toCString(true)), arrayList);
}

jobject JVMTagMap::getObject() {
    return env->CallObjectMethod(tagMap, tagMapGetObjectMethod);
}
