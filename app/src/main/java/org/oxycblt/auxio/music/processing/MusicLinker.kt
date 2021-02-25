package org.oxycblt.auxio.music.processing

import android.content.Context
import android.provider.MediaStore.Audio.Genres
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

/**
 * Object that links music data, such as grouping songs into their albums & genres and creating
 * artists out of the albums.
 * @author OxygenCobalt
 */
class MusicLinker(
    private val context: Context,
    val songs: MutableList<Song>,
    val albums: MutableList<Album>,
    val genres: MutableList<Genre>
) {
    private val resolver = context.contentResolver
    val artists = mutableListOf<Artist>()

    /**
     * Begin the linking process.
     * Modified models are pushed to [songs], [albums], [artists], and [genres]
     */
    fun link() {
        linkAlbums()
        linkArtists()
        linkGenres()
    }

    private fun linkAlbums() {
        logD("Linking albums")

        // Group up songs by their album ids and then link them with their albums
        val songsByAlbum = songs.groupBy { it.albumId }
        val unknownAlbum = Album(
            name = context.getString(R.string.placeholder_album),
            artistName = context.getString(R.string.placeholder_artist)
        )

        songsByAlbum.forEach { entry ->
            (albums.find { it.id == entry.key } ?: unknownAlbum).linkSongs(entry.value)
        }

        // If something goes horribly wrong and somehow songs are still not linked up by the
        // album id, just throw them into an unknown album.
        if (unknownAlbum.songs.isNotEmpty()) {
            albums.add(unknownAlbum)
        }
    }

    private fun linkArtists() {
        logD("Linking artists")

        // Group albums up by their artist name, should not result in any null-artist issues
        val albumsByArtist = albums.groupBy { it.artistName }

        albumsByArtist.forEach { entry ->
            artists.add(
                Artist(
                    id = (artists.size + Int.MIN_VALUE).toLong(),
                    name = entry.key, albums = entry.value
                )
            )
        }

        logD("Albums successfully linked into ${artists.size} artists")
    }

    private fun linkGenres() {
        logD("Linking genres")

        /*
         * Okay, I'm going to go on a bit of a tangent here because this bit of code infuriates me.
         *
         * In an ideal world I should just be able to write MediaStore.Media.Audio.GENRE
         * in the original song projection and then have it fetch the genre from the database, but
         * no, why would ANYONE do that? Instead, I have to manually iterate through each genre, get
         * A LIST OF SONGS FROM THEM, and then waste CPU cycles REPEATEDLY ITERATING through the
         * songs list to LINK EACH SONG WITH THEIR GENRE. Why is it this way? Nobody knows! Now this
         * quirk is immortalized and has to be replicated in all future iterations of this API! Yay!
         *
         * I hate this platform so much.
         */
        genres.forEach { genre ->
            val songCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(Genres.Members._ID),
                null, null, null
            )

            songCursor?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(Genres.Members._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)

                    songs.find { it.id == id }?.let { song ->
                        genre.linkSong(song)
                    }
                }
            }
        }

        // Any songs without genres will be thrown into an unknown genre
        val songsWithoutGenres = songs.filter { it.genre == null }

        if (songsWithoutGenres.isNotEmpty()) {
            val unknownGenre = Genre(name = context.getString(R.string.placeholder_genre))

            songsWithoutGenres.forEach { song ->
                unknownGenre.linkSong(song)
            }

            genres.add(unknownGenre)
        }
    }
}
