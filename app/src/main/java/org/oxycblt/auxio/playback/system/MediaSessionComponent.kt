/*
 * Copyright (c) 2021 Auxio Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.playback.system

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.Player
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD

/**
 */
class MediaSessionComponent(private val context: Context, private val player: Player) :
    PlaybackStateManager.Callback,
    Player.Listener,
    SettingsManager.Callback,
    MediaSessionCompat.Callback() {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private val mediaSession = MediaSessionCompat(context, context.packageName)

    val token: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    init {
        mediaSession.setCallback(this)
        playbackManager.addCallback(this)
        settingsManager.addCallback(this)
        player.addListener(this)

        onSongChanged(playbackManager.song)
        onPlayingChanged(playbackManager.isPlaying)
    }

    fun handleMediaButtonIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    fun release() {
        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)
        player.removeListener(this)
        mediaSession.release()
    }

    // --- MEDIASESSION CALLBACKS ---

    override fun onPlay() {
        playbackManager.isPlaying = true
    }

    override fun onPause() {
        playbackManager.isPlaying = false
    }

    override fun onSkipToNext() {
        playbackManager.next()
    }

    override fun onSkipToPrevious() {
        playbackManager.prev()
    }

    override fun onSeekTo(position: Long) {
        player.seekTo(position)
    }

    override fun onRewind() {
        playbackManager.rewind()
        playbackManager.isPlaying = true
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        playbackManager.repeatMode =
            when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ALL -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_GROUP -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_ONE -> RepeatMode.TRACK
                else -> RepeatMode.NONE
            }
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        playbackManager.reshuffle(
            shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP)
    }

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackService.ACTION_EXIT))
    }

    // --- PLAYBACKSTATEMANAGER CALLBACKS ---

    override fun onIndexMoved(index: Int) {
        onSongChanged(playbackManager.song)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        onSongChanged(playbackManager.song)
    }

    private fun onSongChanged(song: Song?) {
        if (song == null) {
            mediaSession.setMetadata(emptyMetadata)
            return
        }

        val artistName = song.resolveIndividualArtistName(context)

        val builder =
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.resolveName(context))
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.resolveName(context))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.resolveName(context))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)

        // Load the cover asynchronously. This is the entire reason I don't use a plain
        // MediaSessionConnector, which AFAIK makes it impossible to load this the way I do
        // without a bunch of stupid race conditions.
        loadBitmap(context, song) { bitmap ->
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            mediaSession.setMetadata(builder.build())
        }
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        invalidateSessionState()
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        mediaSession.setRepeatMode(
            when (repeatMode) {
                RepeatMode.NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
                RepeatMode.TRACK -> PlaybackStateCompat.REPEAT_MODE_ONE
                RepeatMode.ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
            })
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        mediaSession.setShuffleMode(
            if (isShuffled) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            } else {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            })
    }

    // -- SETTINGSMANAGER CALLBACKS --

    override fun onShowCoverUpdate(showCovers: Boolean) {
        onSongChanged(playbackManager.song)
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        onSongChanged(playbackManager.song)
    }

    // -- EXOPLAYER CALLBACKS --

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        invalidateSessionState()
    }

    // --- MISC ---

    private fun invalidateSessionState() {
        logD("Updating media session state")

        // Position updates arrive faster when you upload STATE_PAUSED for some insane reason.
        val state =
            PlaybackStateCompat.Builder()
                .setActions(ACTIONS)
                .setBufferedPosition(player.bufferedPosition)
                .setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    player.currentPosition,
                    1.0f,
                    SystemClock.elapsedRealtime())

        mediaSession.setPlaybackState(state.build())

        state.setState(
            getPlayerState(), player.currentPosition, 1.0f, SystemClock.elapsedRealtime())

        mediaSession.setPlaybackState(state.build())
    }

    private fun getPlayerState(): Int {
        if (playbackManager.song == null) {
            // No song, player should be stopped
            return PlaybackStateCompat.STATE_STOPPED
        }

        // Otherwise play/pause
        return if (playbackManager.isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }
    }

    companion object {
        private val emptyMetadata = MediaMetadataCompat.Builder().build()

        const val ACTIONS =
            PlaybackStateCompat.ACTION_PLAY or
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
