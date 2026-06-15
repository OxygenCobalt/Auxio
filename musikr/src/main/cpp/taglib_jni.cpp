/*
 * Copyright (c) 2024 Auxio Project
 * taglib_jni.cpp is part of Auxio.
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
 
#include <jni.h>
#include <string>
#include "JInputStream.h"
#include "JClassRef.h"
#include "JMetadataBuilder.h"
#include "JObjectRef.h"
#include "util.h"

#include "taglib/fileref.h"
#include "taglib/flacfile.h"
#include "taglib/mp4file.h"
#include "taglib/mp4properties.h"
#include "taglib/mpegfile.h"
#include "taglib/opusfile.h"
#include "taglib/vorbisfile.h"
#include "taglib/wavfile.h"

bool parseMpeg(const std::string &name, TagLib::MPEG::File *mpegFile,
        JMetadataBuilder &jBuilder) {
    auto id3v1Tag = mpegFile->ID3v1Tag();
    if (id3v1Tag != nullptr) {
        try {
            jBuilder.setId3v1(*id3v1Tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse ID3v1 tag in %s: %s", name.c_str(), e.what());
        }
    }
    auto id3v2Tag = mpegFile->ID3v2Tag();
    if (id3v2Tag != nullptr) {
        try {
            jBuilder.setId3v2(*id3v2Tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse ID3v2 tag in %s: %s", name.c_str(), e.what());
        }
    }
    return true;
}

bool parseMp4(const std::string &name, TagLib::MP4::File *mp4File,
        JMetadataBuilder &jBuilder) {
    auto tag = mp4File->tag();
    if (tag != nullptr) {
        try {
            jBuilder.setMp4(*tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse MP4 tag in %s: %s", name.c_str(), e.what());
        }
    }
    return true;
}

bool parseFlac(const std::string &name, TagLib::FLAC::File *flacFile,
        JMetadataBuilder &jBuilder) {
    auto id3v1Tag = flacFile->ID3v1Tag();
    if (id3v1Tag != nullptr) {
        try {
            jBuilder.setId3v1(*id3v1Tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse ID3v1 tag in %s: %s", name.c_str(), e.what());
        }
    }
    auto id3v2Tag = flacFile->ID3v2Tag();
    if (id3v2Tag != nullptr) {
        try {
            jBuilder.setId3v2(*id3v2Tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse ID3v2 tag in %s: %s", name.c_str(), e.what());
        }
    }
    auto xiphComment = flacFile->xiphComment();
    if (xiphComment != nullptr) {
        try {
            jBuilder.setXiph(*xiphComment);
        } catch (std::exception &e) {
            LOGE("Unable to parse Xiph comment in %s: %s", name.c_str(),
                    e.what());
        }
    }
    auto pics = flacFile->pictureList();
    jBuilder.setFlacPictures(pics);
    return true;
}

bool parseOpus(const std::string &name, TagLib::Ogg::Opus::File *opusFile,
        JMetadataBuilder &jBuilder) {
    auto tag = opusFile->tag();
    if (tag != nullptr) {
        try {
            jBuilder.setXiph(*tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse Xiph comment in %s: %s", name.c_str(),
                    e.what());
        }
    }
    return true;
}

bool parseVorbis(const std::string &name, TagLib::Ogg::Vorbis::File *vorbisFile,
        JMetadataBuilder &jBuilder) {
    auto tag = vorbisFile->tag();
    if (tag != nullptr) {
        try {
            jBuilder.setXiph(*tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse Xiph comment %s: %s", name.c_str(), e.what());
        }
    }
    return true;
}

bool parseWav(const std::string &name, TagLib::RIFF::WAV::File *wavFile,
        JMetadataBuilder &jBuilder) {
    auto tag = wavFile->ID3v2Tag();
    if (tag != nullptr) {
        try {
            jBuilder.setId3v2(*tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse ID3v2 tag in %s: %s", name.c_str(), e.what());
        }
    }
    return true;
}

// A minimal implementation of 'detectByContent()' from TagLib,
// limited to the available 'parse..()' functions available here.
// This fixes mostly all parsing problems for formats undetected by
// simple extension based detection, like AAC-in-MP4, but still
// doesn't work for Matroska, as it is not yet supported by TagLib
// see https://github.com/taglib/taglib/pull/1149
// Detects the file type based on the actual content of the stream.
TagLib::File* createFileFromContent(TagLib::IOStream *stream,
        bool readAudioProperties,
        TagLib::AudioProperties::ReadStyle audioPropertiesStyle) {
    TagLib::File *file = nullptr;

    if (TagLib::MPEG::File::isSupported(stream))
        file = new TagLib::MPEG::File(stream, readAudioProperties,
                audioPropertiesStyle);
    else if (TagLib::Ogg::Vorbis::File::isSupported(stream))
        file = new TagLib::Ogg::Vorbis::File(stream, readAudioProperties,
                audioPropertiesStyle);
    else if (TagLib::FLAC::File::isSupported(stream))
        file = new TagLib::FLAC::File(stream, readAudioProperties,
                audioPropertiesStyle);
    else if (TagLib::Ogg::Opus::File::isSupported(stream))
        file = new TagLib::Ogg::Opus::File(stream, readAudioProperties,
                audioPropertiesStyle);
    else if (TagLib::MP4::File::isSupported(stream))
        file = new TagLib::MP4::File(stream, readAudioProperties,
                audioPropertiesStyle);
    else if (TagLib::RIFF::WAV::File::isSupported(stream))
        file = new TagLib::RIFF::WAV::File(stream, readAudioProperties,
                audioPropertiesStyle);

    // double-check that the potentially created file actually valid.
    if (file) {
        if (file->isValid())
            return file;
        delete file;
    }

    return nullptr;
}

// This dispatcher replaces the previous, order-dependent if-else chain. It uses
// dynamic_cast to identify the file's true type, ensuring the correct parser is
// always called. This also allows the 'parse..()' functions to be simplified by
// accepting a specific file type (e.g., 'MPEG::File*'), removing the need for
// redundant internal checks.
// Accurate container MIME type support is not added, as it did not seem
// necessary, we can rely on SAF and MediaStore implementation for now,
// that works on extension based detection, so we expect users to use
// correct file extensions.
bool dispatchAndParse(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    if (auto *mpegFile = dynamic_cast<TagLib::MPEG::File*>(file)) {
        jBuilder.setMimeType("audio/mpeg");
        return parseMpeg(name, mpegFile, jBuilder);
    }
    if (auto *flacFile = dynamic_cast<TagLib::FLAC::File*>(file)) {
        jBuilder.setMimeType("audio/flac");
        return parseFlac(name, flacFile, jBuilder);
    }
    if (auto *opusFile = dynamic_cast<TagLib::Ogg::Opus::File*>(file)) {
        jBuilder.setMimeType("audio/opus");
        return parseOpus(name, opusFile, jBuilder);
    }
    if (auto *vorbisFile = dynamic_cast<TagLib::Ogg::Vorbis::File*>(file)) {
        jBuilder.setMimeType("audio/vorbis");
        return parseVorbis(name, vorbisFile, jBuilder);
    }
    if (auto *wavFile = dynamic_cast<TagLib::RIFF::WAV::File*>(file)) {
        jBuilder.setMimeType("audio/wav");
        return parseWav(name, wavFile, jBuilder);
    }
    if (auto *mp4File = dynamic_cast<TagLib::MP4::File*>(file)) {
        // Why are we setting this as default?
        // 'audio/mp4' is not even a codec MIME type.
        jBuilder.setMimeType("audio/mp4");
        if (auto *props =
                dynamic_cast<TagLib::MP4::Properties*>(mp4File->audioProperties())) {
            using Codec = TagLib::MP4::Properties::Codec;
            switch (props->codec()) {
            case Codec::AAC:
                jBuilder.setMimeType("audio/aac");
                break;
            case Codec::ALAC:
                jBuilder.setMimeType("audio/alac");
                break;
            default:
                break;
            }
        }
        return parseMp4(name, mp4File, jBuilder);
    }

    return false;
}

static jobject metadataResultSuccess(JNIEnv *env, jobject metadata) {
    JClassRef jSuccessClass { env,
            "org/oxycblt/musikr/metadata/MetadataResult$Success" };
    jmethodID jInitMethod = jSuccessClass.method("<init>",
            "(Lorg/oxycblt/musikr/metadata/Metadata;)V");
    return env->NewObject(*jSuccessClass, jInitMethod, metadata);
}

static jobject metadataResultObject(JNIEnv *env, const char *classpath) {
    JClassRef jObjectClass { env, classpath };
    std::string signature = std::string("L") + classpath + ";";
    jfieldID jInstanceField = env->GetStaticFieldID(*jObjectClass, "INSTANCE",
            signature.c_str());
    return env->GetStaticObjectField(*jObjectClass, jInstanceField);
}

static jobject metadataResultNoMetadata(JNIEnv *env) {
    return metadataResultObject(env,
            "org/oxycblt/musikr/metadata/MetadataResult$NoMetadata");
}

static jobject metadataResultNotAudio(JNIEnv *env) {
    return metadataResultObject(env,
            "org/oxycblt/musikr/metadata/MetadataResult$NotAudio");
}

static jobject metadataResultProviderFailed(JNIEnv *env) {
    return metadataResultObject(env,
            "org/oxycblt/musikr/metadata/MetadataResult$ProviderFailed");
}

extern "C" JNIEXPORT jobject JNICALL
Java_org_oxycblt_musikr_metadata_TagLibJNI_openNative(JNIEnv *env,
        jobject /* this */,
        jobject inputStream) {
    std::string name = "unknown file";
    TagLib::File *overriddenFile = nullptr;
    try {
        JInputStream jStream {env, inputStream};
        name = jStream.name();
        TagLib::FileRef fileRef {&jStream, true, TagLib::AudioProperties::Average};
        TagLib::File *fileToUse = fileRef.file();
        bool needsOverride = (fileToUse != nullptr &&
                fileToUse->audioProperties() != nullptr &&
                fileToUse->audioProperties()->lengthInSeconds() == 0);
        if (needsOverride) {
            LOGD("FileRef result for %s is suspicious (duration=0). Forcing content scan.", name.c_str());
            jStream.seek(0, TagLib::IOStream::Beginning);
            overriddenFile = createFileFromContent(&jStream, true, TagLib::AudioProperties::Average);
            if (overriddenFile != nullptr &&
                    overriddenFile->audioProperties() != nullptr &&
                    overriddenFile->audioProperties()->lengthInSeconds() > 0)
            {
                LOGD("Content scan successful. Overriding FileRef result for %s.", name.c_str());
                fileToUse = overriddenFile;
            } else {
                delete overriddenFile;
                overriddenFile = nullptr;
            }
        }
        if (fileToUse == nullptr) {
            delete overriddenFile;
            return metadataResultNotAudio(env);
        }
        if (fileToUse->audioProperties() == nullptr) {
            LOGE("No audio properties for %s", name.c_str());
            delete overriddenFile;
            return metadataResultNoMetadata(env);
        }
        JMetadataBuilder jBuilder {env};
        jBuilder.setProperties(fileToUse->audioProperties());

        if (!dispatchAndParse(name, fileToUse, jBuilder)) {
            LOGE("File format in %s is not supported by any parser.", name.c_str());
            delete overriddenFile;
            return metadataResultNotAudio(env);
        }
        JObjectRef jMetadata {env, jBuilder.build()};
        delete overriddenFile;
        return metadataResultSuccess(env, *jMetadata);
    } catch (std::exception &e) {
        LOGE("Unable to parse metadata in %s: %s", name.c_str(), e.what());
        delete overriddenFile;
        return metadataResultProviderFailed(env);
    }
}
