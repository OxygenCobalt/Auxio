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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.FloatRange
import androidx.core.os.BundleCompat
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
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
    private var mainFabAnimator: Animator? = null
    private val spacingSmall = context.getDimenPixels(R.dimen.spacing_small)
    private var innerChangeListener: ((Boolean) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private val matInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context, MR.attr.motionEasingStandardInterpolator, FastOutSlowInInterpolator())

    private val matDuration =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium2, 350)

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
        val mainFabDrawable =
            RotateDrawable().apply {
                drawable = mainFab.drawable
                toDegrees = 45f + 90f
            }
        mainFabAnimationRotateAngle = 0f
        setMainFabClosedDrawable(mainFabDrawable)
        setOnChangeListener(
            object : OnChangeListener {
                override fun onMainActionSelected(): Boolean = false

                override fun onToggleChanged(isOpen: Boolean) {
                    mainFab.backgroundTintList =
                        ColorStateList.valueOf(
                            if (isOpen) mainFabClosedBackgroundColor
                            else mainFabOpenedBackgroundColor)
                    mainFab.imageTintList =
                        ColorStateList.valueOf(
                            if (isOpen) mainFabClosedIconColor else mainFabOpenedIconColor)
                    mainFabAnimator?.cancel()
                    mainFabAnimator =
                        createMainFabAnimator(isOpen).apply {
                            addListener(
                                object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        mainFabAnimator = null
                                    }
                                })
                            start()
                        }
                    innerChangeListener?.invoke(isOpen)
                }
            })
    }

    private fun createMainFabAnimator(isOpen: Boolean): Animator {
        val totalDuration = matDuration.toLong()
        val partialDuration = totalDuration / 2 // This is half of the total duration
        val delay = totalDuration / 4 // This is one fourth of the total duration

        val backgroundTintAnimator =
            ObjectAnimator.ofArgb(
                    mainFab,
                    VIEW_PROPERTY_BACKGROUND_TINT,
                    if (isOpen) mainFabOpenedBackgroundColor else mainFabClosedBackgroundColor)
                .apply {
                    startDelay = delay
                    duration = partialDuration
                }

        val imageTintAnimator =
            ObjectAnimator.ofArgb(
                    mainFab,
                    IMAGE_VIEW_PROPERTY_IMAGE_TINT,
                    if (isOpen) mainFabOpenedIconColor else mainFabClosedIconColor)
                .apply {
                    startDelay = delay
                    duration = partialDuration
                }

        val levelAnimator =
            ObjectAnimator.ofInt(
                    mainFab.drawable, DRAWABLE_PROPERTY_LEVEL, if (isOpen) 10000 else 0)
                .apply { duration = totalDuration }

        val animatorSet =
            AnimatorSet().apply {
                playTogether(backgroundTintAnimator, imageTintAnimator, levelAnimator)
                interpolator = matInterpolator
            }
        animatorSet.start()
        return animatorSet
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
        animate: Boolean
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
                    actionItem.getFabImageDrawable(null))
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
                    val horizontalMargin = context.getDimenPixels(R.dimen.spacing_mid_large)
                    setMargins(horizontalMargin, 0, horizontalMargin, 0)
                }
                useCompatPadding = false
            }

            labelBackground.apply {
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
                    TextViewCompat.setTextAppearance(this, R.style.TextAppearance_Auxio_LabelLarge)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState =
            BundleCompat.getParcelable(
                super.onSaveInstanceState() as Bundle, "superState", Parcelable::class.java)
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

    companion object {
        private val VIEW_PROPERTY_BACKGROUND_TINT =
            object : Property<View, Int>(Int::class.java, "backgroundTint") {
                override fun get(view: View): Int = view.backgroundTintList!!.defaultColor

                override fun set(view: View, value: Int?) {
                    view.backgroundTintList = ColorStateList.valueOf(value!!)
                }
            }

        private val IMAGE_VIEW_PROPERTY_IMAGE_TINT =
            object : Property<ImageView, Int>(Int::class.java, "imageTint") {
                override fun get(view: ImageView): Int = view.imageTintList!!.defaultColor

                override fun set(view: ImageView, value: Int?) {
                    view.imageTintList = ColorStateList.valueOf(value!!)
                }
            }

        private val DRAWABLE_PROPERTY_LEVEL =
            object : Property<Drawable, Int>(Int::class.java, "level") {
                override fun get(drawable: Drawable): Int = drawable.level

                override fun set(drawable: Drawable, value: Int?) {
                    drawable.level = value!!
                }
            }
    }

    @Parcelize private class State(val superState: Parcelable?, val isOpen: Boolean) : Parcelable
}
