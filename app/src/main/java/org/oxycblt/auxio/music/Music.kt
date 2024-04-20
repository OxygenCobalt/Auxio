/*
 * Copyright (c) 2023 Auxio Project
 * Music.kt is part of Auxio.
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

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import androidx.room.TypeConverter
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.max
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.image.extractor.Cover
import org.oxycblt.auxio.image.extractor.ParentCover
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.concatLocalized
import org.oxycblt.auxio.util.toUuidOrNull

/**
 * Abstract music data. This contains universal information about all concrete music
 * implementations, such as identification information and names.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface Music : Item {
    /**
     * A unique identifier for this music item.
     *
     * @see UID
     */
    val uid: UID

    /** The [Name] of the music item. */
    val name: Name

    /**
     * A unique identifier for a piece of music.
     *
     * [UID] enables a much cheaper and more reliable form of differentiating music, derived from
     * either internal app information or the MusicBrainz ID spec. Using this enables several
     * improvements to music management in this app, including:
     * - Proper differentiation of identical music. It's common for large, well-tagged libraries to
     *   have functionally duplicate items that are differentiated with MusicBrainz IDs, and so
     *   [UID] allows us to properly differentiate between these in the app.
     * - Better music persistence between restarts. Whereas directly storing song names would be
     *   prone to collisions, and storing MediaStore IDs would drift rapidly as the music library
     *   changes, [UID] enables a much stronger form of persistence given it's unique link to a
     *   specific files metadata configuration, which is unlikely to collide with another item or
     *   drift as the music library changes.
     *
     * Note: Generally try to use [UID] as a black box that can only be read, written, and compared.
     * It will not be fun if you try to manipulate it in any other manner.
     *
     * @author Alexander Capehart (OxygenCobalt)
     */
    @Parcelize
    class UID
    private constructor(
        private val format: Format,
        private val type: MusicType,
        private val uuid: UUID
    ) : Parcelable {
        // Cache the hashCode for HashMap efficiency.
        @IgnoredOnParcel private var hashCode = format.hashCode()

        init {
            hashCode = 31 * hashCode + type.hashCode()
            hashCode = 31 * hashCode + uuid.hashCode()
        }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is UID && format == other.format && type == other.type && uuid == other.uuid

        override fun toString() = "${format.namespace}:${type.intCode.toString(16)}-$uuid"

        /**
         * Internal marker of [Music.UID] format type.
         *
         * @param namespace Namespace to use in the [Music.UID]'s string representation.
         */
        private enum class Format(val namespace: String) {
            /** @see auxio */
            AUXIO("org.oxycblt.auxio"),

            /** @see musicBrainz */
            MUSICBRAINZ("org.musicbrainz")
        }

        object TypeConverters {
            /** @see [Music.UID.toString] */
            @TypeConverter fun fromMusicUID(uid: UID?) = uid?.toString()

            /** @see [Music.UID.fromString] */
            @TypeConverter fun toMusicUid(string: String?) = string?.let(UID::fromString)
        }

        companion object {
            /**
             * Creates an Auxio-style [UID] of random composition. Used if there is no
             * non-subjective, unlikely-to-change metadata of the music.
             *
             * @param type The analogous [MusicType] of the item that created this [UID].
             */
            fun auxio(type: MusicType): UID {
                return UID(Format.AUXIO, type, UUID.randomUUID())
            }

            /**
             * Creates an Auxio-style [UID] with a [UUID] composed of a hash of the non-subjective,
             * unlikely-to-change metadata of the music.
             *
             * @param type The analogous [MusicType] of the item that created this [UID].
             * @param updates Block to update the [MessageDigest] hash with the metadata of the
             *   item. Make sure the metadata hashed semantically aligns with the format
             *   specification.
             * @return A new auxio-style [UID].
             */
            fun auxio(type: MusicType, updates: MessageDigest.() -> Unit): UID {
                val digest =
                    MessageDigest.getInstance("SHA-256").run {
                        updates()
                        digest()
                    }
                // Convert the digest to a UUID. This does cleave off some of the hash, but this
                // is considered okay.
                val uuid =
                    UUID(
                        digest[0]
                            .toLong()
                            .shl(56)
                            .or(digest[1].toLong().and(0xFF).shl(48))
                            .or(digest[2].toLong().and(0xFF).shl(40))
                            .or(digest[3].toLong().and(0xFF).shl(32))
                            .or(digest[4].toLong().and(0xFF).shl(24))
                            .or(digest[5].toLong().and(0xFF).shl(16))
                            .or(digest[6].toLong().and(0xFF).shl(8))
                            .or(digest[7].toLong().and(0xFF)),
                        digest[8]
                            .toLong()
                            .shl(56)
                            .or(digest[9].toLong().and(0xFF).shl(48))
                            .or(digest[10].toLong().and(0xFF).shl(40))
                            .or(digest[11].toLong().and(0xFF).shl(32))
                            .or(digest[12].toLong().and(0xFF).shl(24))
                            .or(digest[13].toLong().and(0xFF).shl(16))
                            .or(digest[14].toLong().and(0xFF).shl(8))
                            .or(digest[15].toLong().and(0xFF)))
                return UID(Format.AUXIO, type, uuid)
            }

            /**
             * Creates a MusicBrainz-style [UID] with a [UUID] derived from the MusicBrainz ID
             * extracted from a file.
             *
             * @param type The analogous [MusicType] of the item that created this [UID].
             * @param mbid The analogous MusicBrainz ID for this item that was extracted from a
             *   file.
             * @return A new MusicBrainz-style [UID].
             */
            fun musicBrainz(type: MusicType, mbid: UUID) = UID(Format.MUSICBRAINZ, type, mbid)

            /**
             * Convert a [UID]'s string representation back into a concrete [UID] instance.
             *
             * @param uid The [UID]'s string representation, formatted as
             *   `format_namespace:music_mode_int-uuid`.
             * @return A [UID] converted from the string representation, or null if the string
             *   representation was invalid.
             */
            fun fromString(uid: String): UID? {
                val split = uid.split(':', limit = 2)
                if (split.size != 2) {
                    return null
                }

                val format =
                    when (split[0]) {
                        Format.AUXIO.namespace -> Format.AUXIO
                        Format.MUSICBRAINZ.namespace -> Format.MUSICBRAINZ
                        else -> return null
                    }

                val ids = split[1].split('-', limit = 2)
                if (ids.size != 2) {
                    return null
                }

                val type =
                    MusicType.fromIntCode(ids[0].toIntOrNull(16) ?: return null) ?: return null
                val uuid = ids[1].toUuidOrNull() ?: return null
                return UID(format, type, uuid)
            }
        }
    }
}

/**
 * An abstract grouping of [Song]s and other [Music] data.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface MusicParent : Music {
    /** The child [Song]s of this [MusicParent]. */
    val songs: Collection<Song>
}

/**
 * A song.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Song : Music {
    /** The track number. Will be null if no valid track number was present in the metadata. */
    val track: Int?
    /** The [Disc] number. Will be null if no valid disc number was present in the metadata. */
    val disc: Disc?
    /** The release [Date]. Will be null if no valid date was present in the metadata. */
    val date: Date?
    /**
     * The URI to the audio file that this instance was created from. This can be used to access the
     * audio file in a way that is scoped-storage-safe.
     */
    val uri: Uri
    /** Useful information to quickly obtain the album cover. */
    val cover: Cover
    /**
     * The [Path] to this audio file. This is only intended for display, [uri] should be favored
     * instead for accessing the audio file.
     */
    val path: Path
    /** The [MimeType] of the audio file. Only intended for display. */
    val mimeType: MimeType
    /** The size of the audio file, in bytes. */
    val size: Long
    /** The duration of the audio file, in milliseconds. */
    val durationMs: Long
    /** The ReplayGain adjustment to apply during playback. */
    val replayGainAdjustment: ReplayGainAdjustment
    /** The date the audio file was added to the device, as a unix epoch timestamp. */
    val dateAdded: Long
    /**
     * The parent [Album]. If the metadata did not specify an album, it's parent directory is used
     * instead.
     */
    val album: Album
    /**
     * The parent [Artist]s of this [Song]. Is often one, but there can be multiple if more than one
     * [Artist] name was specified in the metadata. Unliked [Album], artists are prioritized for
     * this field.
     */
    val artists: List<Artist>
    /**
     * The parent [Genre]s of this [Song]. Is often one, but there can be multiple if more than one
     * [Genre] name was specified in the metadata.
     */
    val genres: List<Genre>
}

/**
 * An abstract release group. While it may be called an album, it encompasses other types of
 * releases like singles, EPs, and compilations.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Album : MusicParent {
    /** The [Date.Range] that [Song]s in the [Album] were released. */
    val dates: Date.Range?
    /**
     * The [ReleaseType] of this album, signifying the type of release it actually is. Defaults to
     * [ReleaseType.Album].
     */
    val releaseType: ReleaseType
    /** Cover information from the template song used for the album. */
    val cover: ParentCover
    /** The duration of all songs in the album, in milliseconds. */
    val durationMs: Long
    /** The earliest date a song in this album was added, as a unix epoch timestamp. */
    val dateAdded: Long
    /**
     * The parent [Artist]s of this [Album]. Is often one, but there can be multiple if more than
     * one [Artist] name was specified in the metadata of the [Song]'s. Unlike [Song], album artists
     * are prioritized for this field.
     */
    val artists: List<Artist>
}

/**
 * An abstract artist. These are actually a combination of the artist and album artist tags from
 * within the library, derived from [Song]s and [Album]s respectively.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Artist : MusicParent {
    /** Albums directly credited to this [Artist] via a "Album Artist" tag. */
    val explicitAlbums: Collection<Album>
    /** Albums indirectly credited to this [Artist] via an "Artist" tag. */
    val implicitAlbums: Collection<Album>
    /**
     * The duration of all [Song]s in the artist, in milliseconds. Will be null if there are no
     * songs.
     */
    val durationMs: Long?
    /** Useful information to quickly obtain a (single) cover for a Genre. */
    val cover: ParentCover
    /** The [Genre]s of this artist. */
    val genres: List<Genre>
}

/**
 * A genre.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Genre : MusicParent {
    /** The artists indirectly linked to by the [Artist]s of this [Genre]. */
    val artists: Collection<Artist>
    /** The total duration of the songs in this genre, in milliseconds. */
    val durationMs: Long
    /** Useful information to quickly obtain a (single) cover for a Genre. */
    val cover: ParentCover
}

/**
 * A playlist.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Playlist : MusicParent {
    override val name: Name.Known
    override val songs: List<Song>
    /** The total duration of the songs in this genre, in milliseconds. */
    val durationMs: Long
    /** Useful information to quickly obtain a (single) cover for a Genre. */
    val cover: ParentCover?
}

/**
 * Run [Name.resolve] on each instance in the given list and concatenate them into a [String] in a
 * localized manner.
 *
 * @param context [Context] required
 * @return A concatenated string.
 */
fun <T : Music> List<T>.resolveNames(context: Context) =
    concatLocalized(context) { it.name.resolve(context) }

/**
 * Returns if [Music.name] matches for each item in a list. Useful for scenarios where the display
 * information of an item must be compared without a context.
 *
 * @param other The list of items to compare to.
 * @return True if they are the same (by [Music.name]), false otherwise.
 */
fun <T : Music> List<T>.areNamesTheSame(other: List<T>): Boolean {
    for (i in 0 until max(size, other.size)) {
        val a = getOrNull(i) ?: return false
        val b = other.getOrNull(i) ?: return false
        if (a.name != b.name) {
            return false
        }
    }

    return true
}
