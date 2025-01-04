/*
 * Copyright (c) 2024 Auxio Project
 * JVMTagMap.h is part of Auxio.
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
 
#ifndef AUXIO_JVMTAGMAP_H
#define AUXIO_JVMTAGMAP_H

#include <jni.h>
#include <string_view>
#include <taglib/tstring.h>
#include <taglib/tstringlist.h>

class JVMTagMap {
public:
    JVMTagMap(JNIEnv *env);
    ~JVMTagMap();

    JVMTagMap(const JVMTagMap&) = delete;
    JVMTagMap& operator=(const JVMTagMap&) = delete;

    void add_id(TagLib::String &id, TagLib::String &value);
    void add_id(TagLib::String &id, TagLib::StringList &value);

    void add_custom(TagLib::String &description, TagLib::String &value);
    void add_custom(TagLib::String &description, TagLib::StringList &value);

    void add_combined(TagLib::String &id, TagLib::String &description, TagLib::String &value);
    void add_combined(TagLib::String &id, TagLib::String &description, TagLib::StringList &value);

    jobject getObject();

private:
    JNIEnv *env;
    jobject tagMap;
    jmethodID tagMapAddIdSingleMethod;
    jmethodID tagMapAddIdListMethod;
    jmethodID tagMapAddCustomSingleMethod;
    jmethodID tagMapAddCustomListMethod;
    jmethodID tagMapAddCombinedSingleMethod;
    jmethodID tagMapAddCombinedListMethod;
    jmethodID tagMapGetObjectMethod;
    jclass arrayListClass;
    jmethodID arrayListInitMethod;
    jmethodID arrayListAddMethod;
};

#endif //AUXIO_JVMTAGMAP_H
