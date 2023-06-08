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
import kotlinx.coroutines.channels.Channel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.fs.useQuery
import org.oxycblt.auxio.util.logW
import org.oxycblt.auxio.util.unlikelyToBeNull

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
    val songs: Collection<Song>
    /** All [Album]s in this [DeviceLibrary]. */
    val albums: Collection<Album>
    /** All [Artist]s in this [DeviceLibrary]. */
    val artists: Collection<Artist>
    /** All [Genre]s in this [DeviceLibrary]. */
    val genres: Collection<Genre>

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
        suspend fun create(
            rawSongs: Channel<RawSong>,
            processedSongs: Channel<RawSong>
        ): DeviceLibraryImpl
    }
}

class DeviceLibraryFactoryImpl @Inject constructor(private val musicSettings: MusicSettings) :
    DeviceLibrary.Factory {
    override suspend fun create(
        rawSongs: Channel<RawSong>,
        processedSongs: Channel<RawSong>
    ): DeviceLibraryImpl {
        val songGrouping = mutableMapOf<Music.UID, SongImpl>()
        val albumGrouping = mutableMapOf<RawAlbum.Key, Grouping<RawAlbum, SongImpl>>()
        val artistGrouping = mutableMapOf<RawArtist.Key, Grouping<RawArtist, Music>>()
        val genreGrouping = mutableMapOf<RawGenre.Key, Grouping<RawGenre, SongImpl>>()

        // All music information is grouped as it is indexed by other components.
        for (rawSong in rawSongs) {
            val song = SongImpl(rawSong, musicSettings)
            // At times the indexer produces duplicate songs, try to filter these. Comparing by
            // UID is sufficient for something like this, and also prevents collisions from
            // causing severe issues elsewhere.
            if (songGrouping.containsKey(song.uid)) {
                logW(
                    "Duplicate song found: ${song.path} in " +
                        "collides with ${unlikelyToBeNull(songGrouping[song.uid]).path}")
                processedSongs.send(rawSong)
                continue
            }
            songGrouping[song.uid] = song

            // Group the new song into an album.
            val albumKey = song.rawAlbum.key
            val albumBody = albumGrouping[albumKey]
            if (albumBody != null) {
                albumBody.music.add(song)
                val prioritized = albumBody.raw.src
                // Since albums are grouped fuzzily, we pick the song with the earliest track to
                // use for album information to ensure consistent metadata and UIDs. Fall back to
                // the name otherwise.
                val trackLower =
                    song.track != null && (prioritized.track == null || song.track < prioritized.track)
                val nameLower =
                    song.name < prioritized.name
                if (trackLower || nameLower) {
                    albumBody.raw = PrioritizedRaw(song.rawAlbum, song)
                }
            } else {
                // Need to initialize this grouping.
                albumGrouping[albumKey] =
                    Grouping(PrioritizedRaw(song.rawAlbum, song), mutableListOf(song))
            }

            // Group the song into each of it's artists.
            for (rawArtist in song.rawArtists) {
                val artistKey = rawArtist.key
                val artistBody = artistGrouping[artistKey]
                if (artistBody != null) {
                    // Since artists are not guaranteed to have songs, song artist information is
                    // de-prioritized compared to album artist information.
                    artistBody.music.add(song)
                } else {
                    // Need to initialize this grouping.
                    artistGrouping[artistKey] =
                        Grouping(PrioritizedRaw(rawArtist, song), mutableListOf(song))
                }
            }

            // Group the song into each of it's genres.
            for (rawGenre in song.rawGenres) {
                val genreKey = rawGenre.key
                val genreBody = genreGrouping[genreKey]
                if (genreBody != null) {
                    genreBody.music.add(song)
                    // Genre information from higher songs in ascending alphabetical order are
                    // prioritized.
                    val prioritized = genreBody.raw.src
                    val nameLower = song.name < prioritized.name
                    if (nameLower) {
                        genreBody.raw = PrioritizedRaw(rawGenre, song)
                    }
                } else {
                    // Need to initialize this grouping.
                    genreGrouping[genreKey] =
                        Grouping(PrioritizedRaw(rawGenre, song), mutableListOf(song))
                }
            }

            processedSongs.send(rawSong)
        }

        // Now that all songs are processed, also process albums and group them into their
        // respective artists.
        val albums =
            albumGrouping.values.map { AlbumImpl(it.raw.inner, musicSettings, it.music) }
        for (album in albums) {
            for (rawArtist in album.rawArtists) {
                val key = RawArtist.Key(rawArtist)
                val body = artistGrouping[key]
                if (body != null) {
                    body.music.add(album)
                    when (val prioritized = body.raw.src) {
                        // Immediately replace any songs that initially held the priority position.
                        is SongImpl -> body.raw = PrioritizedRaw(rawArtist, album)
                        is AlbumImpl -> {
                            // Album information from later dates is prioritized, as it is more likely to
                            // contain the "modern" name of the artist if the information really is
                            // in-consistent. Fall back to the name otherwise.
                            val dateEarlier =
                                album.dates != null && (prioritized.dates == null || album.dates < prioritized.dates)
                            val nameLower =
                                album.name < prioritized.name
                            if (dateEarlier || nameLower) {
                                body.raw = PrioritizedRaw(rawArtist, album)
                            }
                        }
                        else -> throw IllegalStateException()
                    }
                } else {
                    // Need to initialize this grouping.
                    artistGrouping[key] =
                        Grouping(PrioritizedRaw(rawArtist, album), mutableListOf(album))
                }
            }
        }

        // Artists and genres do not need to be grouped and can be processed immediately.
        val artists =
            artistGrouping.values.map { ArtistImpl(it.raw.inner, musicSettings, it.music) }
        val genres =
            genreGrouping.values.map { GenreImpl(it.raw.inner, musicSettings, it.music) }

        return DeviceLibraryImpl(songGrouping.values, albums, artists, genres)
    }

    private data class Grouping<R, M : Music>(
        var raw: PrioritizedRaw<R, M>,
        val music: MutableList<M>
    )

    private data class PrioritizedRaw<R, M : Music>(val inner: R, val src: M)
}

class DeviceLibraryImpl(
    override val songs: Collection<SongImpl>,
    override val albums: Collection<AlbumImpl>,
    override val artists: Collection<ArtistImpl>,
    override val genres: Collection<GenreImpl>
) : DeviceLibrary {
    // Use a mapping to make finding information based on it's UID much faster.
    private val songUidMap = buildMap { songs.forEach { put(it.uid, it.finalize()) } }
    private val albumUidMap = buildMap { albums.forEach { put(it.uid, it.finalize()) } }
    private val artistUidMap = buildMap { artists.forEach { put(it.uid, it.finalize()) } }
    private val genreUidMap = buildMap { genres.forEach { put(it.uid, it.finalize()) } }

    // All other music is built from songs, so comparison only needs to check songs.
    override fun equals(other: Any?) = other is DeviceLibrary && other.songs == songs
    override fun hashCode() = songs.hashCode()
    override fun toString() =
        "DeviceLibrary(songs=${songs.size}, albums=${albums.size}, " +
                "artists=${artists.size}, genres=${genres.size})"

    override fun findSong(uid: Music.UID): Song? = songUidMap[uid]
    override fun findAlbum(uid: Music.UID): Album? = albumUidMap[uid]
    override fun findArtist(uid: Music.UID): Artist? = artistUidMap[uid]
    override fun findGenre(uid: Music.UID): Genre? = genreUidMap[uid]

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
}
