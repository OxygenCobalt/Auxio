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
import org.oxycblt.auxio.detail.adapters.GenreArtistAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.disable
import org.oxycblt.auxio.ui.setupArtistActions

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

        val artistAdapter = GenreArtistAdapter(
            doOnClick = {
                if (!detailModel.isNavigating) {
                    detailModel.updateNavigationStatus(true)

                    findNavController().navigate(
                        GenreDetailFragmentDirections.actionShowArtist(it.id)
                    )
                }
            },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupArtistActions(
                    data, playbackModel
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
                    R.id.action_play -> {
                        playbackModel.playGenre(
                            detailModel.currentGenre.value!!, false
                        )

                        true
                    }

                    else -> false
                }
            }
        }

        // Disable the sort button if there is only one artist [Or less]
        if (detailModel.currentGenre.value!!.artists.size < 2) {
            binding.genreSortButton.disable(requireContext())
        }

        binding.genreArtistRecycler.apply {
            adapter = artistAdapter
            setHasFixedSize(true)
        }

        // --- VIEWMODEL SETUP ---

        detailModel.genreSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            // Update the current sort icon
            binding.genreSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the artist adapter.
            artistAdapter.submitList(
                mode.getSortedArtistList(detailModel.currentGenre.value!!.artists)
            )
        }

        logD("Fragment created.")

        return binding.root
    }
}
