package org.oxycblt.musikr.fs.query

import android.content.ContentResolver
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.MusicLocationFactoryImpl

@Module
@InstallIn(SingletonComponent::class)
class QueryProvidesModule {
    @Provides
    fun contentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolverSafe
}

@Module
@InstallIn(SingletonComponent::class)
interface QueryBindsModule {
    @Binds
    fun deviceFiles(deviceFilesImpl: DeviceFilesImpl): DeviceFiles
}
