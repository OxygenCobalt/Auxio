package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.databinding.FragmentArtistDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailAlbumAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider

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
        if (detailModel.currentArtist == null) {
            val musicModel: MusicViewModel by activityViewModels()
            detailModel.currentArtist = musicModel.artists.value!!.find {
                it.id == args.artistId
            }!!
        }

        val albumAdapter = DetailAlbumAdapter(
            detailModel.currentArtist!!.albums,
            ClickListener {
                navToAlbum(it)
            }
        )

        binding.lifecycleOwner = this
        binding.artist = detailModel.currentArtist!!

        binding.albumRecycler.adapter = albumAdapter
        binding.albumRecycler.applyDivider()
        binding.albumRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        detailModel.isAlreadyNavigating = false
    }

    override fun onDestroy() {
        super.onDestroy()

        // Reset the stored artist so that the next instance of ArtistDetailFragment
        // will not read it.
        detailModel.currentArtist = null
    }

    private fun navToAlbum(album: Album) {
        // Don't navigate if an item already has been selected.
        if (!detailModel.isAlreadyNavigating) {
            detailModel.isAlreadyNavigating = true

            findNavController().navigate(
                ArtistDetailFragmentDirections.actionShowAlbum(album.id)
            )
        }
    }
}
