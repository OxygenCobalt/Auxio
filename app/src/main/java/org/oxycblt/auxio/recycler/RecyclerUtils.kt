package org.oxycblt.auxio.recycler

import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.theme.getAccentTransparency
import org.oxycblt.auxio.theme.getDayNightTransparency

// Apply a custom vertical divider
fun RecyclerView.applyDivider() {
    val div = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )

    div.setDrawable(
        ColorDrawable(
            getAccentTransparency(
                context, R.color.divider_color, getDayNightTransparency()
            )
        )
    )

    addItemDecoration(div)
}
