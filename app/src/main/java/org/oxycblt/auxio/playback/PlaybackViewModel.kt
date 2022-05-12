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
 
package org.oxycblt.auxio.playback

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The ViewModel that provides a UI frontend for [PlaybackStateManager].
 *
 * **PLEASE Use this instead of [PlaybackStateManager], UI's are extremely volatile and this
 * provides an interface that properly sanitizes input and abstracts functions unlike the master
 * class.**
 * @author OxygenCobalt
 *
 * TODO: Completely rework this module to support the new music rescan system, proper android auto
 * and external exposing, and so on.
 * - DO NOT REWRITE IT! THAT'S BAD AND WILL PROBABLY RE-INTRODUCE A TON OF BUGS.
 */
class PlaybackViewModel : ViewModel(), PlaybackStateManager.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val playbackManager = PlaybackStateManager.getInstance()

    private var intentUri: Uri? = null

    private val _song = MutableLiveData<Song?>()
    /** The current song. */
    val song: LiveData<Song?>
        get() = _song
    private val _parent = MutableLiveData<MusicParent?>()
    /** The current model that is being played from, such as an [Album] or [Artist] */
    val parent: LiveData<MusicParent?>
        get() = _parent
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying
    private val _positionSecs = MutableLiveData(0L)
    /** The current playback position, in seconds */
    val positionSecs: LiveData<Long>
        get() = _positionSecs
    private val _repeatMode = MutableLiveData(RepeatMode.NONE)
    /** The current repeat mode, see [RepeatMode] for more information */
    val repeatMode: LiveData<RepeatMode>
        get() = _repeatMode
    private val _isShuffled = MutableLiveData(false)
    val isShuffled: LiveData<Boolean>
        get() = _isShuffled

    private val _nextUp = MutableLiveData(listOf<Song>())
    /** The queue, without the previous items. */
    val nextUp: LiveData<List<Song>>
        get() = _nextUp

    init {
        playbackManager.addCallback(this)

        // If the PlaybackViewModel was cleared [Signified by PlaybackStateManager still being
        // around & the fact that we are in the init function], then attempt to restore the
        // ViewModel state. If it isn't, then wait for MainFragment to give the command to restore
        // PlaybackStateManager.
        if (playbackManager.isInitialized) {
            restorePlaybackState()
        }
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a [song] with the [mode] specified. [mode] will default to the preferred song playback
     * mode of the user if not specified.
     */
    fun playSong(song: Song, mode: PlaybackMode = settingsManager.songPlaybackMode) {
        playbackManager.play(song, mode)
    }

    /**
     * Play an [album].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playAlbum(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            logE("Album is empty, Not playing")

            return
        }

        playbackManager.play(album, shuffled)
    }

    /**
     * Play an [artist].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playArtist(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            logE("Artist is empty, Not playing")

            return
        }

        playbackManager.play(artist, shuffled)
    }

    /**
     * Play a [genre].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playGenre(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            logE("Genre is empty, Not playing")

            return
        }

        playbackManager.play(genre, shuffled)
    }

    /**
     * Play using a file [uri]. This will not play instantly during the initial startup sequence.
     */
    fun playWithUri(uri: Uri, context: Context) {
        // Check if everything is already running to run the URI play
        if (playbackManager.isInitialized && musicStore.library != null) {
            playWithUriInternal(uri, context)
        } else {
            logD("Cant play this URI right now, waiting")

            intentUri = uri
        }
    }

    /** Play with a file URI. This is called after [playWithUri] once its deemed safe to do so. */
    private fun playWithUriInternal(uri: Uri, context: Context) {
        logD("Playing with uri $uri")
        val library = musicStore.library ?: return
        library.findSongForUri(uri, context.contentResolver)?.let { song -> playSong(song) }
    }

    /** Shuffle all songs */
    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    // --- POSITION FUNCTIONS ---

    /** Update the position and push it to [PlaybackStateManager] */
    fun setPosition(progress: Long) {
        playbackManager.seekTo(progress * 1000)
    }

    // --- QUEUE FUNCTIONS ---

    /** Skip to the next song. */
    fun skipNext() {
        playbackManager.next()
    }

    /** Skip to the previous song. */
    fun skipPrev() {
        playbackManager.prev()
    }

    /**
     * Remove a queue item using it's recyclerview adapter index. If the indices are valid, [apply]
     * is called just before the change is committed so that the adapter can be updated.
     */
    fun removeQueueDataItem(adapterIndex: Int, apply: () -> Unit) {
        val index =
            adapterIndex + (playbackManager.queue.size - unlikelyToBeNull(_nextUp.value).size)
        if (index in playbackManager.queue.indices) {
            apply()
            playbackManager.removeQueueItem(index)
        }
    }
    /**
     * Move queue items using their recyclerview adapter indices. If the indices are valid, [apply]
     * is called just before the change is committed so that the adapter can be updated.
     */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int, apply: () -> Unit): Boolean {
        val delta = (playbackManager.queue.size - unlikelyToBeNull(_nextUp.value).size)
        val from = adapterFrom + delta
        val to = adapterTo + delta
        if (from in playbackManager.queue.indices && to in playbackManager.queue.indices) {
            apply()
            playbackManager.moveQueueItem(from, to)
            return true
        }

        return false
    }

    /** Add a [Song] to the top of the queue. */
    fun playNext(song: Song) {
        playbackManager.playNext(song)
    }

    /** Add an [Album] to the top of the queue. */
    fun playNext(album: Album) {
        playbackManager.playNext(settingsManager.detailAlbumSort.album(album))
    }

    /** Add a [Song] to the end of the queue. */
    fun addToQueue(song: Song) {
        playbackManager.addToQueue(song)
    }

    /** Add an [Album] to the end of the queue. */
    fun addToQueue(album: Album) {
        playbackManager.addToQueue(settingsManager.detailAlbumSort.album(album))
    }

    // --- STATUS FUNCTIONS ---

    /** Flip the playing status, e.g from playing to paused */
    fun invertPlaying() {
        playbackManager.isPlaying = !playbackManager.isPlaying
    }

    /** Flip the shuffle status, e.g from on to off. Will keep song by default. */
    fun invertShuffled() {
        playbackManager.reshuffle(!playbackManager.isShuffled)
    }

    /** Increment the repeat mode, e.g from [RepeatMode.NONE] to [RepeatMode.ALL] */
    fun incrementRepeatMode() {
        playbackManager.repeatMode = playbackManager.repeatMode.increment()
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force save the current [PlaybackStateManager] state to the database. Called by
     * SettingsListFragment.
     */
    fun savePlaybackState(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            playbackManager.saveState(context)
            onDone()
        }
    }

    /**
     * Restore playback on startup. This can do one of two things:
     * - Play a file intent that was given by MainActivity in [playWithUri]
     * - Restore the last playback state if there is no active file intent.
     */
    fun setupPlayback(context: Context) {
        val intentUri = intentUri

        if (intentUri != null) {
            playWithUriInternal(intentUri, context)
            // Remove the uri after finishing the calls so that this does not fire again.
            this.intentUri = null
        } else if (!playbackManager.isInitialized) {
            // Otherwise just restore
            viewModelScope.launch { playbackManager.restoreState(context) }
        }
    }

    /**
     * Attempt to restore the current playback state from an existing [PlaybackStateManager]
     * instance.
     */
    private fun restorePlaybackState() {
        logD("Attempting to restore playback state")

        onPositionChanged(playbackManager.positionMs)
        onPlayingChanged(playbackManager.isPlaying)
        onShuffledChanged(playbackManager.isShuffled)
        onRepeatChanged(playbackManager.repeatMode)
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        playbackManager.removeCallback(this)
    }

    override fun onIndexMoved(index: Int) {
        _song.value = playbackManager.song
        _nextUp.value = playbackManager.queue.slice(index + 1 until playbackManager.queue.size)
    }

    override fun onQueueChanged(index: Int, queue: List<Song>) {
        _nextUp.value = queue.slice(index + 1 until queue.size)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        _parent.value = playbackManager.parent
        _song.value = playbackManager.song
        _nextUp.value = queue.slice(index + 1 until queue.size)
    }

    override fun onPositionChanged(positionMs: Long) {
        _positionSecs.value = positionMs / 1000
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        _isShuffled.value = isShuffled
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        _repeatMode.value = repeatMode
    }
}
