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

// FileRef helper functions
bool FileRef_isNull(const TagLib::FileRef& ref);
const TagLib::File& FileRef_file(const TagLib::FileRef& ref);

// File tag methods
bool File_tag(const TagLib::File& file);
const TagLib::String& File_tag_title(const TagLib::File& file);

// File type checking functions
bool File_isMPEG(const TagLib::File& file);
bool File_isFLAC(const TagLib::File& file);
bool File_isMP4(const TagLib::File& file);
bool File_isOgg(const TagLib::File& file);
bool File_isOpus(const TagLib::File& file);
bool File_isWAV(const TagLib::File& file);
bool File_isWavPack(const TagLib::File& file);
bool File_isAPE(const TagLib::File& file);

// Audio Properties methods
const TagLib::AudioProperties* File_audioProperties(const TagLib::File& file);
int AudioProperties_length(const TagLib::AudioProperties* properties);
int AudioProperties_bitrate(const TagLib::AudioProperties* properties);
int AudioProperties_sampleRate(const TagLib::AudioProperties* properties);
int AudioProperties_channels(const TagLib::AudioProperties* properties);

} // namespace taglib_shim 