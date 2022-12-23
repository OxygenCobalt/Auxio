package org.oxycblt.auxio.music.extractor

/**
 * Represents the result of an extraction operation.
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class ExtractionResult {
    /**
     * A raw song was successfully extracted from the cache.
     */
    CACHED,

    /**
     * A raw song was successfully extracted from parsing it's file.
     */
    PARSED,

    /**
     * A raw song could not be parsed.
     */
    NONE
}