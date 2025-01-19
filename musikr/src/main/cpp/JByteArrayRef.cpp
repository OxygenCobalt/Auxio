/*
 * Copyright (c) 2025 Auxio Project
 * JByteArrayRef.cpp is part of Auxio.
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
 
#include "JByteArrayRef.h"

JByteArrayRef::JByteArrayRef(JNIEnv *env, TagLib::ByteVector &data) : env(env) {
    auto size = static_cast<jsize>(data.size());
    array = env->NewByteArray(size);
    env->SetByteArrayRegion(array, 0, static_cast<jsize>(size),
            reinterpret_cast<const jbyte*>(data.data()));
}

JByteArrayRef::JByteArrayRef(JNIEnv *env, jbyteArray array) : env(env), array(
        array) {
}

JByteArrayRef::~JByteArrayRef() {
    env->DeleteLocalRef(array);
}

TagLib::ByteVector JByteArrayRef::copy() {
    jsize length = env->GetArrayLength(array);
    auto data = env->GetByteArrayElements(array, nullptr);
    TagLib::ByteVector byteVector(reinterpret_cast<const char*>(data), length);
    env->ReleaseByteArrayElements(array, data, JNI_ABORT);
    return byteVector;
}

jbyteArray& JByteArrayRef::operator*() {
    return array;
}

