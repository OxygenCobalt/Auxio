package org.oxycblt.auxio

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getInactiveAlpha
import org.oxycblt.auxio.ui.getTransparentAccent
import org.oxycblt.auxio.ui.toColor

class MainFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

    private val shownFragments = listOf(0, 1)
    private val tabIcons = listOf(
        R.drawable.ic_library,
        R.drawable.ic_song
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        // If the music was cleared while the app was closed [Likely due to Auxio being suspended
        // in the background], then navigate back to LoadingFragment to reload the music.
        if (MusicStore.getInstance().songs.isEmpty()) {
            findNavController().navigate(MainFragmentDirections.actionReturnToLoading())

            return null
        }

        val colorActive = accent.first.toColor(requireContext())
        val colorInactive = getTransparentAccent(
            requireContext(),
            accent.first,
            getInactiveAlpha(accent.first)
        )
        val navIconTints = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(colorInactive, colorActive)
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        binding.navBar.itemIconTintList = navIconTints
        binding.navBar.itemTextColor = navIconTints
        ((childFragmentManager.findFragmentById(R.id.explore_nav_host) as NavHostFragment?))?.let {
            // TODO: Add animation with BottomNavigationView navs
            binding.navBar.setupWithNavController(it.findNavController())
        }

        // --- VIEWMODEL SETUP ---

        // Change CompactPlaybackFragment's visibility here so that an animation occurs.
        playbackModel.song.observe(viewLifecycleOwner) {
            if (it == null) {
                Log.d(
                    this::class.simpleName,
                    "Hiding CompactPlaybackFragment since no song is being played."
                )

                binding.compactPlayback.visibility = View.GONE
            } else {
                binding.compactPlayback.visibility = View.VISIBLE
            }
        }

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }
}
