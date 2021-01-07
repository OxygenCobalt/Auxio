package org.oxycblt.auxio.songs

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ActionMenu
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getLandscapeSpans
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.requireCompatActivity
import org.oxycblt.auxio.ui.toColor
import kotlin.math.ceil

/**
 * A [Fragment] that shows a list of all songs on the device. Contains options to search/shuffle
 * them.
 * @author OxygenCobalt
 */
class SongsFragment : Fragment(), SearchView.OnQueryTextListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val songsModel: SongsViewModel by activityViewModels()
    private val settingsManager = SettingsManager.getInstance()

    // Lazy init the text size so that it doesn't have to be calculated every time.
    private val indicatorTextSize: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14F,
            requireContext().resources.displayMetrics
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSongsBinding.inflate(inflater)

        val musicStore = MusicStore.getInstance()
        val songAdapter = SongsAdapter(musicStore.songs, ::playSong, ::showSongMenu)
        val searchAdapter = SongSearchAdapter(::playSong, ::showSongMenu)

        // --- UI SETUP ---

        binding.songToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_search -> {
                        TransitionManager.beginDelayedTransition(this, Fade())
                        it.expandActionView()
                    }

                    R.id.action_shuffle -> {
                        playbackModel.shuffleAll()
                    }
                }

                true
            }

            menu.apply {
                val searchAction = findItem(R.id.action_search)
                val shuffleAction = findItem(R.id.action_shuffle)
                val searchView = searchAction.actionView as SearchView

                searchView.queryHint = getString(R.string.hint_search_songs)
                searchView.maxWidth = Int.MAX_VALUE
                searchView.setOnQueryTextListener(this@SongsFragment)

                searchAction.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        binding.songRecycler.adapter = searchAdapter
                        searchAction.isVisible = false
                        shuffleAction.isVisible = false

                        binding.songFastScroll.visibility = View.INVISIBLE
                        binding.songFastScroll.isActivated = false
                        binding.songFastScrollThumb.visibility = View.INVISIBLE

                        songsModel.resetQuery()

                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        songsModel.resetQuery()

                        binding.songRecycler.adapter = songAdapter
                        searchAction.isVisible = true
                        shuffleAction.isVisible = true

                        binding.songFastScroll.visibility = View.VISIBLE
                        binding.songFastScrollThumb.visibility = View.VISIBLE

                        return true
                    }
                })
            }
        }

        binding.songRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                val spans = getLandscapeSpans(resources)

                layoutManager = GridLayoutManager(requireContext(), spans).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (binding.songRecycler.adapter == searchAdapter && position == 0)
                                2 else 1
                        }
                    }
                }
            }

            post {
                if (computeVerticalScrollRange() < height) {
                    binding.songFastScroll.visibility = View.GONE
                    binding.songFastScrollThumb.visibility = View.GONE
                }
            }
        }

        setupFastScroller(binding)

        // --- VIEWMODEL SETUP ---

        songsModel.searchResults.observe(viewLifecycleOwner) {
            if (binding.songRecycler.adapter == searchAdapter) {
                searchAdapter.submitList(it) {
                    binding.songRecycler.scrollToPosition(0)
                }
            }
        }

        logD("Fragment created.")

        return binding.root
    }

    override fun onDestroyView() {
        requireView().rootView.clearFocus()

        super.onDestroyView()
    }

    override fun onQueryTextChange(newText: String): Boolean {
        songsModel.doSearch(newText, requireContext())

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    /**
     * Go through the fast scroller setup process.
     * @param binding Binding required
     */
    private fun setupFastScroller(binding: FragmentSongsBinding) {
        val musicStore = MusicStore.getInstance()

        binding.songFastScroll.apply {
            var concatInterval = -1

            // API 22 and below don't support the state color, so just use the accent.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                textColor = ColorStateList.valueOf(accent.first.toColor(requireContext()))
            }

            setupWithRecyclerView(
                binding.songRecycler,
                { pos ->
                    val item = musicStore.songs[pos]

                    // If the item starts with "the"/"a", then actually use the character after that
                    // as its initial. Yes, this is stupidly western-centric but the code [hopefully]
                    // shouldn't run with other languages.
                    val char: Char = if (item.name.length > 5 &&
                        item.name.startsWith("the ", ignoreCase = true)
                    ) {
                        item.name[4].toUpperCase()
                    } else if (item.name.length > 3 &&
                        item.name.startsWith("a ", ignoreCase = true)
                    ) {
                        item.name[2].toUpperCase()
                    } else {
                        // If it doesn't begin with that word, then just use the first character.
                        item.name[0].toUpperCase()
                    }

                    // Use "#" if the character is a digit, also has the nice side-effect of
                    // truncating extra numbers.
                    if (char.isDigit()) {
                        FastScrollItemIndicator.Text("#")
                    } else {
                        FastScrollItemIndicator.Text(char.toString())
                    }
                }
            )

            showIndicator = { _, i, total ->
                var isGood = true

                if (concatInterval == -1) {
                    // If the scroller size is too small to contain all the entries, truncate entries
                    // so that the fast scroller entries fit.
                    val maxEntries = (height / (indicatorTextSize + textPadding))

                    if (total > maxEntries.toInt()) {
                        concatInterval = ceil(total / maxEntries).toInt()

                        logD("More entries than screen space, truncating by $concatInterval.")

                        check(concatInterval > 1) {
                            "ConcatInterval was one despite truncation being needed"
                        }
                    } else {
                        concatInterval = 1
                    }
                }

                if ((i % concatInterval) != 0) {
                    isGood = false
                }

                isGood
            }

            useDefaultScroller = false

            itemIndicatorSelectedCallbacks.add(
                object : FastScrollerView.ItemIndicatorSelectedCallback {
                    override fun onItemIndicatorSelected(
                        indicator: FastScrollItemIndicator,
                        indicatorCenterY: Int,
                        itemPosition: Int
                    ) {
                        val layoutManager = binding.songRecycler.layoutManager
                            as LinearLayoutManager

                        layoutManager.scrollToPositionWithOffset(itemPosition, 0)
                    }
                }
            )
        }

        binding.songFastScrollThumb.apply {
            setupWithFastScroller(binding.songFastScroll)
        }
    }

    private fun playSong(song: Song) {
        playbackModel.playSong(song, settingsManager.songPlaybackMode)
    }

    private fun showSongMenu(song: Song, view: View) {
        ActionMenu(requireCompatActivity(), view, song, ActionMenu.FLAG_NONE)
    }
}
