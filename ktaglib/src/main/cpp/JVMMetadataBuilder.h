//
// Created by oxycblt on 12/12/24.
//

#ifndef AUXIO_JVMMETADATABUILDER_H
#define AUXIO_JVMMETADATABUILDER_H

#include <jni.h>
#include <string_view>
#include <optional>

#include "taglib/id3v2tag.h"
#include "taglib/xiphcomment.h"
#include "taglib/mp4tag.h"
#include "taglib/audioproperties.h"

#include "JVMTagMap.h"

class JVMMetadataBuilder {
public:
    JVMMetadataBuilder(JNIEnv *env);

    void setMimeType(const std::string_view mimeType);
    void setId3v2(const TagLib::ID3v2::Tag &tag);
    void setXiph(const TagLib::Ogg::XiphComment &tag);
    void setMp4(const TagLib::MP4::Tag &tag);
    void setCover(const TagLib::List<TagLib::VariantMap> covers);
    void setProperties(TagLib::AudioProperties *properties);

    jobject build();

private:
    JNIEnv *env;

    std::string_view mimeType;

    std::optional<TagLib::ByteVector> cover;
    TagLib::AudioProperties *properties;

    JVMTagMap id3v2;
    JVMTagMap xiph;
    JVMTagMap mp4;
};

#endif //AUXIO_JVMMETADATABUILDER_H
