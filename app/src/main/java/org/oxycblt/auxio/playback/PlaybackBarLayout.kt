/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackBarLayout.kt is part of Auxio.
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

import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.children
import androidx.core.view.updatePadding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A layout that manages the bottom playback bar while still enabling edge-to-edge to work
 * properly. The mechanism is mostly inspired by Material Files' PersistentBarLayout, however
 * this class was primarily written by me and I plan to expand this layout to become part of
 * the playback navigation process.
 *
 * TODO: Implement animation
 * TODO: Implement the swipe-up behavior. This needs to occur, as the way the main fragment
 *  saves state results in'
 */
class PlaybackBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val playbackView = CompactPlaybackView(context)
    private var lastInsets: WindowInsets? = null

    init {
        addView(playbackView)

        (playbackView.layoutParams as LayoutParams).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isBar = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)

        val barParams = playbackView.layoutParams as LayoutParams

        val barWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, barParams.width)
        val barHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, barParams.height)
        playbackView.measure(barWidthSpec, barHeightSpec)

        updateWindowInsets()

        val barHeightAdjusted = (playbackView.measuredHeight * barParams.offset).toInt()

        val contentWidth = measuredWidth
        val contentHeight = measuredHeight - barHeightAdjusted

        for (child in children) {
            if (child.visibility == View.GONE) continue

            val childParams = child.layoutParams as LayoutParams

            if (!childParams.isBar) {
                val childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY)
                val childHeightSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)

                child.measure(childWidthSpec, childHeightSpec)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val barHeight = playbackView.measuredHeight
        val barHeightAdjusted = (barHeight * (playbackView.layoutParams as LayoutParams).offset).toInt()

        for (child in children) {
            if (child.visibility == View.GONE) continue

            val childParams = child.layoutParams as LayoutParams

            if (childParams.isBar) {
                child.layout(
                    0, height - barHeightAdjusted,
                    width, height + (barHeight - barHeightAdjusted)
                )
            } else {
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        playbackView.updatePadding(bottom = insets.systemBarsCompat.bottom)

        lastInsets = insets
        updateWindowInsets()

        return insets
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        playbackView.clearCallback()
    }

    private fun updateWindowInsets() {
        val insets = lastInsets

        if (insets != null) {
            val adjustedInsets = adjustInsets(insets)

            for (child in children) {
                child.dispatchApplyWindowInsets(adjustedInsets)
            }
        }
    }

    private fun adjustInsets(insets: WindowInsets): WindowInsets {
        val barParams = playbackView.layoutParams as LayoutParams
        val childConsumedInset = (playbackView.measuredHeight * barParams.offset).toInt()

        val bars = insets.systemBarsCompat

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                WindowInsets.Builder(insets)
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        Insets.of(
                            bars.left, bars.top,
                            bars.right, (bars.bottom - childConsumedInset).coerceAtLeast(0)
                        )
                    )
                    .build()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                @Suppress("DEPRECATION")
                insets.replaceSystemWindowInsets(
                    bars.left, bars.top,
                    bars.right, (bars.bottom - childConsumedInset).coerceAtLeast(0)
                )
            }

            else -> insets
        }
    }

    fun setSong(song: Song?) {
        if (song != null) {
            showBar()
            playbackView.setSong(song)
        } else {
            hideBar()
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        playbackView.setPlaying(isPlaying)
    }

    fun setPosition(position: Long) {
        playbackView.setPosition(position)
    }

    fun setActionCallback(callback: ActionCallback) {
        playbackView.setCallback(callback)
    }

    private fun showBar() {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.offset == 1f) {
            return
        }

        barParams.offset = 1f

        if (isLaidOut) {
            updateWindowInsets()
        }

        invalidate()
    }

    private fun hideBar() {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.offset == 0f) {
            return
        }

        barParams.offset = 0f

        if (isLaidOut) {
            updateWindowInsets()
        }

        invalidate()
    }

    // --- LAYOUT PARAMS ---

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(
        layoutParams: ViewGroup.LayoutParams
    ): ViewGroup.LayoutParams =
        when (layoutParams) {
            is LayoutParams -> LayoutParams(layoutParams)
            is MarginLayoutParams -> LayoutParams(layoutParams)
            else -> LayoutParams(layoutParams)
        }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams =
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun checkLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean =
        layoutParams is LayoutParams && super.checkLayoutParams(layoutParams)

    @Suppress("UNUSED")
    class LayoutParams : ViewGroup.LayoutParams {
        var isBar = false
        var offset = 0f

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: LayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }

    interface ActionCallback {
        fun onPlayPauseClick()
        fun onNavToItem()
        fun onNavToPlayback()
    }
}
