package org.oxycblt.auxio.music.stack.explore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.music.stack.Indexer
import org.oxycblt.auxio.music.stack.IndexerImpl

@Module
@InstallIn(SingletonComponent::class)
interface ExploreModule {
    @Binds fun explorer(impl: ExplorerImpl): Explorer
}
