package org.oxycblt.auxio.music

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist

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

// Convert legacy ID3 genres to a named genre
fun String.toNamedGenre(): String {
    // Strip the genres of any parentheses, and convert it to an int
    val intGenre = this.filterNot {
        PAREN_FILTER.indexOf(it) > -1
    }.toInt()

    return ID3_GENRES.getOrNull(intGenre) ?: ""
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

// Format the amount of songs in an album
@BindingAdapter("songCount")
fun TextView.getAlbumSongs(album: Album) {
    text = context.resources.getQuantityString(
        R.plurals.format_song_count, album.numSongs, album.numSongs
    )
}

@BindingAdapter("artistCounts")
fun TextView.bindArtistCounts(artist: Artist) {
    val albums = context.resources.getQuantityString(
        R.plurals.format_albums, artist.numAlbums, artist.numAlbums
    )
    val songs = context.resources.getQuantityString(
        R.plurals.format_song_count, artist.numSongs, artist.numSongs
    )

    text = context.getString(R.string.format_double_counts, albums, songs)
}

@BindingAdapter("artistGenre")
fun TextView.getArtistGenre(artist: Artist) {
    // If the artist has more than one genre, pick the most used one.
    // TODO: Add an option to display all genres.
    val genre: String = if (artist.genres.size > 1) {
        val genres = artist.genres.groupBy { it.name }

        genres.keys.sortedByDescending { genres[it]?.size }[0]
    } else {
        artist.genres[0].name
    }

    text = genre
}
