/*
 * Copyright (c) 2018 Auxio Project
 * ThemedSpeedDialView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.FloatRange
import androidx.core.os.BundleCompat
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.google.android.material.R as MR
import com.google.android.material.motion.MotionUtils
import com.google.android.material.shape.MaterialShapeDrawable
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlin.math.roundToInt
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getAttrResourceId
import org.oxycblt.auxio.util.getDimen
import org.oxycblt.auxio.util.getDimenPixels

/**
 * Customized Speed Dial view with some bug fixes and Material 3 theming.
 *
 * Adapted from Material Files:
 * https://github.com/zhanghai/MaterialFiles/tree/79f1727cec72a6a089eb495f79193f87459fc5e3
 *
 * MODIFICATIONS:
 * - Removed dynamic theme changes based on the MaterialFile's Material 3 setting
 * - Adapted code to the extensions in this project
 *
 * @author Hai Zhang, Alexander Capehart (OxygenCobalt)
 */
class ThemedSpeedDialView : SpeedDialView {
    private var mainFabAnimation: MainFabAnimation? = null
    private val spacingSmall = context.getDimenPixels(R.dimen.spacing_small)
    private var innerChangeListener: ((Boolean) -> Unit)? = null
    private val mainFabDrawable = RotatingDrawable(checkNotNull(mainFab.drawable).mutate())
    private val mainFabSpatialSpring =
        MotionUtils.resolveThemeSpringForce(
            context,
            MR.attr.motionSpringFastSpatial,
            MR.style.Motion_Material3_Spring_Standard_Fast_Spatial,
        )
    private val mainFabEffectsSpring =
        MotionUtils.resolveThemeSpringForce(
            context,
            MR.attr.motionSpringFastEffects,
            MR.style.Motion_Material3_Spring_Standard_Fast_Effects,
        )
    private val argbEvaluator = ArgbEvaluator()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
    ) : super(context, attrs, defStyleAttr)

    init {
        // Work around ripple bug on Android 12 when useCompatPadding = true.
        // @see https://github.com/material-components/material-components-android/issues/2617
        mainFab.apply {
            updateLayoutParams<MarginLayoutParams> {
                setMargins(context.getDimenPixels(R.dimen.spacing_medium))
            }
            useCompatPadding = false
        }
        val context = context
        mainFabClosedBackgroundColor =
            context
                .getAttrColorCompat(com.google.android.material.R.attr.colorPrimaryContainer)
                .defaultColor
        mainFabClosedIconColor =
            context
                .getAttrColorCompat(com.google.android.material.R.attr.colorOnPrimaryContainer)
                .defaultColor
        mainFabOpenedBackgroundColor =
            context.getAttrColorCompat(androidx.appcompat.R.attr.colorPrimary).defaultColor
        mainFabOpenedIconColor =
            context
                .getAttrColorCompat(com.google.android.material.R.attr.colorOnPrimary)
                .defaultColor

        // Always use our own animation to fix the library issue that ripple is rotated as well.
        mainFabAnimationRotateAngle = 0f
        setMainFabClosedDrawable(mainFabDrawable)
        setMainFabOpenedDrawable(null)
        setOnChangeListener(
            object : OnChangeListener {
                override fun onMainActionSelected(): Boolean = false

                override fun onToggleChanged(isOpen: Boolean) {
                    mainFabAnimation?.cancel()
                    mainFab.backgroundTintList =
                        ColorStateList.valueOf(
                            if (isOpen) mainFabClosedBackgroundColor
                            else mainFabOpenedBackgroundColor
                        )
                    mainFab.imageTintList =
                        ColorStateList.valueOf(
                            if (isOpen) mainFabClosedIconColor else mainFabOpenedIconColor
                        )
                    mainFabAnimation = createMainFabAnimation(isOpen).apply { start() }
                    innerChangeListener?.invoke(isOpen)
                }
            }
        )
    }

    private fun createMainFabAnimation(isOpen: Boolean): MainFabAnimation {
        val targetBackgroundTint =
            if (isOpen) mainFabOpenedBackgroundColor else mainFabClosedBackgroundColor
        val targetImageTint = if (isOpen) mainFabOpenedIconColor else mainFabClosedIconColor

        val backgroundTintAnimation =
            createColorSpringAnimation(
                startColor =
                    if (isOpen) mainFabClosedBackgroundColor else mainFabOpenedBackgroundColor,
                endColor = targetBackgroundTint,
            ) {
                mainFab.backgroundTintList = ColorStateList.valueOf(it)
            }
        val imageTintAnimation =
            createColorSpringAnimation(
                startColor = if (isOpen) mainFabClosedIconColor else mainFabOpenedIconColor,
                endColor = targetImageTint,
            ) {
                mainFab.imageTintList = ColorStateList.valueOf(it)
            }
        val rotationAnimation =
            createSpringAnimation(
                startValue = mainFabDrawable.rotationDegrees,
                finalValue = if (isOpen) MAIN_FAB_OPEN_ROTATION_DEGREES else 0f,
                springTemplate = mainFabSpatialSpring,
                minimumVisibleChange = MAIN_FAB_ROTATION_MIN_VISIBLE_CHANGE,
                dampingRatioOverride = MAIN_FAB_ROTATION_DAMPING_RATIO_OVERRIDE,
            ) {
                mainFabDrawable.rotationDegrees = it
            }

        return MainFabAnimation(
            listOf(backgroundTintAnimation, imageTintAnimation, rotationAnimation)
        ) {
            mainFabAnimation = null
        }
    }

    private fun createColorSpringAnimation(
        startColor: Int,
        endColor: Int,
        update: (Int) -> Unit,
    ): SpringAnimation =
        createSpringAnimation(
            startValue = 0f,
            finalValue = 1f,
            springTemplate = mainFabEffectsSpring,
            minimumVisibleChange = MAIN_FAB_COLOR_PROGRESS_MIN_VISIBLE_CHANGE,
        ) { progress ->
            val color =
                argbEvaluator.evaluate(progress.coerceIn(0f, 1f), startColor, endColor) as Int
            update(color)
        }

    private fun createSpringAnimation(
        startValue: Float,
        finalValue: Float,
        springTemplate: SpringForce,
        minimumVisibleChange: Float,
        dampingRatioOverride: Float? = null,
        update: (Float) -> Unit,
    ): SpringAnimation =
        SpringAnimation(FloatValueHolder(startValue)).apply {
            spring =
                SpringForce().apply {
                    dampingRatio = dampingRatioOverride ?: springTemplate.dampingRatio
                    stiffness = springTemplate.stiffness
                    finalPosition = finalValue
                }
            setStartValue(startValue)
            setMinimumVisibleChange(minimumVisibleChange)
            addUpdateListener { _, value, _ -> update(value) }
            addEndListener { _, canceled, value, _ -> update(if (canceled) value else finalValue) }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val overlayLayout = overlayLayout
        if (overlayLayout != null) {
            val surfaceColor =
                context.getAttrColorCompat(com.google.android.material.R.attr.colorSurface)
            val overlayColor = surfaceColor.defaultColor.withModulatedAlpha(0.87f)
            overlayLayout.setBackgroundColor(overlayColor)
        }
        // Fix default margins added by library
        (mainFab.layoutParams as LayoutParams).setMargins(0, 0, 0, 0)
    }

    private fun Int.withModulatedAlpha(
        @FloatRange(from = 0.0, to = 1.0) alphaModulation: Float
    ): Int {
        val alpha = (alpha * alphaModulation).roundToInt()
        return ((alpha shl 24) or (this and 0x00FFFFFF))
    }

    override fun addActionItem(
        actionItem: SpeedDialActionItem,
        position: Int,
        animate: Boolean,
    ): FabWithLabelView? {
        val context = context
        val fabImageTintColor = context.getAttrColorCompat(androidx.appcompat.R.attr.colorPrimary)
        val fabBackgroundColor =
            context.getAttrColorCompat(com.google.android.material.R.attr.colorSurface)
        val labelColor = context.getAttrColorCompat(android.R.attr.textColorSecondary)
        val labelBackgroundColor =
            context.getAttrColorCompat(com.google.android.material.R.attr.colorSurface)
        val labelElevation =
            context.getDimen(com.google.android.material.R.dimen.m3_card_elevated_elevation)
        val cornerRadius = context.getDimenPixels(R.dimen.spacing_medium)
        val actionItem =
            SpeedDialActionItem.Builder(
                    actionItem.id,
                    // Should not be a resource, pass null to fail fast.
                    actionItem.getFabImageDrawable(null),
                )
                .setLabel(actionItem.getLabel(context))
                .setFabImageTintColor(fabImageTintColor.defaultColor)
                .setFabBackgroundColor(fabBackgroundColor.defaultColor)
                .setLabelColor(labelColor.defaultColor)
                .setLabelBackgroundColor(labelBackgroundColor.defaultColor)
                .setLabelClickable(actionItem.isLabelClickable)
                .setTheme(actionItem.theme)
                .create()
        return super.addActionItem(actionItem, position, animate)?.apply {
            fab.apply {
                updateLayoutParams<MarginLayoutParams> {
                    val rightMargin = context.getDimenPixels(R.dimen.spacing_tiny)
                    if (position == actionItems.lastIndex) {
                        val bottomMargin = context.getDimenPixels(R.dimen.spacing_small)
                        setMargins(0, 0, rightMargin, bottomMargin)
                    } else {
                        setMargins(0, 0, rightMargin, 0)
                    }
                }
                useCompatPadding = false
            }

            labelBackground.apply {
                updateLayoutParams<MarginLayoutParams> {
                    if (position == actionItems.lastIndex) {
                        val bottomMargin = context.getDimenPixels(R.dimen.spacing_small)
                        setMargins(0, 0, rightMargin, bottomMargin)
                    }
                }
                useCompatPadding = false
                setContentPadding(spacingSmall, spacingSmall, spacingSmall, spacingSmall)
                background =
                    MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                        fillColor = labelBackgroundColor
                        elevation = labelElevation
                        setCornerSize(cornerRadius.toFloat())
                    }
                foreground = null
                (getChildAt(0) as TextView).apply {
                    TextViewCompat.setTextAppearance(
                        this,
                        context.getAttrResourceId(
                            com.google.android.material.R.attr.textAppearanceLabelLargeEmphasized
                        ),
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState =
            BundleCompat.getParcelable(
                super.onSaveInstanceState() as Bundle,
                "superState",
                Parcelable::class.java,
            )
        return State(superState, isOpen)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        state as State
        super.onRestoreInstanceState(state.superState)
        if (state.isOpen) {
            toggle(false)
        }
    }

    fun setChangeListener(listener: ((Boolean) -> Unit)?) {
        innerChangeListener = listener
    }

    @Parcelize private class State(val superState: Parcelable?, val isOpen: Boolean) : Parcelable

    private class MainFabAnimation(
        private val animations: List<SpringAnimation>,
        private val onEnd: () -> Unit,
    ) {
        private var remainingAnimations = animations.size
        private var finished = false

        init {
            animations.forEach { animation ->
                animation.addEndListener { _, _, _, _ ->
                    if (finished) {
                        return@addEndListener
                    }
                    remainingAnimations -= 1
                    if (remainingAnimations == 0) {
                        finished = true
                        onEnd()
                    }
                }
            }
        }

        fun start() {
            animations.forEach { animation ->
                animation.animateToFinalPosition(animation.spring.finalPosition)
            }
        }

        fun cancel() {
            if (finished) {
                return
            }
            finished = true
            animations.forEach { it.cancel() }
            onEnd()
        }
    }
}

private class RotatingDrawable(drawable: Drawable) : Drawable(), Drawable.Callback {
    private var wrappedDrawable = drawable

    var rotationDegrees = 0f
        set(value) {
            if (field == value) {
                return
            }
            field = value
            invalidateSelf()
        }

    init {
        wrappedDrawable.callback = this
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty) {
            return
        }

        val saveCount = canvas.save()
        canvas.rotate(rotationDegrees, bounds.exactCenterX(), bounds.exactCenterY())
        wrappedDrawable.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onBoundsChange(bounds: Rect) {
        wrappedDrawable.bounds = bounds
    }

    override fun onStateChange(state: IntArray): Boolean {
        val changed = wrappedDrawable.setState(state)
        if (changed) {
            invalidateSelf()
        }
        return changed
    }

    override fun isStateful(): Boolean = wrappedDrawable.isStateful

    override fun getPadding(padding: Rect): Boolean = wrappedDrawable.getPadding(padding)

    override fun getIntrinsicWidth(): Int = wrappedDrawable.intrinsicWidth

    override fun getIntrinsicHeight(): Int = wrappedDrawable.intrinsicHeight

    override fun setAlpha(alpha: Int) {
        wrappedDrawable.alpha = alpha
    }

    override fun getAlpha(): Int = wrappedDrawable.alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        wrappedDrawable.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun setTint(tintColor: Int) {
        wrappedDrawable.setTint(tintColor)
    }

    override fun setTintList(tint: ColorStateList?) {
        wrappedDrawable.setTintList(tint)
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        wrappedDrawable.setTintMode(tintMode)
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean =
        super.setVisible(visible, restart) || wrappedDrawable.setVisible(visible, restart)

    override fun onLevelChange(level: Int): Boolean = wrappedDrawable.setLevel(level)

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }
}

private const val MAIN_FAB_OPEN_ROTATION_DEGREES = 135f
private const val MAIN_FAB_ROTATION_MIN_VISIBLE_CHANGE = 0.1f
private const val MAIN_FAB_ROTATION_DAMPING_RATIO_OVERRIDE = 0.6f
private const val MAIN_FAB_COLOR_PROGRESS_MIN_VISIBLE_CHANGE = 1f / 255f
