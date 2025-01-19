/*
 * Copyright (c) 2024 Auxio Project
 * JInputStream.cpp is part of Auxio.
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
 
#include "JInputStream.h"

#include <cmath>

#include "JClassRef.h"
#include "JByteArrayRef.h"

JInputStream::JInputStream(JNIEnv *env, jobject jInputStream) : env(env), jInputStream(
        jInputStream) {
    JClassRef jInputStreamClass = { env,
            "org/oxycblt/musikr/metadata/NativeInputStream" };
    if (!env->IsInstanceOf(jInputStream, *jInputStreamClass)) {
        throw std::runtime_error("oStream is not an instance of TagLibOStream");
    }
    jInputStreamReadBlockMethod = jInputStreamClass.method("readBlock",
            "(J)[B");
    jInputStreamIsOpenMethod = jInputStreamClass.method("isOpen", "()Z");
    jInputStreamSeekFromBeginningMethod = jInputStreamClass.method(
            "seekFromBeginning", "(J)Z");
    jInputStreamSeekFromCurrentMethod = jInputStreamClass.method(
            "seekFromCurrent", "(J)Z");
    jInputStreamSeekFromEndMethod = jInputStreamClass.method("seekFromEnd",
            "(J)Z");
    jInputStreamTellMethod = jInputStreamClass.method("tell", "()J");
    jInputStreamLengthMethod = jInputStreamClass.method("length", "()J");
}

JInputStream::~JInputStream() {
    // The implicit assumption is that inputStream is managed by the owner,
    // so we don't need to delete any references here
}

TagLib::FileName JInputStream::name() const {
    // Not actually used except in FileRef, can safely ignore.
    return "";
}

TagLib::ByteVector JInputStream::readBlock(size_t length) {
    // Do manual memory management here since we don't to avoid the added abstraction
    // overhead of a smart JByteArrayRef.
    auto data = env->CallObjectMethod(jInputStream, jInputStreamReadBlockMethod,
            static_cast<jlong>(length));
    if (data == nullptr) {
        throw std::runtime_error("Failed to read block, see logs");
    }
    JByteArrayRef jByteArray = { env, reinterpret_cast<jbyteArray>(data) };
    return jByteArray.copy();
}

void JInputStream::writeBlock(const TagLib::ByteVector &data) {
    throw std::runtime_error("Not implemented");
}

void JInputStream::insert(const TagLib::ByteVector &data,
        TagLib::offset_t start, size_t replace) {
    throw std::runtime_error("Not implemented");
}

void JInputStream::removeBlock(TagLib::offset_t start, size_t length) {
    throw std::runtime_error("Not implemented");
}

bool JInputStream::readOnly() const {
    return true;
}

bool JInputStream::isOpen() const {
    return env->CallBooleanMethod(jInputStream, jInputStreamIsOpenMethod);
}

void JInputStream::seek(TagLib::offset_t offset, Position p) {
    auto joffset = static_cast<jlong>(std::llround(offset));
    jboolean result;
    switch (p) {
    case Beginning:
        result = env->CallBooleanMethod(jInputStream,
                jInputStreamSeekFromBeginningMethod, joffset);
        break;
    case Current:
        result = env->CallBooleanMethod(jInputStream,
                jInputStreamSeekFromCurrentMethod, joffset);
        break;
    case End:
        result = env->CallBooleanMethod(jInputStream,
                jInputStreamSeekFromEndMethod, joffset);
        break;
    }
    if (!result) {
        throw std::runtime_error("Failed to seek, see logs");
    }
}

void JInputStream::clear() {
    // Nothing to do
}

TagLib::offset_t JInputStream::tell() const {
    jlong jposition = env->CallLongMethod(jInputStream, jInputStreamTellMethod);
    if (jposition == INT64_MIN) {
        throw std::runtime_error("Failed to get position, see logs");
    }
    return static_cast<TagLib::offset_t>(jposition);
}

TagLib::offset_t JInputStream::length() {
    jlong jlength = env->CallLongMethod(jInputStream, jInputStreamLengthMethod);
    if (jlength == INT64_MIN) {
        throw std::runtime_error("Failed to get length, see logs");
    }
    return static_cast<TagLib::offset_t>(jlength);
}

void JInputStream::truncate(TagLib::offset_t length) {
    throw std::runtime_error("Not implemented");
}

