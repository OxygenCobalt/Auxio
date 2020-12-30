package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import org.oxycblt.auxio.recycler.Highlightable
import org.oxycblt.auxio.ui.createToast
import org.oxycblt.auxio.ui.setupAlbumSongActions

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
            detailModel, viewLifecycleOwner,
            doOnClick = { playbackModel.playSong(it, PlaybackMode.IN_ALBUM) },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupAlbumSongActions(
                    requireContext(), data, detailModel, playbackModel
                )
            }
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        setupToolbar(R.menu.menu_album_actions) {
            when (it) {
                R.id.action_shuffle -> {
                    playbackModel.playAlbum(
                        detailModel.currentAlbum.value!!, true
                    )

                    true
                }
                R.id.action_play -> {
                    playbackModel.playAlbum(
                        detailModel.currentAlbum.value!!, false
                    )

                    true
                }

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
                if (args.fromArtist) {
                    findNavController().navigateUp()
                } else {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowParentArtist(
                            detailModel.currentAlbum.value!!.artist.id
                        )
                    )
                }

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

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (playbackModel.mode.value == PlaybackMode.IN_ALBUM &&
                playbackModel.parent.value!!.id == detailModel.currentAlbum.value!!.id
            ) {
                detailAdapter.setCurrentSong(song)

                lastHolder?.setHighlighted(false)
                lastHolder = null

                if (song != null) {
                    // Use existing data instead of having to re-sort it.
                    val pos = detailAdapter.currentList.indexOfFirst {
                        it.name == song.name
                    }

                    // Check if the ViewHolder for this song is visible, if it is then highlight it.
                    // If the ViewHolder is not visible, then the adapter should take care of it if it does become visible.
                    binding.detailRecycler.layoutManager?.findViewByPosition(pos)?.let { child ->
                        binding.detailRecycler.getChildViewHolder(child)?.let {
                            lastHolder = it as Highlightable

                            lastHolder?.setHighlighted(true)
                        }
                    }
                }
            } else {
                // Clear the viewholders if the mode isn't ALL_SONGS
                detailAdapter.setCurrentSong(null)

                lastHolder?.setHighlighted(false)
                lastHolder = null
            }
        }

        playbackModel.isInUserQueue.observe(viewLifecycleOwner) {
            if (it) {
                // Remove any highlighted ViewHolders if the playback is in the user queue.
                detailAdapter.setCurrentSong(null)
                lastHolder?.setHighlighted(false)
                lastHolder = null
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    /**
     * Calculate the position and and scroll to a currently playing item.
     */
    private fun scrollToPlayingItem() {
        // Calculate where the item for the currently played song is, and scroll to there
        val pos = detailModel.albumSortMode.value!!.getSortedSongList(
            detailModel.currentAlbum.value!!.songs
        ).indexOf(playbackModel.song.value)

        if (pos != -1) {
            binding.detailRecycler.post {
                binding.detailRecycler.layoutManager?.startSmoothScroll(
                    CenterSmoothScroller(requireContext(), pos.inc())
                )
            }

            detailModel.doneWithNavToItem()
        }
    }
}
