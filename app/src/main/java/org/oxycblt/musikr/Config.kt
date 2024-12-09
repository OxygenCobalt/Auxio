package org.oxycblt.musikr

import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.cache.TagCache
import org.oxycblt.musikr.tag.interpret.Separators

data class Storage(val tagCache: TagCache, val coverEditor: StoredCovers.Editor)

data class Interpretation(val nameFactory: Name.Known.Factory, val separators: Separators)
