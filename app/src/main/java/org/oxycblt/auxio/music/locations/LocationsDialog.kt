/*
 * Copyright (c) 2024 Auxio Project
 * BaseLocationsDialog.kt is part of Auxio.
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

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.ConcatAdapter
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicLocationsBinding
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.showToast
import org.oxycblt.musikr.fs.Location
import timber.log.Timber as L

abstract class LocationsDialog<T : Location> :
    ViewBindingMaterialDialogFragment<DialogMusicLocationsBinding>(),
    LocationAdapter.Listener<T>,
    NewLocationFooterAdapter.Listener {
    protected abstract val locationAdapter: LocationAdapter<T>
    private val locationFooterAdapter = NewLocationFooterAdapter(this)
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>? = null
    abstract val musicSettings: MusicSettings

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicLocationsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(getDialogTitle())
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                val newDirs = locationAdapter.locations
                saveLocations(newDirs)
            }
    }

    override fun onBindingCreated(
        binding: DialogMusicLocationsBinding,
        savedInstanceState: Bundle?
    ) {
        openDocumentTreeLauncher =
            registerForActivityResult(
                ActivityResultContracts.OpenDocumentTree(), ::addDocumentTreeUriToDirs)

        binding.locationsRecycler.apply {
            adapter = ConcatAdapter(locationAdapter, locationFooterAdapter)
            itemAnimator = null
        }

        val locations =
            savedInstanceState?.getStringArrayList(getPendingLocationsKey())?.mapNotNull {
                convertUriToLocation(it.toUri())
            } ?: getCurrentLocations()

        locationAdapter.addAll(locations)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(
            getPendingLocationsKey(), ArrayList(locationAdapter.locations.map { it.uri.toString() }))
    }

    override fun onDestroyBinding(binding: DialogMusicLocationsBinding) {
        super.onDestroyBinding(binding)
        openDocumentTreeLauncher = null
        binding.locationsRecycler.adapter = null
    }

    override fun onNewLocation() {
        L.d("Opening launcher")
        val launcher =
            requireNotNull(openDocumentTreeLauncher) { "Document tree launcher was not available" }

        try {
            launcher.launch(null)
        } catch (e: ActivityNotFoundException) {
            requireContext().showToast(R.string.err_no_app)
        }
    }

    private fun addDocumentTreeUriToDirs(uri: Uri?) {
        if (uri == null) {
            L.d("No URI given (user closed the dialog)")
            return
        }
        val context = requireContext()
        val location = createLocationFromUri(context, uri)

        if (location != null) {
            locationAdapter.add(location)
        } else {
            requireContext().showToast(R.string.err_bad_location)
        }
    }

    override fun onRemoveLocation(location: T) {
        locationAdapter.remove(location)
    }

    protected abstract fun getDialogTitle(): Int
    protected abstract fun getCurrentLocations(): List<T>
    protected abstract fun saveLocations(locations: List<T>)
    protected abstract fun getPendingLocationsKey(): String
    protected abstract fun convertUriToLocation(uri: Uri): T?
    protected abstract fun createLocationFromUri(context: android.content.Context, uri: Uri): T?
}