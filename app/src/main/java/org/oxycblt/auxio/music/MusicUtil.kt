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

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.text.isDigitsOnly
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.nonZeroOrNull

/** Shortcut for making a [ContentResolver] query with less superfluous arguments. */
fun ContentResolver.queryCursor(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null
) = query(uri, projection, selector, args, null)

/** Shortcut for making a [ContentResolver] query and using the particular cursor with [use]. */
fun <R> ContentResolver.useQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null,
    block: (Cursor) -> R
): R? = queryCursor(uri, projection, selector, args)?.use(block)

/**
 * For some reason the album cover URI namespace does not have a member in [MediaStore], but it
 * still works since at least API 21.
 */
private val EXTERNAL_ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")

/** Converts a [Long] Audio ID into a URI to that particular audio file. */
val Long.audioUri: Uri
    get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this)

/** Converts a [Long] Album ID into a URI pointing to MediaStore-cached album art. */
val Long.albumCoverUri: Uri
    get() = ContentUris.withAppendedId(EXTERNAL_ALBUM_ART_URI, this)

/**
 * Parse out the track number field as if the given Int is formatted as DTTT, where D Is the disc
 * and T is the track number. Values of zero will be ignored under the assumption that they are
 * invalid.
 */
fun Int.unpackTrackNo() = mod(1000).nonZeroOrNull()

/**
 * Parse out the disc number field as if the given Int is formatted as DTTT, where D Is the disc and
 * T is the track number. Values of zero will be ignored under the assumption that they are invalid.
 */
fun Int.unpackDiscNo() = div(1000).nonZeroOrNull()

/**
 * Parse out the number field from an NN/TT string that is typically found in DISC_NUMBER and
 * CD_TRACK_NUMBER. Values of zero will be ignored under the assumption that they are invalid.
 */
fun String.parsePositionNum() = split('/', limit = 2)[0].toIntOrNull()?.nonZeroOrNull()

/** Parse a plain year from the field into a [Date]. */
fun String.parseYear() = toIntOrNull()?.let(Date::from)

/** Parse an ISO-8601 time-stamp from this field into a [Date]. */
fun String.parseTimestamp() = Date.from(this)

/** Shortcut to resolve a year from a nullable date. Will return "No Date" if it is null. */
fun Date?.resolveYear(context: Context) =
    this?.resolveYear(context) ?: context.getString(R.string.def_date)

/**
 * Slice a string so that any preceding articles like The/A(n) are truncated. This is hilariously
 * anglo-centric, but it's also a bit of an expected feature in music players, so we implement it
 * anyway.
 */
fun String.parseSortName() =
    when {
        length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
        length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
        length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
        else -> this
    }

/** Shortcut to parse an [ReleaseType] from a string */
fun String.parseReleaseType() = ReleaseType.parse(this)

/** Shortcut to parse a [ReleaseType] from a list of strings */
fun List<String>.parseReleaseType() = ReleaseType.parse(this)

/**
 * Decodes the genre name from an ID3(v2) constant. See [GENRE_TABLE] for the genre constant map
 * that Auxio uses.
 */
fun String.parseId3GenreName() = parseId3v1Genre() ?: parseId3v2Genre() ?: this

private fun String.parseId3v1Genre(): String? =
    when {
        // ID3v1 genres are a plain integer value without formatting, so in that case
        // try to index the genre table with such.
        isDigitsOnly() -> GENRE_TABLE.getOrNull(toInt())

        // CR and RX are not technically ID3v1, but are formatted similarly to a plain number.
        this == "CR" -> "Cover"
        this == "RX" -> "Remix"

        // Current name is fine.
        else -> null
    }

private fun String.parseId3v2Genre(): String? {
    val groups = (GENRE_RE.matchEntire(this) ?: return null).groupValues
    val genres = mutableSetOf<String>()

    // ID3v2 genres are far more complex and require string grokking to properly implement.
    // You can read the spec for it here: https://id3.org/id3v2.3.0#TCON
    // This implementation in particular is based off Mutagen's genre parser.

    // Case 1: Genre IDs in the format (INT|RX|CR). If these exist, parse them as
    // ID3v1 tags.
    val genreIds = groups.getOrNull(1)
    if (genreIds != null && genreIds.isNotEmpty()) {
        val ids = genreIds.substring(1, genreIds.lastIndex).split(")(")
        for (id in ids) {
            id.parseId3v1Genre()?.let(genres::add)
        }
    }

    // Case 2: Genre names as a normal string. The only case we have to look out for are
    // escaped strings formatted as ((genre).
    val genreName = groups.getOrNull(3)
    if (genreName != null && genreName.isNotEmpty()) {
        if (genreName.startsWith("((")) {
            genres.add(genreName.substring(1))
        } else {
            genres.add(genreName)
        }
    }

    return genres.joinToString(separator = ", ").ifEmpty { null }
}

/** Regex that implements matching for ID3v2's genre format. */
private val GENRE_RE = Regex("((?:\\(([0-9]+|RX|CR)\\))*)(.+)?")

/**
 * A complete table of all the constant genre values for ID3(v2), including non-standard extensions.
 * Note that we do not translate these, as that greatly increases technical complexity.
 */
private val GENRE_TABLE =
    arrayOf(
        // ID3 Standard
        "Blues",
        "Classic Rock",
        "Country",
        "Dance",
        "Disco",
        "Funk",
        "Grunge",
        "Hip-Hop",
        "Jazz",
        "Metal",
        "New Age",
        "Oldies",
        "Other",
        "Pop",
        "R&B",
        "Rap",
        "Reggae",
        "Rock",
        "Techno",
        "Industrial",
        "Alternative",
        "Ska",
        "Death Metal",
        "Pranks",
        "Soundtrack",
        "Euro-Techno",
        "Ambient",
        "Trip-Hop",
        "Vocal",
        "Jazz+Funk",
        "Fusion",
        "Trance",
        "Classical",
        "Instrumental",
        "Acid",
        "House",
        "Game",
        "Sound Clip",
        "Gospel",
        "Noise",
        "AlternRock",
        "Bass",
        "Soul",
        "Punk",
        "Space",
        "Meditative",
        "Instrumental Pop",
        "Instrumental Rock",
        "Ethnic",
        "Gothic",
        "Darkwave",
        "Techno-Industrial",
        "Electronic",
        "Pop-Folk",
        "Eurodance",
        "Dream",
        "Southern Rock",
        "Comedy",
        "Cult",
        "Gangsta",
        "Top 40",
        "Christian Rap",
        "Pop/Funk",
        "Jungle",
        "Native American",
        "Cabaret",
        "New Wave",
        "Psychadelic",
        "Rave",
        "Showtunes",
        "Trailer",
        "Lo-Fi",
        "Tribal",
        "Acid Punk",
        "Acid Jazz",
        "Polka",
        "Retro",
        "Musical",
        "Rock & Roll",
        "Hard Rock",

        // Winamp extensions, more or less a de-facto standard
        "Folk",
        "Folk-Rock",
        "National Folk",
        "Swing",
        "Fast Fusion",
        "Bebob",
        "Latin",
        "Revival",
        "Celtic",
        "Bluegrass",
        "Avantgarde",
        "Gothic Rock",
        "Progressive Rock",
        "Psychedelic Rock",
        "Symphonic Rock",
        "Slow Rock",
        "Big Band",
        "Chorus",
        "Easy Listening",
        "Acoustic",
        "Humour",
        "Speech",
        "Chanson",
        "Opera",
        "Chamber Music",
        "Sonata",
        "Symphony",
        "Booty Bass",
        "Primus",
        "Porn Groove",
        "Satire",
        "Slow Jam",
        "Club",
        "Tango",
        "Samba",
        "Folklore",
        "Ballad",
        "Power Ballad",
        "Rhythmic Soul",
        "Freestyle",
        "Duet",
        "Punk Rock",
        "Drum Solo",
        "A capella",
        "Euro-House",
        "Dance Hall",
        "Goa",
        "Drum & Bass",
        "Club-House",
        "Hardcore",
        "Terror",
        "Indie",
        "Britpop",
        "Negerpunk",
        "Polsk Punk",
        "Beat",
        "Christian Gangsta",
        "Heavy Metal",
        "Black Metal",
        "Crossover",
        "Contemporary Christian",
        "Christian Rock",
        "Merengue",
        "Salsa",
        "Thrash Metal",
        "Anime",
        "JPop",
        "Synthpop",

        // Winamp 5.6+ extensions, also used by EasyTAG.
        // I only include this because post-rock is a based genre and deserves a slot.
        "Abstract",
        "Art Rock",
        "Baroque",
        "Bhangra",
        "Big Beat",
        "Breakbeat",
        "Chillout",
        "Downtempo",
        "Dub",
        "EBM",
        "Eclectic",
        "Electro",
        "Electroclash",
        "Emo",
        "Experimental",
        "Garage",
        "Global",
        "IDM",
        "Illbient",
        "Industro-Goth",
        "Jam Band",
        "Krautrock",
        "Leftfield",
        "Lounge",
        "Math Rock",
        "New Romantic",
        "Nu-Breakz",
        "Post-Punk",
        "Post-Rock",
        "Psytrance",
        "Shoegaze",
        "Space Rock",
        "Trop Rock",
        "World Music",
        "Neoclassical",
        "Audiobook",
        "Audio Theatre",
        "Neue Deutsche Welle",
        "Podcast",
        "Indie Rock",
        "G-Funk",
        "Dubstep",
        "Garage Rock",
        "Psybient",

        // Auxio's extensions (Future garage is also based and deserves a slot)
        "Future Garage")
