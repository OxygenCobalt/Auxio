package org.oxycblt.auxio.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

// ViewHolder abstraction that automates some of the things that are common for all ViewHolders.
abstract class BaseViewHolder<T : BaseModel>(
    private val baseBinding: ViewDataBinding,
    protected val listener: ClickListener<T>?
) : RecyclerView.ViewHolder(baseBinding.root) {
    init {
        baseBinding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    fun bind(model: T) {
        if (listener != null) {
            baseBinding.root.setOnClickListener {
                listener.onClick(model)
            }
        }

        onBind(model)

        baseBinding.executePendingBindings()
    }

    abstract fun onBind(model: T)
}
