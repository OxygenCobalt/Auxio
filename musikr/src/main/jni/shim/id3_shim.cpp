#include "id3_shim.hpp"

namespace taglib_shim {
    std::unique_ptr<TagLib::ID3v2::FrameList> Tag_frameList(const TagLib::ID3v2::Tag& tag) {
        return std::make_unique<TagLib::ID3v2::FrameList>(tag.frameList());
    }

    std::unique_ptr<std::vector<FramePointer>> FrameList_to_vector(const TagLib::ID3v2::FrameList& list) {
        auto frames = std::make_unique<std::vector<FramePointer>>();
        for (const auto& frame : list) {
            frames->push_back(FramePointer{frame});
        }
        return frames;
    }

    const TagLib::ID3v2::TextIdentificationFrame* Frame_asTextIdentification(const TagLib::ID3v2::Frame* frame) {
        return dynamic_cast<const TagLib::ID3v2::TextIdentificationFrame*>(frame);
    }

    const TagLib::ID3v2::UserTextIdentificationFrame* Frame_asUserTextIdentification(const TagLib::ID3v2::Frame* frame) {
        return dynamic_cast<const TagLib::ID3v2::UserTextIdentificationFrame*>(frame);
    }

    const TagLib::ID3v2::AttachedPictureFrame* Frame_asAttachedPicture(const TagLib::ID3v2::Frame* frame) {
        return dynamic_cast<const TagLib::ID3v2::AttachedPictureFrame*>(frame);
    }

    std::unique_ptr<TagLib::ByteVector> AttachedPictureFrame_picture(const TagLib::ID3v2::AttachedPictureFrame& frame) {
        return std::make_unique<TagLib::ByteVector>(frame.picture());
    }

    std::unique_ptr<TagLib::StringList> TextIdentificationFrame_fieldList(const TagLib::ID3v2::TextIdentificationFrame& frame) {
        return std::make_unique<TagLib::StringList>(frame.fieldList());
    }

    std::unique_ptr<TagLib::StringList> UserTextIdentificationFrame_fieldList(const TagLib::ID3v2::UserTextIdentificationFrame& frame) {
        return std::make_unique<TagLib::StringList>(frame.fieldList());
    }

} 