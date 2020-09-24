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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentAlbumDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailSongAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
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

        // If DetailViewModel isn't already storing the album, get it from MusicViewModel
        // using the ID given by the navigation arguments.
        if (detailModel.currentAlbum == null) {
            val musicModel: MusicViewModel by activityViewModels()

            detailModel.currentAlbum = musicModel.albums.value!!.find {
                it.id == args.albumId
            }!!
        }

        val songAdapter = DetailSongAdapter(
            detailModel.currentAlbum!!.songs,
            ClickListener {
                Log.d(this::class.simpleName, it.name)
            }
        )

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.album = detailModel.currentAlbum

        binding.songRecycler.adapter = songAdapter
        binding.songRecycler.applyDivider()
        binding.songRecycler.setHasFixedSize(true)

        // If the album was shown directly from LibraryFragment [No parent artist stored],
        // then enable the ability to navigate upwards to the album's parent artist.
        if (detailModel.currentArtist == null) {
            detailModel.doneWithNavToParent()

            detailModel.navToParentArtist.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowParentArtist(
                            detailModel.currentAlbum!!.artist.id
                        )
                    )

                    detailModel.doneWithNavToParent()
                }
            }

            binding.artistName.setBackgroundResource(R.drawable.ripple)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        detailModel.currentAlbum = null
    }
}
