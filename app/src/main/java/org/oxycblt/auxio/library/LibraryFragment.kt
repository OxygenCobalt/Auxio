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
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.getLandscapeSpans
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.requireCompatActivity

/**
 * A [Fragment] that shows a custom list of [Genre], [Artist], or [Album] data. Also allows for
 * search functionality.
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

        val libraryAdapter = LibraryAdapter(::onItemSelection) { data, view ->
            ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_NONE)
        }

        // --- UI SETUP ---

        binding.libraryToolbar.apply {
            menu.findItem(libraryModel.sortMode.toMenuId()).isChecked = true

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.submenu_sorting -> {}

                    else -> {
                        it.isChecked = true
                        libraryModel.updateSortMode(it.itemId)
                    }
                }

                true
            }
        }

        binding.libraryRecycler.apply {
            adapter = libraryAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                layoutManager = GridLayoutManager(requireContext(), getLandscapeSpans(resources))
            }
        }

        // --- VIEWMODEL SETUP ---

        libraryModel.libraryData.observe(viewLifecycleOwner) {
            libraryAdapter.updateData(it)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                libraryModel.updateNavigationStatus(false)

                if (it is Song) {
                    onItemSelection(it.album)
                } else {
                    onItemSelection(it)
                }
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.updateNavigationStatus(false)
    }

    /**
     * Navigate to an item
     * @param baseModel The data things should be done with
     */
    private fun onItemSelection(baseModel: BaseModel) {
        if (baseModel is Song) {
            logE("onItemSelection does not support song")
            return
        }

        requireView().rootView.clearFocus()

        if (!libraryModel.isNavigating) {
            libraryModel.updateNavigationStatus(true)

            logD("Navigating to the detail fragment for ${baseModel.name}")

            findNavController().navigate(
                when (baseModel) {
                    is Genre -> LibraryFragmentDirections.actionShowGenre(baseModel.id)
                    is Artist -> LibraryFragmentDirections.actionShowArtist(baseModel.id)
                    is Album -> LibraryFragmentDirections.actionShowAlbum(baseModel.id, false)

                    // If given model wasn't valid, then reset the navigation status
                    // and abort the navigation.
                    else -> {
                        libraryModel.updateNavigationStatus(false)
                        return
                    }
                }
            )
        }
    }
}
