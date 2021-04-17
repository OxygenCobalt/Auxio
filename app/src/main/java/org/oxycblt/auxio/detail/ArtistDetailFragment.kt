package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.detail.adapters.ArtistDetailAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.newMenu

/**
 * The [DetailFragment] for an artist.
 * TODO: Show a list of songs?
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
            detailModel, playbackModel, viewLifecycleOwner,
            doOnClick = { album ->
                if (!detailModel.isNavigating) {
                    detailModel.setNavigating(true)

                    findNavController().navigate(
                        ArtistDetailFragmentDirections.actionShowAlbum(album.id)
                    )
                }
            },
            doOnLongClick = { view, data ->
                newMenu(view, data, ActionMenu.FLAG_IN_ARTIST)
            }
        )

        // --- UI SETUP ---

        val imgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        }

        binding.lifecycleOwner = this

        setupToolbar()
        setupRecycler(detailAdapter)

        binding.detailToolbar.setOnClickListener {
            imgLauncher.launch("image/*")
        }

        // --- VIEWMODEL SETUP ---

        detailModel.artistSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            // Header detail data is always included
            val data = mutableListOf<BaseModel>(detailModel.currentArtist.value!!).also {
                it.addAll(mode.getSortedAlbumList(detailModel.currentArtist.value!!.albums))
            }

            detailAdapter.submitList(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                is Artist -> {
                    if (item.id == detailModel.currentArtist.value!!.id) {
                        binding.detailRecycler.scrollToPosition(0)
                        detailModel.doneWithNavToItem()
                    } else {
                        findNavController().navigate(
                            ArtistDetailFragmentDirections.actionShowArtist(item.id)
                        )
                    }
                }

                is Album -> findNavController().navigate(
                    ArtistDetailFragmentDirections.actionShowAlbum(item.id)
                )

                is Song -> findNavController().navigate(
                    ArtistDetailFragmentDirections.actionShowAlbum(item.album.id)
                )

                else -> {}
            }
        }

        // Highlight albums if they are being played
        playbackModel.parent.observe(viewLifecycleOwner) { parent ->
            if (parent is Album?) {
                detailAdapter.setCurrentAlbum(parent, binding.detailRecycler)
            } else {
                detailAdapter.setCurrentAlbum(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }
}
