package org.oxycblt.auxio.music.picker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.unlikelyToBeNull

class MusicPickerViewModel : ViewModel(), MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> get() = _currentSong

    private val _currentArtists = MutableStateFlow<List<Artist>?>(null)
    val currentArtists: StateFlow<List<Artist>?> get() = _currentArtists

    fun setSongUid(uid: Music.UID) {
        val library = unlikelyToBeNull(musicStore.library)
        _currentSong.value = library.find(uid)
        _currentArtists.value =  _currentSong.value?.artists
    }

    fun setArtistUids(uids: Array<Music.UID>) {
        val library = unlikelyToBeNull(musicStore.library)
        _currentArtists.value = uids.mapNotNull { library.find<Artist>(it) }.ifEmpty { null }
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            val song = _currentSong.value
            val artists = _currentArtists.value
            if (song != null) {
                _currentSong.value = library.sanitize(song)
                _currentArtists.value = _currentSong.value?.artists
            } else if (artists != null){
                _currentArtists.value = artists.mapNotNull { library.sanitize(it) }
            }
        }
    }
}