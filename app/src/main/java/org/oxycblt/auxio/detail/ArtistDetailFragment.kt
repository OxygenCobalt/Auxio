package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentArtistDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailAlbumAdapter
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.disable
import org.oxycblt.auxio.ui.setupAlbumActions

class ArtistDetailFragment : DetailFragment() {
    private val args: ArtistDetailFragmentArgs by navArgs()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentArtistDetailBinding.inflate(inflater)

        // If DetailViewModel isn't already storing the artist, get it from MusicStore
        // using the ID given by the navigation arguments
        if (detailModel.currentArtist.value == null ||
            detailModel.currentArtist.value?.id != args.artistId
        ) {
            detailModel.updateArtist(
                MusicStore.getInstance().artists.find {
                    it.id == args.artistId
                }!!
            )
        }

        val albumAdapter = DetailAlbumAdapter(
            doOnClick = {
                if (!detailModel.isNavigating) {
                    detailModel.updateNavigationStatus(true)

                    findNavController().navigate(
                        ArtistDetailFragmentDirections.actionShowAlbum(it.id, false)
                    )
                }
            },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupAlbumActions(
                    data, requireContext(), playbackModel
                )
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.playbackModel = playbackModel
        binding.artist = detailModel.currentArtist.value!!

        binding.artistToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_shuffle -> playbackModel.playArtist(
                        detailModel.currentArtist.value!!,
                        true
                    )
                    R.id.action_play -> playbackModel.playArtist(
                        detailModel.currentArtist.value!!, false
                    )
                }

                true
            }
        }

        // Disable the sort button if there is only one album [Or less]
        if (detailModel.currentArtist.value!!.albums.size < 2) {
            binding.artistSortButton.disable(requireContext())
        }

        binding.artistAlbumRecycler.apply {
            adapter = albumAdapter
            setHasFixedSize(true)
        }

        // --- VIEWMODEL SETUP ---

        detailModel.artistSortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the current sort icon
            binding.artistSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the album adapter.
            albumAdapter.submitList(
                mode.getSortedAlbumList(detailModel.currentArtist.value!!.albums)
            )
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
