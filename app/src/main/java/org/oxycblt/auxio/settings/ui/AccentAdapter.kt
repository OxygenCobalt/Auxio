package org.oxycblt.auxio.settings.ui

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
 * @param doOnAccentConfirm What to do when an accent is confirmed.
 */
class AccentAdapter(
    private val doOnAccentConfirm: (accent: Accent) -> Unit
) : RecyclerView.Adapter<AccentAdapter.ViewHolder>() {
    override fun getItemCount(): Int = ACCENTS.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAccentBinding.inflate(parent.context.inflater))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ACCENTS[position])
    }

    inner class ViewHolder(
        private val binding: ItemAccentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accent: Accent) {
            binding.accent.apply {
                contentDescription = context.getString(accent.name)

                imageTintList = if (accent == Accent.get()) {
                    isEnabled = false

                    R.color.background.toStateList(context)
                } else {
                    isEnabled = true

                    android.R.color.transparent.toStateList(context)
                }

                backgroundTintList = accent.getStateList(context)

                TooltipCompat.setTooltipText(this, contentDescription)

                setOnClickListener {
                    doOnAccentConfirm(accent)
                }
            }
        }
    }
}
