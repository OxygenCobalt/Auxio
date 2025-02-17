#include "id3v1_shim.hpp"

namespace taglib_shim {
    std::unique_ptr<String> ID3v1Tag_title(const ID3v1::Tag& tag) {
        return std::make_unique<String>(tag.title());
    }

    std::unique_ptr<String> ID3v1Tag_artist(const ID3v1::Tag& tag) {
        return std::make_unique<String>(tag.artist());
    }

    std::unique_ptr<String> ID3v1Tag_album(const ID3v1::Tag& tag) {
        return std::make_unique<String>(tag.album());
    }

    std::unique_ptr<String> ID3v1Tag_comment(const ID3v1::Tag& tag) {
        return std::make_unique<String>(tag.comment());
    }

    uint ID3v1Tag_genreIndex(const ID3v1::Tag& tag) {
        return tag.genreIndex();
    }

    uint ID3v1Tag_year(const ID3v1::Tag& tag) {
        return tag.year();
    }

    uint ID3v1Tag_track(const ID3v1::Tag& tag) {
        return tag.track();
    }
} 