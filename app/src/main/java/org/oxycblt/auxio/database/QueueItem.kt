package org.oxycblt.auxio.database

/**
 * A database entity that stores a simplified representation of a song in a queue.
 * @property id The database entity's id
 * @property songHash The hash for the song represented
 * @property albumHash The hash for the album represented
 * @property isUserQueue A bool for if this queue item is a user queue item or not
 * @author OxygenCobalt
 */
data class QueueItem(
    var id: Long = 0L,
    val songHash: Int,
    val albumHash: Int,
    val isUserQueue: Boolean = false
) {
    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_ALBUM_HASH = "album"
        const val COLUMN_IS_USER_QUEUE = "is_user_queue"
    }
}
