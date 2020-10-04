package org.oxycblt.auxio.recycler

enum class ShowMode(val constant: Long) {
    SHOW_GENRES(0), SHOW_ARTISTS(1), SHOW_ALBUMS(2), SHOW_SONGS(3);

    // Make a slice of all the values that this ShowMode covers.
    // ex. SHOW_ARTISTS would return SHOW_ARTISTS, SHOW_ALBUMS, and SHOW_SONGS
    fun getChildren(): List<ShowMode> {
        val vals = values()

        return vals.slice(vals.indexOf(this) until vals.size)
    }
}
