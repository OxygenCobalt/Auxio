package org.oxycblt.auxio.database

data class PlaybackState(
    val id: Long = 0L,
    val songId: Long = -1L,
    val position: Long,
    val parentId: Long = -1L,
    val index: Int,
    val mode: Int,
    val isShuffling: Boolean,
    val shuffleSeed: Long,
    val loopMode: Int,
    val inUserQueue: Boolean
) {
    companion object {
        const val COLUMN_ID = "state_id"
        const val COLUMN_SONG_ID = "song_id"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_ID = "parent_id"
        const val COLUMN_INDEX = "state_index"
        const val COLUMN_MODE = "mode"
        const val COLUMN_IS_SHUFFLING = "is_shuffling"
        const val COLUMN_SHUFFLE_SEED = "shuffle_seed"
        const val COLUMN_LOOP_MODE = "loop_mode"
        const val COLUMN_IN_USER_QUEUE = "is_user_queue"
    }
}
