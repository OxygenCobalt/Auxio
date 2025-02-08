#include "string_shim.hpp"

namespace taglib_shim {

const char* to_string(const TagLib::String& str) {
    return str.toCString(true);
}

bool isEmpty(const TagLib::String& str) {
    return str.isEmpty();
}

} // namespace taglib_shim 