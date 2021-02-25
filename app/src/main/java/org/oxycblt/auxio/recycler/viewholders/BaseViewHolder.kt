package org.oxycblt.auxio.recycler.viewholders

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

/**
 * A [RecyclerView.ViewHolder] that streamlines a lot of the common things across all viewholders.
 * @param T The datatype, inheriting [BaseModel] for this ViewHolder.
 * @param binding Basic [ViewDataBinding] required to set up click listeners & sizing.
 * @param doOnClick (Optional) Function that calls on a click.
 * @param doOnLongClick (Optional) Functions that calls on a long-click.
 * @author OxygenCobalt
 */
abstract class BaseViewHolder<T : BaseModel>(
    private val binding: ViewDataBinding,
    private val doOnClick: ((data: T) -> Unit)? = null,
    private val doOnLongClick: ((view: View, data: T) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {
    init {
        // Force the layout to *actually* be the screen width
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Bind the viewholder with whatever [BaseModel] instance that has been specified.
     * Will call [onBind] on the inheriting ViewHolder.
     * @param data Data that the viewholder should be bound with
     */
    fun bind(data: T) {
        doOnClick?.let { onClick ->
            binding.root.setOnClickListener {
                onClick(data)
            }
        }

        doOnLongClick?.let { onLongClick ->
            binding.root.setOnLongClickListener { view ->
                onLongClick(view, data)

                true
            }
        }

        onBind(data)

        binding.executePendingBindings()
    }

    /**
     * Function that performs binding operations unique to the inheriting viewholder.
     * Add any specialized code to an override of this instead of [BaseViewHolder] itself.
     */
    protected abstract fun onBind(data: T)
}
