package org.oxycblt.auxio.settings.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAccentBinding
import org.oxycblt.auxio.ui.ACCENTS
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.toColor

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
        return ViewHolder(ItemAccentBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ACCENTS[position])
    }

    inner class ViewHolder(
        private val binding: ItemAccentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accent: Accent) {
            binding.accent.apply {
                contentDescription = accent.getDetailedSummary(context)

                setOnClickListener {
                    doOnAccentConfirm(accent)
                }

                imageTintList = if (accent == Accent.get()) {
                    isEnabled = false

                    ColorStateList.valueOf(R.color.background.toColor(context))
                } else {
                    isEnabled = true

                    ColorStateList.valueOf(android.R.color.transparent.toColor(context))
                }

                backgroundTintList = accent.getStateList(context)
            }
        }
    }
}
