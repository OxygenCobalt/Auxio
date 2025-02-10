#include "file_shim.hpp"

namespace taglib_shim
{

    const TagLib::Ogg::File *File_asOgg(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::Ogg::File *>(file);
    }

    const TagLib::Ogg::Vorbis::File *File_asVorbis(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::Ogg::Vorbis::File *>(file);
    }

    const TagLib::Ogg::Opus::File *File_asOpus(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::Ogg::Opus::File *>(file);
    }

    const TagLib::MPEG::File *File_asMPEG(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::MPEG::File *>(file);
    }

    const TagLib::FLAC::File *File_asFLAC(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::FLAC::File *>(file);
    }

    const TagLib::MP4::File *File_asMP4(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::MP4::File *>(file);
    }

    const TagLib::RIFF::WAV::File *File_asWAV(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::RIFF::WAV::File *>(file);
    }

    const TagLib::WavPack::File *File_asWavPack(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::WavPack::File *>(file);
    }

    const TagLib::APE::File *File_asAPE(const TagLib::File *file)
    {
        return dynamic_cast<const TagLib::APE::File *>(file);
    }

} // namespace taglib_shim