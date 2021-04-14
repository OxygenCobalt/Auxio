package org.oxycblt.auxio.database

/**
 * A database entity that stores a compressed variant of the current playback state.
 * @property id - The database key for this state
 * @property songHash - The hash for the currently playing song
 * @property parentHash - The hash for the currently playing parent
 * @property index - The current index in the queue.
 * @property mode - The integer form of the current [org.oxycblt.auxio.playback.state.PlaybackMode]
 * @property isShuffling - A bool for if the queue was shuffled
 * @property loopMode - The integer form of the current [org.oxycblt.auxio.playback.state.LoopMode]
 * @property inUserQueue - A bool for if the state was currently playing from the user queue.
 * @author OxygenCobalt
 */
data class PlaybackState(
    val id: Long = 0L,
    val songHash: Int,
    val position: Long,
    val parentHash: Int,
    val index: Int,
    val mode: Int,
    val isShuffling: Boolean,
    val loopMode: Int,
    val inUserQueue: Boolean
) {
    companion object {
        const val COLUMN_ID = "state_id"
        const val COLUMN_SONG_HASH = "song"
        const val COLUMN_POSITION = "position"
        const val COLUMN_PARENT_HASH = "parent"
        const val COLUMN_INDEX = "_index"
        const val COLUMN_MODE = "mode"
        const val COLUMN_IS_SHUFFLING = "is_shuffling"
        const val COLUMN_LOOP_MODE = "loop_mode"
        const val COLUMN_IN_USER_QUEUE = "is_user_queue"
    }
}
