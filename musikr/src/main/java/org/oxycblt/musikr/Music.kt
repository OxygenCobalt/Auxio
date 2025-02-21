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
 
package org.oxycblt.musikr

import android.net.Uri
import android.os.Parcelable
import androidx.room.TypeConverter
import java.security.MessageDigest
import java.util.UUID
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverCollection
import org.oxycblt.musikr.fs.Format
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.Disc
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.ReleaseType
import org.oxycblt.musikr.tag.ReplayGainAdjustment
import org.oxycblt.musikr.util.toUuidOrNull

/**
 * Abstract music data. This contains universal information about all concrete music
 * implementations, such as identification information and names.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface Music {
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
        private val item: Item,
        private val uuid: UUID
    ) : Parcelable {
        // Cache the hashCode for HashMap efficiency.
        @IgnoredOnParcel private var hashCode = format.hashCode()

        init {
            hashCode = 31 * hashCode + item.hashCode()
            hashCode = 31 * hashCode + uuid.hashCode()
        }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is UID && format == other.format && item == other.item && uuid == other.uuid

        override fun toString() = "${format.namespace}:${item.intCode.toString(16)}-$uuid"

        internal enum class Item(val intCode: Int) {
            // Item used to be MusicType back when the music module was
            // part of Auxio, so these old integer codes remain.
            // TODO: Introduce new UID format that removes these.
            SONG(0xA10B),
            ALBUM(0xA10A),
            ARTIST(0xA109),
            GENRE(0xA108),
            PLAYLIST(0xA107)
        }

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
            @TypeConverter fun toMusicUid(string: String?) = string?.let(Companion::fromString)
        }

        companion object {
            /**
             * Creates an Auxio-style [UID] of random composition. Used if there is no
             * non-subjective, unlikely-to-change metadata of the music.
             *
             * @param item The type of [Item] that created this [UID].
             */
            internal fun auxio(item: Item): UID {
                return UID(Format.AUXIO, item, UUID.randomUUID())
            }

            /**
             * Creates an Auxio-style [UID] with a [UUID] composed of a hash of the non-subjective,
             * unlikely-to-change metadata of the music.
             *
             * @param item The type of [Item] that created this [UID].
             * @param updates Block to update the [MessageDigest] hash with the metadata of the
             *   item. Make sure the metadata hashed semantically aligns with the format
             *   specification.
             * @return A new auxio-style [UID].
             */
            internal fun auxio(item: Item, updates: MessageDigest.() -> Unit): UID {
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
                return UID(Format.AUXIO, item, uuid)
            }

            /**
             * Creates a MusicBrainz-style [UID] with a [UUID] derived from the MusicBrainz ID
             * extracted from a file.
             *
             * @param item The [Item] that created this [UID].
             * @param mbid The analogous MusicBrainz ID for this item that was extracted from a
             *   file.
             * @return A new MusicBrainz-style [UID].
             */
            internal fun musicBrainz(item: Item, mbid: UUID) = UID(Format.MUSICBRAINZ, item, mbid)

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

                val intCode = ids[0].toIntOrNull(16) ?: return null
                val type = Item.entries.firstOrNull { it.intCode == intCode } ?: return null
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
    override val name: Name.Known
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
    /**
     * The [Path] to this audio file. This is only intended for display, [uri] should be favored
     * instead for accessing the audio file.
     */
    val path: Path
    /** The [Format] of the audio file. Only intended for display. */
    val format: Format
    /** The size of the audio file, in bytes. */
    val size: Long
    /** The duration of the audio file, in milliseconds. */
    val durationMs: Long
    /** The bitrate of the audio file, in kbps. */
    val bitrateKbps: Int
    /** The sample rate of the audio file, in Hz. */
    val sampleRateHz: Int
    /** The ReplayGain adjustment to apply during playback. */
    val replayGainAdjustment: ReplayGainAdjustment
    /**
     * The date last modified the audio file was last modified, in milliseconds since the unix
     * epoch.
     */
    val modifiedMs: Long
    /** The time the audio file was added to the device, in milliseconds since the unix epoch. */
    val addedMs: Long
    /** Useful information to quickly obtain the album cover. */
    val cover: Cover?
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
    /** Cover information from album's songs. */
    val covers: CoverCollection
    /** The duration of all songs in the album, in milliseconds. */
    val durationMs: Long
    /** The earliest date a song in this album was added, in milliseconds since the unix epoch. */
    val addedMs: Long
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
    val covers: CoverCollection
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
    val covers: CoverCollection
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
    val covers: CoverCollection
}
