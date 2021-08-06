package org.oxycblt.auxio.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentAboutBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.showToast

/**
 * A [BottomSheetDialogFragment] that shows Auxio's about screen.
 * @author OxygenCobalt
 */
class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(layoutInflater)
        val musicStore = MusicStore.getInstance()

        binding.aboutToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.aboutVersion.text = BuildConfig.VERSION_NAME
        binding.aboutCode.setOnClickListener { openLinkInBrowser(LINK_CODEBASE) }
        binding.aboutFaq.setOnClickListener { openLinkInBrowser(LINK_FAQ) }
        binding.aboutLicenses.setOnClickListener { openLinkInBrowser(LINK_LICENSES) }
        binding.aboutSongCount.text = getString(
            R.string.fmt_songs_loaded, musicStore.songs.size
        )

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
                } catch (exception: ActivityNotFoundException) {
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
