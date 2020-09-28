package org.oxycblt.auxio.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel

// RecyclerView click listener
class ClickListener<T>(val onClick: (T) -> Unit)

// Base Diff callback
class DiffCallback<T : BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

// ViewHolder abstraction that automates some of the things that are common for all ViewHolders.
abstract class BaseViewHolder<T : BaseModel>(
    private val baseBinding: ViewDataBinding,
    protected val listener: ClickListener<T>
) : RecyclerView.ViewHolder(baseBinding.root) {
    init {
        baseBinding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    fun bind(model: T) {
        baseBinding.root.setOnClickListener { listener.onClick(model) }

        onBind(model)

        baseBinding.executePendingBindings()
    }

    abstract fun onBind(model: T)
}
