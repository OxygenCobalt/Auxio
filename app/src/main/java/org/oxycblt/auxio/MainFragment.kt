package org.oxycblt.auxio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.library.LibraryFragment
import org.oxycblt.auxio.songs.SongsFragment

class MainFragment : Fragment() {

    private val shownFragments = listOf(
        0, 1
    )

    private val libraryFragment: LibraryFragment by lazy {
        LibraryFragment()
    }

    private val songsFragment: SongsFragment by lazy {
        SongsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false
        )

        val adapter = PagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        Log.d(this::class.simpleName, "Fragment Created.")

        return binding.root
    }

    private fun getFragment(pos: Int): Fragment {
        if (shownFragments.contains(pos)) {
            return when (pos) {
                1 -> libraryFragment
                0 -> songsFragment

                else -> libraryFragment
            }
        }

        // Not sure how this would happen but it might
        Log.e(
            this::class.simpleName,
            "Something went terribly wrong while swapping fragments, Substituting with libraryFragment."
        )

        return libraryFragment
    }

    private inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = shownFragments.size

        override fun createFragment(position: Int): Fragment {
            return getFragment(position)
        }
    }
}
