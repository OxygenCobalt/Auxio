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

import android.content.Context
import android.database.Cursor
import android.os.Build
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD

/**
 * Auxio's media indexer.
 *
 * Auxio's media indexer is somewhat complicated, as it has grown to support a variety of use cases
 * (and hacky garbage) in order to produce the best possible experience. It is split into three
 * distinct steps:
 *
 * 1. Finding a [Backend] to use and then querying the media database with it.
 * 2. Using the [Backend] and the media data to create songs
 * 3. Using the songs to build the library, which primarily involves linking up all data objects
 * with their corresponding parents/children.
 *
 * This class in particular handles 3 primarily. For the code that handles 1 and 2, see the other
 * files in the module.
 *
 * @author OxygenCobalt
 */
object Indexer {
    fun index(context: Context, callback: MusicStore.LoadCallback): MusicStore.Library? {
        // Establish the backend to use when initially loading songs.
        val mediaStoreBackend =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30MediaStoreBackend()
                else -> Api21MediaStoreBackend()
            }

        val songs = buildSongs(context, mediaStoreBackend, callback)
        if (songs.isEmpty()) {
            return null
        }

        val buildStart = System.currentTimeMillis()

        val albums = buildAlbums(songs)
        val artists = buildArtists(albums)

        val genres = buildGenres(songs)

        // Sanity check: Ensure that all songs are linked up to albums/artists/genres.
        for (song in songs) {
            if (song._isMissingAlbum || song._isMissingArtist || song._isMissingGenre) {
                error(
                    "Found unlinked song: ${song.rawName} [" +
                        "missing album: ${song._isMissingAlbum} " +
                        "missing artist: ${song._isMissingArtist} " +
                        "missing genre: ${song._isMissingGenre}]")
            }
        }

        logD("Successfully built library in ${System.currentTimeMillis() - buildStart}ms")

        return MusicStore.Library(genres, artists, albums, songs)
    }

    /**
     * Does the initial query over the song database using [backend]. The songs returned by this
     * function are **not** well-formed. The companion [buildAlbums], [buildArtists], and
     * [buildGenres] functions must be called with the returned list so that all songs are properly
     * linked up.
     */
    private fun buildSongs(
        context: Context,
        backend: Backend,
        callback: MusicStore.LoadCallback
    ): List<Song> {
        val start = System.currentTimeMillis()

        var songs =
            backend.query(context).use { cursor ->
                logD(
                    "Successfully queried media database " +
                        "in ${System.currentTimeMillis() - start}ms")

                backend.loadSongs(context, cursor, callback)
            }

        // Deduplicate songs to prevent (most) deformed music clones
        songs =
            songs
                .distinctBy {
                    it.rawName to
                        it._albumName to
                        it._artistName to
                        it._albumArtistName to
                        it._genreName to
                        it.track to
                        it.disc to
                        it.durationMs
                }
                .toMutableList()

        // Ensure that sorting order is consistent so that grouping is also consistent.
        Sort.ByName(true).songsInPlace(songs)

        logD("Successfully loaded ${songs.size} songs in ${System.currentTimeMillis() - start}ms")

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

            albums.add(
                Album(
                    rawName = templateSong._albumName,
                    year = templateSong._year,
                    albumCoverUri = templateSong._albumCoverUri,
                    songs = entry.value,
                    _artistGroupingName = templateSong._artistGroupingName))
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
            // The first album will suffice for template metadata.
            val templateAlbum = entry.value[0]
            artists.add(Artist(rawName = templateAlbum._artistGroupingName, albums = entry.value))
        }

        logD("Successfully built ${artists.size} artists")

        return artists
    }

    /**
     * Group up songs into genres. This is a relatively simple step compared to the other library
     * steps, as there is no demand to deduplicate genres by a lowercase name.
     */
    private fun buildGenres(songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()
        val songsByGenre = songs.groupBy { it._genreGroupingId }

        for (entry in songsByGenre) {
            // The first song fill suffice for template metadata.
            val templateSong = entry.value[0]
            genres.add(Genre(rawName = templateSong._genreName, songs = entry.value))
        }

        logD("Successfully built ${genres.size} genres")

        return genres
    }

    /** Represents a backend that metadata can be extracted from. */
    interface Backend {
        /** Query the media database for a basic cursor. */
        fun query(context: Context): Cursor

        /** Create a list of songs from the [Cursor] queried in [query]. */
        fun loadSongs(
            context: Context,
            cursor: Cursor,
            callback: MusicStore.LoadCallback
        ): Collection<Song>
    }
}
