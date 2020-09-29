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
import org.oxycblt.auxio.databinding.FragmentArtistDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailAlbumAdapter
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.toColor

class ArtistDetailFragment : Fragment() {

    private val args: ArtistDetailFragmentArgs by navArgs()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentArtistDetailBinding.inflate(inflater)

        // If DetailViewModel isn't already storing the artist, get it from MusicViewModel
        // using the ID given by the navigation arguments
        if (detailModel.currentArtist.value == null ||
            detailModel.currentArtist.value?.id != args.artistId
        ) {
            val musicModel: MusicViewModel by activityViewModels()

            detailModel.updateArtist(
                musicModel.artists.value!!.find {
                    it.id == args.artistId
                }!!
            )
        }

        val albumAdapter = DetailAlbumAdapter(
            ClickListener {
                navToAlbum(it)
            }
        )

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.artist = detailModel.currentArtist.value!!

        binding.artistAlbumRecycler.apply {
            adapter = albumAdapter
            applyDivider()
            setHasFixedSize(true)
        }

        binding.artistToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        detailModel.artistSortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the current sort icon
            binding.artistSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the album adapter.
            albumAdapter.submitList(
                mode.getSortedAlbumList(detailModel.currentArtist.value!!.albums)
            )
        }

        // Don't enable the sort button if there is only one album [Or less]
        if (detailModel.currentArtist.value!!.numAlbums < 2) {
            binding.artistSortButton.imageTintList = ColorStateList.valueOf(
                R.color.inactive_color.toColor(requireContext())
            )

            binding.artistSortButton.isEnabled = false
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        detailModel.isAlreadyNavigating = false
    }

    private fun navToAlbum(album: Album) {
        // Don't navigate if an item already has been selected.
        if (!detailModel.isAlreadyNavigating) {
            detailModel.isAlreadyNavigating = true

            findNavController().navigate(
                ArtistDetailFragmentDirections.actionShowAlbum(album.id, false)
            )
        }
    }
}
