#include "id3v1_shim.hpp"

namespace taglib_shim {
    std::unique_ptr<TagLib::String> ID3v1Tag_title(const TagLib::ID3v1::Tag& tag) {
        return std::make_unique<TagLib::String>(tag.title());
    }

    std::unique_ptr<TagLib::String> ID3v1Tag_artist(const TagLib::ID3v1::Tag& tag) {
        return std::make_unique<TagLib::String>(tag.artist());
    }

    std::unique_ptr<TagLib::String> ID3v1Tag_album(const TagLib::ID3v1::Tag& tag) {
        return std::make_unique<TagLib::String>(tag.album());
    }

    std::unique_ptr<TagLib::String> ID3v1Tag_comment(const TagLib::ID3v1::Tag& tag) {
        return std::make_unique<TagLib::String>(tag.comment());
    }

    uint ID3v1Tag_genreIndex(const TagLib::ID3v1::Tag& tag) {
        return tag.genreNumber();
    }

    uint ID3v1Tag_year(const TagLib::ID3v1::Tag& tag) {
        return tag.year();
    }

    uint ID3v1Tag_track(const TagLib::ID3v1::Tag& tag) {
        return tag.track();
    }
} 