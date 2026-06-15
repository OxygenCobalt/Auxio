/*
 * Copyright (c) 2026 Auxio Project
 * WavySlider.kt is part of Auxio.
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
 
package com.google.android.material.slider

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import androidx.core.content.res.getDimensionPixelSizeOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withSave
import androidx.core.view.isVisible
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import com.google.android.material.R as MR
import com.google.android.material.motion.MotionUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.R
import timber.log.Timber

/**
 * Slider with active-track wave rendering that ports MDC LinearProgressIndicator's wavy draw
 * behavior.
 *
 * @author Codex, Alexander Capehart (OxygenCobalt) (Clean Up/Rewrite/Cognitive Ownership)
 */
class WavySlider
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = MR.attr.sliderStyle,
) : Slider(context, attrs, defStyleAttr) {
    private val wavePaint: Paint

    private val displayedWavePath = Path()
    private val pathMeasure = PathMeasure()
    private var adjustedWavelength = 0f

    private val cachedWavePath = Path()
    private val transform = Matrix()
    private var cachedWavelength = -1
    private var cachedTrackLength = -1f

    private val drawRect = RectF()
    private val patchRect = RectF()
    private val clipRect = RectF()
    private val roundedRectPath = Path()

    private val startPoint = PointF()
    private val endPoint = PointF()

    private val transparentTrackTint = ColorStateList.valueOf(Color.TRANSPARENT)
    // Necessary due to us clobbering it when we enable the wavy track
    private var tmpTrackTintList: ColorStateList = trackActiveTintList
    private var linearActiveTrackSuppressed = false

    private var waveTransitionAnimation: SpringAnimation? = null
    private var currentAmplitudeFraction = MIN_VISIBLE_WAVE_FRACTION
    private var waveEnabled = false

    private var phaseFraction = 0f
    private var lastPhaseFrameNanos = 0L
    private var phaseTickerScheduled = false

    private var configuredWavelengthPx = 0
    private var configuredAmplitudePx = 0
    private var configuredSpeedPx = 0

    private var displayedTrackThickness = 0f
    private var displayedCornerRadius = 0f
    private var displayedInnerCornerRadius = 0f
    private var displayedAmplitude = 0f

    private var waveRampProgressMin = 0f
    private var waveRampProgressMax = DEFAULT_WAVE_RAMP_PROGRESS_MAX

    @Px private var waveSpeed: Int = 0

    private val phaseTicker =
        object : Runnable {
            override fun run() {
                phaseTickerScheduled = false
                if (!shouldTickPhase() && waveTransitionAnimation == null) {
                    return
                }

                if (shouldTickPhase()) {
                    updatePhaseFraction()
                }
                invalidate()
                schedulePhaseTicker()
            }
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.WavySlider) {
            waveRampProgressMin = getFloat(R.styleable.WavySlider_waveAmplitudeRampProgressMin, 0f)
            waveRampProgressMax =
                getFloat(
                    R.styleable.WavySlider_waveAmplitudeRampProgressMax,
                    DEFAULT_WAVE_RAMP_PROGRESS_MAX,
                )
            check(waveRampProgressMin in 0.0..1.0) { "rampProgressMin out of range" }
            check(waveRampProgressMax in 0.0..1.0) { "rampProgressMax out of range" }
            check(waveRampProgressMax - waveRampProgressMin > EPSILON) {
                "rampProgressMin must be < rampProgressMax"
            }

            configuredWavelengthPx = getDimensionPixelSizeOrThrow(R.styleable.WavySlider_wavelength)
            configuredAmplitudePx =
                getDimensionPixelSizeOrThrow(R.styleable.WavySlider_waveAmplitude)
            configuredSpeedPx = getDimensionPixelSizeOrThrow(R.styleable.WavySlider_waveSpeed)
            check(
                configuredWavelengthPx > 0 && configuredAmplitudePx > 0 && configuredSpeedPx > 0
            ) {
                "Invalid wave dimensions, must be >0"
            }
        }

        wavePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                // style = is set later
                strokeCap = Paint.Cap.BUTT
                strokeWidth = trackHeight.toFloat()
                isAntiAlias = true
                color =
                    trackActiveTintList.getColorForState(
                        drawableState,
                        trackActiveTintList.defaultColor,
                    )
            }
        updateActiveTrackSuppression()

        // Easier logic this way (even if a bit non-standard)
        // The funky tangent stuff was just not my thing
        if (trackCornerSize < trackHeight) {
            Timber.w(
                "WavySlider can only be fully rounded. Clamping corner size down to trackHeight / 2"
            )
            trackCornerSize = trackHeight
        }
        if (trackInsideCornerSize < trackHeight) {
            Timber.w(
                "WavySlider can only be fully rounded. Clamping inner corner size down to trackHeight / 2"
            )
            trackInsideCornerSize = trackHeight
        }
    }

    // State reset handling

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ensurePhaseTickerState()
    }

    override fun onDetachedFromWindow() {
        waveTransitionAnimation?.cancel()
        waveTransitionAnimation = null
        removeCallbacks(phaseTicker)
        phaseTickerScheduled = false
        resetPhaseClock()
        super.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        ensurePhaseTickerState()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        ensurePhaseTickerState()
    }

    override fun setTrackActiveTintList(trackColor: ColorStateList) {
        tmpTrackTintList = trackColor
        if (!linearActiveTrackSuppressed) {
            // Only forward to the superclass if we aren't overriding it
            // If we are overriding it we store in tmpTrackTintList for later
            super.setTrackActiveTintList(trackColor)
        }
    }

    /**
     * Configure whether the wave effect is on or off.
     *
     * This will animate the wave in.
     *
     * @param enabled Whether to enable the wave or not.
     */
    fun setWaveEnabled(enabled: Boolean) {
        waveTransitionAnimation?.cancel()
        waveTransitionAnimation = null

        waveEnabled = enabled
        if (enabled) {
            // Enabled, start moving / transition to 1f amplitude
            waveSpeed = configuredSpeedPx
            resetPhaseClock()
            transitionToAmplitudeFraction(1f)
        } else if (abs(currentAmplitudeFraction - MIN_VISIBLE_WAVE_FRACTION) < EPSILON) {
            // No amplitude configured, do nothing
            waveSpeed = 0
            resetPhaseClock()
        } else {
            // Off, transition to 0 amplitude and stop
            waveSpeed = 0
            transitionToAmplitudeFraction(MIN_VISIBLE_WAVE_FRACTION) { resetPhaseClock() }
        }
        // Get rid of inactive track if needed
        updateActiveTrackSuppression()
        // Start doing the actual moving wave animation
        ensurePhaseTickerState()
        // Start drawing
        markDirty()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val trackLength = trackWidth.toFloat()
        if (trackLength <= 0f) {
            // Nothing to draw width-wise
            return
        }
        val range = valueTo - valueFrom
        if (range <= 0f) {
            // Nothing to draw length-wise
            return
        }
        val progressFraction = ((value - valueFrom) / range).coerceIn(0f, 1f)
        if (progressFraction <= 0f) {
            // Nothing to draw progress-wise (the wavy part is only from active process)
            return
        }

        displayedTrackThickness = trackHeight.toFloat()
        displayedCornerRadius =
            min(min(displayedTrackThickness / 2f, trackCornerSize.toFloat()), trackLength / 2f)
        displayedInnerCornerRadius = displayedCornerRadius

        // Calculate the actual wave path or re-use it
        ensureCachedWavePath(trackLength, configuredWavelengthPx)
        if (pathMeasure.length <= 0f || adjustedWavelength <= 0f) {
            return
        }

        // Tune down the amplitude by the progress ramping
        // Important since short wavy progress looks odd and we want to interpolate
        // in all things considered
        val startRampFraction =
            if (shouldDrawWave()) {
                calculateStartRampFraction(progressFraction, trackLength)
            } else {
                0f
            }
        val rampedAmplitudeFraction =
            if (shouldDrawWave()) {
                applyStartWaveRamp(
                    amplitudeFraction = currentAmplitudeFraction,
                    startRampFraction = startRampFraction,
                )
            } else {
                0f
            }
        displayedAmplitude = configuredAmplitudePx.toFloat() * rampedAmplitudeFraction

        val activeTrackColor =
            tmpTrackTintList.getColorForState(drawableState, tmpTrackTintList.defaultColor)
        wavePaint.color = activeTrackColor
        wavePaint.strokeWidth = displayedTrackThickness

        // Now
        val isRtl = layoutDirection == LAYOUT_DIRECTION_RTL

        // Where the thumb should be
        val thumbTrackPosition =
            if (isRtl) {
                // RTL - Invert progress
                (1f - progressFraction) * trackLength
            } else {
                progressFraction * trackLength
            }

        // Where the progress track should start
        val startBound =
            if (isRtl) {
                // RTL, anchor start to pos (making room for gap)
                thumbTrackPosition + thumbTrackGapSize
            } else {
                // LTR, anchor start to right (making room for corner radius)
                -displayedCornerRadius
            }

        // Where the progress track should end
        val endBound =
            if (isRtl) {
                // RTL, anchor end to left (making room for corner radius)
                trackLength + displayedCornerRadius
            } else {
                // LTR, anchor end to progress (make room for gap)
                thumbTrackPosition - thumbTrackGapSize
            }
        if (startBound >= endBound) {
            // No progress to draw.
            return
        }

        // Set up the start/end corner radii (this changes for inner/outer radii)
        drawTrack(
            canvas = canvas,
            trackLength = trackLength,
            startBound = startBound,
            endBound = endBound,
            amplitudeFraction = rampedAmplitudeFraction,
            phaseFraction = phaseFraction,
        )
    }

    private fun drawTrack(
        canvas: Canvas,
        trackLength: Float,
        startBound: Float,
        endBound: Float,
        amplitudeFraction: Float,
        phaseFraction: Float,
    ) {
        // This logic seems more complex than it should be largely because
        // we have to gracefully collapse the active track as we seek
        // to the end.
        val originX = trackSidePadding.toFloat()
        val trackCenterY = height / 2f

        val startBlockCenterX = startBound + displayedCornerRadius
        val endBlockCenterX = endBound - displayedCornerRadius

        startPoint.reset()
        endPoint.reset()
        startPoint.translate(startBlockCenterX + originX, trackCenterY)
        endPoint.translate(endBlockCenterX + originX, trackCenterY)

        if (startBound <= 0f && endBlockCenterX < startBlockCenterX) {
            // Too small for caps, need to draw a rounded block instead
            drawRoundedBlock(
                canvas = canvas,
                drawCenter = startPoint,
                clipCenter = endPoint,
                clipRight = true,
            )
            return
        }

        if (startBlockCenterX > endBlockCenterX) {
            // Too small for caps, need to draw a rounded block instead
            // In this case since start > end we actually need to draw it in
            // reverse or it'll break
            drawRoundedBlock(
                canvas = canvas,
                drawCenter = endPoint,
                clipCenter = startPoint,
                clipRight = false,
            )
            return
        }

        // Otherwise draw the wavy stroke
        // Need to configure the paint mode for this
        wavePaint.style = Paint.Style.STROKE
        // Always fully rounded (easier, albeit not spec compliant)
        wavePaint.strokeCap = Paint.Cap.ROUND

        if (amplitudeFraction > 0f) {
            // Calculate what we should actually display
            // This includes the phase-scroll logic
            calculateDisplayedWavePath(
                trackLength = trackLength,
                start = startBlockCenterX / trackLength,
                end = endBlockCenterX / trackLength,
                phaseFraction = phaseFraction,
                baseTranslationX = trackSidePadding.toFloat(),
                baseTranslationY = trackCenterY,
            )
            // Then draw the displayed wave path
            canvas.drawPath(displayedWavePath, wavePaint)
        } else {
            // No wave, simplify to line
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, wavePaint)
        }
    }

    private fun drawRoundedBlock(
        canvas: Canvas,
        drawCenter: PointF,
        clipCenter: PointF,
        clipRight: Boolean,
    ) {
        // NOTE: Still need to study this more.

        // Have to swap paint to fill for a rounded rect
        wavePaint.style = Paint.Style.FILL
        canvas.withSave {
            // Drawing the "block" is weird since we ideally want one that can go arbitrarily
            // tiny w/o clipping. This leads to some odd logic.
            var clipWidth = trackHeight.toFloat()
            val halfHeight = trackHeight / 2f
            // Set up a rect that fills in the side that would otherwise get clobbered
            // if we clipped an already rounded sikde
            if (clipRight) {
                val leftEdgeDiff = (clipCenter.x - trackHeight / 2f) - (drawCenter.x - trackHeight)
                if (leftEdgeDiff > 0f) {
                    clipCenter.translate(-leftEdgeDiff / 2f, 0f)
                    clipWidth += leftEdgeDiff
                }
                patchRect.set(
                    drawCenter.x,
                    drawCenter.y - halfHeight,
                    drawCenter.x + trackHeight / 2f,
                    drawCenter.y + halfHeight,
                )
            } else {
                val rightEdgeDiff = (clipCenter.x + trackHeight / 2f) - (drawCenter.x + trackHeight)
                if (rightEdgeDiff < 0f) {
                    clipCenter.translate(-rightEdgeDiff / 2f, 0f)
                    clipWidth -= rightEdgeDiff
                }
                patchRect.set(
                    drawCenter.x - trackHeight / 2f,
                    drawCenter.y - halfHeight,
                    drawCenter.x,
                    drawCenter.y + halfHeight,
                )
            }

            // Create the mask that applies rounded corners/caps that also adequately shrinks
            // when extremely thin
            val halfClipWidth = clipWidth / 2f
            clipRect.set(
                clipCenter.x - halfClipWidth,
                clipCenter.y - halfHeight,
                clipCenter.x + halfClipWidth,
                clipCenter.y + halfHeight,
            )

            // Create the rounded cap rect
            drawRect.set(
                drawCenter.x - halfHeight,
                drawCenter.y - halfHeight,
                drawCenter.x + halfHeight,
                drawCenter.y + halfHeight,
            )

            // Then finally draw the three rects to create the final pill shape.
            roundedRectPath.reset()
            roundedRectPath.addRoundRect(
                clipRect,
                trackHeight.toFloat(),
                trackHeight.toFloat(),
                Path.Direction.CCW,
            )
            clipPath(roundedRectPath)

            drawRect(patchRect, wavePaint)
            drawRoundRect(drawRect, trackHeight.toFloat(), trackHeight.toFloat(), wavePaint)
        }
    }

    private fun applyStartWaveRamp(amplitudeFraction: Float, startRampFraction: Float): Float {
        if (amplitudeFraction <= 0f) {
            // Don't care, nothing to ramp
            return 0f
        }
        val easedRamp = applyRampEasing(startRampFraction)
        return amplitudeFraction * easedRamp
    }

    private fun calculateStartRampFraction(progressFraction: Float, trackLength: Float): Float {
        val progressRamp = calculateProgressRamp(progressFraction)
        if (trackLength <= 0f) {
            return progressRamp
        }
        val thresholdPx = displayedCornerRadius + thumbTrackGapSize
        val rampDistancePx = max(displayedCornerRadius, configuredAmplitudePx.toFloat())
        if (thresholdPx <= 0f || rampDistancePx <= 0f) {
            return progressRamp
        }
        val activeLengthPx = progressFraction * trackLength
        val edgeRamp = ((activeLengthPx - thresholdPx) / rampDistancePx).coerceIn(0f, 1f)
        return min(edgeRamp, progressRamp)
    }

    private fun calculateProgressRamp(progressFraction: Float): Float {
        if (progressFraction <= waveRampProgressMin) {
            return 0f
        }
        val span = (waveRampProgressMax - waveRampProgressMin).coerceAtLeast(EPSILON)
        return ((progressFraction - waveRampProgressMin) / span).coerceIn(0f, 1f)
    }

    @SuppressLint("PrivateResource")
    private fun transitionToAmplitudeFraction(target: Float, onFinished: (() -> Unit)? = null) {
        val clampedTarget = target
        if (abs(currentAmplitudeFraction - clampedTarget) < EPSILON) {
            // Too little work, nothing to do
            currentAmplitudeFraction = clampedTarget
            updateActiveTrackSuppression()
            ensurePhaseTickerState()
            onFinished?.invoke()
            return
        }

        waveTransitionAnimation?.cancel()
        waveTransitionAnimation = null

        // Use a standard M3 animation
        // Not using Auxio anim utils due to this being outside the Auxio pkg
        // TODO: Could move it inside the pkg honestly
        val springAnimation =
            SpringAnimation(FloatValueHolder(currentAmplitudeFraction)).apply {
                spring =
                    MotionUtils.resolveThemeSpringForce(
                        context,
                        MR.attr.motionSpringFastEffects,
                        MR.style.Motion_Material3_Spring_Standard_Fast_Effects,
                    )
                setStartValue(currentAmplitudeFraction)
                setMinimumVisibleChange(MIN_SPRING_VISIBLE_CHANGE)
                addUpdateListener { _, value, _ ->
                    currentAmplitudeFraction = value
                    ensurePhaseTickerState()
                    invalidate()
                }
                addEndListener { _, canceled, value, _ ->
                    currentAmplitudeFraction = value
                    updateActiveTrackSuppression()
                    ensurePhaseTickerState()
                    invalidate()
                    if (!canceled) {
                        onFinished?.invoke()
                    }
                }
            }

        waveTransitionAnimation = springAnimation
        updateActiveTrackSuppression()
        springAnimation.animateToFinalPosition(clampedTarget)
    }

    private fun ensurePhaseTickerState() {
        if (shouldTickPhase() || waveTransitionAnimation != null) {
            schedulePhaseTicker()
        } else {
            removeCallbacks(phaseTicker)
            phaseTickerScheduled = false
        }
    }

    private fun schedulePhaseTicker() {
        if (phaseTickerScheduled) {
            return
        }
        phaseTickerScheduled = true
        postOnAnimation(phaseTicker)
    }

    private fun shouldTickPhase(): Boolean =
        shouldDrawWave() &&
            waveSpeed != 0 &&
            isVisible &&
            windowVisibility == VISIBLE &&
            isShown &&
            alpha > 0f

    private fun updatePhaseFraction() {
        val speed = waveSpeed
        if (speed == 0) {
            return
        }

        val trackLength = trackWidth.toFloat()
        val range = valueTo - valueFrom
        if (trackLength <= 0f || range <= 0f) {
            return
        }
        val progressFraction = ((value - valueFrom) / range).coerceIn(0f, 1f)
        val phaseSpeedScale =
            applyRampEasing(calculateStartRampFraction(progressFraction, trackLength))
        val effectiveSpeed = speed.toFloat() * phaseSpeedScale
        if (effectiveSpeed == 0f) {
            // Not actually moving once ramp is taken in, move on
            return
        }

        val nowNanos = System.nanoTime()
        if (lastPhaseFrameNanos != 0L) {
            val deltaSeconds = (nowNanos - lastPhaseFrameNanos) / 1_000_000_000f
            val delta = deltaSeconds * (effectiveSpeed / configuredWavelengthPx.toFloat())
            // Move phase forward, take the decimal
            // All incoming inputs (phase & delta) are not-zero so no need
            // to care about negative modulo
            phaseFraction = (phaseFraction + delta) % 1f
        }
        lastPhaseFrameNanos = nowNanos
    }

    private fun resetPhaseClock() {
        phaseFraction = MIN_PHASE_FRACTION
        lastPhaseFrameNanos = 0L
    }

    private fun shouldDrawWave(): Boolean = waveEnabled || waveTransitionAnimation != null

    private fun updateActiveTrackSuppression() {
        val suppress = true
        if (suppress == linearActiveTrackSuppressed) {
            return
        }
        if (suppress) {
            // We can piggyback off of Slider's existing rendering by just
            // making sure the straight track is disabled when we switch to the
            // wavy one.
            super.setTrackActiveTintList(transparentTrackTint)
        } else {
            super.setTrackActiveTintList(tmpTrackTintList)
        }
        linearActiveTrackSuppressed = suppress
    }

    private fun markDirty() {
        cachedWavelength = -1
        cachedTrackLength = -1f
        adjustedWavelength = 0f
    }

    private fun ensureCachedWavePath(trackLength: Float, wavelength: Int) {
        if (
            cachedTrackLength == trackLength &&
                cachedWavelength == wavelength &&
                adjustedWavelength > 0f
        ) {
            return
        }
        cachedTrackLength = trackLength
        cachedWavelength = wavelength
        invalidateCachedWavePath(trackLength, wavelength)
    }

    private fun invalidateCachedWavePath(trackLength: Float, wavelength: Int) {
        // NOTE: Going to be honest, this is the part I understand the least.
        // Need to study it more.
        cachedWavePath.rewind()
        adjustedWavelength = 0f
        if (trackLength <= 0f || wavelength <= 0) {
            pathMeasure.setPath(cachedWavePath, false)
            return
        }

        val cycleCount = max(1, (trackLength / wavelength).toInt())
        // toInt rounding errors necessitate us to actually indicate what the
        // real rendered wavelength is.
        adjustedWavelength = trackLength / cycleCount
        for (i in 0..cycleCount) {
            // To be honest this is math magic but it does draw the two
            // curves needed for waves. They aren't distinct up/down poitns.
            val cycle = i.toFloat()
            cachedWavePath.cubicTo(
                2 * cycle + WAVE_SMOOTHNESS,
                0f,
                2 * cycle + 1 - WAVE_SMOOTHNESS,
                1f,
                2 * cycle + 1,
                1f,
            )
            cachedWavePath.cubicTo(
                2 * cycle + 1 + WAVE_SMOOTHNESS,
                1f,
                2 * cycle + 2 - WAVE_SMOOTHNESS,
                0f,
                2 * cycle + 2,
                0f,
            )
        }

        // Lay out the actual wave across the seekbar
        // Without this phase animations won't work and the track will appear "scrunched"
        // Additionally if I understand right this also helps extend the wave enough to
        // select parts of it to scroll through.
        transform.reset()
        transform.setScale(adjustedWavelength / 2f, -2f)
        transform.postTranslate(0f, 1f)
        cachedWavePath.transform(transform)
        pathMeasure.setPath(cachedWavePath, false)
    }

    private fun calculateDisplayedWavePath(
        trackLength: Float,
        start: Float,
        end: Float,
        phaseFraction: Float,
        baseTranslationX: Float,
        baseTranslationY: Float,
    ): Boolean {
        displayedWavePath.rewind()
        if (pathMeasure.length <= EPSILON) {
            return false
        }

        var adjustedStart = start
        var adjustedEnd = end
        var resultTranslationX = baseTranslationX

        if (adjustedWavelength > EPSILON) {
            // Viable wavelength, need to cycle it
            val cycleCount = trackLength / adjustedWavelength
            if (cycleCount > EPSILON) {
                // Viable actual wave count, need to convert phase to a wave segment to draw
                val phaseFractionInPath = phaseFraction / cycleCount
                val ratio = cycleCount / (cycleCount + 1f)
                adjustedStart = (adjustedStart + phaseFractionInPath) * ratio
                adjustedEnd = (adjustedEnd + phaseFractionInPath) * ratio
            }
            // Cycle wave backwards by phase
            resultTranslationX -= phaseFraction * adjustedWavelength
        }

        adjustedStart = adjustedStart.coerceIn(0f, 1f)
        adjustedEnd = adjustedEnd.coerceIn(0f, 1f)
        if (adjustedEnd <= adjustedStart) {
            // Nothing to draw
            return false
        }

        // Get the wave segment we are cycling through
        val startDistance = adjustedStart * pathMeasure.length
        val endDistance = adjustedEnd * pathMeasure.length
        pathMeasure.getSegment(startDistance, endDistance, displayedWavePath, true)
        transform.reset()
        transform.setTranslate(resultTranslationX, baseTranslationY)

        // Then actually scale the wave by the current amplitude
        // (which may not be full due to anims)
        val scaleY = displayedAmplitude
        if (scaleY > 0f) {
            transform.postScale(1f, scaleY, resultTranslationX, baseTranslationY)
        }
        displayedWavePath.transform(transform)

        return true
    }

    private fun applyRampEasing(fraction: Float): Float {
        val clamped = fraction.coerceIn(0f, 1f)
        return clamped * clamped * (3f - 2f * clamped)
    }

    private fun PointF.reset() {
        this.x = 0f
        this.y = 0f
    }

    private fun PointF.translate(x: Float, y: Float) {
        this.x += x
        this.y += y
    }

    private companion object {
        const val MIN_SPRING_VISIBLE_CHANGE = 0.001f
        const val DEFAULT_WAVE_RAMP_PROGRESS_MAX = 0.03f
        const val MIN_VISIBLE_WAVE_FRACTION = 0.001f
        const val MIN_PHASE_FRACTION = 0.0001f
        const val EPSILON = 0.0001f
        const val WAVE_SMOOTHNESS = 0.48f
    }
}
