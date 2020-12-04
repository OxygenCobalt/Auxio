@file:Suppress("unused")

package org.oxycblt.auxio.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.adapters.AccentAdapter
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.getDetailedAccentSummary

class SettingsListFragment : PreferenceFragmentCompat() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- PREFERENCE ITEM SETUP ---

        preferenceScreen.children.forEach {
            recursivelyHandleChildren(it)
        }

        Log.d(this::class.simpleName, "Fragment created.")
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_main, rootKey)
    }

    private fun recursivelyHandleChildren(pref: Preference) {
        if (pref is PreferenceCategory) {
            pref.children.forEach {
                recursivelyHandleChildren(it)
            }
        } else {
            handlePreference(pref)
        }
    }

    private fun handlePreference(it: Preference) {
        it.apply {
            when (it.key) {
                SettingsManager.Keys.KEY_THEME -> {
                    setIcon(AppCompatDelegate.getDefaultNightMode().toThemeIcon())

                    onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
                        AppCompatDelegate.setDefaultNightMode((value as String).toThemeInt())

                        setIcon(AppCompatDelegate.getDefaultNightMode().toThemeIcon())

                        true
                    }
                }

                SettingsManager.Keys.KEY_ACCENT -> {
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {
                        showAccentDialog()
                        true
                    }

                    summary = getDetailedAccentSummary(requireActivity(), accent)
                }

                SettingsManager.Keys.KEY_EDGE_TO_EDGE -> {
                    onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                        requireActivity().recreate()

                        true
                    }
                }
            }
        }
    }

    private fun showAccentDialog() {
        MaterialDialog(requireActivity()).show {
            title(R.string.setting_accent)

            // Roll my own RecyclerView since [To no surprise whatsoever] Material Dialogs
            // has a bug where ugly dividers will show with the RecyclerView even if you disable them.
            // This is why I hate using third party libraries.
            val recycler = RecyclerView(requireContext()).apply {
                adapter = AccentAdapter {
                    if (it.first != accent.first) {
                        SettingsManager.getInstance().accent = it

                        requireActivity().recreate()
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
}
