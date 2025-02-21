/*
 * Copyright (c) 2024 Auxio Project
 * JInputStream.h is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
#ifndef AUXIO_JINPUTSTREAM_H
#define AUXIO_JINPUTSTREAM_H

#include <jni.h>
#include "JObjectRef.h"

#include "taglib/tiostream.h"
#include "taglib/tstring.h"

class JInputStream: public TagLib::IOStream {
public:
    JInputStream(JNIEnv *env, jobject jInputStream);

    ~JInputStream();

    JInputStream(const JInputStream&) = delete;
    JInputStream& operator=(const JInputStream&) = delete;

    /*!
     * Returns the stream name in the local file system encoding.
     */
    TagLib::FileName /* const char * */ name() const override;

    /*!
     * Reads a block of size \a length at the current get pointer.
     */
    TagLib::ByteVector readBlock(size_t length) override;

    /*!
     * Attempts to write the block \a data at the current get pointer.  If the
     * file is currently only opened read only -- i.e. readOnly() returns \c true --
     * this attempts to reopen the file in read/write mode.
     *
     * \note This should be used instead of using the streaming output operator
     * for a ByteVector.  And even this function is significantly slower than
     * doing output with a char[].
     */
    void writeBlock(const TagLib::ByteVector &data) override;

    /*!
     * Insert \a data at position \a start in the file overwriting \a replace
     * bytes of the original content.
     *
     * \note This method is slow since it requires rewriting all of the file
     * after the insertion point.
     */
    void insert(const TagLib::ByteVector &data, TagLib::offset_t start = 0,
            size_t replace = 0) override;

    /*!
     * Removes a block of the file starting a \a start and continuing for
     * \a length bytes.
     *
     * \note This method is slow since it involves rewriting all of the file
     * after the removed portion.
     */
    void removeBlock(TagLib::offset_t start = 0, size_t length = 0) override;

    /*!
     * Returns \c true if the file is read only (or if the file can not be opened).
     */
    bool readOnly() const override;

    /*!
     * Since the file can currently only be opened as an argument to the
     * constructor (sort-of by design), this returns if that open succeeded.
     */
    bool isOpen() const override;

    /*!
     * Move the I/O pointer to \a offset in the stream from position \a p.  This
     * defaults to seeking from the beginning of the stream.
     *
     * \see Position
     */
    void seek(TagLib::offset_t offset, Position p = Beginning) override;

    /*!
     * Reset the end-of-stream and error flags on the stream.
     */
    void clear() override;

    /*!
     * Returns the current offset within the stream.
     */
    TagLib::offset_t tell() const override;

    /*!
     * Returns the length of the stream.
     */
    TagLib::offset_t length() override;

    /*!
     * Truncates the stream to a \a length.
     */
    void truncate(TagLib::offset_t length) override;

private:
    JNIEnv *env;
    jobject jInputStream;
    TagLib::String _name;
    jmethodID jInputStreamReadBlockMethod;
    jmethodID jInputStreamIsOpenMethod;
    jmethodID jInputStreamSeekFromBeginningMethod;
    jmethodID jInputStreamSeekFromCurrentMethod;
    jmethodID jInputStreamSeekFromEndMethod;
    jmethodID jInputStreamTellMethod;
    jmethodID jInputStreamLengthMethod;
};

#endif //AUXIO_JINPUTSTREAM_H
