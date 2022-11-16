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
 
package org.oxycblt.auxio.music

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries
import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.inRangeOrNull
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.nonZeroOrNull

/**
 * An ISO-8601/RFC 3339 Date.
 *
 * Unlike a typical Date within the standard library, this class just represents the ID3v2/Vorbis
 * date format, which is largely assumed to be a subset of ISO-8601. No validation outside of format
 * validation is done, and any date calculation is (fallibly) performed when displayed in the UI.
 *
 * The reasoning behind Date is that Auxio cannot trust any kind of metadata date to actually make
 * sense in a calendar, due to bad tagging, locale-specific issues, or simply from the limited
 * nature of tag formats. Thus, it's better to use an analogous data structure that will not mangle
 * or reject valid-ish dates.
 *
 * Date instances are immutable and their implementation is hidden. To instantiate one, use [from].
 * The string representation of a Date is RFC 3339, with granular position depending on the presence
 * of particular tokens.
 *
 * @author OxygenCobalt
 */
class Date private constructor(private val tokens: List<Int>) : Comparable<Date> {
    init {
        if (BuildConfig.DEBUG) {
            // Last-ditch sanity check to catch format bugs that might slip through
            check(tokens.size in 1..6) { "There must be 1-6 date tokens" }
            check(tokens.slice(0..min(tokens.lastIndex, 2)).all { it > 0 }) {
                "All date tokens must be non-zero "
            }
            check(tokens.slice(1..tokens.lastIndex).all { it < 100 }) {
                "All non-year tokens must be two digits"
            }
        }
    }

    private val year = tokens[0]

    private val month = tokens.getOrNull(1)

    private val day = tokens.getOrNull(2)

    private val hour = tokens.getOrNull(3)

    private val minute = tokens.getOrNull(4)

    private val second = tokens.getOrNull(5)

    /**
     * Resolve this date into a string. This could result in a year string formatted as "YYYY", or a
     * month and year string formatted as "MMM YYYY" depending on the situation.
     */
    fun resolveDate(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return try {
                resolveFullDate(context)
            } catch (e: Exception) {
                logE("Failed to format a full date")
                logE(e.stackTraceToString())
                return resolveYear(context)
            }
        } else {
            return resolveYear(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun resolveFullDate(context: Context) =
        if (month != null) {
            val temporal =
                DateTimeFormatter.ISO_DATE.parse(
                    "$year-$month-${day ?: 1}", TemporalQueries.localDate())

            // When it comes to songs, we only want to show the month and year. This
            // cannot be done with DateUtils due to it's dynamic nature, so instead
            // it's done with the built-in date formatter. Since the legacy date API
            // is awful, we only use instant and limit it to Android 8 onwards.
            temporal
                .atStartOfDay(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault()))
        } else {
            resolveYear(context)
        }

    /** Resolve the year field in a way suitable for the UI. */
    private fun resolveYear(context: Context) = context.getString(R.string.fmt_number, year)

    override fun hashCode() = tokens.hashCode()

    override fun equals(other: Any?) = other is Date && tokens == other.tokens

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
        append(year.toFixedString(4))
        append("-${(month ?: 1).toFixedString(2)}")
        append("-${(day ?: 1).toFixedString(2)}")
        append("T${(hour ?: 0).toFixedString(2)}")
        append(":${(minute ?: 0).toFixedString(2)}")
        append(":${(second ?: 0).toFixedString(2)}")
        return this.append('Z')
    }

    private fun Int.toFixedString(len: Int) = toString().padStart(len, '0').substring(0 until len)

    companion object {
        private val ISO8601_REGEX =
            Regex(
                """^(\d{4,})([-.](\d{2})([-.](\d{2})([T ](\d{2})([:.](\d{2})([:.](\d{2})(Z)?)?)?)?)?)?$""")

        fun from(year: Int) = fromTokens(listOf(year))

        fun from(year: Int, month: Int, day: Int) = fromTokens(listOf(year, month, day))

        fun from(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
            fromTokens(listOf(year, month, day, hour, minute))

        fun from(timestamp: String): Date? {
            val groups =
                (ISO8601_REGEX.matchEntire(timestamp) ?: return null)
                    .groupValues
                    .mapIndexedNotNull { index, s -> if (index % 2 != 0) s.toIntOrNull() else null }

            return fromTokens(groups)
        }

        private fun fromTokens(tokens: List<Int>): Date? {
            val out = mutableListOf<Int>()
            validateTokens(tokens, out)
            if (out.isEmpty()) {
                return null
            }

            return Date(out)
        }

        private fun validateTokens(src: List<Int>, dst: MutableList<Int>) {
            dst.add(src.getOrNull(0)?.nonZeroOrNull() ?: return)
            dst.add(src.getOrNull(1)?.inRangeOrNull(1..12) ?: return)
            dst.add(src.getOrNull(2)?.inRangeOrNull(1..31) ?: return)
            dst.add(src.getOrNull(3)?.inRangeOrNull(0..23) ?: return)
            dst.add(src.getOrNull(4)?.inRangeOrNull(0..59) ?: return)
            dst.add(src.getOrNull(5)?.inRangeOrNull(0..59) ?: return)
        }
    }
}

/**
 * Represents the type of release a particular album is.
 *
 * This can be used to differentiate between album sub-types like Singles, EPs, Compilations, and
 * others. Internally, it operates on a reduced version of the MusicBrainz release type
 * specification. It can be extended if there is demand.
 *
 * @author OxygenCobalt
 */
sealed class ReleaseType {
    abstract val refinement: Refinement?
    abstract val stringRes: Int

    data class Album(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_album
                    Refinement.LIVE -> R.string.lbl_album_live
                    Refinement.REMIX -> R.string.lbl_album_remix
                }
    }

    data class EP(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_ep
                    Refinement.LIVE -> R.string.lbl_ep_live
                    Refinement.REMIX -> R.string.lbl_ep_remix
                }
    }

    data class Single(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_single
                    Refinement.LIVE -> R.string.lbl_single_live
                    Refinement.REMIX -> R.string.lbl_single_remix
                }
    }

    data class Compilation(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_compilation
                    Refinement.LIVE -> R.string.lbl_compilation_live
                    Refinement.REMIX -> R.string.lbl_compilation_remix
                }
    }

    object Soundtrack : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_soundtrack
    }

    object Mix : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_mix
    }

    object Mixtape : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_mixtape
    }

    /**
     * Roughly analogous to the MusicBrainz "live" and "remix" secondary types. Unlike the main
     * types, these only modify an existing, primary type. They are not implemented for secondary
     * types, however they may be expanded to compilations in the future.
     */
    enum class Refinement {
        LIVE,
        REMIX
    }

    companion object {
        // Note: The parsing code is extremely clever in order to reduce duplication. It's
        // better just to read the specification behind release types than follow this code.

        fun parse(types: List<String>): ReleaseType? {
            val primary = types.getOrNull(0) ?: return null

            // Primary types should be the first one in sequence. The spec makes no mention of
            // whether primary types are a pre-requisite for secondary types, so we assume that
            // it isn't. There are technically two other types, but those are unrelated to music
            // and thus we don't support them.
            return when {
                primary.equals("album", true) -> types.parseSecondaryTypes(1) { Album(it) }
                primary.equals("ep", true) -> types.parseSecondaryTypes(1) { EP(it) }
                primary.equals("single", true) -> types.parseSecondaryTypes(1) { Single(it) }
                else -> types.parseSecondaryTypes(0) { Album(it) }
            }
        }

        private inline fun List<String>.parseSecondaryTypes(
            secondaryIdx: Int,
            convertRefinement: (Refinement?) -> ReleaseType
        ): ReleaseType {
            val secondary = getOrNull(secondaryIdx)

            return if (secondary.equals("compilation", true)) {
                // Secondary type is a compilation, actually parse the third type
                // and put that into a compilation if needed.
                parseSecondaryTypeImpl(getOrNull(secondaryIdx + 1)) { Compilation(it) }
            } else {
                // Secondary type is a plain value, use the original values given.
                parseSecondaryTypeImpl(secondary, convertRefinement)
            }
        }

        private inline fun parseSecondaryTypeImpl(
            type: String?,
            convertRefinement: (Refinement?) -> ReleaseType
        ) =
            when {
                // Parse all the types that have no children
                type.equals("soundtrack", true) -> Soundtrack
                type.equals("mixtape/street", true) -> Mixtape
                type.equals("dj-mix", true) -> Mix
                type.equals("live", true) -> convertRefinement(Refinement.LIVE)
                type.equals("remix", true) -> convertRefinement(Refinement.REMIX)
                else -> convertRefinement(null)
            }
    }
}
