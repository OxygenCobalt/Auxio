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
import java.text.CollationKey
import java.text.Collator
import java.util.UUID
import kotlin.math.max
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.Disc
import org.oxycblt.auxio.music.metadata.ReleaseType
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

    /**
     * The raw name of this item as it was extracted from the file-system. Will be null if the
     * item's name is unknown. When showing this item in a UI, avoid this in favor of [resolveName].
     */
    val rawName: String?

    /**
     * Returns a name suitable for use in the app UI. This should be favored over [rawName] in
     * nearly all cases.
     *
     * @param context [Context] required to obtain placeholder text or formatting information.
     * @return A human-readable string representing the name of this music. In the case that the
     *   item does not have a name, an analogous "Unknown X" name is returned.
     */
    fun resolveName(context: Context): String

    /**
     * The raw sort name of this item as it was extracted from the file-system. This can be used not
     * only when sorting music, but also trying to locate music based on a fuzzy search by the user.
     * Will be null if the item has no known sort name.
     */
    val rawSortName: String?

    /**
     * A black-box value derived from [rawSortName] and [rawName] that can be used for user-friendly
     * sorting in the context of music. This should be preferred over [rawSortName] in most cases.
     * Null if there are no [rawName] or [rawSortName] values to build on.
     */
    val sortName: SortName?

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
        private val mode: MusicMode,
        private val uuid: UUID
    ) : Parcelable {
        // Cache the hashCode for HashMap efficiency.
        @IgnoredOnParcel private var hashCode = format.hashCode()

        init {
            hashCode = 31 * hashCode + mode.hashCode()
            hashCode = 31 * hashCode + uuid.hashCode()
        }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is UID && format == other.format && mode == other.mode && uuid == other.uuid

        override fun toString() = "${format.namespace}:${mode.intCode.toString(16)}-$uuid"

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
             * Creates an Auxio-style [UID] with a [UUID] generated by internal Auxio code.
             *
             * @param mode The analogous [MusicMode] of the item that created this [UID].
             * @param uuid The generated [UUID] for this item.
             * @return A new auxio-style [UID].
             */
            fun auxio(mode: MusicMode, uuid: UUID) = UID(Format.AUXIO, mode, uuid)

            /**
             * Creates a MusicBrainz-style [UID] with a [UUID] derived from the MusicBrainz ID
             * extracted from a file.
             *
             * @param mode The analogous [MusicMode] of the item that created this [UID].
             * @param mbid The analogous MusicBrainz ID for this item that was extracted from a
             *   file.
             * @return A new MusicBrainz-style [UID].
             */
            fun musicBrainz(mode: MusicMode, mbid: UUID) = UID(Format.MUSICBRAINZ, mode, mbid)

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

                val mode =
                    MusicMode.fromIntCode(ids[0].toIntOrNull(16) ?: return null) ?: return null
                val uuid = ids[1].toUuidOrNull() ?: return null
                return UID(format, mode, uuid)
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
    val songs: List<Song>
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
    /**
     * The URI to a MediaStore-provided album cover. These images will be fast to load, but at the
     * cost of image quality.
     */
    val coverUri: Uri
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
    /**
     * All of the [Album]s this artist is credited to. Note that any [Song] credited to this artist
     * will have it's [Album] considered to be "indirectly" linked to this [Artist], and thus
     * included in this list.
     */
    val albums: List<Album>
    /**
     * The duration of all [Song]s in the artist, in milliseconds. Will be null if there are no
     * songs.
     */
    val durationMs: Long?
    /**
     * Whether this artist is considered a "collaborator", i.e it is not directly credited on any
     * [Album].
     */
    val isCollaborator: Boolean
    /** The [Genre]s of this artist. */
    val genres: List<Genre>
}

/**
 * A genre.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Genre : MusicParent {
    /** The albums indirectly linked to by the [Song]s of this [Genre]. */
    val albums: List<Album>
    /** The artists indirectly linked to by the [Artist]s of this [Genre]. */
    val artists: List<Artist>
    /** The total duration of the songs in this genre, in milliseconds. */
    val durationMs: Long
}

/**
 * A playlist.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Playlist : MusicParent {
    /** The albums indirectly linked to by the [Song]s of this [Playlist]. */
    val albums: List<Album>
    /** The total duration of the songs in this genre, in milliseconds. */
    val durationMs: Long
}

/**
 * A black-box datatype for a variation of music names that is suitable for music-oriented sorting.
 * It will automatically handle articles like "The" and numeric components like "An".
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class SortName(name: String, musicSettings: MusicSettings) : Comparable<SortName> {
    private val number: Int?
    private val collationKey: CollationKey
    val thumbString: String?

    init {
        var sortName = name
        if (musicSettings.intelligentSorting) {
            sortName =
                sortName.run {
                    when {
                        length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
                        length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
                        length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
                        else -> this
                    }
                }

            // Parse out numeric portions of the title and use those for sorting, if applicable.
            when (val numericEnd = sortName.indexOfFirst { !it.isDigit() }) {
                // No numeric component.
                0 -> number = null
                // Whole title is numeric.
                -1 -> {
                    number = sortName.toIntOrNull()
                    sortName = ""
                }
                // Part of the title is numeric.
                else -> {
                    number = sortName.slice(0 until numericEnd).toIntOrNull()
                    sortName = sortName.slice(numericEnd until sortName.length)
                }
            }
        } else {
            number = null
        }

        collationKey = COLLATOR.getCollationKey(sortName)

        // Keep track of a string to use in the thumb view.
        // TODO: This needs to be moved elsewhere.
        thumbString = (number?.toString() ?: collationKey?.run { sourceString.first().uppercase() })
    }

    override fun toString(): String = number?.toString() ?: collationKey.sourceString

    override fun compareTo(other: SortName) =
        when {
            number != null && other.number != null -> number.compareTo(other.number)
            number != null && other.number == null -> -1 // a < b
            number == null && other.number != null -> 1 // a > b
            else -> collationKey.compareTo(other.collationKey)
        }

    override fun equals(other: Any?) =
        other is SortName && number == other.number && collationKey == other.collationKey

    override fun hashCode(): Int {
        var hashCode = collationKey.hashCode()
        if (number != null) hashCode = 31 * hashCode + number
        return hashCode
    }

    private companion object {
        val COLLATOR: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    }
}

/**
 * Run [Music.resolveName] on each instance in the given list and concatenate them into a [String]
 * in a localized manner.
 *
 * @param context [Context] required
 * @return A concatenated string.
 */
fun <T : Music> List<T>.resolveNames(context: Context) =
    concatLocalized(context) { it.resolveName(context) }

/**
 * Returns if [Music.rawName] matches for each item in a list. Useful for scenarios where the
 * display information of an item must be compared without a context.
 *
 * @param other The list of items to compare to.
 * @return True if they are the same (by [Music.rawName]), false otherwise.
 */
fun <T : Music> List<T>.areRawNamesTheSame(other: List<T>): Boolean {
    for (i in 0 until max(size, other.size)) {
        val a = getOrNull(i) ?: return false
        val b = other.getOrNull(i) ?: return false
        if (a.rawName != b.rawName) {
            return false
        }
    }

    return true
}
