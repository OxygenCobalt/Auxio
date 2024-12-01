package org.oxycblt.auxio.musikr.metadata

import kotlinx.coroutines.flow.Flow
import org.oxycblt.auxio.musikr.fs.DeviceFile

interface MetadataExtractor {
    fun extract(files: Flow<DeviceFile>): Flow<AudioMetadata>
}