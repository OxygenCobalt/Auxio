#pragma once

#include "rust/cxx.h"
#include "taglib/id3v2tag.h"
#include "taglib/id3v2frame.h"
#include "taglib/textidentificationframe.h"
#include "taglib/unsynchronizedlyricsframe.h"
#include "taglib/attachedpictureframe.h"
#include "taglib/tbytevector.h"
#include "taglib/mpegfile.h"

namespace taglib_shim {
    struct FramePointer {
        const TagLib::ID3v2::Frame* inner;
        const TagLib::ID3v2::Frame* get() const { return inner; }
    };

    std::unique_ptr<TagLib::ID3v2::FrameList> Tag_frameList(const TagLib::ID3v2::Tag& tag);

    std::unique_ptr<std::vector<FramePointer>> FrameList_to_vector(const TagLib::ID3v2::FrameList& list);

    // Frame type checking and casting
    std::unique_ptr<TagLib::ByteVector> Frame_id(const TagLib::ID3v2::Frame &frame);
    const TagLib::ID3v2::TextIdentificationFrame* Frame_asTextIdentification(const TagLib::ID3v2::Frame* frame);
    const TagLib::ID3v2::UserTextIdentificationFrame* Frame_asUserTextIdentification(const TagLib::ID3v2::Frame* frame);
    const TagLib::ID3v2::AttachedPictureFrame* Frame_asAttachedPicture(const TagLib::ID3v2::Frame* frame);

    // Frame data access
    std::unique_ptr<TagLib::ByteVector> AttachedPictureFrame_picture(const TagLib::ID3v2::AttachedPictureFrame& frame);
    std::unique_ptr<TagLib::StringList> TextIdentificationFrame_fieldList(const TagLib::ID3v2::TextIdentificationFrame& frame);
    std::unique_ptr<TagLib::StringList> UserTextIdentificationFrame_fieldList(const TagLib::ID3v2::UserTextIdentificationFrame& frame);

    // ID3v2 tag access
    TagLib::ID3v2::Tag* File_ID3v2Tag(TagLib::MPEG::File* file, bool create);
} 