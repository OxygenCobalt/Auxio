package org.oxycblt.auxio.recycler.viewholders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

// ViewHolder abstraction that automates some of the things that are common for all ViewHolders.
abstract class BaseViewHolder<T : BaseModel>(
    private val baseBinding: ViewDataBinding,
    private val doOnClick: ((T) -> Unit)?
) : RecyclerView.ViewHolder(baseBinding.root) {
    init {
        baseBinding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    fun bind(model: T) {
        doOnClick?.let {
            baseBinding.root.setOnClickListener { it(model) }
        }

        onBind(model)

        baseBinding.executePendingBindings()
    }

    abstract fun onBind(model: T)
}
