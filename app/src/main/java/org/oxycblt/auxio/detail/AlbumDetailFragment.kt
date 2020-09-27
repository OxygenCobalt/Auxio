package org.oxycblt.auxio.detail

import android.content.res.ColorStateList
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
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.toColor

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
            ClickListener {
                Log.d(this::class.simpleName, it.name)
            }
        )

        binding.lifecycleOwner = this
        binding.detailModel = detailModel
        binding.album = detailModel.currentAlbum

        binding.albumSongRecycler.apply {
            adapter = songAdapter
            applyDivider()
            setHasFixedSize(true)
        }

        binding.albumToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

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

            binding.albumArtist.setBackgroundResource(R.drawable.ripple)
        }

        detailModel.albumSortMode.observe(viewLifecycleOwner) { mode ->
            // Update the current sort icon
            binding.albumSortButton.setImageResource(mode.iconRes)

            // Then update the sort mode of the album adapter.
            songAdapter.submitList(
                detailModel.currentAlbum!!.songs.sortedWith(
                    SortMode.songSortComparators.getOrDefault(
                        mode,

                        // If any invalid value is given, just default to the normal sort order.
                        compareByDescending { it.track }
                    )
                )
            )
        }

        // Don't enable the sort button if there's only one song [or less]
        if (detailModel.currentAlbum!!.numSongs < 2) {
            binding.albumSortButton.imageTintList = ColorStateList.valueOf(
                R.color.inactive_color.toColor(requireContext())
            )

            binding.albumSortButton.isEnabled = false
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        detailModel.currentAlbum = null
    }
}
