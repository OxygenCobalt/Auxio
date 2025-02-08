#pragma once

#include <memory>
#include <string>
#include <taglib/tiostream.h>
#include <taglib/fileref.h>
#include <taglib/tag.h>
#include <taglib/tstring.h>
#include <cstdint>

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

// String utilities
const char* to_string(const TagLib::String& str);
bool isEmpty(const TagLib::String& str);

} 