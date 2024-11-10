package org.oxycblt.auxio.music.model

import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.metadata.Separators

data class Interpretation(
    val nameFactory: Name.Known.Factory,
    val separators: Separators
)
