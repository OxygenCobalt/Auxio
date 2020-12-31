package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.isLandscape
import org.oxycblt.auxio.ui.memberBinding

/**
 * A Base [Fragment] implementing the base features shared across all detail fragments.
 * TODO: Add custom artist images
 * @author OxygenCobalt
 */
abstract class DetailFragment : Fragment() {
    protected val detailModel: DetailViewModel by activityViewModels()
    protected val playbackModel: PlaybackViewModel by activityViewModels()
    protected val binding: FragmentDetailBinding by memberBinding(
        FragmentDetailBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()

        callback.isEnabled = true
        detailModel.updateNavigationStatus(false)
    }

    override fun onPause() {
        super.onPause()
        callback.isEnabled = false
    }

    /**
     * Shortcut method for doing setup of the detail toolbar.
     */
    protected fun setupToolbar(@MenuRes menu: Int, onMenuClick: (id: Int) -> Boolean) {
        binding.detailToolbar.apply {
            inflateMenu(menu)

            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener {
                onMenuClick(it.itemId)
            }
        }
    }

    /**
     * Shortcut method for recyclerview setup
     */
    protected fun setupRecycler(detailAdapter: ListAdapter<BaseModel, RecyclerView.ViewHolder>) {
        binding.detailRecycler.apply {
            adapter = detailAdapter
            setHasFixedSize(true)

            if (isLandscape(resources)) {
                layoutManager = GridLayoutManager(requireContext(), 2).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0) 2 else 1
                        }
                    }
                }
            }
        }
    }

    // Override the back button so that going back will only exit the detail fragments instead of
    // the entire app.
    private val callback = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            val navController = findNavController()
            // Check if it's the root of nested fragments in this NavHost
            if (navController.currentDestination?.id == navController.graph.startDestination) {
                isEnabled = false
                requireActivity().onBackPressed()
                isEnabled = true
            } else {
                navController.navigateUp()
            }
        }
    }
}
