package org.oxycblt.auxio.songs

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.addIndicatorCallback
import org.oxycblt.auxio.ui.canScroll
import org.oxycblt.auxio.ui.getSpans
import org.oxycblt.auxio.ui.newMenu
import kotlin.math.ceil

/**
 * A [Fragment] that shows a list of all songs on the device.
 * Contains options to search/shuffle them.
 * @author OxygenCobalt
 */
class SongsFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val musicStore = MusicStore.getInstance()

    // Lazy init the text size so that it doesn't have to be calculated every time.
    private val indicatorTextSize: Float by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14F,
            resources.displayMetrics
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSongsBinding.inflate(inflater)
        val songAdapter = SongsAdapter(musicStore.songs, playbackModel::playSong) { view, data ->
            newMenu(view, data)
        }

        // --- UI SETUP ---

        binding.songToolbar.apply {
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_shuffle) {
                    playbackModel.shuffleAll()
                    true
                } else false
            }
        }

        binding.songRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)

            val spans = getSpans()

            if (spans != 1) {
                layoutManager = GridLayoutManager(requireContext(), spans)
            }

            post {
                // Disable fast scrolling if there is nothing to scroll
                if (!canScroll()) {
                    binding.songFastScroll.visibility = View.GONE
                    binding.songFastScrollThumb.visibility = View.GONE
                }
            }
        }

        binding.songFastScroll.setup(binding.songRecycler, binding.songFastScrollThumb)

        logD("Fragment created.")

        return binding.root
    }

    /**
     * Perform the (Frustratingly Long and Complicated) FastScrollerView setup.
     */
    private fun FastScrollerView.setup(recycler: RecyclerView, thumb: CobaltScrollThumb) {
        var concatInterval: Int = -1

        // API 22 and below don't support the state color, so just use the accent.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            textColor = Accent.get().getStateList(requireContext())
        }

        setupWithRecyclerView(
            recycler,
            { pos ->
                val char = musicStore.songs[pos].name.first

                // Use "#" if the character is a digit, also has the nice side-effect of
                // truncating extra numbers.
                if (char.isDigit()) {
                    FastScrollItemIndicator.Text("#")
                } else {
                    FastScrollItemIndicator.Text(char.toString())
                }
            },
            null, false
        )

        showIndicator = { _, i, total ->
            if (concatInterval == -1) {
                // If the scroller size is too small to contain all the entries, truncate entries
                // so that the fast scroller entries fit.
                val maxEntries = (height / (indicatorTextSize + textPadding))

                if (total > maxEntries.toInt()) {
                    concatInterval = ceil(total / maxEntries).toInt()

                    check(concatInterval > 1) {
                        "Needed to truncate, but concatInterval was 1 or lower anyway"
                    }

                    logD("More entries than screen space, truncating by $concatInterval.")
                } else {
                    concatInterval = 1
                }
            }

            // Any items that need to be truncated will be hidden
            (i % concatInterval) == 0
        }

        addIndicatorCallback { _, _, pos ->
            recycler.apply {
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)

                stopScroll()
            }
        }

        thumb.setup(this)
    }

    /**
     * Dumb shortcut for getting the first letter in a string, while regarding certain
     * semantics when it comes to articles.
     */
    private val String.first: Char get() {
        // If the name actually starts with "The" or "A", get the character *after* that word.
        // Yes, this is stupidly english centric but it wont run with other languages.
        if (length > 5 && startsWith("the ", true)) {
            return get(4).toUpperCase()
        }

        if (length > 3 && startsWith("a ", true)) {
            return get(2).toUpperCase()
        }

        return get(0).toUpperCase()
    }
}
