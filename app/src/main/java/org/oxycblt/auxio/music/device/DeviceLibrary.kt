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
import java.util.LinkedList
import javax.inject.Inject
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.fs.useQuery
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

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

    // All other music is built from songs, so comparison only needs to check songs.
    override fun equals(other: Any?) = other is DeviceLibrary && other.songs == songs
    override fun hashCode() = songs.hashCode()
    override fun toString() =
        "DeviceLibrary(songs=${songs.size}, albums=${albums.size}, artists=${artists.size}, genres=${genres.size})"

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

    private fun buildSongs(rawSongs: List<RawSong>, settings: MusicSettings): List<SongImpl> {
        val start = System.currentTimeMillis()
        val uidSet = LinkedHashSet<Music.UID>(rawSongs.size)
        val songs = LinkedList<SongImpl>()
        for (rawSong in rawSongs) {
            val song = SongImpl(rawSong, settings)
            if (uidSet.add(song.uid)) {
                songs.add(song)
            } else {
                logW("Duplicate song found: $song")
            }
        }
        logD("Successfully built ${songs.size} songs in ${System.currentTimeMillis() - start}ms")
        return songs
    }

    private fun buildAlbums(songs: List<SongImpl>, settings: MusicSettings): List<AlbumImpl> {
        val start = System.currentTimeMillis()
        val albumGrouping = mutableMapOf<RawAlbum.Key, Grouping<RawAlbum, SongImpl, SongImpl>>()
        for (song in songs) {
            val key = RawAlbum.Key(song.rawAlbum)
            val body = albumGrouping[key]
            if (body != null) {
                body.music.add(song)
                val dominantSong = body.dominantRaw.derived
                val dominates =
                    song.track != null &&
                        (dominantSong.track == null || song.track < dominantSong.track)
                if (dominates) {
                    body.dominantRaw = DominantRaw(song.rawAlbum, song)
                }
            } else {
                albumGrouping[key] = Grouping(DominantRaw(song.rawAlbum, song), mutableListOf(song))
            }
        }

        // Group songs by their singular raw album, then map the raw instances and their
        // grouped songs to Album values. Album.Raw will handle the actual grouping rules.
        val albums =
            albumGrouping.values.map { AlbumImpl(it.dominantRaw.inner, settings, it.music) }
        logD("Successfully built ${albums.size} albums in ${System.currentTimeMillis() - start}ms")
        return albums
    }

    private fun buildArtists(
        songs: List<SongImpl>,
        albums: List<AlbumImpl>,
        settings: MusicSettings
    ): List<ArtistImpl> {
        val start = System.currentTimeMillis()
        // Add every raw artist credited to each Song/Album to the grouping. This way,
        // different multi-artist combinations are not treated as different artists.
        // Songs and albums are grouped by artist and album artist respectively.
        val artistGrouping = mutableMapOf<RawArtist.Key, Grouping<RawArtist, AlbumImpl, Music>>()

        for (song in songs) {
            for (rawArtist in song.rawArtists) {
                val key = RawArtist.Key(rawArtist)
                val body = artistGrouping[key]
                if (body != null) {
                    body.music.add(song)
                } else {
                    artistGrouping[key] =
                        Grouping(DominantRaw(rawArtist, albums.first()), mutableListOf(song))
                }
            }
        }

        for (album in albums) {
            for (rawArtist in album.rawArtists) {
                val key = RawArtist.Key(rawArtist)
                val body = artistGrouping[key]
                if (body != null) {
                    body.music.add(album)
                    val dominantAlbum = body.dominantRaw.derived
                    val dominates =
                        album.dates != null &&
                            (dominantAlbum.dates == null || album.dates < dominantAlbum.dates)
                    if (dominates) {
                        body.dominantRaw = DominantRaw(rawArtist, album)
                    }
                } else {
                    artistGrouping[key] =
                        Grouping(DominantRaw(rawArtist, album), mutableListOf(album))
                }
            }
        }

        // Convert the combined mapping into artist instances.
        val artists =
            artistGrouping.values.map { ArtistImpl(it.dominantRaw.inner, settings, it.music) }
        logD(
            "Successfully built ${artists.size} artists in ${System.currentTimeMillis() - start}ms")
        return artists
    }

    private fun buildGenres(songs: List<SongImpl>, settings: MusicSettings): List<GenreImpl> {
        val start = System.currentTimeMillis()
        // Add every raw genre credited to each Song to the grouping. This way,
        // different multi-genre combinations are not treated as different genres.
        val songsByGenre = mutableMapOf<RawGenre.Key, Grouping<RawGenre, SongImpl, SongImpl>>()
        for (song in songs) {
            for (rawGenre in song.rawGenres) {
                val key = RawGenre.Key(rawGenre)
                val body = songsByGenre[key]
                if (body != null) {
                    body.music.add(song)
                    val dominantSong = body.dominantRaw.derived
                    if (song.date != null && song.name < dominantSong.name) {
                        body.dominantRaw = DominantRaw(rawGenre, song)
                    }
                } else {
                    songsByGenre[key] = Grouping(DominantRaw(rawGenre, song), mutableListOf(song))
                }
            }
        }

        // Convert the mapping into genre instances.
        val genres =
            songsByGenre.map { GenreImpl(it.value.dominantRaw.inner, settings, it.value.music) }
        logD("Successfully built ${genres.size} genres in ${System.currentTimeMillis() - start}ms")
        return genres
    }

    data class DominantRaw<R, M : Music>(val inner: R, val derived: M)

    data class Grouping<R, D : Music, M : Music>(
        var dominantRaw: DominantRaw<R, D>,
        val music: MutableList<M>
    )
}
