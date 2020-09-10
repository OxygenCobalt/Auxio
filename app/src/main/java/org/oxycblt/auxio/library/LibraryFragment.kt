package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLibraryBinding>(
            inflater, R.layout.fragment_library, container, false
        )

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, callback
        )

        val navHost = childFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment
        navController = navHost?.navController

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        callback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()

        callback.isEnabled = false
    }

    val callback = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {

            // If at the root of the navigation, perform onBackPressed from the main activity.
            if (navController?.currentDestination?.id == navController?.graph?.startDestination) {
                // Disable the callback as it will get caught in an infinite loop otherwise.
                isEnabled = false

                requireActivity().onBackPressed()

                isEnabled = true
            } else {
                navController?.navigateUp()
            }
        }
    }
}
