/*
 * Copyright (c) 2025 Auxio Project
 * JClassRef.h is part of Auxio.
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
 
#ifndef AUXIO_JCLASSREF_H
#define AUXIO_JCLASSREF_H

#include <jni.h>

class JClassRef {
public:
    JClassRef(JNIEnv *env, const char *classpath);

    ~JClassRef();

    JClassRef(const JClassRef&) = delete;

    JClassRef& operator=(const JClassRef&) = delete;

    // Only exists to work around a broken lint that doesn't
    // realize that this class is a smart pointer to jclass.
    jmethodID method(const char *name, const char *signature);

    jclass& operator*();

private:
    JNIEnv *env;
    jclass clazz;
};

#endif //AUXIO_JCLASSREF_H
