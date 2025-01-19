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

extern "C" JNIEXPORT jobject JNICALL
Java_org_oxycblt_musikr_metadata_TagLibJNI_openNative(JNIEnv *env,
        jobject /* this */,
        jobject inputStream) {
    try {
        JInputStream jStream {env, inputStream};
        TagLib::FileRef fileRef {&jStream};
        if (fileRef.isNull()) {
            LOGE("Error opening file");
            return nullptr;
        }
        TagLib::File *file = fileRef.file();
        JMetadataBuilder jBuilder {env};

        if (auto *mpegFile = dynamic_cast<TagLib::MPEG::File *>(file)) {
            jBuilder.setMimeType("audio/mpeg");
            auto id3v1Tag = mpegFile->ID3v1Tag();
            if (id3v1Tag != nullptr) {
                jBuilder.setId3v1(*id3v1Tag);
            }
            auto id3v2Tag = mpegFile->ID3v2Tag();
            if (id3v2Tag != nullptr) {
                jBuilder.setId3v2(*id3v2Tag);
            }
        } else if (auto *mp4File = dynamic_cast<TagLib::MP4::File *>(file)) {
            jBuilder.setMimeType("audio/mp4");
            auto tag = mp4File->tag();
            if (tag != nullptr) {
                jBuilder.setMp4(*tag);
            }
        } else if (auto *flacFile = dynamic_cast<TagLib::FLAC::File *>(file)) {
            jBuilder.setMimeType("audio/flac");
            auto id3v1Tag = flacFile->ID3v1Tag();
            if (id3v1Tag != nullptr) {
                jBuilder.setId3v1(*id3v1Tag);
            }
            auto id3v2Tag = flacFile->ID3v2Tag();
            if (id3v2Tag != nullptr) {
                jBuilder.setId3v2(*id3v2Tag);
            }
            auto xiphComment = flacFile->xiphComment();
            if (xiphComment != nullptr) {
                jBuilder.setXiph(*xiphComment);
            }
            auto pics = flacFile->pictureList();
            jBuilder.setFlacPictures(pics);
        } else if (auto *opusFile = dynamic_cast<TagLib::Ogg::Opus::File *>(file)) {
            jBuilder.setMimeType("audio/opus");
            auto tag = opusFile->tag();
            if (tag != nullptr) {
                jBuilder.setXiph(*tag);
            }
        } else if (auto *vorbisFile =
                dynamic_cast<TagLib::Ogg::Vorbis::File *>(file)) {
            jBuilder.setMimeType("audio/vorbis");
            auto tag = vorbisFile->tag();
            if (tag != nullptr) {
                jBuilder.setXiph(*tag);
            }
        } else if (auto *wavFile = dynamic_cast<TagLib::RIFF::WAV::File *>(file)) {
            jBuilder.setMimeType("audio/wav");
            auto tag = wavFile->ID3v2Tag();
            if (tag != nullptr) {
                jBuilder.setId3v2(*tag);
            }
        } else {
            // While taglib supports other formats, ExoPlayer does not. Ignore them.
            LOGD("Unsupported file format");
            return nullptr;
        }

        jBuilder.setProperties(file->audioProperties());
        return jBuilder.build();
    } catch (std::runtime_error e) {
        LOGD("Error opening file: %s", e.what());
        return nullptr;
    }

}
