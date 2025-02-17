#pragma once

#include <memory>
#include <taglib/id3v1tag.h>
#include <taglib/tstring.h>

namespace taglib_shim {
    std::unique_ptr<TagLib::String> ID3v1Tag_title(const TagLib::ID3v1::Tag& tag);
    std::unique_ptr<TagLib::String> ID3v1Tag_artist(const TagLib::ID3v1::Tag& tag);
    std::unique_ptr<TagLib::String> ID3v1Tag_album(const TagLib::ID3v1::Tag& tag);
    std::unique_ptr<TagLib::String> ID3v1Tag_comment(const TagLib::ID3v1::Tag& tag);
    uint ID3v1Tag_genreIndex(const TagLib::ID3v1::Tag& tag);
    uint ID3v1Tag_year(const TagLib::ID3v1::Tag& tag);
    uint ID3v1Tag_track(const TagLib::ID3v1::Tag& tag);
} 