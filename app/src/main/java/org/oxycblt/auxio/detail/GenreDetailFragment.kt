package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentGenreDetailBinding
import org.oxycblt.auxio.detail.adapters.GenreSongAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.disable
import org.oxycblt.auxio.ui.setupGenreSongActions

/**
 * The [DetailFragment] for a genre.
 * @author OxygenCobalt
 */
class GenreDetailFragment : DetailFragment() {

    private val args: GenreDetailFragmentArgs by navArgs()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGenreDetailBinding.inflate(inflater)

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

        val songAdapter = GenreSongAdapter(
            doOnClick = {
                playbackModel.playSong(it, PlaybackMode.IN_GENRE)
            },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupGenreSongActions(
                    requireContext(), data, playbackModel
                )
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.genre = detailModel.currentGenre.value

        binding.genreToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_shuffle -> {
                        playbackModel.playGenre(
                            detailModel.currentGenre.value!!,
                            true
                        )

                        true
                    }

                    else -> false
                }
            }
        }

        if (detailModel.currentGenre.value!!.songs.size < 2) {
            binding.genreSortButton.disable(requireContext())
        }

        binding.genreSongRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)
        }

        // --- VIEWMODEL SETUP ---

        detailModel.genreSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            // Update the current sort icon
            binding.genreSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the artist adapter.
            songAdapter.submitList(
                mode.getSortedSongList(detailModel.currentGenre.value!!.songs)
            )
        }

        logD("Fragment created.")

        return binding.root
    }
}
