#include "mp4_shim.hpp"
#include <taglib/tstring.h>
#include <memory>
#include <vector>

namespace taglib_shim {
    ItemMapEntry::ItemMapEntry(TagLib::String key, TagLib::MP4::Item value)
        : key_(std::move(key)), value_(std::move(value)) {}

    std::unique_ptr<TagLib::String> ItemMapEntry::key() const {
        return std::make_unique<TagLib::String>(key_);
    }

    std::unique_ptr<TagLib::MP4::Item> ItemMapEntry::value() const {
        return std::make_unique<TagLib::MP4::Item>(value_);
    }

    std::unique_ptr<std::vector<ItemMapEntry>> ItemMap_to_entries(const TagLib::MP4::ItemMap& map) {
        auto entries = std::make_unique<std::vector<ItemMapEntry>>();
        
        for (auto it = map.begin(); it != map.end(); ++it) {
            entries->emplace_back(it->first, it->second);
        }
        
        return entries;
    }

    IntPair::IntPair(int first, int second)
        : first_(first), second_(second) {}

    int IntPair::first() const {
        return first_;
    }

    int IntPair::second() const {
        return second_;
    }

    CoverArt::CoverArt(TagLib::MP4::CoverArt::Format format, const TagLib::ByteVector& data)
        : art_(format, data) {}

    uint32_t CoverArt::format() const {
        return static_cast<uint32_t>(art_.format());
    }

    std::unique_ptr<TagLib::ByteVector> CoverArt::data() const {
        return std::make_unique<TagLib::ByteVector>(art_.data());
    }

    CoverArtList::CoverArtList(const TagLib::MP4::CoverArtList& list)
        : list_(list) {}

    std::unique_ptr<std::vector<CoverArt>> CoverArtList::to_vector() const {
        auto vec = std::make_unique<std::vector<CoverArt>>();
        for (const auto& item : list_) {
            vec->emplace_back(item.format(), item.data());
        }
        return vec;
    }

    unsigned int Item_type(const TagLib::MP4::Item& item) {
        return static_cast<unsigned int>(item.type());
    }

    std::unique_ptr<IntPair> Item_toIntPair(const TagLib::MP4::Item& item) {
        auto pair = item.toIntPair();
        return std::make_unique<IntPair>(pair.first, pair.second);
    }

    std::unique_ptr<TagLib::StringList> Item_toStringList(const TagLib::MP4::Item& item) {
        return std::make_unique<TagLib::StringList>(item.toStringList());
    }

    std::unique_ptr<TagLib::ByteVectorList> Item_toByteVectorList(const TagLib::MP4::Item& item) {
        return std::make_unique<TagLib::ByteVectorList>(item.toByteVectorList());
    }

    std::unique_ptr<CoverArtList> Item_toCoverArtList(const TagLib::MP4::Item& item) {
        return std::make_unique<CoverArtList>(item.toCoverArtList());
    }

    int64_t Item_toLongLong(const TagLib::MP4::Item& item) {
        return item.toLongLong();
    }
} 