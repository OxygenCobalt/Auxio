#include "iostream_shim.hpp"
#include <stdexcept>
#include <rust/cxx.h>
#include <vector>
#include "metadatajni/src/taglib/bridge.rs.h"

namespace taglib_shim
{

    // C++ implementation of TagLib::IOStream that delegates to Rust
    class WrappedRsIOStream : public TagLib::IOStream
    {
    public:
        explicit WrappedRsIOStream(RsIOStream *stream);
        ~WrappedRsIOStream() override;

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
        std::string _name;
        RsIOStream *rust_stream;
    };

    WrappedRsIOStream::WrappedRsIOStream(RsIOStream *stream) : rust_stream(stream)
    {
        _name = std::move(std::string(rust_stream->name()));
    }

    WrappedRsIOStream::~WrappedRsIOStream() = default;

    TagLib::FileName WrappedRsIOStream::name() const
    {
        return _name.c_str();
    }

    TagLib::ByteVector WrappedRsIOStream::readBlock(size_t length)
    {
        std::vector<uint8_t> buffer(length);
        size_t bytes_read = rust_stream->read(rust::Slice<uint8_t>(buffer.data(), length));
        return TagLib::ByteVector(reinterpret_cast<char *>(buffer.data()), bytes_read);
    }

    void WrappedRsIOStream::writeBlock(const TagLib::ByteVector &data)
    {
        rust_stream->write(rust::Slice<const uint8_t>(
            reinterpret_cast<const uint8_t *>(data.data()), data.size()));
    }

    void WrappedRsIOStream::insert(const TagLib::ByteVector &data, TagLib::offset_t start, size_t replace)
    {
        // Save current position
        auto current = tell();

        // Seek to insert position
        seek(start);

        // If replacing, remove that section first
        if (replace > 0)
        {
            removeBlock(start, replace);
        }

        // Write new data
        writeBlock(data);

        // Restore position
        seek(current);
    }

    void WrappedRsIOStream::removeBlock(TagLib::offset_t start, size_t length)
    {
        if (length == 0)
            return;

        // Save current position
        auto current = tell();

        // Get file size
        auto file_length = this->length();

        // Read everything after the removed section
        seek(start + length);
        auto remaining = readBlock(file_length - (start + length));

        // Truncate to start position
        seek(start);
        truncate(start);

        // Write remaining data
        writeBlock(remaining);

        // Restore position
        seek(current);
    }

    void WrappedRsIOStream::seek(TagLib::offset_t offset, Position p)
    {
        int32_t whence;
        switch (p)
        {
        case Beginning:
            whence = SEEK_SET;
            break;
        case Current:
            whence = SEEK_CUR;
            break;
        case End:
            whence = SEEK_END;
            break;
        default:
            return;
            break;
        }
        rust_stream->seek(offset, whence);
    }

    void WrappedRsIOStream::clear()
    {
        truncate(0);
        seek(0);
    }

    void WrappedRsIOStream::truncate(TagLib::offset_t length)
    {
        rust_stream->truncate(length);
    }

    TagLib::offset_t WrappedRsIOStream::tell() const
    {
        return rust_stream->tell();
    }

    TagLib::offset_t WrappedRsIOStream::length()
    {
        return rust_stream->length();
    }

    bool WrappedRsIOStream::readOnly() const
    {
        return rust_stream->is_readonly();
    }

    bool WrappedRsIOStream::isOpen() const
    {
        return true; // If we have a stream, it's open
    }

    // Factory function to create a new RustIOStream
    std::unique_ptr<TagLib::IOStream> wrap_RsIOStream(RsIOStream *stream)
    {
        return std::unique_ptr<TagLib::IOStream>(new WrappedRsIOStream(stream));
    }

} // namespace taglib_shim