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
 
package org.oxycblt.auxio.list.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import java.lang.reflect.Field
import org.oxycblt.auxio.util.lazyReflectedField
import org.oxycblt.auxio.util.requireIs

val ASD_MAX_GENERATION_FIELD: Field by
    lazyReflectedField(AsyncListDiffer::class, "mMaxScheduledGeneration")
val ASD_MUTABLE_LIST_FIELD: Field by lazyReflectedField(AsyncListDiffer::class, "mList")
val ASD_READ_ONLY_LIST_FIELD: Field by lazyReflectedField(AsyncListDiffer::class, "mReadOnlyList")

/**
 * Force-update an [AsyncListDiffer] with new data. It's hard to state how incredibly dangerous this
 * is, so only use it when absolutely necessary.
 * @param newList The new list to write to the [AsyncListDiffer].
 */
fun <T> AsyncListDiffer<T>.overwriteList(newList: List<T>) {
    // Should update the generation field to prevent any previous jobs from conflicting, then
    // updates the mutable list to it's nullable value, and then updates the read-only list to
    // it's non-nullable value.
    ASD_MAX_GENERATION_FIELD.set(this, requireIs<Int>(ASD_MAX_GENERATION_FIELD.get(this)) + 1)
    ASD_MUTABLE_LIST_FIELD.set(this, newList.ifEmpty { null })
    ASD_READ_ONLY_LIST_FIELD.set(this, newList)
}
