/*
 * Copyright (c) 2023 Auxio Project
 * SearchEngine.kt is part of Auxio.
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
 
package org.oxycblt.auxio.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.Normalizer
import javax.inject.Inject
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

/**
 * Implements the fuzzy-ish searching algorithm used in the search view.
 *
 * @author Alexander Capehart
 */
interface SearchEngine {
    /**
     * Begin a search.
     *
     * @param items The items to search over.
     * @param query The query to search for.
     * @return A list of items filtered by the given query.
     */
    suspend fun search(items: Items, query: String): Items

    /**
     * Input/output data to use with [SearchEngine].
     *
     * @param songs A list of [Song]s, null if empty.
     * @param albums A list of [Album]s, null if empty.
     * @param artists A list of [Artist]s, null if empty.
     * @param genres A list of [Genre]s, null if empty.
     */
    data class Items(
        val songs: List<Song>?,
        val albums: List<Album>?,
        val artists: List<Artist>?,
        val genres: List<Genre>?
    )
}

class SearchEngineImpl @Inject constructor(@ApplicationContext private val context: Context) :
    SearchEngine {
    override suspend fun search(items: SearchEngine.Items, query: String) =
        SearchEngine.Items(
            songs = items.songs?.searchListImpl(query) { q, song -> song.path.name.contains(q) },
            albums = items.albums?.searchListImpl(query),
            artists = items.artists?.searchListImpl(query),
            genres = items.genres?.searchListImpl(query))

    /**
     * Search a given [Music] list.
     *
     * @param query The query to search for. The routine will compare this query to the names of
     *   each object in the list and
     * @param fallback Additional comparison code to run if the item does not match the query
     *   initially. This can be used to compare against additional attributes to improve search
     *   result quality.
     */
    private inline fun <T : Music> List<T>.searchListImpl(
        query: String,
        fallback: (String, T) -> Boolean = { _, _ -> false }
    ) =
        filter {
                // See if the plain resolved name matches the query. This works for most
                // situations.
                val name = it.resolveName(context)
                if (name.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                // See if the sort name matches. This can sometimes be helpful as certain
                // libraries
                // will tag sort names to have a alphabetized version of the title.
                val sortName = it.rawSortName
                if (sortName != null && sortName.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                // As a last-ditch effort, see if the normalized name matches. This will replace
                // any non-alphabetical characters with their alphabetical representations,
                // which
                // could make it match the query.
                val normalizedName =
                    NORMALIZE_POST_PROCESSING_REGEX.replace(
                        Normalizer.normalize(name, Normalizer.Form.NFKD), "")
                if (normalizedName.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                fallback(query, it)
            }
            .ifEmpty { null }

    private companion object {
        /**
         * Converts the output of [Normalizer] to remove any junk characters added by it's
         * replacements, alongside punctuation.
         */
        val NORMALIZE_POST_PROCESSING_REGEX =
            Regex("(\\p{InCombiningDiacriticalMarks}+)|(\\p{Punct})")
    }
}
