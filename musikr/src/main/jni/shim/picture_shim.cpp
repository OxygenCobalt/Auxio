#include "picture_shim.hpp"
#include "taglib/flacfile.h"

namespace taglib_shim {
    std::unique_ptr<PictureList> XiphComment_pictureList(TagLib::Ogg::XiphComment& comment) {
        return std::make_unique<PictureList>(comment.pictureList());
    }

    std::unique_ptr<PictureList> FLACFile_pictureList(TagLib::FLAC::File& file) {
        return std::make_unique<PictureList>(file.pictureList());
    }

    std::unique_ptr<std::vector<PicturePointer>> PictureList_to_vector(const PictureList& list) {
        auto result = std::make_unique<std::vector<PicturePointer>>();
        for (const auto* picture : list) {
            result->emplace_back(picture);
        }
        return result;
    }

    std::unique_ptr<TagLib::String> Picture_mimeType(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::String>(picture.mimeType());
    }

    std::unique_ptr<TagLib::String> Picture_description(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::String>(picture.description());
    }

    std::unique_ptr<TagLib::ByteVector> Picture_data(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::ByteVector>(picture.data());
    }

    uint32_t Picture_type(const TagLib::FLAC::Picture& picture) {
        return static_cast<uint32_t>(picture.type());
    }
} 