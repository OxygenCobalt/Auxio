/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.metadata

import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.inRangeOrNull
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * An ISO-8601/RFC 3339 Date.
 *
 * This class only encodes the timestamp spec and it's conversion to a human-readable date, without
 * any other time management or validation. In general, this should only be used for display. Use
 * [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class Date private constructor(private val tokens: List<Int>) : Comparable<Date> {
    private val year = tokens[0]
    private val month = tokens.getOrNull(1)
    private val day = tokens.getOrNull(2)
    private val hour = tokens.getOrNull(3)
    private val minute = tokens.getOrNull(4)
    private val second = tokens.getOrNull(5)

    /**
     * Resolve this instance into a human-readable date.
     * @param context [Context] required to get human-readable names.
     * @return If the [Date] has a valid month and year value, a more fine-grained date (ex. "Jan
     * 2020") will be returned. Otherwise, a plain year value (ex. "2020") is returned. Dates will
     * be properly localized.
     */
    fun resolveDate(context: Context): String {
        if (month != null) {
            // Parse a date format from an ISO-ish format
            val format = (SimpleDateFormat.getDateInstance() as SimpleDateFormat)
            format.applyPattern("yyyy-MM")
            val date =
                try {
                    format.parse("$year-$month")
                } catch (e: ParseException) {
                    null
                }

            if (date != null) {
                // Reformat as a readable month and year
                format.applyPattern("MMM yyyy")
                return format.format(date)
            }
        }

        // Unable to create fine-grained date, just format as a year.
        return context.getString(R.string.fmt_number, year)
    }

    override fun hashCode() = tokens.hashCode()

    override fun equals(other: Any?) = other is Date && compareTo(other) == 0

    override fun compareTo(other: Date): Int {
        for (i in 0 until max(tokens.size, other.tokens.size)) {
            val ai = tokens.getOrNull(i)
            val bi = other.tokens.getOrNull(i)
            when {
                ai != null && bi != null -> {
                    val result = ai.compareTo(bi)
                    if (result != 0) {
                        return result
                    }
                }
                ai == null && bi != null -> return -1 // a < b
                ai == null && bi == null -> return 0 // a = b
                else -> return 1 // a < b
            }
        }

        return 0
    }

    override fun toString() = StringBuilder().appendDate().toString()

    private fun StringBuilder.appendDate(): StringBuilder {
        // Construct an ISO-8601 date, dropping precision that doesn't exist.
        append(year.toStringFixed(4))
        append("-${(month ?: return this).toStringFixed(2)}")
        append("-${(day ?: return this).toStringFixed(2)}")
        append("T${(hour ?: return this).toStringFixed(2)}")
        append(":${(minute ?: return this.append('Z')).toStringFixed(2)}")
        append(":${(second ?: return this.append('Z')).toStringFixed(2)}")
        return this.append('Z')
    }

    private fun Int.toStringFixed(len: Int) = toString().padStart(len, '0').substring(0 until len)

    /**
     * A range of [Date]s. This is used in contexts where the [Date] of an item is derived from
     * several sub-items and thus can have a "range" of release dates. Use [from] to create an
     * instance.
     * @author Alexander Capehart
     */
    class Range
    private constructor(
        /** The earliest [Date] in the range. */
        val min: Date,
        /** the latest [Date] in the range. May be the same as [min]. ] */
        val max: Date
    ) : Comparable<Range> {

        /**
         * Resolve this instance into a human-readable date range.
         * @param context [Context] required to get human-readable names.
         * @return If the date has a maximum value, then a `min - max` formatted string will be
         * returned with the formatted [Date]s of the minimum and maximum dates respectively.
         * Otherwise, the formatted name of the minimum [Date] will be returned.
         */
        fun resolveDate(context: Context) =
            if (min != max) {
                context.getString(
                    R.string.fmt_date_range, min.resolveDate(context), max.resolveDate(context))
            } else {
                min.resolveDate(context)
            }

        override fun equals(other: Any?) = other is Range && min == other.min && max == other.max

        override fun hashCode() = 31 * max.hashCode() + min.hashCode()

        override fun compareTo(other: Range) = min.compareTo(other.min)

        companion object {
            /**
             * Create a [Range] from the given list of [Date]s.
             * @param dates The [Date]s to use.
             * @return A [Range] based on the minimum and maximum [Date]s. If there are no [Date]s,
             * null is returned.
             */
            fun from(dates: List<Date>): Range? {
                if (dates.isEmpty()) {
                    // Nothing to do.
                    return null
                }
                // Simultaneously find the minimum and maximum values in the given range.
                // If this list has only one item, then that one date is the minimum and maximum.
                var min = dates.first()
                var max = min
                for (i in 1..dates.lastIndex) {
                    if (dates[i] < min) {
                        min = dates[i]
                    }
                    if (dates[i] > max) {
                        max = dates[i]
                    }
                }
                return Range(min, max)
            }
        }
    }

    companion object {
        /**
         * A [Regex] that can parse a variable-precision ISO-8601 timestamp. Derived from
         * https://github.com/quodlibet/mutagen
         */
        private val ISO8601_REGEX =
            Regex(
                """^(\d{4})([-.](\d{2})([-.](\d{2})([T ](\d{2})([:.](\d{2})([:.](\d{2})(Z)?)?)?)?)?)?$""")

        /**
         * Create a [Date] from a year component.
         * @param year The year component.
         * @return A new [Date] of the given component, or null if the component is invalid.
         */
        fun from(year: Int) =
            if (year in 10000000..100000000) {
                // Year is actually more likely to be a separated date timestamp. Interpret
                // it as such.
                val stringYear = year.toString()
                from(
                    stringYear.substring(0..3).toInt(),
                    stringYear.substring(4..5).toInt(),
                    stringYear.substring(6..7).toInt())
            } else {
                fromTokens(listOf(year))
            }

        /**
         * Create a [Date] from a date component.
         * @param year The year component.
         * @param month The month component.
         * @param day The day component.
         * @return A new [Date] consisting of the given components. May have reduced precision if
         * the components were partially invalid, and will be null if all components are invalid.
         */
        fun from(year: Int, month: Int, day: Int) = fromTokens(listOf(year, month, day))

        /**
         * Create [Date] from a datetime component.
         * @param year The year component.
         * @param month The month component.
         * @param day The day component.
         * @param hour The hour component
         * @return A new [Date] consisting of the given components. May have reduced precision if
         * the components were partially invalid, and will be null if all components are invalid.
         */
        fun from(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
            fromTokens(listOf(year, month, day, hour, minute))

        /**
         * Create a [Date] from a [String] timestamp.
         * @param timestamp The ISO-8601 timestamp to parse. Can have reduced precision.
         * @return A new [Date] consisting of the given components. May have reduced precision if
         * the components were partially invalid, and will be null if all components are invalid or
         * if the timestamp is invalid.
         */
        fun from(timestamp: String): Date? {
            val tokens =
            // Match the input with the timestamp regex. If there is no match, see if we can
            // fall back to some kind of year value.
            (ISO8601_REGEX.matchEntire(timestamp)
                        ?: return timestamp.toIntOrNull()?.let(Companion::from))
                    .groupValues
                    // Filter to the specific tokens we want and convert them to integer tokens.
                    .mapIndexedNotNull { index, s -> if (index % 2 != 0) s.toIntOrNull() else null }
            return fromTokens(tokens)
        }

        /**
         * Create a [Date] from the given non-validated tokens.
         * @param tokens The tokens to use for each date component, in order of precision.
         * @return A new [Date] consisting of the given components. May have reduced precision if
         * the components were partially invalid, and will be null if all components are invalid.
         */
        private fun fromTokens(tokens: List<Int>): Date? {
            val validated = mutableListOf<Int>()
            transformTokens(tokens, validated)
            if (validated.isEmpty()) {
                // No token was valid, return null.
                return null
            }
            return Date(validated)
        }

        /**
         * Validate a list of tokens provided by [src], and add the valid ones to [dst]. Will stop
         * as soon as an invalid token is found.
         * @param src The input tokens to validate.
         * @param dst The destination list to add valid tokens to.
         */
        private fun transformTokens(src: List<Int>, dst: MutableList<Int>) {
            dst.add(src.getOrNull(0)?.nonZeroOrNull() ?: return)
            dst.add(src.getOrNull(1)?.inRangeOrNull(1..12) ?: return)
            dst.add(src.getOrNull(2)?.inRangeOrNull(1..31) ?: return)
            dst.add(src.getOrNull(3)?.inRangeOrNull(0..23) ?: return)
            dst.add(src.getOrNull(4)?.inRangeOrNull(0..59) ?: return)
            dst.add(src.getOrNull(5)?.inRangeOrNull(0..59) ?: return)
        }
    }
}
