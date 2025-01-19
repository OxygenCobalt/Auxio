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

void JVMTagMap::add_id(const TagLib::String id, const TagLib::String value) {
    jstring jid = env->NewStringUTF(id.toCString(true));
    jstring jvalue = env->NewStringUTF(value.toCString(true));
    env->CallVoidMethod(tagMap, tagMapAddIdSingleMethod, jid, jvalue);
    env->DeleteLocalRef(jid);
    env->DeleteLocalRef(jvalue);
}

void JVMTagMap::add_id(const TagLib::String id,
        const TagLib::StringList values) {
    jstring jid = env->NewStringUTF(id.toCString(true));
    jobject jvalues = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : values) {
        jstring jvalue = env->NewStringUTF(item.toCString(true));
        env->CallBooleanMethod(jvalues, arrayListAddMethod, jvalue);
        env->DeleteLocalRef(jvalue);
    }
    env->CallVoidMethod(tagMap, tagMapAddIdListMethod, jid, jvalues);
    env->DeleteLocalRef(jid);
}

void JVMTagMap::add_custom(const TagLib::String description,
        const TagLib::String value) {
    jstring jdescription = env->NewStringUTF(description.toCString(true));
    jstring jvalue = env->NewStringUTF(value.toCString(true));
    env->CallVoidMethod(tagMap, tagMapAddCustomSingleMethod, jdescription,
            jvalue);
    env->DeleteLocalRef(jdescription);
    env->DeleteLocalRef(jvalue);
}

void JVMTagMap::add_custom(const TagLib::String description,
        const TagLib::StringList values) {
    jstring jid = env->NewStringUTF(description.toCString(true));
    jobject jvalues = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : values) {
        jstring jvalue = env->NewStringUTF(item.toCString(true));
        env->CallBooleanMethod(jvalues, arrayListAddMethod, jvalue);
        env->DeleteLocalRef(jvalue);
    }
    env->CallVoidMethod(tagMap, tagMapAddCustomListMethod, jid, jvalues);
    env->DeleteLocalRef(jid);
    env->DeleteLocalRef(jvalues);
}

void JVMTagMap::add_combined(const TagLib::String id,
        const TagLib::String description, const TagLib::String value) {
    jstring jid = env->NewStringUTF(id.toCString(true));
    jstring jdescription = env->NewStringUTF(description.toCString(true));
    jstring jvalue = env->NewStringUTF(value.toCString(true));
    env->CallVoidMethod(tagMap, tagMapAddCombinedSingleMethod, jid,
            jdescription, jvalue);
    env->DeleteLocalRef(jid);
    env->DeleteLocalRef(jdescription);
    env->DeleteLocalRef(jvalue);
}

void JVMTagMap::add_combined(const TagLib::String id,
        const TagLib::String description, const TagLib::StringList values) {
    jstring jid = env->NewStringUTF(id.toCString(true));
    jstring jdescription = env->NewStringUTF(description.toCString(true));
    jobject jvalues = env->NewObject(arrayListClass, arrayListInitMethod);
    for (auto &item : values) {
        jstring jvalue = env->NewStringUTF(item.toCString(true));
        env->CallBooleanMethod(jvalues, arrayListAddMethod, jvalue);
        env->DeleteLocalRef(jvalue);
    }
    env->CallVoidMethod(tagMap, tagMapAddCombinedListMethod, jid, jdescription,
            jvalues);
    env->DeleteLocalRef(jid);
    env->DeleteLocalRef(jdescription);
    env->DeleteLocalRef(jvalues);
}

jobject JVMTagMap::getObject() {
    return env->CallObjectMethod(tagMap, tagMapGetObjectMethod);
}
