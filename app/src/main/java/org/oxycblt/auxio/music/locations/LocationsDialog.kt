/*
 * Copyright (c) 2024 Auxio Project
 * LocationsDialog.kt is part of Auxio.
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

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.R as MR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicLocationsBinding
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.showToast
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.mediastore.MediaStore
import org.oxycblt.musikr.fs.saf.SAF
import timber.log.Timber as L

@AndroidEntryPoint
class LocationsDialog : ViewBindingMaterialDialogFragment<DialogMusicLocationsBinding>() {

    private val includeLocationListener =
        object : LocationAdapter.Listener {
            override fun onRemoveLocation(location: Location) {
                includeLocationAdapter.remove(location as Location.Opened)
                updateSaveButtonState()
            }
        }

    private val excludeLocationListener =
        object : LocationAdapter.Listener {
            override fun onRemoveLocation(location: Location) {
                excludeLocationAdapter.remove(location as Location.Unopened)
                updateSaveButtonState()
            }
        }

    private val filterLocationListener =
        object : LocationAdapter.Listener {
            override fun onRemoveLocation(location: Location) {
                filterLocationAdapter.remove(location as Location.Unopened)
                updateSaveButtonState()
            }
        }

    private val includeLocationAdapter: LocationAdapter<Location.Opened> =
        LocationAdapter(includeLocationListener)
    private val excludeLocationAdapter: LocationAdapter<Location.Unopened> =
        LocationAdapter(excludeLocationListener)
    private val filterLocationAdapter: LocationAdapter<Location.Unopened> =
        LocationAdapter(filterLocationListener)
    private var openDocumentTreeLauncher: ActivityResultLauncher<Uri?>? = null
    private var storagePermissionLauncher: ActivityResultLauncher<String>? = null
    @Inject lateinit var musicSettings: MusicSettings

    private var isFilePickerMode = true
    private var isIncludeMode = true
    private var hasStoragePermission = false
    private var isExtrasExpanded = false
    private var pendingLocationCallback: ((Location.Unopened) -> Unit)? = null
    private var permissionGrantedInSession = false

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicLocationsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.set_locations)
            .setNegativeButton(R.string.lbl_cancel, null)
            .setPositiveButton(R.string.lbl_save) { _, _ -> saveChanges() }
    }

    override fun onBindingCreated(
        binding: DialogMusicLocationsBinding,
        savedInstanceState: Bundle?
    ) {
        openDocumentTreeLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                addDocumentTreeUriToDirs(uri)
            }

        storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                L.d("Storage permission granted: $isGranted")
                hasStoragePermission = isGranted
                if (isGranted && !permissionGrantedInSession) {
                    permissionGrantedInSession = true
                }
                updateModeUI(binding)
                updateSaveButtonState()
            }

        binding.locationsIncludeRecycler.apply {
            adapter = includeLocationAdapter
            itemAnimator = null
        }

        binding.locationsExcludeRecycler.apply {
            adapter = excludeLocationAdapter
            itemAnimator = null
        }

        binding.locationsFilterRecycler.apply {
            adapter = filterLocationAdapter
            itemAnimator = null
        }

        // Load initial state from MusicSettings
        loadInitialState(binding)

        // Set up string resources
        binding.locationsModeHeader.setText(R.string.set_load_from)
        binding.locationsModeExclude.setText(R.string.set_file_picker)
        binding.locationsModeInclude.setText(R.string.set_system_database)
        binding.locationsExcludeModeHeader.setText(R.string.set_filter_mode)
        binding.locationsExcludeModeExclude.setText(R.string.set_include)
        binding.locationsExcludeModeInclude.setText(R.string.set_exclude)
        binding.locationsIncludeListHeader.setText(R.string.set_folders_to_load)
        binding.locationsIncludeAdd.contentDescription = getString(R.string.desc_add_folder)
        binding.locationsExcludeAdd.contentDescription = getString(R.string.desc_add_folder)
        binding.locationsFilterAdd.contentDescription = getString(R.string.desc_add_folder)
        binding.locationsExtrasDropdown.setText(R.string.set_extra_settings)

        // Set up extras dropdown click listener
        binding.locationsExtrasDropdown.setOnClickListener {
            isExtrasExpanded = !isExtrasExpanded
            updateExtrasVisibility(binding)
        }

        // Set up mode toggle listener
        binding.folderModeGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.locations_mode_exclude -> {
                    isFilePickerMode = true
                    updateModeUI(binding)
                    updateSaveButtonState()
                }
                R.id.locations_mode_include -> {
                    isFilePickerMode = false
                    updateModeUI(binding)
                    updateSaveButtonState()
                }
            }
        }

        // Set up exclude/include mode toggle listener for System Database mode
        binding.locationsExcludeModeGroup.addOnButtonCheckedListener { group, checkedId, isChecked
            ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.locations_exclude_mode_exclude -> {
                    isIncludeMode = true
                    updateExcludeModeUI(binding)
                }
                R.id.locations_exclude_mode_include -> {
                    isIncludeMode = false
                    updateExcludeModeUI(binding)
                }
            }
        }

        // Set up add folder buttons
        binding.locationsIncludeAdd.setOnClickListener {
            pendingLocationCallback = { location ->
                location.open(requireContext())?.let { opened ->
                    includeLocationAdapter.add(opened)
                    updateSaveButtonState()
                }
            }
            onNewLocation()
        }
        binding.locationsExcludeAdd.setOnClickListener {
            pendingLocationCallback = { location ->
                excludeLocationAdapter.add(location)
                updateSaveButtonState()
            }
            onNewLocation()
        }
        binding.locationsFilterAdd.setOnClickListener {
            pendingLocationCallback = { location ->
                filterLocationAdapter.add(location)
                updateSaveButtonState()
            }
            onNewLocation()
        }

        // Set up grant permission card click
        binding.locationsPermsCard.setOnClickListener { requestStoragePermission() }

        // Initialize UI state
        updateModeUI(binding)
        updateExtrasVisibility(binding)
        updateSaveButtonState()
    }

    private fun loadInitialState(binding: DialogMusicLocationsBinding) {
        // Determine mode based on the locationMode setting
        isFilePickerMode = musicSettings.locationMode == LocationMode.SAF

        // Load data for the initial mode
        loadModeData(binding)

        // Set the initial toggle button selection
        if (isFilePickerMode) {
            binding.folderModeGroup.check(R.id.locations_mode_exclude)
        } else {
            binding.folderModeGroup.check(R.id.locations_mode_include)
        }

        // Check storage permission status
        hasStoragePermission = checkStoragePermission()
    }

    private fun loadModeData(binding: DialogMusicLocationsBinding) {
        // Load SAF data
        musicSettings.safQuery.let { query ->
            includeLocationAdapter.addAll(query.source)
            excludeLocationAdapter.addAll(query.exclude)
            binding.locationsWithHiddenSwitch.isChecked = query.withHidden
        }
        // Load MediaStore data
        musicSettings.mediaStoreQuery.let { query ->
            filterLocationAdapter.addAll(query.filtered)
            binding.locationsExcludeNonMusicSwitch.isChecked = query.excludeNonMusic

            isIncludeMode = query.mode == MediaStore.FilterMode.INCLUDE
            binding.locationsExcludeModeGroup.check(
                if (isIncludeMode) R.id.locations_exclude_mode_exclude
                else R.id.locations_exclude_mode_include)
        }
    }

    override fun onStart() {
        super.onStart()
        // Update save button state after dialog is shown
        updateSaveButtonState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO
    }

    override fun onDestroyBinding(binding: DialogMusicLocationsBinding) {
        super.onDestroyBinding(binding)
        openDocumentTreeLauncher = null
        storagePermissionLauncher = null
        binding.locationsIncludeRecycler.adapter = null
        binding.locationsExcludeRecycler.adapter = null
        binding.locationsFilterRecycler.adapter = null
    }

    private fun onNewLocation() {
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
            pendingLocationCallback = null
            return
        }
        val context = requireContext()
        val location = Location.Unopened.from(context, uri)

        if (location != null) {
            pendingLocationCallback?.invoke(location)
        } else {
            requireContext().showToast(R.string.err_bad_location)
        }
        pendingLocationCallback = null
    }

    private fun updateModeUI(binding: DialogMusicLocationsBinding) {
        with(binding) {
            if (isFilePickerMode) {
                // File Picker mode
                locationsModeDesc.setText(R.string.lng_file_picker)

                // Update permission section
                locationsPermsDesc.setText(R.string.set_grant_storage_anyway)
                locationsPermsSubtitle.setText(R.string.lng_grant_storage_anyway)

                // File Picker mode - no need to update switch text as it's set in XML
            } else {
                // System Database mode
                locationsModeDesc.setText(R.string.lng_system_database)

                // Update permission section
                locationsPermsDesc.setText(R.string.set_grant_storage)
                locationsPermsSubtitle.setText(R.string.lng_grant_storage_required)

                // Update exclude mode description based on selection
                updateExcludeModeUI(binding)

                // System Database mode - no need to update switch text as it's set in XML
            }

            // Update enabled state based on permission
            updatePermissionDependentUI(binding)
            // Update card colors based on mode and permission
            updatePermissionCardColors(binding)
            // Update permission card visibility
            updatePermissionCardVisibility(binding)
            // Update extras visibility based on current state
            updateExtrasVisibility(binding)
        }
    }

    private fun updateExcludeModeUI(binding: DialogMusicLocationsBinding) {
        with(binding) {
            if (isIncludeMode) {
                locationsExcludeModeDesc.setText(R.string.lng_include_folders)
            } else {
                locationsExcludeModeDesc.setText(R.string.lng_exclude_folders)
            }
        }
    }

    private fun updatePermissionDependentUI(binding: DialogMusicLocationsBinding) {
        with(binding) {
            // Only disable views in System Database mode when permission not granted
            // File Picker mode doesn't require storage permission
            val isEnabled = isFilePickerMode || hasStoragePermission

            locationsIncludeListHeader.isEnabled = isEnabled
            locationsIncludeAdd.isEnabled = isEnabled
            locationsIncludeRecycler.isEnabled = isEnabled

            locationsExcludeModeHeader.isEnabled = isEnabled
            locationsExcludeModeGroup.isEnabled = isEnabled
            locationsExcludeModeDesc.isEnabled = isEnabled
            locationsExcludeModeExclude.isEnabled = isEnabled
            locationsExcludeModeInclude.isEnabled = isEnabled

            locationsExcludeListHeader.isEnabled = isEnabled
            locationsExcludeAdd.isEnabled = isEnabled
            locationsExcludeRecycler.isEnabled = isEnabled

            locationsFilterListHeader.isEnabled = isEnabled
            locationsFilterAdd.isEnabled = isEnabled
            locationsFilterRecycler.isEnabled = isEnabled

            locationsWithHiddenTitle.isEnabled = isEnabled
            locationsWithHiddenDesc.isEnabled = isEnabled
            locationsWithHidden.isEnabled = isEnabled

            locationsExcludeNonMusicTitle.isEnabled = isEnabled
            locationsExcludeNonMusicDesc.isEnabled = isEnabled
            locationsExcludeNonMusic.isEnabled = isEnabled
        }
    }

    private fun updatePermissionCardColors(binding: DialogMusicLocationsBinding) {
        val context = requireContext()
        with(binding.locationsPermsCard) {
            if (isFilePickerMode) {
                // File Picker mode - use secondary colors
                setCardBackgroundColor(context.getAttrColorCompat(MR.attr.colorSecondaryContainer))
                binding.locationsPermsDesc.setTextColor(
                    context.getAttrColorCompat(MR.attr.colorOnSecondaryContainer))
                binding.locationsPermsSubtitle.setTextColor(
                    context.getAttrColorCompat(MR.attr.colorSecondary))
                binding.locationsPermsOpen.imageTintList =
                    context.getAttrColorCompat(MR.attr.colorOnSecondaryContainer)
            } else {
                // System Database mode - use error colors if no permission, secondary colors if
                // granted
                if (hasStoragePermission) {
                    // Has permission - use secondary colors
                    setCardBackgroundColor(
                        context.getAttrColorCompat(MR.attr.colorSecondaryContainer))
                    binding.locationsPermsDesc.setTextColor(
                        context.getAttrColorCompat(MR.attr.colorOnSecondaryContainer))
                    binding.locationsPermsSubtitle.setTextColor(
                        context.getAttrColorCompat(MR.attr.colorSecondary))
                    binding.locationsPermsOpen.imageTintList =
                        context.getAttrColorCompat(MR.attr.colorOnSecondaryContainer)
                } else {
                    setCardBackgroundColor(context.getAttrColorCompat(MR.attr.colorErrorContainer))
                    binding.locationsPermsDesc.setTextColor(
                        context.getAttrColorCompat(MR.attr.colorOnErrorContainer))
                    binding.locationsPermsSubtitle.setTextColor(
                        context.getAttrColorCompat(androidx.appcompat.R.attr.colorError))
                    binding.locationsPermsOpen.imageTintList =
                        context.getAttrColorCompat(MR.attr.colorOnErrorContainer)
                }
            }
        }
    }

    private fun updatePermissionCardVisibility(binding: DialogMusicLocationsBinding) {
        with(binding) {
            // Hide the permission card when permissions are granted
            locationsPermsCard.isVisible = !hasStoragePermission
        }
    }

    private fun updateExtrasVisibility(binding: DialogMusicLocationsBinding) {
        with(binding) {
            // Update dropdown icon rotation
            locationsExtrasDropdownIcon.rotation = if (isExtrasExpanded) 180f else 0f

            if (isFilePickerMode) {
                // File Picker mode - show include/exclude lists when expanded
                // Include section
                locationsIncludeListHeaderDivider.isVisible = true
                locationsIncludeListHeader.isVisible = true
                locationsIncludeAdd.isVisible = true
                locationsIncludeRecycler.isVisible = true

                // Show dividers and exclude section only when expanded
                locationsExcludeListHeader.isVisible = isExtrasExpanded
                locationsExcludeAdd.isVisible = isExtrasExpanded
                locationsExcludeRecycler.isVisible = isExtrasExpanded

                // Hide filter mode section completely
                locationsExcludeModeHeader.isVisible = false
                locationsExcludeModeGroup.isVisible = false
                locationsExcludeModeDesc.isVisible = false
                locationsFilterModeDivider.isVisible = false
                locationsFilterListHeader.isVisible = false
                locationsFilterAdd.isVisible = false
                locationsFilterRecycler.isVisible = false
                locationsExcludeListDivider.isVisible = false

                // Config section
                configDivider.isVisible = isExtrasExpanded
                locationsWithHiddenTitle.isVisible = isExtrasExpanded
                locationsWithHiddenDesc.isVisible = isExtrasExpanded
                locationsWithHidden.isVisible = isExtrasExpanded

                locationsExcludeNonMusicTitle.isVisible = false
                locationsExcludeNonMusicDesc.isVisible = false
                locationsExcludeNonMusic.isVisible = false
            } else {
                // System Database mode - show filter mode when expanded
                // Hide include section
                locationsIncludeListHeaderDivider.isVisible = false
                locationsIncludeListHeader.isVisible = false
                locationsIncludeAdd.isVisible = false
                locationsIncludeRecycler.isVisible = false

                // Hide exclude section (at bottom)
                locationsExcludeListDivider.isVisible = false
                locationsExcludeListHeader.isVisible = false
                locationsExcludeAdd.isVisible = false
                locationsExcludeRecycler.isVisible = false

                // Show filter mode section only when expanded
                locationsExcludeModeHeader.isVisible = isExtrasExpanded
                locationsExcludeModeGroup.isVisible = isExtrasExpanded
                locationsExcludeModeDesc.isVisible = isExtrasExpanded
                locationsFilterModeDivider.isVisible = isExtrasExpanded
                locationsFilterListHeader.isVisible = isExtrasExpanded
                locationsFilterAdd.isVisible = isExtrasExpanded
                locationsFilterRecycler.isVisible = isExtrasExpanded

                // Config section
                configDivider.isVisible = isExtrasExpanded
                locationsWithHiddenTitle.isVisible = false
                locationsWithHiddenDesc.isVisible = false
                locationsWithHidden.isVisible = false

                locationsExcludeNonMusicTitle.isVisible = isExtrasExpanded
                locationsExcludeNonMusicDesc.isVisible = isExtrasExpanded
                locationsExcludeNonMusic.isVisible = isExtrasExpanded
            }
        }
    }

    private fun saveChanges() {
        val binding = requireBinding()

        // Check if configuration has actually changed
        val currentMode = musicSettings.locationMode
        val modeChanged =
            currentMode != (if (isFilePickerMode) LocationMode.SAF else LocationMode.MEDIA_STORE)

        var configChanged = modeChanged

        if (isFilePickerMode) {
            // Check if SAF query changed
            val currentSafQuery = musicSettings.safQuery
            val newSafQuery =
                SAF.Query(
                    source = includeLocationAdapter.locations,
                    exclude = excludeLocationAdapter.locations,
                    withHidden = binding.locationsWithHiddenSwitch.isChecked)

            if (!modeChanged && currentMode == LocationMode.SAF) {
                configChanged = currentSafQuery != newSafQuery
            }

            // Save the new SAF query
            musicSettings.safQuery = newSafQuery
        } else {
            // Check if MediaStore query changed
            val currentMediaStoreQuery = musicSettings.mediaStoreQuery
            val filterMode =
                if (isIncludeMode) {
                    MediaStore.FilterMode.INCLUDE
                } else {
                    MediaStore.FilterMode.EXCLUDE
                }
            val newMediaStoreQuery =
                MediaStore.Query(
                    mode = filterMode,
                    filtered = filterLocationAdapter.locations,
                    excludeNonMusic = binding.locationsExcludeNonMusicSwitch.isChecked)

            if (!modeChanged && currentMode == LocationMode.MEDIA_STORE) {
                configChanged = currentMediaStoreQuery != newMediaStoreQuery
            }

            // Save the new MediaStore query
            musicSettings.mediaStoreQuery = newMediaStoreQuery
        }

        // Save the mode setting
        musicSettings.locationMode =
            if (isFilePickerMode) LocationMode.SAF else LocationMode.MEDIA_STORE

        // If no configuration changed but permission was granted in this session,
        // force a location update
        if (!configChanged && permissionGrantedInSession) {
            L.d("No config changes detected, but permission was granted - forcing location update")
            musicSettings.forceLocationUpdate()
        }
    }

    private fun checkStoragePermission(): Boolean {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        return ContextCompat.checkSelfPermission(requireContext(), permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        val launcher =
            requireNotNull(storagePermissionLauncher) {
                "Storage permission launcher was not available"
            }

        try {
            L.d("Requesting storage permission: $permission")
            launcher.launch(permission)
        } catch (e: Exception) {
            L.e("Failed to request storage permission")
            L.e(e.stackTraceToString())
            requireContext().showToast(R.string.err_no_app)
        }
    }

    private fun updateSaveButtonState() {
        val dialog = dialog as? AlertDialog ?: return

        val isEnabled =
            if (isFilePickerMode) {
                // File Picker mode: Enable save only if there's at least one folder
                includeLocationAdapter.locations.isNotEmpty()
            } else {
                // System mode: Enable save only if permission is granted
                hasStoragePermission
            }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isEnabled
    }
}
