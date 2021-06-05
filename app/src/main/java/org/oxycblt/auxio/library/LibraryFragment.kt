package org.oxycblt.auxio.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.getSpans
import org.oxycblt.auxio.ui.newMenu

/**
 * A [Fragment] that shows a custom list of [Genre], [Artist], or [Album] data. Also allows for
 * search functionality.
 * TODO: Add fast scrolling
 * @author OxygenCobalt
 */
class LibraryFragment : Fragment() {
    private val libraryModel: LibraryViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLibraryBinding.inflate(inflater)

        val libraryAdapter = LibraryAdapter(::navToDetail) { view, data ->
            newMenu(view, data)
        }

        // --- UI SETUP ---

        binding.libraryToolbar.apply {
            menu.findItem(libraryModel.sortMode.toMenuId()).isChecked = true

            setOnMenuItemClickListener { item ->
                if (item.itemId != R.id.submenu_sorting) {
                    libraryModel.updateSortMode(item.itemId)
                    item.isChecked = true
                    true
                } else {
                    false
                }
            }
        }

        binding.libraryRecycler.apply {
            adapter = libraryAdapter
            setHasFixedSize(true)

            val spans = getSpans()
            if (spans != 1) {
                layoutManager = GridLayoutManager(requireContext(), spans)
            }
        }

        // --- VIEWMODEL SETUP ---

        libraryModel.libraryData.observe(viewLifecycleOwner) { data ->
            libraryAdapter.updateData(data)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                libraryModel.setNavigating(false)

                if (item is Parent) {
                    navToDetail(item)
                } else if (item is Song) {
                    navToDetail(item.album)
                }
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.setNavigating(false)
    }

    /**
     * Navigate to the detail UI for a [parent].
     */
    private fun navToDetail(parent: Parent) {
        requireView().rootView.clearFocus()

        if (!libraryModel.isNavigating) {
            libraryModel.setNavigating(true)

            logD("Navigating to the detail fragment for ${parent.name}")

            findNavController().navigate(
                when (parent) {
                    is Genre -> LibraryFragmentDirections.actionShowGenre(parent.id)
                    is Artist -> LibraryFragmentDirections.actionShowArtist(parent.id)
                    is Album -> LibraryFragmentDirections.actionShowAlbum(parent.id)
                }
            )
        }
    }
}
