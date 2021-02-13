package org.oxycblt.auxio.ui

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
 * views when animated with a stock LayoutTransition. Adapted from this StackOverflow answer:
 * https://stackoverflow.com/a/35087229
 */
class SlideLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {
    private val disappearingChildrenField: Field? = try {
        ViewGroup::class.java.getDeclaredField("mDisappearingChildren").also {
            it.isAccessible = true
        }
    } catch (e: NoSuchFieldException) {
        logE("Could not get mDisappearingChildren.")
        null
    }

    private var disappearingChildren: List<View>? = null

    private var dumpView: View? = null
    private var doDrawingTrick: Boolean = false

    init {
        if (disappearingChildrenField != null) {
            // Create a junk view and add it, which makes all the magic happen [I think??].
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

        // I have no idea what this code does.
        if (doDrawingTrick && children != null) {
            if (child == dumpView) {
                var more = false
                children.forEach {
                    more = more or super.drawChild(canvas, it, drawingTime)
                }
                return more
            } else if (children.contains(child)) {
                return false
            }
        }

        return super.drawChild(canvas, child, drawingTime)
    }

    private fun beforeDispatchDraw(): Boolean {
        val children = getDisappearingChildren()

        if (children == null || children.isEmpty() || childCount <= 1) { // Junk view included
            return false
        }

        return true
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDisappearingChildren(): List<View>? {
        if (disappearingChildrenField == null || disappearingChildren != null) {
            return disappearingChildren
        }

        // If there is no list of disappearing children yet, attempt to get it.
        try {
            disappearingChildren = disappearingChildrenField.get(this) as List<View>?
        } catch (e: IllegalAccessException) {
            logD("Could not get list of disappearing children.")
        }

        return disappearingChildren
    }
}
