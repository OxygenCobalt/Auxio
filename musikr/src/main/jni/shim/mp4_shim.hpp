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
} 