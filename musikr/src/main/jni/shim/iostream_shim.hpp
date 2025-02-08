#pragma once

#include <memory>
#include <string>
#include <taglib/tiostream.h>
#include <taglib/fileref.h>
#include <taglib/tag.h>
#include <taglib/tstring.h>
#include <taglib/audioproperties.h>
#include <cstdint>
#include <taglib/mpegfile.h>
#include <taglib/flacfile.h>
#include <taglib/mp4file.h>
#include <taglib/oggfile.h>
#include <taglib/opusfile.h>
#include <taglib/wavfile.h>
#include <taglib/wavpackfile.h>
#include <taglib/apefile.h>

namespace taglib_shim {

// Forward declaration of the Rust-side stream
struct RustStream;

// C++ implementation of TagLib::IOStream that delegates to Rust
class RustIOStream : public TagLib::IOStream {
public:
    explicit RustIOStream(RustStream* stream);
    ~RustIOStream() override;

    // TagLib::IOStream interface implementation
    TagLib::FileName name() const override;
    TagLib::ByteVector readBlock(size_t length) override;
    void writeBlock(const TagLib::ByteVector& data) override;
    void insert(const TagLib::ByteVector& data, TagLib::offset_t start = 0, size_t replace = 0) override;
    void removeBlock(TagLib::offset_t start = 0, size_t length = 0) override;
    void seek(TagLib::offset_t offset, Position p = Beginning) override;
    void clear() override;
    void truncate(TagLib::offset_t length) override;
    TagLib::offset_t tell() const override;
    TagLib::offset_t length() override;
    bool readOnly() const override;
    bool isOpen() const override;

private:
    RustStream* rust_stream;
};

// Factory functions
std::unique_ptr<RustIOStream> new_rust_iostream(RustStream* stream);
std::unique_ptr<TagLib::FileRef> new_FileRef_from_stream(std::unique_ptr<RustIOStream> stream);

// FileRef helper functions
bool FileRef_isNull(const TagLib::FileRef& ref);
const TagLib::File& FileRef_file(const TagLib::FileRef& ref);

// File tag methods
bool File_tag(const TagLib::File& file);
const TagLib::String& File_tag_title(const TagLib::File& file);

// File type checking functions
bool File_isMPEG(const TagLib::File& file) {
    return dynamic_cast<const TagLib::MPEG::File*>(&file) != nullptr;
}

bool File_isFLAC(const TagLib::File& file) {
    return dynamic_cast<const TagLib::FLAC::File*>(&file) != nullptr;
}

bool File_isMP4(const TagLib::File& file) {
    return dynamic_cast<const TagLib::MP4::File*>(&file) != nullptr;
}

bool File_isOgg(const TagLib::File& file) {
    return dynamic_cast<const TagLib::Ogg::File*>(&file) != nullptr;
}

bool File_isOpus(const TagLib::File& file) {
    return dynamic_cast<const TagLib::Ogg::Opus::File*>(&file) != nullptr;
}

bool File_isWAV(const TagLib::File& file) {
    return dynamic_cast<const TagLib::RIFF::WAV::File*>(&file) != nullptr;
}

bool File_isWavPack(const TagLib::File& file) {
    return dynamic_cast<const TagLib::WavPack::File*>(&file) != nullptr;
}

bool File_isAPE(const TagLib::File& file) {
    return dynamic_cast<const TagLib::APE::File*>(&file) != nullptr;
}

// Audio Properties methods
const TagLib::AudioProperties* File_audioProperties(const TagLib::File& file);
int AudioProperties_length(const TagLib::AudioProperties* properties);
int AudioProperties_bitrate(const TagLib::AudioProperties* properties);
int AudioProperties_sampleRate(const TagLib::AudioProperties* properties);
int AudioProperties_channels(const TagLib::AudioProperties* properties);

// String utilities
const char* to_string(const TagLib::String& str);
bool isEmpty(const TagLib::String& str);

} 