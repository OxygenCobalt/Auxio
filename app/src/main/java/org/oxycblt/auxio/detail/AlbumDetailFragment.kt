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
import org.oxycblt.auxio.ClickListener
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentAlbumDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailSongAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.theme.applyDivider

class AlbumDetailFragment : Fragment() {

    private val args: AlbumDetailFragmentArgs by navArgs()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAlbumDetailBinding.inflate(inflater)

        // I honestly don't want to turn of the any data classes into parcelables due to how
        // many lists they store, so just pick up the artist id and find it from musicModel.
        val musicModel: MusicViewModel by activityViewModels()
        val album = musicModel.albums.value?.find { it.id == args.albumId }!!

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.album = album

        binding.songRecycler.adapter = DetailSongAdapter(
            album.songs,
            ClickListener {
                Log.d(this::class.simpleName, it.name)
            }
        )
        binding.songRecycler.applyDivider()
        binding.songRecycler.setHasFixedSize(true)

        // If the album was shown directly from LibraryFragment, then enable the ability
        // to navigate to the artist from the album. Don't do this if the album was shown
        // from ArtistDetailFragment, as you can just navigate up to see the parent artist.
        if (args.isFromLibrary) {
            detailModel.doneWithNavToParent()

            detailModel.navToParentArtist.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowParentArtist(album.artist.id)
                    )

                    detailModel.doneWithNavToParent()
                }
            }

            binding.artistName.setBackgroundResource(R.drawable.ripple)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
