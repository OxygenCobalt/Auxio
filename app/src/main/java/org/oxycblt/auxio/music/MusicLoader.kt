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
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.Media
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.R
import org.oxycblt.auxio.excluded.ExcludedDatabase
import org.oxycblt.auxio.ui.SortMode
import org.oxycblt.auxio.util.logD

/**
 * This class does pretty much all the black magic required to get a remotely sensible music
 * indexing system while still optimizing for time. I would recommend you leave this file now
 * before you lose your sanity trying to understand the hoops I had to jump through for this
 * system, but if you really want to stay, here's a debrief on why this code is so awful.
 *
 * MediaStore is not a good API. It is not even a bad API. Calling it a bad API is an insult to
 * other bad android APIs, like CoordinatorLayout or InputMethodManager. No. MediaStore is a
 * crime against humanity and probably a way to summon Zalgo if you look at it the wrong way.
 *
 * You think that if you wanted to query a song's genre from a media database, you could just
 * put "genre" in the query and it would return it, right? But not with MediaStore! No, that's
 * too straightfoward for this class that was dropped on it's head as a baby. So instead, you
 * have to query for each genre, query all the songs in each genre, and then iterate through those
 * songs to link every song with their genre. This is not documented anywhere, and the
 * O(mom im scared) algorithm you have to run to get it working single-handedly DOUBLES Auxio's
 * loading times. At no point have the devs considered that this column is absolutely busted, and
 * instead focused on adding infuriat- I mean nice proprietary extensions to MediaStore for their
 * own Google Play Music, and we all know how great that worked out!
 *
 * It's not even ergonomics that makes this API bad. It's base implementation is completely borked
 * as well. Did you know that MediaStore doesn't accept dates that aren't from ID3v2.3 MP3 files?
 * I sure didn't, until I decided to upgrade my music collection to ID3v2.4 and Xiph only to see
 * that their metadata parser has a brain aneurysm the moment it stumbles upon a dreaded TRDC or
 * DATE tag. Once again, this is because internally android uses an ancient in-house metadata
 * parser to get everything indexed, and so far they have not bothered to modernize this parser
 * or even switch it to something more powerful like Taglib, not even in Android 12. ID3v2.4 has
 * been around for 21 years. It can drink now. All of my what.
 *
 * Not to mention all the other infuriating quirks. Album artists can't be accessed from the albums
 * table, so we have to go for the less efficent "make a big query on all the songs lol" method
 * so that songs don't end up fragmented across artists. Pretty much every OEM has added some
 * extension or quirk to MediaStore that I cannot reproduce, with some OEMs (COUGHSAMSUNGCOUGH)
 * crippling the normal tables so that you're railroaded into their music app. The way I do
 * blacklisting relies on a deprecated method, and the supposedly "modern" method is SLOWER and
 * causes even more problems since I have to manage databases across version boundaries. Sometimes
 * music will have a deformed clone that I can't filter out, sometimes Genres will just break for no
 * reason, sometimes this spaghetti parser just completely breaks down and is unable to get any
 * metadata. Everything is broken in it's own special unique way and I absolutely hate it.
 *
 * Is there anything we can do about it? No. Google has routinely shut down issues that begged google
 * to fix glaring issues with MediaStore or to just take the API behind the woodshed and shoot it.
 * Largely because they have zero incentive to improve it, especially for such obscure things
 * as indexing music. As a result, some players like Vanilla and VLC just hack their own pidgin
 * version of MediaStore from their own parsers, but this is both infeasible for Auxio due to how
 * incredibly slow it is to get a file handle from the android sandbox AND how much harder it is
 * to manage a database of your own media that mirrors the filesystem perfectly. And even if I set
 * aside those crippling issues and changed my indexer to that, it would face the even larger
 * problem of how google keeps trying to kill the filesystem and force you into their
 * ContentResolver API. In the future MediaStore could be the only system we have, which is also the
 * day that greenland melts and birthdays stop happening forever.
 *
 * I'm pretty sure nothing is going to happen and MediaStore will continue to be neglected and
 * probably deprecated eventually for a "new" API that just coincidentally excludes music indexing.
 * Because go screw yourself for wanting to listen to music you own. Be a good consoomer and listen
 * to your AlgoPop StreamMix™ instead.
 *
 * I wish I was born in the neolithic.
 *
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
        loadSongs()
        linkGenres()

        buildAlbums()
        buildArtists()
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
                // No non-broken genre would be missing a name.
                val name = cursor.getStringOrNull(nameIndex) ?: continue

                genres.add(Genre(id, name, name.getGenreNameCompat() ?: name))
            }
        }

        logD("Genre search finished with ${genres.size} genres found.")
    }

    @SuppressLint("InlinedApi")
    private fun loadSongs() {
        logD("Starting song search...")

        val songCursor = resolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID, // 0
                Media.TITLE, // 1
                Media.DISPLAY_NAME, // 2
                Media.ALBUM, // 3
                Media.ALBUM_ID, // 4
                Media.ARTIST, // 5
                Media.ALBUM_ARTIST, // 6
                Media.YEAR, // 7
                Media.TRACK, // 8
                Media.DURATION, // 9
            ),
            selector, args,
            Media.DEFAULT_SORT_ORDER
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(Media._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE)
            val fileIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)
            val albumIndex = cursor.getColumnIndexOrThrow(Media.ALBUM)
            val albumIdIndex = cursor.getColumnIndexOrThrow(Media.ALBUM_ID)
            val artistIndex = cursor.getColumnIndexOrThrow(Media.ARTIST)
            val albumArtistIndex = cursor.getColumnIndexOrThrow(Media.ALBUM_ARTIST)
            val yearIndex = cursor.getColumnIndexOrThrow(Media.YEAR)
            val trackIndex = cursor.getColumnIndexOrThrow(Media.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val fileName = cursor.getString(fileIndex)
                val title = cursor.getString(titleIndex) ?: fileName
                val album = cursor.getString(albumIndex)
                val albumId = cursor.getLong(albumIdIndex)

                // MediaStore does not have support for artists in the album field, so we have to
                // detect it on a song-by-song basis. This is another massive bottleneck in the music
                // loader since we have to do a massive query to get what we want, but theres not
                // a lot I can do that doesn't degrade UX.
                val artist = cursor.getStringOrNull(albumArtistIndex)
                    ?: cursor.getString(artistIndex)

                val year = cursor.getInt(yearIndex)
                val track = cursor.getInt(trackIndex)
                val duration = cursor.getLong(durationIndex)

                songs.add(
                    Song(
                        id, title, fileName, album, albumId, artist, year, track, duration
                    )
                )
            }
        }

        songs = songs.distinctBy {
            it.name to it.albumId to it.artistName to it.track to it.duration
        }.toMutableList()

        logD("Song search finished with ${songs.size} found")
    }

    private fun buildAlbums() {
        logD("Linking albums")

        // Group up songs by their album ids and then link them with their albums
        val songsByAlbum = songs.groupBy { it.albumId }

        songsByAlbum.forEach { entry ->
            // Rely on the first song in this list for album information.
            // Note: This might result in a bad year being used for an album if an album's songs
            // have multiple years. This is fixable but is currently omitted for speed.
            val song = entry.value[0]

            albums.add(
                Album(
                    id = entry.key,
                    name = song.albumName,
                    artistName = song.artistName,
                    songs = entry.value,
                    year = song.year
                )
            )
        }

        albums.removeAll { it.songs.isEmpty() }
        albums = SortMode.ASCENDING.sortAlbums(albums).toMutableList()

        logD("Songs successfully linked into ${albums.size} albums")
    }

    private fun buildArtists() {
        logD("Linking artists")

        val albumsByArtist = albums.groupBy { it.artistName }

        albumsByArtist.forEach { entry ->
            val resolvedName = if (entry.key == MediaStore.UNKNOWN_STRING) {
                context.getString(R.string.def_artist)
            } else {
                entry.key
            }

            // Because of our hacky album artist system, MediaStore artist IDs are unreliable.
            // Therefore we just use the hashCode of the artist name as our ID and move on.
            artists.add(
                Artist(
                    id = entry.key.hashCode().toLong(),
                    name = entry.key,
                    resolvedName = resolvedName,
                    albums = entry.value
                )
            )
        }

        artists = SortMode.ASCENDING.sortParents(artists).toMutableList()

        logD("Albums successfully linked into ${artists.size} artists")
    }

    private fun linkGenres() {
        logD("Linking genres")

        genres.forEach { genre ->
            val songCursor = resolver.query(
                Genres.Members.getContentUri("external", genre.id),
                arrayOf(Genres.Members._ID),
                null, null, null // Dont even bother blacklisting here as useless iters are less expensive than IO
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

        // Songs that don't have a genre will be thrown into an unknown genre.

        val unknownGenre = Genre(
            id = Long.MIN_VALUE,
            name = MediaStore.UNKNOWN_STRING,
            resolvedName = context.getString(R.string.def_genre)
        )

        songs.forEach { song ->
            if (song.genre == null) {
                unknownGenre.linkSong(song)
            }
        }

        if (unknownGenre.songs.isNotEmpty()) {
            genres.add(unknownGenre)
        }
    }
}
