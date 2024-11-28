package org.oxycblt.auxio.image.stack.cache

import android.content.Context
import androidx.media3.datasource.cache.Cache
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.music.stack.explore.cache.TagCache
import org.oxycblt.auxio.music.stack.explore.cache.TagCacheImpl
import org.oxycblt.auxio.music.stack.explore.cache.TagDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface StackModule {
    @Binds fun appFiles(impl: AppFilesImpl): AppFiles

    @Binds fun cache(impl: CoverCacheImpl): Cache

    @Binds fun perceptualHash(perceptualHash: PerceptualHashImpl): PerceptualHash

    @Binds fun coverCache(cache: CoverCacheImpl): CoverCache
}

@Module
@InstallIn(SingletonComponent::class)
class StoredCoversDatabaseModule {
    @Singleton
    @Provides
    fun database(@ApplicationContext context: Context) =
        Room.databaseBuilder(context.applicationContext, StoredCoversDatabase::class.java, "stored_covers.db")
            .fallbackToDestructiveMigration()
            .build()
}
