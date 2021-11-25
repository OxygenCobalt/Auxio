package org.oxycblt.auxio.coil

import coil.key.Keyer
import coil.request.Options
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

/**
 * A basic keyer for music data.
 */
class MusicKeyer : Keyer<Music> {
    override fun key(data: Music, options: Options): String {
        return if (data is Song) {
            key(data.album, options)
        } else {
            "${data::class.simpleName}: ${data.id}"
        }
    }
}
