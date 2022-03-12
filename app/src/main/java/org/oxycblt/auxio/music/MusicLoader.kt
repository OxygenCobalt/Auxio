package org.oxycblt.auxio.music

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.text.isDigitsOnly
import org.oxycblt.auxio.R
import org.oxycblt.auxio.excluded.ExcludedDatabase
import org.oxycblt.auxio.util.logD

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
 * too straightforward for this contract that was dropped on it's head as a baby. So instead, you
 * have to query for each genre, query all the songs in each genre, and then iterate through those
 * songs to link every song with their genre. This is not documented anywhere, and the
 * O(mom im scared) algorithm you have to run to get it working single-handedly DOUBLES Auxio's
 * loading times. At no point have the devs considered that this system is absolutely insane, and
 * instead focused on adding infuriat- I mean nice proprietary extensions to MediaStore for their
 * own Google Play Music, and of course every Google Play Music user knew how great that turned
 * out!
 *
 * It's not even ergonomics that makes this API bad. It's base implementation is completely borked
 * as well. Did you know that MediaStore doesn't accept dates that aren't from ID3v2.3 MP3 files?
 * I sure didn't, until I decided to upgrade my music collection to ID3v2.4 and FLAC only to see
 * that the metadata parser has a brain aneurysm the moment it stumbles upon a dreaded TRDC or
 * DATE tag. Once again, this is because internally android uses an ancient in-house metadata
 * parser to get everything indexed, and so far they have not bothered to modernize this parser
 * or even switch it to something more powerful like Taglib, not even in Android 12. ID3v2.4 has
 * been around for *21 years.* *It can drink now.* All of my what.
 *
 * Not to mention all the other infuriating quirks. Album artists can't be accessed from the albums
 * table, so we have to go for the less efficient "make a big query on all the songs lol" method
 * so that songs don't end up fragmented across artists. Pretty much every OEM has added some
 * extension or quirk to MediaStore that I cannot reproduce, with some OEMs (COUGHSAMSUNGCOUGH)
 * crippling the normal tables so that you're railroaded into their music app. The way I do
 * blacklisting relies on a semi-deprecated method, and the supposedly "modern" method is SLOWER and
 * causes even more problems since I have to manage databases across version boundaries. Sometimes
 * music will have a deformed clone that I can't filter out, sometimes Genres will just break for
 * no reason, and sometimes tags encoded in UTF-8 will be interpreted as anything from UTF-16 to
 * Latin-1 to *Shift JIS* WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY
 *
 * Is there anything we can do about it? No. Google has routinely shut down issues that begged google
 * to fix glaring issues with MediaStore or to just take the API behind the woodshed and shoot it.
 * Largely because they have zero incentive to improve it given how "obscure" local music listening
 * is. As a result, some players like Vanilla and VLC just hack their own pseudo-MediaStore
 * implementation from their own (better) parsers, but this is both infeasible for Auxio due to how
 * incredibly slow it is to get a file handle from the android sandbox AND how much harder it is to
 * manage a database of your own media that mirrors the filesystem perfectly. And even if I set
 * aside those crippling issues and changed my indexer to that, it would face the even larger
 * problem of how google keeps trying to kill the filesystem and force you into their
 * ContentResolver API. In the future MediaStore could be the only system we have, which is also the
 * day that greenland melts and birthdays stop happening forever.
 *
 * I'm pretty sure nothing is going to happen and MediaStore will continue to be neglected and
 * probably deprecated eventually for a "new" API that just coincidentally excludes music indexing.
 * Because go screw yourself for wanting to listen to music you own. Be a good consoomer and listen
 * to your AlgoPop StreamMix™.
 *
 * I wish I was born in the neolithic.
 *
 * @author OxygenCobalt
 */
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

        // Sanity check: Ensure that all songs are linked up to albums/artists/genres.
        for (song in songs) {
            if (song.internalIsMissingAlbum ||
                song.internalIsMissingArtist ||
                song.internalIsMissingGenre
            ) {
                throw IllegalStateException(
                    "Found malformed song: ${song.name} [" +
                        "album: ${!song.internalIsMissingAlbum} " +
                        "artist: ${!song.internalIsMissingArtist} " +
                        "genre: ${!song.internalIsMissingGenre}]"
                )
            }
        }

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
        // TODO: Determine if grokking the actual DATA value outside of SQL is more or less
        //  efficient than the current system
        for (path in paths) {
            selector += " AND ${MediaStore.Audio.Media.DATA} NOT LIKE ?"
            args += "$path%" // Append % so that the selector properly detects children
        }

        // TODO: Move all references to contentResolver into a single variable so we can
        //  avoid accidentally removing the applicationContext fix
        context.applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                AUDIO_COLUMN_ALBUM_ARTIST
            ),
            selector, args.toTypedArray(), null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val fileIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
            val durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val yearIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
            val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val albumArtistIndex = cursor.getColumnIndexOrThrow(AUDIO_COLUMN_ALBUM_ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex)
                val fileName = cursor.getString(fileIndex)

                // The TRACK field is for some reason formatted as DTTT, where D is the disk
                // and T is the track. This is dumb and insane and forces me to mangle track
                // numbers above 1000 but there is nothing we can do that won't break the app
                // below API 30.
                // TODO: Disk number support?
                val track = cursor.getIntOrNull(trackIndex)?.mod(1000)

                val duration = cursor.getLong(durationIndex)
                val year = cursor.getIntOrNull(yearIndex)

                val album = cursor.getString(albumIndex)
                val albumId = cursor.getLong(albumIdIndex)

                // If the artist field is <unknown>, make it null. This makes handling the
                // insanity of the artist field easier later on.
                val artist = cursor.getStringOrNull(artistIndex)?.run {
                    if (this == MediaStore.UNKNOWN_STRING) {
                        null
                    } else {
                        this
                    }
                }

                val albumArtist = cursor.getStringOrNull(albumArtistIndex)

                // Note: Directory parsing is currently disabled until artist images are added.
                // val dirs = cursor.getStringOrNull(dataIndex)?.run {
                //     substringBeforeLast("/", "").ifEmpty { null }
                // }

                songs.add(
                    Song(
                        title,
                        fileName,
                        duration,
                        track,
                        id,
                        year,
                        album,
                        albumId,
                        artist,
                        albumArtist,
                    )
                )
            }
        }

        // Deduplicate songs to prevent (most) deformed music clones
        songs = songs.distinctBy {
            it.name to it.internalMediaStoreAlbumName to it.internalMediaStoreArtistName to
                it.internalMediaStoreAlbumArtistName to it.track to it.duration
        }.toMutableList()

        logD("Successfully loaded ${songs.size} songs")

        return songs
    }

    private fun buildAlbums(songs: List<Song>): List<Album> {
        // Group up songs by their lowercase artist and album name. This serves two purposes:
        // 1. Sometimes artist names can be styled differently, e.g "Rammstein" vs. "RAMMSTEIN".
        // This makes sure both of those are resolved into a single artist called "Rammstein"
        // 2. Sometimes MediaStore will split album IDs up if the songs differ in format. This
        // ensures that all songs are unified under a single album.
        // This does come with some costs, it's far slower than using the album ID itself, and it
        // may result in an unrelated album art being selected depending on the song chosen as
        // the template, but it seems to work pretty well.
        val albums = mutableListOf<Album>()
        val songsByAlbum = songs.groupBy { song ->
            song.internalGroupingId
        }

        for (entry in songsByAlbum) {
            val albumSongs = entry.value

            // Use the song with the latest year as our metadata song.
            // This allows us to replicate the LAST_YEAR field, which is useful as it means that
            // weird years like "0" wont show up if there are alternatives.
            // TODO: Weigh songs with null years lower than songs with zero years
            val templateSong = requireNotNull(
                albumSongs.maxByOrNull { it.internalMediaStoreYear ?: 0 }
            )
            val albumName = templateSong.internalMediaStoreAlbumName
            val albumYear = templateSong.internalMediaStoreYear
            val albumCoverUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                templateSong.internalMediaStoreAlbumId
            )
            val artistName = templateSong.internalGroupingArtistName

            albums.add(
                Album(
                    albumName,
                    albumYear,
                    albumCoverUri,
                    albumSongs,
                    artistName,
                )
            )
        }

        logD("Successfully built ${albums.size} albums")

        return albums
    }

    private fun buildArtists(context: Context, albums: List<Album>): List<Artist> {
        val artists = mutableListOf<Artist>()
        val albumsByArtist = albums.groupBy { it.internalGroupingArtistName }

        for (entry in albumsByArtist) {
            val artistName = entry.key
            val resolvedName = when (artistName) {
                MediaStore.UNKNOWN_STRING -> context.getString(R.string.def_artist)
                else -> artistName
            }
            val artistAlbums = entry.value

            // Album deduplication does not eliminate every case of fragmented artists, do
            // we deduplicate in the artist creation step as well.
            // Note that we actually don't do this in groupBy. This is generally because using
            // a template song may not result in the best possible artist name in all cases.
            val previousArtistIndex = artists.indexOfFirst { artist ->
                artist.name.lowercase() == artistName.lowercase()
            }

            if (previousArtistIndex > -1) {
                val previousArtist = artists[previousArtistIndex]
                logD("Merging duplicate artist into pre-existing artist ${previousArtist.name}")
                artists[previousArtistIndex] = Artist(
                    previousArtist.name,
                    previousArtist.resolvedName,
                    previousArtist.albums + artistAlbums
                )
            } else {
                artists.add(
                    Artist(
                        artistName,
                        resolvedName,
                        artistAlbums
                    )
                )
            }
        }

        logD("Successfully built ${artists.size} artists")

        return artists
    }

    private fun readGenres(context: Context, songs: List<Song>): List<Genre> {
        val genres = mutableListOf<Genre>()

        val genreCursor = context.applicationContext.contentResolver.query(
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
                // Genre names can be a normal name, an ID3v2 constant, or null. Normal names are
                // resolved as usual, but null values don't make sense and are often junk anyway,
                // so we skip genres that have them.
                val id = cursor.getLong(idIndex)
                val name = cursor.getStringOrNull(nameIndex) ?: continue
                val resolvedName = name.genreNameCompat ?: name
                val genreSongs = queryGenreSongs(context, id, songs) ?: continue

                genres.add(
                    Genre(
                        name,
                        resolvedName,
                        genreSongs
                    )
                )
            }
        }

        val songsWithoutGenres = songs.filter { it.internalIsMissingGenre }

        if (songsWithoutGenres.isNotEmpty()) {
            // Songs that don't have a genre will be thrown into an unknown genre.
            val unknownGenre = Genre(
                name = MediaStore.UNKNOWN_STRING,
                resolvedName = context.getString(R.string.def_genre),
                songsWithoutGenres
            )

            genres.add(unknownGenre)
        }

        logD("Successfully loaded ${genres.size} genres")

        return genres
    }

    private fun queryGenreSongs(context: Context, genreId: Long, songs: List<Song>): List<Song>? {
        val genreSongs = mutableListOf<Song>()

        // Don't even bother blacklisting here as useless iterations are less expensive than IO
        val songCursor = context.applicationContext.contentResolver.query(
            MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
            arrayOf(MediaStore.Audio.Genres.Members._ID),
            null, null, null
        )

        songCursor?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                songs.find { it.internalMediaStoreId == id }?.let { song ->
                    genreSongs.add(song)
                }
            }
        }

        // Some genres might be empty due to MediaStore insanity.
        // If that is the case, we drop them.
        return genreSongs.ifEmpty { null }
    }

    private val String.genreNameCompat: String? get() {
        if (isDigitsOnly()) {
            // ID3v1, just parse as an integer
            return legacyGenreTable.getOrNull(toInt())
        }

        if (startsWith('(') && endsWith(')')) {
            // ID3v2.3/ID3v2.4, parse out the parentheses and get the integer
            // Any genres formatted as "(CHARS)" will be ignored.
            val genreInt = substring(1 until lastIndex).toIntOrNull()
            if (genreInt != null) {
                return legacyGenreTable.getOrNull(genreInt)
            }
        }

        // Current name is fine.
        return null
    }

    companion object {
        /**
         * The album_artist MediaStore field has existed since at least API 21, but until API
         * 30 it was a proprietary extension for Google Play Music and was not documented.
         * Since this field probably works on all versions Auxio supports, we suppress the
         * warning about using a possibly-unsupported constant.
         */
        @Suppress("InlinedApi")
        const val AUDIO_COLUMN_ALBUM_ARTIST = MediaStore.Audio.AudioColumns.ALBUM_ARTIST

        /**
         * A complete array of all the hardcoded genre values for ID3(v2), contains standard genres and
         * winamp extensions.
         */
        private val legacyGenreTable = arrayOf(
            // ID3 Standard
            "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop",
            "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock",
            "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack",
            "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance",
            "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise",
            "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop",
            "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic",
            "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta",
            "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret",
            "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal",
            "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock",

            // Winamp Extensions
            "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin",
            "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock",
            "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus",
            "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music",
            "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam",
            "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul",
            "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall",
            "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "Britpop",
            "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal",
            "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa",
            "Thrash Metal", "Anime", "JPop", "Synthpop",

            // Winamp 5.6+ extensions, used by EasyTAG and friends
            // The only reason I include this set is because post-rock is a based genre and
            // deserves a slot.
            "Abstract", "Art Rock", "Baroque", "Bhangra", "Big Beat", "Breakbeat", "Chillout",
            "Downtempo", "Dub", "EBM", "Eclectic", "Electro", "Electroclash", "Emo", "Experimental",
            "Garage", "Global", "IDM", "Illbient", "Industro-Goth", "Jam Band", "Krautrock",
            "Leftfield", "Lounge", "Math Rock", "New Romantic", "Nu-Breakz", "Post-Punk",
            "Post-Rock", "Psytrance", "Shoegaze", "Space Rock", "Trop Rock", "World Music",
            "Neoclassical", "Audiobook", "Audio Theatre", "Neue Deutsche Welle", "Podcast",
            "Indie Rock", "G-Funk", "Dubstep", "Garage Rock", "Psybient"
        )
    }
}
