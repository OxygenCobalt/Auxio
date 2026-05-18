package org.oxycblt.auxio.playback.state

/**
 * Persistent UI-facing shuffle scope for playback controls.
 *
 * This is intentionally separate from [ShuffleMode], which is command-time intent used when
 * constructing a new playback command.
 */
enum class ShuffleScope {
    OFF,
    ALL,
    GENRE,
}
