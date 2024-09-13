package org.oxycblt.auxio.home

import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD

interface HomeGenerator {
    fun songs(): List<Song>
    fun albums(): List<Album>
    fun artists(): List<Artist>
    fun genres(): List<Genre>
    fun playlists(): List<Playlist>
    fun tabs(): List<MusicType>
    fun release()

    interface Invalidator {
        fun invalidateMusic(type: MusicType, instructions: UpdateInstructions)
        fun invalidateTabs()
    }

    interface Factory {
        fun create(invalidator: Invalidator): HomeGenerator
    }
}

private class HomeGeneratorImpl(
    private val invalidator: HomeGenerator.Invalidator,
    private val homeSettings: HomeSettings,
    private val listSettings: ListSettings,
    private val musicRepository: MusicRepository,
) : HomeGenerator, HomeSettings.Listener, ListSettings.Listener, MusicRepository.UpdateListener {
    override fun songs() =
        musicRepository.deviceLibrary?.let { listSettings.songSort.songs(it.songs) } ?: emptyList()
    override fun albums() = musicRepository.deviceLibrary?.let { listSettings.albumSort.albums(it.albums) } ?: emptyList()
    override fun artists() = musicRepository.deviceLibrary?.let { listSettings.artistSort.artists(it.artists) } ?: emptyList()
    override fun genres() = musicRepository.deviceLibrary?.let { listSettings.genreSort.genres(it.genres) } ?: emptyList()
    override fun playlists() = musicRepository.userLibrary?.let { listSettings.playlistSort.playlists(it.playlists) } ?: emptyList()
    override fun tabs() =
        homeSettings.homeTabs.filterIsInstance<Tab.Visible>().map { it.type }


    override fun onTabsChanged() {
        invalidator.invalidateTabs()
    }

    init {
        homeSettings.registerListener(this)
        listSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
    }

    override fun release() {
        musicRepository.removeUpdateListener(this)
        listSettings.unregisterListener(this)
        homeSettings.unregisterListener(this)
    }

    override fun onHideCollaboratorsChanged() {
        // Changes in the hide collaborator setting will change the artist contents
        // of the library, consider it a library update.
        logD("Collaborator setting changed, forwarding update")
        onMusicChanges(MusicRepository.Changes(deviceLibrary = true, userLibrary = false))
    }

    override fun onSongSortChanged() {
        super.onSongSortChanged()
        invalidator.invalidateMusic(MusicType.SONGS, UpdateInstructions.Replace(0))
    }

    override fun onAlbumSortChanged() {
        super.onAlbumSortChanged()
        invalidator.invalidateMusic(MusicType.ALBUMS, UpdateInstructions.Replace(0))
    }

    override fun onArtistSortChanged() {
        super.onArtistSortChanged()
        invalidator.invalidateMusic(MusicType.ARTISTS, UpdateInstructions.Replace(0))
    }

    override fun onGenreSortChanged() {
        super.onGenreSortChanged()
        invalidator.invalidateMusic(MusicType.GENRES, UpdateInstructions.Replace(0))
    }

    override fun onPlaylistSortChanged() {
        super.onPlaylistSortChanged()
        invalidator.invalidateMusic(MusicType.PLAYLISTS, UpdateInstructions.Replace(0))
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary
        if (changes.deviceLibrary && deviceLibrary != null) {
            logD("Refreshing library")
            // Get the each list of items in the library to use as our list data.
            // Applying the preferred sorting to them.
            invalidator.invalidateMusic(MusicType.SONGS, UpdateInstructions.Diff)
            invalidator.invalidateMusic(MusicType.ALBUMS, UpdateInstructions.Diff)
            invalidator.invalidateMusic(MusicType.ARTISTS, UpdateInstructions.Diff)
            invalidator.invalidateMusic(MusicType.GENRES, UpdateInstructions.Diff)
        }

        val userLibrary = musicRepository.userLibrary
        if (changes.userLibrary && userLibrary != null) {
            logD("Refreshing playlists")
            invalidator.invalidateMusic(MusicType.PLAYLISTS, UpdateInstructions.Diff)
        }
    }

}