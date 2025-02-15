#include "tk_shim.hpp"

namespace taglib_shim
{
    Property::Property(TagLib::String key, TagLib::StringList value) : key_(key), value_(value) {}

    const TagLib::String &Property::key() const
    {
        return key_;
    }

    const TagLib::StringList &Property::value() const
    {
        return value_;
    }
    std::unique_ptr<std::vector<Property>> SimplePropertyMap_to_vector(const TagLib::SimplePropertyMap &map)
    {
        std::unique_ptr<std::vector<Property>> result = std::make_unique<std::vector<Property>>();
        for (const auto &pair : map)
        {
            result->push_back(Property(pair.first, pair.second));
        }
        return result;
    }

    std::unique_ptr<std::vector<TagLib::String>> StringList_to_vector(const TagLib::StringList &list)
    {
        std::unique_ptr<std::vector<TagLib::String>> result = std::make_unique<std::vector<TagLib::String>>();
        for (const auto &str : list)
        {
            result->push_back(str);
        }
        return result;
    }

    std::unique_ptr<std::vector<PictureRef>> FLACFile_pictureList_to_vector(TagLib::FLAC::File &file)
    {
        std::unique_ptr<std::vector<PictureRef>> result = std::make_unique<std::vector<PictureRef>>();
        const auto pictures = file.pictureList();
        for (const auto &picture : pictures)
        {
            result->push_back(PictureRef(picture));
        }
        return result;
    }

    std::unique_ptr<std::vector<PictureRef>> XiphComment_pictureList_to_vector(TagLib::Ogg::XiphComment &comment)
    {
        std::unique_ptr<std::vector<PictureRef>> result = std::make_unique<std::vector<PictureRef>>();
        const auto pictures = comment.pictureList();
        for (const auto &picture : pictures)
        {
            result->push_back(PictureRef(picture));
        }
        return result;
    }

    rust::String String_to_string(const TagLib::String &str)
    {
        return rust::String(str.to8Bit());
    }

    std::unique_ptr<std::vector<uint8_t>> ByteVector_to_bytes(const TagLib::ByteVector &data)
    {
        auto result = std::make_unique<std::vector<uint8_t>>();
        result->reserve(data.size());
        for (size_t i = 0; i < data.size(); i++)
        {
            result->push_back(static_cast<uint8_t>(data[i]));
        }
        return result;
    }
}
