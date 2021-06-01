package org.oxycblt.auxio.playback.system

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

class PlaybackSessionConnector(
    private val context: Context,
    private val mediaSession: MediaSessionCompat
) : PlaybackStateManager.Callback, MediaSessionCompat.Callback() {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val emptyMetadata = MediaMetadataCompat.Builder().build()

    init {
        mediaSession.setCallback(this)
        playbackManager.addCallback(this)

        onSongUpdate(playbackManager.song)
        onPlayingUpdate(playbackManager.isPlaying)
    }

    fun release() {
        playbackManager.removeCallback(this)
    }

    // --- MEDIASESSION CALLBACKS ---

    override fun onPlay() {
        playbackManager.setPlaying(true)
    }

    override fun onPause() {
        playbackManager.setPlaying(false)
    }

    override fun onSkipToNext() {
        playbackManager.next()
    }

    override fun onSkipToPrevious() {
        playbackManager.prev()
    }

    override fun onSeekTo(position: Long) {
        playbackManager.seekTo(position)
    }

    override fun onRewind() {
        playbackManager.rewind()
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        val mode = when (repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_ALL -> LoopMode.ALL
            PlaybackStateCompat.REPEAT_MODE_GROUP -> LoopMode.ALL
            PlaybackStateCompat.REPEAT_MODE_ONE -> LoopMode.TRACK
            else -> LoopMode.NONE
        }

        playbackManager.setLoopMode(mode)
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        playbackManager.setShuffling(
            shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP,
            true
        )
    }

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackNotification.ACTION_EXIT))
    }

    // --- PLAYBACKSTATEMANAGER CALLBACKS ---

    override fun onSongUpdate(song: Song?) {
        invalidateSessionState()

        if (song == null) {
            mediaSession.setMetadata(emptyMetadata)
            return
        }

        val builder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.name)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)

        loadBitmap(context, song) { bitmap ->
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            mediaSession.setMetadata(builder.build())
        }
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        invalidateSessionState()
    }

    override fun onSeek(position: Long) {
        invalidateSessionState()
    }

    // --- MISC ---

    private fun invalidateSessionState() {
        // Position updates arrive faster when you upload STATE_PAUSED for some inane reason.
        val state = PlaybackStateCompat.Builder()
            .setActions(ACTIONS)
            .setState(
                PlaybackStateCompat.STATE_PAUSED,
                playbackManager.position,
                1.0f,
                SystemClock.elapsedRealtime()
            )

        mediaSession.setPlaybackState(state.build())

        state.setState(
            getPlayerState(),
            playbackManager.position,
            1.0f,
            SystemClock.elapsedRealtime()
        )

        mediaSession.setPlaybackState(state.build())
    }

    private fun getPlayerState(): Int {
        if (playbackManager.song == null) {
            return PlaybackStateCompat.STATE_STOPPED
        }

        return if (playbackManager.isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }
    }

    companion object {
        const val ACTIONS = PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
            PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_SEEK_TO or
            PlaybackStateCompat.ACTION_STOP
    }
}
