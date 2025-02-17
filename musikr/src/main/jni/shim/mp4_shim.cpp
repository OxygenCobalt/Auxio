#include "mp4_shim.hpp"
#include <taglib/tstring.h>
#include <memory>
#include <vector>

namespace taglib_shim {
    ItemMapEntry::ItemMapEntry(TagLib::String key, TagLib::MP4::Item value)
        : key_(std::move(key)), value_(std::move(value)) {}

    const TagLib::String& ItemMapEntry::key() const {
        return key_;
    }

    const TagLib::MP4::Item& ItemMapEntry::value() const {
        return value_;
    }

    std::unique_ptr<std::vector<ItemMapEntry>> ItemMap_to_entries(const TagLib::MP4::ItemMap& map) {
        auto entries = std::make_unique<std::vector<ItemMapEntry>>();
        
        for (auto it = map.begin(); it != map.end(); ++it) {
            entries->emplace_back(it->first, it->second);
        }
        
        return entries;
    }
} 