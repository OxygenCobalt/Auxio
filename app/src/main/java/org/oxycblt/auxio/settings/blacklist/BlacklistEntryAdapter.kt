package org.oxycblt.auxio.settings.blacklist

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemBlacklistEntryBinding
import org.oxycblt.auxio.ui.inflater

/**
 * Adapter that shows the blacklist entries and their "Clear" button.
 * @author OxygenCobalt
 */
class BlacklistEntryAdapter(
    private val onClear: (String) -> Unit
) : RecyclerView.Adapter<BlacklistEntryAdapter.ViewHolder>() {
    private var paths = mutableListOf<String>()

    override fun getItemCount() = paths.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBlacklistEntryBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(paths[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newPaths: MutableList<String>) {
        paths = newPaths
        notifyDataSetChanged() // TODO: Consider using remote/addition
    }

    inner class ViewHolder(
        private val binding: ItemBlacklistEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(path: String) {
            binding.blacklistTitle.text = path
            binding.blacklistTitle.requestLayout()
            binding.blacklistClear.setOnClickListener {
                onClear(path)
            }
        }
    }
}
