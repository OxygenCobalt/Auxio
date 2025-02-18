#include "taglib/tpropertymap.h"
#include "taglib/xiphcomment.h"

namespace taglib_shim
{
    using FieldListMap = TagLib::SimplePropertyMap;

    struct FieldListEntry
    {
        FieldListEntry(TagLib::String key, TagLib::StringList value);
        std::unique_ptr<TagLib::String> key() const;
        std::unique_ptr<TagLib::StringList> value() const;

    private:
        TagLib::String key_;
        TagLib::StringList value_;
    };


    std::unique_ptr<std::vector<FieldListEntry>> FieldListMap_to_entries(const FieldListMap &map);
    std::unique_ptr<std::vector<TagLib::String>> StringList_to_vector(const TagLib::StringList &list);
}