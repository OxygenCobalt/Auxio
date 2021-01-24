package org.oxycblt.auxio

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.fixAnimInfoLeak
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.isTablet
import org.oxycblt.auxio.ui.toColor

/**
 * The primary "Home" [Fragment] for Auxio.
 */
class MainFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        // If the music was cleared while the app was closed [Likely due to Auxio being suspended
        // in the background], then navigate back to LoadingFragment to reload the music.
        // This may actually not happen in normal use, but its a good failsafe.
        if (MusicStore.getInstance().songs.isEmpty()) {
            findNavController().navigate(MainFragmentDirections.actionReturnToLoading())

            return null
        }

        val colorActive = Accent.get().color.toColor(requireContext())
        val colorInactive = ColorUtils.setAlphaComponent(colorActive, 150)

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

        binding.navBar.apply {
            itemIconTintList = navTints
            itemTextColor = navTints

            if (isTablet(resources) && !isLandscape(resources)) {
                labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            }

            navController?.let { controller ->
                binding.navBar.setOnNavigationItemSelectedListener {
                    navigateWithItem(controller, it)
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        // Change CompactPlaybackFragment's visibility here so that an animation occurs.
        handleCompactPlaybackVisibility(binding, playbackModel.song.value)

        playbackModel.song.observe(viewLifecycleOwner) {
            handleCompactPlaybackVisibility(binding, it)
        }

        detailModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null && navController != null) {
                val curDest = navController.currentDestination?.id

                // SongsFragment and SettingsFragment have no navigation pathways, so correct
                // them to the library tab instead.
                if (curDest == R.id.songs_fragment || curDest == R.id.settings_fragment) {
                    binding.navBar.selectedItemId = R.id.library_fragment
                }
            }
        }

        playbackModel.restorePlaybackIfNeeded(requireContext())

        logD("Fragment Created.")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fixAnimInfoLeak()
    }

    /**
     * Custom navigator code that has proper animations, unlike BottomNavigationView.setupWithNavController().
     */
    private fun navigateWithItem(navController: NavController, item: MenuItem): Boolean {
        if (navController.currentDestination!!.id != item.itemId) {
            // Create custom NavOptions myself so that animations work
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.animator.nav_default_enter_anim)
                .setExitAnim(R.animator.nav_default_exit_anim)
                .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
                .build()

            return try {
                navController.navigate(item.itemId, null, options)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        return false
    }

    /**
     * Handle the visibility of CompactPlaybackFragment. Done here so that there's a nice animation.
     */
    private fun handleCompactPlaybackVisibility(binding: FragmentMainBinding, song: Song?) {
        if (song == null) {
            logD("Hiding CompactPlaybackFragment since no song is being played.")

            if (!isLandscape(resources)) {
                binding.compactPlayback.visibility = View.GONE
            }

            playbackModel.disableAnimation()
        } else {
            binding.compactPlayback.visibility = View.VISIBLE
        }
    }
}
