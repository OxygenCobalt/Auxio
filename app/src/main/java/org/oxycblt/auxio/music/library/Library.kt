/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.library

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.storage.contentResolverSafe
import org.oxycblt.auxio.music.storage.useQuery
import org.oxycblt.auxio.util.logD

/**
 * Organized music library information.
 *
 * This class allows for the creation of a well-formed music library graph from raw song
 * information. It's generally not expected to create this yourself and instead use [MusicStore].
 *
 * @author Alexander Capehart
 */
class Library(rawSongs: List<Song.Raw>, settings: MusicSettings) {
    /** All [Song]s that were detected on the device. */
    val songs = Sort(Sort.Mode.ByName, true).songs(rawSongs.map { Song(it, settings) }.distinct())
    /** All [Album]s found on the device. */
    val albums = buildAlbums(songs)
    /** All [Artist]s found on the device. */
    val artists = buildArtists(songs, albums)
    /** All [Genre]s found on the device. */
    val genres = buildGenres(songs)

    // Use a mapping to make finding information based on it's UID much faster.
    private val uidMap = buildMap {
        for (music in (songs + albums + artists + genres)) {
            // Finalize all music in the same mapping creation loop for efficiency.
            music._finalize()
            this[music.uid] = music
        }
    }

    /**
     * Finds a [Music] item [T] in the library by it's [Music.UID].
     * @param uid The [Music.UID] to search for.
     * @return The [T] corresponding to the given [Music.UID], or null if nothing could be found or
     * the [Music.UID] did not correspond to a [T].
     */
    @Suppress("UNCHECKED_CAST") fun <T : Music> find(uid: Music.UID) = uidMap[uid] as? T

    /**
     * Convert a [Song] from an another library into a [Song] in this [Library].
     * @param song The [Song] to convert.
     * @return The analogous [Song] in this [Library], or null if it does not exist.
     */
    fun sanitize(song: Song) = find<Song>(song.uid)

    /**
     * Convert a [Album] from an another library into a [Album] in this [Library].
     * @param album The [Album] to convert.
     * @return The analogous [Album] in this [Library], or null if it does not exist.
     */
    fun sanitize(album: Album) = find<Album>(album.uid)

    /**
     * Convert a [Artist] from an another library into a [Artist] in this [Library].
     * @param artist The [Artist] to convert.
     * @return The analogous [Artist] in this [Library], or null if it does not exist.
     */
    fun sanitize(artist: Artist) = find<Artist>(artist.uid)

    /**
     * Convert a [Genre] from an another library into a [Genre] in this [Library].
     * @param genre The [Genre] to convert.
     * @return The analogous [Genre] in this [Library], or null if it does not exist.
     */
    fun sanitize(genre: Genre) = find<Genre>(genre.uid)

    /**
     * Find a [Song] instance corresponding to the given Intent.ACTION_VIEW [Uri].
     * @param context [Context] required to analyze the [Uri].
     * @param uri [Uri] to search for.
     * @return A [Song] corresponding to the given [Uri], or null if one could not be found.
     */
    fun findSongForUri(context: Context, uri: Uri) =
        context.contentResolverSafe.useQuery(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)) { cursor ->
            cursor.moveToFirst()
            // We are weirdly limited to DISPLAY_NAME and SIZE when trying to locate a
            // song. Do what we can to hopefully find the song the user wanted to open.
            val displayName =
                cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            songs.find { it.path.name == displayName && it.size == size }
        }

    /**
     * Build a list of [Album]s from the given [Song]s.
     * @param songs The [Song]s to build [Album]s from. These will be linked with their respective
     * [Album]s when created.
     * @return A non-empty list of [Album]s. These [Album]s will be incomplete and must be linked
     * with parent [Artist] instances in order to be usable.
     */
    private fun buildAlbums(songs: List<Song>): List<Album> {
        // Group songs by their singular raw album, then map the raw instances and their
        // grouped songs to Album values. Album.Raw will handle the actual grouping rules.
        val songsByAlbum = songs.groupBy { it._rawAlbum }
        val albums = songsByAlbum.map { Album(it.key, it.value) }
        logD("Successfully built ${albums.size} albums")
        return albums
    }

    /**
     * Group up [Song]s and [Album]s into [Artist] instances. Both of these items are required as
     * they group into [Artist] instances much differently, with [Song]s being grouped primarily by
     * artist names, and [Album]s being grouped primarily by album artist names.
     * @param songs The [Song]s to build [Artist]s from. One [Song] can result in the creation of
     * one or more [Artist] instances. These will be linked with their respective [Artist]s when
     * created.
     * @param albums The [Album]s to build [Artist]s from. One [Album] can result in the creation of
     * one or more [Artist] instances. These will be linked with their respective [Artist]s when
     * created.
     * @return A non-empty list of [Artist]s. These [Artist]s will consist of the combined groupings
     * of [Song]s and [Album]s.
     */
    private fun buildArtists(songs: List<Song>, albums: List<Album>): List<Artist> {
        // Add every raw artist credited to each Song/Album to the grouping. This way,
        // different multi-artist combinations are not treated as different artists.
        val musicByArtist = mutableMapOf<Artist.Raw, MutableList<Music>>()

        for (song in songs) {
            for (rawArtist in song._rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(song)
            }
        }

        for (album in albums) {
            for (rawArtist in album._rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(album)
            }
        }

        // Convert the combined mapping into artist instances.
        val artists = musicByArtist.map { Artist(it.key, it.value) }
        logD("Successfully built ${artists.size} artists")
        return artists
    }

    /**
     * Group up [Song]s into [Genre] instances.
     * @param [songs] The [Song]s to build [Genre]s from. One [Song] can result in the creation of
     * one or more [Genre] instances. These will be linked with their respective [Genre]s when
     * created.
     * @return A non-empty list of [Genre]s.
     */
    private fun buildGenres(songs: List<Song>): List<Genre> {
        // Add every raw genre credited to each Song to the grouping. This way,
        // different multi-genre combinations are not treated as different genres.
        val songsByGenre = mutableMapOf<Genre.Raw, MutableList<Song>>()
        for (song in songs) {
            for (rawGenre in song._rawGenres) {
                songsByGenre.getOrPut(rawGenre) { mutableListOf() }.add(song)
            }
        }

        // Convert the mapping into genre instances.
        val genres = songsByGenre.map { Genre(it.key, it.value) }
        logD("Successfully built ${genres.size} genres")
        return genres
    }
}
