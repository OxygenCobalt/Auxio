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
 
package org.oxycblt.auxio.music.indexer

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.text.isDigitsOnly

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

/** Converts a [Long] Audio ID into a URI to that particular audio file. */
val Long.audioUri: Uri
    get() =
        ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, requireNotNull(this))

/**
 * Parse out the number field from an NN/TT string that is typically found in DISC_NUMBER and
 * CD_TRACK_NUMBER.
 */
val String.no: Int?
    get() = split('/', limit = 2).getOrNull(0)?.toIntOrNull()

/**
 * Parse out the year field from a (presumably) ISO-8601-like date. This differs across tag formats
 * and has no real consistency, but it's assumed that most will format granular dates as YYYY-MM-DD
 * (...) and thus we can parse the year out by splitting at the first -.
 */
val String.iso8601year: Int?
    get() = split('-', limit = 2).getOrNull(0)?.toIntOrNull()

/**
 * Slice a string so that any preceding articles like The/A(n) are truncated. This is hilariously
 * anglo-centric, but it's also a bit of an expected feature in music players, so we implement it
 * anyway.
 */
val String.withoutArticle: String
    get() {
        if (length > 5 && startsWith("the ", ignoreCase = true)) {
            return slice(4..lastIndex)
        }

        if (length > 4 && startsWith("an ", ignoreCase = true)) {
            return slice(3..lastIndex)
        }

        if (length > 3 && startsWith("a ", ignoreCase = true)) {
            return slice(2..lastIndex)
        }

        return this
    }

/**
 * Decodes the genre name from an ID3(v2) constant. See [genreConstantTable] for the genre constant
 * map that Auxio uses.
 */
val String.id3v2GenreName: String
    get() {
        if (isDigitsOnly()) {
            // ID3v1, just parse as an integer
            return genreConstantTable.getOrNull(toInt()) ?: this
        }

        if (startsWith('(') && endsWith(')')) {
            // ID3v2.3/ID3v2.4, parse out the parentheses and get the integer
            // Any genres formatted as "(CHARS)" will be ignored.

            // TODO: Technically, the spec for genres is far more complex here. Perhaps we
            //  should copy mutagen's implementation?
            //  https://github.com/quodlibet/mutagen/blob/master/mutagen/id3/_frames.py

            val genreInt = substring(1 until lastIndex).toIntOrNull()
            if (genreInt != null) {
                return genreConstantTable.getOrNull(genreInt) ?: this
            }
        }

        // Current name is fine.
        return this
    }

/**
 * A complete table of all the constant genre values for ID3(v2), including non-standard extensions.
 */
private val genreConstantTable =
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
        "Psybient")
