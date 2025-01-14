/*
 * Copyright (c) 2024 Auxio Project
 * JVMInputStream.cpp is part of Auxio.
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
 
#include "JVMInputStream.h"
#include "util.h"

#include <cmath>

// TODO: Handle stream exceptions
JVMInputStream::JVMInputStream(JNIEnv *env, jobject inputStream) : env(env), inputStream(
        inputStream) {
    TRY(auto isNativeInputStream = env->IsInstanceOf(inputStream,
                    env->FindClass("org/oxycblt/musikr/metadata/NativeInputStream")))
    if (!isNativeInputStream) {
        throw std::runtime_error("oStream is not an instance of TagLibOStream");
    }
    TRY(jclass inputStreamClass = env->FindClass(
                    "org/oxycblt/musikr/metadata/NativeInputStream"));
    TRY(
            inputStreamReadBlockMethod = env->GetMethodID(inputStreamClass,
                    "readBlock", "(J)[B"));
    TRY(
            inputStreamIsOpenMethod = env->GetMethodID(inputStreamClass,
                    "isOpen", "()Z"));
    TRY(
            inputStreamSeekFromBeginningMethod = env->GetMethodID(
                    inputStreamClass, "seekFromBeginning", "(J)V"));
    TRY(
            inputStreamSeekFromCurrentMethod = env->GetMethodID(
                    inputStreamClass, "seekFromCurrent", "(J)V"));
    TRY(
            inputStreamSeekFromEndMethod = env->GetMethodID(inputStreamClass,
                    "seekFromEnd", "(J)V"));
    TRY(
            inputStreamTellMethod = env->GetMethodID(inputStreamClass, "tell",
                    "()J"));
    TRY(
            inputStreamLengthMethod = env->GetMethodID(inputStreamClass,
                    "length", "()J"));
    TRY(env->DeleteLocalRef(inputStreamClass));
}

JVMInputStream::~JVMInputStream() {
    // The implicit assumption is that inputStream is managed by the owner,
    // so we don't need to delete any references here
}

TagLib::FileName JVMInputStream::name() const {
    // Not actually used except in FileRef, can safely ignore.
    return "";
}

TagLib::ByteVector JVMInputStream::readBlock(size_t length) {
    TRY(auto data = (jbyteArray) env->CallObjectMethod(inputStream,
                    inputStreamReadBlockMethod, length));
    // Check for an exception
    // If we don't do this, data == nullptr and the remaining calls crash.
    TRY(jsize dataLength = env->GetArrayLength(data));
    TRY(auto dataBytes = env->GetByteArrayElements(data, nullptr));
    TagLib::ByteVector byteVector(reinterpret_cast<const char*>(dataBytes),
            dataLength);
    TRY(env->ReleaseByteArrayElements(data, dataBytes, JNI_ABORT));
    return byteVector;
}

void JVMInputStream::writeBlock(const TagLib::ByteVector &data) {
    throw std::runtime_error("Not implemented");
}

void JVMInputStream::insert(const TagLib::ByteVector &data,
        TagLib::offset_t start, size_t replace) {
    throw std::runtime_error("Not implemented");
}

void JVMInputStream::removeBlock(TagLib::offset_t start, size_t length) {
    throw std::runtime_error("Not implemented");
}

bool JVMInputStream::readOnly() const {
    return true;
}

bool JVMInputStream::isOpen() const {
    TRY(auto result = env->CallBooleanMethod(inputStream, inputStreamIsOpenMethod));
    return result;
}

void JVMInputStream::seek(TagLib::offset_t offset, Position p) {
    auto joffset = static_cast<jlong>(std::llround(offset));
    switch (p) {
    case Beginning:
        TRY(
                env->CallVoidMethod(inputStream,
                        inputStreamSeekFromBeginningMethod, joffset));
        break;
    case Current:
        env->CallVoidMethod(inputStream, inputStreamSeekFromCurrentMethod,
                joffset);
        break;
    case End:
        env->CallVoidMethod(inputStream, inputStreamSeekFromEndMethod, joffset);
        break;
    }
}

void JVMInputStream::clear() {
    // Nothing to do
}

TagLib::offset_t JVMInputStream::tell() const {
    TRY(jlong jposition = env->CallLongMethod(inputStream, inputStreamTellMethod));
    return static_cast<TagLib::offset_t>(jposition);
}

TagLib::offset_t JVMInputStream::length() {
    TRY(jlong jlength = env->CallLongMethod(inputStream, inputStreamLengthMethod));
    return static_cast<TagLib::offset_t>(jlength);
}

void JVMInputStream::truncate(TagLib::offset_t length) {
    throw std::runtime_error("Not implemented");
}
