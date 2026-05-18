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
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.R

/**
 * Slider with active-track wave rendering that ports MDC LinearProgressIndicator's wavy draw
 * behavior.
 */
class WavySlider
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = MR.attr.sliderStyle,
) : Slider(context, attrs, defStyleAttr) {
    private val wavePaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
        }

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

    private val startPoint = PathPoint()
    private val endPoint = PathPoint()

    private val transparentTrackTint = ColorStateList.valueOf(Color.TRANSPARENT)
    private var waveTrackTintList: ColorStateList = trackActiveTintList
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
    }

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
        waveTrackTintList = trackColor
        if (!linearActiveTrackSuppressed) {
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
            waveSpeed = configuredSpeedPx
            resetPhaseClock()
            transitionToAmplitudeFraction(1f)
        } else if (abs(currentAmplitudeFraction - MIN_VISIBLE_WAVE_FRACTION) < EPSILON) {
            waveSpeed = 0
            resetPhaseClock()
        } else {
            waveSpeed = 0
            transitionToAmplitudeFraction(MIN_VISIBLE_WAVE_FRACTION) { resetPhaseClock() }
        }
        updateActiveTrackSuppression()
        ensurePhaseTickerState()
        markDirty()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!shouldDrawWave()) {
            return
        }
        val trackLength = trackWidth.toFloat()
        if (trackLength <= 0f) {
            return
        }
        val range = valueTo - valueFrom
        if (range <= 0f) {
            return
        }
        val progressFraction = ((value - valueFrom) / range).coerceIn(0f, 1f)
        if (progressFraction <= 0f) {
            return
        }

        displayedTrackThickness = trackHeight.toFloat()
        displayedCornerRadius = min(displayedTrackThickness / 2f, trackCornerSize.toFloat())
        displayedInnerCornerRadius =
            min(displayedTrackThickness / 2f, trackInsideCornerSize.toFloat())
        displayedAmplitude = configuredAmplitudePx.toFloat()

        ensureCachedWavePath(trackLength, configuredWavelengthPx)
        if (pathMeasure.length <= 0f || adjustedWavelength <= 0f) {
            return
        }

        val activeTrackColor =
            waveTrackTintList.getColorForState(drawableState, waveTrackTintList.defaultColor)
        wavePaint.color = activeTrackColor
        wavePaint.strokeWidth = displayedTrackThickness
        val startRampFraction = calculateStartRampFraction(progressFraction, trackLength)
        val rampedAmplitudeFraction =
            applyStartWaveRamp(
                amplitudeFraction = currentAmplitudeFraction,
                startRampFraction = startRampFraction,
            )
        drawActiveWaveSegment(
            canvas = canvas,
            trackLength = trackLength,
            progressFraction = progressFraction,
            paintColor = activeTrackColor,
            amplitudeFraction = rampedAmplitudeFraction,
            phaseFraction = phaseFraction,
        )
    }

    private fun drawActiveWaveSegment(
        canvas: Canvas,
        trackLength: Float,
        progressFraction: Float,
        paintColor: Int,
        amplitudeFraction: Float,
        phaseFraction: Float,
    ) {
        val clampedProgress = progressFraction.coerceIn(0f, 1f)
        val isRtl = layoutDirection == LAYOUT_DIRECTION_RTL
        val direction = if (isRtl) ActiveTrackDirection.RIGHT else ActiveTrackDirection.LEFT
        val thumbTrackPosition =
            if (isRtl) {
                (1f - clampedProgress) * trackLength
            } else {
                clampedProgress * trackLength
            }

        val startBound =
            if (direction == ActiveTrackDirection.LEFT) {
                -displayedCornerRadius
            } else {
                thumbTrackPosition + thumbTrackGapSize
            }
        val endBound =
            if (direction == ActiveTrackDirection.LEFT) {
                thumbTrackPosition - thumbTrackGapSize
            } else {
                trackLength + displayedCornerRadius
            }
        if (startBound >= endBound) {
            return
        }

        val startCornerRadius = calculateStartTrackCornerSize(thumbTrackPosition)
        val endCornerRadius = calculateEndTrackCornerSize(trackLength, thumbTrackPosition)

        drawSegment(
            canvas = canvas,
            trackLength = trackLength,
            startBound = startBound,
            endBound = endBound,
            startCornerRadius = startCornerRadius,
            endCornerRadius = endCornerRadius,
            paintColor = paintColor,
            amplitudeFraction = amplitudeFraction,
            phaseFraction = phaseFraction,
        )
    }

    private fun drawSegment(
        canvas: Canvas,
        trackLength: Float,
        startBound: Float,
        endBound: Float,
        startCornerRadius: Float,
        endCornerRadius: Float,
        paintColor: Int,
        amplitudeFraction: Float,
        phaseFraction: Float,
    ) {
        val originX = trackSidePadding.toFloat()
        val trackCenterY = height / 2f

        val startBlockCenterX = startBound + startCornerRadius
        val endBlockCenterX = endBound - endCornerRadius
        val startBlockWidth = startCornerRadius * 2f
        val endBlockWidth = endCornerRadius * 2f

        wavePaint.color = paintColor
        wavePaint.isAntiAlias = true
        wavePaint.strokeWidth = displayedTrackThickness

        startPoint.reset()
        endPoint.reset()
        startPoint.translate(startBlockCenterX + originX, trackCenterY)
        endPoint.translate(endBlockCenterX + originX, trackCenterY)

        val drawWavyPath = amplitudeFraction > 0f
        if (
            startBound <= 0f &&
                endBlockCenterX + endCornerRadius < startBlockCenterX + startCornerRadius
        ) {
            drawRoundedBlock(
                canvas = canvas,
                drawCenter = startPoint,
                drawWidth = startBlockWidth,
                drawHeight = displayedTrackThickness,
                drawCornerSize = startCornerRadius,
                clipCenter = endPoint,
                clipWidth = endBlockWidth,
                clipHeight = displayedTrackThickness,
                clipCornerSize = endCornerRadius,
                clipRight = true,
            )
            return
        }

        if (startBlockCenterX - startCornerRadius > endBlockCenterX - endCornerRadius) {
            drawRoundedBlock(
                canvas = canvas,
                drawCenter = endPoint,
                drawWidth = endBlockWidth,
                drawHeight = displayedTrackThickness,
                drawCornerSize = endCornerRadius,
                clipCenter = startPoint,
                clipWidth = startBlockWidth,
                clipHeight = displayedTrackThickness,
                clipCornerSize = startCornerRadius,
                clipRight = false,
            )
            return
        }

        wavePaint.style = Paint.Style.STROKE
        wavePaint.strokeCap = if (useStrokeCap()) Paint.Cap.ROUND else Paint.Cap.BUTT

        if (!drawWavyPath) {
            canvas.drawLine(
                startPoint.posVec.x,
                startPoint.posVec.y,
                endPoint.posVec.x,
                endPoint.posVec.y,
                wavePaint,
            )
        } else {
            calculateDisplayedWavePath(
                trackLength = trackLength,
                start = startBlockCenterX / trackLength,
                end = endBlockCenterX / trackLength,
                amplitudeFraction = amplitudeFraction,
                phaseFraction = phaseFraction,
                trackCenterY = trackCenterY,
            )
            canvas.drawPath(displayedWavePath, wavePaint)
        }

        if (!useStrokeCap()) {
            if (startCornerRadius > 0f) {
                drawRoundedBlock(
                    canvas = canvas,
                    drawCenter = startPoint,
                    drawWidth = startBlockWidth,
                    drawHeight = displayedTrackThickness,
                    drawCornerSize = startCornerRadius,
                )
            }
            if (endCornerRadius > 0f) {
                drawRoundedBlock(
                    canvas = canvas,
                    drawCenter = endPoint,
                    drawWidth = endBlockWidth,
                    drawHeight = displayedTrackThickness,
                    drawCornerSize = endCornerRadius,
                )
            }
        }
    }

    private fun calculateDisplayedWavePath(
        trackLength: Float,
        start: Float,
        end: Float,
        amplitudeFraction: Float,
        phaseFraction: Float,
        trackCenterY: Float,
    ) {
        calculateDisplayedWavePath(
            trackLength = trackLength,
            start = start,
            end = end,
            amplitudeFraction = amplitudeFraction,
            phaseFraction = phaseFraction,
            baseTranslationX = trackSidePadding.toFloat(),
            baseTranslationY = trackCenterY,
        )
    }

    private fun drawRoundedBlock(
        canvas: Canvas,
        drawCenter: PathPoint,
        drawWidth: Float,
        drawHeight: Float,
        drawCornerSize: Float,
    ) {
        drawRoundedBlock(
            canvas = canvas,
            paint = wavePaint,
            drawCenter = drawCenter,
            drawWidth = drawWidth,
            drawHeight = drawHeight,
            drawCornerSize = drawCornerSize,
        )
    }

    private fun drawRoundedBlock(
        canvas: Canvas,
        drawCenter: PathPoint,
        drawWidth: Float,
        drawHeight: Float,
        drawCornerSize: Float,
        clipCenter: PathPoint?,
        clipWidth: Float,
        clipHeight: Float,
        clipCornerSize: Float,
        clipRight: Boolean,
    ) {
        drawRoundedBlock(
            canvas = canvas,
            paint = wavePaint,
            drawCenter = drawCenter,
            drawWidth = drawWidth,
            drawHeight = drawHeight,
            drawCornerSize = drawCornerSize,
            clipCenter = clipCenter,
            clipWidth = clipWidth,
            clipHeight = clipHeight,
            clipCornerSize = clipCornerSize,
            clipRight = clipRight,
        )
    }

    private fun calculateStartTrackCornerSize(thumbTrackPosition: Float): Float {
        if (thumbTrackGapSize <= 0) {
            return displayedCornerRadius
        }
        return if (thumbTrackPosition < displayedCornerRadius) {
            max(thumbTrackPosition, displayedInnerCornerRadius)
        } else {
            displayedCornerRadius
        }
    }

    private fun calculateEndTrackCornerSize(trackLength: Float, thumbTrackPosition: Float): Float {
        if (thumbTrackGapSize <= 0) {
            return displayedCornerRadius
        }
        return if (thumbTrackPosition > trackLength - displayedCornerRadius) {
            max(trackLength - thumbTrackPosition, displayedInnerCornerRadius)
        } else {
            displayedCornerRadius
        }
    }

    private fun useStrokeCap(): Boolean {
        val fullyRounded = abs(displayedCornerRadius - displayedTrackThickness / 2f) < EPSILON
        val sameInnerOuter = abs(displayedInnerCornerRadius - displayedCornerRadius) < EPSILON
        return fullyRounded && sameInnerOuter
    }

    private fun applyStartWaveRamp(amplitudeFraction: Float, startRampFraction: Float): Float {
        if (amplitudeFraction <= 0f) {
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
        if (thresholdPx <= 0f) {
            return progressRamp
        }
        val thresholdFraction = (thresholdPx / trackLength).coerceIn(0f, 1f)
        val fullFraction = (thresholdFraction * 2f).coerceIn(0f, 1f)
        if (fullFraction - thresholdFraction <= EPSILON) {
            return progressRamp
        }
        val edgeRamp =
            ((progressFraction - thresholdFraction) / (fullFraction - thresholdFraction)).coerceIn(
                0f,
                1f,
            )
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
        val clampedTarget = target.coerceIn(MIN_VISIBLE_WAVE_FRACTION, 1f)
        if (abs(currentAmplitudeFraction - clampedTarget) < EPSILON) {
            currentAmplitudeFraction = clampedTarget
            updateActiveTrackSuppression()
            ensurePhaseTickerState()
            onFinished?.invoke()
            return
        }

        waveTransitionAnimation?.cancel()
        waveTransitionAnimation = null

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
                    currentAmplitudeFraction = value.coerceIn(MIN_VISIBLE_WAVE_FRACTION, 1f)
                    ensurePhaseTickerState()
                    invalidate()
                }
                addEndListener { _, canceled, value, _ ->
                    if (waveTransitionAnimation === this) {
                        waveTransitionAnimation = null
                    }
                    currentAmplitudeFraction =
                        if (canceled) {
                            value.coerceIn(MIN_VISIBLE_WAVE_FRACTION, 1f)
                        } else {
                            clampedTarget
                        }
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
            // not actually moving once ramp is taken in, move on
            return
        }

        val nowNanos = System.nanoTime()
        if (lastPhaseFrameNanos != 0L) {
            val deltaSeconds = (nowNanos - lastPhaseFrameNanos) / 1_000_000_000f
            val delta = deltaSeconds * (effectiveSpeed / configuredWavelengthPx.toFloat())
            // move phase forward, take the decimal
            // all incoming inputs (phase & delta) are not-zero so no need
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
        val suppress = shouldDrawWave()
        if (suppress == linearActiveTrackSuppressed) {
            return
        }
        if (suppress) {
            waveTrackTintList = trackActiveTintList
            super.setTrackActiveTintList(transparentTrackTint)
        } else {
            super.setTrackActiveTintList(waveTrackTintList)
        }
        linearActiveTrackSuppressed = suppress
    }

    private enum class ActiveTrackDirection {
        LEFT,
        RIGHT,
    }

    private fun applyRampEasing(fraction: Float): Float {
        val clamped = fraction.coerceIn(0f, 1f)
        return clamped * clamped * (3f - 2f * clamped)
    }

    private class PathPoint(
        val posVec: PointF = PointF(0f, 0f),
        val tanVec: PointF = PointF(1f, 0f),
    ) {
        fun reset() {
            posVec.x = 0f
            posVec.y = 0f
            tanVec.x = 1f
            tanVec.y = 0f
        }

        fun translate(dx: Float, dy: Float) {
            posVec.x += dx
            posVec.y += dy
        }

        fun scale(sx: Float, sy: Float, pivotY: Float = 0f) {
            posVec.x *= sx
            posVec.y = (posVec.y - pivotY) * sy + pivotY
            tanVec.x *= sx
            tanVec.y *= sy
        }
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
        cachedWavePath.rewind()
        adjustedWavelength = 0f
        if (trackLength <= 0f || wavelength <= 0) {
            pathMeasure.setPath(cachedWavePath, false)
            return
        }

        val cycleCount = max(1, (trackLength / wavelength).toInt())
        adjustedWavelength = trackLength / cycleCount
        for (i in 0..cycleCount) {
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
        amplitudeFraction: Float,
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
            val cycleCount = trackLength / adjustedWavelength
            if (cycleCount > EPSILON) {
                val phaseFractionInPath = phaseFraction / cycleCount
                val ratio = cycleCount / (cycleCount + 1f)
                adjustedStart = (adjustedStart + phaseFractionInPath) * ratio
                adjustedEnd = (adjustedEnd + phaseFractionInPath) * ratio
            }
            resultTranslationX -= phaseFraction * adjustedWavelength
        }

        adjustedStart = adjustedStart.coerceIn(0f, 1f)
        adjustedEnd = adjustedEnd.coerceIn(0f, 1f)
        if (adjustedEnd <= adjustedStart) {
            return false
        }

        val startDistance = adjustedStart * pathMeasure.length
        val endDistance = adjustedEnd * pathMeasure.length
        pathMeasure.getSegment(startDistance, endDistance, displayedWavePath, true)

        startPoint.reset()
        val startPosArr = floatArrayOf(startPoint.posVec.x, startPoint.posVec.y)
        val startTanArr = floatArrayOf(startPoint.tanVec.x, startPoint.tanVec.y)
        pathMeasure.getPosTan(
            startDistance,
            startPosArr,
            startTanArr
        )
        startPoint.posVec.x = startPosArr[0]
        startPoint.posVec.y = startPosArr[1]
        startPoint.tanVec.x = startTanArr[0]
        startPoint.tanVec.y = startTanArr[1]

        endPoint.reset()
        val endPosArr = floatArrayOf(endPoint.posVec.x, endPoint.posVec.y)
        val endTanArr = floatArrayOf(endPoint.tanVec.x, endPoint.tanVec.y)
        pathMeasure.getPosTan(
            endDistance,
            endPosArr,
            endTanArr
        )
        endPoint.posVec.x = endPosArr[0]
        endPoint.posVec.y = endPosArr[1]
        endPoint.tanVec.x = endTanArr[0]
        endPoint.tanVec.y = endTanArr[1]
        transform.reset()
        transform.setTranslate(resultTranslationX, baseTranslationY)
        startPoint.translate(resultTranslationX, baseTranslationY)
        endPoint.translate(resultTranslationX, baseTranslationY)

        val scaleY = displayedAmplitude * amplitudeFraction
        if (scaleY > 0f) {
            transform.postScale(1f, scaleY, resultTranslationX, baseTranslationY)
            startPoint.scale(1f, scaleY, baseTranslationY)
            endPoint.scale(1f, scaleY, baseTranslationY)
        }

        displayedWavePath.transform(transform)
        return true
    }

    private fun drawRoundedBlock(
        canvas: Canvas,
        paint: Paint,
        drawCenter: PathPoint,
        drawWidth: Float,
        drawHeight: Float,
        drawCornerSize: Float,
        clipCenter: PathPoint? = null,
        clipWidth: Float = 0f,
        clipHeight: Float = 0f,
        clipCornerSize: Float = 0f,
        clipRight: Boolean = false,
    ) {
        val localDrawHeight = min(drawHeight, displayedTrackThickness)
        var localClipWidth = clipWidth
        var localClipHeight = clipHeight
        var localClipCornerSize = clipCornerSize

        drawRect.set(-drawWidth / 2f, -localDrawHeight / 2f, drawWidth / 2f, localDrawHeight / 2f)
        paint.style = Paint.Style.FILL
        canvas.withSave {
            if (clipCenter != null) {
                localClipHeight = min(localClipHeight, displayedTrackThickness)
                localClipCornerSize =
                    min(
                        localClipWidth / 2f,
                        localClipCornerSize * localClipHeight / displayedTrackThickness,
                    )
                if (clipRight) {
                    val leftEdgeDiff =
                        (clipCenter.posVec.x - localClipCornerSize) -
                            (drawCenter.posVec.x - drawCornerSize)
                    if (leftEdgeDiff > 0f) {
                        clipCenter.translate(-leftEdgeDiff / 2f, 0f)
                        localClipWidth += leftEdgeDiff
                    }
                    patchRect.set(0f, -localDrawHeight / 2f, drawWidth / 2f, localDrawHeight / 2f)
                } else {
                    val rightEdgeDiff =
                        (clipCenter.posVec.x + localClipCornerSize) -
                            (drawCenter.posVec.x + drawCornerSize)
                    if (rightEdgeDiff < 0f) {
                        clipCenter.translate(-rightEdgeDiff / 2f, 0f)
                        localClipWidth -= rightEdgeDiff
                    }
                    patchRect.set(-drawWidth / 2f, -localDrawHeight / 2f, 0f, localDrawHeight / 2f)
                }

                clipRect.set(
                    -localClipWidth / 2f,
                    -localClipHeight / 2f,
                    localClipWidth / 2f,
                    localClipHeight / 2f,
                )
                translate(clipCenter.posVec.x, clipCenter.posVec.y)
                rotate(vectorToCanvasRotation(clipCenter.tanVec))
                roundedRectPath.reset()
                roundedRectPath.addRoundRect(
                    clipRect,
                    localClipCornerSize,
                    localClipCornerSize,
                    Path.Direction.CCW,
                )
                clipPath(roundedRectPath)

                rotate(-vectorToCanvasRotation(clipCenter.tanVec))
                translate(-clipCenter.posVec.x, -clipCenter.posVec.y)
                translate(drawCenter.posVec.x, drawCenter.posVec.y)
                rotate(vectorToCanvasRotation(drawCenter.tanVec))
                drawRect(patchRect, paint)
                drawRoundRect(drawRect, drawCornerSize, drawCornerSize, paint)
            } else {
                translate(drawCenter.posVec.x, drawCenter.posVec.y)
                rotate(vectorToCanvasRotation(drawCenter.tanVec))
                drawRoundRect(drawRect, drawCornerSize, drawCornerSize, paint)
            }
        }
    }

    private fun vectorToCanvasRotation(vector: PointF): Float =
        Math.toDegrees(atan2(vector.y, vector.x).toDouble()).toFloat()

    private companion object {
        const val MIN_SPRING_VISIBLE_CHANGE = 0.001f
        const val DEFAULT_WAVE_RAMP_PROGRESS_MAX = 0.03f
        const val MIN_VISIBLE_WAVE_FRACTION = 0.001f
        const val MIN_PHASE_FRACTION = 0.0001f
        const val EPSILON = 0.0001f
        const val WAVE_SMOOTHNESS = 0.48f
    }
}
