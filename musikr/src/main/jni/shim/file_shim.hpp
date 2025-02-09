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

namespace taglib_shim {

// File type checking functions
bool File_isMPEG(TagLib::File* file);
bool File_isFLAC(TagLib::File* file);
bool File_isMP4(TagLib::File* file);
bool File_isOgg(TagLib::File* file);
bool File_isOpus(TagLib::File* file);
bool File_isWAV(TagLib::File* file);
bool File_isWavPack(TagLib::File* file);
bool File_isAPE(TagLib::File* file);


} // namespace taglib_shim 