package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.adapters.ArtistDetailAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.setupAlbumActions

/**
 * The [DetailFragment] for an artist.
 * @author OxygenCobalt
 */
class ArtistDetailFragment : DetailFragment() {
    private val args: ArtistDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val detailAdapter = ArtistDetailAdapter(
            detailModel, viewLifecycleOwner,
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
                    requireContext(), data, playbackModel
                )
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        setupToolbar(R.menu.menu_artist_actions) {
            when (it) {
                R.id.action_shuffle -> {
                    playbackModel.playArtist(
                        detailModel.currentArtist.value!!,
                        true
                    )

                    true
                }

                R.id.action_play_albums -> {
                    playbackModel.playArtist(
                        detailModel.currentArtist.value!!, false
                    )

                    true
                }

                else -> false
            }
        }

        binding.detailRecycler.apply {
            adapter = detailAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                layoutManager = GridLayoutManager(requireContext(), 2).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0) 2 else 1
                        }
                    }
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        detailModel.artistSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            val data = mutableListOf<BaseModel>(detailModel.currentArtist.value!!).also {
                it.addAll(mode.getSortedAlbumList(detailModel.currentArtist.value!!.albums))
            }

            detailAdapter.submitList(data)
        }

        playbackModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null && it is Artist) {
                playbackModel.doneWithNavToItem()
            }
        }

        logD("Fragment created.")

        return binding.root
    }
}
