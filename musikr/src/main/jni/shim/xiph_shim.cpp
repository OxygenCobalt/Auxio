#include "xiph_shim.hpp"

namespace taglib_shim
{
    FieldListEntry::FieldListEntry(TagLib::String key, TagLib::StringList value) : key_(key), value_(value) {}

    const TagLib::String &FieldListEntry::key() const
    {
        return key_;
    }

    const TagLib::StringList &FieldListEntry::value() const
    {
        return value_;
    }
    
    std::unique_ptr<std::vector<FieldListEntry>> FieldListMap_to_entries(const TagLib::SimplePropertyMap &map)
    {
        std::unique_ptr<std::vector<FieldListEntry>> result = std::make_unique<std::vector<FieldListEntry>>();
        for (const auto &pair : map)
        {
            result->push_back(FieldListEntry(pair.first, pair.second));
        }
        return result;
    }
}
