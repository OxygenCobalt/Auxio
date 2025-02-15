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

    struct PictureRef {
        PictureRef(const TagLib::FLAC::Picture* picture) : picture_(picture) {}
        const TagLib::FLAC::Picture* get() const { return picture_; }
    private:
        const TagLib::FLAC::Picture* picture_;
    };

    std::unique_ptr<std::vector<Property>> SimplePropertyMap_to_vector(const TagLib::SimplePropertyMap &map);
    std::unique_ptr<std::vector<TagLib::String>> StringList_to_vector(const TagLib::StringList &list);
    std::unique_ptr<std::vector<PictureRef>> FLACFile_pictureList_to_vector(TagLib::FLAC::File &file);
    std::unique_ptr<std::vector<PictureRef>> XiphComment_pictureList_to_vector(TagLib::Ogg::XiphComment &comment);
    rust::String String_to_string(const TagLib::String &str);
    std::unique_ptr<std::vector<uint8_t>> ByteVector_to_bytes(const TagLib::ByteVector &data);
}