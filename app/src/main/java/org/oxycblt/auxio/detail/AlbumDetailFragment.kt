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
import org.oxycblt.auxio.databinding.FragmentAlbumDetailBinding
import org.oxycblt.auxio.detail.adapters.AlbumSongAdapter
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.createToast
import org.oxycblt.auxio.ui.disable
import org.oxycblt.auxio.ui.setupAlbumSongActions

/**
 * The [DetailFragment] for an album.
 * @author OxygenCobalt
 */
class AlbumDetailFragment : DetailFragment() {

    private val args: AlbumDetailFragmentArgs by navArgs()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAlbumDetailBinding.inflate(inflater)

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

        val songAdapter = AlbumSongAdapter(
            doOnClick = { playbackModel.playSong(it, PlaybackMode.IN_ALBUM) },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupAlbumSongActions(
                    data, requireContext(), detailModel, playbackModel
                )
            }
        )

        var lastHolder: AlbumSongAdapter.ViewHolder? = null

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.playbackModel = playbackModel
        binding.album = detailModel.currentAlbum.value!!

        binding.albumToolbar.apply {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
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
                        context.getString(R.string.label_queue_added).createToast(requireContext())

                        true
                    }

                    else -> false
                }
            }
        }

        binding.albumSongRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)
        }

        // Don't enable the sort button if there's only one song [or less]
        if (detailModel.currentAlbum.value!!.songs.size < 2) {
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

        detailModel.doneWithNavToParent()

        detailModel.navToParent.observe(viewLifecycleOwner) {
            if (it) {
                if (!args.enableParentNav) {
                    findNavController().navigateUp()
                } else {
                    findNavController().navigate(
                        AlbumDetailFragmentDirections.actionShowParentArtist(
                            detailModel.currentAlbum.value!!.artist.id
                        )
                    )
                }
            }
        }

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                val pos = detailModel.albumSortMode.value!!.getSortedSongList(
                    detailModel.currentAlbum.value!!.songs
                ).indexOfFirst { it.id == song.id }

                if (pos != -1) {
                    binding.albumSongRecycler.post {
                        lastHolder?.removePlaying()

                        lastHolder = binding.albumSongRecycler.getChildViewHolder(
                            binding.albumSongRecycler.getChildAt(pos)
                        ) as AlbumSongAdapter.ViewHolder

                        lastHolder?.setPlaying(requireContext())
                    }

                    return@observe
                } else {
                    lastHolder?.removePlaying()
                }
            }

            lastHolder?.removePlaying()
        }

        playbackModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it is Song) {
                    // Calculate where the item for the currently played song is, and navigate to there.
                    val pos = detailModel.albumSortMode.value!!.getSortedSongList(
                        detailModel.currentAlbum.value!!.songs
                    ).indexOf(playbackModel.song.value)

                    if (pos != -1) {
                        binding.albumSongRecycler.post {
                            // Only scroll after UI creation
                            val y = binding.albumSongRecycler.y +
                                binding.albumSongRecycler.getChildAt(pos).y

                            binding.nestedScroll.scrollTo(0, y.toInt())
                        }

                        playbackModel.doneWithNavToItem()
                    }
                }

                if (detailModel.currentAlbum.value!!.id == playbackModel.song.value!!.album.id) {
                    playbackModel.doneWithNavToItem()
                }
            }
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
