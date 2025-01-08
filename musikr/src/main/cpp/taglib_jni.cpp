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
#include "JVMInputStream.h"
#include "JVMMetadataBuilder.h"
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
        JVMInputStream stream {env, inputStream};
        TagLib::FileRef fileRef {&stream};
        if (fileRef.isNull()) {
            LOGE("Error opening file");
            return nullptr;
        }
        TagLib::File *file = fileRef.file();
        JVMMetadataBuilder builder {env};

        if (auto *mpegFile = dynamic_cast<TagLib::MPEG::File *>(file)) {
            builder.setMimeType("audio/mpeg");
            auto tag = mpegFile->ID3v2Tag();
            if (tag != nullptr) {
                builder.setId3v2(*tag);
            }
        } else if (auto *mp4File = dynamic_cast<TagLib::MP4::File *>(file)) {
            builder.setMimeType("audio/mp4");
            auto tag = mp4File->tag();
            if (tag != nullptr) {
                builder.setMp4(*tag);
            }
        } else if (auto *flacFile = dynamic_cast<TagLib::FLAC::File *>(file)) {
            builder.setMimeType("audio/flac");
            auto id3v2Tag = flacFile->ID3v2Tag();
            if (id3v2Tag != nullptr) {
                builder.setId3v2(*id3v2Tag);
            }
            auto xiphComment = flacFile->xiphComment();
            if (xiphComment != nullptr) {
                builder.setXiph(*xiphComment);
            }
        } else if (auto *opusFile = dynamic_cast<TagLib::Ogg::Opus::File *>(file)) {
            builder.setMimeType("audio/opus");
            auto tag = opusFile->tag();
            if (tag != nullptr) {
                builder.setXiph(*tag);
            }
        } else if (auto *vorbisFile =
                dynamic_cast<TagLib::Ogg::Vorbis::File *>(file)) {
            builder.setMimeType("audio/vorbis");
            auto tag = vorbisFile->tag();
            if (tag != nullptr) {
                builder.setXiph(*tag);
            }
        } else if (auto *wavFile = dynamic_cast<TagLib::RIFF::WAV::File *>(file)) {
            builder.setMimeType("audio/wav");
            auto tag = wavFile->ID3v2Tag();
            if (tag != nullptr) {
                builder.setId3v2(*tag);
            }
        } else {
            // While taglib supports other formats, ExoPlayer does not. Ignore them.
            LOGE("Unsupported file format");
            return nullptr;
        }

        builder.setProperties(file->audioProperties());
        builder.setCover(file->tag()->complexProperties("PICTURE"));
        return builder.build();
    } catch (std::runtime_error e) {
        LOGE("Error opening file: %s", e.what());
        return nullptr;
    }

}
