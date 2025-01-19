/*
 * Copyright (c) 2024 Auxio Project
 * JMetadataBuilder.h is part of Auxio.
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
 
#ifndef AUXIO_JMETADATABUILDER_H
#define AUXIO_JMETADATABUILDER_H

#include <jni.h>
#include <string_view>
#include <optional>

#include "taglib/id3v1tag.h"
#include "taglib/id3v2tag.h"
#include "taglib/xiphcomment.h"
#include "taglib/mp4tag.h"
#include "taglib/audioproperties.h"

#include "JTagMap.h"

class JMetadataBuilder {
public:
    JMetadataBuilder(JNIEnv *env);

    void setMimeType(TagLib::String type);
    void setId3v1(TagLib::ID3v1::Tag &tag);
    void setId3v2(TagLib::ID3v2::Tag &tag);
    void setXiph(TagLib::Ogg::XiphComment &tag);
    void setMp4(TagLib::MP4::Tag &tag);
    void setFlacPictures(TagLib::List<TagLib::FLAC::Picture*> &pics);
    void setProperties(TagLib::AudioProperties *properties);

    jobject build();

private:
    JNIEnv *env;

    TagLib::String mimeType;

    std::optional<TagLib::ByteVector> cover;
    TagLib::AudioProperties *properties;

    JTagMap id3v2;
    JTagMap xiph;
    JTagMap mp4;
};

#endif //AUXIO_JMETADATABUILDER_H
