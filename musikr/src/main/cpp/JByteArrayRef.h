/*
 * Copyright (c) 2025 Auxio Project
 * JByteArrayRef.h is part of Auxio.
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
 
#ifndef AUXIO_JBYTEARRAYREF_H
#define AUXIO_JBYTEARRAYREF_H

#include <jni.h>
#include <taglib/tbytevector.h>

class JByteArrayRef {
public:
    JByteArrayRef(JNIEnv *env, TagLib::ByteVector &data);
    JByteArrayRef(JNIEnv *env, jbyteArray array);

    ~JByteArrayRef();

    JByteArrayRef(const JByteArrayRef&) = delete;

    JByteArrayRef& operator=(const JByteArrayRef&) = delete;

    TagLib::ByteVector copy();

    jbyteArray& operator*();

private:
    JNIEnv *env;
    jbyteArray array;
};

#endif //AUXIO_JBYTEARRAYREF_H
