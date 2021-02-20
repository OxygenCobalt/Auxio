package org.oxycblt.auxio.music

import android.net.Uri

// --- MUSIC MODELS ---

/**
 * The base data object for all music.
 * @property id The ID that is assigned to this object
 * @property name The name of this object (Such as a song title)
 * @author OxygenCobalt
 */
sealed class BaseModel {
    abstract val id: Long
    abstract val name: String
}

/**
 * [BaseModel] variant that denotes that this object is a parent of other data objects, such
 * as an [Album] or [Artist]
 */
sealed class Parent : BaseModel()

/**
 * The data object for a song. Inherits [BaseModel].
 * @property fileName The raw filename for this track
 * @property albumId  The Song's Album ID. Never use this outside of when attaching a song to its album.
 * @property track    The Song's Track number
 * @property duration The duration of the song, in millis.
 * @property album    The Song's parent album. Use this instead of [albumId].
 * @property genre    The Song's [Genre]
 * @property seconds  The Song's duration in seconds
 * @property formattedDuration The Song's duration as a duration string.
 * @author OxygenCobalt
 */
data class Song(
    override val id: Long = -1,
    override val name: String,
    val fileName: String,
    val albumId: Long = -1,
    val track: Int = -1,
    val duration: Long = 0,
) : BaseModel() {
    private var mAlbum: Album? = null
    private var mGenre: Genre? = null

    val genre: Genre? get() = mGenre
    val album: Album get() = requireNotNull(mAlbum)

    fun linkAlbum(album: Album) {
        if (mAlbum == null) {
            mAlbum = album
        }
    }

    fun linkGenre(genre: Genre) {
        if (mGenre == null) {
            mGenre = genre
        }
    }

    val seconds = duration / 1000
    val formattedDuration: String = seconds.toDuration()
}

/**
 * The data object for an album. Inherits [Parent].
 * @property artistName    The name of the parent artist. Do not use this outside of creating the artist from albums
 * @property coverUri      The [Uri] for the album's cover. **Load this using Coil.**
 * @property year          The year this album was released. 0 if there is none in the metadata.
 * @property artist        The Album's parent [Artist]. use this instead of [artistName]
 * @property songs         The Album's child [Song]s.
 * @property totalDuration The combined duration of all of the album's child songs, formatted.
 * @author OxygenCobalt
 */
data class Album(
    override val id: Long = -1,
    override val name: String,
    val artistName: String,
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0
) : Parent() {
    private var mArtist: Artist? = null
    val artist: Artist get() = requireNotNull(mArtist)

    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    val totalDuration: String get() = songs.sumOf { it.seconds }.toDuration()

    fun linkArtist(artist: Artist) {
        mArtist = artist
    }

    fun linkSongs(songs: List<Song>) {
        for (song in songs) {
            song.linkAlbum(this)
            mSongs.add(song)
        }
    }
}

/**
 * The data object for an artist. Inherits [Parent]
 * @property albums The list of all [Album]s in this artist
 * @property genre  The most prominent genre for this artist
 * @property songs  The list of all [Song]s in this artist
 * @author OxygenCobalt
 */
data class Artist(
    override val id: Long = -1,
    override val name: String,
    val albums: List<Album>
) : Parent() {
    init {
        albums.forEach { album ->
            album.linkArtist(this)
        }
    }

    val genre: Genre? by lazy {
        songs.map { it.genre }.maxByOrNull { it?.songs?.size ?: 0 }
    }

    val songs: List<Song> by lazy {
        albums.flatMap { it.songs }
    }
}

/**
 * The data object for a genre. Inherits [Parent]
 * @property songs   The list of all [Song]s in this genre.
 * @property displayName A name that can be displayed without it showing up as an integer. ***USE THIS INSTEAD OF [name]!!!!***
 * @author OxygenCobalt
 */
data class Genre(
    override val id: Long = -1,
    override val name: String,
) : Parent() {
    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    val displayName: String by lazy {
        if (name.contains(Regex("[0123456789)]"))) {
            name.toNamedGenre() ?: name
        } else {
            name
        }
    }

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration()

    fun linkSong(song: Song) {
        mSongs.add(song)
        song.linkGenre(this)
    }
}

/**
 * A data object used solely for the "Header" UI element. Inherits [BaseModel].
 * @property isAction Value that marks whether this header should have an action attached to it.
 * @author OxygenCobalt
 */
data class Header(
    override val id: Long = -1,
    override val name: String = "",
    val isAction: Boolean = false
) : BaseModel()
