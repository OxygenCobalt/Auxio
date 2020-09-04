package org.oxycblt.auxio.music

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

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

// Get the cover art
@BindingAdapter("coverArt")
fun ImageView.getCoverArt(any: Any) {
    val uri = when (any) {
        is Song -> any.album.coverUri
        is Album -> any.coverUri

        // TODO: Artist images

        else -> Uri.EMPTY
    }

    load(uri) {
        crossfade(true)
        placeholder(android.R.color.transparent)
        error(R.drawable.ic_music)
    }
}

fun Int.toSongCount(): Int {
    return if (this < 2) {
        R.string.label_single_song
    } else {
        R.string.format_multi_song_count
    }
}

fun Int.toAlbumCount(): Int {
    return if (this < 2) {
        R.string.label_single_album
    } else {
        R.string.format_album_count
    }
}

// Format the amount of songs in an album
@BindingAdapter("songCount")
fun TextView.getAlbumSongs(album: Album) {
    text = context.getString(album.numSongs.toSongCount(), album.numSongs)
}

@BindingAdapter("albumSongCount")
fun TextView.getSongAlbumCount(artist: Artist) {
    val albums = context.getString(artist.numAlbums.toAlbumCount(), artist.numAlbums)
    val songs = context.getString(artist.numSongs.toSongCount(), artist.numSongs)

    text = context.getString(R.string.format_combined_song_album, albums, songs)
}
