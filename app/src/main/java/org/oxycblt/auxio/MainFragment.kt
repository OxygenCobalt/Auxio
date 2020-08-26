package org.oxycblt.auxio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.library.LibraryFragment

// TODO: Placeholder, page count will be dynamic
private const val PAGES = 1

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false
        )

        val adapter = FragmentAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        return binding.root
    }
}

class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = PAGES

    override fun createFragment(position: Int): Fragment {
        // TODO: Also placeholder, remove when there are other fragments than just library
        return LibraryFragment()
    }
}
