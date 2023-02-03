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

import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.util.nonZeroOrNull

/// --- GENERIC PARSING ---

/**
 * Parse a multi-value tag based on the user configuration. If the value is already composed of more
 * than one value, nothing is done. Otherwise, this function will attempt to split it based on the
 * user's separator preferences.
 * @param settings [MusicSettings] required to obtain user separator configuration.
 * @return A new list of one or more [String]s.
 */
fun List<String>.parseMultiValue(settings: MusicSettings) =
    if (size == 1) {
        first().maybeParseBySeparators(settings)
    } else {
        // Nothing to do.
        this
    }

/**
 * Split a [String] by the given selector, automatically handling escaped characters that satisfy
 * the selector.
 * @param selector A block that determines if the string should be split at a given character.
 * @return One or more [String]s split by the selector.
 */
inline fun String.splitEscaped(selector: (Char) -> Boolean): List<String> {
    val split = mutableListOf<String>()
    var currentString = ""
    var i = 0

    while (i < length) {
        val a = get(i)
        val b = getOrNull(i + 1)

        if (selector(a)) {
            // Non-escaped separator, split the string here, making sure any stray whitespace
            // is removed.
            split.add(currentString)
            currentString = ""
            i++
            continue
        }

        if (b != null && a == '\\' && selector(b)) {
            // Is an escaped character, add the non-escaped variant and skip two
            // characters to move on to the next one.
            currentString += b
            i += 2
        } else {
            // Non-escaped, increment normally.
            currentString += a
            i++
        }
    }

    if (currentString.isNotEmpty()) {
        // Had an in-progress split string that is now terminated, add it.
        split.add(currentString)
    }

    return split
}

/**
 * Fix trailing whitespace or blank contents in a [String].
 * @return A string with trailing whitespace remove,d or null if the [String] was all whitespace or
 * empty.
 */
fun String.correctWhitespace() = trim().ifBlank { null }

/**
 * Fix trailing whitespace or blank contents within a list of [String]s.
 * @return A list of non-blank strings with trailing whitespace removed.
 */
fun List<String>.correctWhitespace() = mapNotNull { it.correctWhitespace() }

/**
 * Attempt to parse a string by the user's separator preferences.
 * @param settings [MusicSettings] required to obtain user separator configuration.
 * @return A list of one or more [String]s that were split up by the user-defined separators.
 */
private fun String.maybeParseBySeparators(settings: MusicSettings): List<String> {
    // Get the separators the user desires. If null, there's nothing to do.
    return splitEscaped { settings.multiValueSeparators.contains(it) }.correctWhitespace()
}

/// --- ID3v2 PARSING ---

/**
 * Parse an ID3v2-style position + total [String] field. These fields consist of a number and an
 * (optional) total value delimited by a /.
 * @return The position value extracted from the string field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 * @see transformPositionField
 */
fun String.parseId3v2PositionField() =
    split('/', limit = 2).let {
        transformPositionField(it[0].toIntOrNull(), it.getOrNull(1)?.toIntOrNull())
    }

/**
 * Parse a vorbis-style position + total field. These fields consist of two fields for the position
 * and total numbers.
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 * @see transformPositionField
 */
fun parseVorbisPositionField(pos: String?, total: String?) =
    transformPositionField(pos?.toIntOrNull(), total?.toIntOrNull())

/**
 * Transform a raw position + total field into a position a way that tolerates placeholder values.
 * @param pos The position value, or null if not present.
 * @param total The total value, if not present.
 * @return The position value extracted from the field, or null if:
 * - The position could not be parsed
 * - The position was zeroed AND the total value was not present/zeroed
 */
fun transformPositionField(pos: Int?, total: Int?) =
    if (pos != null && (pos > 0 || (total?.nonZeroOrNull() != null))) {
        pos
    } else {
        null
    }

/**
 * Parse a multi-value genre name using ID3 rules. This will convert any ID3v1 integer
 * representations of genre fields into their named counterparts, and split up singular ID3v2-style
 * integer genre fields into one or more genres.
 * @param settings [MusicSettings] required to obtain user separator configuration.
 * @return A list of one or more genre names..
 */
fun List<String>.parseId3GenreNames(settings: MusicSettings) =
    if (size == 1) {
        first().parseId3MultiValueGenre(settings)
    } else {
        // Nothing to split, just map any ID3v1 genres to their name counterparts.
        map { it.parseId3v1Genre() ?: it }
    }

/**
 * Parse a single ID3v1/ID3v2 integer genre field into their named representations.
 * @param settings [MusicSettings] required to obtain user separator configuration.
 * @return A list of one or more genre names.
 */
private fun String.parseId3MultiValueGenre(settings: MusicSettings) =
    parseId3v1Genre()?.let { listOf(it) } ?: parseId3v2Genre() ?: maybeParseBySeparators(settings)

/**
 * Parse an ID3v1 integer genre field.
 * @return A named genre if the field is a valid integer, "Cover" or "Remix" if the field is
 * "CR"/"RX" respectively, and nothing if the field is not a valid ID3v1 integer genre.
 */
private fun String.parseId3v1Genre(): String? {
    // ID3v1 genres are a plain integer value without formatting, so in that case
    // try to index the genre table with such.
    val numeric =
        toIntOrNull()
        // Not a numeric value, try some other fixed values.
        ?: return when (this) {
                // CR and RX are not technically ID3v1, but are formatted similarly to a plain
                // number.
                "CR" -> "Cover"
                "RX" -> "Remix"
                else -> null
            }
    return GENRE_TABLE.getOrNull(numeric)
}

/**
 * A [Regex] that implements parsing for ID3v2's genre format. Derived from mutagen:
 * https://github.com/quodlibet/mutagen
 */
private val ID3V2_GENRE_RE = Regex("((?:\\((\\d+|RX|CR)\\))*)(.+)?")

/**
 * Parse an ID3v2 integer genre field, which has support for multiple genre values and combined
 * named/integer genres.
 * @return A list of one or more genres, or null if the field is not a valid ID3v2 integer genre.
 */
private fun String.parseId3v2Genre(): List<String>? {
    val groups = (ID3V2_GENRE_RE.matchEntire(this) ?: return null).groupValues
    val genres = mutableSetOf<String>()

    // ID3v2.3 genres are far more complex and require string grokking to properly implement.
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

    // If this parsing task didn't change anything, move on.
    if (genres.size == 1 && genres.first() == this) {
        return null
    }

    return genres.toList()
}

/**
 * A table of the "conventional" mapping between ID3v1 integer genres and their named counterparts.
 * Includes non-standard extensions.
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

        // Winamp 5.6+ extensions, also used by EasyTAG. Not common, but post-rock is a good
        // genre and should be included in the mapping.
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

        // Auxio's extensions, added because Future Garage is also a good genre.
        "Future Garage")
