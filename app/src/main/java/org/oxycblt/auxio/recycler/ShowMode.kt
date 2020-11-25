package org.oxycblt.auxio.recycler

/**
 * An enum for determining what items to show in a given list.
 * @author OxygenCobalt
 */
enum class ShowMode {
    SHOW_GENRES, SHOW_ARTISTS, SHOW_ALBUMS, SHOW_SONGS;

    /**
     * Make a slice of all the values that this ShowMode covers.
     *
     * ex. SHOW_ARTISTS would return SHOW_ARTISTS, SHOW_ALBUMS, and SHOW_SONGS
     * @return The values that this ShowMode covers.
     */
    fun getChildren(): List<ShowMode> {
        val vals = values()

        return vals.slice(vals.indexOf(this) until vals.size)
    }
}
