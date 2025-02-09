#include "file_shim.hpp"

namespace taglib_shim {

// File conversion functions
TagLib::Ogg::Vorbis::File* File_asVorbis(TagLib::File* file) {
    return dynamic_cast<TagLib::Ogg::Vorbis::File*>(file);
}

TagLib::Ogg::Opus::File* File_asOpus(TagLib::File* file) {
    return dynamic_cast<TagLib::Ogg::Opus::File*>(file);
}

TagLib::MPEG::File* File_asMPEG(TagLib::File* file) {
    return dynamic_cast<TagLib::MPEG::File*>(file);
}

TagLib::FLAC::File* File_asFLAC(TagLib::File* file) {
    return dynamic_cast<TagLib::FLAC::File*>(file);
}

TagLib::MP4::File* File_asMP4(TagLib::File* file) {
    return dynamic_cast<TagLib::MP4::File*>(file);
}

TagLib::RIFF::WAV::File* File_asWAV(TagLib::File* file) {
    return dynamic_cast<TagLib::RIFF::WAV::File*>(file);
}

TagLib::WavPack::File* File_asWavPack(TagLib::File* file) {
    return dynamic_cast<TagLib::WavPack::File*>(file);
}

TagLib::APE::File* File_asAPE(TagLib::File* file) {
    return dynamic_cast<TagLib::APE::File*>(file);
}

} // namespace taglib_shim 