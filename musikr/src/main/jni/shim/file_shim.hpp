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

    // File conversion functions
    const TagLib::Ogg::File *File_asOgg(const TagLib::File *file);
    const TagLib::Ogg::Vorbis::File *File_asVorbis(const TagLib::File *file);
    const TagLib::Ogg::Opus::File *File_asOpus(const TagLib::File *file);
    const TagLib::MPEG::File *File_asMPEG(const TagLib::File *file);
    const TagLib::FLAC::File *File_asFLAC(const TagLib::File *file);
    const TagLib::MP4::File *File_asMP4(const TagLib::File *file);
    const TagLib::RIFF::WAV::File *File_asWAV(const TagLib::File *file);
    const TagLib::WavPack::File *File_asWavPack(const TagLib::File *file);
    const TagLib::APE::File *File_asAPE(const TagLib::File *file);

} // namespace taglib_shim