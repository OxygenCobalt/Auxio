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

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD

object Indexer {
    fun index(context: Context): MusicStore.Library? {
        // Establish the backend to use when initially loading songs.
        val backend =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30MediaStoreBackend()
                else -> Api21MediaStoreBackend()
            }

        val songs = buildSongs(context, backend)
        if (songs.isEmpty()) return null

        val albums = buildAlbums(songs)
        val artists = buildArtists(albums)
        val genres = buildGenres(songs)

        // Sanity check: Ensure that all songs are linked up to albums/artists/genres.
        for (song in songs) {
            if (song._isMissingAlbum || song._isMissingArtist || song._isMissingGenre) {
                throw IllegalStateException(
                    "Found malformed song: ${song.rawName} [" +
                        "album: ${!song._isMissingAlbum} " +
                        "artist: ${!song._isMissingArtist} " +
                        "genre: ${!song._isMissingGenre}]")
            }
        }

        return MusicStore.Library(genres, artists, albums, songs)
    }

    /**
     * Does the initial query over the song database, including excluded directory checks. The songs
     * returned by this function are **not** well-formed. The companion [buildAlbums],
     * [buildArtists], and [buildGenres] functions must be called with the returned list so that all
     * songs are properly linked up.
     */
    private fun buildSongs(context: Context, backend: Backend): List<Song> {
        var songs = backend.query(context).use { cursor -> backend.loadSongs(context, cursor) }

        // Deduplicate songs to prevent (most) deformed music clones
        songs =
            songs.distinctBy {
                it.rawName to
                    it._mediaStoreAlbumName to
                    it._mediaStoreArtistName to
                    it._mediaStoreAlbumArtistName to
                    it._mediaStoreGenreName to
                    it.track to
                    it.disc to
                    it.durationMs
            }

        logD("Successfully loaded ${songs.size} songs")

        return songs
    }

    /**
     * Group songs up into their respective albums. Instead of using the unreliable album or artist
     * databases, we instead group up songs by their *lowercase* artist and album name to create
     * albums. This serves two purposes:
     * 1. Sometimes artist names can be styled differently, e.g "Rammstein" vs. "RAMMSTEIN". This
     * makes sure both of those are resolved into a single artist called "Rammstein"
     * 2. Sometimes MediaStore will split album IDs up if the songs differ in format. This ensures
     * that all songs are unified under a single album.
     *
     * This does come with some costs, it's far slower than using the album ID itself, and it may
     * result in an unrelated album art being selected depending on the song chosen as the template,
     * but it seems to work pretty well.
     */
    private fun buildAlbums(songs: List<Song>): List<Album> {
        val albums = mutableListOf<Album>()
        val songsByAlbum = songs.groupBy { it._albumGroupingId }

        for (entry in songsByAlbum) {
            val albumSongs = entry.value

            // Use the song with the latest year as our metadata song.
            // This allows us to replicate the LAST_YEAR field, which is useful as it means that
            // weird years like "0" wont show up if there are alternatives.
            // Note: Normally we could want to use something like maxByWith, but apparently
            // that does not exist in the kotlin stdlib yet.
            val comparator = Sort.NullableComparator<Int>()
            var templateSong = albumSongs[0]
            for (i in 1..albumSongs.lastIndex) {
                val candidate = albumSongs[i]
                if (comparator.compare(templateSong.track, candidate.track) < 0) {
                    templateSong = candidate
                }
            }

            val albumName = templateSong._mediaStoreAlbumName
            val albumYear = templateSong._mediaStoreYear
            val albumCoverUri =
                ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    templateSong._mediaStoreAlbumId)
            val artistName = templateSong._artistGroupingName

            albums.add(
                Album(
                    albumName,
                    albumYear,
                    albumCoverUri,
                    albumSongs,
                    artistName,
                ))
        }

        logD("Successfully built ${albums.size} albums")

        return albums
    }

    /**
     * Group up albums into artists. This also requires a de-duplication step due to some edge cases
     * where [buildAlbums] could not detect duplicates.
     */
    private fun buildArtists(albums: List<Album>): List<Artist> {
        val artists = mutableListOf<Artist>()
        val albumsByArtist = albums.groupBy { it._artistGroupingId }

        for (entry in albumsByArtist) {
            val templateAlbum = entry.value[0]
            val artistName =
                if (templateAlbum._artistGroupingName != MediaStore.UNKNOWN_STRING) {
                    templateAlbum._artistGroupingName
                } else {
                    null
                }
            val artistAlbums = entry.value

            artists.add(Artist(artistName, artistAlbums))
        }

        logD("Successfully built ${artists.size} artists")

        return artists
    }

    /**
     * Read all genres and link them up to the given songs. This is the code that requires me to
     * make dozens of useless queries just to link genres up.
     */
    private fun buildGenres(songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()
        val songsByGenre = songs.groupBy { it._mediaStoreGenreName?.hashCode() }

        for (entry in songsByGenre) {
            val templateSong = entry.value[0]
            genres.add(Genre(templateSong._mediaStoreGenreName, entry.value))
        }

        logD("Successfully built ${genres.size} genres")

        return genres
    }

    interface Backend {
        fun query(context: Context): Cursor
        fun loadSongs(context: Context, cursor: Cursor): Collection<Song>
    }
}
