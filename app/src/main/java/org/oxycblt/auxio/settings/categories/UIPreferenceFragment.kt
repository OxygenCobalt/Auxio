/*
 * Copyright (c) 2023 Auxio Project
 * UIPreferenceFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.settings.categories

import android.content.Context
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.headunit.compat.HeadUnitCompatManager
import org.oxycblt.auxio.headunit.compat.NativePrivateIntegrationStatus
import org.oxycblt.auxio.settings.BasePreferenceFragment
import org.oxycblt.auxio.settings.ui.WrappedDialogPreference
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.navigateSafe
import timber.log.Timber as L

/**
 * Display preferences.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class UIPreferenceFragment : BasePreferenceFragment(R.xml.preferences_ui) {
    @Inject lateinit var uiSettings: UISettings

    override fun onOpenDialogPreference(preference: WrappedDialogPreference) {
        if (preference.key == getString(R.string.set_key_accent)) {
            L.d("Navigating to accent dialog")
            findNavController().navigateSafe(UIPreferenceFragmentDirections.accentSettings())
        }
    }

    override fun onSetupPreference(preference: Preference) {
        when (preference.key) {
            getString(R.string.set_key_theme) -> {
                L.d("Configuring theme setting")
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, value ->
                        L.d("Theme changed, recreating")
                        requireActivity().recreate()
                        true
                    }
            }
            getString(R.string.set_key_accent) -> {
                L.d("Configuring accent setting")
                preference.summary = getString(uiSettings.accent.name)
            }
            getString(R.string.set_key_black_theme) -> {
                L.d("Configuring black theme setting")
                preference.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, _ ->
                        val activity = requireActivity()
                        if (activity.isNight) {
                            L.d("Black theme changed in night mode, recreating")
                            activity.recreate()
                        }

                        true
                    }
            }
            getString(R.string.set_head_unit_compat_status) -> {
                val compatStatus =
                    HeadUnitCompatManager.currentStatus(
                        compatModeEnabled = uiSettings.headUnitLandscapeMode,
                        widgetMetadataPublishable = uiSettings.showHeadUnitAlbumArt,
                        shortcutCompatReady = uiSettings.showHeadUnitDashboardQuickAccess,
                        sessionCompatReady = uiSettings.headUnitLandscapeMode,
                    )
                val nativeStatusSummary =
                    when (compatStatus.nativePrivateIntegrationStatus) {
                        NativePrivateIntegrationStatus.NOT_ENABLED_REQUIRES_VALIDATION ->
                            getString(
                                R.string.set_head_unit_compat_native_not_enabled_requires_validation
                            )
                    }
                preference.summary =
                    getString(
                        R.string.set_head_unit_compat_status_summary,
                        statusSummary(compatStatus.compatModeEnabled),
                        statusSummary(compatStatus.androidFallbackActive),
                        statusSummary(compatStatus.widgetMetadataPublishable),
                        statusSummary(compatStatus.shortcutCompatReady),
                        statusSummary(compatStatus.sessionCompatReady),
                        nativeStatusSummary,
                    ) + "\n" + uiSettings.headUnitCompatStatusSummary
            }
            KEY_CAR_OVERLAY_ENABLED -> setupCarOverlayEnabled(preference)
            KEY_CAR_OVERLAY_RESET_POSITION -> setupCarOverlayReset(preference)
        }
    }

    /**
     * Called when the car overlay enabled preference is found. Uses reflection to wire the
     * overlay settings facade since the class only exists in the topwayTwMusic variant.
     */
    private fun setupCarOverlayEnabled(preference: Preference) {
        if (!BuildConfig.TOPWAY_TWMUSIC_FLAVOR) return
        try {
            val settingsClass = Class.forName(
                "org.oxycblt.auxio.car.overlay.CarOverlaySettings"
            )
            val instance = settingsClass.getDeclaredField("INSTANCE").get(null)
            val setEnabledMethod = settingsClass.getMethod(
                "setEnabled", Context::class.java, Boolean::class.javaPrimitiveType
            )
            preference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    setEnabledMethod.invoke(instance, requireContext(), newValue as Boolean)
                    true
                }
        } catch (e: Exception) {
            L.w("Car overlay settings class not available: ${e.message}")
        }
    }

    private fun setupCarOverlayReset(preference: Preference) {
        if (!BuildConfig.TOPWAY_TWMUSIC_FLAVOR) return
        try {
            val settingsClass = Class.forName(
                "org.oxycblt.auxio.car.overlay.CarOverlaySettings"
            )
            val instance = settingsClass.getDeclaredField("INSTANCE").get(null)
            val resetMethod = settingsClass.getMethod(
                "resetPosition", Context::class.java
            )
            preference.setOnPreferenceClickListener {
                resetMethod.invoke(instance, requireContext())
                true
            }
        } catch (e: Exception) {
            L.w("Car overlay settings class not available: ${e.message}")
        }
    }

    private fun statusSummary(status: Boolean): String =
        if (status) getString(R.string.lbl_enabled) else getString(R.string.lbl_disabled)

    private companion object {
        const val KEY_CAR_OVERLAY_ENABLED = "car_overlay_enabled"
        const val KEY_CAR_OVERLAY_RESET_POSITION = "car_overlay_reset_position"
    }
}
