package org.oxycblt.auxio.music

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateUtils
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.oxycblt.auxio.R

// List of ID3 genres + Winamp extensions, each index corresponds to their int value.
// There are a lot more int-genre extensions as far as Im aware, but this works for most cases.
private val ID3_GENRES = arrayOf(
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

    // Winamp extensions
    "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival",
    "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock",
    "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour",
    "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus",
    "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad",
    "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella",
    "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie",
    "Britpop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal",
    "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal",
    "Anime", "JPop", "Synthpop"
)

const val PAREN_FILTER = "()"

// --- EXTENSION FUNCTIONS ---

// Convert legacy ID3 genres to a named genre
fun String.toNamedGenre(): String? {
    // Strip the genres of any parentheses, and convert it to an int
    val intGenre = this.filterNot {
        PAREN_FILTER.indexOf(it) > -1
    }.toInt()

    // If the conversion fails [Due to the genre using an extension that Auxio doesn't have],
    // then return null.
    return ID3_GENRES.getOrNull(intGenre)
}

// Convert a song to its URI
fun Long.toURI(): Uri {
    return ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        this
    )
}

// Convert an albums ID into its album art URI
fun Long.toAlbumArtURI(): Uri {
    return ContentUris.withAppendedId(
        Uri.parse("content://media/external/audio/albumart"),
        this
    )
}

// Convert seconds into its string duration
fun Long.toDuration(): String {
    var durationString = DateUtils.formatElapsedTime(this)

    // If the duration begins with a excess zero [e.g 01:42], then cut it off.
    if (durationString[0] == '0') {
        durationString = durationString.slice(1 until durationString.length)
    }

    return durationString
}

// --- BINDING ADAPTERS ---

@BindingAdapter("genreCounts")
fun TextView.bindGenreCounts(genre: Genre) {
    val artists = context.resources.getQuantityString(
        R.plurals.format_artist_count, genre.numArtists, genre.numArtists
    )
    val albums = context.resources.getQuantityString(
        R.plurals.format_album_count, genre.numAlbums, genre.numAlbums
    )

    text = context.getString(R.string.format_double_counts, artists, albums)
}

// Get the artist genre.
// TODO: Add option to list all genres
@BindingAdapter("artistGenre")
fun TextView.bindArtistGenre(artist: Artist) {
    text = if (artist.genres.isNotEmpty()) {
        artist.genres[0].name
    } else {
        context.getString(R.string.placeholder_genre)
    }
}

// Get the artist counts
@BindingAdapter("artistCounts")
fun TextView.bindArtistCounts(artist: Artist) {
    val albums = context.resources.getQuantityString(
        R.plurals.format_album_count, artist.numAlbums, artist.numAlbums
    )
    val songs = context.resources.getQuantityString(
        R.plurals.format_song_count, artist.numSongs, artist.numSongs
    )

    text = context.getString(R.string.format_double_counts, albums, songs)
}

// Get a bunch of miscellaneous album information [Year, Songs, Duration] and combine them
@BindingAdapter("albumDetails")
fun TextView.bindAlbumDetails(album: Album) {
    text = context.getString(
        R.string.format_double_info,
        album.year.toString(),
        context.resources.getQuantityString(
            R.plurals.format_song_count,
            album.numSongs, album.numSongs
        ),
        album.totalDuration
    )
}

@BindingAdapter("albumSongs")
// Format the amount of songs in an album
fun TextView.bindAlbumSongs(album: Album) {
    text = context.resources.getQuantityString(
        R.plurals.format_song_count, album.numSongs, album.numSongs
    )
}
