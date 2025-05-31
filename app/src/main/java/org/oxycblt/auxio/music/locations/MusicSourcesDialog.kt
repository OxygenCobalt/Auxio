/*
 * Copyright (c) 2021 Auxio Project
 * MusicSourcesDialog.kt is part of Auxio.
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicLocationsBinding
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.showToast
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.OpenedLocation
import timber.log.Timber as L

/**
 * Dialog that manages the music locations setting.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class MusicSourcesDialog :
    ViewBindingMaterialDialogFragment<DialogMusicLocationsBinding>(),
    LocationAdapter.Listener,
    NewLocationFooterAdapter.Listener {
    private val locationAdapter = LocationAdapter(this)
    private val locationFooterAdapter = NewLocationFooterAdapter(this)
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>? = null
    @Inject lateinit var musicSettings: MusicSettings

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicLocationsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_locations)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                val newDirs = locationAdapter.locations
                musicSettings.musicLocations = newDirs
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
            savedInstanceState?.getStringArrayList(KEY_PENDING_LOCATIONS)?.mapNotNull {
                val context = requireContext()
                Location.from(context, it.toUri())?.open(context)
            } ?: musicSettings.musicLocations

        locationAdapter.addAll(locations)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(
            KEY_PENDING_LOCATIONS, ArrayList(locationAdapter.locations.map { it.uri.toString() }))
    }

    override fun onDestroyBinding(binding: DialogMusicLocationsBinding) {
        super.onDestroyBinding(binding)
        openDocumentTreeLauncher = null
        binding.locationsRecycler.adapter = null
    }

    override fun onRemoveLocation(location: OpenedLocation) {
        locationAdapter.remove(location)
    }

    override fun onNewLocation() {
        L.d("Opening launcher")
        val launcher =
            requireNotNull(openDocumentTreeLauncher) { "Document tree launcher was not available" }

        try {
            launcher.launch(null)
        } catch (e: ActivityNotFoundException) {
            // User doesn't have a capable file manager.
            requireContext().showToast(R.string.err_no_app)
        }
    }

    /**
     * Add a Document Tree [Uri] chosen by the user to the current [OpenedLocation]s.
     *
     * @param uri The document tree [Uri] to add, chosen by the user. Will do nothing if the [Uri]
     *   is null or not valid.
     */
    private fun addDocumentTreeUriToDirs(uri: Uri?) {
        if (uri == null) {
            // A null URI means that the user left the file picker without picking a locationectory
            L.d("No URI given (user closed the dialog)")
            return
        }
        val context = requireContext()
        val location = Location.from(context, uri)?.open(context)

        if (location != null) {
            locationAdapter.add(location)
        } else {
            requireContext().showToast(R.string.err_bad_location)
        }
    }

    private companion object {
        const val KEY_PENDING_LOCATIONS = BuildConfig.APPLICATION_ID + ".key.PENDING_LOCATIONS"
    }
}
