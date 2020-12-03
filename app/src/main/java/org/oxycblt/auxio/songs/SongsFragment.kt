package org.oxycblt.auxio.songs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.setupSongActions

/**
 * A [Fragment] that shows a list of all songs on the device. Contains options to search/shuffle
 * them.
 * @author OxygenCobalt
 */
class SongsFragment : Fragment(), SearchView.OnQueryTextListener {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSongsBinding.inflate(inflater)

        val musicStore = MusicStore.getInstance()

        val songAdapter = SongsAdapter(
            musicStore.songs,
            doOnClick = { playbackModel.playSong(it, PlaybackMode.ALL_SONGS) },
            doOnLongClick = { data, view ->
                PopupMenu(requireContext(), view).setupSongActions(
                    data, requireContext(), playbackModel
                )
            }
        )

        // --- UI SETUP ---

        binding.songToolbar.apply {
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_shuffle) {
                    playbackModel.shuffleAll()
                }
                true
            }
        }

        binding.songRecycler.apply {
            adapter = songAdapter
            setHasFixedSize(true)
        }

        setupFastScroller(binding)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    private fun setupFastScroller(binding: FragmentSongsBinding) {
        val musicStore = MusicStore.getInstance()

        binding.songFastScroll.apply {
            var hasAddedNumber = false
            var iters = 0

            // TODO: Do selection instead of using iters

            setupWithRecyclerView(
                binding.songRecycler,
                { pos ->
                    val item = musicStore.songs[pos]
                    iters++

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

                    // Check if this song starts with a number, if so, then concat it with a single
                    // "Numeric" item if haven't already.
                    // This check only occurs on the second time the fast scroller is polled for items.
                    if (iters >= musicStore.songs.size) {
                        if (char.isDigit()) {
                            if (!hasAddedNumber) {
                                hasAddedNumber = true

                                return@setupWithRecyclerView FastScrollItemIndicator.Text("#")
                            } else {
                                return@setupWithRecyclerView null
                            }
                        }
                    }

                    FastScrollItemIndicator.Text(
                        char.toString()
                    )
                }
            )

            useDefaultScroller = false

            itemIndicatorSelectedCallbacks.add(object :
                    FastScrollerView.ItemIndicatorSelectedCallback {
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
            textAppearanceRes = R.style.TextAppearance_ThumbIndicator
        }
    }
}
