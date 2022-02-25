/*
 * Copyright (c) 2021 Auxio Project
 * MusicUtils.kt is part of Auxio.
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
import android.text.format.DateUtils
import android.widget.TextView
import androidx.core.text.isDigitsOnly
import androidx.databinding.BindingAdapter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A complete array of all the hardcoded genre values for ID3(v2), contains standard genres and
 * winamp extensions.
 */
private val ID3_GENRES = arrayOf(
    // ID3 Standard
    "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz",
    "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno",
    "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno",
    "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental",
    "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk",
    "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
    "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy",
    "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American",
    "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal",
    "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock",

    // Winamp Extensions
    "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival",
    "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock",
    "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour",
    "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus",
    "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad",
    "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella",
    "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie",
    "Britpop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal",
    "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal",
    "Anime", "JPop", "Synthpop",

    // Winamp 5.6+ extensions, used by EasyTAG and friends
    "Abstract", "Art Rock", "Baroque", "Bhangra", "Big Beat", "Breakbeat", "Chillout", "Downtempo",
    "Dub", "EBM", "Eclectic", "Electro", "Electroclash", "Emo", "Experimental", "Garage", "Global",
    "IDM", "Illbient", "Industro-Goth", "Jam Band", "Krautrock", "Leftfield", "Lounge", "Math Rock", // S I X T Y   F I V E
    "New Romantic", "Nu-Breakz", "Post-Punk", "Post-Rock", "Psytrance", "Shoegaze", "Space Rock",
    "Trop Rock", "World Music", "Neoclassical", "Audiobook", "Audio Theatre", "Neue Deutsche Welle",
    "Podcast", "Indie Rock", "G-Funk", "Dubstep", "Garage Rock", "Psybient"
)

// --- EXTENSION FUNCTIONS ---

/**
 * Convert legacy int-based ID3 genres to their human-readable genre
 * @return The named genre for this legacy genre, null if there is no need to parse it
 * or if the genre is invalid.
 */
fun String.getGenreNameCompat(): String? {
    if (isDigitsOnly()) {
        // ID3v1, just parse as an integer
        return ID3_GENRES.getOrNull(toInt())
    }

    if (startsWith('(') && endsWith(')')) {
        // ID3v2.3/ID3v2.4, parse out the parentheses and get the integer
        // Any genres formatted as "(CHARS)" will be ignored.
        val genreInt = substring(1 until lastIndex).toIntOrNull()

        if (genreInt != null) {
            return ID3_GENRES.getOrNull(genreInt)
        }
    }

    // Current name is fine.
    return null
}

/**
 * Convert a [Long] of seconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then
 * --:-- will be returned if the second value is 0.
 */
fun Long.toDuration(isElapsed: Boolean): String {
    if (!isElapsed && this == 0L) {
        logD("Non-elapsed duration is zero, using --:--")
        return "--:--"
    }

    var durationString = DateUtils.formatElapsedTime(this)

    // If the duration begins with a excess zero [e.g 01:42], then cut it off.
    if (durationString[0] == '0') {
        durationString = durationString.slice(1 until durationString.length)
    }

    return durationString
}

fun Int.toDate(context: Context): String {
    return if (this == 0) {
        context.getString(R.string.def_date)
    } else {
        toString()
    }
}

// --- BINDING ADAPTERS ---

@BindingAdapter("songInfo")
fun TextView.bindSongInfo(song: Song?) {
    if (song == null) {
        logW("Song was null, not applying info")
        return
    }

    text = song.resolvedArtistName
}

@BindingAdapter("albumInfo")
fun TextView.bindAlbumInfo(album: Album?) {
    if (album == null) {
        logW("Album was null, not applying info")
        return
    }

    text = album.resolvedArtistName
}

@BindingAdapter("artistInfo")
fun TextView.bindArtistInfo(artist: Artist?) {
    if (artist == null) {
        logW("Artist was null, not applying info")
        return
    }

    text = context.getString(
        R.string.fmt_counts,
        context.getPluralSafe(R.plurals.fmt_album_count, artist.albums.size),
        context.getPluralSafe(R.plurals.fmt_song_count, artist.songs.size)
    )
}

@BindingAdapter("genreInfo")
fun TextView.bindGenreInfo(genre: Genre?) {
    if (genre == null) {
        logW("Genre was null, not applying info")
        return
    }

    text = context.getPluralSafe(R.plurals.fmt_song_count, genre.songs.size)
}
