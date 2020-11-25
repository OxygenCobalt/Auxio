package org.oxycblt.auxio.database

/**
 * A database entity that stores a simplified representation of a song in a queue.
 * @property id The database entity's id
 * @property songId The song id for this queue item
 * @property albumId The album id for this queue item, used to make searching quicker
 * @property isUserQueue A bool for if this queue item is a user queue item or not
 */
data class QueueItem(
    var id: Long = 0L,
    val songId: Long = Long.MIN_VALUE,
    val albumId: Long = Long.MIN_VALUE,
    val isUserQueue: Boolean = false
) {
    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_ALBUM_ID = "album_id"
        const val COLUMN_IS_USER_QUEUE = "is_user_queue"
    }
}
