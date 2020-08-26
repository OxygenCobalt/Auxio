package org.oxycblt.auxio.recycler

import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album

@BindingAdapter("songCount")
fun TextView.numSongsToText(album: Album) {
    text = if (album.numSongs < 2) {
        context.getString(R.string.label_single_song)
    } else {
        context.getString(R.string.format_multi_song_count, album.numSongs.toString())
    }
}

// Apply a custom vertical divider
fun RecyclerView.applyDivider() {
    val div = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )

    div.setDrawable(
        ColorDrawable(
            getDividerColor(this)
        )
    )

    addItemDecoration(div)
}

private fun getDividerColor(recycler: RecyclerView): Int {
    val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    // Depending on the theme use a different opacity for the divider
    val alpha = if (isDark) 45 else 85

    return ColorUtils.setAlphaComponent(
        ContextCompat.getColor(recycler.context, R.color.blue),
        alpha
    )
}
