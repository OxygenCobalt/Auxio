package org.oxycblt.auxio.settings.accent

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

/**
 * A sub-class of [GridLayoutManager] that automatically sets the spans so that they fit the width
 * of the RecyclerView.
 * Adapted from this StackOverflow answer: https://stackoverflow.com/a/30256880/14143986
 */
class AutoGridLayoutManager(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) : GridLayoutManager(context, attrs, defStyleAttr, defStyleRes) {
    // We use 72dp here since that's the rough size of the accent item.
    // This will need to be modified if this is used beyond the accent dialog.
    private var columnWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 72F, context.resources.displayMetrics
    ).toInt()

    private var lastWidth = -1
    private var lastHeight = -1

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (width > 0 && height > 0 && (lastWidth != width || lastHeight != height)) {
            val totalSpace = width - paddingRight - paddingLeft
            val spanCount = max(1, totalSpace / columnWidth)

            setSpanCount(spanCount)
        }

        lastWidth = width
        lastHeight = height

        super.onLayoutChildren(recycler, state)
    }
}
