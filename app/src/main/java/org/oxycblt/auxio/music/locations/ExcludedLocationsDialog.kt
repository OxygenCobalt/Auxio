/*
 * Copyright (c) 2021 Auxio Project
 * ExcludedLocationsDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.locations

import android.content.Context
import android.net.Uri
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.musikr.fs.Location
import javax.inject.Inject

/**
 * Dialog that manages the excluded locations setting.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class ExcludedLocationsDialog : LocationsDialog<Location.Unopened>() {
    override val locationAdapter = ExcludedLocationAdapter(this)

    @Inject
    override lateinit var musicSettings: MusicSettings

    override fun getDialogTitle(): Int = R.string.set_excluded_locations

    override fun getCurrentLocations(): List<Location.Unopened> = musicSettings.excludedLocations

    override fun saveLocations(locations: List<Location.Unopened>) {
        musicSettings.excludedLocations = locations
    }

    override fun getPendingLocationsKey(): String = KEY_PENDING_EXCLUDED_LOCATIONS

    override fun convertUriToLocation(uri: Uri): Location.Unopened? {
        return Location.Unopened.from(requireContext(), uri)
    }

    override fun createLocationFromUri(context: Context, uri: Uri): Location.Unopened? {
        return Location.Unopened.from(context, uri)
    }

    private companion object {
        const val KEY_PENDING_EXCLUDED_LOCATIONS = BuildConfig.APPLICATION_ID + ".key.PENDING_EXCLUDED_LOCATIONS"
    }
}