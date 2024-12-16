/*
 * Copyright (c) 2024 Auxio Project
 * MusicUtil.kt is part of Auxio.
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
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.concatLocalized
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.Disc
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.Placeholder
import org.oxycblt.musikr.tag.ReleaseType
import org.oxycblt.musikr.tag.ReleaseType.Refinement
import timber.log.Timber

fun Name.resolve(context: Context) =
    when (this) {
        is Name.Known -> raw
        is Name.Unknown ->
            when (placeholder) {
                Placeholder.ALBUM -> context.getString(R.string.def_album)
                Placeholder.ARTIST -> context.getString(R.string.def_artist)
                Placeholder.GENRE -> context.getString(R.string.def_genre)
            }
    }

/**
 * Run [Name.resolve] on each instance in the given list and concatenate them into a [String] in a
 * localized manner.
 *
 * @param context [Context] required
 * @return A concatenated string.
 */
fun <T : Music> List<T>.resolveNames(context: Context) =
    concatLocalized(context) { it.name.resolve(context) }

/**
 * Returns if [Music.name] matches for each item in a list. Useful for scenarios where the display
 * information of an item must be compared without a context.
 *
 * @param other The list of items to compare to.
 * @return True if they are the same (by [Music.name]), false otherwise.
 */
fun <T : Music> List<T>.areNamesTheSame(other: List<T>): Boolean {
    for (i in 0 until max(size, other.size)) {
        val a = getOrNull(i) ?: return false
        val b = other.getOrNull(i) ?: return false
        if (a.name != b.name) {
            return false
        }
    }

    return true
}

/**
 * Resolve this instance into a human-readable date.
 *
 * @param context [Context] required to get human-readable names.
 * @return If the [Date] has a valid month and year value, a more fine-grained date (ex. "Jan 2020")
 *   will be returned. Otherwise, a plain year value (ex. "2020") is returned. Dates will be
 *   properly localized.
 */
fun Date.resolve(context: Context) =
    // Unable to create fine-grained date, just format as a year.
    month?.let { resolveFineGrained() } ?: context.getString(R.string.fmt_number, year)

private fun Date.resolveFineGrained(): String? {
    // We can't directly load a date with our own
    val format = (SimpleDateFormat.getDateInstance() as SimpleDateFormat)
    format.applyPattern("yyyy-MM")
    val date =
        try {
            format.parse("$year-$month")
        } catch (e: ParseException) {
            Timber.e("Unable to parse fine-grained date: $e")
            return null
        }

    // Reformat as a readable month and year
    format.applyPattern("MMM yyyy")
    return format.format(date)
}

fun Disc?.resolve(context: Context) =
    this?.run { context.getString(R.string.fmt_disc_no, number) }
        ?: context.getString(R.string.def_disc)

/**
 * Resolve this instance into a human-readable date range.
 *
 * @param context [Context] required to get human-readable names.
 * @return If the date has a maximum value, then a `min - max` formatted string will be returned
 *   with the formatted [Date]s of the minimum and maximum dates respectively. Otherwise, the
 *   formatted name of the minimum [Date] will be returned.
 */
fun Date.Range.resolve(context: Context) =
    if (min != max) {
        context.getString(R.string.fmt_date_range, min.resolve(context), max.resolve(context))
    } else {
        min.resolve(context)
    }

fun ReleaseType.resolve(context: Context) =
    when (this) {
        is ReleaseType.Album ->
            when (refinement) {
                null -> context.getString(R.string.lbl_album)
                Refinement.LIVE -> context.getString(R.string.lbl_album_live)
                Refinement.REMIX -> context.getString(R.string.lbl_album_remix)
            }
        is ReleaseType.EP ->
            when (refinement) {
                null -> context.getString(R.string.lbl_ep)
                Refinement.LIVE -> context.getString(R.string.lbl_ep_live)
                Refinement.REMIX -> context.getString(R.string.lbl_ep_remix)
            }
        is ReleaseType.Single ->
            when (refinement) {
                null -> context.getString(R.string.lbl_single)
                Refinement.LIVE -> context.getString(R.string.lbl_single_live)
                Refinement.REMIX -> context.getString(R.string.lbl_single_remix)
            }
        is ReleaseType.Compilation ->
            when (refinement) {
                null -> context.getString(R.string.lbl_compilation)
                Refinement.LIVE -> context.getString(R.string.lbl_compilation_live)
                Refinement.REMIX -> context.getString(R.string.lbl_compilation_remix)
            }
        is ReleaseType.Soundtrack -> context.getString(R.string.lbl_soundtrack)
        is ReleaseType.Mix -> context.getString(R.string.lbl_mix)
        is ReleaseType.Mixtape -> context.getString(R.string.lbl_mixtape)
        is ReleaseType.Demo -> context.getString(R.string.lbl_demo)
    }
