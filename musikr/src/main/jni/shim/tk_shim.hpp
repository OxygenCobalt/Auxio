#pragma once

#include "taglib/tpropertymap.h"
#include "taglib/xiphcomment.h"
#include "taglib/tstring.h"
#include "taglib/tstringlist.h"
#include "taglib/flacpicture.h"
#include "taglib/flacfile.h"
#include "taglib/tbytevector.h"
#include <memory>
#include <iterator>
#include <vector>
#include <string>
#include "rust/cxx.h"

namespace taglib_shim
{

    struct Property
    {
        Property(TagLib::String key, TagLib::StringList value);
        const TagLib::String &key() const;
        const TagLib::StringList &value() const;

    private:
        TagLib::String key_;
        TagLib::StringList value_;
    };


    std::unique_ptr<std::vector<Property>> SimplePropertyMap_to_vector(const TagLib::SimplePropertyMap &map);
    std::unique_ptr<std::vector<TagLib::String>> StringList_to_vector(const TagLib::StringList &list);
}