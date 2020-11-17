package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentGenreDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailArtistAdapter
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.applyDivider
import org.oxycblt.auxio.ui.disable

class GenreDetailFragment : DetailFragment() {

    private val args: GenreDetailFragmentArgs by navArgs()
    private val detailModel: DetailViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        val artistAdapter = DetailArtistAdapter {
            if (!detailModel.isNavigating) {
                detailModel.updateNavigationStatus(true)

                findNavController().navigate(
                    GenreDetailFragmentDirections.actionShowArtist(it.id)
                )
            }
        }

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
                    R.id.action_shuffle -> playbackModel.playGenre(
                        detailModel.currentGenre.value!!,
                        true
                    )
                    R.id.action_play -> playbackModel.playGenre(
                        detailModel.currentGenre.value!!, false
                    )
                }

                true
            }
        }

        // Disable the sort button if there is only one artist [Or less]
        if (detailModel.currentGenre.value!!.numArtists < 2) {
            binding.genreSortButton.disable(requireContext())
        }

        binding.genreArtistRecycler.apply {
            adapter = artistAdapter
            applyDivider()
            setHasFixedSize(true)
        }

        // --- VIEWMODEL SETUP ---

        detailModel.genreSortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the current sort icon
            binding.genreSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the artist adapter.
            artistAdapter.submitList(
                mode.getSortedArtistList(detailModel.currentGenre.value!!.artists)
            )
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        detailModel.updateNavigationStatus(false)
    }
}
