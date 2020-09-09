package org.oxycblt.auxio

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.library.LibraryFragment
import org.oxycblt.auxio.library.SongsFragment
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.processing.MusicLoaderResponse
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.getInactiveAlpha
import org.oxycblt.auxio.theme.getTransparentAccent
import org.oxycblt.auxio.theme.toColor

class MainActivity : AppCompatActivity() {

    private val shownFragments = listOf(0, 1)

    private val libraryFragment: LibraryFragment by lazy { LibraryFragment() }
    private val songsFragment: SongsFragment by lazy { SongsFragment() }

    private val tabIcons = listOf(
        R.drawable.ic_library,
        R.drawable.ic_song
    )

    private lateinit var binding: ActivityMainBinding

    private val musicModel: MusicViewModel by lazy {
        ViewModelProvider(
            this, MusicViewModel.Factory(application)
        ).get(MusicViewModel::class.java)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Apply the theme
        setTheme(accent.second)

        return super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        binding.lifecycleOwner = this

        val adapter = PagerAdapter(this)
        binding.viewPager.adapter = adapter

        val colorActive = accent.first.toColor(baseContext)
        val colorInactive = getTransparentAccent(
            baseContext,
            accent.first,
            getInactiveAlpha(accent.first)
        )

        // Link the ViewPager & Tab View
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.icon = ContextCompat.getDrawable(baseContext, tabIcons[position])

            // Set the icon tint to deselected if its not the default tab
            if (position > 0) {
                tab.icon?.setTint(colorInactive)
            }

            // Init the fragment
            fragmentAt(position)
        }.attach()

        // Set up the selected/deselected colors
        binding.tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(colorActive)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(colorInactive)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            }
        )

        Log.d(this::class.simpleName, musicModel.done.toString())

        musicModel.response.observe(
            this,
            {
                if (it == MusicLoaderResponse.DONE) {
                    binding.loadingFragment.visibility = View.GONE
                    binding.viewPager.visibility = View.VISIBLE
                }
            }
        )

        Log.d(this::class.simpleName, "Activity Created.")
    }

    private fun fragmentAt(position: Int): Fragment {
        return when (position) {
            0 -> libraryFragment
            1 -> songsFragment

            else -> libraryFragment
        }
    }

    private inner class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = shownFragments.size

        override fun createFragment(position: Int): Fragment {
            Log.d(this::class.simpleName, "Switching to fragment $position.")

            if (shownFragments.contains(position)) {
                return fragmentAt(position)
            }

            // Not sure how this would happen but it might
            Log.e(
                this::class.simpleName,
                "Attempted to index a fragment that shouldn't be shown. Returning libraryFragment."
            )

            return libraryFragment
        }
    }
}
