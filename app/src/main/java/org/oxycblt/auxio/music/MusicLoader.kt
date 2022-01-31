package org.oxycblt.auxio.music

import android.content.Context
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.R
import org.oxycblt.auxio.excluded.ExcludedDatabase

/**
 * This class acts as the base for most the black magic required to get a remotely sensible music
 * indexing system while still optimizing for time. I would recommend you leave this module now
 * before you lose your sanity trying to understand the hoops I had to jump through for this
 * system, but if you really want to stay, here's a debrief on why this code is so awful.
 *
 * MediaStore is not a good API. It is not even a bad API. Calling it a bad API is an insult to
 * other bad android APIs, like CoordinatorLayout or InputMethodManager. No. MediaStore is a
 * crime against humanity and probably a way to summon Zalgo if you look at it the wrong way.
 *
 * You think that if you wanted to query a song's genre from a media database, you could just
 * put "genre" in the query and it would return it, right? But not with MediaStore! No, that's
 * too straightforward for this class that was dropped on it's head as a baby. So instead, you
 * have to query for each genre, query all the songs in each genre, and then iterate through those
 * songs to link every song with their genre. This is not documented anywhere, and the
 * O(mom im scared) algorithm you have to run to get it working single-handedly DOUBLES Auxio's
 * loading times. At no point have the devs considered that this column is absolutely insane, and
 * instead focused on adding infuriat- I mean nice proprietary extensions to MediaStore for their
 * own Google Play Music, and we all know how great that worked out!
 *
 * It's not even ergonomics that makes this API bad. It's base implementation is completely borked
 * as well. Did you know that MediaStore doesn't accept dates that aren't from ID3v2.3 MP3 files?
 * I sure didn't, until I decided to upgrade my music collection to ID3v2.4 and FLAC only to see
 * that their metadata parser has a brain aneurysm the moment it stumbles upon a dreaded TRDC or
 * DATE tag. Once again, this is because internally android uses an ancient in-house metadata
 * parser to get everything indexed, and so far they have not bothered to modernize this parser
 * or even switch it to something more powerful like Taglib, not even in Android 12. ID3v2.4 has
 * been around for 21 years. It can drink now. All of my what.
 *
 * Not to mention all the other infuriating quirks. Album artists can't be accessed from the albums
 * table, so we have to go for the less efficient "make a big query on all the songs lol" method
 * so that songs don't end up fragmented across artists. Pretty much every OEM has added some
 * extension or quirk to MediaStore that I cannot reproduce, with some OEMs (COUGHSAMSUNGCOUGH)
 * crippling the normal tables so that you're railroaded into their music app. The way I do
 * blacklisting relies on a deprecated method, and the supposedly "modern" method is SLOWER and
 * causes even more problems since I have to manage databases across version boundaries. Sometimes
 * music will have a deformed clone that I can't filter out, sometimes Genres will just break for no
 * reason, and sometimes tags encoded in UTF-8 will be interpreted as anything from UTF-16 to Latin-1
 * to Shift JIS WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY
 *
 * Is there anything we can do about it? No. Google has routinely shut down issues that begged google
 * to fix glaring issues with MediaStore or to just take the API behind the woodshed and shoot it.
 * Largely because they have zero incentive to improve it given how "obscure" music listening is.
 * As a result, some players like Vanilla and VLC just hack their own pidgin version of MediaStore
 * from their own parsers, but this is both infeasible for Auxio due to how incredibly slow it is
 * to get a file handle from the android sandbox AND how much harder it is to manage a database of
 * your own media that mirrors the filesystem perfectly. And even if I set aside those crippling
 * issues and changed my indexer to that, it would face the even larger problem of how google keeps
 * trying to kill the filesystem and force you into their ContentResolver API. In the future
 * MediaStore could be the only system we have, which is also the day that greenland melts and
 * birthdays stop happening forever.
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
@Suppress("InlinedApi")
class MusicLoader {
    data class Library(
        val genres: List<Genre>,
        val artists: List<Artist>,
        val albums: List<Album>,
        val songs: List<Song>
    )

    fun load(context: Context): Library? {
        val songs = loadSongs(context)
        if (songs.isEmpty()) return null

        val albums = buildAlbums(songs)
        val artists = buildArtists(context, albums)
        val genres = readGenres(context, songs)

        return Library(
            genres,
            artists,
            albums,
            songs
        )
    }

    private fun loadSongs(context: Context): List<Song> {
        var songs = mutableListOf<Song>()
        val blacklistDatabase = ExcludedDatabase.getInstance(context)
        val paths = blacklistDatabase.readPaths()

        var selector = "${MediaStore.Audio.Media.IS_MUSIC}=1"
        val args = mutableListOf<String>()

        // DATA was deprecated on Android 10, but is set to be un-deprecated in Android 12L.
        // The only reason we'd want to change this is to add external partitions support, but
        // that's less efficient and there's no demand for that right now.
        for (path in paths) {
            selector += " AND ${MediaStore.Audio.Media.DATA} NOT LIKE ?"
            args += "$path%" // Append % so that the selector properly detects children
        }

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.DURATION,
            ),
            selector, args.toTypedArray(), null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val fileIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumArtistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            val yearIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val fileName = cursor.getString(fileIndex)
                val title = cursor.getString(titleIndex) ?: fileName
                val album = cursor.getString(albumIndex)
                val albumId = cursor.getLong(albumIdIndex)

                val artist = cursor.getString(artistIndex).let {
                    if (it != MediaStore.UNKNOWN_STRING) it else null
                }

                val albumArtist = cursor.getStringOrNull(albumArtistIndex)

                val year = cursor.getInt(yearIndex)
                val track = cursor.getInt(trackIndex)
                val duration = cursor.getLong(durationIndex)

                songs.add(
                    Song(
                        id, title, fileName, album, albumId, artist,
                        albumArtist, year, track, duration
                    )
                )
            }
        }

        songs = songs.distinctBy {
            it.name to it.albumName to it.artistName to it.track to it.duration
        }.toMutableList()

        return songs
    }

    private fun buildAlbums(songs: List<Song>): List<Album> {
        // Group up songs by their album ids and then link them with their albums
        // TODO: Figure out how to group up songs by album in a way that does not accidentally
        //  split songs by album.
        val albums = mutableListOf<Album>()
        val songsByAlbum = songs.groupBy { it.albumId }

        songsByAlbum.forEach { entry ->
            // Use the song with the latest year as our metadata song.
            // This allows us to replicate the LAST_YEAR field, which is useful as it means that
            // weird years like "0" wont show up if there are alternatives.
            val song = requireNotNull(entry.value.maxByOrNull { it.year })

            albums.add(
                Album(
                    // When assigning an artist to an album, use the album artist first, then the
                    // normal artist, and then the internal representation of an unknown artist name.
                    entry.key, song.albumName,
                    song.albumArtistName ?: song.artistName ?: MediaStore.UNKNOWN_STRING,
                    song.year, entry.value
                )
            )
        }

        albums.removeAll { it.songs.isEmpty() }

        return albums
    }

    private fun buildArtists(context: Context, albums: List<Album>): List<Artist> {
        val artists = mutableListOf<Artist>()
        val albumsByArtist = albums.groupBy { it.artistName }

        for (entry in albumsByArtist) {
            val name = entry.key
            val resolvedName = when (name) {
                MediaStore.UNKNOWN_STRING -> context.getString(R.string.def_artist)
                else -> name
            }
            val artistAlbums = entry.value.toMutableList()

            // Music files from the same artist may format the artist differently, such as being
            // in uppercase/lowercase forms. If we have already built an artist that has a
            // functionally identical name to this one, then simply merge the artists instead
            // of removing them.
            val previousArtistIndex = artists.indexOfFirst { artist ->
                artist.name.lowercase() == name.lowercase()
            }

            // In most cases, MediaStore artist IDs are unreliable or omitted for speed.
            // Use the hashCode of the artist name as our ID and move on.
            if (previousArtistIndex > -1) {
                val previousArtist = artists[previousArtistIndex]
                artists[previousArtistIndex] = Artist(
                    previousArtist.name.hashCode().toLong(), previousArtist.name,
                    previousArtist.resolvedName, previousArtist.albums + artistAlbums
                )
            } else {
                artists.add(Artist(name.hashCode().toLong(), name, resolvedName, artistAlbums))
            }
        }

        return artists
    }

    private fun readGenres(context: Context, songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()

        // First, get a cursor for every genre in the android system
        val genreCursor = context.contentResolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
            ),
            null, null, null
        )

        // And then process those into Genre objects
        genreCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                // No non-broken genre would be missing a name.
                val name = cursor.getStringOrNull(nameIndex) ?: continue
                val resolvedName = when (name) {
                    MediaStore.UNKNOWN_STRING -> context.getString(R.string.def_genre)
                    else -> name.getGenreNameCompat() ?: name
                }

                val genre = Genre(id, name, resolvedName)

                linkGenre(context, genre, songs)
                genres.add(genre)
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

        return genres
    }

    private fun linkGenre(context: Context, genre: Genre, songs: List<Song>) {
        // Don't even bother blacklisting here as useless iterations are less expensive than IO
        val songCursor = context.contentResolver.query(
            MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
            arrayOf(MediaStore.Audio.Genres.Members._ID),
            null, null, null
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)

                songs.find { it.id == id }?.let { song ->
                    genre.linkSong(song)
                }
            }
        }
    }
}
