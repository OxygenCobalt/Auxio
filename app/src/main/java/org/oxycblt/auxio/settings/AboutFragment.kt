/*
 * Copyright (c) 2021 Auxio Project
 * AboutFragment.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentAboutBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A [BottomSheetDialogFragment] that shows Auxio's about screen.
 * @author OxygenCobalt
 */
class AboutFragment : Fragment() {
    private val homeModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(layoutInflater)

        binding.aboutContents.setOnApplyWindowInsetsListener { _, insets ->
            binding.aboutContents.updatePadding(bottom = insets.systemBarsCompat.bottom)
            insets
        }

        binding.aboutToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.aboutVersion.text = BuildConfig.VERSION_NAME
        binding.aboutCode.setOnClickListener { openLinkInBrowser(LINK_CODEBASE) }
        binding.aboutFaq.setOnClickListener { openLinkInBrowser(LINK_FAQ) }
        binding.aboutLicenses.setOnClickListener { openLinkInBrowser(LINK_LICENSES) }

        homeModel.songs.observe(viewLifecycleOwner) { songs ->
            binding.aboutSongCount.text = getString(
                R.string.fmt_songs_loaded, songs.size
            )
        }

        logD("Dialog created.")

        return binding.root
    }

    /**
     * Go through the process of opening a [link] in a browser.
     */
    private fun openLinkInBrowser(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, link.toUri()).setFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 seems to now handle the app chooser situations on its own now
            // [along with adding a new permission that breaks the old manual code], so
            // we just do a typical activity launch.
            try {
                requireContext().startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                // No app installed to open the link
                requireContext().showToast(R.string.err_no_app)
            }
        } else {
            // On older versions of android, opening links from an ACTION_VIEW intent might
            // not work in all cases, especially when no default app was set. If that is the
            // case, we will try to manually handle these cases before we try to launch the
            // browser.
            val pkgName = requireContext().packageManager.resolveActivity(
                browserIntent, PackageManager.MATCH_DEFAULT_ONLY
            )?.activityInfo?.packageName

            if (pkgName != null) {
                if (pkgName == "android") {
                    // No default browser [Must open app chooser, may not be supported]
                    openAppChooser(browserIntent)
                } else {
                    try {
                        browserIntent.setPackage(pkgName)
                        startActivity(browserIntent)
                    } catch (e: ActivityNotFoundException) {
                        // Not browser but an app chooser due to OEM garbage
                        browserIntent.setPackage(null)
                        openAppChooser(browserIntent)
                    }
                }
            } else {
                // No app installed to open the link
                requireContext().showToast(R.string.err_no_app)
            }
        }
    }

    private fun openAppChooser(intent: Intent) {
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            .putExtra(Intent.EXTRA_INTENT, intent)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(chooserIntent)
    }

    companion object {
        private const val LINK_CODEBASE = "https://github.com/oxygencobalt/Auxio"
        private const val LINK_FAQ = "$LINK_CODEBASE/blob/master/info/FAQ.md"
        private const val LINK_LICENSES = "$LINK_CODEBASE/blob/master/info/LICENSES.md"
    }
}
