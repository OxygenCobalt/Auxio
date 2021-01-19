package org.oxycblt.auxio.music.processing

import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Song

/**
 * Object responsible for creating [Artist]s from [Album]s and generally sorting everything.
 */
class MusicSorter(
    val songs: MutableList<Song>,
    val albums: MutableList<Album>,
) {
    val artists = mutableListOf<Artist>()

    fun sort() {
        albums.forEach {
            groupSongsIntoAlbum(it)
        }

        createArtistsFromAlbums(albums)
    }

    private fun groupSongsIntoAlbum(album: Album) {
        album.applySongs(songs.filter { it.albumId == album.id })
    }

    private fun createArtistsFromAlbums(albums: List<Album>) {
        val groupedAlbums = albums.groupBy { it.artistName }

        groupedAlbums.forEach {
            artists.add(
                // Min value is deliberately used to prevent conflicts with the MediaStore
                // album & artist IDs. Shouldnt conflict with other negative IDs unless there
                // are ~2.147 billion artists.
                Artist(
                    id = (artists.size + Int.MIN_VALUE).toLong(),
                    name = it.key,
                    albums = it.value
                )
            )
        }
    }
}
