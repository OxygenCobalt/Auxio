package org.oxycblt.auxio.settings.accent

import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAccentBinding
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.inflater
import org.oxycblt.auxio.ui.toStateList

/**
 * An adapter that displays the list of all possible accents, and highlights the current one.
 * @author OxygenCobalt
 * @param onSelect What to do when an accent is selected.
 */

class AccentAdapter(
    private var curAccent: Accent,
    private val onSelect: (accent: Accent) -> Unit
) : RecyclerView.Adapter<AccentAdapter.ViewHolder>() {
    private var selectedViewHolder: ViewHolder? = null

    override fun getItemCount(): Int = ACCENTS.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAccentBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ACCENTS[position])
    }

    private fun setAccent(accent: Accent) {
        curAccent = accent
        onSelect(accent)
    }

    inner class ViewHolder(
        private val binding: ItemAccentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accent: Accent) {
            setSelected(accent == curAccent)

            binding.accent.apply {
                contentDescription = context.getString(accent.name)
                backgroundTintList = accent.getStateList(context)
                TooltipCompat.setTooltipText(this, contentDescription)
            }

            binding.accent.setOnClickListener {
                setAccent(accent)
                setSelected(true)
            }
        }

        private fun setSelected(isSelected: Boolean) {
            val context = binding.accent.context

            binding.accent.isEnabled = !isSelected

            binding.accent.imageTintList = if (isSelected) {
                // Switch out the currently selected viewholder with this one.
                selectedViewHolder?.setSelected(false)
                selectedViewHolder = this

                R.color.background.toStateList(context)
            } else {
                android.R.color.transparent.toStateList(context)
            }
        }
    }
}
