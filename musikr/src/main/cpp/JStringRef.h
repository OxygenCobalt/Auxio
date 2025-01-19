/*
 * Copyright (c) 2025 Auxio Project
 * JStringRef.h is part of Auxio.
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
 
#ifndef AUXIO_JSTRINGREF_H
#define AUXIO_JSTRINGREF_H

#include <jni.h>
#include <taglib/tstring.h>

class JStringRef {
public:
    JStringRef(JNIEnv *env, TagLib::String string);

    ~JStringRef();

    JStringRef(const JStringRef&) = delete;

    JStringRef& operator=(const JStringRef&) = delete;

    jstring& operator*();

private:
    JNIEnv *env;
    jstring string;
};

#endif //AUXIO_JSTRINGREF_H
