/*
 * Copyright (c) 2024 Auxio Project
 * JMetadataBuilder.cpp is part of Auxio.
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
 
#include "JMetadataBuilder.h"

#include "util.h"

#include <taglib/mp4tag.h>
#include <taglib/textidentificationframe.h>
#include <taglib/attachedpictureframe.h>

#include <taglib/tpropertymap.h>

#include "JObjectRef.h"
#include "JClassRef.h"
#include "JStringRef.h"
#include "JByteArrayRef.h"

JMetadataBuilder::JMetadataBuilder(JNIEnv *env) : env(env), id3v2(env), xiph(
        env), mp4(env), cover(), properties(nullptr) {
}

void JMetadataBuilder::setMimeType(TagLib::String type) {
    mimeType = type;
}

void JMetadataBuilder::setId3v1(TagLib::ID3v1::Tag &tag) {
    id3v2.add_id("TIT2", tag.title());
    id3v2.add_id("TPE1", tag.artist());
    id3v2.add_id("TALB", tag.album());
    id3v2.add_id("TRCK", std::to_string(tag.track()));
    id3v2.add_id("TYER", std::to_string(tag.year()));
    const int genreNumber = tag.genreNumber();
    if (genreNumber != 255) {
        id3v2.add_id("TCON", std::to_string(genreNumber));
    }
}

void JMetadataBuilder::setId3v2(TagLib::ID3v2::Tag &tag) {
    // We want to ideally find the front cover, fall back to the first picture otherwise.
    std::optional<TagLib::ID3v2::AttachedPictureFrame*> firstPic;
    std::optional<TagLib::ID3v2::AttachedPictureFrame*> frontCoverPic;
    for (auto frame : tag.frameList()) {
        if (auto txxxFrame =
                dynamic_cast<TagLib::ID3v2::UserTextIdentificationFrame*>(frame)) {
            TagLib::String id = frame->frameID();
            TagLib::StringList frameText = txxxFrame->fieldList();
            if (frameText.isEmpty())
                continue;
            auto begin = frameText.begin();
            TagLib::String description = *begin;
            frameText.erase(begin);
            id3v2.add_combined(id, description, frameText);
        } else if (auto textFrame =
                dynamic_cast<TagLib::ID3v2::TextIdentificationFrame*>(frame)) {
            TagLib::String key = frame->frameID();
            TagLib::StringList frameText = textFrame->fieldList();
            id3v2.add_id(key, frameText);
        } else if (auto pictureFrame =
                dynamic_cast<TagLib::ID3v2::AttachedPictureFrame*>(frame)) {
            if (!firstPic) {
                firstPic = pictureFrame;
            }
            if (!frontCoverPic
                    && pictureFrame->type()
                            == TagLib::ID3v2::AttachedPictureFrame::FrontCover) {
                frontCoverPic = pictureFrame;
            }
        } else {
            continue;
        }
    }
    if (frontCoverPic) {
        auto pic = *frontCoverPic;
        cover = pic->picture();
    } else if (firstPic) {
        auto pic = *firstPic;
        cover = pic->picture();
    }
}

void JMetadataBuilder::setXiph(TagLib::Ogg::XiphComment &tag) {
    for (auto field : tag.fieldListMap()) {
        auto key = field.first.upper();
        auto values = field.second;
        xiph.add_custom(key, values);
    }
    auto pics = tag.pictureList();
    setFlacPictures(pics);
}

template<typename T>
void mp4AddImpl(JTagMap &map, TagLib::String &itemName, T itemValue) {
    if (itemName.startsWith("----")) {
        // Split this into it's atom name and description
        auto split = itemName.find(':');
        auto atomName = itemName.substr(0, split);
        auto atomDescription = itemName.substr(split + 1);
        map.add_combined(atomName, atomDescription, itemValue);
    } else {
        map.add_id(itemName, itemValue);
    }
}

void JMetadataBuilder::setMp4(TagLib::MP4::Tag &tag) {
    auto map = tag.itemMap();
    std::optional < TagLib::MP4::CoverArt > firstCover;
    for (auto item : map) {
        auto itemName = item.first;
        auto itemValue = item.second;
        if (itemName == "covr") {
            // Special cover case.
            // MP4 has no types, so just prioritize easier to decode covers (PNG, JPEG)
            auto pics = itemValue.toCoverArtList();
            for (auto &pic : pics) {
                auto format = pic.format();
                if (format == TagLib::MP4::CoverArt::PNG
                        || format == TagLib::MP4::CoverArt::JPEG) {
                    cover = pic.data();
                    continue;
                }
            }
            cover = pics.front().data();
            continue;
        }
        auto type = itemValue.type();
        std::string serializedValue;
        switch (type) {
        // Normal expected MP4 items
        case TagLib::MP4::Item::Type::StringList:
            mp4AddImpl(mp4, itemName, itemValue.toStringList());
            break;
            // Weird MP4 items I'm 90% sure I'll encounter.
        case TagLib::MP4::Item::Type::Int:
            serializedValue = std::to_string(itemValue.toInt());
            break;
        case TagLib::MP4::Item::Type::UInt:
            serializedValue = std::to_string(itemValue.toUInt());
            break;
        case TagLib::MP4::Item::Type::LongLong:
            serializedValue = std::to_string(itemValue.toLongLong());
            break;
        case TagLib::MP4::Item::Type::IntPair:
            // It's inefficient going from the integer representation back into
            // a string, but I fully expect taggers to just write "NN/TT" strings
            // anyway, and musikr doesn't have to do as much fiddly variant handling.
            serializedValue = std::to_string(itemValue.toIntPair().first) + "/"
                    + std::to_string(itemValue.toIntPair().second);
            break;
        default:
            // Don't care about the other types
            continue;
        }
        mp4AddImpl(mp4, itemName, TagLib::String(serializedValue));
    }
}

void JMetadataBuilder::setFlacPictures(
        TagLib::List<TagLib::FLAC::Picture*> &pics) {
    // Find the front cover image. If it doesn't exist, fall back to the first image.
    for (auto pic : pics) {
        if (pic->type() == TagLib::FLAC::Picture::FrontCover) {
            cover = pic->data();
            return;
        }
    }
    if (!pics.isEmpty()) {
        cover = pics.front()->data();
    }
}

void JMetadataBuilder::setProperties(TagLib::AudioProperties *properties) {
    this->properties = properties;
}

jobject JMetadataBuilder::build() {
    JClassRef jPropertiesClass { env, "org/oxycblt/musikr/metadata/Properties" };
    jmethodID jPropertiesInitMethod = jPropertiesClass.method("<init>",
            "(Ljava/lang/String;JII)V");
    JStringRef jMimeType { env, this->mimeType };

    JObjectRef jProperties { env, env->NewObject(*jPropertiesClass,
            jPropertiesInitMethod, *jMimeType,
            (jlong) properties->lengthInMilliseconds(), properties->bitrate(),
            properties->sampleRate()) };

    JClassRef jMetadataClass { env, "org/oxycblt/musikr/metadata/Metadata" };
    jmethodID jMetadataInitMethod = jMetadataClass.method("<init>",
            "(Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;[BLorg/"
                    "oxycblt/musikr/metadata/Properties;)V");
    auto jId3v2Map = id3v2.getObject();
    auto jXiphMap = xiph.getObject();
    auto jMp4Map = mp4.getObject();
    if (cover.has_value()) {
        JByteArrayRef jCoverArray { env, cover.value() };
        jobject result = env->NewObject(*jMetadataClass, jMetadataInitMethod,
                **jId3v2Map, **jXiphMap, **jMp4Map, *jCoverArray, *jProperties);
        return result;
    }
    return env->NewObject(*jMetadataClass, jMetadataInitMethod, **jId3v2Map,
            **jXiphMap, **jMp4Map, nullptr, *jProperties);
}
