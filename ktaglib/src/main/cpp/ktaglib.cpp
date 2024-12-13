#include <jni.h>
#include <string>

#include "JVMInputStream.h"
#include "JVMMetadataBuilder.h"

#include "taglib/fileref.h"
#include "taglib/mpegfile.h"
#include "taglib/mp4file.h"
#include "taglib/flacfile.h"
#include "taglib/opusfile.h"
#include "taglib/vorbisfile.h"
#include "taglib/wavfile.h"

extern "C" JNIEXPORT jobject JNICALL
Java_org_oxycblt_ktaglib_KTagLib_openNative(
        JNIEnv* env,
        jobject /* this */,
        jobject inputStream) {
    JVMInputStream stream { env, inputStream };
    TagLib::FileRef fileRef { &stream };
    if (fileRef.isNull()) {
        return nullptr;
    }
    TagLib::File *file = fileRef.file();
    JVMMetadataBuilder builder { env };

    if (auto *mpegFile = dynamic_cast<TagLib::MPEG::File *>(file)) {
        builder.setMimeType("audio/mpeg");
        builder.setId3v2(*mpegFile->ID3v2Tag());
    } else if (auto *mp4File = dynamic_cast<TagLib::MP4::File *>(file)) {
        builder.setMimeType("audio/mp4");
        builder.setMp4(*mp4File->tag());
    } else if (auto *flacFile = dynamic_cast<TagLib::FLAC::File *>(file)) {
        builder.setMimeType("audio/flac");
        builder.setId3v2(*flacFile->ID3v2Tag());
        builder.setXiph(*flacFile->xiphComment());
    } else if (auto *opusFile = dynamic_cast<TagLib::Ogg::Opus::File *>(file)) {
        builder.setMimeType("audio/opus");
        builder.setXiph(*opusFile->tag());
    } else if (auto *vorbisFile = dynamic_cast<TagLib::Ogg::Vorbis::File *>(file)) {
        builder.setMimeType("audio/vorbis");
        builder.setXiph(*vorbisFile->tag());
    } else if (auto *wavFile = dynamic_cast<TagLib::RIFF::WAV::File *>(file)) {
        builder.setMimeType("audio/wav");
        builder.setId3v2(*wavFile->ID3v2Tag());
    } else {
        // While taglib supports other formats, ExoPlayer does not. Ignore them.
        return nullptr;
    }

    return builder.build();
}
