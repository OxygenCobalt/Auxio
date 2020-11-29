package org.oxycblt.auxio.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.adapters.AccentAdapter
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getAccentItemSummary

class SettingListFragment : PreferenceFragmentCompat(), SettingsManager.Callback {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>(SettingsManager.Keys.KEY_ACCENT)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showAccentDialog()
                true
            }

            summary = getAccentItemSummary(requireActivity(), accent)
        }

        Log.d(this::class.simpleName, "Fragment created.")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }

    override fun onResume() {
        super.onResume()

        SettingsManager.getInstance().addCallback(this)
    }

    override fun onPause() {
        super.onPause()

        SettingsManager.getInstance().removeCallback(this)
    }

    private fun showAccentDialog() {
        MaterialDialog(requireActivity()).show {
            title(R.string.label_settings_accent)

            // Roll my own RecyclerView since [To no surprise whatsoever] Material Dialogs
            // has a bug where ugly dividers will show with the RecyclerView even if you disable them.
            // This is why I hate using third party libraries.
            val recycler = RecyclerView(requireContext()).apply {
                adapter = AccentAdapter {
                    if (it.first != accent.first) {
                        SettingsManager.getInstance().setAccent(it)
                    }

                    this@show.dismiss()
                }

                post {
                    // Combine the width of the recyclerview with the width of an item in order
                    // to center the currently selected accent.
                    val childWidth = getChildAt(0).width / 2

                    (layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(
                            ACCENTS.indexOf(accent),
                            (width / 2) - childWidth
                        )
                }

                layoutManager = LinearLayoutManager(
                    requireContext()
                ).also { it.orientation = LinearLayoutManager.HORIZONTAL }
            }

            customView(view = recycler)

            view.invalidateDividers(showTop = false, showBottom = false)

            negativeButton(android.R.string.cancel)

            show()
        }
    }

    override fun onAccentUpdate(newAccent: Pair<Int, Int>) {
        findPreference<Preference>(getString(R.string.label_settings_accent))?.apply {
            summary = getAccentItemSummary(requireActivity(), accent)
        }
    }
}
