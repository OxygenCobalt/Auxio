package org.oxycblt.auxio.playback.system

import android.content.Context
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager

class PlaybackSessionConnector(
    private val context: Context,
    private val mediaSession: MediaSessionCompat
) : PlaybackStateManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()

    private val emptyMetadata = MediaMetadataCompat.Builder().build()
    private val state = PlaybackStateCompat.Builder()
        .setActions(ACTIONS)

    private var playerState = PlaybackStateCompat.STATE_NONE
    private var playerPosition = playbackManager.position

    init {
        playbackManager.addCallback(this)

        onSongUpdate(playbackManager.song)
        onPlayingUpdate(playbackManager.isPlaying)
        onPositionUpdate(playbackManager.position)
    }

    fun release() {
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        if (song == null) {
            mediaSession.setMetadata(emptyMetadata)
            setPlayerState(PlaybackStateCompat.STATE_STOPPED)
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
        setPlayerState(
            if (playbackManager.isPlaying) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            }
        )
    }

    override fun onPositionUpdate(position: Long) {
        playerPosition = position
        updateState()
    }

    private fun setPlayerState(state: Int) {
        playerState = state
        updateState()
    }

    private fun updateState() {
        state.setState(playerState, playerPosition, 1.0f, SystemClock.elapsedRealtime())
        mediaSession.setPlaybackState(state.build())
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
