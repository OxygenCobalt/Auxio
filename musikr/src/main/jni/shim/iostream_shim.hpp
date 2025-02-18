#pragma once

#include <memory>
#include <string>
#include <taglib/tiostream.h>
#include <taglib/fileref.h>
#include "rust/cxx.h"

// Forward declare the bridge type
struct RsIOStream;

namespace taglib_shim
{
    // Factory functions with external linkage
    std::unique_ptr<TagLib::IOStream> wrap_RsIOStream(rust::Box<RsIOStream> stream);
} // namespace taglib_shim