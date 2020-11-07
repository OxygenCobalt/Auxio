package org.oxycblt.auxio.recycler.viewholders

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

// ViewHolder abstraction that automates some of the things that are common for all ViewHolders.
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

    protected abstract fun onBind(data: T)
}
