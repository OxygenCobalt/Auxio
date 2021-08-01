package org.oxycblt.auxio.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.getSpans
import org.oxycblt.auxio.ui.newMenu

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * @author OxygenCobalt
 */
class SearchFragment : Fragment() {
    // SearchViewModel only scoped to this Fragment
    private val searchModel: SearchViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater)

        val searchAdapter = SearchAdapter(::onItemSelection) { view, data ->
            newMenu(view, data)
        }

        val toolbarParams = binding.searchToolbar.layoutParams as AppBarLayout.LayoutParams
        val defaultParams = toolbarParams.scrollFlags

        // --- UI SETUP --

        binding.lifecycleOwner = viewLifecycleOwner

        binding.searchToolbar.apply {
            menu.findItem(searchModel.filterMode.toId()).isChecked = true

            setOnMenuItemClickListener { item ->
                if (item.itemId != R.id.submenu_filtering) {
                    searchModel.updateFilterModeWithId(item.itemId, requireContext())
                    item.isChecked = true
                    true
                } else {
                    false
                }
            }
        }

        binding.searchEditText.addTextChangedListener { text ->
            // Run the search with the updated text as the query
            searchModel.doSearch(text?.toString() ?: "", requireContext())
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            val spans = getSpans()

            if (spans != -1) {
                layoutManager = GridLayoutManager(requireContext(), spans).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int =
                            if (searchAdapter.currentList[position] is Header) spans else 1
                    }
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        searchModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results) {
                binding.searchRecycler.scrollToPosition(0)
            }

            if (results.isEmpty()) {
                // If the data is empty, then the ability for the toolbar to collapse
                // on scroll should be disabled.
                binding.searchAppbar.setExpanded(true)
                binding.searchRecycler.visibility = View.GONE
                toolbarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
            } else {
                binding.searchRecycler.visibility = View.VISIBLE
                toolbarParams.scrollFlags = defaultParams
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) { item ->
            findNavController().navigate(
                when (item) {
                    is Song -> SearchFragmentDirections.actionShowAlbum(item.album.id)
                    is Album -> SearchFragmentDirections.actionShowAlbum(item.id)
                    is Artist -> SearchFragmentDirections.actionShowArtist(item.id)

                    else -> return@observe
                }
            )
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        searchModel.setNavigating(false)
    }

    /**
     * Function that handles when an [item] is selected.
     * Handles all datatypes that are selectable.
     */
    private fun onItemSelection(item: BaseModel) {
        if (item is Song) {
            playbackModel.playSong(item)

            return
        }

        // Get rid of the keyboard if we are navigating
        requireView().rootView.clearFocus()

        if (!searchModel.isNavigating) {
            searchModel.setNavigating(true)

            logD("Navigating to the detail fragment for ${item.name}")

            findNavController().navigate(
                when (item) {
                    is Genre -> SearchFragmentDirections.actionShowGenre(item.id)
                    is Artist -> SearchFragmentDirections.actionShowArtist(item.id)
                    is Album -> SearchFragmentDirections.actionShowAlbum(item.id)

                    // If given model wasn't valid, then reset the navigation status
                    // and abort the navigation.
                    else -> {
                        searchModel.setNavigating(false)
                        return
                    }
                }
            )
        }
    }
}
