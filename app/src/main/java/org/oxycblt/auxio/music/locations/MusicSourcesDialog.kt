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
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicLocationsBinding
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.musikr.fs.DocumentPathFactory
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.showToast
import timber.log.Timber as L

/**
 * Dialog that manages the music locations setting.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class MusicSourcesDialog :
    ViewBindingMaterialDialogFragment<DialogMusicLocationsBinding>(), LocationAdapter.Listener {
    private val locationAdapter = LocationAdapter(this)
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>? = null
    @Inject lateinit var documentPathFactory: DocumentPathFactory
    @Inject lateinit var musicSettings: MusicSettings

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicLocationsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_locations)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ ->
                val newDirs = locationAdapter.locations.map { it.uri }
                if (musicSettings.musicLocations != newDirs) {
                    L.d("Committing changes")
                    musicSettings.musicLocations = newDirs
                }
            }
    }

    override fun onBindingCreated(
        binding: DialogMusicLocationsBinding,
        savedInstanceState: Bundle?
    ) {
        openDocumentTreeLauncher =
            registerForActivityResult(
                object : ActivityResultContracts.OpenDocumentTree() {
                    override fun createIntent(context: Context, input: Uri?): Intent {
                        return super.createIntent(context, input).apply {
                            flags =
                                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                        }
                    }
                },
                ::addDocumentTreeUriToDirs)

        binding.locationsAdd.apply {
            ViewCompat.setTooltipText(this, contentDescription)
            setOnClickListener {
                L.d("Opening launcher")
                val launcher =
                    requireNotNull(openDocumentTreeLauncher) {
                        "Document tree launcher was not available"
                    }

                try {
                    launcher.launch(null)
                } catch (e: ActivityNotFoundException) {
                    // User doesn't have a capable file manager.
                    requireContext().showToast(R.string.err_no_app)
                }
            }
        }

        binding.locationsRecycler.apply {
            adapter = locationAdapter
            itemAnimator = null
        }

        val locationUris =
            savedInstanceState?.getStringArrayList(KEY_PENDING_LOCATIONS)?.map { Uri.parse(it) }
                ?: musicSettings.musicLocations
        val locations =
            locationUris.mapNotNull {
                MusicLocation(
                    it, documentPathFactory.unpackDocumentTreeUri(it) ?: return@mapNotNull null)
            }

        locationAdapter.addAll(locations)
        requireBinding().locationsEmpty.isVisible = locations.isEmpty()
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

    override fun onRemoveLocation(location: MusicLocation) {
        locationAdapter.remove(location)
        requireBinding().locationsEmpty.isVisible = locationAdapter.locations.isEmpty()
    }

    @Inject lateinit var contentResolver: ContentResolver

    /**
     * Add a Document Tree [Uri] chosen by the user to the current [MusicLocation]s.
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
        val takeFlags: Int =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)

        val path = documentPathFactory.unpackDocumentTreeUri(uri)

        if (path != null) {
            locationAdapter.add(MusicLocation(uri, path))
            requireBinding().locationsEmpty.isVisible = false
        } else {
            requireContext().showToast(R.string.err_bad_location)
        }
    }

    private companion object {
        const val KEY_PENDING_LOCATIONS = BuildConfig.APPLICATION_ID + ".key.PENDING_LOCATIONS"
    }
}
