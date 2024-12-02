package org.oxycblt.auxio.musikr.metadata

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.MediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.musikr.fs.DeviceFile
import javax.inject.Inject

interface MetadataExtractor {
    fun extract(files: Flow<DeviceFile>): Flow<AudioMetadata>
}

class MetadataExtractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaSourceFactory: MediaSource.Factory
) : MetadataExtractor {
    override fun extract(files: Flow<DeviceFile>) = files.mapNotNull {
        val exoPlayerMetadataFuture = MetadataRetriever.retrieveMetadata(
            mediaSourceFactory,
            MediaItem.fromUri(it.uri)
        )
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            withContext(Dispatchers.IO) {
                setDataSource(context, it.uri)
            }
        }
        val trackGroupArray = exoPlayerMetadataFuture.await()
        if (trackGroupArray.isEmpty) {
            return@mapNotNull null
        }
        val trackGroup = trackGroupArray.get(0)
        if (trackGroup.length == 0) {
            return@mapNotNull null
        }
        val format = trackGroup.getFormat(0)
        AudioMetadata(
            it,
            format,
            mediaMetadataRetriever
        )
    }
}