package org.oxycblt.auxio.recycler

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R

// Apply a custom vertical divider
fun RecyclerView.applyDivider() {
    val div = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )

    div.setDrawable(
        ColorDrawable(
            getDividerDrawable(this)
        )
    )

    addItemDecoration(div)
}

private fun getDividerDrawable(recycler: RecyclerView): Int {
    val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    // Depending on the theme use a different opacity for the divider
    val alpha = if (isDark) 45 else 85

    return ColorUtils.setAlphaComponent(
        ContextCompat.getColor(recycler.context, R.color.divider_color),
        alpha
    )
}
