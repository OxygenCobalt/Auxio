/*
 * Copyright (c) 2025 Auxio Project
 * JStringRef.cpp is part of Auxio.
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
 
#include "JStringRef.h"
#include "util.h"

JStringRef::JStringRef(JNIEnv *env, const TagLib::String string) {
    this->env = env;
    this->string = env->NewStringUTF(string.toCString(true));
}

JStringRef::~JStringRef() {
    env->DeleteLocalRef(string);
}

jstring& JStringRef::operator*() {
    return string;
}
