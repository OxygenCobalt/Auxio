/*
 * Copyright (c) 2021 Auxio Project
 * MainActivity.kt is part of Auxio.
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
 
package org.oxycblt.auxio

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.music.LibraryOrganizer
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.ui.UISettings
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbDacPreferences
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * Auxio's single [AppCompatActivity].
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Add error screens
 * TODO: Custom language support
 * TODO: Use proper material attributes (Not the weird dimen attributes I currently have)
 * TODO: Migrate to material animation system
 * TODO: Unit testing
 * TODO: Fix UID naming
 * TODO: Leverage FlexibleListAdapter more in dialogs (Disable item anims)
 * TODO: Improve multi-threading support in shared objects
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by viewModels()
    private val musicModel: MusicViewModel by viewModels()
    @Inject lateinit var uiSettings: UISettings
    @Inject lateinit var usbAudioManager: UsbAudioManager
    @Inject lateinit var usbSettings: UsbDacPreferences

    // Claims hardware/Bluetooth media-key routing for as long as this Activity is the foreground
    // window -- Activity.setMediaController's own doc comment says exactly this ("Media key ...
    // events which are received while the Activity is in the foreground will be forwarded to the
    // controller"), and PhoneWindow's key dispatch (frameworks/base) special-cases this ahead of
    // MediaSessionService's normal session-priority routing entirely, which otherwise only
    // reconsiders which app it targets when a *new* local audio player becomes active -- not
    // just because this app came to the foreground. Confirmed on real hardware: without this,
    // bringing Auxio to the foreground while paused (or while another app still holds that
    // routing) left physical media keys going to that other app. AuxioService is a
    // MediaBrowserServiceCompat, so a MediaBrowserCompat connection is the only way to reach its
    // live MediaSessionCompat.Token from here.
    private lateinit var mediaBrowser: MediaBrowserCompat
    private val mediaBrowserConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                MediaControllerCompat.setMediaController(
                    this@MainActivity, MediaControllerCompat(this@MainActivity, mediaBrowser.sessionToken))
            }
        }

    // Only MainActivity (a long-lived, single Activity) can host this launcher and reliably get
    // a result back -- the menu dialog that actually requests a deletion is a bottom sheet that
    // closes itself the instant any option is clicked (see MenuDialogFragment.onClick), so by the
    // time the system's delete-confirmation dialog this launches returns a result, that fragment
    // is long gone.
    private val deleteRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            musicModel.onDeleteRequestResult(result.resultCode == RESULT_OK)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()
        // Inflate the views after setting up the theme so that the theme attributes are applied.
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge(binding.root)

        mediaBrowser =
            MediaBrowserCompat(
                this, ComponentName(this, AuxioService::class.java), mediaBrowserConnectionCallback, null)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    musicModel.deleteRequest.flow.collect { intentSender ->
                        if (intentSender == null) return@collect
                        musicModel.deleteRequest.consume()
                        deleteRequestLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    }
                }
                launch {
                    musicModel.songMessage.flow.collect { message ->
                        if (message == null) return@collect
                        musicModel.songMessage.consume()
                        showToast(message.stringRes)
                    }
                }
                launch {
                    musicModel.deleteConfirmation.flow.collect { songs ->
                        if (songs == null) return@collect
                        musicModel.deleteConfirmation.consume()
                        showDeleteConfirmation(songs)
                    }
                }
                launch {
                    musicModel.organizePreview.flow.collect { plan ->
                        if (plan == null) return@collect
                        musicModel.organizePreview.consume()
                        showOrganizePreview(plan)
                    }
                }
            }
        }

        L.d("Activity created")
    }

    override fun onResume() {
        super.onResume()

        startService(
            Intent(this, AuxioService::class.java)
                .setAction(AuxioService.ACTION_START)
                .putExtra(AuxioService.INTENT_KEY_START_ID, IntegerTable.START_ID_ACTIVITY)
        )

        if (!startIntentAction(intent)) {
            // No intent action to do, just restore the previously saved state.
            playbackModel.playDeferred(DeferredPlayback.RestoreState(false))
        }

        // Permission prompts need a foregrounded Activity, so ask here rather than in
        // UsbAudioManager itself for any device we've detected but don't yet have access to.
        // Direct USB output is opt-in (off by default), so don't prompt unless the user has
        // actually turned it on in settings.
        if (usbSettings.enabled) {
            usbAudioManager.devices.value
                .filterNot { it.hasPermission }
                .forEach { usbAudioManager.requestPermission(it.device) }
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        // Give up the foreground-window media-key claim as soon as this Activity itself stops
        // being the foreground window, rather than leaving a stale controller registered -- see
        // mediaBrowserConnectionCallback's doc comment for why this exists at all.
        MediaControllerCompat.setMediaController(this, null)
        mediaBrowser.disconnect()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        startIntentAction(intent)
    }

    /** See [MusicViewModel.deleteConfirmation]'s doc comment for why this always runs. */
    private fun showDeleteConfirmation(songs: List<Song>) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.lbl_confirm_delete_song)
            .setMessage(
                getString(R.string.fmt_deletion_info, getPlural(R.plurals.fmt_song_count, songs.size)))
            .setPositiveButton(R.string.lbl_delete) { _, _ -> musicModel.confirmDeleteSongs(songs) }
            .setNegativeButton(R.string.lbl_cancel, null)
            .show()
    }

    /** See [MusicViewModel.organizePreview]'s doc comment for why this always runs. */
    private fun showOrganizePreview(plan: LibraryOrganizer.Plan) {
        if (plan.moves.isEmpty()) {
            showToast(R.string.err_organize_failed)
            return
        }
        val body = buildString {
            append(getString(R.string.fmt_organize_info, getPlural(R.plurals.fmt_song_count, plan.moves.size)))
            if (plan.skipped.isNotEmpty()) {
                append("\n\n")
                append(getString(R.string.fmt_organize_skipped, plan.skipped.size))
            }
            append("\n\n")
            plan.moves.take(MAX_ORGANIZE_PREVIEW_LINES).forEach { move ->
                append(getString(R.string.fmt_organize_preview_entry, move.oldDisplayPath, move.newDisplayPath))
                append("\n\n")
            }
            if (plan.moves.size > MAX_ORGANIZE_PREVIEW_LINES) {
                append(getString(R.string.fmt_organize_preview_more, plan.moves.size - MAX_ORGANIZE_PREVIEW_LINES))
            }
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.lbl_confirm_organize)
            .setMessage(body.trim())
            .setPositiveButton(R.string.lbl_organize) { _, _ ->
                musicModel.confirmOrganizeLibrary(plan.moves)
            }
            .setNegativeButton(R.string.lbl_cancel, null)
            .show()
    }

    private fun setupTheme() {
        // Apply the theme configuration.
        AppCompatDelegate.setDefaultNightMode(uiSettings.theme)
        // Apply the color scheme. The black theme requires it's own set of themes since
        // it's not possible to modify the themes at run-time.
        if (isNight && uiSettings.useBlackTheme) {
            L.d("Applying black theme [accent ${uiSettings.accent}]")
            setTheme(uiSettings.accent.blackTheme)
        } else {
            L.d("Applying normal theme [accent ${uiSettings.accent}]")
            setTheme(uiSettings.accent.theme)
        }
    }

    private fun setupEdgeToEdge(contentView: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        contentView.setOnApplyWindowInsetsListener { view, insets ->
            // Automatically inset the view to the left/right, as component support for
            // these insets are highly lacking.
            val bars = insets.systemBarInsetsCompat
            view.updatePadding(left = bars.left, right = bars.right)
            insets
        }
    }

    /**
     * Transform an [Intent] given to [MainActivity] into a [DeferredPlayback] that can be used in
     * the playback system.
     *
     * @param intent The (new) [Intent] given to this [MainActivity], or null if there is no intent.
     * @return true If the analogous [DeferredPlayback] to the given [Intent] was started, false
     *   otherwise.
     */
    private fun startIntentAction(intent: Intent?): Boolean {
        if (intent == null) {
            // Nothing to do.
            L.d("No intent to handle")
            return false
        }

        if (intent.getBooleanExtra(KEY_INTENT_USED, false)) {
            // Don't commit the action, but also return that the intent was applied.
            // This is because onStart can run multiple times, and thus we really don't
            // want to return false and override the original delayed action with a
            // RestoreState action.
            L.d("Already used this intent")
            return true
        }
        intent.putExtra(KEY_INTENT_USED, true)

        val action =
            when (intent.action) {
                Intent.ACTION_VIEW -> DeferredPlayback.Open(intent.data ?: return false)
                Auxio.INTENT_KEY_SHORTCUT_SHUFFLE -> DeferredPlayback.ShuffleAll
                else -> {
                    L.w("Unexpected intent ${intent.action}")
                    return false
                }
            }
        L.d("Translated intent to $action")
        playbackModel.playDeferred(action)
        return true
    }

    private companion object {
        const val KEY_INTENT_USED = BuildConfig.APPLICATION_ID + ".key.FILE_INTENT_USED"
        const val MAX_ORGANIZE_PREVIEW_LINES = 50
    }
}
