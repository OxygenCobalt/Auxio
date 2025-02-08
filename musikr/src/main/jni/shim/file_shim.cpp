#include "file_shim.hpp"

namespace taglib_shim {

// FileRef helper functions
bool FileRef_isNull(const TagLib::FileRef& ref) {
    return ref.isNull();
}

const TagLib::File& FileRef_file(const TagLib::FileRef& ref) {
    return *ref.file();
}

// File tag methods
bool File_tag(const TagLib::File& file) {
    return file.tag() != nullptr;
}

namespace {
    // Keep the empty string as a static member to ensure it lives long enough
    const TagLib::String empty_string;
}

const TagLib::String& File_tag_title(const TagLib::File& file) {
    if (auto* tag = file.tag()) {
        static TagLib::String title;
        title = tag->title();
        return title;
    }
    return empty_string;
}

// File type checking functions
bool File_isMPEG(const TagLib::File& file) {
    return dynamic_cast<const TagLib::MPEG::File*>(&file) != nullptr;
}

bool File_isFLAC(const TagLib::File& file) {
    return dynamic_cast<const TagLib::FLAC::File*>(&file) != nullptr;
}

bool File_isMP4(const TagLib::File& file) {
    return dynamic_cast<const TagLib::MP4::File*>(&file) != nullptr;
}

bool File_isOgg(const TagLib::File& file) {
    return dynamic_cast<const TagLib::Ogg::File*>(&file) != nullptr;
}

bool File_isOpus(const TagLib::File& file) {
    return dynamic_cast<const TagLib::Ogg::Opus::File*>(&file) != nullptr;
}

bool File_isWAV(const TagLib::File& file) {
    return dynamic_cast<const TagLib::RIFF::WAV::File*>(&file) != nullptr;
}

bool File_isWavPack(const TagLib::File& file) {
    return dynamic_cast<const TagLib::WavPack::File*>(&file) != nullptr;
}

bool File_isAPE(const TagLib::File& file) {
    return dynamic_cast<const TagLib::APE::File*>(&file) != nullptr;
}

} // namespace taglib_shim 