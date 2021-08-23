/*
 * Copyright (c) 2021 Auxio Project
 * MusicLoader.kt is part of Auxio.
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

package org.oxycblt.auxio.music

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.R
import org.oxycblt.auxio.excluded.ExcludedDatabase
import org.oxycblt.auxio.util.logD

/**
 * Class that loads/constructs [Genre]s, [Artist]s, [Album]s, and [Song] objects from the filesystem
 * @author OxygenCobalt
 */
class MusicLoader(private val context: Context) {
    var genres = mutableListOf<Genre>()
    var albums = mutableListOf<Album>()
    var artists = mutableListOf<Artist>()
    var songs = mutableListOf<Song>()

    private val resolver = context.contentResolver

    private var selector = "${Media.IS_MUSIC}=1"
    private var args = arrayOf<String>()

    /**
     * Begin the loading process.
     * Resulting models are pushed to [genres], [artists], [albums], and [songs].
     */
    fun load() {
        buildSelector()

        loadGenres()
        loadAlbums()
        loadSongs()

        linkAlbums()
        buildArtists()
        linkGenres()
    }

    @Suppress("DEPRECATION")
    private fun buildSelector() {
        // TODO: Upgrade this to be compatible with Android Q.
        val blacklistDatabase = ExcludedDatabase.getInstance(context)

        val paths = blacklistDatabase.readPaths()

        for (path in paths) {
            selector += " AND ${Media.DATA} NOT LIKE ?"
            args += "$path%" // Append % so that the selector properly detects children
        }
    }

    private fun loadGenres() {
        logD("Starting genre search...")

        // First, get a cursor for every genre in the android system
        val genreCursor = resolver.query(
            Genres.EXTERNAL_CONTENT_URI,
            arrayOf(
                Genres._ID, // 0
                Genres.NAME // 1
            ),
            null, null,
            Genres.DEFAULT_SORT_ORDER
        )

        // And then process those into Genre objects
        genreCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Genres._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Genres.NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getStringOrNull(nameIndex) ?: continue // No non-broken genre would be missing a name

                genres.add(Genre(id, name))
            }
        }

        logD("Genre search finished with ${genres.size} genres found.")
    }

    private fun loadAlbums() {
        logD("Starting album search...")

        val albumCursor = resolver.query(
            Albums.EXTERNAL_CONTENT_URI,
            arrayOf(
                Albums._ID, // 0
                Albums.ALBUM, // 1
                Albums.ARTIST, // 2
                Albums.LAST_YEAR, // 4
            ),
            null, null,
            Albums.DEFAULT_SORT_ORDER
        )

        val albumPlaceholder = context.getString(R.string.def_album)
        val artistPlaceholder = context.getString(R.string.def_artist)

        albumCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Albums._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Albums.ALBUM)
            val artistNameIndex = cursor.getColumnIndexOrThrow(Albums.ARTIST)
            val yearIndex = cursor.getColumnIndexOrThrow(Albums.LAST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: albumPlaceholder
                var artistName = cursor.getString(artistNameIndex) ?: artistPlaceholder
                val year = cursor.getInt(yearIndex)
                val coverUri = id.toAlbumArtURI()

                // Correct any artist names to a nicer "Unknown Artist" label
                if (artistName == MediaStore.UNKNOWN_STRING) {
                    artistName = artistPlaceholder
                }

                albums.add(Album(id, name, artistName, coverUri, year))
            }
        }

        albums = albums.distinctBy {
            it.name to it.artistName to it.year
        }.toMutableList()

        logD("Album search finished with ${albums.size} albums found")
    }

    @SuppressLint("InlinedApi")
    private fun loadSongs() {
        logD("Starting song search...")

        val songCursor = resolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID, // 0
                Media.DISPLAY_NAME, // 1
                Media.TITLE, // 2
                Media.ALBUM_ID, // 3
                Media.TRACK, // 4
                Media.DURATION, // 5
            ),
            selector, args,
            Media.DEFAULT_SORT_ORDER
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Media._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE)
            val fileIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
            val albumIndex = cursor.getColumnIndexOrThrow(Media.ALBUM_ID)
            val trackIndex = cursor.getColumnIndexOrThrow(Media.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val fileName = cursor.getString(fileIndex)
                val title = cursor.getString(titleIndex) ?: fileName
                val albumId = cursor.getLong(albumIndex)
                val track = cursor.getInt(trackIndex)
                val duration = cursor.getLong(durationIndex)

                songs.add(Song(id, title, fileName, albumId, track, duration))
            }
        }

        songs = songs.distinctBy {
            it.name to it.albumId to it.track to it.duration
        }.toMutableList()

        logD("Song search finished with ${songs.size} found")
    }

    private fun linkAlbums() {
        logD("Linking albums")

        // Group up songs by their album ids and then link them with their albums
        val songsByAlbum = songs.groupBy { it.albumId }
        val unknownAlbum = Album(
            id = -1,
            name = context.getString(R.string.def_album),
            artistName = context.getString(R.string.def_artist),
            coverUri = Uri.EMPTY,
            year = 0
        )

        songsByAlbum.forEach { entry ->
            (albums.find { it.id == entry.key } ?: unknownAlbum).linkSongs(entry.value)
        }

        albums.removeAll { it.songs.isEmpty() }

        // If something goes horribly wrong and somehow songs are still not linked up by the
        // album id, just throw them into an unknown album.
        if (unknownAlbum.songs.isNotEmpty()) {
            albums.add(unknownAlbum)
        }
    }

    private fun buildArtists() {
        logD("Linking artists")

        // Group albums up by their artist name, should not result in any null-artist issues
        val albumsByArtist = albums.groupBy { it.artistName }

        albumsByArtist.forEach { entry ->
            artists.add(
                // IDs are incremented from the minimum int value so that they remain unique.
                Artist(
                    id = (Int.MIN_VALUE + artists.size).toLong(),
                    name = entry.key,
                    albums = entry.value
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
         * In any reasonable platform, you think that you could just query a media database for the
         * "genre" field and get the genre for a song, right? But not with android! No. Thats too
         * normal for this dysfunctional SDK that was dropped on it's head as a baby. Instead, we
         * have to iterate through EACH GENRE, QUERY THE MEDIA DATABASE FOR THE SONGS OF THAT GENRE,
         * AND THEN ITERATE THROUGH THAT LIST TO LINK EVERY SONG WITH THE GENRE. This O(mom im scared)
         * algorithm single-handedly DOUBLES the amount of time it takes Auxio to load music, but
         * apparently this is the only way you can have a remotely sensible genre system on this
         * busted OS. Why is it this way? Nobody knows! Now this quirk is immortalized and has to
         * be replicated in all future versions of this API, because god forbid you break some
         * app that's probably older than some of the people reading this by now.
         *
         * Another fun fact, did you know that you can only get the date from ID3v2.3 MPEG files?
         * I sure didn't, until I decided to update my music collection to ID3v2.4 and Xiph only
         * to see that android apparently has a brain aneurysm the moment it sees a dreaded TDRC
         * or DATE tag. This bug is similarly baked into the platform and hasnt been fixed, even
         * in android TWELVE. ID3v2.4 has existed for TWENTY-ONE YEARS. IT CAN DRINK NOW. At least
         * you could replace your ancient dinosaur parser with Taglib or something, but again,
         * google cant bear even slighting the gods of backwards compat.
         *
         * Is there anything we can do about this system? No. Google's issue tracker, in classic
         * google fashion, requires a google account to even view. Even if I were to set aside my
         * Torvalds-esqe convictions and make an account, MediaStore is such an obscure part of the
         * platform that it basically receives no attention compared to the advertising APIs or the
         * nineteenth UI rework, and its not like the many music player developers are going to band
         * together to beg google to take this API behind the woodshed and shoot it. So instead,
         * players like VLC and Vanilla just hack their own pidgin version of MediaStore off of
         * their own media parsers, but even this becomes increasingly impossible as google
         * continues to kill the filesystem ala iOS. In the future MediaStore could be the only
         * system we have, which is also the day greenland melts and birthdays stop happening
         * forever. I'm pretty sure that at this point nothing is going to happen, google will
         * continue to neglect MediaStore, and all the people who just want to listen to their music
         * collections on their phone will continue to get screwed. But hey, at least some dev at
         * google got a cushy managerial position where they can tweet about politics all day for
         * shipping the brand new androidx.FooBarBlasterView, yay!
         *
         * I hate this platform so much.
         */

        genres.forEach { genre ->
            val songCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(Genres.Members._ID),
                null, null, null // Dont even bother selecting here as useless iters are less expensive than IO
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
            val unknownGenre = Genre(
                id = -2,
                name = context.getString(R.string.def_genre)
            )

            songsWithoutGenres.forEach { song ->
                unknownGenre.linkSong(song)
            }

            genres.add(unknownGenre)
        }

        genres.removeAll { it.songs.isEmpty() }
    }
}
