package org.oxycblt.auxio.widgets.forms

import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.oxycblt.auxio.ui.newMainIntent
import org.oxycblt.auxio.widgets.WidgetState

abstract class WidgetForm(@LayoutRes private val layout: Int) {
    open fun createViews(context: Context, state: WidgetState): RemoteViews {
        val views = RemoteViews(context.packageName, layout)

        views.setOnClickPendingIntent(
            android.R.id.background,
            context.newMainIntent()
        )

        return views
    }
}
