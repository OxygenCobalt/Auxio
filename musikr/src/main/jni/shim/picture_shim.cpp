#include "picture_shim.hpp"

namespace taglib_shim {
    std::unique_ptr<TagLib::String> Picture_mimeType(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::String>(picture.mimeType());
    }

    std::unique_ptr<TagLib::String> Picture_description(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::String>(picture.description());
    }

    std::unique_ptr<TagLib::ByteVector> Picture_data(const TagLib::FLAC::Picture& picture) {
        return std::make_unique<TagLib::ByteVector>(picture.data());
    }
} 