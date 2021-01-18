package org.oxycblt.auxio

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import org.oxycblt.auxio.settings.SettingsManager

@Suppress("UNUSED")
class AuxioApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        // Init SettingsManager here so that there aren't any race conditions
        // [e.g PlaybackService gets SettingsManager before activity can init SettingsManager]
        SettingsManager.init(applicationContext)
    }

    override fun newImageLoader(): ImageLoader {
        // Don't cache images on-disk [The covers are already on-disk]
        // Crossfade by default
        // Use a transparent placeholder
        return ImageLoader.Builder(applicationContext)
            .diskCachePolicy(CachePolicy.DISABLED)
            .crossfade(true)
            .placeholder(android.R.color.transparent)
            .build()
    }
}
