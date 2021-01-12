package org.oxycblt.auxio.songs

import android.content.res.ColorStateList
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
class SongsFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
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

        // --- UI SETUP ---

        binding.songToolbar.apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_shuffle -> {
                        playbackModel.shuffleAll()
                    }
                }

                true
            }
        }

        binding.songRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                layoutManager = GridLayoutManager(requireContext(), getLandscapeSpans(resources))
            }

            post {
                if (computeVerticalScrollRange() < height) {
                    binding.songFastScroll.visibility = View.GONE
                    binding.songFastScrollThumb.visibility = View.GONE
                }
            }
        }

        setupFastScroller(binding)

        logD("Fragment created.")

        return binding.root
    }

    override fun onDestroyView() {
        requireView().rootView.clearFocus()

        super.onDestroyView()
    }

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
