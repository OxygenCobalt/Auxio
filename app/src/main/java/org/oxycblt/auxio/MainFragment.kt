package org.oxycblt.auxio

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getTransparentAccent
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.toColor
import kotlin.IllegalArgumentException

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
        if (MusicStore.getInstance().songs.isEmpty()) {
            findNavController().navigate(MainFragmentDirections.actionReturnToLoading())

            return null
        }

        val colorActive = accent.first.toColor(requireContext())
        val colorInactive = getTransparentAccent(
            requireContext(), accent.first, 150
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

        navController?.let { controller ->
            binding.navBar.setOnNavigationItemSelectedListener {
                navigateWithItem(controller, it)
            }
        }

        // --- VIEWMODEL SETUP ---

        // Change CompactPlaybackFragment's visibility here so that an animation occurs.
        if (!isLandscape(resources)) {
            handleCompactPlaybackVisibility(binding, playbackModel.song.value)

            playbackModel.song.observe(viewLifecycleOwner) {
                handleCompactPlaybackVisibility(binding, it)
            }
        }

        playbackModel.navToItem.observe(viewLifecycleOwner) {
            if (it != null) {
                // If the current destination isn't even LibraryFragment, then navigate there first
                if (binding.navBar.selectedItemId != R.id.library_fragment) {
                    binding.navBar.selectedItemId = R.id.library_fragment
                } else {
                    // If the user currently is in library, check if its valid to navigate to the
                    // item in question.
                    if ((it is Album || it is Song) && shouldGoToAlbum(navController!!)) {
                        binding.navBar.selectedItemId = R.id.library_fragment
                    } else if (it is Artist && shouldGoToArtist(navController!!)) {
                        binding.navBar.selectedItemId = R.id.library_fragment
                    }
                }
            }
        }

        playbackModel.restorePlaybackIfNeeded(requireContext())

        logD("Fragment Created.")

        return binding.root
    }

    // Functions that check if MainFragment should nav over to LibraryFragment, or whether
    // it should stay put. Mostly by checking if the navController is currently in a detail
    // fragment, and if the playing item is already being shown.
    private fun shouldGoToAlbum(controller: NavController): Boolean {
        return (
            controller.currentDestination!!.id == R.id.album_detail_fragment &&
                detailModel.currentAlbum.value?.id != playbackModel.song.value!!.album.id
            ) ||
            controller.currentDestination!!.id == R.id.artist_detail_fragment ||
            controller.currentDestination!!.id == R.id.genre_detail_fragment
    }

    private fun shouldGoToArtist(controller: NavController): Boolean {
        return (
            controller.currentDestination!!.id == R.id.artist_detail_fragment &&
                detailModel.currentArtist.value?.id != playbackModel.song.value!!.album.artist.id
            ) ||
            controller.currentDestination!!.id == R.id.album_detail_fragment ||
            controller.currentDestination!!.id == R.id.genre_detail_fragment
    }

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

    private fun handleCompactPlaybackVisibility(binding: FragmentMainBinding, song: Song?) {
        if (song == null) {
            logD("Hiding CompactPlaybackFragment since no song is being played.")

            binding.compactPlayback.visibility = View.GONE
            playbackModel.disableAnimation()
        } else {
            binding.compactPlayback.visibility = View.VISIBLE
        }
    }
}
