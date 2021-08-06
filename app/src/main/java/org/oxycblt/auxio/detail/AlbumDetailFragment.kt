package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.adapters.AlbumDetailAdapter
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.recycler.CenterSmoothScroller
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.canScroll
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.ui.showToast

/**
 * The [DetailFragment] for an album.
 * @author OxygenCobalt
 */
class AlbumDetailFragment : DetailFragment() {
    private val args: AlbumDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // If DetailViewModel isn't already storing the album, get it from MusicStore
        // using the ID given by the navigation arguments.
        if (detailModel.currentAlbum.value == null ||
            detailModel.currentAlbum.value?.id != args.albumId
        ) {
            detailModel.updateAlbum(
                MusicStore.getInstance().albums.find {
                    it.id == args.albumId
                }!!
            )
        }

        val detailAdapter = AlbumDetailAdapter(
            detailModel, playbackModel, viewLifecycleOwner,
            doOnClick = { playbackModel.playSong(it, PlaybackMode.IN_ALBUM) },
            doOnLongClick = { view, data -> newMenu(view, data, ActionMenu.FLAG_IN_ALBUM) }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        setupToolbar(R.menu.menu_album_detail) { itemId ->
            if (itemId == R.id.action_queue_add) {
                playbackModel.addToUserQueue(detailModel.currentAlbum.value!!)
                requireContext().showToast(R.string.lbl_queue_added)
                true
            } else {
                false
            }
        }

        setupRecycler(detailAdapter) { pos ->
            pos == 0
        }

        // -- DETAILVIEWMODEL SETUP ---

        detailModel.albumSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            // Detail header data is included
            val data = mutableListOf<BaseModel>(detailModel.currentAlbum.value!!).also {
                it.addAll(mode.getSortedSongList(detailModel.currentAlbum.value!!.songs))
            }

            detailAdapter.submitList(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            when (item) {
                // Songs should be scrolled to if the album matches, or a new detail
                // fragment should be launched otherwise.
                is Song -> {
                    if (detailModel.currentAlbum.value!!.id == item.album.id) {
                        scrollToItem(item.id)

                        detailModel.doneWithNavToItem()
                    } else {
                        findNavController().navigate(
                            AlbumDetailFragmentDirections.actionShowAlbum(item.album.id)
                        )
                    }
                }

                // If the album matches, no need to do anything. Otherwise launch a new
                // detail fragment.
                is Album -> {
                    if (detailModel.currentAlbum.value!!.id == item.id) {
                        binding.detailRecycler.scrollToPosition(0)
                        detailModel.doneWithNavToItem()
                    } else {
                        findNavController().navigate(
                            AlbumDetailFragmentDirections.actionShowAlbum(item.id)
                        )
                    }
                }

                // Always launch a new ArtistDetailFragment.
                is Artist -> {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowArtist(item.id)
                    )
                }

                else -> {}
            }
        }

        // --- PLAYBACKVIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (playbackModel.mode.value == PlaybackMode.IN_ALBUM &&
                playbackModel.parent.value?.id == detailModel.currentAlbum.value!!.id
            ) {
                detailAdapter.highlightSong(song, binding.detailRecycler)
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        playbackModel.isInUserQueue.observe(viewLifecycleOwner) { inUserQueue ->
            if (inUserQueue) {
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    /**
     * Scroll to an song using its [id].
     */
    private fun scrollToItem(id: Long) {
        // Calculate where the item for the currently played song is
        val pos = detailModel.albumSortMode.value!!.getSortedSongList(
            detailModel.currentAlbum.value!!.songs
        ).indexOfFirst { it.id == id }

        if (pos != -1) {
            binding.detailRecycler.post {
                // Make sure to increment the position to make up for the detail header
                binding.detailRecycler.layoutManager?.startSmoothScroll(
                    CenterSmoothScroller(requireContext(), pos.inc())
                )

                // If the recyclerview can scroll, its certain that it will have to scroll to
                // correctly center the playing item, so make sure that the Toolbar is lifted in
                // that case.
                binding.detailAppbar.isLifted = binding.detailRecycler.canScroll()
            }
        }
    }
}
