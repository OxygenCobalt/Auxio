#include "file_shim.hpp"

namespace taglib_shim {

// File type checking functions
bool File_isMPEG(TagLib::File* file) {
    return dynamic_cast<TagLib::MPEG::File*>(file) != nullptr;
}

bool File_isFLAC(TagLib::File* file) {
    return dynamic_cast<TagLib::FLAC::File*>(file) != nullptr;
}

bool File_isMP4(TagLib::File* file) {
    return dynamic_cast<TagLib::MP4::File*>(file) != nullptr;
}

bool File_isOgg(TagLib::File* file) {
    return dynamic_cast<TagLib::Ogg::File*>(file) != nullptr;
}

bool File_isOpus(TagLib::File* file) {
    return dynamic_cast<TagLib::Ogg::Opus::File*>(file) != nullptr;
}

bool File_isWAV(TagLib::File* file) {
    return dynamic_cast<TagLib::RIFF::WAV::File*>(file) != nullptr;
}

bool File_isWavPack(TagLib::File* file) {
    return dynamic_cast<TagLib::WavPack::File*>(file) != nullptr;
}

bool File_isAPE(TagLib::File* file) {
    return dynamic_cast<TagLib::APE::File*>(file) != nullptr;
}

} // namespace taglib_shim 