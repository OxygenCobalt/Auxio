#include "audioproperties_shim.hpp"
#include <taglib/tfile.h>

namespace taglib_shim {

const TagLib::AudioProperties* File_audioProperties(const TagLib::File& file) {
    return file.audioProperties();
}

int AudioProperties_lengthInMilliseconds(const TagLib::AudioProperties* properties) {
    return properties->lengthInMilliseconds();
}

int AudioProperties_bitrateInKilobitsPerSecond(const TagLib::AudioProperties* properties) {
    return properties->bitrate();
}

int AudioProperties_sampleRateInHz(const TagLib::AudioProperties* properties) {
    return properties->sampleRate();
}

int AudioProperties_numberOfChannels(const TagLib::AudioProperties* properties) {
    return properties->channels();
}

} // namespace taglib_shim 