//
// Created by oxycblt on 12/12/24.
//

#include "AndroidIOStream.h"


AndroidIOStream::AndroidIOStream(JNIEnv *env, jobject &fileRef) : env(env), fileRef(fileRef) {}

AndroidIOStream::~AndroidIOStream() {}

/*!
 * Returns the stream name in the local file system encoding.
 */
TagLib::FileName AndroidIOStream::name() const {
    return TagLib::FileName();
};

/*!
 * Reads a block of size \a length at the current get pointer.
 */
TagLib::ByteVector AndroidIOStream::readBlock(size_t length) {
    return {};
};

void AndroidIOStream::writeBlock(const TagLib::ByteVector &data) {
    throw std::runtime_error("Not implemented");
};

void AndroidIOStream::insert(const TagLib::ByteVector &data,
            TagLib::offset_t start, size_t replace) {
    throw std::runtime_error("Not implemented");
};

void AndroidIOStream::removeBlock(TagLib::offset_t start, size_t length) {
    throw std::runtime_error("Not implemented");
};

bool AndroidIOStream::readOnly() const {
    return true;
};

bool AndroidIOStream::isOpen() const {

};

void AndroidIOStream::seek(TagLib::offset_t offset, Position p) {

};

void AndroidIOStream::clear() {

}

TagLib::offset_t AndroidIOStream::tell() const {
    return 0;
};

TagLib::offset_t AndroidIOStream::length() {
    return 0;
};

void AndroidIOStream::truncate(TagLib::offset_t length) {

};