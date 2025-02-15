#include "file_shim.hpp"

namespace taglib_shim
{
    std::unique_ptr<TagLib::FileRef> new_FileRef(std::unique_ptr<TagLib::IOStream> stream) {
        return std::make_unique<TagLib::FileRef>(stream.release());
    }

    TagLib::Ogg::Vorbis::File *File_asVorbis(TagLib::File *file)
    {
        return dynamic_cast<TagLib::Ogg::Vorbis::File *>(file);
    }

    TagLib::Ogg::Opus::File *File_asOpus(TagLib::File *file)
    {
        return dynamic_cast<TagLib::Ogg::Opus::File *>(file);
    }

    TagLib::MPEG::File *File_asMPEG(TagLib::File *file)
    {
        return dynamic_cast<TagLib::MPEG::File *>(file);
    }

    TagLib::FLAC::File *File_asFLAC(TagLib::File *file)
    {
        return dynamic_cast<TagLib::FLAC::File *>(file);
    }

    TagLib::MP4::File *File_asMP4(TagLib::File *file)
    {
        return dynamic_cast<TagLib::MP4::File *>(file);
    }

    TagLib::RIFF::WAV::File *File_asWAV(TagLib::File *file)
    {
        return dynamic_cast<TagLib::RIFF::WAV::File *>(file);
    }

} // namespace taglib_shim