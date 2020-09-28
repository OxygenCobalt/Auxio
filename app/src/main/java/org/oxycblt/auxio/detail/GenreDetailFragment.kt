package org.oxycblt.auxio.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentGenreDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailArtistAdapter
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.toColor

class GenreDetailFragment : Fragment() {

    private val args: GenreDetailFragmentArgs by navArgs()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGenreDetailBinding.inflate(inflater)

        // If DetailViewModel isn't already storing the genre, get it from MusicViewModel
        // using the ID given by the navigation arguments
        if (detailModel.currentGenre.value!!.id != args.genreId) {
            val musicModel: MusicViewModel by activityViewModels()

            detailModel.updateGenre(
                musicModel.genres.value!!.find {
                    it.id == args.genreId
                }!!
            )
        }

        val albumAdapter = DetailArtistAdapter(
            ClickListener {
                navToArtist(it)
            }
        )

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.genre = detailModel.currentGenre.value

        binding.genreArtistRecycler.adapter = albumAdapter
        binding.genreArtistRecycler.applyDivider()
        binding.genreArtistRecycler.setHasFixedSize(true)

        binding.genreToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        detailModel.genreSortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the current sort icon
            binding.genreSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the artist adapter.
            albumAdapter.submitList(
                mode.getSortedArtistList(detailModel.currentGenre.value!!.artists)
            )
        }

        // Don't enable the sort button if there is only one artist [Or less]
        if (detailModel.currentGenre.value!!.numArtists < 2) {
            binding.genreSortButton.imageTintList = ColorStateList.valueOf(
                R.color.inactive_color.toColor(requireContext())
            )

            binding.genreSortButton.isEnabled = false
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        detailModel.isAlreadyNavigating = false
    }

    private fun navToArtist(artist: Artist) {
        // Don't navigate if an item already has been selected.
        if (!detailModel.isAlreadyNavigating) {
            detailModel.isAlreadyNavigating = true

            findNavController().navigate(
                GenreDetailFragmentDirections.actionShowArtist(artist.id)
            )
        }
    }
}
