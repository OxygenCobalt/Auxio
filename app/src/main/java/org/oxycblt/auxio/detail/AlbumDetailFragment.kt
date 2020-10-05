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
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.disable

class AlbumDetailFragment : Fragment() {

    private val args: AlbumDetailFragmentArgs by navArgs()
    private val detailModel: DetailViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAlbumDetailBinding.inflate(inflater)

        // If DetailViewModel isn't already storing the album, get it from MusicViewModel
        // using the ID given by the navigation arguments.
        if (detailModel.currentAlbum.value == null ||
            detailModel.currentAlbum.value?.id != args.albumId
        ) {
            val musicModel: MusicViewModel by activityViewModels()

            detailModel.updateAlbum(
                musicModel.albums.value!!.find {
                    it.id == args.albumId
                }!!
            )
        }

        val songAdapter = DetailSongAdapter(
            ClickListener {
                playbackModel.updateSong(it)
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.album = detailModel.currentAlbum.value!!

        binding.albumSongRecycler.apply {
            adapter = songAdapter
            applyDivider()
            setHasFixedSize(true)
        }

        binding.albumToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Don't enable the sort button if there's only one song [or less]
        if (detailModel.currentAlbum.value!!.numSongs < 2) {
            binding.albumSortButton.disable(requireContext())
        }

        // -- VIEWMODEL SETUP ---

        detailModel.albumSortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the current sort icon
            binding.albumSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the album adapter.
            songAdapter.submitList(
                mode.getSortedSongList(detailModel.currentAlbum.value!!.songs)
            )
        }

        // If the album was shown directly from LibraryFragment, Then enable the ability to
        // navigate upwards to the parent artist
        if (args.enableParentNav) {
            detailModel.doneWithNavToParent()

            detailModel.navToParent.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowParentArtist(
                            detailModel.currentAlbum.value!!.artist.id
                        )
                    )

                    detailModel.doneWithNavToParent()
                }
            }

            binding.albumArtist.setBackgroundResource(R.drawable.ripple)
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
