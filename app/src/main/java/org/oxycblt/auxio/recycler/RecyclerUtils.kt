package org.oxycblt.auxio.recycler

import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.theme.getDayNightAlpha
import org.oxycblt.auxio.theme.getTransparentAccent

// Apply a custom vertical divider
fun RecyclerView.applyDivider() {
    val div = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )

    div.setDrawable(
        ColorDrawable(
            getTransparentAccent(
                context, R.color.divider_color, getDayNightAlpha()
            )
        )
    )

    addItemDecoration(div)
}
