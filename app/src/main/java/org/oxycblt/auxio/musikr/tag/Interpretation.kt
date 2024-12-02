package org.oxycblt.auxio.musikr.tag

import org.oxycblt.auxio.musikr.tag.interpret.Separators

data class Interpretation(val nameFactory: Name.Known.Factory, val separators: Separators)