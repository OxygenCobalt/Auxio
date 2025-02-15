#pragma once

#include "taglib/flacpicture.h"
#include "taglib/tstring.h"
#include "taglib/tbytevector.h"
#include <memory>

namespace taglib_shim {
    std::unique_ptr<TagLib::String> Picture_mimeType(const TagLib::FLAC::Picture& picture);
    std::unique_ptr<TagLib::String> Picture_description(const TagLib::FLAC::Picture& picture);
    std::unique_ptr<TagLib::ByteVector> Picture_data(const TagLib::FLAC::Picture& picture);
} 