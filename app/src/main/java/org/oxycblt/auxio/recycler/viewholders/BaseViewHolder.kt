package org.oxycblt.auxio.recycler.viewholders

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

/**
 * A [RecyclerView.ViewHolder] that streamlines a lot of the common things across all viewholders.
 * @property baseBinding Basic [ViewDataBinding] required to set up click listeners & sizing.
 * @property doOnClick Function that specifies what to do when an item is clicked. Specify null if you want no action to occur.
 * @property doOnLongClick Function that specifies what to do when an item is long clicked. Specify null if you want no action to occur.
 * @author OxygenCobalt
 */
abstract class BaseViewHolder<T : BaseModel>(
    private val baseBinding: ViewDataBinding,
    private val doOnClick: ((data: T) -> Unit)?,
    private val doOnLongClick: ((data: T, view: View) -> Unit)?
) : RecyclerView.ViewHolder(baseBinding.root) {
    init {
        // Force the layout to *actually* be the screen width
        baseBinding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Bind the viewholder with whatever [BaseModel] instance that has been specified.
     * Will call [onBind] on the inheriting ViewHolder.
     * @param data Data that the viewholder should be binded with
     */
    fun bind(data: T) {
        doOnClick?.let { onClick ->
            baseBinding.root.setOnClickListener {
                onClick(data)
            }
        }

        doOnLongClick?.let { onLongClick ->
            baseBinding.root.setOnLongClickListener {
                onLongClick(data, baseBinding.root)

                true
            }
        }

        onBind(data)

        baseBinding.executePendingBindings()
    }

    /**
     * Function that performs binding operations unique to the inheriting viewholder.
     * Add any specialized code to an override of this instead of [BaseViewHolder] itself.
     */
    protected abstract fun onBind(data: T)
}
