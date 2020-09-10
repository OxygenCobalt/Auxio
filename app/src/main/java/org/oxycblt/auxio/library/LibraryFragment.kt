package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import org.oxycblt.auxio.R

class LibraryFragment : NavHostFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this, callback
        )

        navController.setGraph(R.navigation.nav_library)

        Log.d(this::class.simpleName, "Fragment created.")
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
            if (navController.currentDestination?.id == navController.graph.startDestination) {
                // Disable the callback as it will get caught in an infinite loop otherwise.
                isEnabled = false

                requireActivity().onBackPressed()

                isEnabled = true
            } else {
                navController.navigateUp()
            }
        }
    }
}
