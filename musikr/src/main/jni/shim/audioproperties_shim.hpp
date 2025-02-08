#pragma once

#include <taglib/audioproperties.h>
#include <taglib/tfile.h>

namespace taglib_shim {

// Audio Properties methods
const TagLib::AudioProperties* File_audioProperties(const TagLib::File& file);
int AudioProperties_lengthInMilliseconds(const TagLib::AudioProperties* properties);
int AudioProperties_bitrateInKilobitsPerSecond(const TagLib::AudioProperties* properties);
int AudioProperties_sampleRateInHz(const TagLib::AudioProperties* properties);
int AudioProperties_numberOfChannels(const TagLib::AudioProperties* properties);

} // namespace taglib_shim 