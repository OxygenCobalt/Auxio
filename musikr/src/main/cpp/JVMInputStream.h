//
// Created by oxycblt on 12/12/24.
//

#ifndef AUXIO_JVMINPUTSTREAM_H
#define AUXIO_JVMINPUTSTREAM_H

#include <jni.h>

#include "taglib/tiostream.h"

class JVMInputStream : public  TagLib::IOStream {
public:
    JVMInputStream(JNIEnv *env, jobject inputStream);

    ~JVMInputStream();

    JVMInputStream(const JVMInputStream &) = delete;
    JVMInputStream &operator=(const JVMInputStream &) = delete;

    /*!
     * Returns the stream name in the local file system encoding.
     */
    TagLib::FileName name() const override;

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
    void insert(const TagLib::ByteVector &data,
                        TagLib::offset_t start = 0, size_t replace = 0) override;

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
    jobject inputStream;
    jmethodID inputStreamReadBlockMethod;
    jmethodID inputStreamIsOpenMethod;
    jmethodID inputStreamSeekFromBeginningMethod;
    jmethodID inputStreamSeekFromCurrentMethod;
    jmethodID inputStreamSeekFromEndMethod;
    jmethodID inputStreamTellMethod;
    jmethodID inputStreamLengthMethod;

};


#endif //AUXIO_JVMINPUTSTREAM_H
