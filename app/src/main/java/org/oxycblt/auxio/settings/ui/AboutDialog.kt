package org.oxycblt.auxio.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentAboutBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.createToast
import org.oxycblt.auxio.ui.isLandscape

/**
 * A [BottomSheetDialogFragment] that shows Auxio's about screen.
 * @author OxygenCobalt
 */
class AboutDialog : BottomSheetDialogFragment() {
    override fun getTheme() = R.style.Theme_BottomSheetFix

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(layoutInflater)
        val musicStore = MusicStore.getInstance()

        binding.aboutVersion.text = BuildConfig.VERSION_NAME

        binding.aboutCode.setOnClickListener { openLinkInBrowser(LINK_CODEBASE) }
        binding.aboutFaq.setOnClickListener { openLinkInBrowser(LINK_FAQ) }
        binding.aboutLicenses.setOnClickListener { openLinkInBrowser(LINK_LICENSES) }

        binding.aboutSongCount.text = getString(
            R.string.format_songs_loaded, musicStore.songs.size.toString()
        )
        binding.aboutAuthor.text = getString(
            R.string.format_author, getString(R.string.label_author_oxycblt)
        )

        if (isLandscape(resources)) {
            val dialog = requireDialog() as BottomSheetDialog
            dialog.findViewById<CoordinatorLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        logD("Dialog created.")

        return binding.root
    }

    /**
     * Go through the process of opening one of the about links in a browser.
     */
    private fun openLinkInBrowser(link: String) {
        check(link in LINKS) { "Invalid link." }

        try {
            val tabsIntent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .setShowTitle(true)
                .build()

            val uri = link.toUri()
            val manager = requireActivity().packageManager
            val browserCandidates = manager.queryIntentActivities(tabsIntent.intent, 0)

            // If there are candidates for this link, then launch it through that.
            if (browserCandidates.size > 0) {
                tabsIntent.launchUrl(requireActivity(), uri)
            } else {
                // If there are no candidates, then fallback to another list of browsers

                val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val fallbackCandidates = manager.queryIntentActivities(browserIntent, 0)

                // If there are candidates here, then launch those.
                if (fallbackCandidates.size > 0) {
                    requireActivity().startActivity(browserIntent)
                } else {
                    // Otherwise they don't have a browser on their phone, meaning they should
                    // just see an error.
                    getString(R.string.error_no_browser).createToast(requireContext())
                }
            }
        } catch (e: Exception) {
            logE("Browser intent launching failed [Probably android's fault]")
            e.printStackTrace()

            // Sometimes people have """""Browsers""""" on their phone according to android,
            // but they actually don't so here's a fallback for that.
            getString(R.string.error_no_browser).createToast(requireContext())
        }
    }

    companion object {
        private const val LINK_CODEBASE = "https://github.com/oxygencobalt/Auxio"
        private const val LINK_FAQ = "$LINK_CODEBASE/blob/master/info/FAQ.md"
        private const val LINK_LICENSES = "$LINK_CODEBASE/blob/master/info/LICENSES.md"

        val LINKS = arrayOf(LINK_CODEBASE, LINK_FAQ, LINK_LICENSES)
    }
}
