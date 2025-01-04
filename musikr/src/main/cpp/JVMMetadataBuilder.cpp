/*
 * Copyright (c) 2024 Auxio Project
 * JVMMetadataBuilder.cpp is part of Auxio.
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
 
#include "JVMMetadataBuilder.h"

#include "util.h"

#include <taglib/mp4tag.h>
#include <taglib/textidentificationframe.h>

#include <taglib/tpropertymap.h>

JVMMetadataBuilder::JVMMetadataBuilder(JNIEnv *env) : env(env), id3v2(env), xiph(
        env), mp4(env), cover(), properties(nullptr) {
}

void JVMMetadataBuilder::setMimeType(const std::string_view type) {
    this->mimeType = type;
}

void JVMMetadataBuilder::setId3v2(const TagLib::ID3v2::Tag &tag) {
    for (auto frame : tag.frameList()) {
        if (auto txxxFrame =
                dynamic_cast<TagLib::ID3v2::UserTextIdentificationFrame*>(frame)) {
            TagLib::String id = frame->frameID();
            TagLib::StringList frameText = txxxFrame->fieldList();
            // Frame text starts with the description then the remaining values
            auto begin = frameText.begin();
            TagLib::String description = *begin;
            frameText.erase(begin);
            id3v2.add_combined(id, description, frameText);
        } else if (auto textFrame =
                dynamic_cast<TagLib::ID3v2::TextIdentificationFrame*>(frame)) {
            TagLib::String key = frame->frameID();
            TagLib::StringList frameText = textFrame->fieldList();
            id3v2.add_id(key, frameText);
        } else {
            continue;
        }
    }
}

void JVMMetadataBuilder::setXiph(const TagLib::Ogg::XiphComment &tag) {
    for (auto field : tag.fieldListMap()) {
        auto key = field.first.upper();
        auto values = field.second;
        xiph.add_custom(key, values);
    }
}

template<typename T>
void mp4AddImpl(JVMTagMap &map, TagLib::String &itemName, T itemValue) {
    if (itemName.startsWith("----")) {
        // Split this into it's atom name and description
        auto split = itemName.split(":");
        if (split.size() != 2) {
            throw std::runtime_error("Invalid atom name");
        }
        auto atomName = split[0];
        auto atomDescription = split[1];
        map.add_combined(atomName, atomDescription, itemValue);
    } else {
        map.add_id(itemName, itemValue);
    }
}

void JVMMetadataBuilder::setMp4(const TagLib::MP4::Tag &tag) {
    auto map = tag.itemMap();
    for (auto item : map) {
        auto itemName = item.first;
        auto itemValue = item.second;
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

void JVMMetadataBuilder::setCover(
        const TagLib::List<TagLib::VariantMap> covers) {
    if (covers.isEmpty()) {
        return;
    }
    // Find the cover with a "front cover" type
    for (auto cover : covers) {
        auto type = cover["pictureType"].toString();
        if (type == "Front Cover") {
            this->cover = cover["data"].toByteVector();
            return;
        }
    }
    // No front cover, just pick first.
    // TODO: Consider having cascading fallbacks to increasingly less
    //  relevant covers perhaps
    this->cover = covers.front()["data"].toByteVector();
}

void JVMMetadataBuilder::setProperties(TagLib::AudioProperties *properties) {
    this->properties = properties;
}

jobject JVMMetadataBuilder::build() {
    jclass propertiesClass = env->FindClass(
            "org/oxycblt/musikr/metadata/Properties");
    jmethodID propertiesInit = env->GetMethodID(propertiesClass, "<init>",
            "(Ljava/lang/String;JII)V");
    jobject propertiesObj = env->NewObject(propertiesClass, propertiesInit,
            env->NewStringUTF(mimeType.data()),
            (jlong) properties->lengthInMilliseconds(), properties->bitrate(),
            properties->sampleRate());
    env->DeleteLocalRef(propertiesClass);

    jclass metadataClass = env->FindClass(
            "org/oxycblt/musikr/metadata/Metadata");
    jmethodID metadataInit = env->GetMethodID(metadataClass, "<init>",
            "(Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;[BLorg/"
                    "oxycblt/musikr/metadata/Properties;)V");
    jobject id3v2Map = id3v2.getObject();
    jobject xiphMap = xiph.getObject();
    jobject mp4Map = mp4.getObject();
    jbyteArray coverArray = nullptr;
    if (cover.has_value()) {
        auto coverSize = static_cast<jsize>(cover->size());
        coverArray = env->NewByteArray(coverSize);
        env->SetByteArrayRegion(coverArray, 0, coverSize,
                reinterpret_cast<const jbyte*>(cover->data()));
    }
    jobject metadataObj = env->NewObject(metadataClass, metadataInit, id3v2Map,
            xiphMap, mp4Map, coverArray, propertiesObj);
    env->DeleteLocalRef(metadataClass);
    return metadataObj;
}
