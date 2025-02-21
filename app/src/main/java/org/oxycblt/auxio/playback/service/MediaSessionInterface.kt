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
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.service.MediaSessionUID
import org.oxycblt.auxio.music.service.MusicBrowser
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Library
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.tag.Name
import timber.log.Timber as L

class MediaSessionInterface
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val commandFactory: PlaybackCommand.Factory,
    private val musicRepository: MusicRepository,
) : MediaSessionCompat.Callback() {
    private val jaroWinkler = JaroWinklerSimilarity()

    //    STUBS: We already automatically prepare playback.
    //    override fun onPrepare() {
    //        super.onPrepare()
    //    }

    //    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
    //        super.onPrepareFromMediaId(mediaId, extras)
    //    }
    //
    //    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
    //        super.onPrepareFromUri(uri, extras)
    //    }
    //
    //    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
    //        super.onPlayFromUri(uri, extras)
    //    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        val uid = MediaSessionUID.fromString(mediaId ?: return) ?: return
        val parentUid =
            extras?.getString(MusicBrowser.KEY_CHILD_OF)?.let { MediaSessionUID.fromString(it) }
        val command = expandUidIntoCommand(uid, parentUid)
        L.d(extras?.getString(MusicBrowser.KEY_CHILD_OF))
        playbackManager.play(requireNotNull(command) { "Invalid playback configuration" })
    }

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        super.onPrepareFromSearch(query, extras)
        // STUB, can't tell when this is called
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        super.onPlayFromSearch(query, extras)
        val library = musicRepository.library ?: return
        val command = expandSearchInfoCommand(query.ifBlank { null }, extras, library)
        playbackManager.play(requireNotNull(command) { "Invalid playback configuration" })
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        super.onAddQueueItem(description)
        val library = musicRepository.library ?: return
        val uid = MediaSessionUID.fromString(description.mediaId ?: return) ?: return
        val songUid =
            when (uid) {
                is MediaSessionUID.SingleItem -> uid.uid
                else -> return
            }
        val song = library.songs.find { it.uid == songUid } ?: return
        playbackManager.addToQueue(song)
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
        super.onRemoveQueueItem(description)
        val at = description.extras?.getInt(KEY_QUEUE_POS)
        if (at != null) {
            // Direct queue item removal w/preserved extras, we can explicitly remove
            // the correct item rather than a duplicate elsewhere.
            playbackManager.removeQueueItem(at)
            return
        }
        // Non-queue item or queue item lost it's extras in transit, remove the first item
        val uid = MediaSessionUID.fromString(description.mediaId ?: return) ?: return
        val songUid =
            when (uid) {
                is MediaSessionUID.SingleItem -> uid.uid
                else -> return
            }
        val firstAt = playbackManager.queue.indexOfFirst { it.uid == songUid }
        playbackManager.removeQueueItem(firstAt)
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

    override fun onSkipToQueueItem(id: Long) {
        playbackManager.goto(id.toInt())
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

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackActions.ACTION_EXIT))
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        super.onCustomAction(action, extras)
        // Service already handles intents from the old notification actions, easier to
        // plug into that system.
        context.sendBroadcast(Intent(action))
    }

    private fun expandUidIntoCommand(
        uid: MediaSessionUID,
        parentUid: MediaSessionUID?
    ): PlaybackCommand? {
        val unwrappedUid = (uid as? MediaSessionUID.SingleItem)?.uid ?: return null
        val unwrappedParentUid = (parentUid as? MediaSessionUID.SingleItem)?.uid
        val music = musicRepository.find(unwrappedUid) ?: return null
        val parent = unwrappedParentUid?.let { musicRepository.find(it) as? MusicParent }
        return expandMusicIntoCommand(music, parent)
    }

    @Suppress("DEPRECATION")
    private fun expandSearchInfoCommand(
        query: String?,
        extras: Bundle,
        library: Library
    ): PlaybackCommand? {
        if (query == null) {
            // User just wanted to 'play some music', shuffle all
            return commandFactory.all(ShuffleMode.ON)
        }

        when (extras.getString(MediaStore.EXTRA_MEDIA_FOCUS)) {
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                val songQuery = extras.getString(MediaStore.EXTRA_MEDIA_TITLE)
                val albumQuery = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
                val artistQuery = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                val best =
                    library.songs.maxByOrNull {
                        fuzzy(it.name, songQuery) +
                            fuzzy(it.album.name, albumQuery) +
                            it.artists.maxOf { artist -> fuzzy(artist.name, artistQuery) }
                    }
                if (best != null) {
                    return expandSongIntoCommand(best, null)
                }
            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                val albumQuery = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
                val artistQuery = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                val best =
                    library.albums.maxByOrNull {
                        fuzzy(it.name, albumQuery) +
                            it.artists.maxOf { artist -> fuzzy(artist.name, artistQuery) }
                    }
                if (best != null) {
                    return commandFactory.album(best, ShuffleMode.OFF)
                }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                val artistQuery = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
                val best = library.artists.maxByOrNull { fuzzy(it.name, artistQuery) }
                if (best != null) {
                    return commandFactory.artist(best, ShuffleMode.OFF)
                }
            }
            MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                val genreQuery = extras.getString(MediaStore.EXTRA_MEDIA_GENRE)
                val best = library.genres.maxByOrNull { fuzzy(it.name, genreQuery) }
                if (best != null) {
                    return commandFactory.genre(best, ShuffleMode.OFF)
                }
            }
            MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE -> {
                val playlistQuery = extras.getString(MediaStore.EXTRA_MEDIA_PLAYLIST)
                val best = library.playlists.maxByOrNull { fuzzy(it.name, playlistQuery) }
                if (best != null) {
                    return commandFactory.playlist(best, ShuffleMode.OFF)
                }
            }
            else -> {}
        }

        val bestMusic =
            (library.songs + library.albums + library.artists + library.genres + library.playlists)
                .maxByOrNull { fuzzy(it.name, query) }
        // TODO: Error out when we can't correctly resolve the query
        return bestMusic?.let { expandMusicIntoCommand(it, null) }
            ?: commandFactory.all(ShuffleMode.ON)
    }

    private fun fuzzy(name: Name, query: String?): Double =
        query?.let { jaroWinkler.apply(name.resolve(context), it) } ?: 0.0

    private fun expandMusicIntoCommand(music: Music, parent: MusicParent?) =
        when (music) {
            is Song -> expandSongIntoCommand(music, parent)
            is Album -> commandFactory.album(music, ShuffleMode.IMPLICIT)
            is Artist -> commandFactory.artist(music, ShuffleMode.IMPLICIT)
            is Genre -> commandFactory.genre(music, ShuffleMode.IMPLICIT)
            is Playlist -> commandFactory.playlist(music, ShuffleMode.IMPLICIT)
        }

    private fun expandSongIntoCommand(music: Song, parent: MusicParent?) =
        when (parent) {
            is Album -> commandFactory.songFromAlbum(music, ShuffleMode.IMPLICIT)
            is Artist ->
                commandFactory.songFromArtist(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromArtist(music, music.artists[0], ShuffleMode.IMPLICIT)
            is Genre ->
                commandFactory.songFromGenre(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromGenre(music, music.genres[0], ShuffleMode.IMPLICIT)
            is Playlist -> commandFactory.songFromPlaylist(music, parent, ShuffleMode.IMPLICIT)
            null -> commandFactory.songFromAll(music, ShuffleMode.IMPLICIT)
        }

    companion object {
        const val KEY_QUEUE_POS = BuildConfig.APPLICATION_ID + ".metadata.QUEUE_POS"
        const val ACTIONS =
            PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
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
