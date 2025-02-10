#include "iostream_shim.hpp"
#include <stdexcept>
#include <rust/cxx.h>
#include <vector>

// These are the functions we'll define in Rust
extern "C"
{
    const char *rust_stream_name(const void *stream);
    size_t rust_stream_read(void *stream, uint8_t *buffer, size_t length);
    // void rust_stream_write(void *stream, const uint8_t *data, size_t length);
    void rust_stream_seek(void *stream, int64_t offset, int32_t whence);
    // void rust_stream_truncate(void *stream, int64_t length);
    int64_t rust_stream_tell(const void *stream);
    int64_t rust_stream_length(const void *stream);
    // bool rust_stream_is_readonly(const void *stream);
}

namespace taglib_shim
{

    // Factory function to create a new RustIOStream
    std::unique_ptr<RustIOStream> new_rust_iostream(RustStream *stream)
    {
        return std::unique_ptr<RustIOStream>(new RustIOStream(stream));
    }

    // Factory function to create a FileRef from a stream
    std::unique_ptr<TagLib::FileRef> new_FileRef_from_stream(std::unique_ptr<RustIOStream> stream)
    {
        return std::make_unique<TagLib::FileRef>(stream.release(), true);
    }

    RustIOStream::RustIOStream(RustStream *stream) : rust_stream(stream) {}

    RustIOStream::~RustIOStream() = default;

    TagLib::FileName RustIOStream::name() const
    {
        return rust_stream_name(rust_stream);
    }

    TagLib::ByteVector RustIOStream::readBlock(size_t length)
    {
        std::vector<uint8_t> buffer(length);
        size_t bytes_read = rust_stream_read(rust_stream, buffer.data(), length);
        return TagLib::ByteVector(reinterpret_cast<char *>(buffer.data()), bytes_read);
    }

    void RustIOStream::writeBlock(const TagLib::ByteVector &_)
    {
        throw std::runtime_error("Stream is read-only");
    }

    void RustIOStream::insert(const TagLib::ByteVector &data, TagLib::offset_t start, size_t replace)
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

    void RustIOStream::removeBlock(TagLib::offset_t start, size_t length)
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

    void RustIOStream::seek(TagLib::offset_t offset, Position p)
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
            throw std::runtime_error("Invalid seek position");
        }
        rust_stream_seek(rust_stream, offset, whence);
    }

    void RustIOStream::clear()
    {
        truncate(0);
        seek(0);
    }

    void RustIOStream::truncate(TagLib::offset_t _)
    {
        throw std::runtime_error("Stream is read-only");
    }

    TagLib::offset_t RustIOStream::tell() const
    {
        return rust_stream_tell(rust_stream);
    }

    TagLib::offset_t RustIOStream::length()
    {
        return rust_stream_length(rust_stream);
    }

    bool RustIOStream::readOnly() const
    {
        return true;
    }

    bool RustIOStream::isOpen() const
    {
        return true; // If we have a stream, it's open
    }

} // namespace taglib_shim