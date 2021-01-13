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
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.recycler.CenterSmoothScroller
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.createToast
import org.oxycblt.auxio.ui.requireCompatActivity

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
            doOnLongClick = { data, view ->
                ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_IN_ALBUM)
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        setupToolbar(R.menu.menu_album_detail) {
            when (it) {
                R.id.action_queue_add -> {
                    playbackModel.addToUserQueue(detailModel.currentAlbum.value!!)
                    getString(R.string.label_queue_added).createToast(requireContext())

                    true
                }

                else -> false
            }
        }

        setupRecycler(detailAdapter)

        // -- DETAILVIEWMODEL SETUP ---

        detailModel.albumSortMode.observe(viewLifecycleOwner) { mode ->
            logD("Updating sort mode to $mode")

            val data = mutableListOf<BaseModel>(detailModel.currentAlbum.value!!).also {
                it.addAll(mode.getSortedSongList(detailModel.currentAlbum.value!!.songs))
            }

            detailAdapter.submitList(data)
        }

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

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it is Song) {
                    scrollToPlayingItem()
                }

                if (it is Album && it.id == detailModel.currentAlbum.value!!.id) {
                    detailModel.doneWithNavToItem()
                }
            }
        }

        // --- PLAYBACKVIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) {
            handlePlayingItem(detailAdapter)
        }

        playbackModel.mode.observe(viewLifecycleOwner) {
            handlePlayingItem(detailAdapter)
        }

        playbackModel.isInUserQueue.observe(viewLifecycleOwner) {
            if (it) {
                detailAdapter.highlightSong(null, binding.detailRecycler)
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    /**
     * Handle an update to the mode or the song and determine whether to highlight a song
     * item based off that
     */
    private fun handlePlayingItem(detailAdapter: AlbumDetailAdapter) {
        if (playbackModel.mode.value == PlaybackMode.IN_ALBUM &&
            playbackModel.parent.value?.id == detailModel.currentAlbum.value!!.id
        ) {
            detailAdapter.highlightSong(playbackModel.song.value, binding.detailRecycler)
        } else {
            // Clear the viewholders if the mode isn't ALL_SONGS
            detailAdapter.highlightSong(null, binding.detailRecycler)
        }
    }

    /**
     * Scroll to the currently playing item.
     */
    private fun scrollToPlayingItem() {
        // Calculate where the item for the currently played song is
        val pos = detailModel.albumSortMode.value!!.getSortedSongList(
            detailModel.currentAlbum.value!!.songs
        ).indexOf(playbackModel.song.value)

        if (pos != -1) {
            binding.detailRecycler.post {
                // Make sure to increment the position to make up for the detail header
                binding.detailRecycler.layoutManager?.startSmoothScroll(
                    CenterSmoothScroller(requireContext(), pos.inc())
                )

                // If the recyclerview can scroll, its certain that it will have to scroll to
                // correctly center the playing item, so make sure that the Toolbar is lifted in
                // that case.
                if (binding.detailRecycler.computeVerticalScrollRange() > binding.detailRecycler.height) {
                    binding.detailAppbar.isLifted = true
                }
            }

            detailModel.doneWithNavToItem()
        }
    }
}
