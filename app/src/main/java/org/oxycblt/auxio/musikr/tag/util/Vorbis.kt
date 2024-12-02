package org.oxycblt.auxio.musikr.tag.util

import org.oxycblt.auxio.util.positiveOrNull


/**
 * Parse an ID3v2-style position + total [String] field. These fields consist of a number and an
 * (optional) total value delimited by a /.
 *
 * @return The position value extracted from the string field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 *
 * @see transformPositionField
 */
fun String.parseId3v2PositionField() =
    split('/', limit = 2).let {
        transformPositionField(it[0].toIntOrNull(), it.getOrNull(1)?.toIntOrNull())
    }

/**
 * Parse a vorbis-style position + total field. These fields consist of two fields for the position
 * and total numbers.
 *
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 *
 * @see transformPositionField
 */
fun parseVorbisPositionField(pos: String?, total: String?) =
    transformPositionField(pos?.toIntOrNull(), total?.toIntOrNull())

/**
 * Transform a raw position + total field into a position a way that tolerates placeholder values.
 *
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 */
fun transformPositionField(pos: Int?, total: Int?) =
    if (pos != null && (pos > 0 || (total?.positiveOrNull() != null))) {
        pos
    } else {
        null
    }
