/*
 * Copyright (c) 2021 Auxio Project
 * ViewUtil.kt is part of Auxio.
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

package org.oxycblt.auxio.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.PlaybackViewModel

/**
 * Apply a [MaterialShapeDrawable] to this view, automatically initializing the elevation overlay
 * and setting the fill color. The [View]'s current elevation will be applied to the drawable.
 * This functions assumes that the background is a [ColorDrawable] and will crash if not.
 */
fun View.applyMaterialDrawable() {
    check(background is ColorDrawable) { "Background was not defined as a solid color" }

    background = MaterialShapeDrawable.createWithElevationOverlay(context).apply {
        elevation = this@applyMaterialDrawable.elevation
        fillColor = ColorStateList.valueOf((background as ColorDrawable).color)
    }
}

/**
 * Apply the recommended spans for a [RecyclerView].
 *
 * @param shouldBeFullWidth Optional callback for determining whether an item should be full-width,
 * regardless of spans
 */
fun RecyclerView.applySpans(shouldBeFullWidth: ((Int) -> Boolean)? = null) {
    val spans = if (context.isLandscape()) {
        if (context.isXLTablet()) 3 else 2
    } else {
        if (context.isXLTablet()) 2 else 1
    }

    if (spans > 1) {
        val mgr = GridLayoutManager(context, spans)

        if (shouldBeFullWidth != null) {
            mgr.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (shouldBeFullWidth(position)) spans else 1
                }
            }
        }

        layoutManager = mgr
    }
}

/**
 * Returns whether a recyclerview can scroll.
 */
fun RecyclerView.canScroll(): Boolean = computeVerticalScrollRange() > height

/**
 * Resolve a color.
 * @param context [Context] required
 * @return The resolved color, black if the resolving process failed.
 */
@ColorInt
fun @receiver:ColorRes Int.resolveColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Resources.NotFoundException) {
        logE("Attempted color load failed: ${e.stackTraceToString()}")

        // Default to the emergency color [Black] if the loading fails.
        ContextCompat.getColor(context, android.R.color.black)
    }
}

/**
 * Resolve a color and turn it into a [ColorStateList]
 * @param context [Context] required
 * @return The resolved color as a [ColorStateList]
 * @see resolveColor
 */
fun @receiver:ColorRes Int.resolveStateList(context: Context) =
    ContextCompat.getColorStateList(context, this)

/*
 * Resolve a color and turn it into a [ColorStateList]
 * @param context [Context] required
 * @return The resolved color as a [ColorStateList]
 * @see resolveColor
 */
fun @receiver:DrawableRes Int.resolveDrawable(context: Context) =
    requireNotNull(ContextCompat.getDrawable(context, this))

/**
 * Resolve this int into a color as if it was an attribute
 */
@ColorInt
fun @receiver:AttrRes Int.resolveAttr(context: Context): Int {
    // First resolve the attribute into its ID
    val resolvedAttr = TypedValue()
    context.theme.resolveAttribute(this, resolvedAttr, true)

    // Then convert it to a proper color
    val color = if (resolvedAttr.resourceId != 0) {
        resolvedAttr.resourceId
    } else {
        resolvedAttr.data
    }

    return color.resolveColor(context)
}

/**
 * Apply edge-to-edge tweaks to the root of a [ViewBinding].
 * @param onApply What to do when the system bar insets are provided
 */
fun ViewBinding.applyEdge(onApply: (Rect) -> Unit) {
    root.applyEdge(onApply)
}

/**
 * Apply edge-to-edge tweaks to a [View].
 * @param onApply What to do when the system bar insets are provided
 */
fun View.applyEdge(onApply: (Rect) -> Unit) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            setOnApplyWindowInsetsListener { _, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars()).run {
                    Rect(left, top, right, bottom)
                }

                onApply(bars)

                insets
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
            setOnApplyWindowInsetsListener { _, insets ->
                @Suppress("DEPRECATION")
                val bars = Rect(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )

                onApply(bars)
                insets
            }
        }

        // Not on a version that supports edge-to-edge [yet], don't do anything
    }
}

/**
 * Stopgap measure to make edge-to-edge work on views that also have a playback bar.
 * The issue is that while we can apply padding initially, the padding will still be applied
 * when the bar is shown, which is very ungood. We mitigate this by just checking the song state
 * and removing the padding if there is one, which is a stupidly fragile band-aid but it
 * works.
 *
 * TODO: Dumpster this and replace it with a dedicated layout. Only issue with that is how
 *  nested our layouts are, which basically forces us to do recursion magic. Hai Zhang's Material
 *  Files layout may help in this task.
 */
fun View.applyEdgeRespectingBar(
    playbackModel: PlaybackViewModel,
    viewLifecycleOwner: LifecycleOwner,
    initialPadding: Int = 0
) {
    var bottomPadding = 0

    applyEdge {
        bottomPadding = it.bottom

        if (playbackModel.song.value == null) {
            updatePadding(bottom = bottomPadding)
        } else {
            updatePadding(bottom = initialPadding)
        }
    }

    playbackModel.song.observe(viewLifecycleOwner) { song ->
        if (song == null) {
            updatePadding(bottom = bottomPadding)
        } else {
            updatePadding(bottom = initialPadding)
        }
    }
}
