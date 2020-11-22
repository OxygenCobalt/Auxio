package org.oxycblt.auxio.database

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
