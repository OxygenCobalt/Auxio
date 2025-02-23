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
#include "JStringRef.h"

JInputStream::JInputStream(JNIEnv *env, jobject jInputStream) : env(env), jInputStream(
        jInputStream) {
    JClassRef jInputStreamClass = { env,
            "org/oxycblt/musikr/metadata/NativeInputStream" };
    if (!env->IsInstanceOf(jInputStream, *jInputStreamClass)) {
        throw std::runtime_error("Object is not NativeInputStream");
    }
    jmethodID jInputStreamNameMethod = jInputStreamClass.method("name",
            "()Ljava/lang/String;");
    jInputStreamReadBlockMethod = jInputStreamClass.method("readBlock",
            "(Ljava/nio/ByteBuffer;)I");
    jInputStreamIsOpenMethod = jInputStreamClass.method("isOpen", "()Z");
    jInputStreamSeekFromBeginningMethod = jInputStreamClass.method(
            "seekFromBeginning", "(J)Z");
    jInputStreamSeekFromCurrentMethod = jInputStreamClass.method(
            "seekFromCurrent", "(J)Z");
    jInputStreamSeekFromEndMethod = jInputStreamClass.method("seekFromEnd",
            "(J)Z");
    jInputStreamTellMethod = jInputStreamClass.method("tell", "()J");
    jInputStreamLengthMethod = jInputStreamClass.method("length", "()J");
    JStringRef jName = { env, reinterpret_cast<jstring>(env->CallObjectMethod(
            jInputStream, jInputStreamNameMethod)) };
    _name = TagLib::String(env->GetStringUTFChars(*jName, nullptr));
}

JInputStream::~JInputStream() {
    // The implicit assumption is that inputStream is managed by the owner,
    // so we don't need to delete any references here
}

TagLib::FileName /* const char * */JInputStream::name() const {
    return _name.toCString(true);
}

jint JInputStream::readBlockImpl(TagLib::ByteVector &buf) {
    jobject wrappedByteBuffer = env->NewDirectByteBuffer(buf.data(),
            buf.size());
    if (wrappedByteBuffer == nullptr) {
        throw std::runtime_error("Failed to wrap ByteBuffer");
    }
    JObjectRef byteBuffer { env, wrappedByteBuffer };
    jint read = env->CallIntMethod(jInputStream, jInputStreamReadBlockMethod,
            *byteBuffer);
    return read;
}

TagLib::ByteVector JInputStream::readBlock(size_t length) {
    // We have to invert the buffer allocation here
    TagLib::ByteVector buf { static_cast<unsigned int>(length), 0 };
    jint read = readBlockImpl(buf);
    if (read >= 0) {
        buf.resize(read);
        return buf;
    } else if (read == -1) {
        buf.resize(0);
        return buf;
    } else {
        throw std::runtime_error("Failed to read block, see logs");
    }
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

