#pragma once

#include <taglib/tstring.h>

namespace taglib_shim {

// String utilities
const char* to_string(const TagLib::String& str);
bool isEmpty(const TagLib::String& str);

} // namespace taglib_shim 