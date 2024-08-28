/*
 * Copyright (c) 2024 Auxio Project
 * MediaSessionInterface.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.oxycblt.auxio.BuildConfig
import javax.inject.Inject
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.service.MediaSessionUID
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode

class MediaSessionInterface
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val commandFactory: PlaybackCommand.Factory,
    private val musicRepository: MusicRepository,
) : MediaSessionCompat.Callback() {
    override fun onPrepare() {
        super.onPrepare()
    }

    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPrepareFromMediaId(mediaId, extras)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        val uid = MediaSessionUID.fromString(mediaId ?: return) ?: return
        val command = expandIntoCommand(uid)
        requireNotNull(command) { "Invalid playback configuration" }
        playbackManager.play(command)
    }

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        super.onPrepareFromSearch(query, extras)
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        // STUB: Unimplemented, no search engine
    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
        super.onPrepareFromUri(uri, extras)
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        // STUB
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        super.onAddQueueItem(description)
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val uid = MediaSessionUID.fromString(description.mediaId ?: return) ?: return
        val song =
            when (uid) {
                is MediaSessionUID.SingleItem -> deviceLibrary.findSong(uid.uid)
                is MediaSessionUID.ChildItem -> deviceLibrary.findSong(uid.childUid)
                else -> null
            }
                ?: return
        playbackManager.addToQueue(song)
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
        super.onRemoveQueueItem(description)
        val at = description.extras?.getInt(KEY_QUEUE_POS) ?: return
        playbackManager.removeQueueItem(at)
    }

    override fun onPlay() {
        playbackManager.playing(true)
    }

    override fun onPause() {
        playbackManager.playing(false)
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

    override fun onFastForward() {
        playbackManager.next()
    }

    override fun onRewind() {
        playbackManager.seekTo(0)
        playbackManager.playing(true)
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        playbackManager.repeatMode(
            when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ALL -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_GROUP -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_ONE -> RepeatMode.TRACK
                else -> RepeatMode.NONE
            })
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        playbackManager.shuffled(
            shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP)
    }

    override fun onSkipToQueueItem(id: Long) {
        playbackManager.goto(id.toInt())
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        super.onCustomAction(action, extras)
        // Service already handles intents from the old notification actions, easier to
        // plug into that system.
        context.sendBroadcast(Intent(action))
    }

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackActions.ACTION_EXIT))
    }

    private fun expandIntoCommand(uid: MediaSessionUID): PlaybackCommand? {
        val music: Music
        var parent: MusicParent? = null
        when (uid) {
            is MediaSessionUID.SingleItem -> {
                music = musicRepository.find(uid.uid) ?: return null
            }
            is MediaSessionUID.ChildItem -> {
                music = musicRepository.find(uid.childUid) ?: return null
                parent = musicRepository.find(uid.parentUid) as? MusicParent ?: return null
            }
            else -> return null
        }

        return when (music) {
            is Song -> inferSongFromParent(music, parent)
            is Album -> commandFactory.album(music, ShuffleMode.OFF)
            is Artist -> commandFactory.artist(music, ShuffleMode.OFF)
            is Genre -> commandFactory.genre(music, ShuffleMode.OFF)
            is Playlist -> commandFactory.playlist(music, ShuffleMode.OFF)
        }
    }

    private fun inferSongFromParent(music: Song, parent: MusicParent?) =
        when (parent) {
            is Album -> commandFactory.songFromAlbum(music, ShuffleMode.IMPLICIT)
            is Artist -> commandFactory.songFromArtist(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromArtist(music, music.artists[0], ShuffleMode.IMPLICIT)
            is Genre -> commandFactory.songFromGenre(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromGenre(music, music.genres[0], ShuffleMode.IMPLICIT)
            is Playlist -> commandFactory.songFromPlaylist(music, parent, ShuffleMode.IMPLICIT)
            null -> commandFactory.songFromAll(music, ShuffleMode.IMPLICIT)
        }

    companion object {
        const val KEY_QUEUE_POS = BuildConfig.APPLICATION_ID + ".metadata.QUEUE_POS"
        const val ACTIONS =
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_REWIND or
                PlaybackStateCompat.ACTION_STOP
    }
}
