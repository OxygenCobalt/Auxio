/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackPanelFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.media.audiofx.AudioEffect
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentPlaybackPanelBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.music.resolveShort
import org.oxycblt.auxio.playback.lyrics.LrcParser
import org.oxycblt.auxio.playback.lyrics.Lyrics
import org.oxycblt.auxio.playback.lyrics.LyricsAdapter
import org.oxycblt.auxio.playback.lyrics.currentLyricsLineIndex
import org.oxycblt.auxio.playback.queue.QueueViewModel
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.ui.StyledSeekBar
import org.oxycblt.auxio.playback.ui.stepper.Direction
import org.oxycblt.auxio.playback.ui.stepper.StepperOverlay
import org.oxycblt.auxio.playback.ui.swiper.CarouselTransformer
import org.oxycblt.auxio.playback.ui.swiper.CoverPagerAdapter
import org.oxycblt.auxio.playback.ui.swiper.UserAwarePagerCallback
import org.oxycblt.auxio.ui.ViewBindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.ostrichbat2k.usbdac.control.UsbControlState
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbDacPreferences
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.dampen
import org.oxycblt.auxio.util.recycler
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.smoothScrollByPageTo
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * A [ViewBindingFragment] more information about the currently playing song, alongside all
 * available controls.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Improve flickering situation on play button
 */
@AndroidEntryPoint
class PlaybackPanelFragment :
    ViewBindingFragment<FragmentPlaybackPanelBinding>(),
    Toolbar.OnMenuItemClickListener,
    StyledSeekBar.Listener,
    StepperOverlay.Listener {
    @Inject lateinit var usbAudioManager: UsbAudioManager
    @Inject lateinit var usbSettings: UsbDacPreferences
    private val coverPagerAdapter = CoverPagerAdapter(this)
    private val lyricsOverlayAdapter = LyricsAdapter()
    private var lyricsOverlayLayoutManager: LyricsScrollLayoutManager? = null
    private var lyricsOverlayTapListener: RecyclerView.SimpleOnItemTouchListener? = null
    private var lyrics: Lyrics? = null
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val listModel: ListViewModel by activityViewModels()
    private val queueModel: QueueViewModel by viewModels()
    private var equalizerLauncher: ActivityResultLauncher<Intent>? = null
    private var userAwarePagerCallback: UserAwarePagerCallback? = null

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentPlaybackPanelBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentPlaybackPanelBinding,
        savedInstanceState: Bundle?,
    ) {
        super.onBindingCreated(binding, savedInstanceState)

        // AudioEffect expects you to use startActivityForResult with the panel intent. There is no
        // contract analogue for this intent, so the generic contract is used instead.
        equalizerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // Nothing to do
            }

        // --- UI SETUP ---
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.systemBarInsetsCompat
            view.updatePadding(bottom = bars.bottom)
            insets
        }

        binding.playbackToolbar.apply {
            setNavigationOnClickListener { playbackModel.openMain() }
            setOnMenuItemClickListener(this@PlaybackPanelFragment)
        }

        binding.playbackPager?.apply {
            // intentional LTR override since thepager is a chronological element
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            adapter = coverPagerAdapter
            userAwarePagerCallback =
                UserAwarePagerCallback(this) {
                        // Posting the queue goto command prevents the seekbar pos from desyncing
                        // from the song's duration, which creates a visual flicker in the seekbar.
                        post { queueModel.goto(it) }
                    }
                    .also { it.attach() }
            setPageTransformer(CarouselTransformer())
            recycler().apply {
                // Make it possible to collapse the bottom sheet from the ViewPager's touch area.
                isNestedScrollingEnabled = false
                // Visual effect consistency
                // TODO: Custom overscroll?
                overScrollMode = View.OVER_SCROLL_NEVER
            }
            // Make it easier to collapse the bottom sheet
            dampen()
            offscreenPageLimit = 1
        }

        // Set up the lyrics-over-blurred-art overlay, toggled by tapping the cover (see
        // onSingleTap). It's a sibling clipped to the same rounded shape the cover pages
        // themselves use, built the same way StepperOverlay clips its own shape.
        binding.lyricsOverlay.apply {
            clipToOutline = true
            background =
                MaterialShapeDrawable().apply {
                    shapeAppearanceModel =
                        ShapeAppearanceModel.builder(
                                context,
                                com.google.android.material.R.style
                                    .ShapeAppearance_Material3_Corner_ExtraLarge,
                                -1)
                            .build()
                    fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
                }
            setOnClickListener { toggleLyricsOverlay() }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                binding.lyricsOverlayBackdrop.setRenderEffect(
                    RenderEffect.createBlurEffect(60f, 60f, Shader.TileMode.CLAMP))
            }
        }
        binding.lyricsOverlayRecycler.apply {
            adapter = lyricsOverlayAdapter
            // For synced lyrics, scrolling here is purely programmatic (auto-advance to the
            // current line) -- user-driven vertical scroll is disabled so a single tap is the
            // only gesture, without affecting scrollToPositionWithOffset calls. Unsynced lyrics
            // have no auto-scroll to rely on, so scrolling stays enabled there (toggled in
            // updateLyricsOverlayContent) or the tail of a long lyric would be unreachable.
            layoutManager = LyricsScrollLayoutManager(context).also { lyricsOverlayLayoutManager = it }
            // A plain OnClickListener on a RecyclerView doesn't reliably fire for taps landing
            // within its scrollable content -- its own touch handling can swallow the gesture
            // before performClick() would trigger. A GestureDetector-based OnItemTouchListener
            // that doesn't intercept is the reliable way to catch a plain tap anywhere in it.
            val tapListener =
                object : RecyclerView.SimpleOnItemTouchListener() {
                    private val detector =
                        GestureDetector(
                            context,
                            object : GestureDetector.SimpleOnGestureListener() {
                                override fun onSingleTapUp(e: MotionEvent): Boolean {
                                    toggleLyricsOverlay()
                                    return true
                                }
                            })

                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        detector.onTouchEvent(e)
                        return false
                    }
                }
            lyricsOverlayTapListener = tapListener
            addOnItemTouchListener(tapListener)
        }

        // Set up fast seek overlay
        binding.playbackSong.apply {
            isSelected = true
            setOnClickListener { navigateToCurrentSong() }
        }
        binding.playbackArtist.apply {
            isSelected = true
            setOnClickListener { navigateToCurrentArtist() }
        }
        binding.playbackAlbum?.apply {
            isSelected = true
            setOnClickListener { navigateToCurrentAlbum() }
        }

        binding.playbackSeekBar?.listener = this

        // Set up actions
        // TODO: Add better playback button accessibility
        binding.playbackRepeat.setOnClickListener { playbackModel.toggleRepeatMode() }
        binding.playbackSkipPrev.setOnClickListener { playbackModel.prev() }
        binding.playbackPlayPause.apply {
            @SuppressLint("RestrictedApi")
            setCornerSpringForce(
                SpringForce().apply {
                    stiffness = 700f
                    dampingRatio = 0.9f
                }
            )
            setOnClickListener { playbackModel.togglePlaying() }
        }
        binding.playbackSkipNext.setOnClickListener { playbackModel.next() }
        binding.playbackShuffle.setOnClickListener { playbackModel.toggleShuffled() }
        binding.playbackMore?.setOnClickListener {
            playbackModel.song.value?.let {
                listModel.openMenu(R.menu.playback_song, it, PlaySong.ByItself)
            }
        }

        // --- VIEWMODEL SETUP --
        collectImmediately(playbackModel.song, ::updateSong)
        collectImmediately(playbackModel.parent, ::updateParent)
        collectImmediately(playbackModel.positionDs, ::updatePosition)
        collectImmediately(playbackModel.repeatMode, ::updateRepeat)
        collectImmediately(playbackModel.isPlaying, ::updatePlaying)
        collectImmediately(playbackModel.isShuffled, ::updateShuffled)
        collectImmediately(playbackModel.pagerQueue, ::updatePager)
        collectImmediately(usbAudioManager.playbackRoute, usbAudioManager.controlState, ::updateUsbStatus)
        collectImmediately(usbAudioManager.abxStats) { _ ->
            updateUsbStatus(usbAudioManager.playbackRoute.value, usbAudioManager.controlState.value)
        }
        collectImmediately(usbAudioManager.abxPendingGuess, ::updateAbxPendingGuess)
        collectImmediately(usbAudioManager.blockPlayback, ::updateBlockPlayback)
    }

    // FIXME: Old code!! Maybe not necessary anymore?
    //    override fun onStart() {
    //        super.onStart()
    //        playbackModel.song.value?.let { requireBinding().playbackCover.bind(it) }
    //        requireBinding().root.viewTreeObserver.addOnGlobalLayoutListener(this)
    //    }

    //    override fun onStop() {
    //        super.onStop()
    //        requireBinding().root.viewTreeObserver.removeOnGlobalLayoutListener(this)
    //    }

    //    override fun onGlobalLayout() {
    //        if (binding == null || lastCoverWidth < 0) {
    //            return
    //        }
    // Hacky workaround for cover radius not being preserved in between sizing changes
    // (i.e split screen or landscape mode)
    // For some reason ConstraintLayout does several passes on 1:1 elements that causes their
    // size to radically change, so we wait until it stabilizes and then force an image
    // reload if needed. Optimistically this is a no-op from coil caching, but when the cover
    // did accidentally load the wrong image (with weird corner radius intended for bigger
    // covers) we can force it to reload.
    // If this breaks, it's fine since we also started a load as we normally did w/state
    // updates, so the cover will not break.
    //        val binding = requireBinding()
    //        val coverWidth = binding.playbackCover.width
    //        if (lastCoverWidth != coverWidth) {
    //            lastCoverWidth = coverWidth
    //        } else {
    //            playbackModel.song.value?.let { binding.playbackCover.bind(it) }
    //            lastCoverWidth = -1
    //        }
    //    }

    override fun onDestroyBinding(binding: FragmentPlaybackPanelBinding) {
        equalizerLauncher = null
        binding.playbackRepeat.clearPendingIcon()
        binding.playbackSong.isSelected = false
        binding.playbackArtist.isSelected = false
        binding.playbackAlbum?.isSelected = false
        binding.playbackToolbar.setOnMenuItemClickListener(null)
        userAwarePagerCallback?.release()
        binding.playbackPager?.adapter = null
        binding.lyricsOverlay.setOnClickListener(null)
        lyricsOverlayTapListener?.let { binding.lyricsOverlayRecycler.removeOnItemTouchListener(it) }
        lyricsOverlayTapListener = null
        binding.lyricsOverlayRecycler.adapter = null
        lyricsOverlayLayoutManager = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_open_equalizer) {
            // Launch the system equalizer app, if possible.
            L.d("Launching equalizer")
            val equalizerIntent =
                Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    // Provide audio session ID so the equalizer can show options for this app
                    // in particular.
                    .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, playbackModel.currentAudioSessionId)
                    // Signal music type so that the equalizer settings are appropriate for
                    // music playback.
                    .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            try {
                requireNotNull(equalizerLauncher) { "Equalizer panel launcher was not available" }
                    .launch(equalizerIntent)
            } catch (e: ActivityNotFoundException) {
                requireContext().showToast(R.string.err_no_app)
            }
            return true
        }

        return false
    }

    override fun onSeekConfirmed(positionDs: Long) {
        playbackModel.seekTo(positionDs)
    }

    private fun updateSong(song: Song?) {
        if (song == null) {
            // Nothing to do.
            return
        }

        val binding = requireBinding()
        val context = requireContext()
        L.d("Updating song display: $song")
        binding.playbackSong.text = song.name.resolve(context)
        binding.playbackArtist.text = song.artists.resolveNames(context)
        binding.playbackAlbum?.text = song.album.name.resolve(context)
        binding.playbackSeekBar?.durationDs = song.durationMs.msToDs()
        if (binding.lyricsOverlay.isVisible) {
            updateLyricsOverlayContent(song)
        }
        // playbackRoute/controlState (updateUsbStatus's own triggers) don't necessarily re-emit
        // on every track change -- two consecutive HAL-fallback tracks report the exact same
        // PlaybackRoute.Fallback value (always the fixed HAL output format, never the file's own),
        // which a StateFlow conflates away as a no-op. The bitrate shown alongside it is per-track
        // though, so it needs its own refresh right here, not just whenever route/controlState
        // actually change.
        updateUsbStatus(usbAudioManager.playbackRoute.value, usbAudioManager.controlState.value)
    }

    private fun updateParent(parent: MusicParent?) {
        val binding = requireBinding()
        val context = requireContext()
        binding.playbackToolbar.subtitle =
            parent?.run { name.resolve(context) } ?: context.getString(R.string.lbl_all_songs)
    }

    /**
     * Shows exactly what [org.ostrichbat2k.usbdac.RoutingAudioSink] is doing with the current
     * track, always visible (not just while a DAC session exists) since the current track's
     * format/bitrate is real, file-derived info relevant to every user.
     *
     * Both [UsbAudioManager.PlaybackRoute.Direct] and [UsbAudioManager.PlaybackRoute.Fallback]
     * carry the file's own real bit depth/sample rate (see [UsbAudioManager.PlaybackRoute
     * .Fallback]'s own doc comment for why Fallback's fields mean this despite not being what HAL
     * is actually doing), so both render the same five-segment layout here -- only the leading
     * path label ("USB" vs. "HAL") differs. The container/codec label and bitrate are always the
     * file's own, independent of route entirely -- a compressed source's bitrate has no bearing on
     * the decoded PCM actually reaching either sink.
     */
    private fun updateUsbStatus(route: UsbAudioManager.PlaybackRoute, controlState: UsbControlState?) {
        val statusView = requireBinding().playbackUsbStatus
        statusView.visibility = View.VISIBLE
        if (usbSettings.abxTestMode && controlState != null) {
            // Deliberately never derived from `route` here -- the path label itself (USB vs. HAL)
            // would give away which route is live just as plainly as the format details would.
            // The guess prompt itself is triggered on track change (see updateAbxPendingGuess),
            // not from here -- this just reports the running score.
            val stats = usbAudioManager.abxStats.value
            statusView.text = getString(R.string.lbl_abx_prompt, stats.correct, stats.total)
            return
        }
        val song = playbackModel.song.value
        val formatLabel = song?.format?.resolveShort()
        val bitrateLabel = song?.let { getString(R.string.fmt_bitrate, it.bitrateKbps) }
        statusView.text =
            when (route) {
                is UsbAudioManager.PlaybackRoute.Direct ->
                    formatStatus("USB", formatLabel, route.bitDepth, route.sampleRateHz, bitrateLabel)
                is UsbAudioManager.PlaybackRoute.Fallback ->
                    formatStatus("HAL", formatLabel, route.bitDepth, route.sampleRateHz, bitrateLabel)
                UsbAudioManager.PlaybackRoute.Switching -> "Switching audio output…"
            }
    }

    private fun formatStatus(
        pathLabel: String,
        formatLabel: String?,
        bitDepth: Int?,
        sampleRateHz: Int,
        bitrateLabel: String?,
    ): String =
        listOfNotNull(pathLabel, formatLabel, bitDepth?.let { "$it-bit" }, bitrateLabel, formatSampleRate(sampleRateHz))
            .joinToString(" · ")

    /**
     * Fires on every track change while an ABX test is running (see
     * [UsbAudioManager.abxPendingGuess]'s doc comment) -- prompts about whichever track just
     * finished, never the one that's now playing. Pauses first: the queue has already advanced
     * to the next (independently, freshly-rolled) track by this point, and letting it keep
     * playing while the user is still deciding about the *previous* one would both be a
     * distraction and risk them answering about the wrong track entirely.
     */
    private fun updateAbxPendingGuess(pending: Boolean?) {
        if (pending == null) return
        if (playbackModel.isPlaying.value) playbackModel.togglePlaying()
        showAbxGuessDialog()
    }

    /**
     * Grades the guess immediately via [UsbAudioManager.submitAbxGuess], which also consumes the
     * trial -- this dialog can't be reopened for the same track after a guess, same as a real ABX
     * test only counting one answer per trial. Not cancelable: a dismissed-without-answering
     * trial would otherwise silently vanish (the next track change overwrites it) with no
     * indication anything was missed.
     */
    private fun showAbxGuessDialog() {
        val context = requireContext()
        MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setTitle(R.string.lbl_abx_dialog_title)
            .setItems(
                arrayOf(
                    getString(R.string.lbl_abx_guess_direct), getString(R.string.lbl_abx_guess_fallback))) { _, which ->
                    val guessedDirect = which == 0
                    val correct = usbAudioManager.submitAbxGuess(guessedDirect) ?: return@setItems
                    // The guess itself was right or wrong about which route was live -- so the
                    // actual route is the guess when correct, and its opposite when not.
                    val actualWasDirect = if (correct) guessedDirect else !guessedDirect
                    val actualLabel =
                        getString(
                            if (actualWasDirect) R.string.lbl_abx_route_direct else R.string.lbl_abx_route_fallback)
                    val message =
                        if (correct) getString(R.string.lbl_abx_correct, actualLabel)
                        else getString(R.string.lbl_abx_incorrect, actualLabel)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            .show()
    }

    private fun formatSampleRate(sampleRateHz: Int): String {
        val khz = sampleRateHz / 1000f
        val rate = if (khz == khz.toInt().toFloat()) "${khz.toInt()}" else "$khz"
        return "${rate}kHz"
    }

    private fun updatePosition(positionDs: Long) {
        requireBinding().playbackSeekBar?.positionDs = positionDs
        updateLyricsOverlayPosition(positionDs)
    }

    /** Toggles the lyrics-over-blurred-art overlay, refreshing its content when opening. */
    private fun toggleLyricsOverlay() {
        val binding = requireBinding()
        val opening = !binding.lyricsOverlay.isVisible
        binding.lyricsOverlay.isVisible = opening
        if (opening) {
            playbackModel.song.value?.let { updateLyricsOverlayContent(it) }
        }
    }

    private fun updateLyricsOverlayContent(song: Song) {
        val binding = requireBinding()
        binding.lyricsOverlayBackdrop.bind(song)
        val parsed = LrcParser.parse(song.lyrics)
        lyrics = parsed
        binding.lyricsOverlayRecycler.isVisible = parsed != null
        binding.lyricsOverlayEmpty.isVisible = parsed == null
        // Unsynced lyrics have no auto-scroll to reach lines past the visible area, so they stay
        // user-scrollable; synced lyrics are driven entirely by updateLyricsOverlayPosition.
        lyricsOverlayLayoutManager?.scrollEnabled = parsed?.synced != true
        lyricsOverlayAdapter.submitLines(parsed?.lines.orEmpty(), parsed?.synced == true) {
            updateLyricsOverlayPosition(playbackModel.positionDs.value)
        }
    }

    private fun updateLyricsOverlayPosition(positionDs: Long) {
        val binding = requireBinding()
        if (!binding.lyricsOverlay.isVisible) return
        val lyrics = lyrics ?: return
        if (!lyrics.synced) return
        val index = currentLyricsLineIndex(lyrics.lines, positionDs.dsToMs())
        // Only the adapter's own equality check (line == currentLine) guards setCurrentLine
        // itself -- without also checking it here, every ~100ms position tick re-triggered a
        // full scrollToPositionWithOffset layout pass even when the current line hadn't actually
        // changed since the last tick.
        val changed = index != lyricsOverlayAdapter.currentLine
        lyricsOverlayAdapter.setCurrentLine(index)
        if (changed && index >= 0) {
            // Keep the current line pinned near the top rather than centered, so more of the
            // upcoming lines are visible below it.
            val recycler = binding.lyricsOverlayRecycler
            val layoutManager = recycler.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(index, recycler.height / 6)
        }
    }

    private fun updateRepeat(repeatMode: RepeatMode) {
        val repeatButton = requireBinding().playbackRepeat
        repeatButton.isChecked = repeatMode != RepeatMode.NONE
        repeatButton.setIconResource(repeatMode.icon)
    }

    private fun updatePlaying(isPlaying: Boolean) {
        requireBinding().playbackPlayPause.isChecked = isPlaying
        requireBinding().playbackSeekBar?.setWaveEnabled(isPlaying)
    }

    private var dacActivatingSpin: ObjectAnimator? = null

    /**
     * See [UsbAudioManager.blockPlayback]'s doc comment: disables this button and swaps its icon
     * for a spinner while a USB DAC activation this control would otherwise race is in flight,
     * instead of letting a tap start playback that might land on HAL purely due to timing.
     *
     * Spins the button *view* itself via [ObjectAnimator] rather than animating the icon drawable
     * -- confirmed, reproduced: an [AnimatedVectorDrawable][android.graphics.drawable
     * .AnimatedVectorDrawable] set as this button's icon never actually rotated, even after
     * unwrapping [MaterialButton][com.google.android.material.button.MaterialButton]'s tint-aware
     * wrapper (this button's style chain resolves an iconTint via
     * Widget.Material3Expressive.Button.IconButton.Tonal) to reach the real instance and start it
     * directly -- it just sat on its first frame regardless. [View.ROTATION] has no dependency on
     * any of that; it's a plain property of the view, not the icon.
     */
    private fun updateBlockPlayback(blocked: Boolean) {
        val button = requireBinding().playbackPlayPause
        button.isEnabled = !blocked
        if (blocked) {
            button.setIconResource(R.drawable.ic_dac_activating_24)
            dacActivatingSpin?.cancel()
            dacActivatingSpin =
                ObjectAnimator.ofFloat(button, View.ROTATION, 0f, 360f).apply {
                    duration = 900
                    repeatCount = ObjectAnimator.INFINITE
                    interpolator = LinearInterpolator()
                    start()
                }
        } else {
            dacActivatingSpin?.cancel()
            dacActivatingSpin = null
            button.rotation = 0f
            button.setIconResource(R.drawable.sel_playing_state_24)
        }
    }

    private fun updateShuffled(isShuffled: Boolean) {
        requireBinding().playbackShuffle.isChecked = isShuffled
    }

    private fun updatePager(queue: PagerQueue) {
        // Right now there's easily 140ms of frame skipping when going next/prev. This is primarily
        // the fault of specifically the nested bottom sheet UI setup, which is intractable to
        // optimize. If I don't do multiple remeasures/relayouts on every slightest state
        // instability
        // I will suddenly encounter insane issues where the sheet fails to measure, appears below
        // the sidebar, flies away, not changing with ui scale, etc, often only on third-party OEM
        // ROMs that randomly mangle  SDK APIs and the SystemUI chrome for no reason.
        //
        // Historically this was not an issue, as I did not animate next/prev. Now I do, and it's
        // highly noticeable. So at least for plain next/prev I have to hack around it, do not
        // execute any transition until the state has fully adjudicated and laid out the UI. It's
        // not effective for swiping but there's nothing I can do there.
        //
        // Eventually one day Claude Fable 6.7 will probably be able to figure out that you need to
        // reflect into System.FoobaCrumbo::beegieConnector(GoolaUtils.PlubBud) and call it
        // specifically with 0x189B31FA alongside disabling the AndroidX Helpo SuperCharge by
        // manually clobbering `BottomSheetM2InternalBoogieCompat::scrimbloManager` to null for it
        // to not actually randomly mangle the sheets and do it in 1 clean layout, but for now I
        // must do this to keep my sanity.
        //
        // Actual snippet here was codex, just cleaned & adapted it / cognitive ownership
        requireBinding().playbackPager.apply {
            if (!isAttachedToWindow) {
                post { updatePagerImpl(queue) }
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isHardwareAccelerated) {
                // New version using post-Q frame hooks
                viewTreeObserver.registerFrameCommitCallback {
                    post { postOnAnimation { updatePagerImpl(queue) } }
                }
                postInvalidateOnAnimation()
            } else {
                // Let current layout happen, then wait for the next to conclude
                postOnAnimation { postOnAnimation { updatePagerImpl(queue) } }
            }
        }
    }

    private fun updatePagerImpl(queue: PagerQueue) {
        // Android insanity means this may be executed after view destruction
        // but only on some devices.
        val binding = binding ?: return

        val command = playbackModel.pagerCommand.consume()
        if (command == null) {
            // This probably shouldn't happen in practice, as QueueViewModel directly
            // attaches to PlaybackStateManager and will basically always initialize
            // with a command as a result.
            //
            // If it does happen we should just make sure the UI state is aligned. Don't
            // want broken UI.
            coverPagerAdapter.update(queue.queue, null)
            binding.playbackPager.setCurrentItem(queue.index, false)
            return
        }

        if (command.update != null) {
            // queue needs to be updated.
            coverPagerAdapter.update(queue.queue, command.update)
        }

        if (command.scroll != null) {
            // we need to scroll, however the smooth scroll only really looks best
            // when we are only doing next/prev due to various factors. better to
            // just not animate on outright gotos or queue updates
            val delta = binding.playbackPager.currentItem - command.scroll
            if (delta == 0) {
                // user scroll, carry on
                return
            }
            if (command.update == null && abs(delta) == 1) {
                binding.playbackPager.smoothScrollByPageTo(command.scroll)
            } else {
                binding.playbackPager.setCurrentItem(command.scroll, false)
            }
        }
    }

    private fun navigateToCurrentSong() {
        playbackModel.song.value?.let(detailModel::showAlbum)
    }

    private fun navigateToCurrentArtist() {
        playbackModel.song.value?.let(detailModel::showArtist)
    }

    private fun navigateToCurrentAlbum() {
        playbackModel.song.value?.let { detailModel.showAlbum(it.album) }
    }

    override fun seek(direction: Direction) {
        when (direction) {
            Direction.FORWARDS -> playbackModel.stepForward()
            Direction.BACKWARDS -> playbackModel.stepBackwards()
        }
    }

    override fun onSingleTap() {
        toggleLyricsOverlay()
    }
}

/**
 * A [LinearLayoutManager] whose user-driven scroll (drag/fling) can be toggled off while still
 * allowing programmatic calls like [LinearLayoutManager.scrollToPositionWithOffset].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class LyricsScrollLayoutManager(context: Context) : LinearLayoutManager(context) {
    var scrollEnabled = false

    override fun canScrollVertically() = scrollEnabled
}
