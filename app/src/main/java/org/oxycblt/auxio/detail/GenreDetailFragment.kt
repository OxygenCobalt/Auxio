package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.detail.adapters.GenreDetailAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.requireCompatActivity

/**
 * The [DetailFragment] for a genre.
 * @author OxygenCobalt
 */
class GenreDetailFragment : DetailFragment() {
    private val args: GenreDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // If DetailViewModel isn't already storing the genre, get it from MusicStore
        // using the ID given by the navigation arguments
        if (detailModel.currentGenre.value == null ||
            detailModel.currentGenre.value?.id != args.genreId
        ) {
            detailModel.updateGenre(
                MusicStore.getInstance().genres.find {
                    it.id == args.genreId
                }!!
            )
        }

        val detailAdapter = GenreDetailAdapter(
            detailModel, playbackModel, viewLifecycleOwner,
            doOnClick = {
                playbackModel.playSong(it, PlaybackMode.IN_GENRE)
            },
            doOnLongClick = { view, data ->
                ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_IN_GENRE)
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        setupToolbar()
        setupRecycler(detailAdapter)

        // --- DETAILVIEWMODEL SETUP ---

        detailModel.genreSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            // Detail header data is included
            val data = mutableListOf<BaseModel>(detailModel.currentGenre.value!!).also {
                it.addAll(mode.getSortedSongList(detailModel.currentGenre.value!!.songs))
            }

            detailAdapter.submitList(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
                    // All items will launch new detail fragments.
                    is Artist -> findNavController().navigate(
                        GenreDetailFragmentDirections.actionShowArtist(it.id)
                    )

                    is Album -> findNavController().navigate(
                        GenreDetailFragmentDirections.actionShowAlbum(it.id)
                    )

                    is Song -> findNavController().navigate(
                        GenreDetailFragmentDirections.actionShowAlbum(it.album.id)
                    )

                    else -> {}
                }
            }
        }

        // --- PLAYBACKVIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            if (playbackModel.mode.value == PlaybackMode.IN_GENRE &&
                playbackModel.parent.value?.id == detailModel.currentGenre.value!!.id
            ) {
                detailAdapter.highlightSong(playbackModel.song.value, binding.detailRecycler)
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        playbackModel.isInUserQueue.observe(viewLifecycleOwner) {
            if (it) {
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }
}
