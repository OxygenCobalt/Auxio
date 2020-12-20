package org.oxycblt.auxio.music.processing

import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Song

class MusicSorter(
    val songs: MutableList<Song>,
    val albums: MutableList<Album>
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
            logD(it.key)
            artists.add(
                Artist(id = artists.size.toLong(), name = it.key, albums = it.value)
            )
        }
    }
}
