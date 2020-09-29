package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.SHOW_ALBUMS
import org.oxycblt.auxio.theme.SHOW_ARTISTS
import org.oxycblt.auxio.theme.SHOW_GENRES
import org.oxycblt.auxio.theme.applyColor
import org.oxycblt.auxio.theme.applyDivider
import org.oxycblt.auxio.theme.resolveAttr

class LibraryFragment : Fragment() {

    private val musicModel: MusicViewModel by activityViewModels()
    private val libraryModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLibraryBinding.inflate(inflater)

        val artistAdapter = LibraryAdapter(
            libraryModel.showMode.value!!,
            ClickListener { navToItem(it) }
        )

        binding.libraryRecycler.adapter = artistAdapter
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        libraryModel.sortMode.observe(viewLifecycleOwner) { mode ->
            Log.d(this::class.simpleName, "Updating sort mode to $mode")

            // Update the adapter with the new data
            artistAdapter.updateData(
                when (libraryModel.showMode.value) {
                    SHOW_GENRES -> mode.getSortedGenreList(musicModel.genres.value!!)
                    SHOW_ARTISTS -> mode.getSortedArtistList(musicModel.artists.value!!)
                    SHOW_ALBUMS -> mode.getSortedAlbumList(musicModel.albums.value!!)

                    else -> mode.getSortedArtistList(musicModel.artists.value!!)
                }
            )

            // Then update the shown icon & the currently highlighted sort icon to reflect
            // the new sorting mode.
            binding.libraryToolbar.overflowIcon = ContextCompat.getDrawable(
                requireContext(), mode.iconRes
            )

            binding.libraryToolbar.menu.forEach {
                if (it.itemId == libraryModel.sortMode.value!!.toMenuId()) {
                    it.applyColor(resolveAttr(requireContext(), R.attr.colorPrimary))
                } else {
                    it.applyColor(resolveAttr(requireContext(), android.R.attr.textColorPrimary))
                }
            }
        }

        binding.libraryToolbar.setOnMenuItemClickListener {
            libraryModel.updateSortMode(it)

            true
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.isAlreadyNavigating = false
    }

    private fun navToItem(baseModel: BaseModel) {
        // Don't navigate if an item has already been selected
        if (!libraryModel.isAlreadyNavigating) {
            libraryModel.isAlreadyNavigating = true

            Log.d(this::class.simpleName, "Navigating to the detail fragment for $baseModel.name")

            findNavController().navigate(
                when (baseModel) {
                    is Genre -> MainFragmentDirections.actionShowGenre(baseModel.id)
                    is Artist -> MainFragmentDirections.actionShowArtist(baseModel.id)
                    is Album -> MainFragmentDirections.actionShowAlbum(baseModel.id, true)

                    else -> return
                }
            )
        }
    }
}
