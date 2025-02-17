#pragma once

#include <memory>
#include <taglib/mp4tag.h>
#include <taglib/mp4item.h>
#include <taglib/tstring.h>
#include "rust/cxx.h"

namespace taglib_shim {
    class ItemMapEntry {
    public:
        ItemMapEntry(TagLib::String key, TagLib::MP4::Item value);
        const TagLib::String& key() const;
        const TagLib::MP4::Item& value() const;

    private:
        TagLib::String key_;
        TagLib::MP4::Item value_;
    };

    std::unique_ptr<std::vector<ItemMapEntry>> ItemMap_to_entries(const TagLib::MP4::ItemMap& map);

    class IntPair {
    public:
        IntPair(int first, int second);
        int first() const;
        int second() const;

    private:
        int first_;
        int second_;
    };

    class CoverArt {
    public:
        CoverArt(TagLib::MP4::CoverArt::Format format, const TagLib::ByteVector& data);
        uint32_t format() const;
        std::unique_ptr<TagLib::ByteVector> data() const;

    private:
        TagLib::MP4::CoverArt art_;
    };

    class CoverArtList {
    public:
        CoverArtList(const TagLib::MP4::CoverArtList& list);
        std::unique_ptr<std::vector<CoverArt>> to_vector() const;

    private:
        TagLib::MP4::CoverArtList list_;
    };

    unsigned int Item_type(const TagLib::MP4::Item& item);
    std::unique_ptr<IntPair> Item_toIntPair(const TagLib::MP4::Item& item);
    std::unique_ptr<TagLib::StringList> Item_toStringList(const TagLib::MP4::Item& item);
    std::unique_ptr<TagLib::ByteVectorList> Item_toByteVectorList(const TagLib::MP4::Item& item);
    std::unique_ptr<CoverArtList> Item_toCoverArtList(const TagLib::MP4::Item& item);
    int64_t Item_toLongLong(const TagLib::MP4::Item& item);
} 