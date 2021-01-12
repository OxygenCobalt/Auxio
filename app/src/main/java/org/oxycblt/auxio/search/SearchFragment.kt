package org.oxycblt.auxio.search

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColor
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSearchBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getLandscapeSpans
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.requireCompatActivity
import org.oxycblt.auxio.ui.toColor

// TODO: Fix TextView memory leak
// TODO: Add Filtering
// TODO: Add "No Results" marker
class SearchFragment : Fragment() {
    // SearchViewModel only scoped to this Fragment
    private val searchModel: SearchViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSearchBinding.inflate(inflater)

        // Apply the accents manually. Not going through the mess of converting my app's
        // styling to Material given all the second-and-third-order effects it has.
        val accent = accent.first.toColor(requireContext())

        val searchAdapter = SearchAdapter(::onItemSelection) { data, view ->
            ActionMenu(requireCompatActivity(), view, data, ActionMenu.FLAG_NONE)
        }

        // --- UI SETUP --

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

            if (isLandscape(resources)) {
                val spans = getLandscapeSpans(resources)

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        try {
            // Use reflection to fix a memory leak in the fragment source code that occurs
            // from leaving an EditText focused when exiting the view.
            // I cant believe I have to do this.
            Fragment::class.java.getDeclaredMethod("setFocusedView", View::class.java).apply {
                isAccessible = true
                invoke(this@SearchFragment, null)
            }
        } catch (e: Exception) {
            logE("Hacky reflection leak fix failed. Oh well.")

            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        searchModel.updateNavigationStatus(false)
    }

    /**
     * Navigate to an item, or play it, depending on what the given item is.
     * @param baseModel The data things should be done with
     */
    private fun onItemSelection(baseModel: BaseModel) {
        if (baseModel is Song) {
            val settingsManager = SettingsManager.getInstance()
            playbackModel.playSong(baseModel, settingsManager.songPlaybackMode)

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
                    is Album -> SearchFragmentDirections.actionShowAlbum(baseModel.id, false)

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
