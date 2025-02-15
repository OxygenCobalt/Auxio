#include "id3_shim.hpp"

namespace taglib_shim {
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

    TagLib::ID3v2::Tag* File_ID3v2Tag(TagLib::MPEG::File* file, bool create) {
        return file->ID3v2Tag(create);
    }

    std::unique_ptr<std::vector<WrappedFrame>> Tag_frameList(const TagLib::ID3v2::Tag& tag) {
        auto frames = std::make_unique<std::vector<WrappedFrame>>();
        for (const auto& frame : tag.frameList()) {
            frames->push_back(WrappedFrame{frame});
        }
        return frames;
    }
} 