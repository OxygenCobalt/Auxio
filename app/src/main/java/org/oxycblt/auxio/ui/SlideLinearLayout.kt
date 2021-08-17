/*
 * Copyright (c) 2021 Auxio Project
 * SlideLinearLayout.kt is part of Auxio.
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

package org.oxycblt.auxio.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import java.lang.reflect.Field

/**
 * Hack layout that fixes an issue where disappearing views would draw over non-disappearing
 * views when animated with a stock LayoutTransition. If something breaks on the playback controls
 * or nav bar, this is probably the culprit.
 *
 * "But oxygencobalt, couldn't you just write your own animation code and run that instea-"
 * **NO.**
 *
 * Adapted from this StackOverflow answer:
 * https://stackoverflow.com/a/35087229
 * @author OxygenCobalt
 */
class SlideLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {
    @SuppressLint("DiscouragedPrivateApi")
    private val disappearingChildrenField: Field? = try {
        // We need to read this field to correctly draw the disappearing children
        // If google ever hides this API I am going to scream, because its their busted
        // ViewGroup code that forces me to do this in the first place
        ViewGroup::class.java.getDeclaredField("mDisappearingChildren").also {
            it.isAccessible = true
        }
    } catch (e: NoSuchFieldException) {
        logE("Could not get mDisappearingChildren. This is very ungood.")
        null
    }

    private var disappearingChildren: List<View>? = null

    private var dumpView: View? = null
    private var doDrawingTrick: Boolean = false

    init {
        if (disappearingChildrenField != null) {
            // Create a invisible junk view and add it, which makes all the magic happen [I think].
            dumpView = View(context)
            addView(dumpView, 0, 0)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        doDrawingTrick = beforeDispatchDraw()
        super.dispatchDraw(canvas)

        if (doDrawingTrick) {
            doDrawingTrick = false
        }
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val children = getDisappearingChildren()

        if (doDrawingTrick && children != null) {
            // Use the dump view as a marker for when to do the trick
            if (child == dumpView) {
                // I dont even know what this does.
                var consumed = false

                children.forEach { view ->
                    consumed = consumed || super.drawChild(canvas, view, drawingTime)
                }

                return consumed
            } else if (children.contains(child)) {
                // Ignore the disappearing children
                return false
            }
        }

        return super.drawChild(canvas, child, drawingTime)
    }

    private fun beforeDispatchDraw(): Boolean {
        val children = getDisappearingChildren()

        // Dont do trick if there are no disappearing children or if there arent any children other
        // than the dump view.
        if (children == null || children.isEmpty() || childCount <= 1) {
            return false
        }

        return true
    }

    private fun getDisappearingChildren(): List<View>? {
        if (disappearingChildrenField == null || disappearingChildren != null) {
            return disappearingChildren
        }

        try {
            @Suppress("UNCHECKED_CAST")
            disappearingChildren = disappearingChildrenField.get(this) as List<View>?
        } catch (e: Exception) {
            logD("Could not get list of disappearing children.")
        }

        return disappearingChildren
    }
}
