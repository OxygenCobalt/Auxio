package org.oxycblt.auxio.database

/**
 * A database entity that stores a compressed variant of the current playback state.
 * @property id - The database key for this state
 * @property songId - The song that is currently playing
 * @property parentId - The parent that is being played from [-1 if none]
 * @property index - The current index in the queue.
 * @property mode - The integer form of the current [org.oxycblt.auxio.playback.state.PlaybackMode]
 * @property isShuffling - A bool for if the queue was shuffled
 * @property loopMode - The integer form of the current [org.oxycblt.auxio.playback.state.LoopMode]
 * @property inUserQueue - A bool for if the state was currently playing from the user queue.
 * @author OxygenCobalt
 */
data class PlaybackState(
    val id: Long = 0L,
    val songId: Long = -1L,
    val position: Long,
    val parentId: Long = -1L,
    val index: Int,
    val mode: Int,
    val isShuffling: Boolean,
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
        const val COLUMN_LOOP_MODE = "loop_mode"
        const val COLUMN_IN_USER_QUEUE = "is_user_queue"
    }
}
