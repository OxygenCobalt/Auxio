package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * A Base [Fragment] implementing a [OnBackPressedCallback] so that Auxio will navigate upwards
 * instead of out of the app if a Detail Fragment is currently open. Also carries the
 * multi-navigation fix.
 * @author OxygenCobalt
 */
abstract class DetailFragment : Fragment() {
    protected val detailModel: DetailViewModel by activityViewModels()

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

    private val callback = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            val navController = findNavController()
            // Check if it's the root of nested fragments in this navhost
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
