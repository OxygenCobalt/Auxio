/*
 * Copyright (c) 2024 Auxio Project
 * JTagMap.h is part of Auxio.
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
 
#ifndef AUXIO_JTAGMAP_H
#define AUXIO_JTAGMAP_H

#include <jni.h>
#include <string_view>
#include <taglib/tstring.h>
#include <taglib/tstringlist.h>

#include "JObjectRef.h"
#include "JClassRef.h"

class JTagMap {
public:
    JTagMap(JNIEnv *env);

    JTagMap(const JTagMap&) = delete;
    JTagMap& operator=(const JTagMap&) = delete;

    void add_id(TagLib::String id, TagLib::String value);
    void add_id(TagLib::String id, TagLib::StringList values);

    void add_custom(TagLib::String description, TagLib::String value);
    void add_custom(TagLib::String description, TagLib::StringList values);

    void add_combined(TagLib::String id, TagLib::String description,
            TagLib::String value);
    void add_combined(TagLib::String id, TagLib::String description,
            TagLib::StringList values);

    std::unique_ptr<JObjectRef> getObject();

private:
    JNIEnv *env;

    std::unique_ptr<JObjectRef> jTagMap;
    jmethodID jTagMapAddIdSingleMethod;
    jmethodID jTagMapAddIdListMethod;
    jmethodID jTagMapAddCustomSingleMethod;
    jmethodID jTagMapAddCustomListMethod;
    jmethodID jTagMapAddCombinedSingleMethod;
    jmethodID jTagMapAddCombinedListMethod;
    jmethodID jTagMapGetObjectMethod;

    std::unique_ptr<JClassRef> jArrayListClass;
    jmethodID jArrayListInitMethod;
    jmethodID jArrayListAddMethod;
};

#endif //AUXIO_JTAGMAP_H
