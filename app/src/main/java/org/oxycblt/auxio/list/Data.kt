package org.oxycblt.auxio.list

import androidx.annotation.StringRes

/**
 * A marker for something that is a RecyclerView item. Has no functionality on it's own.
 */
interface Item

/**
 * A "header" used for delimiting groups of data.
 * @param titleRes The string resource used for the header's title.
 */
data class Header(@StringRes val titleRes: Int) : Item
