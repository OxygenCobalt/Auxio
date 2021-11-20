package org.oxycblt.auxio.coil

import coil.key.Keyer
import coil.request.Options
import org.oxycblt.auxio.music.Music

/**
 * A basic keyer for music data.
 */
class MusicKeyer : Keyer<Music> {
    override fun key(data: Music, options: Options): String? {
        return "${data::class.simpleName}: ${data.id}"
    }
}
