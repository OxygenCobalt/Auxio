package org.oxycblt.auxio.search

import android.content.res.ColorStateList
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
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.fixAnimationInfoMemoryLeak
import org.oxycblt.auxio.ui.getSpans
import org.oxycblt.auxio.ui.requireCompatActivity
import org.oxycblt.auxio.ui.toColor

/**
 * A [Fragment] that allows for the searching of the entire music library.
 * TODO: Add "Recent Searches" & No Results indicator
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

        // Apply the accents manually. Not going through the mess of converting my app's
        // styling to Material given all the second-and-third-order effects it has.
        val accent = Accent.get().color.toColor(requireContext())

        val searchAdapter = SearchAdapter(::onItemSelection) { view, data ->
            ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_NONE)
        }

        // --- UI SETUP --

        binding.searchToolbar.apply {
            menu.findItem(searchModel.filterMode.toId()).isChecked = true

            setOnMenuItemClickListener {
                if (it.itemId != R.id.submenu_filtering) {
                    it.isChecked = true
                    searchModel.updateFilterModeWithId(it.itemId, requireContext())

                    true
                } else false
            }
        }

        binding.searchTextLayout.apply {
            boxStrokeColor = accent
            hintTextColor = ColorStateList.valueOf(accent)
            setEndIconTintList(
                ColorStateList.valueOf(R.color.control_color.toColor(requireContext()))
            )
        }

        binding.searchEditText.addTextChangedListener {
            searchModel.doSearch(it?.toString() ?: "", requireContext())
        }

        binding.searchRecycler.apply {
            adapter = searchAdapter
            val spans = context.getSpans()

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

        searchModel.searchResults.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it) {
                binding.searchRecycler.scrollToPosition(0)
            }

            if (it.isEmpty()) {
                binding.searchAppbar.setExpanded(true)
                binding.searchRecycler.visibility = View.GONE
            } else {
                binding.searchRecycler.visibility = View.VISIBLE
            }
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    when (it) {
                        is Song -> SearchFragmentDirections.actionShowAlbum(it.album.id)
                        is Album -> SearchFragmentDirections.actionShowAlbum(it.id)
                        is Artist -> SearchFragmentDirections.actionShowArtist(it.id)

                        else -> return@observe
                    }
                )
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fixAnimationInfoMemoryLeak()
    }

    override fun onResume() {
        super.onResume()

        searchModel.updateNavigationStatus(false)
    }

    /**
     * Navigate to an item, or play it, depending on what the given item is.
     * @param baseModel The data the action should be done with
     */
    private fun onItemSelection(baseModel: BaseModel) {
        if (baseModel is Song) {
            playbackModel.playSong(baseModel)

            return
        }

        requireView().rootView.clearFocus()

        if (!searchModel.isNavigating) {
            searchModel.updateNavigationStatus(true)

            logD("Navigating to the detail fragment for ${baseModel.name}")

            findNavController().navigate(
                when (baseModel) {
                    is Genre -> SearchFragmentDirections.actionShowGenre(baseModel.id)
                    is Artist -> SearchFragmentDirections.actionShowArtist(baseModel.id)
                    is Album -> SearchFragmentDirections.actionShowAlbum(baseModel.id)

                    // If given model wasn't valid, then reset the navigation status
                    // and abort the navigation.
                    else -> {
                        searchModel.updateNavigationStatus(false)
                        return
                    }
                }
            )
        }
    }
}
