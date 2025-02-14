#pragma once

#include <memory>
#include <string>
#include <taglib/tiostream.h>
#include <taglib/fileref.h>
#include "rust/cxx.h"

// Forward declare the bridge type
struct BridgeStream;

namespace taglib_shim
{

    // C++ implementation of TagLib::IOStream that delegates to Rust
    class RustIOStream : public TagLib::IOStream
    {
    public:
        explicit RustIOStream(BridgeStream& stream);
        ~RustIOStream() override;

        // TagLib::IOStream interface implementation
        TagLib::FileName name() const override;
        TagLib::ByteVector readBlock(size_t length) override;
        void writeBlock(const TagLib::ByteVector &data) override;
        void insert(const TagLib::ByteVector &data, TagLib::offset_t start = 0, size_t replace = 0) override;
        void removeBlock(TagLib::offset_t start = 0, size_t length = 0) override;
        void seek(TagLib::offset_t offset, Position p = Beginning) override;
        void clear() override;
        void truncate(TagLib::offset_t length) override;
        TagLib::offset_t tell() const override;
        TagLib::offset_t length() override;
        bool readOnly() const override;
        bool isOpen() const override;

    private:
        BridgeStream& rust_stream;
    };

    // Factory functions with external linkage
    std::unique_ptr<RustIOStream> new_RustIOStream(BridgeStream& stream);
    
    std::unique_ptr<TagLib::FileRef> new_FileRef_from_stream(std::unique_ptr<RustIOStream> stream);

} // namespace taglib_shim