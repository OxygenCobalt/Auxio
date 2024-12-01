package org.oxycblt.auxio.musikr.playlist.m3u

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.musikr.playlist.ExternalPlaylistManager
import org.oxycblt.auxio.musikr.playlist.ExternalPlaylistManagerImpl
import org.oxycblt.auxio.musikr.playlist.m3u.M3U
import org.oxycblt.auxio.musikr.playlist.m3u.M3UImpl

@Module
@InstallIn(SingletonComponent::class)
interface PlaylistModule {
    @Binds fun m3u(m3u: M3UImpl): M3U
}
