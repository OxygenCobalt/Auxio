#include "tk_shim.hpp"

namespace taglib_shim
{
    Property::Property(TagLib::String key, TagLib::StringList value) : key_(key), value_(value) {}

    const TagLib::String &Property::key() const
    {
        return key_;
    }

    const TagLib::StringList &Property::value() const
    {
        return value_;
    }
    std::unique_ptr<std::vector<Property>> SimplePropertyMap_to_vector(const TagLib::SimplePropertyMap &map)
    {
        std::unique_ptr<std::vector<Property>> result = std::make_unique<std::vector<Property>>();
        for (const auto &pair : map)
        {
            result->push_back(Property(pair.first, pair.second));
        }
        return result;
    }

    std::unique_ptr<std::vector<TagLib::String>> StringList_to_vector(const TagLib::StringList &list)
    {
        std::unique_ptr<std::vector<TagLib::String>> result = std::make_unique<std::vector<TagLib::String>>();
        for (const auto &str : list)
        {
            result->push_back(str);
        }
        return result;
    }

}
