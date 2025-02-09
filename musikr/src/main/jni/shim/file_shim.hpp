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

namespace taglib_shim {

// File conversion functions
TagLib::Ogg::Vorbis::File* File_asVorbis(TagLib::File* file);
TagLib::Ogg::Opus::File* File_asOpus(TagLib::File* file);
TagLib::MPEG::File* File_asMPEG(TagLib::File* file);
TagLib::FLAC::File* File_asFLAC(TagLib::File* file);
TagLib::MP4::File* File_asMP4(TagLib::File* file);
TagLib::RIFF::WAV::File* File_asWAV(TagLib::File* file);
TagLib::WavPack::File* File_asWavPack(TagLib::File* file);
TagLib::APE::File* File_asAPE(TagLib::File* file);

} // namespace taglib_shim 