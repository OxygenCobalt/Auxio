/*
 * Copyright (c) 2024 Auxio Project
 * MediaSessionPlayer.kt is part of Auxio.
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

import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.service.MediaSessionUID
import org.oxycblt.auxio.music.service.toSong
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode

/**
 * A thin wrapper around the player instance that takes all the events I know MediaSession will send
 * and routes them to PlaybackStateManager so I know that they will work the way I want it to.
 *
 * @author Alexander Capehart
 */
class MediaSessionPlayer(
    player: Player,
    private val playbackManager: PlaybackStateManager,
    private val commandFactory: PlaybackCommand.Factory,
    private val musicRepository: MusicRepository
) : ForwardingPlayer(player) {
    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands()
            .buildUpon()
            .addAll(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS)
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        // We can always skip forward and backward (this is to retain parity with the old behavior)
        return super.isCommandAvailable(command) ||
            command in setOf(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS)
    }

    override fun setMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        // We assume the only people calling this method are going to be the MediaSession callbacks,
        // since anything else (like newPlayback) will be calling directly on the player. As part
        // of this, we expand the given MediaItems into the command that should be sent to the
        // player.
        val command =
            if (mediaItems.size > 1) {
                this.playMediaItemSelection(mediaItems, startIndex)
            } else {
                this.playSingleMediaItem(mediaItems.first())
            }
        requireNotNull(command) { "Invalid playback configuration" }
        playbackManager.play(command)
        if (startPositionMs != C.TIME_UNSET) {
            playbackManager.seekTo(startPositionMs)
        }
    }

    private fun playMediaItemSelection(
        mediaItems: List<MediaItem>,
        startIndex: Int
    ): PlaybackCommand? {
        val deviceLibrary = musicRepository.deviceLibrary ?: return null
        val targetSong = mediaItems.getOrNull(startIndex)?.toSong(deviceLibrary)
        val songs = mediaItems.mapNotNull { it.toSong(deviceLibrary) }
        var index = startIndex
        if (targetSong != null) {
            while (songs.getOrNull(index)?.uid != targetSong.uid) {
                index--
            }
        }
        return commandFactory.songs(songs, ShuffleMode.OFF)
    }

    private fun playSingleMediaItem(mediaItem: MediaItem): PlaybackCommand? {
        val uid = MediaSessionUID.fromString(mediaItem.mediaId) ?: return null
        val music: Music
        var parent: MusicParent? = null
        when (uid) {
            is MediaSessionUID.Single -> {
                music = musicRepository.find(uid.uid) ?: return null
            }
            is MediaSessionUID.Joined -> {
                music = musicRepository.find(uid.childUid) ?: return null
                parent = musicRepository.find(uid.parentUid) as? MusicParent ?: return null
            }
            else -> return null
        }

        return when (music) {
            is Song -> inferSongFromParentCommand(music, parent)
            is Album -> commandFactory.album(music, ShuffleMode.OFF)
            is Artist -> commandFactory.artist(music, ShuffleMode.OFF)
            is Genre -> commandFactory.genre(music, ShuffleMode.OFF)
            is Playlist -> commandFactory.playlist(music, ShuffleMode.OFF)
        }
    }

    private fun inferSongFromParentCommand(music: Song, parent: MusicParent?) =
        when (parent) {
            is Album -> commandFactory.songFromAlbum(music, ShuffleMode.IMPLICIT)
            is Artist -> commandFactory.songFromArtist(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromArtist(music, music.artists[0], ShuffleMode.IMPLICIT)
            is Genre -> commandFactory.songFromGenre(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromGenre(music, music.genres[0], ShuffleMode.IMPLICIT)
            is Playlist -> commandFactory.songFromPlaylist(music, parent, ShuffleMode.IMPLICIT)
            null -> commandFactory.songFromAll(music, ShuffleMode.IMPLICIT)
        }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        playbackManager.playing(playWhenReady)
    }

    override fun setRepeatMode(repeatMode: Int) {
        val appRepeatMode =
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.NONE
                Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
            }
        playbackManager.repeatMode(appRepeatMode)
    }

    override fun seekToNext() = playbackManager.next()

    override fun seekToNextMediaItem() = playbackManager.next()

    override fun seekToPrevious() = playbackManager.prev()

    override fun seekToPreviousMediaItem() = playbackManager.prev()

    override fun seekTo(positionMs: Long) = playbackManager.seekTo(positionMs)

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        val indices = unscrambleQueueIndices()
        val fakeIndex = indices.indexOf(mediaItemIndex)
        if (fakeIndex < 0) {
            return
        }
        playbackManager.goto(fakeIndex)
        if (positionMs == C.TIME_UNSET) {
            return
        }
        playbackManager.seekTo(positionMs)
    }

    override fun addMediaItems(index: Int, mediaItems: MutableList<MediaItem>) {
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val songs = mediaItems.mapNotNull { it.toSong(deviceLibrary) }
        when {
            index ==
                currentTimeline.getNextWindowIndex(
                    currentMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled) -> {
                playbackManager.playNext(songs)
            }
            index >= mediaItemCount -> playbackManager.addToQueue(songs)
            else -> error("Unsupported index $index")
        }
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        playbackManager.shuffled(shuffleModeEnabled)
    }

    override fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        val indices = unscrambleQueueIndices()
        val fakeFrom = indices.indexOf(currentIndex)
        if (fakeFrom < 0) {
            return
        }
        val fakeTo =
            if (newIndex >= mediaItemCount) {
                currentTimeline.getLastWindowIndex(shuffleModeEnabled)
            } else {
                indices.indexOf(newIndex)
            }
        if (fakeTo < 0) {
            return
        }
        playbackManager.moveQueueItem(fakeFrom, fakeTo)
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) =
        error("Multi-item queue moves are unsupported")

    override fun removeMediaItem(index: Int) {
        val indices = unscrambleQueueIndices()
        val fakeAt = indices.indexOf(index)
        if (fakeAt < 0) {
            return
        }
        playbackManager.removeQueueItem(fakeAt)
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) =
        error("Any multi-item queue removal is unsupported")

    // These methods I don't want MediaSession calling in any way since they'll do insane things
    // that I'm not tracking. If they do call them, I will know.

    override fun setMediaItem(mediaItem: MediaItem) = notAllowed()

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) = notAllowed()

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) = notAllowed()

    override fun setMediaItems(mediaItems: MutableList<MediaItem>) = notAllowed()

    override fun setMediaItems(mediaItems: MutableList<MediaItem>, resetPosition: Boolean) =
        notAllowed()

    override fun addMediaItem(mediaItem: MediaItem) = notAllowed()

    override fun addMediaItem(index: Int, mediaItem: MediaItem) = notAllowed()

    override fun addMediaItems(mediaItems: MutableList<MediaItem>) = notAllowed()

    override fun replaceMediaItem(index: Int, mediaItem: MediaItem) = notAllowed()

    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: MutableList<MediaItem>
    ) = notAllowed()

    override fun clearMediaItems() = notAllowed()

    override fun setPlaybackSpeed(speed: Float) = notAllowed()

    override fun seekToDefaultPosition() = notAllowed()

    override fun seekToDefaultPosition(mediaItemIndex: Int) = notAllowed()

    override fun seekForward() = notAllowed()

    override fun seekBack() = notAllowed()

    @Deprecated("Deprecated in Java") override fun next() = notAllowed()

    @Deprecated("Deprecated in Java") override fun previous() = notAllowed()

    @Deprecated("Deprecated in Java") override fun seekToPreviousWindow() = notAllowed()

    @Deprecated("Deprecated in Java") override fun seekToNextWindow() = notAllowed()

    override fun play() = playbackManager.playing(true)

    override fun pause() = playbackManager.playing(false)

    override fun prepare() = notAllowed()

    override fun release() = notAllowed()

    override fun stop() = notAllowed()

    override fun hasNextMediaItem() = notAllowed()

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) =
        notAllowed()

    override fun setVolume(volume: Float) = notAllowed()

    override fun setDeviceVolume(volume: Int, flags: Int) = notAllowed()

    override fun setDeviceMuted(muted: Boolean, flags: Int) = notAllowed()

    override fun increaseDeviceVolume(flags: Int) = notAllowed()

    override fun decreaseDeviceVolume(flags: Int) = notAllowed()

    @Deprecated("Deprecated in Java") override fun increaseDeviceVolume() = notAllowed()

    @Deprecated("Deprecated in Java") override fun decreaseDeviceVolume() = notAllowed()

    @Deprecated("Deprecated in Java") override fun setDeviceVolume(volume: Int) = notAllowed()

    @Deprecated("Deprecated in Java") override fun setDeviceMuted(muted: Boolean) = notAllowed()

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) = notAllowed()

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) = notAllowed()

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) = notAllowed()

    override fun setVideoSurface(surface: Surface?) = notAllowed()

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) = notAllowed()

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) = notAllowed()

    override fun setVideoTextureView(textureView: TextureView?) = notAllowed()

    override fun clearVideoSurface() = notAllowed()

    override fun clearVideoSurface(surface: Surface?) = notAllowed()

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) = notAllowed()

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) = notAllowed()

    override fun clearVideoTextureView(textureView: TextureView?) = notAllowed()

    private fun notAllowed(): Nothing = error("MediaSession unexpectedly called this method")
}

fun Player.unscrambleQueueIndices(): List<Int> {
    val timeline = currentTimeline
    if (timeline.isEmpty) {
        return emptyList()
    }
    val queue = mutableListOf<Int>()

    // Add the active queue item.
    val currentMediaItemIndex = currentMediaItemIndex
    queue.add(currentMediaItemIndex)

    // Fill queue alternating with next and/or previous queue items.
    var firstMediaItemIndex = currentMediaItemIndex
    var lastMediaItemIndex = currentMediaItemIndex
    val shuffleModeEnabled = shuffleModeEnabled
    while ((firstMediaItemIndex != C.INDEX_UNSET || lastMediaItemIndex != C.INDEX_UNSET)) {
        // Begin with next to have a longer tail than head if an even sized queue needs to be
        // trimmed.
        if (lastMediaItemIndex != C.INDEX_UNSET) {
            lastMediaItemIndex =
                timeline.getNextWindowIndex(
                    lastMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
            if (lastMediaItemIndex != C.INDEX_UNSET) {
                queue.add(lastMediaItemIndex)
            }
        }
        if (firstMediaItemIndex != C.INDEX_UNSET) {
            firstMediaItemIndex =
                timeline.getPreviousWindowIndex(
                    firstMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
            if (firstMediaItemIndex != C.INDEX_UNSET) {
                queue.add(0, firstMediaItemIndex)
            }
        }
    }

    return queue
}
