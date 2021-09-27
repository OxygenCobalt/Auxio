/*
 * Copyright (c) 2021 Auxio Project
 * HomeAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.home.recycler

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

/**
 * A base class that implements an [updateData] that is required across [SongsAdapter] and [ParentAdapter]
 */
abstract class HomeAdapter<T : BaseModel> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var data = listOf<BaseModel>()

    /**
     * Update the data with [newData]. [notifyDataSetChanged] will be called.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<BaseModel>) {
        data = newData

        // I would use ListAdapter instead of this inefficient invalidate call, but they still
        // haven't fixed the issue where ListAdapter's calculations will cause wild scrolling
        // for basically no reason.
        notifyDataSetChanged()
    }
}
