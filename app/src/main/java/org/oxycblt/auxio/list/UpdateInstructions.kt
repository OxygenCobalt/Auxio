package org.oxycblt.auxio.list

/**
 * Represents the specific way to update a list of items.
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class UpdateInstructions {
    /**
     * (A)synchronously diff the list. This should be used for small diffs with little item
     * movement.
     */
    DIFF,

    /**
     * Synchronously remove the current list and replace it with a new one. This should be used
     * for large diffs with that would cause erratic scroll behavior or in-efficiency.
     */
    REPLACE
}