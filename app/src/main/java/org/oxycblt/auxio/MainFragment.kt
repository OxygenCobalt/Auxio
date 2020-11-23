package org.oxycblt.auxio

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getInactiveAlpha
import org.oxycblt.auxio.ui.getTransparentAccent
import org.oxycblt.auxio.ui.toColor
import java.lang.IllegalArgumentException

class MainFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()

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

        // Set up the tints for the navigation icons + text
        val navTints = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(colorInactive, colorActive)
        )

        val navController = (
            childFragmentManager.findFragmentById(R.id.explore_nav_host)
                as NavHostFragment?
            )?.findNavController()

        // --- UI SETUP ---

        binding.lifecycleOwner = this

        binding.navBar.itemIconTintList = navTints
        binding.navBar.itemTextColor = navTints

        navController?.let {
            binding.navBar.setOnNavigationItemSelectedListener { item ->
                navigateWithItem(item, it)
            }
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
                playbackModel.resetCanAnimate()
            } else {
                binding.compactPlayback.visibility = View.VISIBLE
            }
        }

        playbackModel.restorePlaybackIfNeeded(requireContext())

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }

    /**
     * Some custom navigator code based off [NavigationUI] that makes animations function
     */
    private fun navigateWithItem(item: MenuItem, navController: NavController): Boolean {
        if (item.itemId != navController.currentDestination!!.id) {
            val builder = NavOptions.Builder().setLaunchSingleTop(true)

            builder.setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_enter_anim)
                .setPopExitAnim(R.anim.nav_default_exit_anim)

            if ((item.order and Menu.CATEGORY_SECONDARY) == 0) {
                builder.setPopUpTo(navController.graph.startDestination, false)
            }

            val options = builder.build()

            return try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        return true
    }
}
