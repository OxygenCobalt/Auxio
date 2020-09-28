package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.library.adapters.ArtistAdapter
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.applyDivider

class LibraryFragment : Fragment() {

    private val musicModel: MusicViewModel by activityViewModels()
    private val libraryModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLibraryBinding.inflate(inflater)

        val artistAdapter = ArtistAdapter(
            ClickListener { navToItem(it) }
        )

        binding.libraryRecycler.adapter = artistAdapter
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        libraryModel.sortMode.observe(viewLifecycleOwner) { mode ->
            binding.libraryToolbar.overflowIcon = ContextCompat.getDrawable(
                requireContext(), mode.iconRes
            )

            artistAdapter.updateData(
                mode.getSortedArtistList(
                    musicModel.artists.value!!
                )
            )
        }

        binding.libraryToolbar.setOnMenuItemClickListener {
            libraryModel.updateSortMode(it)

            true
        }

        binding.libraryToolbar.inflateMenu(R.menu.menu_library)

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
