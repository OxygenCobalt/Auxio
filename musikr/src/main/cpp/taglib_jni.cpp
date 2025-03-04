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
#include "JMetadataBuilder.h"
#include "util.h"

#include "taglib/fileref.h"
#include "taglib/flacfile.h"
#include "taglib/mp4file.h"
#include "taglib/mpegfile.h"
#include "taglib/opusfile.h"
#include "taglib/vorbisfile.h"
#include "taglib/wavfile.h"



bool parseMpeg(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *mpegFile = dynamic_cast<TagLib::MPEG::File*>(file);
    if (mpegFile == nullptr) {
        return false;
    }
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

bool parseMp4(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *mp4File = dynamic_cast<TagLib::MP4::File*>(file);
    if (mp4File == nullptr) {
        return false;
    }
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

bool parseFlac(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *flacFile = dynamic_cast<TagLib::FLAC::File*>(file);
    if (flacFile == nullptr) {
        return false;
    }
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
            LOGE("Unable to parse Xiph comment in %s: %s", name.c_str(), e.what());
        }
    }
    auto pics = flacFile->pictureList();
    jBuilder.setFlacPictures(pics);
    return true;
}

bool parseOpus(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *opusFile = dynamic_cast<TagLib::Ogg::Opus::File*>(file);
    if (opusFile == nullptr) {
        return false;
    }
    auto tag = opusFile->tag();
    if (tag != nullptr) {
        try {
            jBuilder.setXiph(*tag);
        } catch (std::exception &e) {
            LOGE("Unable to parse Xiph comment in %s: %s", name.c_str(), e.what());
        }
    }
    return true;
}

bool parseVorbis(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *vorbisFile = dynamic_cast<TagLib::Ogg::Vorbis::File*>(file);
    if (vorbisFile == nullptr) {
        return false;
    }
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

bool parseWav(const std::string &name, TagLib::File *file,
        JMetadataBuilder &jBuilder) {
    auto *wavFile = dynamic_cast<TagLib::RIFF::WAV::File*>(file);
    if (wavFile == nullptr) {
        return false;
    }
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

extern "C" JNIEXPORT jobject JNICALL
Java_org_oxycblt_musikr_metadata_TagLibJNI_openNative(JNIEnv *env,
        jobject /* this */,
        jobject inputStream) {
    std::string name = "unknown file";
    try {
        JInputStream jStream {env, inputStream};
        name = jStream.name();
        TagLib::FileRef fileRef {&jStream};
        if (fileRef.isNull()) {
            throw std::runtime_error("Invalid file");
        }
        TagLib::File *file = fileRef.file();
        JMetadataBuilder jBuilder {env};
        jBuilder.setProperties(file->audioProperties());

        // TODO: Make some type of composable logger so I don't
        //  have to shoehorn this into the native code.
        if (parseMpeg(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/mpeg");
        } else if (parseMp4(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/mp4");
        } else if (parseFlac(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/flac");
        } else if (parseOpus(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/opus");
        } else if (parseVorbis(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/vorbis");
        } else if (parseWav(name, file, jBuilder)) {
            jBuilder.setMimeType("audio/wav");
        } else {
            LOGE("File format in %s is not supported", name.c_str());
            return nullptr;
        }
        return jBuilder.build();
    } catch (std::exception &e) {
        LOGE("Unable to parse metadata in %s: %s", name.c_str(), e.what());
        return nullptr;
    }
}
