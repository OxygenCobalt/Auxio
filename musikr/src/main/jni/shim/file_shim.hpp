#pragma once

#include <taglib/fileref.h>
#include <taglib/tag.h>
#include <taglib/tstring.h>
#include <taglib/audioproperties.h>
#include <taglib/mpegfile.h>
#include <taglib/flacfile.h>
#include <taglib/mp4file.h>
#include <taglib/oggfile.h>
#include <taglib/opusfile.h>
#include <taglib/wavfile.h>
#include <taglib/wavpackfile.h>
#include <taglib/apefile.h>
#include <taglib/vorbisfile.h>

namespace taglib_shim
{
    std::unique_ptr<TagLib::FileRef> new_FileRef(TagLib::IOStream *stream);

    TagLib::Ogg::Vorbis::File *File_asVorbis(TagLib::File *file);
    TagLib::Ogg::Opus::File *File_asOpus(TagLib::File *file);
    TagLib::MPEG::File *File_asMPEG(TagLib::File *file);
    TagLib::FLAC::File *File_asFLAC(TagLib::File *file);
    TagLib::MP4::File *File_asMP4(TagLib::File *file);
    TagLib::RIFF::WAV::File *File_asWAV(TagLib::File *file);
} // namespace taglib_shim