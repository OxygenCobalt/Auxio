/*
 * Copyright (c) 2023 Auxio Project
 * DeviceLibrary.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.device

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import javax.inject.Inject
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.fs.useQuery
import org.oxycblt.auxio.util.logD

/**
 * Organized music library information obtained from device storage.
 *
 * This class allows for the creation of a well-formed music library graph from raw song
 * information. Instances are immutable. It's generally not expected to create this yourself and
 * instead use [MusicRepository].
 *
 * @author Alexander Capehart
 */
interface DeviceLibrary {
    /** All [Song]s in this [DeviceLibrary]. */
    val songs: List<Song>
    /** All [Album]s in this [DeviceLibrary]. */
    val albums: List<Album>
    /** All [Artist]s in this [DeviceLibrary]. */
    val artists: List<Artist>
    /** All [Genre]s in this [DeviceLibrary]. */
    val genres: List<Genre>

    /**
     * Find a [Song] instance corresponding to the given [Music.UID].
     *
     * @param uid The [Music.UID] to search for.
     * @return The corresponding [Song], or null if one was not found.
     */
    fun findSong(uid: Music.UID): Song?

    /**
     * Find a [Song] instance corresponding to the given Intent.ACTION_VIEW [Uri].
     *
     * @param context [Context] required to analyze the [Uri].
     * @param uri [Uri] to search for.
     * @return A [Song] corresponding to the given [Uri], or null if one could not be found.
     */
    fun findSongForUri(context: Context, uri: Uri): Song?

    /**
     * Find a [Album] instance corresponding to the given [Music.UID].
     *
     * @param uid The [Music.UID] to search for.
     * @return The corresponding [Song], or null if one was not found.
     */
    fun findAlbum(uid: Music.UID): Album?

    /**
     * Find a [Artist] instance corresponding to the given [Music.UID].
     *
     * @param uid The [Music.UID] to search for.
     * @return The corresponding [Song], or null if one was not found.
     */
    fun findArtist(uid: Music.UID): Artist?

    /**
     * Find a [Genre] instance corresponding to the given [Music.UID].
     *
     * @param uid The [Music.UID] to search for.
     * @return The corresponding [Song], or null if one was not found.
     */
    fun findGenre(uid: Music.UID): Genre?

    /** Constructs a [DeviceLibrary] implementation in an asynchronous manner. */
    interface Factory {
        /**
         * Create a new [DeviceLibrary].
         *
         * @param rawSongs [RawSong] instances to create a [DeviceLibrary] from.
         */
        suspend fun create(rawSongs: List<RawSong>): DeviceLibrary
    }

    companion object {
        /**
         * Create an instance of [DeviceLibrary].
         *
         * @param rawSongs [RawSong]s to create the library out of.
         * @param settings [MusicSettings] required.
         */
        fun from(rawSongs: List<RawSong>, settings: MusicSettings): DeviceLibrary =
            DeviceLibraryImpl(rawSongs, settings)
    }
}

class DeviceLibraryFactoryImpl @Inject constructor(private val musicSettings: MusicSettings) :
    DeviceLibrary.Factory {
    override suspend fun create(rawSongs: List<RawSong>): DeviceLibrary =
        DeviceLibraryImpl(rawSongs, musicSettings)
}

private class DeviceLibraryImpl(rawSongs: List<RawSong>, settings: MusicSettings) : DeviceLibrary {
    override val songs = buildSongs(rawSongs, settings)
    override val albums = buildAlbums(songs, settings)
    override val artists = buildArtists(songs, albums, settings)
    override val genres = buildGenres(songs, settings)

    // Use a mapping to make finding information based on it's UID much faster.
    private val songUidMap = buildMap { songs.forEach { put(it.uid, it.finalize()) } }
    private val albumUidMap = buildMap { albums.forEach { put(it.uid, it.finalize()) } }
    private val artistUidMap = buildMap { artists.forEach { put(it.uid, it.finalize()) } }
    private val genreUidMap = buildMap { genres.forEach { put(it.uid, it.finalize()) } }

    override fun equals(other: Any?) =
        other is DeviceLibrary &&
            other.songs == songs &&
            other.albums == albums &&
            other.artists == artists &&
            other.genres == genres

    override fun hashCode(): Int {
        var hashCode = songs.hashCode()
        hashCode = hashCode * 31 + albums.hashCode()
        hashCode = hashCode * 31 + artists.hashCode()
        hashCode = hashCode * 31 + genres.hashCode()
        return hashCode
    }

    override fun findSong(uid: Music.UID) = songUidMap[uid]
    override fun findAlbum(uid: Music.UID) = albumUidMap[uid]
    override fun findArtist(uid: Music.UID) = artistUidMap[uid]
    override fun findGenre(uid: Music.UID) = genreUidMap[uid]

    override fun findSongForUri(context: Context, uri: Uri) =
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
     * Build a list [SongImpl]s from the given [RawSong].
     *
     * @param rawSongs The [RawSong]s to build the [SongImpl]s from.
     * @param settings [MusicSettings] to obtain user parsing configuration.
     * @return A sorted list of [SongImpl]s derived from the [RawSong] that should be suitable for
     *   grouping.
     */
    private fun buildSongs(rawSongs: List<RawSong>, settings: MusicSettings) =
        Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
            .songs(rawSongs.map { SongImpl(it, settings) }.distinct())

    /**
     * Build a list of [Album]s from the given [Song]s.
     *
     * @param songs The [Song]s to build [Album]s from. These will be linked with their respective
     *   [Album]s when created.
     * @param settings [MusicSettings] to obtain user parsing configuration.
     * @return A non-empty list of [Album]s. These [Album]s will be incomplete and must be linked
     *   with parent [Artist] instances in order to be usable.
     */
    private fun buildAlbums(songs: List<SongImpl>, settings: MusicSettings): List<AlbumImpl> {
        // Group songs by their singular raw album, then map the raw instances and their
        // grouped songs to Album values. Album.Raw will handle the actual grouping rules.
        val songsByAlbum = songs.groupBy { it.rawAlbum }
        val albums = songsByAlbum.map { AlbumImpl(it.key, settings, it.value) }
        logD("Successfully built ${albums.size} albums")
        return albums
    }

    /**
     * Group up [Song]s and [Album]s into [Artist] instances. Both of these items are required as
     * they group into [Artist] instances much differently, with [Song]s being grouped primarily by
     * artist names, and [Album]s being grouped primarily by album artist names.
     *
     * @param songs The [Song]s to build [Artist]s from. One [Song] can result in the creation of
     *   one or more [Artist] instances. These will be linked with their respective [Artist]s when
     *   created.
     * @param albums The [Album]s to build [Artist]s from. One [Album] can result in the creation of
     *   one or more [Artist] instances. These will be linked with their respective [Artist]s when
     *   created.
     * @param settings [MusicSettings] to obtain user parsing configuration.
     * @return A non-empty list of [Artist]s. These [Artist]s will consist of the combined groupings
     *   of [Song]s and [Album]s.
     */
    private fun buildArtists(
        songs: List<SongImpl>,
        albums: List<AlbumImpl>,
        settings: MusicSettings
    ): List<ArtistImpl> {
        // Add every raw artist credited to each Song/Album to the grouping. This way,
        // different multi-artist combinations are not treated as different artists.
        val musicByArtist = mutableMapOf<RawArtist, MutableList<Music>>()

        for (song in songs) {
            for (rawArtist in song.rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(song)
            }
        }

        for (album in albums) {
            for (rawArtist in album.rawArtists) {
                musicByArtist.getOrPut(rawArtist) { mutableListOf() }.add(album)
            }
        }

        // Convert the combined mapping into artist instances.
        val artists = musicByArtist.map { ArtistImpl(it.key, settings, it.value) }
        logD("Successfully built ${artists.size} artists")
        return artists
    }

    /**
     * Group up [Song]s into [Genre] instances.
     *
     * @param [songs] The [Song]s to build [Genre]s from. One [Song] can result in the creation of
     *   one or more [Genre] instances. These will be linked with their respective [Genre]s when
     *   created.
     * @param settings [MusicSettings] to obtain user parsing configuration.
     * @return A non-empty list of [Genre]s.
     */
    private fun buildGenres(songs: List<SongImpl>, settings: MusicSettings): List<GenreImpl> {
        // Add every raw genre credited to each Song to the grouping. This way,
        // different multi-genre combinations are not treated as different genres.
        val songsByGenre = mutableMapOf<RawGenre, MutableList<SongImpl>>()
        for (song in songs) {
            for (rawGenre in song.rawGenres) {
                songsByGenre.getOrPut(rawGenre) { mutableListOf() }.add(song)
            }
        }

        // Convert the mapping into genre instances.
        val genres = songsByGenre.map { GenreImpl(it.key, settings, it.value) }
        logD("Successfully built ${genres.size} genres")
        return genres
    }
}
