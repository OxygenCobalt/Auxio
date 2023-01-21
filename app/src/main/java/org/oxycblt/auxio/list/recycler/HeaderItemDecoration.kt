/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.list.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.BackportMaterialDividerItemDecoration
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.adapter.DiffAdapter

/**
 * A [BackportMaterialDividerItemDecoration] that sets up the divider configuration to correctly
 * separate content with headers.
 * @author Alexander Capehart (OxygenCobalt)
 */
class HeaderItemDecoration
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialDividerStyle,
    orientation: Int = LinearLayoutManager.VERTICAL
) : BackportMaterialDividerItemDecoration(context, attributeSet, defStyleAttr, orientation) {
    override fun shouldDrawDivider(position: Int, adapter: RecyclerView.Adapter<*>?) =
        try {
            // Add a divider if the next item is a header. This organizes the divider to separate
            // the ends of content rather than the beginning of content, alongside an added benefit
            // of preventing top headers from having a divider applied.
            (adapter as DiffAdapter<*, *, *>).getItem(position + 1) is Header
        } catch (e: ClassCastException) {
            false
        } catch (e: IndexOutOfBoundsException) {
            false
        }
}
