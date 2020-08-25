package org.oxycblt.auxio.recycler

import android.widget.TextView
import androidx.databinding.BindingAdapter
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
