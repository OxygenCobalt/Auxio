#pragma once

#include "taglib/flacpicture.h"
#include "taglib/tstring.h"
#include "taglib/tbytevector.h"
#include "taglib/tpicturetype.h"
#include "tk_shim.hpp"
#include <memory>
#include <vector>

namespace taglib_shim {
    using PictureList = TagLib::List<TagLib::FLAC::Picture *>;

    class PicturePointer {
    public:
        PicturePointer(const TagLib::FLAC::Picture* picture) : picture(picture) {}
        const TagLib::FLAC::Picture* get() const { return picture; }
    private:
        const TagLib::FLAC::Picture* picture;
    };
    std::unique_ptr<PictureList> FLACFile_pictureList(TagLib::FLAC::File& file);
    std::unique_ptr<PictureList> XiphComment_pictureList(TagLib::Ogg::XiphComment& comment);

    std::unique_ptr<std::vector<PicturePointer>> PictureList_to_vector(const PictureList& list);

    uint32_t Picture_type(const TagLib::FLAC::Picture& picture);
    std::unique_ptr<TagLib::ByteVector> Picture_data(const TagLib::FLAC::Picture& picture);
} 