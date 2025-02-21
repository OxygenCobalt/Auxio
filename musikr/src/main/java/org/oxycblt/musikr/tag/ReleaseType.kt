/*
 * Copyright (c) 2023 Auxio Project
 * ReleaseType.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag

/**
 * The type of release an [Album] is considered. This includes EPs, Singles, Compilations, etc.
 *
 * This class is derived from the MusicBrainz Release Group Type specification. It can be found at:
 * https://musicbrainz.org/doc/Release_Group/Type
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface ReleaseType {
    /**
     * A specification of what kind of performance this release is. If null, the release is
     * considered "Plain".
     */
    val refinement: Refinement?

    /**
     * A plain album.
     *
     * @param refinement A specification of what kind of performance this release is. If null, the
     *   release is considered "Plain".
     */
    data class Album(override val refinement: Refinement?) : ReleaseType

    /**
     * A "Extended Play", or EP. Usually a smaller release consisting of 4-5 songs.
     *
     * @param refinement A specification of what kind of performance this release is. If null, the
     *   release is considered "Plain".
     */
    data class EP(override val refinement: Refinement?) : ReleaseType

    /**
     * A single. Usually a release consisting of 1-2 songs.
     *
     * @param refinement A specification of what kind of performance this release is. If null, the
     *   release is considered "Plain".
     */
    data class Single(override val refinement: Refinement?) : ReleaseType

    /**
     * A compilation. Usually consists of many songs from a variety of artists.
     *
     * @param refinement A specification of what kind of performance this release is. If null, the
     *   release is considered "Plain".
     */
    data class Compilation(override val refinement: Refinement?) : ReleaseType

    /**
     * A soundtrack. Similar to a [Compilation], but created for a specific piece of (usually
     * visual) media.
     */
    data object Soundtrack : ReleaseType {
        override val refinement: Refinement?
            get() = null
    }

    /**
     * A (DJ) Mix. These are usually one large track consisting of the artist playing several
     * sub-tracks with smooth transitions between them.
     */
    data object Mix : ReleaseType {
        override val refinement: Refinement?
            get() = null
    }

    /**
     * A Mix-tape. These are usually [EP]-sized releases of music made to promote an Artist or a
     * future release.
     */
    data object Mixtape : ReleaseType {
        override val refinement: Refinement?
            get() = null
    }

    /**
     * A demo. These are usually [EP]-sized releases of music made to promote an Artist or a future
     * release.
     */
    data object Demo : ReleaseType {
        override val refinement: Refinement?
            get() = null
    }

    /** A specification of what kind of performance a particular release is. */
    enum class Refinement {
        /** A release consisting of a live performance */
        LIVE,

        /** A release consisting of another Artists remix of a prior performance. */
        REMIX
    }

    companion object {
        /**
         * Parse a [ReleaseType] from a string formatted with the MusicBrainz Release Group Type
         * specification.
         *
         * @param types A list of values consisting of valid release type values.
         * @return A [ReleaseType] consisting of the given types, or null if the types were not
         *   valid.
         */
        fun parse(types: List<String>): ReleaseType? {
            val primary = types.getOrNull(0) ?: return null
            return when {
                // Primary types should be the first types in the sequence.
                primary.equals("album", true) -> types.parseSecondaryTypes(1) { Album(it) }
                primary.equals("ep", true) -> types.parseSecondaryTypes(1) { EP(it) }
                primary.equals("single", true) -> types.parseSecondaryTypes(1) { Single(it) }
                // The spec makes no mention of whether primary types are a pre-requisite for
                // secondary types, so we assume that it's not and map oprhan secondary types
                // to Album release types.
                else -> types.parseSecondaryTypes(0) { Album(it) }
            }
        }

        /**
         * Parse "secondary" types (i.e not [Album], [EP], or [Single]) from a string formatted with
         * the MusicBrainz Release Group Type specification.
         *
         * @param index The index of the release type to parse.
         * @param convertRefinement Code to convert a [Refinement] into a [ReleaseType]
         *   corresponding to the callee's context. This is used in order to handle secondary times
         *   that are actually [Refinement]s.
         * @return A [ReleaseType] corresponding to the secondary type found at that index.
         */
        private inline fun List<String>.parseSecondaryTypes(
            index: Int,
            convertRefinement: (Refinement?) -> ReleaseType
        ): ReleaseType {
            val secondary = getOrNull(index)
            return if (secondary.equals("compilation", true)) {
                // Secondary type is a compilation, actually parse the third type
                // and put that into a compilation if needed.
                parseSecondaryTypeImpl(getOrNull(index + 1)) { Compilation(it) }
            } else {
                // Secondary type is a plain value, use the original values given.
                parseSecondaryTypeImpl(secondary, convertRefinement)
            }
        }

        /**
         * Parse "secondary" types (i.e not [Album], [EP], [Single]) that do not correspond to any
         * child values.
         *
         * @param type The release type value to parse.
         * @param convertRefinement Code to convert a [Refinement] into a [ReleaseType]
         *   corresponding to the callee's context. This is used in order to handle secondary times
         *   that are actually [Refinement]s.
         */
        private inline fun parseSecondaryTypeImpl(
            type: String?,
            convertRefinement: (Refinement?) -> ReleaseType
        ) =
            when {
                // Parse all the types that have no children
                type.equals("soundtrack", true) -> Soundtrack
                type.equals("mixtape/street", true) -> Mixtape
                type.equals("dj-mix", true) -> Mix
                type.equals("live", true) -> convertRefinement(Refinement.LIVE)
                type.equals("remix", true) -> convertRefinement(Refinement.REMIX)
                type.equals("demo", true) -> Demo
                else -> convertRefinement(null)
            }
    }
}
